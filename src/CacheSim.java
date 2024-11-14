import java.io.*;

public class CacheSim {
    private int capacityKB;
    private int blockSizeBytes;
    private int associativity;
    private Cache cache;
    private int[] mainMemory;

    // Parameterized Constructor
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

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.out.println("Usage: java CacheSim -c<capacity> -b<blocksize> -a<associativity> <trace_file>");
            return;
        }

        // Parse command-line arguments
        int capacityKB = Integer.parseInt(args[0].substring(2));
        int blockSizeBytes = Integer.parseInt(args[1].substring(2));
        int associativity = Integer.parseInt(args[2].substring(2));
        
        // Initialize the simulator with provided parameters
        CacheSim simulator = new CacheSim(capacityKB, blockSizeBytes, associativity);

        // Process the trace file
        simulator.processTrace(args[3]);


        // Print final statistics and cache contents
        simulator.printStatistics();
    }

    // Initialize the cache based on the given parameters
    private void initializeCache() {
        cache = new Cache(capacityKB, blockSizeBytes, associativity);
        System.out.println("Cache initialized with " + cache.sets.length + " sets.");
        cache.printCache();
    }

    // Process the trace file and apply cache operations
    // Handle a load operation

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
            }
            loadFromMemory(address, evictedLine, tag);
            updateLRU(set, evictedLine);
        }
    }

    // Handle a store operation
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
            }
            loadFromMemory(address, evictedLine, tag);
            evictedLine.data[address % blockSizeBytes / 4] = data;
            evictedLine.dirty = true;
            updateLRU(set, evictedLine);
        }
    }

    // Write back the contents of a dirty cache line to main memory
    private void writeBackToMemory(CacheLine line) {
        int baseAddress = line.tag * cache.sets.length * blockSizeBytes;
        for (int i = 0; i < line.data.length; i++) {
            mainMemory[(baseAddress + i * 4) / 4] = line.data[i];
        }
        line.dirty = false;
    }

    // Load a cache line from main memory
    private void loadFromMemory(int address, CacheLine line, int tag) {
        int baseAddress = address - (address % blockSizeBytes);
        line.tag = tag;
        line.valid = true;
        for (int i = 0; i < line.data.length; i++) {
            line.data[i] = mainMemory[(baseAddress + i * 4) / 4];
        }
    }

    // Update the LRU order for a cache set
    private void updateLRU(CacheSet set, CacheLine accessedLine) {
        set.updateLRU(accessedLine);
    }

    // Calculate the cache index from the address
    private int calculateIndex(int address) {
        int numSets = cache.sets.length;
        return (address / blockSizeBytes) % numSets;
    }

    // Calculate the cache tag from the address
    private int calculateTag(int address) {
        int numSets = cache.sets.length;
        return (address / blockSizeBytes) / numSets;
    }

    // Print final statistics and cache contents
    public void printStatistics() {
        System.out.println("Simulation complete. Final cache and memory contents:");
        cache.printCache();
        // Optionally, print out main memory contents if needed
    }
    public void processTrace(String traceFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(traceFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String command = parts[0];
                String addressString = parts[1];
                int address = Integer.parseInt(addressString.substring(2), 16); // Convert hex address to int
                
                if (command.equals("LOAD")) {
                    load(address);
                } else if (command.equals("STORE")) {
                    store(address);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading trace file: " + e.getMessage());
        }
    }
    

    public void load(int address) {
        System.out.println("Loading address: " + Integer.toHexString(address));
        // Load logic here
    }

    public void store(int address) {
        System.out.println("Storing address: " + Integer.toHexString(address));
        // Store logic here
    }}
