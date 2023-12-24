import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

record Pos(long x, long y, long z) {
    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}

record Stone(Pos p, Pos v) {}

record Params(double a, double b) {}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            List<Stone> stones = new ArrayList<>();
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                String[] parts = line.split(" @ ");
                Stone s = new Stone(toPos(parts[0]), toPos(parts[1]));
                stones.add(s);
            }

            int sum1 = solve1(stones);
            System.out.println("Part 1: " + sum1);
        }
    }

    Pos toPos(String s) {
        String[] parts = s.split(",");
        return new Pos(Long.parseLong(parts[0].trim()), Long.parseLong(parts[1].trim()), Long.parseLong(parts[2].trim()));
    }

    int solve1(List<Stone> stones) {
        int sum = 0;
        long g1 = 200000000000000L;
        long g2 = 400000000000000L;
        for (int i = 0; i < stones.size(); i++) {
            Stone s1 = stones.get(i);
            for (int j = i + 1; j < stones.size(); j++) {
                Stone s2 = stones.get(j);
                Params p1 = calcParams(s1);
                Params p2 = calcParams(s2);
                double x = (p2.b() - p1.b()) / (p1.a() - p2.a());
                double y = p1.a() * x + p1.b();
                if (p1.a() != p2.a() && x >= g1 && x <= g2 && y >= g1 && y <= g2 && inFuture(x, y, s1) && inFuture(x, y, s2)) {
                    sum += 1;
                }
            }
        }
        return sum;
    }

    boolean inFuture(double x, double y, Stone s) {
        if (s.v().x() > 0 && x < s.p().x()) return false;
        if (s.v().x() < 0 && x > s.p().x()) return false;
        if (s.v().y() > 0 && y < s.p().y()) return false;
        if (s.v().y() < 0 && y > s.p().y()) return false;
        return true;
    }

    Params calcParams(Stone s) {
        double a = (double) s.v().y() / s.v().x();
        double b = s.p().y() - a * s.p().x();
        return new Params(a, b);
    }
}
