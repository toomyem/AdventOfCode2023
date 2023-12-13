import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }


    void run() throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            List<List<String>> boards = new ArrayList<>();
            List<String> rows = new ArrayList<>();
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) {
                    boards.add(rows);
                    rows = new ArrayList<>();
                } else {
                    rows.add(line);
                }
            }
            boards.add(rows);

            int sum1 = 0;
            int sum2 = 0;
            for (List<String> board : boards) {
                sum1 += solve1(board);
                sum2 += solve2(board);
            }
            System.out.println("Part 1: " + sum1);
            System.out.println("Part 2: " + sum2);

        }
    }

    int solve1(List<String> rows) {
        int a = 100 * compute(rows, 0);
        if (a == 0) a = compute(flip(rows), 0);
        return a;
    }

    int solve2(List<String> rows) {
        for (int r = 0; r < rows.size(); r++) {
            for (int c = 0; c < rows.get(r).length(); c++) {
                List<String> fixed = fix(rows, r, c);
                int prev = compute(rows, 0);
                int a = compute(fixed, prev);
                if (a > 0) return 100 * a;
                prev = compute(flip(rows), 0);
                a = compute(flip(fixed), prev);
                if (a > 0) return a;
            }
        }
        return 0;
    }

    List<String> fix(List<String> rows, int row, int col) {
        List<String> result = new ArrayList<>();
        for (int r = 0; r < rows.size(); r++) {
            String line = rows.get(r);
            if (r == row) {
                char ch = line.charAt(col);
                if (ch == '.') {
                    ch = '#';
                } else {
                    ch = '.';
                }
                line = line.substring(0, col) + ch + line.substring(col + 1);
            }
            result.add(line);
        }
        return result;
    }

    List<String> flip(List<String> rows) {
        List<String> result = new ArrayList<>();
        for (int c = 0; c < rows.get(0).length(); c++) {
            StringBuilder b = new StringBuilder();
            for (String row : rows) {
                b.append(row.charAt(c));
            }
            result.add(b.toString());
        }
        return result;
    }

    int compute(List<String> rows, int prev) {
        LOOP:
        for (int i = 1; i < rows.size(); i++) {
            int k = 0;
            while (i - k - 1 >= 0 && i + k < rows.size()) {
                String r1 = rows.get(i - k - 1);
                String r2 = rows.get(i + k);
                if (!r1.equals(r2)) continue LOOP;
                k++;
            }
            if (i != prev) return i;
        }
        return 0;
    }
}
