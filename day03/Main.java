import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

record Part(int value, int row, int col) {}
record Gear(int row, int col) {}

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        int row = 0;
        int sum = 0;
        int num = 0;
        int b = -1;
        List<Part> parts = new ArrayList<>();
        List<String> rows = new ArrayList<>();
        Map<Gear, List<Part>> gears = new HashMap<>();

        while (in.hasNext()) {
            String line = in.nextLine();
            rows.add(line);
            for (int i = 0; i <= line.length(); i++) {
                char ch = i < line.length() ? line.charAt(i) : '.';
                if (ch >= '0' && ch <= '9') {
                    if (num == 0) {
                        num = ch - '0';
                        b = i;
                    } else {
                        num = 10 * num + ch - '0';
                    }
                } else if (num > 0) {
                    parts.add(new Part(num, row, b));
                    num = 0;
                }
            }
            row += 1;
        }

        for (Part part : parts) {
            int c1 = part.col() - 1;
            int c2 = part.col() + String.valueOf(part.value()).length();
            int r1 = part.row() - 1;
            int r2 = part.row() + 1;

            Gear gear = null;
            boolean isPart = false;

            for (int r = r1; r <= r2; r++) {
                for (int c = c1; c <= c2; c++) {
                    if (r >= 0 && r < rows.size() && c >= 0 && c < rows.size()) {
                        char ch = rows.get(r).charAt(c);
                        if (ch != '.' && (ch < '0' || ch > '9')) {
                            isPart = true;
                        }
                        if (ch == '*') {
                            gear = new Gear(r, c);
                        }
                    }
                }
            }
            if (isPart) {
                sum += part.value();
            }
            if (gear != null) {
                gears.computeIfAbsent(gear, k -> new ArrayList<>()).add(part);
            }
        }

        System.out.println("Part 1: " + sum);
        int sum2 = gears.values().stream()
                .filter(v -> v.size() == 2)
                        .mapToInt(p -> p.get(0).value() * p.get(1).value())
                                .sum();
        System.out.println("Part 2: " + sum2);
    }
}
