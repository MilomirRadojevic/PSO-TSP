import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Particle {
    private List<Point> currentSolution;
    private List<Point> bestSolution;
    private double currentValue;
    private double bestValue;
    private VelocityType velocityType;

    Particle(List<Point> points) {
        setCurrentSolution(points);
        setBestSolutionToCurrentSolution();
    }

    Particle(Particle other) {
        currentSolution = new ArrayList<>(other.getCurrentSolution());
        bestSolution = new ArrayList<>(other.getBestSolution());
        currentValue = other.getCurrentValue();
        bestValue = other.getBestValue();
    }

    public List<Point> getCurrentSolution() {
        return currentSolution;
    }

    public void setCurrentSolution(List<Point> points) {
        currentSolution = new ArrayList<>(points);
        currentValue = calculateSolutionValue(currentSolution);
    }

    public void shuffleCurrentSolution() {
        Collections.shuffle(currentSolution);
        currentValue = calculateSolutionValue(currentSolution);
    }

    public void inversionNeighborhood() {
        List<List<Point>> neighbors = new ArrayList<>();
        List<Point> bestNeighbor = null;

        for (int i = 1; i < currentSolution.size(); i++) {
            for (int j = 0; j < currentSolution.size() - i; j++) {
                List<Point> neighbor = new ArrayList<>(currentSolution);

                Collections.reverse(neighbor.subList(j, i + j + 1));

                neighbors.add(neighbor);
            }
        }

        for (List<Point> neighbor : neighbors) {
            if (bestNeighbor == null || calculateSolutionValue(neighbor) < calculateSolutionValue(bestNeighbor)) {
                bestNeighbor = neighbor;
            }
        }

        setCurrentSolution(bestNeighbor);
    }

    public void pathRelinking(List<Point> otherPoints) {
        Collections.rotate(currentSolution, -currentSolution.indexOf(otherPoints.get(0)));

        for (int i = 1; i < otherPoints.size(); i++) {
            Point targetElement = otherPoints.get(i);
            int index = currentSolution.indexOf(targetElement);
            while (index != i) {
                Collections.swap(currentSolution, index, index - 1);
                index--;
            }
        }
    }

    public List<Point> getBestSolution() {
        return bestSolution;
    }

    public void setBestSolutionToCurrentSolution() {
        bestSolution = new ArrayList<>(currentSolution);
        bestValue = calculateSolutionValue(bestSolution);
    }

    private static double calculateSolutionValue(List<Point> solution) {
        double value = solution.get(0).distance(solution.get(solution.size() - 1));
        for (int i = 0; i < solution.size() - 1; i++) {
            value += solution.get(i).distance(solution.get(i + 1));
        }

        return value;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public double getBestValue() {
        return bestValue;
    }

    public VelocityType getVelocityType() {
        return velocityType;
    }

    public void setVelocityType(VelocityType velocityType) {
        this.velocityType = velocityType;
    }
}
