import java.util.*;

public class dfs {


public List<Puzzle> search(Puzzle initialState) {
        long startTime = System.currentTimeMillis();

        Stack<Puzzle> stack = new Stack<>();
        Set<Puzzle> visited = new HashSet<>();
        Map<Puzzle, Puzzle> parentMap = new HashMap<>();

        stack.push(initialState);
        visited.add(initialState);
        parentMap.put(initialState, null);

        while (!stack.isEmpty()) {
            Puzzle current = stack.pop();

            if (current.isGoalState()) {
                long endTime = System.currentTimeMillis();
                printStatistics(current, parentMap, startTime, endTime, visited.size());
                return reconstructPath(current, parentMap);
            }

            for (Puzzle neighbor : current.getTilesNear()) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return Collections.emptyList(); // No solution found
    }

    private List<Puzzle> reconstructPath(Puzzle goal, Map<Puzzle, Puzzle> parentMap) {
        List<Puzzle> path = new ArrayList<>();
        for (Puzzle at = goal; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private void printStatistics(Puzzle goal, Map<Puzzle, Puzzle> parentMap, long startTime, long endTime, int nodesVisited) {
        List<Puzzle> path = reconstructPath(goal, parentMap);
        System.out.println("Path to goal:");
        for (int i = 0; i < path.size(); i++) {
            System.out.println("Iteration " + (i + 1) + ":");
            System.out.println(path.get(i));
        }
        System.out.println("Runtime: " + (endTime - startTime) + " ms");
        System.out.println("Nodes Visited: " + nodesVisited);
    }
}
