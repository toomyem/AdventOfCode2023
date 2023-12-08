import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

record Pair (String left, String right) {}

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {

            String code = in.nextLine();
            Map<String, Pair> map = new HashMap<>();
            Pattern rex = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");

            while(in.hasNext()) {
                String line = in.nextLine();
                Matcher m = rex.matcher(line);
                if (!m.find()) continue;
                String node = m.group(1);
                map.put(node, new Pair(m.group(2), m.group(3)));
            }

            int step = calcSteps(code, map, "AAA");
            System.out.println("Part 1: " + step);

            List<String> nodes = map.keySet().stream().filter(n -> n.endsWith("A")).toList();
            List<Integer> steps = new ArrayList<>();
            for (String n : nodes) {
                steps.add(calcSteps(code, map, n));
            }
            long step2 = lcm(steps);
            System.out.println("Part 2: " + step2);
        }
    }

    static int calcSteps(String code, Map<String, Pair> map, String node) {
        int step = 0;
        while (!node.endsWith("Z")) {
            Pair p = map.get(node);
            if (code.charAt(step % code.length()) == 'R') {
                node = p.right();
            } else {
                node = p.left();
            }
            step += 1;
        }
        return step;
    }

    static long gcd(long n1, long n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }

    static long lcm(long n1, long n2) {
        return (n1 / gcd(n1, n2)) * n2;
    }

    static long lcm(List<Integer> values) {
        return values.stream().mapToLong(i -> i).reduce(1, Main::lcm);
    }
}
