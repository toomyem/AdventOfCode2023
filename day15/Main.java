import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

record Lens(String label, int focal) {}

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().run();
    }

    void run() throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {

            List<List<Lens>> boxes = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                List<Lens> box = new ArrayList<>();
                boxes.add(box);
            }

            Pattern p = Pattern.compile("(\\w+)([=-])(\\d*)");

            int sum1 = 0;
            int sum2 = 0;

            while (in.hasNext()) {
                String line = in.nextLine();
                String[] parts = line.split(",");
                for (String s : parts) {
                    sum1 += hash(s);
                    Matcher m = p.matcher(s);
                    if (!m.matches()) continue;
                    String label = m.group(1);
                    char op = m.group(2).charAt(0);
                    List<Lens> box = boxes.get(hash(label));
                    if (op == '=') {
                        int focal = Integer.parseInt(m.group(3));
                        Lens l = new Lens(label, focal);
                        insert(box, l);
                    } else if (op == '-') {
                        remove(box, label);
                    }
                }
                for (int i = 0; i < boxes.size(); i++) {
                    List<Lens> box = boxes.get(i);
                    for (int j = 0; j < box.size(); j++) {
                        sum2 += (i + 1) * (j + 1) * box.get(j).focal();
                    }
                }
            }

            System.out.println("Part 1: " + sum1);
            System.out.println("Part 2: " + sum2);
        }
    }

    void insert(List<Lens> box, Lens lens) {
        for (int i = 0; i < box.size(); i++) {
            if (box.get(i).label().equals(lens.label())) {
                box.set(i, lens);
                return;
            }
        }
        box.add(lens);
    }

    void remove(List<Lens> box, String label) {
        box.removeIf(l -> l.label().equals(label));
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
