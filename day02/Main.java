import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        int sum1 = 0;
        int sum2 = 0;
        int id = 0;

        while (in.hasNext()) {
            String line = in.nextLine();
            id += 1;
            System.out.print(line);
            String[] parts = line.split("[;,:']");
            Pattern p = Pattern.compile("(\\d+) (\\w+)");
            Map<String, Integer> map = new HashMap<>();
            for (String part : parts) {
                Matcher m = p.matcher(part);
                if (m.find() && m.groupCount() == 2) {
                    int n = Integer.parseInt(m.group(1));
                    String c = m.group(2);
                    map.put(c, Math.max(map.getOrDefault(c, 0), n));
                }
            }
            int red = map.getOrDefault("red", 0);
            int green = map.getOrDefault("green", 0);
            int blue = map.getOrDefault("blue", 0);
            int power = red * green * blue;
            System.out.print(", power: " + power + ",");
            if (red <= 12 && green <= 13 && blue <= 14) {
                sum1 += id;
                System.out.println(" OK");
            } else {
                System.out.println(" NOT");
            }
            sum2 += power;
        }
        System.out.println("Sum 1: " + sum1);
        System.out.println("Sum 2: " + sum2);
    }
}
