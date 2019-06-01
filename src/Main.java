import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final int NUMBER_OF_PARTICLES = 16;
    private static List<Particle> particles = new ArrayList<>();

    private static Particle globalBestSolution;
    private static double previousGlobalBestValue = Double.MAX_VALUE;

    private static int iteration = 1;
    private static final int MAX_ITERATIONS = 200;
    private static int iterationsSinceLastImprovement = 0;
    private static final int MAX_ITERATIONS_WITHOUT_IMPROVEMENT = 20;

    private static double inertiaFactorProbability = 0.9;
    private static double cognitiveFactorProbability = 0.05;
    private static double socialFactorProbability = 0.05;

    private static final double INERTIA_DELTA_COEF = 4.0;
    private static final double COGNITIVE_DELTA_COEF = 2.0;

    private static void initialization() {
        List<Point> inputPoints  = new ArrayList<>();
        inputPoints.add(new Point(0, 0));
        inputPoints.add(new Point(1, 0));
        inputPoints.add(new Point(2, 0));
        inputPoints.add(new Point(3, 0));
        inputPoints.add(new Point(3, 1));
        inputPoints.add(new Point(3, 2));
        inputPoints.add(new Point(3, 3));

        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            Collections.shuffle(inputPoints);
            Particle particle = new Particle(inputPoints);

            particles.add(particle);

            if (globalBestSolution == null || particle.getBestValue() < globalBestSolution.getBestValue()) {
                globalBestSolution = new Particle(particle);
            }
        }
    }

    private static void defineVelocity() {
        //.........................
    }

    private static void updatePosition(Particle particle) {
        particle.shuffleCurrentSolution();
    }

    private static void updateProbabilities() {
        //inertia starts as high while others are low
        //by each iteration, inertia gets lower while others get higher
        //in the end, unless there was early exit, social factor will be high, cognitive low, and inertia almost non-existant
        inertiaFactorProbability *= 1 - INERTIA_DELTA_COEF / MAX_ITERATIONS;
        cognitiveFactorProbability *= 1 + COGNITIVE_DELTA_COEF / MAX_ITERATIONS;
        socialFactorProbability = 1 - (inertiaFactorProbability + cognitiveFactorProbability);
    }

    private static void print() {
        System.out.println("Iteration: " + iteration + ". Iterations since last improvement: " + iterationsSinceLastImprovement + ".");

        for (Particle particle : particles) {
            for (Point j : particle.getCurrentSolution()) {
                System.out.print(j.x + "," + j.y + " ");
            }

            System.out.println(" Current: " + particle.getCurrentValue() + "  Best: " + particle.getBestValue());
        }

        System.out.println("Global best value: " + globalBestSolution.getBestValue());
        System.out.println();
    }

    private static void PSO() {
        initialization();

        do {
            for (Particle particle : particles) {
                double currentValue = particle.getCurrentValue();

                if (currentValue < particle.getBestValue()) {
                    particle.setBestSolutionToCurrentSolution();
                }

                if (currentValue < globalBestSolution.getBestValue()) {
                    globalBestSolution = new Particle(particle);
                }
            }

            if (globalBestSolution.getBestValue() < previousGlobalBestValue) {
                iterationsSinceLastImprovement = 0;
                previousGlobalBestValue = globalBestSolution.getBestValue();
            }

            print();

            for (Particle particle : particles) {
                defineVelocity();
                updatePosition(particle);
            }

            updateProbabilities();
        } while (iteration++ < MAX_ITERATIONS && ++iterationsSinceLastImprovement < MAX_ITERATIONS_WITHOUT_IMPROVEMENT);
    }

    public static void main(String[] args) {
        PSO();
    }
}