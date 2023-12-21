import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

enum Dir { UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);
    final int dx;
    final int dy;
    Dir(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

}

record Pos(int row, int col) {
    Pos go(Dir dir) {
        return new Pos(row + dir.dy, col + dir.dx);
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}

record Box(int left, int right, int top, int bottom) {
    public boolean contains(Pos p) {
        return p.col() >= left && p.col() <= right && p.row() >= top && p.row() <= bottom;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            Set<Pos> garden = new HashSet<>();
            Pos start = null;
            int row = 0;
            int col = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                for (col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    Pos pos = new Pos(row, col);
                    if (ch == '#') {
                        garden.add(pos);
                    } else if (ch == 'S') {
                        start = pos;
                    }
                }
                row += 1;
            }

            int sum1 = solve1(garden, start, row, col);
            System.out.println("Part 1: " + sum1);

            long sum2 = solve2(garden, start, row, col);
            System.out.println("Part 2: " + sum2);
        }
    }

    private int solve1(Set<Pos> garden, Pos start, int rows, int cols) {
        List<Pos> queue = new ArrayList<>();
        Pos marker = new Pos(-1, -1);
        queue.add(start);
        queue.add(marker);
        int steps = 0;

        while (steps < 64 && !queue.isEmpty()) {
            Pos pos = queue.remove(0);
            if (pos == marker) {
                steps += 1;
                queue.add(marker);
                continue;
            }
            for (Dir dir : Dir.values()) {
                Pos p2 = pos.go(dir);
                if (p2.row() >= 0 && p2.row() < rows && p2.col() >= 0 && p2.col() <= cols) {
                    if (!garden.contains(p2) && !queue.contains(p2)) {
                        queue.add(p2);
                    }
                }
            }
        }

        Set<Pos> visited = new HashSet<>(queue);
        return visited.size() - 1;
    }

    private long solve2(Set<Pos> garden, Pos start, int rows, int cols) {
        List<Pos> queue = new ArrayList<>();
        Set<Pos> set = new HashSet<>();
        Pos marker = new Pos(-1, -1);
        queue.add(start);
        queue.add(marker);
        set.add(start);
        int steps = rows * 2 + rows / 2;

        while (steps > 0 && !queue.isEmpty()) {
            Pos pos = queue.remove(0);
            set.remove(pos);
            if (pos == marker) {
                steps -= 1;
                System.out.println("Steps: " + steps + ", size: " + queue.size());
                queue.add(marker);
                continue;
            }
            for (Dir dir : Dir.values()) {
                Pos p2 = pos.go(dir);
                Pos p3 = new Pos(wrap(p2.row(), rows), wrap(p2.col(), cols));
                if (!garden.contains(p3) && !set.contains(p2)) {
                    queue.add(p2);
                    set.add(p2);
                }
            }
        }

        List<Long> partials = new ArrayList<>();
        for (int row = -2 * rows; row < 3 * rows; row += rows) {
            for (int col = -2 * cols; col < 3 * cols; col += cols) {
                Box box = new Box(col, col + cols - 1, row, row + rows - 1);
                long sum = set.stream().filter(box::contains).count();
                partials.add(sum);
                System.out.format("%5d ", sum);
            }
            System.out.println();
        }

        steps = 26501365;
        long horiz = (steps * 2 + 1) / rows;
        long diag = (horiz + 1) / 2;
        long sizeA = (diag - 1) * (diag - 1);
        long sizeB = (diag - 2) * (diag - 2);
        long sizeC = diag - 1;
        long sizeD = diag - 2;
        long sum = 0;
        long[] coef = new long[]{
                0L,    sizeC, 1L,    sizeC, 0L,
                0L,    sizeD, sizeA, sizeD, 0L,
                1L,    0L,    sizeB, 0L,    1L,
                sizeC, sizeD, 0L,    sizeD, sizeC,
                0L,    0L,    1L,    0L,    0L
        };
        for (int i = 0; i < coef.length; i++) {
            sum += coef[i] * partials.get(i);
        }
        return sum;
    }

    int wrap(int i, int m) {
        int j = i % m;
        if (j < 0) j += m;
        return j;
    }
}
