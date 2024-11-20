import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CacheSim {
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache; // Cache object
    private int[] mainMemory; // Simple main memory model

    // Statistics
    public static int dirtyEvictions = 0;
    private int totalAccesses = 0;
    private int readHits = 0;
    private int writeHits = 0;
    private int readMisses = 0;
    private int writeMisses = 0;

    public static void main(String[] args) {
        CacheSim simulator = new CacheSim();
        simulator.parseParams(args);
        simulator.initializeCache();
        simulator.loadTrace("trace.txt");

        try (PrintWriter writer = new PrintWriter("output.txt")) {
            simulator.printStatistics(writer);
            simulator.printCacheContents(writer);
            simulator.printMainMemory(writer);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }

    public void parseParams(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("-c")) {
                capacityKB = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-b")) {
                blockSizeBytes = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-a")) {
                associativity = Integer.parseInt(arg.substring(2));
            }
        }
    }

    public void initializeCache() {
        cache = new Cache(capacityKB, blockSizeBytes, associativity);
        mainMemory = new int[16 * 1024 * 1024 / 4]; // Initialize 16MB of main memory
        for (int i = 0; i < mainMemory.length; i++) {
            mainMemory[i] = i; // Memory content initialized to addresses
        }
    }

    public void loadTrace(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(" ");
                if (parts[0].equals("0")) { // Read operation
                    int address = Integer.parseInt(parts[1], 16);
                    readFromCache(address);
                } else if (parts[0].equals("1")) { // Write operation
                    int address = Integer.parseInt(parts[1], 16);
                    int data = Integer.parseInt(parts[2], 16);
                    writeToCache(address, data);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Trace file not found: " + filename);
        }
    }

    private void readFromCache(int address) {
        totalAccesses++;
        int tag = address / blockSizeBytes / cache.sets.length;
        int setIndex = (address / blockSizeBytes) % cache.sets.length;
        CacheSet set = cache.sets[setIndex];

        CacheLine line = set.findLine(tag);
        if (line != null) {
            readHits++;
            set.updateLRUCounters(line);
        } else {
            readMisses++;
            line = set.replaceLine(tag);
            loadFromMemory(address, line);
        }
    }

    private void writeToCache(int address, int data) {
        totalAccesses++;
        int tag = address / blockSizeBytes / cache.sets.length;
        int setIndex = (address / blockSizeBytes) % cache.sets.length;
        CacheSet set = cache.sets[setIndex];

        CacheLine line = set.findLine(tag);
        if (line != null) {
            writeHits++;
            set.updateLRUCounters(line);
        } else {
            writeMisses++;
            line = set.replaceLine(tag);
            loadFromMemory(address, line);
        }
        line.dirty = true;
        int wordIndex = (address % blockSizeBytes) / 4;
        line.data[wordIndex] = data;
    }

    private void loadFromMemory(int address, CacheLine line) {
        int baseAddress = (address / blockSizeBytes) * blockSizeBytes;
        for (int i = 0; i < line.data.length; i++) {
            line.data[i] = mainMemory[(baseAddress / 4) + i];
        }
    }

    public void printStatistics(PrintWriter writer) {
        writer.println("STATISTICS:");
        writer.println("Misses:");
        writer.printf("Total: %d DataReads: %d DataWrites: %d%n", (readMisses + writeMisses), readMisses, writeMisses);
        writer.println("Miss rate:");
        writer.printf("Total: %.6f DataReads: %.6f DataWrites: %.6f%n",
                (double) (readMisses + writeMisses) / totalAccesses,
                (double) readMisses / (readHits + readMisses),
                (double) writeMisses / (writeHits + writeMisses));
        writer.printf("Number of Dirty Blocks Evicted From the Cache: %d%n", dirtyEvictions);
        writer.println();
    }

    public void printCacheContents(PrintWriter writer) {
        writer.println("CACHE CONTENTS");
        writer.println("Set   V    Tag       Dirty    Word0      Word1      Word2      Word3      Word4      Word5      Word6      Word7");
        for (int i = 0; i < cache.sets.length; i++) {
            CacheSet set = cache.sets[i];
            for (CacheLine line : set.lines) {
                writer.printf("%-5d %-4d %-9s %-8d",
                        i,
                        line.valid ? 1 : 0,
                        line.valid ? String.format("%08x", line.tag) : "00000000",
                        line.dirty ? 1 : 0);
                for (int word : line.data) {
                    writer.printf(" %-10s", String.format("%08x", word));
                }
                writer.println();
            }
        }
        writer.println();
    }

    public void printMainMemory(PrintWriter writer) {
        writer.println("MAIN MEMORY:");
        writer.println("Address    Words");
        for (int i = 0; i < mainMemory.length; i += 8) {
            writer.printf("%08x", i * 4);
            for (int j = 0; j < 8 && (i + j) < mainMemory.length; j++) {
                writer.printf("   %-10s", String.format("%08x", mainMemory[i + j]));
            }
            writer.println();
        }
        writer.println();
    }
}
