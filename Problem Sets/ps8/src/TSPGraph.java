import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class TSPGraph implements IApproximateTSP {
    @Override
    public void MST(TSPMap map) {
        TreeMapPriorityQueue<Double, Integer> pq = new TreeMapPriorityQueue<>();
        int numOfPoints = map.getCount();
        boolean[] visited = new boolean[numOfPoints];
        HashMap<Integer, Double> minDistance = new HashMap<>();
        for (int i = 0; i < numOfPoints; i++) {
            if (i == 0) {
                pq.add(i, 0.0);
                visited[i] = true;
                minDistance.put(i, 0.0);
            } else {
                pq.add(i, Double.MAX_VALUE);
                minDistance.put(i, Double.MAX_VALUE);
            }
        }
        while (!pq.isEmpty()) {
            int v = pq.extractMin();
            visited[v] = true;
            for (int i = 0; i < numOfPoints; i++) {
                double distanceWithCurrentNode = map.pointDistance(v, i);
                if (!visited[i] && distanceWithCurrentNode < minDistance.get(i)) {
                    pq.decreasePriority(i, distanceWithCurrentNode);
                    minDistance.put(i, distanceWithCurrentNode);
                    map.setLink(i, v, false);
                }
            }
        }
        map.redraw();
    }

    @Override
    public void TSP(TSPMap map) {
        MST(map);
        int numberOfPoints = map.getCount();
        boolean[] visited = new boolean[numberOfPoints];
        ArrayList<Integer> treeWalk = new ArrayList<>();
        treeWalk.add(0);
        DFS(0, map, treeWalk, visited);
        treeWalk.add(0);
        for (int i = 0; i < treeWalk.size() - 1; i++) {
            map.setLink(treeWalk.get(i), treeWalk.get(i + 1), false);
        }
        map.redraw();
    }

    private void DFS(int current, TSPMap map, ArrayList<Integer> treeWalk, boolean[] visited) {
        visited[current] = true;
        TreeMapPriorityQueue<Double, Integer> pq = new TreeMapPriorityQueue<>();
        for (int i = 0; i < map.getCount(); i++) {
            if (map.getLink(i) == current && !visited[i]) pq.add(i, map.pointDistance(current, i));
        }
        while (!pq.isEmpty()) {
            int closestPoint = pq.extractMin();
            treeWalk.add(closestPoint);
            DFS(closestPoint, map, treeWalk, visited);
        }
    }

    @Override
    public boolean isValidTour(TSPMap map) {
        // Note: this function should with with *any* map, and not just results from TSP().
        boolean[] visited = new boolean[map.getCount()];
        visited[0] = true;
        int end = visitNext(map, map.getLink(0), visited);
        // every city has to be visited
        boolean bool = true;
        for (int i = 0; i < map.getCount(); i++) {
            if (!bool) {
                return false;
            } else {
                bool = bool && visited[i];
            }
        }
        return map.getLink(end) == 0;
    }

    private int visitNext(TSPMap map, int next, boolean[] visited) {
        int n;
        if (map.getLink(next) == -1 || visited[map.getLink(next)]) {
            visited[next] = true;
            return next;
        } else {
            visited[next] = true;
            n = visitNext(map, map.getLink(next), visited);
        }
        return n;
    }

    @Override
    public double tourDistance(TSPMap map) {
        return isValidTour(map)
                ? countDistance(map, 0, map.getLink(0),.0, 1)
                : -1;
    }

    private double countDistance(TSPMap map, int current, int next, double total, int visited) {
        total = total + map.pointDistance(current, next);
        return visited == map.getCount()
                ? total
                : countDistance(map, next, map.getLink(next), total, visited + 1);
    }

    public static void main(String[] args) {
        TSPMap map = new TSPMap(args.length > 0 ? args[0] : "./fiftypoints.txt");
        TSPGraph graph = new TSPGraph();

        graph.MST(map);
        graph.TSP(map);
        System.out.println(graph.isValidTour(map));
        System.out.println(graph.tourDistance(map));
    }
}
