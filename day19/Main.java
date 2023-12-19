import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

record Part(Map<Character, Integer> values) {

    int get(char var) {
        return values.get(var);
    }

    int sum() {
        return values.values().stream().mapToInt(i -> i).sum();
    }
}

class Workflow {
    private final List<Function<Part, String>> rules = new ArrayList<>();

    Workflow(String line) {
        String[] parts = line.split(",");
        for (String part : parts) {
            int i = part.indexOf(':');
            if (i != -1) {
                char var = part.charAt(0);
                char op = part.charAt(1);
                int value = Integer.parseInt(part.substring(2, i));
                String next = part.substring(i + 1);
                if (op == '<') {
                    rules.add(p -> p.get(var) < value ? next : null);
                } else if (op == '>') {
                    rules.add(p -> p.get(var) > value ? next : null);
                }
            } else {
                rules.add(p -> part);
            }
        }
    }

    String process(Part part) {
        String result = null;
        for (Function<Part, String> rule : rules) {
            result = rule.apply(part);
            if (result != null) break;
        }
        return result;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            Map<String, Workflow> workflows = new HashMap<>();
            List<Part> parts = new ArrayList<>();

            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                int i = line.indexOf('{');
                String name = line.substring(0, i);
                int j = line.indexOf('}');
                line = line.substring(i + 1, j);
                workflows.put(name, new Workflow(line));
            }

            while (in.hasNext()) {
                String line = in.nextLine();
                String[] vars = line.substring(1, line.length() - 1).split(",");
                Map<Character, Integer> map = new HashMap<>();
                for (String var : vars) {
                    char name = var.charAt(0);
                    int val = Integer.parseInt(var.substring(2));
                    map.put(name, val);
                }
                parts.add(new Part(map));
            }

            int sum1 = calc(workflows, parts);
            System.out.println("Part 1: " + sum1);
        }
    }

    private int calc(Map<String, Workflow> workflows, List<Part> parts) {
        List<Part> accepted = parts.stream().filter(part -> matchesRules(part, workflows)).toList();
        return accepted.stream().mapToInt(Part::sum).sum();
    }

    private boolean matchesRules(Part part, Map<String, Workflow> workflows) {
        Workflow workflow = workflows.get("in");
        while (true) {
            String result = workflow.process(part);
            if (result.equals("A")) return true;
            if (result.equals("R")) return false;
            workflow = workflows.get(result);
        }
    }
}
