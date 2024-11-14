import java.io.*;

public class CacheSim {
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache;
    private int[] mainMemory;

    private int totalMisses = 0;
    private int readMisses = 0;
    private int writeMisses = 0;
    private int dirtyBlockEvictions = 0;

    public CacheSim(int capacityKB, int blockSizeBytes, int associativity) {
        this.capacityKB = capacityKB;
        this.blockSizeBytes = blockSizeBytes;
        this.associativity = associativity;

        // Initialize main memory with 16 MB (4M words)
        mainMemory = new int[16 * 1024 * 1024 / 4];
        for (int i = 0; i < mainMemory.length; i++) {
            mainMemory[i] = i; // Each word is initialized to its address
        }

        initializeCache();
    }

    private void initializeCache() {
        cache = new Cache(capacityKB, blockSizeBytes, associativity);
        System.out.println("Cache initialized with " + cache.sets.length + " sets.");
    }

    private void handleLoad(int address) {
        int index = calculateIndex(address);
        int tag = calculateTag(address);
        CacheSet set = cache.sets[index];
        CacheLine line = set.findLineByTag(tag);

        if (line != null && line.valid) {
            System.out.println("Load Hit: Address " + Integer.toHexString(address));
            updateLRU(set, line);
        } else {
            System.out.println("Load Miss: Address " + Integer.toHexString(address));
            CacheLine evictedLine = set.getLRULine();
            if (evictedLine.dirty) {
                writeBackToMemory(evictedLine);
                dirtyBlockEvictions++;
            }
            loadFromMemory(address, evictedLine, tag);
            updateLRU(set, evictedLine);
            totalMisses++;
            readMisses++;
        }
    }

    private void handleStore(int address, int data) {
        int index = calculateIndex(address);
        int tag = calculateTag(address);
        CacheSet set = cache.sets[index];
        CacheLine line = set.findLineByTag(tag);

        if (line != null && line.valid) {
            System.out.println("Store Hit: Address " + Integer.toHexString(address));
            line.data[address % blockSizeBytes / 4] = data;
            line.dirty = true;
            updateLRU(set, line);
        } else {
            System.out.println("Store Miss: Address " + Integer.toHexString(address));
            CacheLine evictedLine = set.getLRULine();
            if (evictedLine.dirty) {
                writeBackToMemory(evictedLine);
                dirtyBlockEvictions++;
            }
            loadFromMemory(address, evictedLine, tag);
            evictedLine.data[address % blockSizeBytes / 4] = data;
            evictedLine.dirty = true;
            updateLRU(set, evictedLine);
            totalMisses++;
            writeMisses++;
        }
    }

    private void writeBackToMemory(CacheLine line) {
        int baseAddress = line.tag * cache.sets.length * blockSizeBytes;
        for (int i = 0; i < line.data.length; i++) {
            mainMemory[(baseAddress + i * 4) / 4] = line.data[i];
        }
        line.dirty = false;
    }

    private void loadFromMemory(int address, CacheLine line, int tag) {
        int baseAddress = address - (address % blockSizeBytes);
        line.tag = tag;
        line.valid = true;
        for (int i = 0; i < line.data.length; i++) {
            line.data[i] = mainMemory[(baseAddress + i * 4) / 4];
        }
    }

    private void updateLRU(CacheSet set, CacheLine accessedLine) {
        set.updateLRU(accessedLine);
    }

    private int calculateIndex(int address) {
        int numSets = cache.sets.length;
        return (address / blockSizeBytes) % numSets;
    }

    private int calculateTag(int address) {
        int numSets = cache.sets.length;
        return (address / blockSizeBytes) / numSets;
    }

    public void printStatistics() {
        double missRate = (double) totalMisses / (totalMisses + readMisses + writeMisses);
        double readMissRate = (double) readMisses / (readMisses + totalMisses);
        double writeMissRate = (double) writeMisses / (writeMisses + totalMisses);

        System.out.printf("STATISTICS:\n");
        System.out.printf("Misses: %d %d %d\n", totalMisses, readMisses, writeMisses);
        System.out.printf("Miss Rate: %.4f %.4f %.4f\n", missRate, readMissRate, writeMissRate);
        System.out.printf("Number of Dirty Blocks Evicted From the Cache: %d\n", dirtyBlockEvictions);
        
        cache.printCache();
        printMemoryContents();
    }

    public void printMemoryContents() {
        int startAddress = 0x003F7F00;
        System.out.println("MAIN MEMORY:");
        for (int i = 0; i < 1024; i += 8) {
            System.out.printf("%08X: ", startAddress + i * 4);
            for (int j = 0; j < 8; j++) {
                System.out.printf("%08X ", mainMemory[(startAddress / 4) + i + j]);
            }
            System.out.println();
        }
    }

    public void processTrace(String traceFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(traceFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String command = parts[0];
                int address = Integer.parseInt(parts[1].substring(2), 16); // Parse hex address
                
                if (command.equals("LOAD")) {
                    handleLoad(address);
                } else if (command.equals("STORE")) {
                    // Ensure the STORE command has the data word (i.e., three parts in total)
                    if (parts.length < 3) {
                        System.out.println("Error: STORE command is missing the data word.");
                        continue;  // Skip this line and move to the next one
                    }
                    int data = Integer.parseInt(parts[2].substring(2), 16); // Parse hex data for STORE
                    handleStore(address, data);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading trace file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
        }
    }
    
    
}
