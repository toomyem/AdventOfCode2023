import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        int sum = 0;
        int n = 0;
        while (in.hasNext()) {
            int d1 = -1, d2 = -1, d;
            String line = in.nextLine();
            System.out.printf("%d) '%s' -> ", ++n, line);
            for(int i = 0; i < line.length(); i++) {
                d = -1;
                if (line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                    d = line.charAt(i) - '0';
                }
                if (line.substring(i).startsWith("one")) d = 1;
                if (line.substring(i).startsWith("two")) d = 2;
                if (line.substring(i).startsWith("three")) d = 3;
                if (line.substring(i).startsWith("four")) d = 4;
                if (line.substring(i).startsWith("five")) d = 5;
                if (line.substring(i).startsWith("six")) d = 6;
                if (line.substring(i).startsWith("seven")) d = 7;
                if (line.substring(i).startsWith("eight")) d = 8;
                if (line.substring(i).startsWith("nine")) d = 9;

                if (d > -1) {
                    if (d1 == -1) d1 = d;
                    d2 = d;
                }
            }
            int v = d1 * 10 + d2;
            sum += v;
            System.out.printf("v=%d, sum=%d%n", v, sum);
        }
    }
}
