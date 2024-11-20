public class Cache {
    public CacheSet[] sets; // Array of cache sets

    public Cache(int capacityKB, int blockSizeBytes, int associativity) {
        int numSets = (capacityKB * 1024) / (blockSizeBytes * associativity);
        sets = new CacheSet[numSets];
        for (int i = 0; i < numSets; i++) {
            sets[i] = new CacheSet(associativity, blockSizeBytes / 4);
        }
    }
}
