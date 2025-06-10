import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MazeUI extends JFrame {
    
    // UI Components
    private JPanel gridPanel;
    private JPanel controlPanel;
    private JPanel[][] gridPanels;
    private JLabel counterLabel;
    private JComboBox<String> algorithmDropdown;
    private JButton loadMazeButton;
    private JButton startButton;
    
    // Maze data
    private Tile[][] maze;
    private MazeSolver solver;
    private Counter counter ;
    private boolean mazeLoaded;
    
    // Colors for different tile states
    private static final Color WALL_COLOR = Color.BLACK;
    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color START_COLOR = Color.GREEN;
    private static final Color END_COLOR = Color.RED;
    private static final Color TELEPORT_COLOR = Color.BLUE;
    private static final Color COUNTER_UP_COLOR = new Color(100, 0, 0);
    private static final Color COUNTER_DOWN_COLOR = new Color(0, 100, 0); // Dark green
    private static final Color EXPLORED_COLOR = Color.YELLOW;
    private static final Color FINAL_PATH_COLOR = Color.GREEN;
    
    public MazeUI() {
        this.counter = new Counter(0);
        this.mazeLoaded = false;
        initialize();
    }
    public void initialize() {
        // Set up main frame
        setTitle("Maze Solver Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize components
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        // Pack and center the window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Grid panel - initially empty
        gridPanel = new JPanel();
        gridPanel.setBorder(BorderFactory.createTitledBorder("Maze Grid"));
        gridPanel.setPreferredSize(new Dimension(600, 400));
        
        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        controlPanel.setPreferredSize(new Dimension(200, 400));
        
        // Counter label
        counterLabel = new JLabel("Counter: 0");
        counterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Algorithm dropdown
        String[] algorithms = {"Depth-First Search (DFS)", "Breadth-First Search (BFS)", "A* Search"};
        algorithmDropdown = new JComboBox<>(algorithms);
        
        // Buttons
        loadMazeButton = new JButton("Load Maze");
        startButton = new JButton("Start Algorithm");
        startButton.setEnabled(false); // Disabled until maze is loaded
    }
    
    private void setupLayout() {
        // Add grid panel to center
        add(gridPanel, BorderLayout.CENTER);
        
        // Setup control panel
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(new JLabel("Steps"));
        controlPanel.add(counterLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmDropdown);
        controlPanel.add(Box.createVerticalStrut(20));
        
        controlPanel.add(loadMazeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(startButton);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Add legend
        controlPanel.add(createLegend());
        
        // Add control panel to right
        add(controlPanel, BorderLayout.EAST);
    }
    
    private JPanel createLegend() {
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBorder(BorderFactory.createTitledBorder("Guide"));
        
        legend.add(createLegendItem("Wall", WALL_COLOR));
        legend.add(createLegendItem("Empty", EMPTY_COLOR));
        legend.add(createLegendItem("Start (A)", START_COLOR));
        legend.add(createLegendItem("End (B)", END_COLOR));
        legend.add(createLegendItem("Teleport (T)", TELEPORT_COLOR));
        legend.add(createLegendItem("Counter+ (C)", COUNTER_UP_COLOR));
        legend.add(createLegendItem("Counter- (c)", COUNTER_DOWN_COLOR));
        legend.add(createLegendItem("Explored", EXPLORED_COLOR));
        legend.add(createLegendItem("Final Path", FINAL_PATH_COLOR));
        
        return legend;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel colorBox = new JLabel("  ");
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        item.add(colorBox);
        item.add(new JLabel(text));
        return item;
    }
    
    private void setupEventListeners() {
        loadMazeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMaze();
            }
        });
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAlgorithm();
            }
        });
    }
    
    private void loadMaze() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Load maze using your existing classes
                MazeLoader loader = new MazeLoader();
                char[][] charMaze = loader.load(selectedFile.getAbsolutePath());
                maze = Tile.convertToTiles(charMaze);
                
                // Create solver and set UI reference
                solver = new MazeSolver(maze);
                counter = solver.getCounter();
                solver.setUI(this);
                
                // Setup grid display
                setupGrid();
                
                mazeLoaded = true;
                startButton.setEnabled(true);
                
                JOptionPane.showMessageDialog(this, "Maze loaded successfully!");
                
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error loading maze: " + ex.getMessage(), 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupGrid() {
        gridPanel.removeAll();
        
        int rows = maze.length;
        int cols = maze[0].length;
        
        gridPanel.setLayout(new GridLayout(rows, cols, 1, 1));
        gridPanels = new JPanel[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension(25, 25));
                panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                panel.setOpaque(true);
                
                // Set initial color based on tile type
                setTileColor(panel, maze[i][j]);
                
                gridPanels[i][j] = panel;
                gridPanel.add(panel);
            }
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
        pack();
    }
    
    private void setTileColor(JPanel panel, Tile tile) {
        Color color;
        
        if (tile.isVisited() && !tile.isStart() && !tile.isEnd()) {
            color = EXPLORED_COLOR;
        } else {
            switch (tile.getType()) {
                case WALL:
                    color = WALL_COLOR;
                    break;
                case START:
                    color = START_COLOR;
                    break;
                case END:
                    color = END_COLOR;
                    break;
                case TELEPORT:
                    color = TELEPORT_COLOR;
                    break;
                case COUNTER_UP:
                    color = COUNTER_UP_COLOR;
                    break;
                case COUNTER_DOWN:
                    color = COUNTER_DOWN_COLOR;
                    break;
                default:
                    color = EMPTY_COLOR;
            }
        }
        
        panel.setBackground(color);
    }
    
    public void updateUI() {
        if (maze == null || gridPanels == null) return;
        
        SwingUtilities.invokeLater(() -> {
            // Update grid colors
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {
                    setTileColor(gridPanels[i][j], maze[i][j]);
                }
            }
            
            // Update counter
            counterLabel.setText("Counter: " + counter.value);
            
            // Repaint
            gridPanel.repaint();
        });
        
        // Sleep to show progress
        try {
            Thread.sleep(100); // Adjust delay as needed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void updateCounter(int newCounter) {
        counter.value = newCounter;
    }
    
    public void highlightFinalPath(List<Tile> path) {
        SwingUtilities.invokeLater(() -> {
            for (Tile tile : path) {
                if (!tile.isStart() && !tile.isEnd()) {
                    JPanel panel = gridPanels[tile.getRow()][tile.getCol()];
                    panel.setBackground(FINAL_PATH_COLOR);
                }
            }
            gridPanel.repaint();
        });
        
        // Sleep to show the final path
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void startAlgorithm() {
        if (!mazeLoaded) {
            JOptionPane.showMessageDialog(this, "Please load a maze first!");
            return;
        }
        
        // Reset maze state
        resetMaze();
        
        // Disable start button during execution
        startButton.setEnabled(false);
        
        // Run algorithm in separate thread to avoid blocking UI
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String selectedAlgorithm = (String) algorithmDropdown.getSelectedItem();
                
                boolean found = false;
                if (selectedAlgorithm.contains("DFS")) {
                    found = solver.DFS();
                } else if (selectedAlgorithm.contains("BFS")) {
                    found = solver.BFS();
                } else if (selectedAlgorithm.contains("A*")) {
                    // You'll need to implement A* in your MazeSolver
                    JOptionPane.showMessageDialog(MazeUI.this, "A* not implemented yet!");
                }
                
                return found;
            }
            
            @Override
            protected void done() {
                try {
                    boolean found = get();
                    if (found) {
                        // Show final path
                        List<Tile> path = solver.reconstructPath(findEndTile());
                        highlightFinalPath(path);
                        JOptionPane.showMessageDialog(MazeUI.this, "Path found!");
                    } else {
                        JOptionPane.showMessageDialog(MazeUI.this, "No path found!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MazeUI.this, "Error: " + e.getMessage());
                }
                
                // Re-enable start button
                startButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void resetMaze() {
        counter.value = 0 ;
        for (Tile[] row : maze) {
            for (Tile tile : row) {
                tile.setVisited(false);
                tile.setParent(null);
            }
        }
        updateUI();
    }
    
    private Tile findEndTile() {
        for (Tile[] row : maze) {
            for (Tile tile : row) {
                if (tile.isEnd()) {
                    return tile;
                }
            }
        }
        return null;
    }
}