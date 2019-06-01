import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    private static final int NUMBER_OF_PARTICLES = 16;
    private static List<Particle> particles = new ArrayList<>();

    private static Particle globalBestSolution;
    private static double previousGlobalBestValue = Double.MAX_VALUE;

    private static int iteration = 1;
    private static final int MAX_ITERATIONS = 2000;

    private static double inertiaFactorProbability = 0.9;
    private static double cognitiveFactorProbability = 0.05;
    private static double socialFactorProbability = 0.05;

    private static final double INERTIA_DELTA_COEF = 4.0;
    private static final double COGNITIVE_DELTA_COEF = 2.0;

    private static void initialization() {
        List<Point> inputPoints  = new ArrayList<>();
        //10628
        inputPoints.add(new Point(6734, 1453));
        inputPoints.add(new Point(2233, 10));
        inputPoints.add(new Point(5530, 1424));
        inputPoints.add(new Point(401, 841));
        inputPoints.add(new Point(3082, 1644));
        inputPoints.add(new Point(7608, 4458));
        inputPoints.add(new Point(7573, 3716));
        inputPoints.add(new Point(7265, 1268));
        inputPoints.add(new Point(6898, 1885));
        inputPoints.add(new Point(1112, 2049));
        inputPoints.add(new Point(5468, 2606));
        inputPoints.add(new Point(5989, 2873));
        inputPoints.add(new Point(4706, 2674));
        inputPoints.add(new Point(4612, 2035));
        inputPoints.add(new Point(6347, 2683));
        inputPoints.add(new Point(6107, 669));
        inputPoints.add(new Point(7611, 5184));
        inputPoints.add(new Point(7462, 3590));
        inputPoints.add(new Point(7732, 4723));
        inputPoints.add(new Point(5900, 3561));
        inputPoints.add(new Point(4483, 3369));
        inputPoints.add(new Point(6101, 1110));
        inputPoints.add(new Point(5199, 2182));
        inputPoints.add(new Point(1633, 2809));
        inputPoints.add(new Point(4307, 2322));
        inputPoints.add(new Point(675, 1006));
        inputPoints.add(new Point(7555, 4819));
        inputPoints.add(new Point(7541, 3981));
        inputPoints.add(new Point(3177, 756 ));
        inputPoints.add(new Point(7352, 4506));
        inputPoints.add(new Point(7545, 2801));
        inputPoints.add(new Point(3245, 3305));
        inputPoints.add(new Point(6426, 3173));
        inputPoints.add(new Point(4608, 1198));
        inputPoints.add(new Point(23, 2216));
        inputPoints.add(new Point(7248, 3779));
        inputPoints.add(new Point(7762, 4595));
        inputPoints.add(new Point(7392, 2244));
        inputPoints.add(new Point(3484, 2829));
        inputPoints.add(new Point(6271, 2135));
        inputPoints.add(new Point(4985, 140));
        inputPoints.add(new Point(1916, 1569));
        inputPoints.add(new Point(7280, 4899));
        inputPoints.add(new Point(7509, 3239));
        inputPoints.add(new Point(10, 2676));
        inputPoints.add(new Point(6807, 2993));
        inputPoints.add(new Point(5185, 3258));
        inputPoints.add(new Point(3023, 1942));

        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            Collections.shuffle(inputPoints);
            Particle particle = new Particle(inputPoints);

            particles.add(particle);

            if (globalBestSolution == null || particle.getBestValue() < globalBestSolution.getBestValue()) {
                globalBestSolution = new Particle(particle);
            }
        }
    }

    private static void defineVelocity(Particle particle) {
        double randomNumber = ThreadLocalRandom.current().nextDouble();

        if (randomNumber < inertiaFactorProbability) {
            particle.setVelocityType(VelocityType.V_INERTIA);
        } else if (randomNumber < inertiaFactorProbability + cognitiveFactorProbability) {
            particle.setVelocityType(VelocityType.V_COGNITIVE);
        } else { // randomNumber < inertiaFactorProbability + cognitiveFactorProbability + socialFactorProbability ~ 1
            particle.setVelocityType(VelocityType.V_SOCIAL);
        }
    }

    private static void updatePosition(Particle particle) {
        switch (particle.getVelocityType()) {
            case V_INERTIA:
                particle.inversionNeighborhood();
                break;
            case V_COGNITIVE:
                particle.pathRelinking(particle.getBestSolution());
                break;
            case V_SOCIAL:
                particle.pathRelinking(globalBestSolution.getBestSolution());
                break;
        }
    }

    private static void updateProbabilities() {
        //inertia starts as high while others are low
        //by each iteration, inertia gets lower while others get higher
        //in the end, unless there was early exit, social factor will be high, cognitive low, and inertia almost non-existent
        inertiaFactorProbability *= 1 - INERTIA_DELTA_COEF / MAX_ITERATIONS;
        cognitiveFactorProbability *= 1 + COGNITIVE_DELTA_COEF / MAX_ITERATIONS;
        socialFactorProbability = 1 - (inertiaFactorProbability + cognitiveFactorProbability);
    }

    private static void print() {
//        System.out.println("Iteration: " + iteration + ". Iterations since last improvement: " + iterationsSinceLastImprovement + ".");
//
//        for (Particle particle : particles) {
//            for (Point j : particle.getCurrentSolution()) {
//                System.out.print(j.x + "," + j.y + " ");
//            }
//
//            System.out.println(" Current: " + particle.getCurrentValue() + "  Best: " + particle.getBestValue());
//        }

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
                previousGlobalBestValue = globalBestSolution.getBestValue();
            }

            for (Particle particle : particles) {
                defineVelocity(particle);
                updatePosition(particle);
            }

            updateProbabilities();
        } while (iteration++ < MAX_ITERATIONS);

        print();
    }

    public static void main(String[] args) {
        PSO();
    }
}