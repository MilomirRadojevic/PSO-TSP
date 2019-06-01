import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Particle {
    private List<Point> currentSolution;
    private List<Point> bestSolution;
    private double currentValue;
    private double bestValue;

    Particle(List<Point> points) {
        setCurrentSolution(points);
        setBestSolutionToCurrentSolution();
    }

    Particle(Particle particle) {
        currentSolution = new ArrayList<>(particle.getCurrentSolution());
        bestSolution = new ArrayList<>(particle.getBestSolution());
        currentValue = particle.getCurrentValue();
        bestValue = particle.getBestValue();
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

    public List<Point> getBestSolution() {
        return bestSolution;
    }

    public void setBestSolutionToCurrentSolution() {
        bestSolution = new ArrayList<>(currentSolution);
        bestValue = calculateSolutionValue(bestSolution);
    }

    private static double calculateSolutionValue(List<Point> solution) {
        double value = 0;
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
}
