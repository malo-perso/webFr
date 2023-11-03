public class SellerThread implements Runnable {
    private ResourcePool resourcePool;
    private BalancingLogic balancingLogic;

    public SellerThread(ResourcePool resourcePool, BalancingLogic balancingLogic) {
        this.resourcePool = resourcePool;
        this.balancingLogic = balancingLogic;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int soldStones = sellStone();
                resourcePool.removeStones(soldStones);
                balancingLogic.balanceThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int sellStone() throws InterruptedException {
        // Simuler la vente de pierres (2 secondes)
        Thread.sleep(2000);
        return 1;
    }
}
