import java.util.*;

public class MazeSolver {

    // initialize our grid, counter and 2 end points and our ui (to call updateUI)
    private Tile[][] maze;
    private Counter counter;
    private Tile start;
    private Tile end;
    private MazeUI ui; 

    public MazeSolver(Tile[][] maze) {
        this.maze = maze;
        this.counter = new Counter(0);
        locateStartAndEnd();
    }
    
    // method to set UI reference for updates
    public void setUI(MazeUI ui) {
        this.ui = ui;
    }

    // this method locates our start point and end point by iterating over each tile 
    private void locateStartAndEnd() {
        for (Tile[] row : maze) {
            for (Tile tile : row) {
                if (tile.isStart()) 
                    start = tile;
                
                if (tile.isEnd())
                    end = tile;
            }
        }
        if (start == null || end == null) {
            throw new IllegalStateException("Start or End tile not found.");
        }
    }
    
    // Depth-First search algorithm
    public boolean DFS() {
    	
        // a stack frontier with initial starting tile
        Stack<Tile> stack = new Stack<>();
        stack.push(start);
        start.setVisited(true);

        // loop until we finish the stack  
        while (!stack.isEmpty()) {
            
            // pop the first tile 
            Tile current = stack.pop();
            
            // check if it is the end tile, if not then apply special effects before we continue 
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                // update the UI for the last time after finding the end
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            // update the counter steps 
            counter.value++;
            
            // update the UI 
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // applying special effect
            Tile next = current.applySpecialEffect(counter, maze);

            // if we teleport into a different place, we need to mark it as visited and mark the teleportation tile as the parent tile 
            if (next != current) {
                next.setVisited(true);
                next.setParent(current);
                stack.push(next);
                
                // update UI after teleportation
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                continue;
            }

            // add all neighbors into the stack
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                neighbor.setVisited(true);
                neighbor.setParent(current);
                stack.push(neighbor);
            }
            
            // update UI after exploring neighbors (used to slow down the program, remove if you want it to run faster)
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
        }

