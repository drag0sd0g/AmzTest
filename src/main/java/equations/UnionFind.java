package equations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
  Created by drag0sd0g on 29/01/2017.

  If you were given a series of equations e.g. [A = B, B = D, C = D, F = G, E = H, H = C]
 and then another series [A != C, D != H, ..., F != A ]

 Check whether the equations combined is valid.

 For the example given, your program should return 'invalid', because the first series implies that A = C,
 which contradicts the statement A != C in the second series.

 HINT: Union Find (https://en.wikipedia.org/wiki/Disjoint-set_data_structure)
 */
public class UnionFind {
    public static void main(String[] args) {
        String equalsRelationshipFile = args[0];
        String unequalsRelationshipFile = args[1];

        final Map<String, Node> input = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(equalsRelationshipFile))) {
            stream.forEach(line -> {
                String[] tokens = line.split("=");
                Node x = null;
                Node y = null;
                if (input.get(tokens[0]) == null) {
                    x = makeSet(tokens[0]);
                } else {
                    x = input.get(tokens[0]);
                }

                if (input.get(tokens[1]) == null) {
                    y = makeSet(tokens[1]);
                } else {
                    y = input.get(tokens[1]);
                }

                union(x, y);

                input.put(x.getValue(), x);
                input.put(y.getValue(), y);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        final AtomicBoolean validity = new AtomicBoolean(true);
        try (Stream<String> stream = Files.lines(Paths.get(unequalsRelationshipFile))) {
            stream.forEach(line -> {
                String[] tokens = line.split("!=");
                Node x = input.get(tokens[0]);
                Node y = input.get(tokens[1]);

                Node xRep = getRepresentative(x);
                Node yRep = getRepresentative(y);

                if (xRep.equals(yRep)) {
                    validity.set(false);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (validity.get()) {
            System.out.println("valid");
        } else {
            System.out.println("invalid");
        }
    }

    private static Node makeSet(String x) {
        Node node = new Node();
        node.setValue(x);
        node.setParent(node);
        node.setRank(0);
        return node;
    }

    private static Node getRepresentative(Node x) {
        if (x.getParent().equals(x)) {
            return x;
        } else {
            return getRepresentative(x.getParent());
        }
    }

    private static void union(Node x, Node y) {
        Node xRep = getRepresentative(x);
        Node yRep = getRepresentative(y);
        if (xRep.equals(yRep)) {
            return; //same set, i.e. same rep
        }

        if (xRep.getRank() < yRep.getRank()) {
            System.out.println(String.format("%s rank %d < %s rank %d. Setting %s as %s's parent rep",
                    xRep.getValue(), xRep.getRank(), yRep.getValue(), yRep.getRank(), yRep.getValue(), xRep.getValue()));
            xRep.setParent(yRep);
        } else if (xRep.getRank() > yRep.getRank()) {
            System.out.println(String.format("%s rank %d > %s rank %d. Setting %s as %s's parent rep",
                    xRep.getValue(), xRep.getRank(), yRep.getValue(), yRep.getRank(), xRep.getValue(), yRep.getValue()));
            yRep.setParent(xRep);
        } else {
            System.out.println(String.format("%s rank %d = %s rank %d. Setting %s as %s's parent and increasing %s's rank to %d",
                    xRep.getValue(), xRep.getRank(), yRep.getValue(), yRep.getRank(), xRep.getValue(), yRep.getValue(),
                    xRep.getValue(), xRep.getRank() + 1));
            yRep.setParent(xRep);
            xRep.setRank(1 + xRep.getRank());
        }
    }

    private static class Node {
        private String value;
        private Node parent;
        private int rank;

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            if (rank != node.rank) return false;
            if (value != null ? !value.equals(node.value) : node.value != null) return false;
            return parent != null ? parent.equals(node.parent) : node.parent == null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value='" + value + '\'' +
                    ", parent=" + parent.getValue() +
                    ", rank=" + rank +
                    '}';
        }
    }
}
