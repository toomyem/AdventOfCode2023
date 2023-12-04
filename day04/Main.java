import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class Card {
    final Set<Integer> haves;
    final Set<Integer> wins;
    int copies = 0;

    Card(Set<Integer> haves, Set<Integer> wins) {
        this.haves = haves;
        this.wins = wins;
        this.copies = 1;
    }

    int getMatching() {
        int matching = 0;
        for (int i : wins) {
            if (haves.contains(i)) {
                matching += 1;
            }
        }
        return matching;
    }

    @Override
    public String toString() {
        return String.format("%d x %s | %s",
                copies,
                haves.stream().sorted().toList(),
                wins.stream().sorted().toList()
        );
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        List<Card> cards = new ArrayList<>();
        while (in.hasNext()) {
            String line = in.nextLine();
            line = line.substring(line.indexOf(':') + 2);
            Set<Integer> haves = parseNums(line.split("\\|")[0]);
            Set<Integer> wins = parseNums(line.split("\\|")[1]);
            cards.add(new Card(haves, wins));
        }
        int sum1 = 0;
        int sum2 = 0;
        int idx = 0;
        int c = 0;

        while (idx < cards.size() && c < cards.get(idx).copies) {
            Card card = cards.get(idx);
            int matching = card.getMatching();
            int score = 1 << (matching - 1);
            for (int n = 1; n <= matching; n++) {
                cards.get(idx + n).copies += 1;
            }
            if (c == 0) {
                sum1 += score;
            }
            c += 1;
            if (c == card.copies) {
                sum2 += card.copies;
                System.out.println(idx + ": " + card);
            }
            if (c >= card.copies) {
                idx += 1;
                c = 0;
            }
        }

        System.out.println("Part 1: " + sum1);
        System.out.println("Part 2: " + sum2);
    }

    private static Set<Integer> parseNums(String line) {
        Set<Integer> result = new HashSet<>();
        for (String v : line.trim().split(" +")) {
            result.add(Integer.parseInt(v));
        };
        return result;
    }
}
