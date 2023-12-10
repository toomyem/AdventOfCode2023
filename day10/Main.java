import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

enum Move {
  AHEAD, RIGHT, LEFT
}

enum Direction {
  NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0);

  final int dx;
  final int dy;

  Direction(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  Direction turn(Move move) {
    return switch (move) {
      case AHEAD -> this;
      case LEFT -> left();
      case RIGHT -> right();
    };
  }

  Direction right() {
    return values()[(ordinal() + 1) % 4];
  }

  Direction left() {
    return values()[(ordinal() + 3) % 4];
  }

  Direction revert() {
    return values()[(ordinal() + 2) % 4];
  }
}

record Pos(int row, int col) {
  Pos go(Direction dir) {
    return new Pos(row + dir.dy, col + dir.dx);
  }
}

record PosDir(Pos pos, Direction dir) {
  PosDir go(Move move) {
    Direction newDir = dir;
    if (move == Move.LEFT) {
      newDir = dir.left();
    } else if (move == Move.RIGHT) {
      newDir = dir.right();
    }
    return new PosDir(pos.go(newDir), newDir);
  }

  PosDir turn(Move move) {
    return new PosDir(pos, dir.turn(move));
  }

  boolean equals(Pos pos) {
    return this.pos.equals(pos);
  }
}

record Cell(Set<Direction> exits, boolean visited, boolean inner) {
  Cell(Direction... dirs) {
    this(Set.of(dirs), false, false);
  }

  Cell setVisited() {
    return new Cell(exits, true, inner);
  }

  Cell setInner() {
    return new Cell(exits, visited, true);
  }
}

public class Main {
  public static void main(String[] args) throws Exception {
    new Main().run();
  }

  static final Cell EMPTY = new Cell();

  void run() throws Exception {
    try (Scanner in = new Scanner(new File("input.txt"))) {

      Map<Pos, Cell> map = new HashMap<>();
      Pos start = null;
      int row = 0;
      while (in.hasNext()) {
        String line = in.nextLine();
        for (int col = 0; col < line.length(); col++) {
          char ch = line.charAt(col);
          Pos pos = new Pos(row, col);
          if (ch == 'S') start = pos;
          Cell cell = switch (ch) {
            case 'S' -> new Cell(Direction.values());
            case '|' -> new Cell(Direction.NORTH, Direction.SOUTH);
            case '-' -> new Cell(Direction.WEST, Direction.EAST);
            case 'L' -> new Cell(Direction.NORTH, Direction.EAST);
            case 'J' -> new Cell(Direction.WEST, Direction.NORTH);
            case 'F' -> new Cell(Direction.EAST, Direction.SOUTH);
            case '7' -> new Cell(Direction.WEST, Direction.SOUTH);
            default -> null;
          };
          if (cell != null) map.put(pos, cell);
        }
        row += 1;
      }

      int steps = 0;
      int turns = 0;
      int inner = 0;
      Move side = null;
      PosDir posDir = new PosDir(start, Direction.NORTH);

      for (int round = 1; round <= 2; round++) {
        while (steps == 0 || !posDir.equals(start)) {
          Cell cell = map.get(posDir.pos());
          Move move = findNext(map, posDir);
          map.put(posDir.pos(), cell.setVisited());
          if (side != null && (move == Move.LEFT || move == Move.RIGHT)) {
            inner += fill(map, posDir.turn(move).go(side).pos());
          }
          posDir = posDir.go(move);
          if (side != null) {
            inner += fill(map, posDir.go(side).pos());
          }
          turns += move == Move.LEFT ? -1 : move == Move.RIGHT ? 1 : 0;
          steps += 1;
        }

        if (round == 1) {
          System.out.println("Part 1: " + steps / 2);
          side = turns > 0 ? Move.RIGHT : Move.LEFT;
        }
        if (round == 2) {
          System.out.println("Part 2: " + inner);
        }
        steps = 0;
      }
    }
  }

  Move findNext(Map<Pos, Cell> map, PosDir posDir) {
    for (Move move : Move.values()) {
      PosDir newPos = posDir.go(move);
      Cell from = map.getOrDefault(posDir.pos(), EMPTY);
      Cell to = map.getOrDefault(newPos.pos(), EMPTY);
      Direction dir = posDir.dir().turn(move);
      if (from.exits().contains(dir) && to.exits().contains(dir.revert())) {
        return move;
      }
    }
    return null;
  }

  int fill(Map<Pos, Cell> map, Pos pos) {
    Cell cell = map.getOrDefault(pos, EMPTY);
    if (!cell.visited() && !cell.inner()) {
      int sum = 1;
      map.put(pos, cell.setInner());
      for (Direction dir : Direction.values()) {
        sum += fill(map, pos.go(dir));
      }
      return sum;
    }
    return 0;
  }
}
