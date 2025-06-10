import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        // Ensure UI is created on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set look and feel to system default
                    javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // If setting look and feel fails, use default
                    System.out.println("Could not set system look and feel: " + e.getMessage());
                }
                
                // Create and initialize the UI
                new MazeUI();
            }
        });
    }
}