import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

enum Dir {
    UP(0, -1, '^'),
    RIGHT(1, 0, '>'),
    DOWN(0, 1, 'v'),
    LEFT(-1, 0, '<');

    final int dx;
    final int dy;
    final char ch;

    Dir(int dx, int dy, char ch) {
        this.dx = dx;
        this.dy = dy;
        this.ch = ch;
    }

    Dir inv() {
        return values()[(this.ordinal() + 2) % 4];
    }
}

record Pos(int x, int y) {
    Pos go(Dir dir) {
        return new Pos(x + dir.dx, y + dir.dy);
    }
}

record Edge(Pos dest, int weight, boolean forward) {}
record Node(Set<Edge> edges, boolean isEnd) {}
record Trail(Pos dest, int steps, boolean forward, List<Pos> visited) {}
class Graph extends HashMap<Pos, Node> {}
class Board extends HashMap<Pos, Character> {}

public class Main {

    public static void main(String[] args) throws Exception {
        new Main().run(args.length > 0 ? args[0] : "input.txt");
    }

    void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            Board board = new Board();
            Pos start = null, end = null;
            int y = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                for (int x = 0; x < line.length(); x++) {
                    char ch = line.charAt(x);
                    Pos pos = new Pos(x, y);
                    board.put(pos, ch);
                    if (ch == '.') {
                        if (y == 0) {
                            start = pos;
                        }
                        end = pos;
                    }
                }
                y += 1;
            }
            board.put(start, 'S');
            board.put(end, 'E');

            Graph graph = buildGraph(board);

            int sum1 = solve(start, graph, true);
            System.out.println("Part 1: " + sum1);

            int sum2 = solve(start, graph, false);
            System.out.println("Part 2: " + sum2);
        }
    }

    int solve(Pos start, Graph graph, boolean forwardOnly) {
        return walk(graph, start, 0, new HashSet<>(), forwardOnly);
    }

    boolean isNode(Pos pos, Board board) {
        int exits = 0;
        char ch = board.getOrDefault(pos, '#');
        if (ch == 'S' || ch == 'E') return true;
        if (ch != '.') return false;
        for (Dir dir : Dir.values()) {
            if (board.getOrDefault(pos.go(dir), '#') != '#') {
                exits += 1;
            }
        }
        return exits >= 3;
    }

    Graph buildGraph(Board board) {
        Graph graph = new Graph();
        List<Pos> nodes = board.keySet().stream().filter(pos -> isNode(pos, board)).toList();
        for (Pos pos : nodes) {
            for (Dir dir : Dir.values()) {
                Trail trail = walk(board, pos, dir);
                if (trail == null) continue;
                boolean isEnd = board.get(pos) == 'E';
                Node node = graph.computeIfAbsent(pos, k -> new Node(new HashSet<>(), isEnd));
                node.edges().add(new Edge(trail.dest(), trail.steps(), trail.forward()));
            }
        }
        return graph;
    }

    Trail walk(Board board, Pos pos, Dir dir) {
        int steps = 0;
        boolean forward = false;
        List<Pos> visited = new ArrayList<>();

        while (true) {
            pos = pos.go(dir);
            char ch = board.getOrDefault(pos, '#');
            if (ch == '#') break;
            if (ch != '.' && dir.ch == ch) {
                forward = true;
            }
            visited.add(pos);
            if (isNode(pos, board)) break;
            for (Dir dir2 : Dir.values()) {
                Pos pos2 = pos.go(dir2);
                if (dir2 != dir.inv() && board.getOrDefault(pos2, '#') != '#') {
                    dir = dir2;
                    steps += 1;
                    break;
                }
            }
        }
        return steps == 0 ? null : new Trail(pos, steps + 1, forward, visited);
    }

    int walk(Graph graph, Pos pos, int steps, Set<Pos> visited, boolean forwardOnly) {
        Node node = graph.get(pos);
        if (visited.contains(pos)) return 0;
        if (node.isEnd()) return steps;
        visited.add(pos);
        int max = 0;
        for (Edge edge : node.edges()) {
            if (forwardOnly && !edge.forward()) continue;
            int s = walk(graph, edge.dest(), steps + edge.weight(), visited, forwardOnly);
            if (s > max) {
                max = s;
            }
        }
        visited.remove(pos);
        return max;
    }
}