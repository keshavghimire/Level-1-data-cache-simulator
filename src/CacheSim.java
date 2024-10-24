
class CacheLine {
    int tag;             // Tag for the cache line
    boolean valid;       // Valid bit (true if the line is valid)
    boolean dirty;       // Dirty bit (true if the line is modified)
    int[] data;         // Data stored in the cache line

    // Constructor
    public CacheLine(int blockSize) {
        this.tag = 0;
        this.valid = false;   // Initially invalid
        this.dirty = false;   // Initially not dirty
        this.data = new int[blockSize / 4]; // 4 bytes per word (32 bits)
    }
}

// CacheSet.java - Represents a set in the cache
class CacheSet {
    CacheLine[] lines;  // Array of cache lines

    // Constructor
    public CacheSet(int associativity, int blockSize) {
        lines = new CacheLine[associativity];
        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockSize); // Initialize each cache line
        }
    }
}

// Cache.java - Represents the cache itself
class Cache {
    CacheSet[] sets; // Array of sets in the cache

    // Constructor
    public Cache(int capacityKB, int blockSize, int associativity) {
        int numSets = (capacityKB * 1024) / (blockSize * associativity); // Calculate number of sets
        sets = new CacheSet[numSets];

        for (int i = 0; i < numSets; i++) {
            sets[i] = new CacheSet(associativity, blockSize); // Initialize each set
        }
    }

    // Method to print cache contents for debugging
    public void printCache() {
        for (int i = 0; i < sets.length; i++) {
            System.out.print("Set " + i + ": ");
            for (CacheLine line : sets[i].lines) {
                System.out.print("[Valid: " + line.valid + ", Tag: " + Integer.toHexString(line.tag) + ", Dirty: " + line.dirty + "] ");
            }
            System.out.println();
        }
    }
}

public class CacheSim {

    // Cache parameters
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache; // Cache object


    public static void main(String[] args) {
        CacheSim simulator = new CacheSim();
        simulator.parseParams(args);
        simulator.initializeCache(); // Initialize the cache
        simulator.startSimulation();
    }

    // Method to parse command-line parameters
    public void parseParams(String[] args) {
        if (args.length != 3) {
            System.out.println("Error: Incorrect number of arguments. Usage: java cache_sim -c<capacity> -b<blocksize> -a<associativity>");
            System.exit(1);
        }

        for (String arg : args) {
            if (arg.startsWith("-c")) {
                capacityKB = Integer.parseInt(arg.substring(2)); // Parse capacity in KB
                if (!isValidCapacity(capacityKB)) {
                    System.out.println("Error: Invalid capacity. Valid values are 4, 8, 16, 32, 64 KB.");
                    System.exit(1);
                }
            } else if (arg.startsWith("-b")) {
                blockSizeBytes = Integer.parseInt(arg.substring(2)); // Parse block size in bytes
                if (!isValidBlockSize(blockSizeBytes)) {
                    System.out.println("Error: Invalid block size. Valid values are 4, 8, 16, 32, 64, 128, 256, 512 bytes.");
                    System.exit(1);
                }
            } else if (arg.startsWith("-a")) {
                associativity = Integer.parseInt(arg.substring(2)); // Parse associativity
                if (!isValidAssociativity(associativity)) {
                    System.out.println("Error: Invalid associativity. Valid values are 1, 2, 4, 8, 16.");
                    System.exit(1);
                }
            } else {
                System.out.println("Error: Invalid argument format.");
                System.exit(1);
            }
        }

        System.out.println("Cache Configuration: ");
        System.out.println("Capacity: " + capacityKB + " KB");
        System.out.println("Block Size: " + blockSizeBytes + " bytes");
        System.out.println("Associativity: " + associativity);
    }

    // Validate cache capacity
    private boolean isValidCapacity(int capacity) {
        return capacity == 4 || capacity == 8 || capacity == 16 || capacity == 32 || capacity == 64;
    }

    // Validate block size
    private boolean isValidBlockSize(int blockSize) {
        return blockSize == 4 || blockSize == 8 || blockSize == 16 || blockSize == 32 ||
                blockSize == 64 || blockSize == 128 || blockSize == 256 || blockSize == 512;
    }

    // Validate associativity
    private boolean isValidAssociativity(int associativity) {
        return associativity == 1 || associativity == 2 || associativity == 4 || associativity == 8 || associativity == 16;
    }

    public void initializeCache() {
        cache = new Cache(capacityKB, blockSizeBytes, associativity); // Create cache instance
        System.out.println("Cache initialized with " + cache.sets.length + " sets.");
        cache.printCache(); // Print cache contents for verification
    }

    // Placeholder for starting cache simulation (unchanged)
    public void startSimulation() {
        System.out.println("Simulation started with the given configuration.");
    }


}