        System.out.println("No path found.");
        return false;
    }
    
    // Beadth-First search algorithm
    public boolean BFS() {
    	
        // a queue frontier with initial starting tile
        Queue<Tile> queue = new LinkedList<>();
        queue.offer(start);
        start.setVisited(true);

        // loop until we finish the queue  
        while (!queue.isEmpty()) {
            
            // poll the first tile 
            Tile current = queue.poll();
            
            // check if it is the end tile
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                // update the UI for the last time after finding the end
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            // update the counter steps 
            counter.value++;
            
            // update UI with current state
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // applying special effect
            Tile next = current.applySpecialEffect(counter, maze);

            // if we teleport into a different place
            if (next != current) {
                next.setVisited(true);
                next.setParent(current);
                queue.offer(next);
                
                // update UI after teleportation
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                continue;
            }

            // add all neighbors into the queue
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                neighbor.setVisited(true);
                neighbor.setParent(current);
                queue.offer(neighbor);
            }
            
            // Update UI after exploring neighbors  (used to slow the program down)
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
        }

        System.out.println("No path found.");
        return false;
    }
    
    // A* search algorithm
    public boolean AStar() {
        // Priority queue to store tiles based on their f-score (gScore + hScore)
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>((a, b) -> Double.compare(a.fScore, b.fScore));
        Set<Tile> closedSet = new HashSet<>();
        Map<Tile, Double> gScore = new HashMap<>();
        Map<Tile, Double> fScore = new HashMap<>();
        
        // Initialize starting tile
        AStarNode startNode = new AStarNode(start, 0, heuristic(start, end));
        openSet.offer(startNode);
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, end));
        start.setVisited(true);
        
        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            Tile current = currentNode.tile;
            
            // Check if we reached the end
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            closedSet.add(current);
            counter.value++;
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // Apply special effect
            Tile next = current.applySpecialEffect(counter, maze);
            
            if (next != current) {
                if (!closedSet.contains(next)) {
                    double tentativeGScore = gScore.get(current) + 1;
                    
                    if (!gScore.containsKey(next) || tentativeGScore < gScore.get(next)) {
                        next.setParent(current);
                        gScore.put(next, tentativeGScore);
                        double fScoreValue = tentativeGScore + heuristic(next, end);
                        fScore.put(next, fScoreValue);
                        
                        next.setVisited(true);
                        openSet.offer(new AStarNode(next, tentativeGScore, fScoreValue));
                        
                        if (ui != null) {
                            ui.updateCounter(counter.value);
                            ui.updateUI();
                        }
                    }
                }
                continue;
            }
            
            // Explore neighbors
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                if (closedSet.contains(neighbor)) continue;
                
                double tentativeGScore = gScore.get(current) + 1;
                
                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    neighbor.setParent(current);
                    gScore.put(neighbor, tentativeGScore);
                    double fScoreValue = tentativeGScore + heuristic(neighbor, end);
                    fScore.put(neighbor, fScoreValue);
                    
                    neighbor.setVisited(true);
                    openSet.offer(new AStarNode(neighbor, tentativeGScore, fScoreValue));
                }
            }
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
        }
        
        System.out.println("No path found.");
        return false;
    }
    
 // Greedy Best-First Search algorithm (uses only heuristic, no path cost)
    public boolean greedyBestFirst() {
        // Priority queue to store tiles based on their heuristic distance to end
        PriorityQueue<GreedyNode> openSet = new PriorityQueue<>((a, b) -> Double.compare(a.hScore, b.hScore));
        Set<Tile> closedSet = new HashSet<>();
        
        // Initialize starting tile
        GreedyNode startNode = new GreedyNode(start, heuristic(start, end));
        openSet.offer(startNode);
        start.setVisited(true);
        
        while (!openSet.isEmpty()) {
            GreedyNode currentNode = openSet.poll();
            Tile current = currentNode.tile;
            
            // Check if we reached the end
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            closedSet.add(current);
            counter.value++;
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // Apply special effect
            Tile next = current.applySpecialEffect(counter, maze);
            
            if (next != current) {
                if (!closedSet.contains(next)) {
                    next.setParent(current);
                    next.setVisited(true);
                    openSet.offer(new GreedyNode(next, heuristic(next, end)));
                    
                    if (ui != null) {
                        ui.updateCounter(counter.value);
                        ui.updateUI();
                    }
                }
                continue;
            }
            
            // Explore neighbors
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                if (closedSet.contains(neighbor)) continue;
                
                if (!neighbor.isVisited()) {
                    neighbor.setParent(current);
                    neighbor.setVisited(true);
                    openSet.offer(new GreedyNode(neighbor, heuristic(neighbor, end)));
                }
            }
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
        }
        
        System.out.println("No path found.");
        return false;
    }

    // Helper class for Greedy Best-First Search
    private static class GreedyNode {
        Tile tile;
        double hScore;
        
        GreedyNode(Tile tile, double hScore) {
            this.tile = tile;
            this.hScore = hScore;
        }
    }
    
    // Dijkstra's algorithm
    public boolean Dijkstra() {
        // Priority queue to store tiles based on their distance from start
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>((a, b) -> Double.compare(a.distance, b.distance));
        Map<Tile, Double> distances = new HashMap<>();
        Set<Tile> visited = new HashSet<>();
        
        // Initialize starting tile
        pq.offer(new DijkstraNode(start, 0));
        distances.put(start, 0.0);
        start.setVisited(true);
        
        while (!pq.isEmpty()) {
            DijkstraNode currentNode = pq.poll();
            Tile current = currentNode.tile;
            
            if (visited.contains(current)) continue;
            visited.add(current);
            
            // Check if we reached the end
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            counter.value++;
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // Apply special effect
            Tile next = current.applySpecialEffect(counter, maze);
            
            if (next != current) {
                double newDistance = distances.get(current) + 1;
                
                if (!distances.containsKey(next) || newDistance < distances.get(next)) {
                    distances.put(next, newDistance);
                    next.setParent(current);
                    next.setVisited(true);
                    pq.offer(new DijkstraNode(next, newDistance));
                    
                    if (ui != null) {
                        ui.updateCounter(counter.value);
                        ui.updateUI();
                    }
                }
                continue;
            }
            
            // Explore neighbors
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                if (visited.contains(neighbor)) continue;
                
                double newDistance = distances.get(current) + 1;
                
                if (!distances.containsKey(neighbor) || newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    neighbor.setParent(current);
                    neighbor.setVisited(true);
                    pq.offer(new DijkstraNode(neighbor, newDistance));
                }
            }
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
        }
        
        System.out.println("No path found.");
        return false;
    }
        
    
    // Dead End Fill algorithm
    public boolean deadEndFill() {
        // Create a copy of the maze for dead end filling
        boolean[][] isDeadEnd = new boolean[maze.length][maze[0].length];
        boolean foundDeadEnd = true;
        
        // First, identify and mark dead ends
        while (foundDeadEnd) {
            foundDeadEnd = false;
            
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    Tile tile = maze[i][j];
                    
                    // Skip if it's a wall, start, end tile, or already marked as dead end
                    if (tile.isWall() || tile.isStart() || tile.isEnd() || isDeadEnd[i][j]) {
                        continue;
                    }
                    
                    // Count walkable neighbors that aren't dead ends
                    int walkableNeighbors = 0;
                    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                    
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];
                        
                        if (newRow >= 0 && newRow < maze.length && 
                            newCol >= 0 && newCol < maze[0].length) {
                            
                            Tile neighbor = maze[newRow][newCol];
                            if (!neighbor.isWall() && !isDeadEnd[newRow][newCol]) {
                                walkableNeighbors++;
                            }
                        }
                    }
                    
                    // If it has only one walkable neighbor, it's a dead end
                    if (walkableNeighbors <= 1) {
                        isDeadEnd[i][j] = true;
                        foundDeadEnd = true;
                        counter.value++;
                        
                        // Mark tile for visualization
                        if (ui != null) {
                            ui.markDeadEndTile(i, j);
                            ui.updateCounter(counter.value);
                            ui.updateUI();
                        }
                    }
                }
            }
        }
        
        // Now use BFS on the simplified maze, avoiding dead ends
        Queue<Tile> queue = new LinkedList<>();
        queue.offer(start);
        start.setVisited(true);
        
        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            
            if (current.isEnd()) {
                System.out.println("Reached the end! Final counter: " + counter.value);
                
                if (ui != null) {
                    ui.updateCounter(counter.value);
                    ui.updateUI();
                }
                
                return true;
            }
            
            counter.value++;
            
            if (ui != null) {
                ui.updateCounter(counter.value);
                ui.updateUI();
            }
            
            // Apply special effect
            Tile next = current.applySpecialEffect(counter, maze);
            
            if (next != current) {
                if (!next.isVisited() && !isDeadEnd[next.getRow()][next.getCol()]) {
                    next.setVisited(true);
                    next.setParent(current);
                    queue.offer(next);
                }
                continue;
            }
            
            // Add unvisited walkable neighbors that aren't dead ends
            for (Tile neighbor : current.getValidNeighbors(maze)) {
                if (!isDeadEnd[neighbor.getRow()][neighbor.getCol()]) {
                    neighbor.setVisited(true);
                    neighbor.setParent(current);
                    queue.offer(neighbor);
                }
            }
        }
        
        System.out.println("No path found.");
        return false;
    }
    
    // Helper method to check if a tile is walkable
    private boolean isWalkable(Tile tile) {
        return tile != null && !tile.isWall();
    }
    
    // Heuristic function for A* (Manhattan distance)
    private double heuristic(Tile a, Tile b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }
    
    // Helper classes for A* and Dijkstra
    private static class AStarNode {
        Tile tile;
        double gScore;
        double fScore;
        
        AStarNode(Tile tile, double gScore, double fScore) {
            this.tile = tile;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
    
    private static class DijkstraNode {
        Tile tile;
        double distance;
        
        DijkstraNode(Tile tile, double distance) {
            this.tile = tile;
            this.distance = distance;
        }
    }
    
    // this method backtracks from end to finish using the parent tiles to return the shortest path we found
    public List<Tile> reconstructPath(Tile end) {
    	
    	// initialize arraylist that will contain all the tiles
        List<Tile> path = new ArrayList<>();

        // start from the end tile then loop and call the parent of each tile
        Tile current = end;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }

        // The path is from end to start, so we reverse it
        Collections.reverse(path);
        return path;
    }
    
    // Getters for UI access
    public Counter getCounter() {
        return counter;
    }
    
    public Tile getStart() {
        return start;
    }
    
    public Tile getEnd() {
        return end;
    }
}