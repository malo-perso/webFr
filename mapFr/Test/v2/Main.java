import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ResourcePool resourcePool = new ResourcePool(100); // Limite de pierres à miner
        BalancingLogic balancingLogic = new BalancingLogic(resourcePool);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 3; i++) {
            executor.execute(new MinerThread(resourcePool, balancingLogic));
        }

        for (int i = 0; i < 3; i++) {
            executor.execute(new SellerThread(resourcePool, balancingLogic));
        }
    }
}
