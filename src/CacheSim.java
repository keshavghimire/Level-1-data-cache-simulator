import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class CacheSim {
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache; // Cache object
    private int[] mainMemory; // Simple main memory model
    public static int dirtyEvictions = 0; // Number of dirty evictions
    public static int readHits = 0; // Read hits
    public static int readMisses = 0; // Read misses
    public static int writeHits = 0; // Write hits
    public static int writeMisses = 0; // Write misses
    public static int totalAccesses = 0; // Total accesses

    // Constructor to initialize the CacheSim with capacity, block size, and associativity
    public CacheSim(int capacityKB, int blockSizeBytes, int associativity) {
        this.capacityKB = capacityKB;
        this.blockSizeBytes = blockSizeBytes;
        this.associativity = associativity;
        this.cache = new Cache(capacityKB, blockSizeBytes, associativity);
        this.mainMemory = new int[16 * 1024 * 1024 / 4]; // Initialize 16MB of main memory
        // Initialize main memory with some values (just the addresses for simplicity)
        for (int i = 0; i < mainMemory.length; i++) {
            mainMemory[i] = i;
        }
    }

    public static void main(String[] args) {
        // Parsing input parameters from the command line or GUI
        int capacityKB = 8; // default 8KB
        int blockSizeBytes = 16; // default 16 bytes
        int associativity = 4; // default 4-way set associative

        // Parsing arguments
        for (String arg : args) {
            if (arg.startsWith("-c")) {
                capacityKB = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-b")) {
                blockSizeBytes = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-a")) {
                associativity = Integer.parseInt(arg.substring(2));
            }
        }

        // Initialize the CacheSim with user-specified values
        CacheSim simulator = new CacheSim(capacityKB, blockSizeBytes, associativity);

        // Load and process a trace file
        simulator.loadTrace("/Users/keshavghimire/Master/Comp Arc/Level-1-data-cache-simulator/src/trace.txt");

        // Print the simulation results to output.txt
        try {
            String outputFilePath = "src/test.output"; // Specify path to src folder
            simulator.writeSimulationResultsToFile(outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }

    // Setters and getters for the cache properties
    public void setCapacityKB(int capacityKB) {
        this.capacityKB = capacityKB;
    }

    public void setBlockSizeBytes(int blockSizeBytes) {
        this.blockSizeBytes = blockSizeBytes;
    }

    public void setAssociativity(int associativity) {
        this.associativity = associativity;
    }

    public Cache getCache() {
        return this.cache;
    }

    public void initializeCache() {
        this.cache = new Cache(capacityKB, blockSizeBytes, associativity);
    }

    // Parse trace file for cache operations
    public void loadTrace(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(" ");
                int address = Integer.parseInt(parts[1], 16);
                if (parts[0].equals("0")) { // Read operation
                    readFromCache(address);
                } else if (parts[0].equals("1")) { // Write operation
                    int data = Integer.parseInt(parts[2], 16);
                    writeToCache(address, data);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Trace file not found: " + filename);
        }
    }

    // Read data from cache
    private void readFromCache(int address) {
        totalAccesses++;
        int tag = address / blockSizeBytes / cache.sets.length;
        int setIndex = (address / blockSizeBytes) % cache.sets.length;
        CacheSet set = cache.sets[setIndex];

        CacheLine line = set.findLine(tag);
        if (line != null) {
            readHits++;
            System.out.println("Read hit at address: " + Integer.toHexString(address));
        } else {
            readMisses++;
            System.out.println("Read miss at address: " + Integer.toHexString(address));
            line = set.replaceLine(tag);
            loadFromMemory(address, line);
        }
    }

    // Write data to cache
    private void writeToCache(int address, int data) {
        totalAccesses++;
        int tag = address / blockSizeBytes / cache.sets.length;
        int setIndex = (address / blockSizeBytes) % cache.sets.length;
        CacheSet set = cache.sets[setIndex];

        CacheLine line = set.findLine(tag);
        if (line != null) {
            writeHits++;
            System.out.println("Write hit at address: " + Integer.toHexString(address));
        } else {
            writeMisses++;
            System.out.println("Write miss at address: " + Integer.toHexString(address));
            line = set.replaceLine(tag);
            loadFromMemory(address, line);
        }
        line.dirty = true;
        int wordIndex = (address % blockSizeBytes) / 4; // Assume 4 bytes per word
        line.data[wordIndex] = data;
    }

    // Load data into cache from memory
    private void loadFromMemory(int address, CacheLine line) {
        int baseAddress = (address / blockSizeBytes) * blockSizeBytes;
        for (int i = 0; i < line.data.length; i++) {
            line.data[i] = mainMemory[(baseAddress / 4) + i];
        }
    }

    // Get statistics from the cache simulation
    public String getSimulationResults() {
        StringBuilder results = new StringBuilder();

        // STATISTICS Section
        double totalMissRate = (double) (readMisses + writeMisses) / totalAccesses;
        double readMissRate = (double) readMisses / (readHits + readMisses);
        double writeMissRate = (double) writeMisses / (writeHits + writeMisses);
        results.append("STATISTICS:\n");
        results.append("Misses:\n");
        results.append(String.format("Total: %d DataReads: %d DataWrites: %d\n",
                readMisses + writeMisses, readMisses, writeMisses));
        results.append("Miss rate:\n");
        results.append(String.format("Total: %.4f DataReads: %.4f DataWrites: %.4f\n",
                totalMissRate, readMissRate, writeMissRate));
        results.append(String.format("Number of Dirty Blocks Evicted From the Cache: %d\n", dirtyEvictions));

        // CACHE CONTENTS Section
        results.append("\nCACHE CONTENTS:\n");
        results.append("Set   V   Tag       Dirty   Word0      Word1      Word2      Word3      Word4      Word5      Word6      Word7   \n");
        for (int i = 0; i < cache.sets.length; i++) {
            CacheSet set = cache.sets[i];
            for (CacheLine line : set.lines) {
                if (!line.valid) continue;  // Skip invalid lines
                results.append(String.format("%-5d %-3d %-10s %-7d",
                        i, line.valid ? 1 : 0, String.format("%08x", line.tag), line.dirty ? 1 : 0));
                for (int word : line.data) {
                    results.append(String.format(" %-10s", String.format("%08x", word)));
                }
                results.append("\n");
            }
        }

        // MAIN MEMORY Section
        results.append("\nMAIN MEMORY:\n");
        results.append("Address    Words\n");
        int startAddress = 0x003f7f00 / 4;
        for (int i = startAddress; i < startAddress + 1024; i += 8) {
            results.append(String.format("%08x   ", i * 4));
            for (int j = 0; j < 8; j++) {
                results.append(String.format("%08x   ", mainMemory[i + j]));
            }
            results.append("\n");
        }

        return results.toString();
    }

    // Method to write simulation results to an output file
    public void writeSimulationResultsToFile(String filename) throws IOException {
        String results = getSimulationResults();

        // Create or overwrite the file with the given name
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(results);
            System.out.println("Simulation results have been written to " + filename);
        }
    }
}
