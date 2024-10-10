 **Level 1 Data Cache Simulator** project:

### 1. **Command-Line Input:**
   - The cache simulator takes parameters like:
     - **Capacity (`-c`)**: Size of the cache in KB.
     - **Block size (`-b`)**: Size of each block in bytes.
     - **Associativity (`-a`)**: Number of blocks per set (e.g., 1 for direct-mapped, 4 for 4-way).

   Example:
   ```bash
   java cache_sim -c8 -b16 -a4
   ```

### 2. **Cache Organization:**
   - Cache is divided into sets, each containing a number of blocks.
   - **LRU (Least Recently Used)** replacement policy determines which block to evict when full.
   
### 3. **Cache Operations:**
   - **Load (0)**: Reads data from cache or main memory (if cache miss).
   - **Store (1)**: Writes data into the cache (write-back, write-allocate).
   - Uses **write-back** for write hits and **write-allocate** for write misses.

### 4. **Main Memory:**
   - Simulates 16MB memory, with each word initialized to its own address at startup.

### 5. **Simulation:**
   - Reads memory access traces (loads/stores), simulates cache behavior (hits/misses), and collects statistics like:
     - Total **misses** and **miss rate**.
     - **Dirty block evictions** (blocks written back to memory).

### 6. **Output:**
   - Shows statistics, cache contents, and specific regions of memory.

### 7. **Testing & Experiments:**
   - Test the simulator using provided trace files and verify output format.
   - Run experiments with different cache configurations to evaluate performance, identifying the best setup based on miss rate.

