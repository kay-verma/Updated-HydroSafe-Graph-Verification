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

    public void computeHydrostructure(int start, int end) {
        // all nodes reachable from start(W) without using W as its subwalk
        boolean[] visited = new boolean[n + 1];

        int par = -1;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            if (node == end)
                continue;

            cloud[node] = true;
            for (int neighbor : adj[node]) {
                if (!visited[neighbor]) {
                    queue.add(neighbor);
                    visited[neighbor] = true;
                }
            }
        }

        // all nodes reaching END(W) without using W as its subwalk
        visited = new boolean[n + 1];

        queue = new LinkedList<>();
        queue.add(end);
        visited[end] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();

            if (node == start)
                continue;

            sea[node] = true;
            for (int neighbor : revadj[node]) {
                if (!visited[neighbor]) {
                    queue.add(neighbor);
                    visited[neighbor] = true;
                }
            }
        }

        for (int i = 1; i <= n; i++) {
            if (cloud[i] && sea[i])
                status[i] = 'v';
            else if (cloud[i])
                status[i] = 'c';
            else if (sea[i])
                status[i] = 's';
            else
                status[i] = 'r';
        }
    }

    public boolean isSafe(Walk walk) {
        for (int node : walk.nodes()) {
            if (status[node] != 'c' && status[node] != 'v') {
                return false;
            }
        }

        return true;
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

        // start and end represent head and tail nodes of the walk
        int start = scanner.nextInt();
        int end = scanner.nextInt();

        g.computeHydrostructure(start, end);

        System.out.println("Is the walk `" + walk + "` safe? " + g.isSafe(walk));
    }
}
