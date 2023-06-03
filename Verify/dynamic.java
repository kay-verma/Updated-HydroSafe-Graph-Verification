import java.util.*;

class Graph {
    int n;
    List<Integer>[] adj;
    List<Integer>[] revadj;

    boolean[] cloud;
    boolean[] sea;
    char[] status;

    Graph(int n) {
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

    void addedge(int u, int v) {
        adj[u].add(v);
        revadj[v].add(u);
    }
}

public class Hydrostructure {
    static void compute_sea(Graph g, int s1, int d2, int d1) {
        // all nodes reachable from start(W) without using W as subwalk
        boolean[] vis = new boolean[g.n + 1];

        int par = -1;
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{s1, par});
        vis[s1] = true;

        while (!q.isEmpty()) {
            int[] node = q.poll();
            int currNode = node[0];
            par = node[1];

            if (currNode == d2 && par == d1)
                continue;

            g.sea[currNode] = true;
            for (int it : g.adj[currNode]) {
                if (it == d2 && currNode == d1)
                    continue;
                if (!vis[it]) {
                    q.add(new int[]{it, currNode});
                    vis[it] = true;
                }
            }
        }
    }

    static void compute_cloud(Graph g, int d1, int s1, int s2) {
        // all nodes reaching END(W) without using W as subwalk
        boolean[] vis = new boolean[g.n + 1];

        int par = -1;
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{d1, par});
        vis[d1] = true;

        while (!q.isEmpty()) {
            int[] node = q.poll();
            int currNode = node[0];
            par = node[1];

            if (currNode == s1 && par == s2)
                continue;

            g.cloud[currNode] = true;
            for (int it : g.revadj[currNode]) {
                if (it == s1 && currNode == s2)
                    continue;
                if (!vis[it]) {
                    q.add(new int[]{it, currNode});
                    vis[it] = true;
                }
            }
        }
    }

    static void compute_hydrostructure(Graph g, int s1, int s2, int d1, int d2) {
        compute_sea(g, s1, d2, d1); // SEA- [R+(W)]- all nodes reachable from start(W) without using W as its subwalk
        compute_cloud(g, d1, s1, s2); // CLOUD- [R-(W)]- all nodes reaching END(W) without using W as its subwalk

        for (int i = 1; i <= g.n; i++) {
            // CLOUD- [R-(W)]
            if (g.cloud[i] && g.sea[i])
                g.status[i] = 'v';

            // SEA- [R+(W)]
            else if (g.cloud[i])
                g.status[i] = 'c';

            // VAPOR- [R+ ⋂ R-]
            else if (g.sea[i])
                g.status[i] = 's';

            // RIVER- G-[R+(W) ⋃ R-(W)]
            else
                g.status[i] = 'r';
        }
    }

    static boolean check1(Graph g, int node) {
        // check if river node can be part of cloud or not
        for (int it : g.revadj[node]) {
            if (g.status[it] == 'r')
                return false;
        }
        return true;
    }

    static void update1(Graph g, int node) {
        // river -> cloud
        g.cloud[node] = true;
        g.status[node] = 'c';

        for (int it : g.adj[node]) {
            if (g.status[it] == 'r') {
                if (check1(g, it)) {
                    update1(g, it);
                }
            }
        }
    }

    static boolean check2(Graph g, int node) {
        // check if river node can be part of sea or not
        for (int it : g.adj[node]) {
            if (g.status[it] == 'r' || g.status[it] == 'c')
                return false;
        }
        return true;
    }

    static void update2(Graph g, int node) {
        // sea -> river
        if (check2(g, node)) {
            g.sea[node] = true;
            g.status[node] = 's';

            for (int it : g.revadj[node]) {
                if (g.status[it] == 'r') {
                    if (check2(g, it)) {
                        update2(g, it);
                    }
                }
            }
        }
    }

    static int verify(Graph g, int u, int v) {
        // vapor -> vapor
        if (g.status[u] == 'v' && g.status[v] == 'v')
            return 0;

        if (g.status[u] == 'v' && g.status[v] == 'r') { // vapor -> river
            if (check2(g, v)) {
                update2(g, v);
                return 1;
            }
            return 0;
        }

        if (g.status[u] == 'r' && g.status[v] == 'v') { // river -> vapor
            if (check1(g, u)) {
                update1(g, u);
                return 1;
            }
            return 0;
        }

        if (g.status[u] == 'v' && g.status[v] == 'c' || // vapor -> cloud
                g.status[u] == 's' && g.status[v] == 'v' || // sea -> vapor
                g.status[u] == 's' && g.status[v] == 'c') // sea -> cloud
            return 0; // UNSAFE

        if (g.status[u] == 'v' && g.status[v] == 's' || // vapor -> sea
                g.status[u] == 'c' && g.status[v] == 'v' || // cloud -> vapor
                g.status[u] == 'c' && g.status[v] == 'c' || // cloud -> cloud
                g.status[u] == 's' && g.status[v] == 's' || // sea -> sea
                g.status[u] == 'r' && g.status[v] == 'r' || // river -> river
                g.status[u] == 'r' && g.status[v] == 's' || // river -> sea
                g.status[u] == 'c' && g.status[v] == 's' || // cloud -> sea
                g.status[u] == 'c' && g.status[v] == 'r') // cloud -> river
            return 1;

        if (g.status[u] == 's' && g.status[v] == 'r') { // sea -> river
            if (check2(g, v)) {
                update2(g, v);
                return 1;
            }
            return 0;
        }

        if (g.status[u] == 'r' && g.status[v] == 'c') { // river -> cloud
            if (check1(g, u)) {
                update1(g, u);
                return 1;
            }
            return 0;
        }

        return -1;
    }

    public static void main(String[] args) {
        // auto start = std::chrono::high_resolution_clock::now();

        // freopen("testcase.txt", "r", stdin);
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Graph g = new Graph(n);

        while (m-- > 0) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            g.addedge(u, v);
        }

        int s1 = scanner.nextInt();
        int s2 = scanner.nextInt();
        int d1 = scanner.nextInt();
        int d2 = scanner.nextInt();

        long start = System.nanoTime();

        compute_hydrostructure(g, s1, s2, d1, d2);

        boolean key;
        key = scanner.nextBoolean();
        while (key) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();

            int check = verify(g, u, v);

            switch (check) {
                case 1:
                    System.out.println("\nWalk 'W' is still safe");
                    g.addedge(u, v);
                    break;
                case 0:
                    System.out.println("\nWalk 'W' is unsafe now");
                    break;
                default:
                    System.out.println("\nERROR");
                    break;
            }

            if (check != 0) {
                key = scanner.nextBoolean();
            } else {
                key = false;
            }

            long stop = System.nanoTime();
            long duration = (stop - start) / 1000000;
            System.out.println(duration + " milliseconds");
        }
    }
}
