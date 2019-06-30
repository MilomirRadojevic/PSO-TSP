import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main {
    private static final int NUMBER_OF_PARTICLES = 1000;
    private static List<Particle> particles = new ArrayList<>();

    private static List<Thread> threads = new ArrayList<>();

    private static Particle globalBestParticle;

    private static int iteration = 1;
    private static int iterations_without_progress = 0;
    private static final int MAX_ITERATIONS = 100;
    private static final int MAX_ITERATIONS_WITHOUT_PROGRESS = 100;

    private static double inertiaFactorProbability = 0.8;
    private static double cognitiveFactorProbability = 0.05;
    private static double socialFactorProbability = 0.05;
    private static double chaosFactorProbability = 0.1;

    private static final double INERTIA_DELTA_COEF = 3.0;
    private static final double COGNITIVE_DELTA_COEF = 1.75;

    private static class ParticleThread implements Runnable {
        private List<Particle> particles;

        private ParticleThread(List<Particle> particles) {
            this.particles = particles;
        }

        public void run() {
            for (Particle particle : particles) {
                defineVelocity(particle);
                updatePosition(particle);
            }
        }
    }

    private static void initialization(List<Point> inputPoints) {

        for (int i = 0; i < NUMBER_OF_PARTICLES; i++) {
            Collections.shuffle(inputPoints, ThreadLocalRandom.current());
            Particle particle = new Particle(inputPoints);

            particles.add(particle);

            if (globalBestParticle == null || particle.getBestValue() < globalBestParticle.getBestValue()) {
                globalBestParticle = new Particle(particle);
            }
        }
    }

    private static void defineVelocity(Particle particle) {
        double randomNumber = ThreadLocalRandom.current().nextDouble();

        if (particle.getMovementsAppliedToThisParticle().containsAll(EnumSet.of(VelocityType.V_INERTIA, VelocityType.V_SOCIAL)) ||
            particle.getMovementsAppliedToThisParticle().containsAll(EnumSet.of(VelocityType.V_COGNITIVE, VelocityType.V_SOCIAL)) ||
            particle.getMovementsAppliedToThisParticle().containsAll(EnumSet.of(VelocityType.V_INERTIA, VelocityType.V_COGNITIVE))) {
            //avoid multiple movements of the same type per one instance of particle and force reshuffling
            particle.setVelocityType(VelocityType.V_CHAOS);
            particle.setMovementsAppliedToThisParticle(EnumSet.of(VelocityType.V_CHAOS));

            return;
        }

        if (randomNumber < inertiaFactorProbability) {
            if (particle.getVelocityType() == VelocityType.V_INERTIA) {
                defineVelocity(particle);
            } else {
                particle.setVelocityType(VelocityType.V_INERTIA);
                particle.getMovementsAppliedToThisParticle().add(VelocityType.V_INERTIA);
            }
        } else if (randomNumber < inertiaFactorProbability + cognitiveFactorProbability) {
            if (particle.getVelocityType() == VelocityType.V_COGNITIVE) {
                defineVelocity(particle);
            } else {
                particle.setVelocityType(VelocityType.V_COGNITIVE);
                particle.getMovementsAppliedToThisParticle().add(VelocityType.V_COGNITIVE);
            }
        } else if (randomNumber < inertiaFactorProbability + cognitiveFactorProbability + chaosFactorProbability) {
            if (particle.getVelocityType() == VelocityType.V_CHAOS) {
                defineVelocity(particle);
            } else {
                particle.setVelocityType(VelocityType.V_CHAOS);
                particle.getMovementsAppliedToThisParticle().add(VelocityType.V_CHAOS);
            }
        } else { // randomNumber < inertiaFactorProbability + cognitiveFactorProbability + socialFactorProbability + chaosFactorProbability ~ 1
            if (particle.getVelocityType() == VelocityType.V_SOCIAL) {
                defineVelocity(particle);
            } else {
                particle.setVelocityType(VelocityType.V_SOCIAL);
                particle.getMovementsAppliedToThisParticle().add(VelocityType.V_SOCIAL);
            }
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
                particle.pathRelinking(globalBestParticle.getBestSolution());
                break;
            case V_CHAOS:
                particle.shuffleCurrentSolution();
                break;
        }
    }

    private static void updateProbabilities() {
        //inertia starts as high while others are low
        //by each iteration, inertia gets lower while others get higher
        //in the end, unless there was early exit, social factor will be high, cognitive low, and inertia almost non-existent
        inertiaFactorProbability *= 1 - INERTIA_DELTA_COEF / MAX_ITERATIONS;
        cognitiveFactorProbability *= 1 + COGNITIVE_DELTA_COEF / MAX_ITERATIONS;
        socialFactorProbability = 1 - (inertiaFactorProbability + cognitiveFactorProbability + chaosFactorProbability);
    }

    private static void print() {
        System.out.println("Iteration: " + (iteration - 1) + ".");

        System.out.println("Global best value: " + globalBestParticle.getBestValue());
        System.out.println();

        System.out.print("x = np.fromstring('");
        for (Point j : globalBestParticle.getCurrentSolution()) {
            System.out.print(j.x + " ");
        }
        System.out.print(globalBestParticle.getCurrentSolution().get(0).x + "', dtype=int, sep=' ')");

        System.out.println();

        System.out.print("y = np.fromstring('");
        for (Point j : globalBestParticle.getCurrentSolution()) {
            System.out.print(j.y + " ");
        }
        System.out.print(globalBestParticle.getCurrentSolution().get(0).y + "', dtype=int, sep=' ')");
    }

    private static void PSO() throws InterruptedException {
        do {
            iterations_without_progress++;

            for (Particle particle : particles) {
                double currentValue = particle.getCurrentValue();

                if (currentValue < particle.getBestValue()) {
                    particle.setBestSolutionToCurrentSolution();
                }

                if (currentValue < globalBestParticle.getBestValue()) {
                    globalBestParticle = new Particle(particle);
                    iterations_without_progress = 0;
                }
            }

            int threadStep = 20;
            for (int i = 0; i < particles.size(); i += threadStep) {
                Thread thread = new Thread(new ParticleThread(particles.subList(i, Math.min(i + threadStep, particles.size()))));
                threads.add(thread);

                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            threads.clear();

            updateProbabilities();
        } while (iteration++ < MAX_ITERATIONS && iterations_without_progress <= MAX_ITERATIONS_WITHOUT_PROGRESS);

        print();
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Error occurred while reading command line arguments");

            return;
        }

        String fileName = args[0];

        List<Point> inputPoints;

        try {
            List<String> lines = Files.readAllLines(Paths.get("resources/" + fileName + ".txt"), StandardCharsets.UTF_8);

            inputPoints = lines.stream().map(line -> {
                String[] input = line.split(" ");

                return new Point(Integer.valueOf(input[0]), Integer.valueOf(input[1]));
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error occurred while reading file");

            return;
        }

        long startTime = System.currentTimeMillis();

        initialization(inputPoints);
        PSO();

        System.out.println();
        System.out.println();
        System.out.println(System.currentTimeMillis() - startTime + " ms");
    }
}