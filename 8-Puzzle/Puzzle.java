import java.util.*;

class Puzzle {
    private int[][] state;
    private int emptyTileRow, emptyTileCol;

    // pass in initial state of our puzzle 
    public Puzzle(int[][] initialState) {
        this.state = initialState;

        // we want to find the empty tile
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    emptyTileRow = i;
                    emptyTileCol = j;
                    break;
                }
            }
        }
    }

    // method for checking if we have reached our desired state
    public boolean isGoalState() {
        int[][] goal = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
        return Arrays.deepEquals(this.state, goal);
    }

    // gen all possible states
    public List<Puzzle> getTilesNear() {
        List<Puzzle> neighbors = new ArrayList<>();
        // allowed moves
        int[] rowMoves = {-1, 1, 0, 0};
        int[] colMoves = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = emptyTileRow + rowMoves[i];
            int newCol = emptyTileCol + colMoves[i];
            // check to see if is within bounds of 3x3 arr
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                int[][] newState = deepCopyState();
                newState[emptyTileRow][emptyTileCol] = newState[newRow][newCol];
                newState[newRow][newCol] = 0;
                neighbors.add(new Puzzle(newState));
            }
        }
        return neighbors;
    }
    // manhattan dist calculations
    public int getManhattanDistance() {
        int distance = 0;
        int[][] goalPositions = {{1, 1, 1, 2, 2, 2, 0, 0, 0}, {0, 1, 2, 0, 1, 2, 0, 1, 2}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int value = state[i][j];
                if (value != 0) {
                    distance += Math.abs(i - goalPositions[0][value]) + Math.abs(j - goalPositions[1][value]);
                }
            }
        }
        return distance;
    }

    // counts how manh tiles are out of 'place' compared to goal state
    public int tilesOutOfPlace() {
        int count = 0;
        int[][] goal = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != 0 && state[i][j] != goal[i][j]) {
                    count++;
                }
            }
        }
       return count;
    }
    // copy of current state
    private int[][] deepCopyState() {
        int[][] newState = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(state[i], 0, newState[i], 0, 3);
        }
        return newState;
    }

    // generic methods

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Puzzle puzzle = (Puzzle) obj;
        return Arrays.deepEquals(state, puzzle.state);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(state);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : state) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }
}

class DFS {

    // dfs algo
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
                printStats(current, parentMap, startTime, endTime, visited.size());
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

