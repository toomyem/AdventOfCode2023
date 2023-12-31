import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    void run() throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {

            long sum1 = 0;
            long sum2 = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                String[] parts = line.split(" ");
                String query = parts[0];
                String rules = parts[1];
                sum1 += solve(query, rules, 0);
                sum2 += solve(unfold(query, "?"), unfold(rules, ","), 0);
            }
            System.out.println("Part 1: " + sum1);
            System.out.println("Part 2: " + sum2);
        }
    }

    final Map<String, Long> memo = new HashMap<>();

    long solve(String query, String rules, int n) {
        String key = query + "/" + rules + "/" + n;
        Long sum = memo.get(key);
        if (sum != null) return sum;

        List<Integer> groups = Arrays.stream(rules.split(",")).mapToInt(Integer::parseInt).boxed().toList();
        sum = 0L;
        int size = groups.get(n);
        for (int i = 0; i <= query.length() - size; i++) {
            if (spaces(query, 0, i) && fits(query.substring(i), size)) {
                if (n == groups.size() - 1) {
                    int remaining = query.length() - size - i;
                    if (spaces(query, i + size, remaining)) {
                        sum += 1;
                    }
                } else if (i + size + 1 < query.length()) {
                    sum += solve(query.substring(i + size + 1), rules, n + 1);
                }
            }
        }

        memo.put(key, sum);
        return sum;
    }

    boolean spaces(String query, int s, int n) {
        for (int i = 0; i < n; i++) {
            if (query.charAt(s + i) == '#') return false;
        }
        return true;
    }

    boolean fits(String query, int size) {
        int i = 0;
        while (i < size) {
            char ch = query.charAt(i);
            if (ch == '.') return false;
            i += 1;
        }
        return i >= query.length() || query.charAt(i) != '#';
    }

    String unfold(String s, String delimiter) {
        String result = s;
        for (int i = 1; i < 5; i++) {
            result = result + delimiter + s;
        }
        return result;
    }
}
