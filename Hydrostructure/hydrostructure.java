import java.util.*;

class Graph {
    int n; // number of nodes in Graph
    List<Integer>[] adj;
    List<Integer>[] revadj;

    boolean[] cloud;
    boolean[] sea;
    char[] status; // r-river/ s-sea/ c-cloud/ v-vapor

    public Graph(int n) {
        this.n = n;
        adj = new ArrayList[n + 1];
        revadj = new ArrayList[n + 1];

        cloud = new boolean[n + 1];
        sea = new boolean[n + 1];

        status = new char[n + 1];
        Arrays.fill(status, 'r');

        for (int i = 1; i <= n; i++) {
            adj[i] = new ArrayList<>();
            revadj[i] = new ArrayList<>();
        }
    }

    public void addEdge(int u, int v) {
        adj[u].add(v);
        revadj[v].add(u);
    }
}

class Main {
    public static void printCloud(Graph g) {
        System.out.print("\nCloud-");
        for (int i = 1; i <= g.cloud.length; i++) {
            if (g.cloud[i])
                System.out.print(i + " ");
        }
    }

    public static void printSea(Graph g) {
        System.out.print("\nSea-");
        for (int i = 1; i <= g.sea.length; i++) {
            if (g.sea[i])
                System.out.print(i + " ");
        }
    }

    public static void printVapor(Graph g) {
        System.out.print("\nVapor-");
        for (int i = 1; i < g.status.length; i++) {
            if (g.status[i] == 'v')
                System.out.print(i + " ");
        }
    }

    public static void printRiver(Graph g) {
        System.out.print("\nRiver-");
        for (int i = 1; i < g.status.length; i++) {
            if (g.status[i] == 'r')
                System.out.print(i + " ");
        }
    }

    public static void computeSea(Graph g, int s1, int d2, int d1) {
        // all nodes reachable from start(W) without using W as subwalk
        boolean[] visited = new boolean[g.n + 1];

        int par = -1;
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(s1, par));
        visited[s1] = true;

        while (!queue.isEmpty()) {
            int node = queue.peek().getKey();
            par = queue.peek().getValue();
            queue.poll();

            if (node == d2 && par == d1)
                continue;

            g.sea[node] = true;
            for (int neighbor : g.adj[node]) {
                if (neighbor == d2 && node == d1)
                    continue;
                if (!visited[neighbor]) {
                    queue.add(new Pair<>(neighbor, node));
                    visited[neighbor] = true;
                }
            }
        }
        printSea(g);
    }

    public static void computeCloud(Graph g, int d1, int s1, int s2) {
        // all nodes reaching END(W) without using W as subwalk
        boolean[] visited = new boolean[g.n + 1];

        int par = -1;
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(d1, par));
        visited[d1] = true;

        while (!queue.isEmpty()) {
            int node = queue.peek().getKey();
            par = queue.peek().getValue();
            queue.poll();

            if (node == s1 && par == s2)
                continue;

            g.cloud[node] = true;
            for (int neighbor : g.revadj[node]) {
                if (neighbor == s1 && node == s2)
                    continue;
                if (!visited[neighbor]) {
                    queue.add(new Pair<>(neighbor, node));
                    visited[neighbor] = true;
                }
            }
        }
        printCloud(g);
    }

    public static void computeHydrostructure(Graph g, int s1, int s2, int d1, int d2) {
        computeSea(g, s1, d2, d1);
        // SEA- [R+(W)]- all nodes reachable from start(W) without using W as its subwalk
        computeCloud(g, d1, s1, s2);
        // CLOUD- [R-(W)]- all nodes reaching END(W) without using W as its subwalk

        for (int i = 1; i <= g.n; i++) {
            // VAPOR- [R+ ⋂ R-]
            if (g.cloud[i] && g.sea[i])
                g.status[i] = 'v';

            // CLOUD- [R-(W)]
            else if (g.cloud[i])
                g.status[i] = 'c';

            // SEA- [R+(W)]
            else if (g.sea[i])
                g.status[i] = 's';

            // RIVER- G-[R+(W) ⋃ R-(W)]
            else
                g.status[i] = 'r';
        }
        printVapor(g);
        printRiver(g);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Graph g = new Graph(n);

        while (m-- > 0) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            g.addEdge(u, v);
        }

        // s1 and s2 represent head and tail nodes of start(W) edge of the walk
        // d1 and d2 represent head and tail nodes of end(W) edge of the walk
        int s1 = scanner.nextInt();
        int s2 = scanner.nextInt();
        int d1 = scanner.nextInt();
        int d2 = scanner.nextInt();

        computeHydrostructure(g, s1, s2, d1, d2);
    }
}
