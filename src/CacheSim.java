// main.java

public class CacheSim {

    // Cache parameters
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;


    // Main method: the entry point of the program
    public static void main(String[] args) {
        CacheSim simulator = new CacheSim();
        simulator.parseParams(args);

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


}
