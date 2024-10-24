public class Main {
    public static void main(String[] args) {
        CacheSim simulator = new CacheSim();
        simulator.parseParams(args);
        simulator.initializeCache();
//        simulator.processTrace();
    }
}