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
        //find local minimum using inversion neighborhood strategy
        //TODO: remove following temp code
        shuffleCurrentSolution();
    }

    public void pathRelinking(Particle other) {
        //find combination of two solutions using path relinking strategy
        //TODO: remove following temp code
        shuffleCurrentSolution();
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

    public VelocityType getVelocityType() {
        return velocityType;
    }

    public void setVelocityType(VelocityType velocityType) {
        this.velocityType = velocityType;
    }
}
