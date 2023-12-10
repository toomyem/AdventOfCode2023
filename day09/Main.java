import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {

            long sum = 0;
            long sum2 = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                long[] data = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray();
                sum += calcNext(data);
                sum2 += calcPrev(data);
            }

            System.out.println("Part 1: " + sum);
            System.out.println("Part 2: " + sum2);
        }
    }

    private static List<long[]> prepareRows(long[] data) {
        List<long[]> rows = new ArrayList<>();

        boolean nonZero;
        do {
            rows.add(data);
            long[] prev = rows.get(rows.size() - 1);
            data = new long[prev.length - 1];
            nonZero = false;
            for (int i = 0; i < data.length; i++) {
                data[i] = prev[i + 1] - prev[i];
                if (data[i] != 0) nonZero = true;
            }
        } while (nonZero);

        return rows;
    }

    private static long calcNext(long[] data) {
        List<long[]> rows = prepareRows(data);

        long v = 0;
        for (int i = rows.size() - 1; i >= 0; i--) {
            long[] row = rows.get(i);
            v = v + row[row.length - 1];
        }
        return v;
    }

    private static long calcPrev(long[] data) {
        List<long[]> rows = prepareRows(data);

        long v = 0;
        for (int i = rows.size() - 1; i >= 0; i--) {
            long[] row = rows.get(i);
            v = row[0] - v;
        }
        return v;
    }
}
