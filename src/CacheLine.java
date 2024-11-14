public class CacheLine {
    int tag;
    boolean valid;
    boolean dirty;
    int[] data;

    public CacheLine(int blockSize) {
        this.tag = 0;
        this.valid = false;
        this.dirty = false;
        this.data = new int[blockSize / 4];
    }
}
