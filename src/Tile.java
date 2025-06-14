import java.util.ArrayList;
import java.util.List;
import java.util.Random; 

// enum containing all the types of tiles to avoid repeating blocks of code
enum TileType {
    EMPTY,
    WALL,
    START,
    END,
    TELEPORT,
    COUNTER_UP,
    COUNTER_DOWN
}

public class Tile {
    
    // initialize our grid rows and columns sizes
    private int row;
    private int col;
    // our tile type from the enum
    private TileType type;
    // parent tile for backtracking after solving
    private Tile parent; 
    // boolean to mark any checked tiles to prevent re-checking the same tile over and over 
    private boolean visited;

    public Tile(int row, int col, TileType type) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.visited = false;
    }

    // getters
    public int getRow() { return row; }
    public int getCol() { return col; }
    public TileType getType() { return type; }
    public Tile getParent() { return parent; }
    
    // setters
    public void setVisited(boolean visited) { this.visited = visited; }
    public void setParent(Tile parent) { this.parent = parent; }
    
    // tile status
    public boolean isWall() { return type == TileType.WALL; }
    public boolean isStart() { return type == TileType.START; }
    public boolean isEnd() { return type == TileType.END; }
    public boolean isTeleport() { return type == TileType.TELEPORT; }
    public boolean isCounterUp() { return type == TileType.COUNTER_UP; }
    public boolean isCounterDown() { return type == TileType.COUNTER_DOWN; }
    public boolean isVisited() { return visited; }

    
    
    // this class takes the output of the MazeLoader.load method and turns it into a 2d array of tiles rather than 2d array of chars
    public static Tile[][] convertToTiles(char[][] charMaze) {
        
        // initialize the 2d array 
        int rows = charMaze.length;
        int cols = charMaze[0].length;
        Tile[][] tileMaze = new Tile[rows][cols];

        // fill the array with all tiles
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char ch = charMaze[i][j];
                TileType type;

                switch (ch) {
                    case '#':
                        type = TileType.WALL;
                        break;
                    case 'A':
                        type = TileType.START;
                        break;
                    case 'B':
                        type = TileType.END;
                        break;
                    case 'T':
                        type = TileType.TELEPORT; 
                        break;
                    case 'C':
                        type = TileType.COUNTER_UP;
                        break;
                    case 'c': 
                        type = TileType.COUNTER_DOWN;
                        break;
                    case ' ': 
                        type = TileType.EMPTY;
                        break;
                        
                    default:
                        throw new IllegalArgumentException("Unknown tile character: " + ch);
                }

                tileMaze[i][j] = new Tile(i, j, type);
            }
        }

        return tileMaze;
    }
    
    // this method returns a linked list containing all the legal neighboring tiles
    // we used a linked list instead of directly loading it into a stack or Queue so that we can choose our 
    // search algorithm later and load the neighbors however we want 
    public List<Tile> getValidNeighbors(Tile[][] maze) {
        // initialize the linked list
        List<Tile> neighbors = new ArrayList<>();
        
        // 2d direction array
        int[][] directions = {
            {1, 0}, // Down
            {-1, 0},  // Up
            {0, 1}, // Right
            {0, -1}   // Left
        };
        
        // the boundary for the maze
        int rows = maze.length;
        int cols = maze[0].length;
        
        // loop for each direction and check if that neighbor is within boundary and is not a wall
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            
            //boundary check
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                Tile neighbor = maze[newRow][newCol];
                
                // Only check if it's a wall, NOT if it's visited
                if (!neighbor.isWall() && !neighbor.isVisited()) {
                    neighbors.add(neighbor);
                }
            }
        }
        
        return neighbors;
    }
    
    // this method applies all the special tiles effects ( increase or decrease counter, and teleportation)
    public Tile applySpecialEffect(Counter counter, Tile[][] maze) {
        switch (this.type) {
            case COUNTER_UP:
                counter.value += 50;
                break;
            case COUNTER_DOWN:
                counter.value -= 50;
                break;
            case TELEPORT:
                
                Random random = new Random();
                Tile newLocation = null;
                // our boundary for the random location 
                int rows = maze.length;
                int cols = maze[0].length;

                // loop until we find a legal tile
                do {
                    // choose a random tile within our boundary
                    int randRow = random.nextInt(rows);
                    int randCol = random.nextInt(cols);
                    // check if the tile is a wall
                    if (!maze[randRow][randCol].isWall() && !maze[randRow][randCol].isVisited()) {
                        newLocation = maze[randRow][randCol];
                    }
                } while (newLocation == null);

                return newLocation;

            default:
                break;
        }

        return this; // return current tile if not teleport
    }
}


