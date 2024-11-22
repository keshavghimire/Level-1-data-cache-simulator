import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CacheSimGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create UI components
        Label cacheCapacityLabel = new Label("Cache Capacity (KB):");
        TextField cacheCapacityField = new TextField();
        cacheCapacityField.setPromptText("Enter capacity in KB");

        Label blockSizeLabel = new Label("Block Size (Bytes):");
        TextField blockSizeField = new TextField();
        blockSizeField.setPromptText("Enter block size in bytes");

        Label associativityLabel = new Label("Associativity:");
        TextField associativityField = new TextField();
        associativityField.setPromptText("Enter associativity");

        Button simulateButton = new Button("Run Simulation");

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        Label errorMessageLabel = new Label();
        errorMessageLabel.setStyle("-fx-text-fill: red;");

        // Create a GridPane for layout
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(cacheCapacityLabel, 0, 0);
        grid.add(cacheCapacityField, 1, 0);

        grid.add(blockSizeLabel, 0, 1);
        grid.add(blockSizeField, 1, 1);

        grid.add(associativityLabel, 0, 2);
        grid.add(associativityField, 1, 2);

        grid.add(simulateButton, 1, 3);
        grid.add(outputArea, 0, 4, 2, 1);
        grid.add(errorMessageLabel, 0, 5, 2, 1); // Add error message label

        // Button action - start simulation
        simulateButton.setOnAction(e -> {
            String cacheCapacity = cacheCapacityField.getText();
            String blockSize = blockSizeField.getText();
            String associativity = associativityField.getText();

            // Validate input
            if (cacheCapacity.isEmpty() || blockSize.isEmpty() || associativity.isEmpty()) {
                outputArea.setText("Please fill in all fields.");
                return;
            }

            try {
                // Debugging user input
                System.out.println("Cache Capacity: " + cacheCapacity);
                System.out.println("Block Size: " + blockSize);
                System.out.println("Associativity: " + associativity);

                int capacityKB = Integer.parseInt(cacheCapacity);
                int blockSizeBytes = Integer.parseInt(blockSize);
                int associativityVal = Integer.parseInt(associativity);

                // Call the CacheSim with user input as parameters
                runCacheSimulation(capacityKB, blockSizeBytes, associativityVal, outputArea);

            } catch (NumberFormatException ex) {
                outputArea.setText("Invalid input. Please enter valid numbers.");
                ex.printStackTrace(); // This will print the full stack trace to the console to help debug
            }
        });

        // Set the Scene
        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setTitle("Cache Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to validate cache capacity
    private boolean isValidCapacity(int capacity) {
        return capacity == 4 || capacity == 8 || capacity == 16 || capacity == 32 || capacity == 64;
    }

    // Method to validate block size
    private boolean isValidBlockSize(int blockSize) {
        return blockSize == 4 || blockSize == 8 || blockSize == 16 || blockSize == 32 ||
                blockSize == 64 || blockSize == 128 || blockSize == 256 || blockSize == 512;
    }

    // Method to validate associativity
    private boolean isValidAssociativity(int associativity) {
        return associativity == 1 || associativity == 2 || associativity == 4 ||
                associativity == 8 || associativity == 16;
    }

    private void runCacheSimulation(int capacityKB, int blockSizeBytes, int associativity, TextArea outputArea) {
        // Build the command-line arguments
        String[] args = {
                "-c" + capacityKB,
                "-b" + blockSizeBytes,
                "-a" + associativity
        };

        // Simulate cache operation by calling CacheSim
        CacheSim.main(args);

        // Display the output in the TextArea (replace with actual simulation output)
        outputArea.setText("Simulation complete.\n" +
                "Cache Capacity: " + capacityKB + " KB\n" +
                "Block Size: " + blockSizeBytes + " bytes\n" +
                "Associativity: " + associativity + "\n\n" +
                "Cache Simulation Statistics and Cache Contents will be shown here.");
    }
}
