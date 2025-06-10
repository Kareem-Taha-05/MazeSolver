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