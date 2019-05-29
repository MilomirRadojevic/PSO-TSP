import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    private static final int NB_PARTICLES = 10;
    private static final int NB_OPERANDS = 3;
    private static final int MIN_RANGE = 149;
    private static final int MAX_RANGE = 190;
    private static final int TARGET_VALUE = 100;
    private static final int MAX_ITERATIONS = 200;
    private static final int MAX_VELOCITY = 10;
    private static final int POSITIVE_INFINITY = 100000;
    private static int globalBestValue = POSITIVE_INFINITY;
    private static int iteration = 0;
    private static List<List<Double>> position = Arrays.asList((List<Double>[]) Array.newInstance(List.class, NB_PARTICLES));
    private static List<Integer> currentValue = Arrays.asList(new Integer[NB_PARTICLES]);
    private static List<Integer> bestValue = Arrays.asList(new Integer[NB_PARTICLES]);
    private static List<Double> velocity = Arrays.asList(new Double[NB_PARTICLES]);

    private static int randomInt(int min, int max) {
        Random rand = new Random();

        return min + rand.nextInt(max - min + 1);
    }

    private static double randomDouble() {
        Random rand = new Random();

        return rand.nextDouble();
    }

    private static int solutionValue(int i) {
        int sum = 0;
        for (int j = 0; j < NB_OPERANDS; j++) {
            sum += Math.round(position.get(i).get(j));
        }

        return sum;
    }

    private static void initialization() {
        for (int i = 0; i < NB_PARTICLES; i++) {
            position.set(i, Arrays.asList(new Double[NB_OPERANDS]));

            for (int j = 0; j < NB_OPERANDS; j++) {
                position.get(i).set(j, (double) randomInt(MIN_RANGE, MAX_RANGE));
            }

            currentValue.set(i, solutionValue(i));
            bestValue.set(i, currentValue.get(i));

            if (Math.abs(currentValue.get(i) - TARGET_VALUE) < Math.abs(globalBestValue - TARGET_VALUE)) {
                globalBestValue = currentValue.get(i);
            }

            velocity.set(i, 0.0);
        }
    }

    private static void updateVelocity() {
        for (int i = 0; i < NB_PARTICLES; i++) {
            velocity.set(i, velocity.get(i) + 2.0 * randomDouble() * (bestValue.get(i) - currentValue.get(i)) + 2.0 * randomDouble() * (globalBestValue - currentValue.get(i)));
            if (velocity.get(i) > MAX_VELOCITY) {
                velocity.set(i, (double) MAX_VELOCITY);
            }
            if (velocity.get(i) < -MAX_VELOCITY) {
                velocity.set(i, (double) -MAX_VELOCITY);
            }
        }
    }

    private static void updatePosition() {
        for (int i = 0; i < NB_PARTICLES; i++) {
            for (int j = 0; j < NB_OPERANDS; j++) {
                position.get(i).set(j, position.get(i).get(j) + velocity.get(i));
            }

            currentValue.set(i, solutionValue(i));

            if (Math.abs(currentValue.get(i) - TARGET_VALUE) < Math.abs(bestValue.get(i) - TARGET_VALUE)) {
                bestValue.set(i, currentValue.get(i));
            }

            if (Math.abs(currentValue.get(i) - TARGET_VALUE) < Math.abs(globalBestValue - TARGET_VALUE)) {
                globalBestValue = currentValue.get(i);
            }
        }
    }

    private static void print() {
        for (int i = 0; i < NB_PARTICLES; i++) {
            for (int j = 0; j < NB_OPERANDS; j++) {
                System.out.print(Math.round(position.get(i).get(j)) + (j < NB_OPERANDS - 1 ? " + " : " = "));
            }

            System.out.println(currentValue.get(i));
        }

        System.out.println();
    }

    private static void PSO() {
        initialization();

        while (iteration++ < MAX_ITERATIONS) {
            print();

            if (globalBestValue == TARGET_VALUE) {
                System.out.println("Solution found after " + iteration + " iterations");
                return;
            }

            updateVelocity();
            updatePosition();
        }

        System.out.println("Solution not found");
    }

    public static void main(String[] args) {
        PSO();
    }
}
