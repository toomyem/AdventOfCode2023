import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toSet;

record Star(long row, long col) {}

public class Main {
  public static void main(String[] args) throws Exception {
    new Main().run();
  }

  void run() throws Exception {
    try (Scanner in = new Scanner(new File("input.txt"))) {

      Set<Star> universe = new HashSet<>();

      int rows = 0, cols = 0;
      while (in.hasNext()) {
        String line = in.nextLine();
        for (int col = 0; col < line.length(); col++) {
          if (line.charAt(col) == '#') {
            universe.add(new Star(rows, col));
          }
        }
        rows += 1;
        if (cols == 0) cols = line.length();
      }

      Set<Star> universe1 = expand(rows, cols, universe, 2);
      long sum1 = minDist(universe1);
      System.out.println("Part 1: " + sum1);

      Set<Star> universe2 = expand(rows, cols, universe, 1000000);
      long sum2 = minDist(universe2);
      System.out.println("Part 2: " + sum2);
    }
  }

  Set<Star> expand(long rows, long cols, Set<Star> universe, long speed) {
    long[] emptyCols = LongStream.range(0, cols).filter(c -> universe.stream().noneMatch(s -> s.col() == c)).toArray();
    long[] emptyRows = LongStream.range(0, rows).filter(r -> universe.stream().noneMatch(s -> s.row() == r)).toArray();

    return universe.stream().map(s -> {
      int i = emptyCols.length;
      while (i > 0 && emptyCols[i - 1] > s.col()) {
        i -= 1;
      }

      int j = emptyRows.length;
      while (j > 0 && emptyRows[j - 1] > s.row()) {
        j -= 1;
      }

      return new Star(s.row() + j * (speed - 1), s.col() + i * (speed - 1));
    }).collect(toSet());
  }

  long minDist(Set<Star> universe) {
    long sum = 0;
    for (Star s1 : universe) {
      for (Star s2 : universe) {
        if (s1 == s2) continue;
        sum += Math.abs(s1.row() - s2.row()) + Math.abs(s1.col() - s2.col());
      }
    }
    return sum / 2;
  }
}
