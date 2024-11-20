public class CacheLine {
    public boolean valid;
    public boolean dirty;
    public int tag;
    public int lruCounter;
    public int[] data; // Block data (words)

    public CacheLine(int blockWords) {
        valid = false;
        dirty = false;
        tag = 0;
        lruCounter = 0;
        data = new int[blockWords];
    }
}
