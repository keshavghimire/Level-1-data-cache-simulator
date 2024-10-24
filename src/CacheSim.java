// CacheSim.java - Main entry point for the cache simulator
public class CacheSim {
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache; // Cache object

    // Constructor
    public CacheSim() {
        // You can initialize default values here if needed
    }

    public static void main(String[] args) {
        CacheSim simulator = new CacheSim();
        simulator.parseParams(args);
        simulator.initializeCache(); // Initialize the cache
        simulator.startSimulation();
    }

    // Method to parse command-line parameters
    public void parseParams(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-c")) {
                capacityKB = Integer.parseInt(arg.substring(2));
                if (!isValidCapacity(capacityKB)) {
                    throw new IllegalArgumentException("Invalid capacity: " + capacityKB);
                }
            } else if (arg.startsWith("-b")) {
                blockSizeBytes = Integer.parseInt(arg.substring(2));
                if (!isValidBlockSize(blockSizeBytes)) {
                    throw new IllegalArgumentException("Invalid block size: " + blockSizeBytes);
                }
            } else if (arg.startsWith("-a")) {
                associativity = Integer.parseInt(arg.substring(2));
                if (!isValidAssociativity(associativity)) {
                    throw new IllegalArgumentException("Invalid associativity: " + associativity);
                }
            }
        }
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

    // Method to initialize the cache based on parameters
    public void initializeCache() {
        cache = new Cache(capacityKB, blockSizeBytes, associativity); // Create cache instance
        System.out.println("Cache initialized with " + cache.sets.length + " sets.");
        cache.printCache(); // Print cache contents for verification
    }

    // Placeholder for starting cache simulation
    public void startSimulation() {
        System.out.println("Simulation started with the given configuration.");
    }
}