    // construct pathReconstruction to goal state
    private List<Puzzle> reconstructPath(Puzzle goal, Map<Puzzle, Puzzle> parentMap) {
        List<Puzzle> path = new ArrayList<>();
        for (Puzzle at = goal; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
    // print stat
    private void printStats(Puzzle goal, Map<Puzzle, Puzzle> parentMap, long startTime, long endTime, int nodesVisited) {
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

class UCS {
    public List<Puzzle> search(Puzzle initialState) {
        long startTime = System.currentTimeMillis();

        PriorityQueue<Puzzle> queue = new PriorityQueue<>(Comparator.comparingInt(Puzzle::getManhattanDistance));
        Set<Puzzle> visited = new HashSet<>();
        Map<Puzzle, Puzzle> parentMap = new HashMap<>();

        queue.add(initialState);
        visited.add(initialState);
        parentMap.put(initialState, null);

        while (!queue.isEmpty()) {
            Puzzle current = queue.poll();

            if (current.isGoalState()) {
                long endTime = System.currentTimeMillis();
                printStatistics(current, parentMap, startTime, endTime, visited.size());
                return reconstructPath(current, parentMap);
            }

            for (Puzzle neighbor : current.getTilesNear()) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
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

class BFS {
    public List<Puzzle> search(Puzzle initialState) {
        long startTime = System.currentTimeMillis();

        PriorityQueue<Puzzle> queue = new PriorityQueue<>(Comparator.comparingInt(Puzzle::getManhattanDistance));
        Set<Puzzle> visited = new HashSet<>();
        Map<Puzzle, Puzzle> parentMap = new HashMap<>();

        queue.add(initialState);
        visited.add(initialState);
        parentMap.put(initialState, null);

        while (!queue.isEmpty()) {
            Puzzle current = queue.poll();

            if (current.isGoalState()) {
                long endTime = System.currentTimeMillis();
                printStatistics(current, parentMap, startTime, endTime, visited.size());
                return reconstructPath(current, parentMap);
            }

            for (Puzzle neighbor : current.getTilesNear()) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
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

class AStar {
    public List<Puzzle> search(Puzzle initialState) {
        long startTime = System.currentTimeMillis();
        // we can priority Queue with a compar
        PriorityQueue<Puzzle> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.getManhattanDistance() + p.tilesOutOfPlace()));
        Set<Puzzle> visited = new HashSet<>();
        Map<Puzzle, Puzzle> parentMap = new HashMap<>();

        queue.add(initialState);
        visited.add(initialState);
        parentMap.put(initialState, null);

        while (!queue.isEmpty()) {
            Puzzle current = queue.poll();

            if (current.isGoalState()) {
                long endTime = System.currentTimeMillis();
                printStatistics(current, parentMap, startTime, endTime, visited.size());
                return reconstructPath(current, parentMap);
            }

            for (Puzzle neighbor : current.getTilesNear()) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return Collections.emptyList(); // No solution found
    }

    private List<Puzzle> reconstructPath(Puzzle goal, Map<Puzzle, Puzzle> parentMap) {
        List<Puzzle> pathReconstruction = new ArrayList<>();
        for (Puzzle at = goal; at != null; at = parentMap.get(at)) {
            pathReconstruction.add(at);
        }
        Collections.reverse(pathReconstruction);
        return pathReconstruction;
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


 class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("""
                  OPTIONS
                ------------
                1) DFS
                2) UCS
                3) BFS
                4) A*
                5) Exit
                    """);

            System.out.print("Choose Algorithm: ");


            int option = scanner.nextInt();

            if (option == 5) {
                break;
            }

            System.out.print("Enter Number of Trials: ");
            int numberOfTrials = scanner.nextInt();

            List<Integer> nodesVisitedList = new ArrayList<>();
            List<Long> runtimes = new ArrayList<>();
            
            // runs for n num of trials
            for (int i = 0; i < numberOfTrials; i++) {
                int[][] initialState = RandomInitialFill();
                Puzzle puzzle = new Puzzle(initialState);
                long startTime = System.currentTimeMillis();

                List<Puzzle> path = new ArrayList<>();


                // switch case
                switch (option) {

                    case 1 -> {
                        DFS dfs = new DFS();
                        path = dfs.search(puzzle);
                    }
                    case 2 -> {
                        UCS ucs = new UCS();
                        path = ucs.search(puzzle);
                    }
                    case 3 -> {
                        BFS bfs = new BFS();
                        path = bfs.search(puzzle);
                    }
                    case 4 -> {
                        AStar aStar = new AStar();
                        path = aStar.search(puzzle);
                    }
                }

                long endTime = System.currentTimeMillis();
                if (!path.isEmpty()) {
                    nodesVisitedList.add(path.size());
                    runtimes.add(endTime - startTime);
                }
            }

            System.out.println("Data for Nodes Visited: " + nodesVisitedList);
            System.out.println("Minimum: " + Collections.min(nodesVisitedList));
            System.out.println("Average: " + nodesVisitedList.stream().mapToInt(Integer::intValue).average().orElse(0));
            System.out.println("Maximum: " + Collections.max(nodesVisitedList));
            System.out.println("Runtimes: " + runtimes);
            System.out.println();
        }

        scanner.close();
    }

    private static int[][] RandomInitialFill() {

        List<Integer> tiles = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        Collections.shuffle(tiles);
        int[][] state = new int[3][3];
        int k = 0;

        // fill with 0-8 
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = tiles.get(k++);
            }
        }
        return state;
    }
}

