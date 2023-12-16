import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

enum Dir {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);
    final int dx;
    final int dy;

    Dir(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean horiz() {
        return this == RIGHT || this == LEFT;
    }
}

record Pos(int row, int col) {}

record Beam(Pos pos, Dir dir) {
    public Beam move() {
        return new Beam(new Pos(pos.row() + dir.dy, pos.col() + dir.dx), dir);
    }

    public Beam turnRight() {
        return new Beam(pos, Dir.values()[(dir.ordinal() + 1) % 4]);
    }

    public Beam turnLeft() {
        return new Beam(pos, Dir.values()[(dir.ordinal() + 3) % 4]);
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    void run() throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            Map<Pos, Character> mirrors = new HashMap<>();

            int row = 0;
            int col = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                for (col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    if (ch != '.') {
                        mirrors.put(new Pos(row, col), ch);
                    }
                }
                row += 1;
            }

            int sum = simulate(row, col, mirrors, new Beam(new Pos(0, -1), Dir.RIGHT));
            System.out.println("Part 1: " + sum);

            int max = 0;
            int n;
            for (int r = 0; r < row; r++) {
                n = simulate(row, col, mirrors, new Beam(new Pos(r, -1), Dir.RIGHT));
                if (n > max) max = n;
                n = simulate(row, col, mirrors, new Beam(new Pos(r, col), Dir.LEFT));
                if (n > max) max = n;
            }
            for (int c = 0; c < col; c++) {
                n = simulate(row, col, mirrors, new Beam(new Pos(-1, c), Dir.DOWN));
                if (n > max) max = n;
                n = simulate(row, col, mirrors, new Beam(new Pos(row, c), Dir.UP));
                if (n > max) max = n;
            }
            System.out.println("Part 2: " + max);
        }
    }

    private int simulate(int rows, int cols, Map<Pos, Character> mirrors, Beam start) {
        Set<Pos> heated = new HashSet<>();
        Set<Beam> cache = new HashSet<>();
        List<Beam> beams = new ArrayList<>();
        beams.add(start);

        while (!beams.isEmpty()) {
            Beam beam = beams.remove(0);
            if (cache.contains(beam)) continue;
            cache.add(beam);
            beam = beam.move();
            if (beam.pos().row() < 0 || beam.pos().row() >= rows || beam.pos().col() < 0 || beam.pos().col() >= cols) {
                continue;
            }
            heated.add(beam.pos());
            char m = mirrors.getOrDefault(beam.pos(), '.');
            if (m == '/') {
                if (beam.dir().horiz()) {
                    beams.add(beam.turnLeft());
                } else {
                    beams.add(beam.turnRight());
                }
            } else if (m == '\\') {
                if (beam.dir().horiz()) {
                    beams.add(beam.turnRight());
                } else {
                    beams.add(beam.turnLeft());
                }
            } else if (m == '|') {
                if (beam.dir().horiz()) {
                    beams.add(beam.turnRight());
                    beams.add(beam.turnLeft());
                } else {
                    beams.add(beam);
                }
            } else if (m == '-') {
                if (!beam.dir().horiz()) {
                    beams.add(beam.turnRight());
                    beams.add(beam.turnLeft());
                } else {
                    beams.add(beam);
                }
            } else {
                beams.add(beam);
            }
        }
        return heated.size();
    }
}
