import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final int NUMBER_OF_PARTICLES = 16;
    private static List<Particle> particles = Arrays.asList(new Particle[NUMBER_OF_PARTICLES]);

    private static double globalBestValue = Double.MAX_VALUE;
    private static double previousGlobalBestValue = Double.MAX_VALUE;
    private static int iteration = 1;
    private static final int MAX_ITERATIONS = 200;
    private static int iterationsSinceLastImprovement = 0;
    private static final int MAX_ITERATIONS_WITHOUT_IMPROVEMENT = 20;

    private static double inertiaFactor = 0.9;
    private static double cognitiveFactor = 0.05;
    private static double socialFactor = 0.05;

    private static double solutionValue(Particle particle) {
        double value = 0;
        for (int i = 0; i < particle.getPoints().size() - 1; i++) {
            value += particle.getPoints().get(i).distance(particle.getPoints().get(i + 1));
        }

        return value;
    }

    private static void initialization() {
        List<Point> inputPoints  = new ArrayList<>();
        inputPoints.add(new Point(0, 0));
        inputPoints.add(new Point(1, 0));
        inputPoints.add(new Point(2, 0));
        inputPoints.add(new Point(3, 0));
        inputPoints.add(new Point(3, 2));

        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            Collections.shuffle(inputPoints);
            particles.set(i, new Particle(new ArrayList<>(inputPoints)));
            particles.get(i).setBestValue(solutionValue(particles.get(i)));
        }
    }

    private static void updateVelocity() {
        //
    }

    private static void updatePosition() {
        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            Collections.shuffle(particles.get(i).getPoints());
        }
    }

    private static void print() {
        System.out.println("Iteration: " + iteration + ". Iterations since last improvement: " + iterationsSinceLastImprovement + ".");

        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            for (Point j : particles.get(i).getPoints()) {
                System.out.print(j.x + "," + j.y + " ");
            }

            System.out.println(" Current: " + solutionValue(particles.get(i)) + "  Best: " + particles.get(i).getBestValue());
        }

        System.out.println("Global best value: " + globalBestValue);
        System.out.println();
    }

    private static void PSO() {
        initialization();

        do {
            for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
                double currentValue = solutionValue(particles.get(i));

                if (currentValue < particles.get(i).getBestValue()) {
                    particles.get(i).setBestValue(currentValue);
                }

                if (currentValue < globalBestValue) {
                    globalBestValue = currentValue;
                }
            }

            if (globalBestValue < previousGlobalBestValue) {
                iterationsSinceLastImprovement = 0;
            }
            previousGlobalBestValue = globalBestValue;

            print();

            updateVelocity();
            updatePosition();

            //izmeniti tako da budu proporcinalni broju iteracija
            inertiaFactor *= 0.95;
            cognitiveFactor *= 1.01;
            socialFactor = 1 - (inertiaFactor + cognitiveFactor);

            System.out.println(inertiaFactor + " _ " + cognitiveFactor + " _ " + socialFactor);
        } while (iteration++ < MAX_ITERATIONS && ++iterationsSinceLastImprovement < MAX_ITERATIONS_WITHOUT_IMPROVEMENT);
    }

    public static void main(String[] args) {
        PSO();
    }
}