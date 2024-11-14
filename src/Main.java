public class Main {
    public static void main(String[] args) {
        int capacityKB = 0;
        int blockSizeBytes = 0;
        int associativity = 0;
        String traceFilePath = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-c")) {
                capacityKB = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-b")) {
                blockSizeBytes = Integer.parseInt(arg.substring(2));
            } else if (arg.startsWith("-a")) {
                associativity = Integer.parseInt(arg.substring(2));
            } else {
                // If it's not -c, -b, or -a, treat it as the trace file path
                traceFilePath = arg;
            }
        }

        CacheSim simulator = new CacheSim(capacityKB, blockSizeBytes, associativity);
        simulator.processTrace(traceFilePath); // Use the trace file path as a string here
    }
}
