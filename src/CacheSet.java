import java.util.LinkedList;
import java.util.Queue;

public class CacheSet {
    CacheLine[] lines;
    Queue<CacheLine> lruQueue;

    public CacheSet(int associativity, int blockSize) {
        lines = new CacheLine[associativity];
        lruQueue = new LinkedList<>();

        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockSize);
            lruQueue.add(lines[i]);
        }
    }

    public CacheLine findLineByTag(int tag) {
        for (CacheLine line : lines) {
            if (line.valid && line.tag == tag) {
                return line;
            }
        }
        return null;
    }

    public CacheLine getLRULine() {
        for (CacheLine line : lruQueue) {
            if (!line.valid) {
                return line;
            }
        }
        return lruQueue.peek();
    }

    public void updateLRU(CacheLine accessedLine) {
        lruQueue.remove(accessedLine);
        lruQueue.add(accessedLine);
    }
}
