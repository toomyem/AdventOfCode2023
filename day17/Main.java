import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
    final int sum;

    Crucible(Pos pos, Head head, int stepsForward, int sum) {
        this.pos = pos;
        this.head = head;
        this.stepsForward = stepsForward;
        this.sum = sum;
    }

    Crucible clone(Pos pos, Head head, int stepsForward, int sum) {
        return new Crucible(pos, head, stepsForward, sum);
    }

    Pos go(Dir dir) {
        Head newHead = switch (dir) {
            case RIGHT -> Head.values()[(head.ordinal() + 1) % 4];
            case LEFT -> Head.values()[(head.ordinal() + 3) % 4];
            case STRAIGHT -> head;
        };
        return pos.go(newHead);
    }

    Crucible go(Dir dir, int heat) {
        Head newHead = switch (dir) {
            case RIGHT -> Head.values()[(head.ordinal() + 1) % 4];
            case LEFT -> Head.values()[(head.ordinal() + 3) % 4];
            case STRAIGHT -> head;
        };
        return clone(pos.go(newHead), newHead, dir == Dir.STRAIGHT ? stepsForward + 1 : 1, sum + heat);
    }

    Set<Dir> possibleDirs() {
        if (stepsForward < 3) {
            return Set.of(Dir.STRAIGHT, Dir.RIGHT, Dir.LEFT);
        }
        return Set.of(Dir.RIGHT, Dir.LEFT);
    }

    int minSteps() {
        return 0;
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
    UltraCrucible(Pos pos, Head head, int stepsForward, int sum) {
        super(pos, head, stepsForward, sum);
    }

    @Override
    Crucible clone(Pos pos, Head head, int stepsForward, int sum) {
        return new UltraCrucible(pos, head, stepsForward, sum);
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

    @Override
    int minSteps() {
        return 4;
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

            int sum1 = calc(new Crucible(start, Head.EAST, 0, 0), dest, board);
            System.out.println("Part 1: " + sum1);

            int sum2 = calc(new UltraCrucible(start, Head.EAST, 0, 0), dest, board);
            System.out.println("Part 2: " + sum2);
        }
    }

    private int calc(Crucible start, Pos dest, Board board) {
        PriorityQueue<Crucible> queue = new PriorityQueue<>(Comparator.comparingInt(c -> c.sum));
        Set<Crucible> visited = new HashSet<>();
        queue.add(start);
        int sum = 0;

        while (!queue.isEmpty()) {
            Crucible c = queue.remove();
            if (c.pos.equals(dest) && c.stepsForward >= c.minSteps()) {
                sum = c.sum;
                break;
            }
            visited.add(c);
            Set<Dir> dirs = c.possibleDirs();
            for (Dir dir : dirs) {
                Pos pos2 = c.go(dir);
                Integer cost = board.get(pos2);
                if (cost != null) {
                    Crucible c2 = c.go(dir, cost);
                    if (!visited.contains(c2)) {
                        if (!queue.contains(c2)) {
                            queue.add(c2);
                        }
                    }
                }
            }
        }
        return sum;
    }

    Pos extractPos(Crucible c) {
        return c.pos;
    }
}
