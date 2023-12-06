import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        int[] times = new int[]{};
        int[] distances = new int[]{};
        long time = 0;
        long distance = 0;

        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.startsWith("Time:")) {
                times = Arrays.stream(line.substring(5).trim().split(" +")).mapToInt(Integer::parseInt).toArray();
                time = Long.parseLong(line.substring(5).replaceAll(" +", ""));
            }
            if (line.startsWith("Distance:")) {
                distances = Arrays.stream(line.substring(9).trim().split(" +")).mapToInt(Integer::parseInt).toArray();
                distance = Long.parseLong(line.substring(9).replaceAll(" +", ""));
            }
        }

        long result = 1;
        for (int i = 0; i < times.length; i++) {
            result *= calc(times[i], distances[i]);
        }
        System.out.println("Part 1: " + result);

        result = calc(time, distance);
        System.out.println("Part 2: " + result);
    }

    private static long calc(long n, long d) {
        long m = 0;

        for (long t = 1; t < n; t++) {
            long v = (n - t) * t;
            if (v > d) m += 1;
        }
        return m;
    }
}
