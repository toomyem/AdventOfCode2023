import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

record Part(Map<Character, Integer> values) {
    int get(char var) {
        return values.getOrDefault(var, 0);
    }

    int sum() {
        return values.values().stream().mapToInt(i -> i).sum();
    }
}

class Cond implements Predicate<Integer> {
    final char var;
    final Predicate<Integer> pred;
    private final String info;

    Cond(char var, String info, Predicate<Integer> pred) {
        this.var = var;
        this.pred = pred;
        this.info = info;
    }

    boolean test(Part part) {
        return pred.test(part.get(var));
    }

    @Override
    public boolean test(Integer v) {
        return pred.test(v);
    }

    @Override
    public Cond negate() {
        return new Cond(var,"not " + info, pred.negate());
    }

    @Override
    public String toString() {
        return info;
    }
}

class Rule {
    final char var;
    final Cond pred;
    final String next;

    Rule(char var, char op, int value, String next) {
        this.var = var;
        this.pred = switch (op) {
          case '>' -> new Cond(var, var + ">" + value, i -> i > value);
          case '<' -> new Cond(var, var + "<" + value, i -> i < value);
          default -> new Cond(var, "true", part -> true);
        };
        this.next = next;
    }

    String check(Part part) {
        return pred.test(part) ? next : null;
    }

    @Override
    public String toString() {
        return pred + ":" + next;
    }
}

class Workflow {
    final List<Rule> rules = new ArrayList<>();

    Workflow(String line) {
        String[] parts = line.split(",");
        for (String part : parts) {
            int i = part.indexOf(':');
            if (i != -1) {
                char var = part.charAt(0);
                char op = part.charAt(1);
                int value = Integer.parseInt(part.substring(2, i));
                String next = part.substring(i + 1);
                rules.add(new Rule(var, op, value, next));
            } else {
                rules.add(new Rule('?', 't', 0, part));
            }
        }
    }

    String process(Part part) {
        String result = null;
        for (Rule rule : rules) {
            result = rule.check(part);
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

            int sum1 = solve1(workflows, parts);
            System.out.println("Part 1: " + sum1);

            long sum2 = solve2(workflows);
            System.out.println("Part 2: " + sum2);
        }
    }

    int solve1(Map<String, Workflow> workflows, List<Part> parts) {
        List<Part> accepted = parts.stream().filter(part -> matchesRules(part, workflows)).toList();
        return accepted.stream().mapToInt(Part::sum).sum();
    }

    boolean matchesRules(Part part, Map<String, Workflow> workflows) {
        Workflow workflow = workflows.get("in");
        while (true) {
            String result = workflow.process(part);
            if (result.equals("A")) return true;
            if (result.equals("R")) return false;
            workflow = workflows.get(result);
        }
    }

    long solve2(Map<String, Workflow> workflows) {
        Workflow w = workflows.get("in");
        return traverse(workflows, w, new ArrayList<>(), 0);
    }

    long traverse(Map<String, Workflow> workflows, Workflow w, List<Cond> list, int lvl) {
        long sum = 0;
        int added = 0;
        for (Rule rule : w.rules) {
            if (rule.var != '?') {
                list.add(rule.pred);
                added += 1;
            }
            String next = rule.next;
            if (next.equals("A")) {
                sum += count(list);
            } else if (!next.equals("R")) {
                Workflow w2 = workflows.get(next);
                sum += traverse(workflows, w2, list, lvl + 1);
            }
            if (rule.var != '?') {
                list.remove(list.size() - 1);
                list.add(rule.pred.negate());
            } else {
                while (added > 0) {
                    list.remove(list.size() - 1);
                    added -= 1;
                }
            }
        }
        return sum;
    }

    long count(List<Cond> list) {
        Map<Character, List<Cond>> map = list.stream().collect(Collectors.groupingBy(c -> c.var));
        long total = 1;
        for (char var : Set.of('a', 's', 'm', 'x')) {
            List<Cond> conditions = map.get(var);
            long count = 4000;
            if (conditions != null) {
                Predicate<Integer> p = conditions.stream().map(c -> c.pred).reduce(x -> true, Predicate::and);
                count = IntStream.range(1, 4001).filter(p::test).count();
            }
            total *= count;
        }
        return total;
    }
}
