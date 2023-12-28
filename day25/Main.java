import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

record Cut(int edges, int a, int b) {
    @Override
    public String toString() {
        return edges + "/" + a + "," + b;
    }
}

@SuppressWarnings("UnstableApiUsage")
public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    final Random rnd = new Random();
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

    int solve1(ValueGraph<Node, Integer> graph) {
        Cut cut;
        int i = 0;
        do {
            cut = collapse(graph);
            System.out.print(cut + (++i % 15 == 0 ? "\n" : " "));
        } while (cut.edges() != 3);
        return cut.a() * cut.b();
    }

    Cut collapse(ValueGraph<Node, Integer> graph) {
        while (graph.nodes().size() > 2) {
            List<EndpointPair<Node>> edges = new ArrayList<>(graph.edges());
            EndpointPair<Node> edge = edges.get(rnd.nextInt(edges.size()));
            graph = collapse(graph, edge);
        }
        EndpointPair<Node> edge = graph.edges().iterator().next();
        return new Cut(graph.edgeValue(edge).orElse(0), edge.nodeU().value, edge.nodeV().value);
    }

    private ValueGraph<Node, Integer> collapse(ValueGraph<Node, Integer> graph, EndpointPair<Node> edge) {
        Node nodeU = edge.nodeU();
        Node nodeV = edge.nodeV();
        Iterable<Node> it = graph.nodes().stream().filter(n -> n != nodeU && n != nodeV).toList();
        MutableValueGraph<Node, Integer> g = Graphs.inducedSubgraph(graph, it);
        Set<Node> nodes = g.nodes();
        Node nn = new Node(nodeU.id + "," + nodeV.id, nodeU.value + nodeV.value);
        g.addNode(nn);
        for (EndpointPair<Node> pair : graph.incidentEdges(nodeU)) {
            Node other = pair.adjacentNode(nodeU);
            if (other == nodeV) continue;
            int v = g.edgeValue(nn, other).orElse(0);
            g.putEdgeValue(nn, other, v + graph.edgeValue(pair).orElse(0));
        }
        for (EndpointPair<Node> pair : graph.incidentEdges(nodeV)) {
            Node other = pair.adjacentNode(nodeV);
            if (other == nodeU) continue;
            int v = g.edgeValue(nn, other).orElse(0);
            g.putEdgeValue(nn, other, v + graph.edgeValue(pair).orElse(0));
        }
        return g;
    }
}
