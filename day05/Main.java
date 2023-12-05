import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

record Mapping(long start, long end, long dest) {}

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new File("input.txt"));
        Map<String, List<Mapping>> almanac = new HashMap<>();
        long[] seeds = null;
        String map = null;

        while (in.hasNext()) {
            String line = in.nextLine();
            if (line.startsWith("seeds:")) {
                seeds = Arrays.stream(line.substring(7)
                        .split(" "))
                        .mapToLong(Long::parseLong)
                        .toArray();
            }
            if (line.contains("map:")) {
                map = line.substring(0, line.indexOf(" "));
                continue;
            }
            if (map != null && !line.isBlank()) {
                long dest = Long.parseLong(line.split(" ")[0]);
                long start = Long.parseLong(line.split(" ")[1]);
                long end = start + Long.parseLong(line.split(" ")[2]);
                almanac.computeIfAbsent(map, k -> new ArrayList<>()).add(new Mapping(start, end, dest));
            }
        }

        long minLoc = Long.MAX_VALUE;
        for (long seed : seeds) {
            long loc = transform(almanac, seed);
            if (loc < minLoc) {
                 minLoc = loc;
            }
        }
        System.out.println("Part 1: " + minLoc);

        minLoc = Long.MAX_VALUE;
        for (int i = 0; i < seeds.length; i += 2) {
            long start = seeds[i];
            long end = start + seeds[i + 1];
            System.out.printf("%d, %d, %d\n", i / 2 + 1, start, end);
            for (long seed = start; seed < end; seed++) {
                long loc = transform(almanac, seed);
                if (loc < minLoc) {
                    minLoc = loc;
                }
            }
        }
        System.out.println("Part 2: " + minLoc);
    }

    private static long map(List<Mapping> mapping, long value) {
        long result = value;
        for (Mapping m : mapping) {
            if (value >= m.start() && value < m.end()) {
                result = m.dest() + value - m.start();
                break;
            }
        }
        return result;
    }

    private static long transform(Map<String, List<Mapping>> almanac, long seed) {
        long soil = map(almanac.get("seed-to-soil"), seed);
        long fert = map(almanac.get("soil-to-fertilizer"), soil);
        long water = map(almanac.get("fertilizer-to-water"), fert);
        long light = map(almanac.get("water-to-light"), water);
        long temp = map(almanac.get("light-to-temperature"), light);
        long hum = map(almanac.get("temperature-to-humidity"), temp);
        long loc = map(almanac.get("humidity-to-location"), hum);
        return loc;
    }
}
