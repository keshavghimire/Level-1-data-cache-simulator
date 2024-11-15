public class Cache {
    CacheSet[] sets;

    public Cache(int capacityKB, int blockSize, int associativity) {
        int numSets = (capacityKB * 1024) / (blockSize * associativity);
        sets = new CacheSet[numSets];

        for (int i = 0; i < numSets; i++) {
            sets[i] = new CacheSet(associativity, blockSize);
        }
    }

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
