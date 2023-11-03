public class ResourcePool {
    private int maxStonesToMine;
    private int stoneCount;

    public ResourcePool(int maxStonesToMine) {
        this.maxStonesToMine = maxStonesToMine;
        this.stoneCount = 0;
    }

    public synchronized void addStones(int stones) {
        stoneCount += stones;
        notifyAll();
    }

    public synchronized void removeStones(int stones) {
        while (stoneCount < stones) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stoneCount -= stones;
    }
}
