import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

enum Type {
    FIVE, FOUR, FULL, THREE, TWO, ONE, HIGH
}

class Card {
    public static final String ORDERING = "AKQJT98765432";
    public final String label;
    public final int bid;
    protected Type type;

    Card(String label, int bid) {
        this.label = label;
        this.bid = bid;
        this.type = null;
    }

    Type getType() {
        if (type == null) {
            type = calcType(label);
        }
        return type;
    }

    protected Type calcType(String label) {
        String[] cards = label.split("");
        Map<Character, Integer> map = new HashMap<>();
        for (String c : cards) {
            char ch = c.charAt(0);
            map.put(ch, map.getOrDefault(ch, 0) + 1);
        }
        int[] values = map.values().stream().sorted((o1, o2) -> o2 - o1).mapToInt(i -> i).toArray();

        if (values[0] == 5) return Type.FIVE;
        if (values[0] == 4) return Type.FOUR;
        if (values[0] == 3 && values[1] == 2) return Type.FULL;
        if (values[0] == 3) return Type.THREE;
        if (values[0] == 2 && values[1] == 2) return Type.TWO;
        if (values[0] == 2) return Type.ONE;
        return Type.HIGH;
    }

    int getRank(int i) {
        return ORDERING.indexOf(label.charAt(i));
    }
}

class Card2 extends Card {

    Card2(String label, int bid) {
        super(label, bid);
    }

    @Override
    Type getType() {
        if (type != null) return type;
        if (!label.contains("J")) return super.getType();

        type = Type.HIGH;
        for (String r : ORDERING.split("")) {
            Type t = calcType(label.replaceAll("J", r));
            if (t.ordinal() < type.ordinal()) {
                type = t;
            }
        }
        return type;
    }

    @Override
    int getRank(int i) {
        if (label.charAt(i) == 'J') return 100;
        return super.getRank(i);
    }
}

class CardSorter implements Comparator<Card> {

    @Override
    public int compare(Card c1, Card c2) {
        int d = c2.getType().ordinal() - c1.getType().ordinal();
        if (d != 0) return d;

        for (int i = 0; i < c1.label.length(); i++) {
            d = c2.getRank(i) - c1.getRank(i);
            if (d != 0) return d;
        }
        return 0;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        try (Scanner in = new Scanner(new File("input.txt"))) {
            List<Card> cards = new ArrayList<>();
            List<Card2> cards2 = new ArrayList<>();

            while(in.hasNext()) {
                String line = in.nextLine();
                String label = line.split(" ")[0];
                int bid = Integer.parseInt(line.split(" ")[1]);
                cards.add(new Card(label, bid));
                cards2.add(new Card2(label, bid));
            }

            cards.sort(new CardSorter());
            int sum1 = getSum(cards);
            System.out.println("Part 1: "+sum1);

            cards2.sort(new CardSorter());
            int sum2 = getSum(cards2);
            System.out.println("Part 2: "+sum2);
        }
    }

    static <C extends Card> int getSum(List<C> cards) {
        int sum = 0;
        int rank = 1;
        for (Card c : cards) {
            sum += c.bid * rank;
            rank += 1;
        }
        return sum;
    }
}
