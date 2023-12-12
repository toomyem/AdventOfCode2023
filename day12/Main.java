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

            int sum1 = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                String[] parts = line.split(" ");
                String query = parts[0];
                String rules = parts[1];
                sum1 += solve(query, rules);
            }
            System.out.println("Part 1: " + sum1);
        }
    }

    int solve(String query, String rules) {
        int q = countQuestionMarks(query);
        int n = 1 << q;
        int m = 0;

        for (int i = 0; i < n; i++) {
            String r = replace(query, i);
            String g = groups(r);
            if (g.equals(rules)) m++;
        }
        System.out.println(query + " -> " + m);
        return m;
    }

    String groups(String s) {
        List<String> result = new ArrayList<>();
        int g = 0;
        int i = 0;
        while (i <= s.length()) {
            char ch = i < s.length() ? s.charAt(i) : '.';
            if (ch == '#') {
                g++;
            } else {
                if (g > 0) {
                    result.add("" + g);
                    g = 0;
                }
            }
            i++;
        }
        return String.join(",", result);
    }

    String replace(String s, int n) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            char r;
            if (ch == '?') {
                r = n % 2 == 0 ? '.' : '#';
                n = n / 2;
            } else {
                r = ch;
            }
            result += r;
        }
        return result;
    }

    int countQuestionMarks(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '?') n++;
        }
        return n;
    }
}
