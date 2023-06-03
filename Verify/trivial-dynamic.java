import java.util.*;

class Graph {
    public int n;
    public List<Integer>[] adj;
    public List<Integer>[] revadj;

    public List<Boolean> cloud;
    public List<Boolean> sea;
    public List<Character> status;

    public Graph(int n) {
        this.n = n;
        adj = new ArrayList[n + 1];
        revadj = new ArrayList[n + 1];

        for (int i = 1; i <= n; i++) {
            adj[i] = new ArrayList<>();
            revadj[i] = new ArrayList<>();
        }

        cloud = new ArrayList<>(Collections.nCopies(n + 1, false));
        sea = new ArrayList<>(Collections.nCopies(n + 1, false));
        status = new ArrayList<>(Collections.nCopies(n + 1, 'r'));
    }

    public void addedge(int u, int v) {
        adj[u].add(v);
        revadj[v].add(u);
    }
}

public class Main {
    public static void compute_sea(Graph g, int s1, int d2, int d1) {
        List<Boolean> vis = new ArrayList<>(Collections.nCopies(g.n + 1, false));

        int par = -1;
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(s1, par));
        vis.set(s1, true);

        while (!q.isEmpty()) {
            int node = q.peek().getKey();
            par = q.peek().getValue();
            q.poll();

            if (node == d2 && par == d1)
                continue;

            g.sea.set(node, true);
            for (int it : g.adj[node]) {
                if (it == d2 && node == d1)
                    continue;
                if (!vis.get(it)) {
                    q.add(new Pair<>(it, node));
                    vis.set(it, true);
                }
            }
        }
    }

    public static void compute_cloud(Graph g, int d1, int s1, int s2) {
        List<Boolean> vis = new ArrayList<>(Collections.nCopies(g.n + 1, false));

        int par = -1;
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(d1, par));
        vis.set(d1, true);

        while (!q.isEmpty()) {
            int node = q.peek().getKey();
            par = q.peek().getValue();
            q.poll();

            if (node == s1 && par == s2)
                continue;

            g.cloud.set(node, true);
            for (int it : g.revadj[node]) {
                if (it == s1 && node == s2)
                    continue;
                if (!vis.get(it)) {
                    q.add(new Pair<>(it, node));
                    vis.set(it, true);
                }
            }
        }
    }

    public static void compute_hydrostructure(Graph g, int s1, int s2, int d1, int d2) {
        compute_sea(g, s1, d2, d1);
        compute_cloud(g, d1, s1, s2);

        for (int i = 1; i <= g.n; i++) {
            if (g.cloud.get(i) && g.sea.get(i))
                g.status.set(i, 'v');
            else if (g.cloud.get(i))
                g.status.set(i, 'c');
            else if (g.sea.get(i))
                g.status.set(i, 's');
            else
                g.status.set(i, 'r');
        }
    }

    public static int verify(Graph g, int u, int v, int s1, int s2, int d1, int d2) {
        g.addedge(u, v);
        compute_hydrostructure(g, s1, s2, d1, d2);

        boolean riv_nonempty = false;
        for (int i = 1; i <= g.status.size(); i++) {
            if (g.status.get(i) == 'r') {
                riv_nonempty = true;
                break;
            }
        }

        boolean open_path = false;
        for (int i = 1; i <= g.status.size(); i++) {
            if (g.status.get(i) != 'v' && g.status.get(i) != 'r') {
                open_path = true;
                break;
            }
        }

        return (open_path && riv_nonempty) ? 1 : 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        Graph g = new Graph(n);

        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            g.addedge(u, v);
        }

        int s1 = scanner.nextInt();
        int s2 = scanner.nextInt();
        int d1 = scanner.nextInt();
        int d2 = scanner.nextInt();

        compute_hydrostructure(g, s1, s2, d1, d2);

        boolean key = scanner.nextBoolean();
        while (key) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();

            int check = verify(g, u, v, s1, s2, d1, d2);

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
        }
    }
}
