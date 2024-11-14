import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        int capacityKB = 8;       // Default values as per the baseline
        int blockSizeBytes = 16;
        int associativity = 4;
        String traceFilePath = "trace_file.txt";  // Using the uploaded trace file

        // Parse optional command-line arguments if provided
        if (args.length >= 3) {
            try {
                capacityKB = Integer.parseInt(args[0].substring(2));
                blockSizeBytes = Integer.parseInt(args[1].substring(2));
                associativity = Integer.parseInt(args[2].substring(2));
            } catch (NumberFormatException e) {
                System.out.println("Error parsing arguments. Ensure format: -c<size> -b<size> -a<associativity>");
                return;
            }
            if (args.length == 4) {
                traceFilePath = args[3];
            }
        }

        // Initialize and run the simulator
        CacheSim simulator = new CacheSim(capacityKB, blockSizeBytes, associativity);
        simulator.processTrace(traceFilePath);
        simulator.printStatistics();  // Print statistics and cache contents at end of simulation
    }
}
