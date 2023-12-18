import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Dir { RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0), UP(0, -1);
    final int dx;
    final int dy;
    Dir(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    static Dir of(char ch) {
        for (Dir dir : values()) {
            if (dir.name().charAt(0) == ch) return dir;
        }
        return null;
    }
}

record Pos(int row, int col) {
    Pos go(Dir dir, int steps) {
        return new Pos(row + steps * dir.dy, col + steps * dir.dx);
    }
}

record Line(Pos p1, Pos p2) {
    Line(Pos p1, Pos p2) {
        this.p1 = new Pos(Math.min(p1.row(), p2.row()), Math.min(p1.col(), p2.col()));
        this.p2 = new Pos(Math.max(p1.row(), p2.row()), Math.max(p1.col(), p2.col()));
    }

    boolean isHoriz() {
        return p1.row() == p2.row();
    }

    int length() {
        return isHoriz() ? p2.col() - p1.col() : p2.row() - p1.row();
    }

}

record Dig(Dir dir, int steps) {}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {
            Pattern p = Pattern.compile("(.) (\\d+) \\(#(.....)(.)\\)");
            List<Dig> digs1 = new ArrayList<>();
            List<Dig> digs2 = new ArrayList<>();

            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    Dir dir = Dir.of(m.group(1).charAt(0));
                    int steps = Integer.parseInt(m.group(2));
                    digs1.add(new Dig(dir, steps));
                    steps = Integer.parseInt(m.group(3), 16);
                    dir = Dir.values()[Integer.parseInt(m.group(4))];
                    digs2.add(new Dig(dir, steps));
                }
            }

            long sum1 = count(digs1);
            System.out.println("Part 1: " + sum1);

            long sum2 = count(digs2);
            System.out.println("Part 2: " + sum2);
        }
    }

    private final Set<Dir> CF = Set.of(Dir.DOWN, Dir.RIGHT);
    private final Set<Dir> CJ = Set.of(Dir.UP, Dir.LEFT);
    private final Set<Dir> C7 = Set.of(Dir.DOWN, Dir.LEFT);
    private final Set<Dir> CL = Set.of(Dir.UP, Dir.RIGHT);
    private final Set<Dir> CV = Set.of(Dir.UP, Dir.DOWN);


    long countBorder(List<Line> lines) {
        return lines.stream().mapToInt(Line::length).sum();
    }

    boolean isVert(Set<Dir> prev, Set<Dir> curr) {
        if (curr.equals(CV)) return true;
        if (prev == null) return false;
        return (prev.equals(CF) && curr.equals(CJ)) || (prev.equals(CL) && curr.equals(C7));
    }

    boolean isUShape(Set<Dir> prev, Set<Dir> curr) {
        if (curr.equals(CV)) return false;
        if (prev == null) return false;
        return (prev.equals(CF) && curr.equals(C7)) || (prev.equals(CL) && curr.equals(CJ));
    }

    long count(List<Dig> digs) {
        List<Line> lines = build(digs);
        long sum = countBorder(lines);
        int y1 = lines.stream().mapToInt(l -> l.p1().row()).min().getAsInt();
        int y2 = lines.stream().mapToInt(l -> l.p2().row()).max().getAsInt();

        for (int r = y1; r <= y2; r++) {
            Map<Integer, Set<Dir>> row = buildRow(r, linesInRow(r, lines));
            List<Integer> columns = row.keySet().stream().sorted().toList();
            Integer begin = null;
            Set<Dir> prev = null;
            boolean counted = false;
            int m = 0;
            for (int col : columns) {
                Set<Dir> dirs = row.get(col);
                if (begin != null && !counted) {
                    m += col - begin - 1;
                    counted = true;
                }

                if (isVert(prev, dirs)) {
                    if (begin == null) {
                        begin = col;
                        counted = false;
                    } else {
                        begin = null;
                    }
                }

                if (isUShape(prev, dirs))
                    if (begin != null) {
                        begin = col;
                        counted = false;
                }
                prev = dirs;
            }

            sum += m;
        }
        return sum;
    }

    Map<Integer, Set<Dir>> buildRow(int row, List<Line> lines) {
        Map<Integer, Set<Dir>> map = new HashMap<>();
        lines.forEach(line -> {
            if (line.isHoriz()) {
                map.computeIfAbsent(line.p1().col(), k -> new HashSet<>()).add(Dir.RIGHT);
                map.computeIfAbsent(line.p2().col(), k -> new HashSet<>()).add(Dir.LEFT);
            } else if (line.p1().row() == row) {
                map.computeIfAbsent(line.p1().col(), k -> new HashSet<>()).add(Dir.DOWN);
            } else if (line.p2().row() == row) {
                map.computeIfAbsent(line.p2().col(), k -> new HashSet<>()).add(Dir.UP);
            } else {
                map.computeIfAbsent(line.p1().col(), k -> new HashSet<>()).add(Dir.UP);
                map.computeIfAbsent(line.p1().col(), k -> new HashSet<>()).add(Dir.DOWN);
            }
        });
        return map;
    }

    List<Line> build(List<Dig> digs) {
        List<Line> lines = new ArrayList<>();
        Pos pos = new Pos(0, 0);
        Pos prev;

        for (Dig dig : digs) {
            Dir dir = dig.dir();
            int steps = dig.steps();
            prev = pos;
            pos = pos.go(dir, steps);
            lines.add(new Line(prev, pos));
        }
        return lines;
    }

    List<Line> linesInRow(int row, List<Line> lines) {
        return lines.stream().filter(line ->
            line.p1().row() == row || line.p2().row() == row ||
                    (line.p1().row() < row && line.p2().row() > row)
        ).toList();
    }
}
