import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

enum Head {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);
    final int dx;
    final int dy;

    Head(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}

enum Dir { STRAIGHT, LEFT, RIGHT }

record Pos(int row, int col) {
    Pos go(Head head) {
        return new Pos(row + head.dy, col + head.dx);
    }

    @Override
    public String toString() {
        return row + "," + col;
    }
}

class Board extends HashMap<Pos, Integer> {}

class Crucible {
    final Pos pos;
    final Head head;
    final int stepsForward;

    Crucible(Pos pos, Head head, int stepsForward) {
        this.pos = pos;
        this.head = head;
        this.stepsForward = stepsForward;
    }

    Crucible clone(Pos pos, Head head, int stepsForward) {
        return new Crucible(pos, head, stepsForward);
    }

    Crucible go(Dir dir) {
        Head newHead = switch (dir) {
            case RIGHT -> Head.values()[(head.ordinal() + 1) % 4];
            case LEFT -> Head.values()[(head.ordinal() + 3) % 4];
            case STRAIGHT -> head;
        };
        return clone(pos.go(newHead), newHead, dir == Dir.STRAIGHT ? stepsForward + 1 : 1);
    }

    Set<Dir> possibleDirs() {
        if (stepsForward < 3) {
            return Set.of(Dir.STRAIGHT, Dir.RIGHT, Dir.LEFT);
        }
        return Set.of(Dir.RIGHT, Dir.LEFT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crucible crucible)) return false;
        return stepsForward == crucible.stepsForward && Objects.equals(pos, crucible.pos) && head == crucible.head;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, head, stepsForward);
    }

    @Override
    public String toString() {
        return pos + " " + head + " " + stepsForward;
    }
}

class UltraCrucible extends Crucible {
    UltraCrucible(Pos pos, Head head, int stepsForward) {
        super(pos, head, stepsForward);
    }

    @Override
    Crucible clone(Pos pos, Head head, int stepsForward) {
        return new UltraCrucible(pos, head, stepsForward);
    }

    @Override
    Set<Dir> possibleDirs() {
        if (stepsForward == 0) {
            return Set.of(Dir.STRAIGHT, Dir.RIGHT, Dir.LEFT);
        }
        if (stepsForward < 4) {
            return Set.of(Dir.STRAIGHT);
        }
        if (stepsForward < 10) {
            return Set.of(Dir.STRAIGHT, Dir.RIGHT, Dir.LEFT);
        }
        return Set.of(Dir.RIGHT, Dir.LEFT);
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {
            Board board = new Board();

            int row = 0;
            int col = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                for (col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    board.put(new Pos(row, col), ch - '0');
                }
                row += 1;
            }

            Pos start = new Pos(0, 0);
            Pos dest = new Pos(row - 1, col - 1);

            int sum1 = calc(new Crucible(start, Head.EAST, 0), dest, board);
            System.out.println("Part 1: " + sum1);

            int sum2 = calc(new UltraCrucible(start, Head.EAST, 0), dest, board);
            System.out.println("Part 2: " + sum2);
        }
    }

    private int calc(Crucible start, Pos dest, Board board) {
        Map<Crucible, Integer> cache = new HashMap<>();
        PriorityQueue<Crucible> queue = new PriorityQueue<>(Comparator.comparingInt(c -> cache.getOrDefault(c, Integer.MAX_VALUE)));
        Set<Crucible> visited = new HashSet<>();
        cache.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Crucible c = queue.remove();
            visited.add(c);
            int sum = cache.get(c);
            Set<Dir> dirs = c.possibleDirs();
            for (Dir dir : dirs) {
                Crucible c2 = c.go(dir);
                Pos pos2 = c2.pos;
                Integer cost = board.get(pos2);
                if (cost != null && !visited.contains(c2)) {
                    int t = sum + cost;
                    if (t < cache.getOrDefault(c2, Integer.MAX_VALUE)) {
                        cache.put(c2, t);
                    }
                    if (!queue.contains(c2)) {
                        queue.add(c2);
                    }
                }
            }
        }
        return cache.entrySet().stream().filter(e -> e.getKey().pos.equals(dest))
            .mapToInt(Map.Entry::getValue).min().orElse(Integer.MAX_VALUE);
    }

    Pos extractPos(Crucible c) {
        return c.pos;
    }
}
