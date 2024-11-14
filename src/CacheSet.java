import java.util.LinkedList;
import java.util.Queue;

public class CacheSet {
    CacheLine[] lines;  // Array of cache lines
    Queue<CacheLine> lruQueue; // Queue for tracking LRU order

    // Constructor
    public CacheSet(int associativity, int blockSize) {
        lines = new CacheLine[associativity];
        lruQueue = new LinkedList<>();

        // Initialize each cache line and add it to the LRU queue
        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockSize); // Initialize each cache line
            lruQueue.add(lines[i]); // Initially, add each line to the LRU queue
        }
    }

    // Method to find a line by its tag
    public CacheLine findLineByTag(int tag) {
        for (CacheLine line : lines) {
            if (line.valid && line.tag == tag) {
                return line; // Return line if it matches the tag and is valid
            }
        }
        return null; // Return null if no line with the tag is found
    }

    // Method to get the least recently used (LRU) line for replacement
    public CacheLine getLRULine() {
        for (CacheLine line : lruQueue) {
            if (!line.valid) { // Prefer to replace invalid lines first
                return line;
            }
        }
        return lruQueue.peek(); // Return the LRU line (head of the queue)
    }

    // Method to replace a line with the least recently used line
    public void replaceLRULine(CacheLine newLine) {
        CacheLine lruLine = getLRULine();
        lruQueue.remove(lruLine); // Remove the LRU line from the queue

        // Set newLine properties into the LRU position
        lruLine.tag = newLine.tag;
        lruLine.valid = true;
        lruLine.dirty = newLine.dirty;
        System.arraycopy(newLine.data, 0, lruLine.data, 0, newLine.data.length);

        // Add it back to the queue as the most recently used line
        updateLRU(lruLine);
    }

    // Method to update LRU order when a line is accessed
    public void updateLRU(CacheLine accessedLine) {
        lruQueue.remove(accessedLine); // Remove from current position
        lruQueue.add(accessedLine); // Re-add it to the end to mark it as most recently used
    }
}
