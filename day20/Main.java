import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

enum State { H, L }
enum Type { FLIP_FLOP, CONJ, BROADCAST, OUTPUT }
record Signal(String from, String to, State state) {}

class Module {
    final String name;
    Type type = Type.OUTPUT;
    final Map<String, State> inputs = new HashMap<>();
    final Set<String> outputs = new HashSet<>();
    boolean state = false;

    Module(String name) {
        this.name = name;
    }

    Module setType(Type type) {
        this.type = type;
        return this;
    }

    void addOutputs(String[] outputs) {
        this.outputs.addAll(Arrays.asList(outputs));
    }

    void addInput(String in) {
        inputs.put(in, State.L);
    }

    void send(State state, List<Signal> queue) {
        for (String out : outputs) {
            Signal signal = new Signal(name, out, state);
            queue.add(signal);
        }
    }

    void process(Signal signal, List<Signal> queue) {
        inputs.put(signal.from(), signal.state());
        switch (type) {
            case FLIP_FLOP:
                if (signal.state() == State.L) {
                    state = !state;
                    send(state ? State.H : State.L, queue);
                }
                break;
            case CONJ:
                if (inputs.values().stream().allMatch(s -> s == State.H)) {
                    send(State.L, queue);
                    state = false;
                } else {
                    send(State.H, queue);
                    state = true;
                }
                break;
            case BROADCAST:
                send(signal.state(), queue);
                break;
            case OUTPUT:
                state = signal.state() == State.H;
                break;
        }
    }

    @Override
    public String toString() {
        return name + ":" + type.name().charAt(0) + inputs + outputs + (state ? "t" : "f") + " ";
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : "input.txt";
        new Main().run(file);
    }

    private void run(String file) throws Exception {
        try (Scanner in = new Scanner(new File(file))) {

            List<Signal> queue = new ArrayList<>();
            Map<String, Module> circuit = new HashMap<>();

            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.isBlank()) break;
                String[] parts = line.split(" -> ");
                String name = parts[0];
                String[] outputs = parts[1].split(", ");
                String name2;
                if (name.startsWith("%")) {
                    name2 = name.substring(1);
                    circuit.computeIfAbsent(name2, k -> new Module(name2)).setType(Type.FLIP_FLOP).addOutputs(outputs);
                } else if (name.startsWith("&")) {
                    name2 = name.substring(1);
                    circuit.computeIfAbsent(name2, k -> new Module(name2)).setType(Type.CONJ).addOutputs(outputs);
                } else {
                    name2 = name;
                    circuit.computeIfAbsent(name2, k -> new Module(name2)).setType(Type.BROADCAST).addOutputs(outputs);
                }

                for (String out : outputs) {
                    circuit.computeIfAbsent(out, k -> new Module(out)).addInput(name2);
                }
            }

            saveGraph(circuit);

            int sum1 = solve1(circuit, queue);
            System.out.println("Part 1: " + sum1);

            long sum2 = solve2(circuit, queue);
            System.out.println("Part 2: " + sum2);
        }
    }

    int solve1(Map<String, Module> circuit, List<Signal> queue) {
        int hi = 0;
        int low = 0;

        for (int i = 0; i < 1000; i++) {
            queue.add(new Signal("button", "broadcaster", State.L));
            while (!queue.isEmpty()) {
                Signal s = queue.remove(0);
                if (s.state() == State.H) {
                    hi += 1;
                } else {
                    low += 1;
                }
                Module m = circuit.get(s.to());
                m.process(s, queue);
            }
        }
        return hi * low;
    }

    long solve2(Map<String, Module> circuit, List<Signal> queue) {
        Module rx = circuit.get("rx");
        String in = rx.inputs.keySet().iterator().next();
        Module input = circuit.get(in);
        Map<Module, AtomicLong> obs = new HashMap<>();
        Map<Module, AtomicBoolean> prev = new HashMap<>();
        input.inputs.keySet().forEach(m -> obs.put(circuit.get(m), new AtomicLong(0)));
        List<Long> periods = new ArrayList<>();

        int presses = 0;
        while (periods.size() != obs.size() && presses < 100000) {
            queue.add(new Signal("button", "broadcaster", State.L));
            while (!queue.isEmpty()) {
                Signal s = queue.remove(0);
                Module m = circuit.get(s.to());
                m.process(s, queue);

                int i = presses;
                obs.forEach((m2, counter) -> {
                    if (prev.computeIfAbsent(m2, k -> new AtomicBoolean(false)).getAndSet(m2.state) != m2.state && m2.state) {
                        if (counter.get() == 0) {
                            counter.set(i);
                        } else {
                            periods.add(i - counter.get());
                        }
                    }
                });
            }

            presses += 1;
        }

        return lcm(periods);
    }

    long gcd(long n1, long n2) {
        if (n2 == 0) {
            return n1;
        }
        return gcd(n2, n1 % n2);
    }

    long lcm(long n1, long n2) {
        return (n1 / gcd(n1, n2)) * n2;
    }

    long lcm(List<Long> values) {
        return values.stream().mapToLong(i -> i).reduce(1L, this::lcm);
    }

    void saveGraph(Map<String, Module> circuit) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("graph_temp.dot"));
            writer.write("digraph {\n");
            for (Module m : circuit.values()) {
                writer.write(m.name + "[label=\"" + m.name + " " + m.type.name().charAt(0) + "\"];\n");
                for (String out : m.outputs) {
                    writer.write(m.name + " -> " + out + ";\n");
                }
            }
            writer.write("}\n");
            writer.close();
            Files.move(Path.of("graph_temp.dot"), Path.of("graph.dot"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println("Cannot write graph: " + ex);
        }
    }
}
