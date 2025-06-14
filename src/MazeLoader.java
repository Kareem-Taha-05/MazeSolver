import java.io.*;
import java.util.*;

public class MazeLoader {

    // initialize the 2d array that will contain the maze
    private char[][] maze;

    // counters for special tiles, to make sure that there is only one tile of these types 
    private int startCount = 0;              // char A
    private int endCount = 0;				 // char B
    private int teleportCount = 0;           // char T
    private int counterIncreaseCount = 0;    // char C
    private int counterDecreaseCount = 0;    // char c


    public char[][] load(String filename) throws IOException {
    	
        List<String> lines = new ArrayList<>();
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int rowLength = -1;

        // this loop loads all lines of the txt file into an array list as strings, and it checks for the size of the rows
        while ((line = reader.readLine()) != null) {
            // skip empty lines
            if (line.trim().isEmpty()) 
            	continue;

            // take the size of the first row for reference 
            if (rowLength == -1) {
                rowLength = line.length();
            }
            // compare the rest of the rows with it 
            else if (line.length() != rowLength) {
                throw new IllegalArgumentException("All rows must be the same length.");
            }

            
            lines.add(line);
        }
        reader.close();

        // Number of rows and columns
        int rows = lines.size();
        int cols = rowLength;

        // Initialize maze size 
        maze = new char[rows][cols];

        // Fill maze and validate characters
        for (int i = 0; i < rows; i++) {
            String currentLine = lines.get(i);
            for (int j = 0; j < cols; j++) {
                char ch = currentLine.charAt(j);

                // Validate character
                if (!(ch == 'A' || ch == 'B' || ch == 'C' || ch == 'c' || ch == 'T' || ch == '#' || ch == ' ')) {
                    throw new IllegalArgumentException("Invalid character found: '" + ch + "' at (" + i + ", " + j + ")");
                }

                // Count special tiles
                switch (ch) {
                    case 'A' -> startCount++;
                    case 'B' -> endCount++;
                    case 'T' -> teleportCount++;
                    case 'C' -> counterIncreaseCount++;
                    case 'c' -> counterDecreaseCount++;
                }

                maze[i][j] = ch;
            }
        }

        // Check tile counts
        if (startCount != 1) throw new IllegalArgumentException("Maze must have exactly one start tile (A).");
        if (endCount != 1) throw new IllegalArgumentException("Maze must have exactly one end tile (B).");
        //if (teleportCount != 1) throw new IllegalArgumentException("Maze must have exactly one teleportation tile (T).");    //this one could be removed for smaller mazes to avoid infinite teleportation 
        //if (counterIncreaseCount != 1) throw new IllegalArgumentException("Maze must have exactly one counter increase tile (C).");
        //if (counterDecreaseCount != 1) throw new IllegalArgumentException("Maze must have exactly one counter decrease tile (c).");

        return maze;
    }

}
