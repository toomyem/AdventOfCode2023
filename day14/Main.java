import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    void run() throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {

            List<char[]> board = new ArrayList<>();

            while (in.hasNext()) {
                String line = in.nextLine();
                board.add(line.toCharArray());
            }

            int sum1 = weight(north(board));
            int sum2 = weight(calculate(board));

            System.out.println("Part 1: " + sum1);
            System.out.println("Part 2: " + sum2);
        }
    }

    List<char[]> north(List<char[]> oldBoard) {
        List<char[]> board = new ArrayList<>();
        for (char[] row : oldBoard) {
            board.add(row.clone());
        }

        for (int r = 1; r < board.size(); r++) {
            char[] row = board.get(r);
            for (int c = 0; c < row.length; c++) {
                if (row[c] == 'O') {
                    int n = r;
                    while (n > 0 && board.get(n-1)[c] == '.') {
                        n--;
                    }
                    row[c] = '.';
                    board.get(n)[c] = 'O';
                }
            }
        }
        return board;
    }

    List<char[]> turn(List<char[]> oldBoard) {
        List<char[]> board = new ArrayList<>();
        int rows = oldBoard.size();
        int cols = oldBoard.get(0).length;

        for (int c = 0; c < cols; c++) {
            char[] row = new char[rows];
            for (int r = rows - 1; r >= 0; r--) {
                row[rows - r - 1] = oldBoard.get(r)[c];
            }
            board.add(row);
        }
        return board;
    }

    private final Map<String, Integer> memo = new HashMap<>();
    private final Map<Integer, List<char[]>> step = new HashMap<>();

    String getKey(List<char[]> board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            sb.append(new String(row));
            sb.append("\n");
        }
        return sb.toString();
    }

    List<char[]> cycle(List<char[]> board) {
        for (int i = 0; i < 4; i++) {
            board = turn(north(board));
        }
        return board;
    }

    List<char[]> calculate(List<char[]> board) {
        int i = 0;
        int s = 0;
        while (i < 1000000000) {
            String key = getKey(board);
            s = memo.getOrDefault(key, 0);
            if (s != 0) {
                break;
            }
            memo.putIfAbsent(key, i);
            step.put(i, board);
            board = cycle(board);
            i += 1;
        }
        int r = (1000000000 - s) % (i - s) + s;
        board = step.get(r);

        return board;
    }

    int weight(List<char[]> board) {
        int sum = 0;
        int rows = board.size();
        for (int r = 0; r < rows; r++) {
            char[] row = board.get(r);
            for (int c = 0; c < row.length; c++) {
                if (row[c] == 'O') {
                    sum += rows - r;
                }
            }
        }
        return sum;
    }
}
