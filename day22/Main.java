import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

record Pos(int x, int y) {
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

record Plane(Pos p1, Pos p2) {

    boolean overlaps(Plane p) {
        return overlaps(p1.x(), p2.x(), p.p1.x(), p.p2.x()) &&
                overlaps(p1.y(), p2.y(), p.p1.y(), p.p2.y());
    }

    boolean contains(Pos p) {
        return between(p.x(), p1.x(), p2.x()) &&
                between(p.y(), p1.y(), p2.y());
    }

    boolean overlaps(int a1, int a2, int b1, int b2) {
        return between(a1, b1, b2) || between(a2, b1, b2) ||
                between(b1, a1, a2) || between(b2, a1, a2);
    }

    boolean between(int i, int a1, int a2) {
        return i >= a1 && i <= a2;
    }

    @Override
    public String toString() {
        return p1 + "," + p2;
    }
}

class Box {
    public final String name;
    public final Plane p;
    public int z1;
    public int z2;
    List<Box> laysOn = new ArrayList<>();
    List<Box> supports = new ArrayList<>();

    Box(String name, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        Pos p1 = new Pos(Math.min(x1, x2), Math.min(y1, y2));
        Pos p2 = new Pos(Math.max(x1, x2), Math.max(y1, y2));
        p = new Plane(p1, p2);
        this.z1 = Math.max(z1, z2);
        this.z2 = Math.min(z1, z2);
    }

    public void moveToLevel(int z) {
        int diff = z2 - z;
        z2 -= diff;
        z1 -= diff;
    }

    @Override
    public String toString() {
        return name + ":" + p + "," + z1 + "-" + z2;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            List<Box> boxes = new ArrayList<>();
            int i = 0;
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                String[] parts = line.split("~");
                String[] c1 = parts[0].split(",");
                String[] c2 = parts[1].split(",");
                boxes.add(new Box(Character.toString(65 + i), i(c1[0]), i(c1[1]), i(c1[2]), i(c2[0]), i(c2[1]), i(c2[2])));
                i += 1;
            }

            moveDown(boxes);

            int sum1 = solve1(boxes);
            System.out.println("Part 1: " + sum1);

            int sum2 = solve2(boxes);
            System.out.println("Part 2: " + sum2);
        }
    }

    int i(String s) {
        return Integer.parseInt(s);
    }

    private void moveDown(List<Box> boxes) {
        boxes.sort(Comparator.comparingInt(b -> b.z2));

        for (Box box : boxes) {
            List<Box> below = boxes.stream().filter(b -> b != box && b.z1 < box.z2).toList();
            List<Box> under = below.stream().filter(b -> b.p.overlaps(box.p)).toList();
            int max = under.stream().mapToInt(b -> b.z1).max().orElse(0);
            List<Box> supp = under.stream().filter(b -> b.z1 == max).toList();
            box.laysOn.addAll(supp);
            supp.forEach(s -> s.supports.add(box));
            box.moveToLevel(max + 1);
        }
    }

    int solve1(List<Box> boxes) {
        List<Box> toRemove = boxes.stream().filter(this::canBeRemoved).toList();
        return toRemove.size();
    }

    int solve2(List<Box> boxes) {
        List<Box> queue = new ArrayList<>();
        int sum = 0;

        for (Box box : boxes) {
            queue.add(box);
            Set<Box> removed = new HashSet<>();
            while (!queue.isEmpty()) {
                box = queue.remove(0);
                removed.add(box);
                Set<Box> willFall = whatWillFall(box, removed);
                sum += willFall.size();
                queue.addAll(willFall);
            }
        }

        return sum;
    }

    boolean canBeRemoved(Box box) {
        return whatWillFall(box).isEmpty();
    }

    Set<Box> whatWillFall(Box box) {
        return whatWillFall(box, Set.of());
    }
    
    Set<Box> whatWillFall(Box box, Set<Box> removed) {
        Set<Box> result = new HashSet<>();
        for (Box b : box.supports) {
            long i = b.laysOn.stream().filter(b2 -> b2 != box && !removed.contains(b2)).count();
            if (i == 0) {
                result.add(b);
            }
        }
        return result;
    }
}
