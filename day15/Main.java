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

            int sum1 = 0;

            while (in.hasNext()) {
                String line = in.nextLine();
                String[] parts = line.split(",");
                for (String s : parts) {
                    sum1 += hash(s);
                }
            }

            System.out.println("Part 1: " + sum1);
        }
    }

    int hash(String s) {
        int h = 0;
        for (char ch : s.toCharArray()) {
            h += ch;
            h *= 17;
            h = h % 256;
        }
        return h;
    }
}
