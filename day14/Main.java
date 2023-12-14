import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                char[] row = new char[line.length()];
                for (int i = 0; i < row.length; i++) {
                    row[i] = line.charAt(i);
                }
                board.add(row);
            }

            int sum1 = calculate(board);

            System.out.println("Part 1: " + sum1);

        }
    }

    boolean north(List<char[]> board) {
        boolean moved = false;
        for (int r = 0; r < board.size() - 1; r++) {
            char[] row = board.get(r);
            char[] next = board.get(r + 1);
            for (int i = 0; i < row.length; i++) {
                if (next[i] == 'O' && row[i] == '.') {
                    row[i] = 'O';
                    next[i] = '.';
                    moved = true;
                }
            }
        }
        return moved;
    }

    int calculate(List<char[]> board) {
        while (north(board)) ;

        int sum = 0;
        for (int r = 0; r < board.size(); r++) {
            char[] row = board.get(r);
            for (int c = 0; c < row.length; c++) {
                if (row[c] == 'O') {
                    sum += board.size() - r;
                }
            }
        }
        return sum;
    }

    private void printBoard(List<char[]> board) {
        for (char[] row : board) {
            String line = "";
            for (int i = 0; i < row.length; i++) {
                line += row[i];
            }
            System.out.println(line);
        }
    }
}
