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

            int sum2 = solve2(garden, start, row, col);
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

    private int solve2(Set<Pos> garden, Pos start, int rows, int cols) {
        List<Pos> queue = new ArrayList<>();
        Set<Pos> set = new HashSet<>();
        Pos marker = new Pos(-1, -1);
        queue.add(start);
        queue.add(marker);
        set.add(start);
        int steps = 0;

        while (steps < 26501365 && !queue.isEmpty()) {
            Pos pos = queue.remove(0);
            set.remove(pos);
            if (pos == marker) {
                steps += 1;
                //printGarden(garden, rows, cols, queue);
                System.out.println("Steps: " + steps + ", size: " + queue.size());// + ", queue: " + queue);
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

        Set<Pos> visited = new HashSet<>(queue);
        return visited.size() - 1;
    }

    int wrap(int i, int m) {
        int j = i % m;
        if (j < 0) j += m;
        return j;
    }

    private void printGarden(Set<Pos> garden, int rows, int cols, List<Pos> queue) {
        System.out.println("\nGarden:");
        for (int row = 0; row < rows; row++) {
            String line = "";
            for (int col = 0; col < cols; col++) {
                Pos pos = new Pos(row, col);
                char ch = garden.contains(pos) ? '#' : '.';
                if (queue.contains(pos)) {
                    ch = 'O';
                }
                line += ch;
            }
            System.out.println(line);
        }
    }
}
