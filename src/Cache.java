public class Cache {
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
