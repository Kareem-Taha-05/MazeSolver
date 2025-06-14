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

        
        
    }

}
