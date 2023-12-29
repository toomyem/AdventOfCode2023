import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

class Node {
    String id;
    int value;

    Node(String id, int value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return id + "(" + value + ")";
    }
}

record Cut(Node s, Node t, int weight) {}

@SuppressWarnings("UnstableApiUsage")
public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            MutableValueGraph<Node, Integer> g = ValueGraphBuilder.undirected().build();
            Map<String, Node> nodes = new HashMap<>();

            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                String[] parts = line.split(":");
                String node = parts[0];
                String[] adj = parts[1].trim().split(" ");
                Node n1 = nodes.computeIfAbsent(node, k -> new Node(node, 1));
                for (String node2 : adj) {
                    Node n2 = nodes.computeIfAbsent(node2, k -> new Node(node2, 1));
                    g.putEdgeValue(n1, n2, 1);
                }
            }

            int sum1 = solve1(g);
            System.out.println("Part 1: " + sum1);
        }
    }

    int solve1(MutableValueGraph<Node, Integer> graph) {
        ValueGraph<Node, Integer> original = Graphs.copyOf(graph);
        Set<Node> partition = new HashSet<>();
        Set<Node> bestPartition = null;
        Cut bestCut = null;
        int size;
        while ((size = graph.nodes().size()) > 1) {
            Cut cut = computeCut(graph);
            partition.add(cut.t());
            if (bestCut == null || cut.weight() < bestCut.weight()) {
                bestCut = cut;
                bestPartition = new HashSet<>(partition);
                if (bestCut.weight() == 3) break;
            }
            mergeNodes(graph, cut);
            System.out.print(size + (size % 20 == 0 ? "\n" : " "));
        }
        System.out.println(bestCut);

        return buildMinCut(original, bestPartition);
    }

    Cut computeCut(ValueGraph<Node, Integer> graph) {
        Node start = graph.nodes().iterator().next();
        Set<Node> candidates = new HashSet<>(graph.nodes());
        candidates.remove(start);
        List<Node> list = new ArrayList<>(List.of(start));
        int weight = 0;

        while (!candidates.isEmpty()) {
            int maxWeight = 0;
            Node maxNode = null;
            for (Node next : candidates) {
                int sum = 0;
                for (Node node : list) {
                    int v = graph.edgeValue(next, node).orElse(0);
                    sum += v;
                }
                if (sum > maxWeight) {
                    maxWeight = sum;
                    maxNode = next;
                }
            }
            candidates.remove(maxNode);
            list.add(maxNode);
            weight = maxWeight;
        }

        Node s = list.get(list.size() - 2);
        Node t = list.get(list.size() - 1);
        return new Cut(s, t, weight);
    }

    void mergeNodes(MutableValueGraph<Node, Integer> graph, Cut cut) {
        Node s = cut.s();
        Node t = cut.t();
        for (EndpointPair<Node> pair : graph.incidentEdges(t)) {
            Node other = pair.adjacentNode(t);
            if (other == s) continue;
            int v = graph.edgeValue(pair).get();
            int vv = graph.edgeValue(s, other).orElse(0);
            graph.putEdgeValue(s, other, v + vv);
        }
        graph.removeNode(t);
    }

    int buildMinCut(ValueGraph<Node, Integer> graph, Set<Node> partition) {
        Set<Node> first = new HashSet<>();
        Set<Node> second = new HashSet<>();

        for (Node node : graph.nodes()) {
            if (partition.contains(node)) {
                first.add(node);
            } else {
                second.add(node);
            }
        }
        return first.size() * second.size();
    }
}
