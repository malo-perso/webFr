import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinerAndSellerBalanced {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int stoneCount = 0;
        int minerCount = 3;
        int sellerCount = 3;
        int earnings = 0;
        final int targetEarnings = 100;
        final int maxThreads = 10;

        int minMiners = 3; // Nombre minimal de mineurs
        int maxMiners = maxThreads / 3; // Nombre maximal de mineurs

        while (earnings < targetEarnings) {
            // Affichage du tableau
            System.out.println("+------+---------------+-----------------+------------------+");
            System.out.println("| Gain | Pierres dispo |  Thread mineur  |  Thread vendeur  |");
            System.out.println("+------+---------------+-----------------+------------------+");
            System.out.println("|" + String.format("%6d", earnings) + "|" + String.format("%15d", stoneCount)
                    + "|" + String.format("%17d", minerCount)
                    + "|" + String.format("%18d", sellerCount) + "|");
            System.out.println("+------+---------------+-----------------+------------------+");

            // Rééquilibrage du nombre de threads
            if (stoneCount >= 10 && minerCount < maxMiners) {
                System.out.println("Nouveau thread mineur ajouté.");
                executor.execute(() -> {
                    mineStone();
                });
                minerCount++;
            } else if (stoneCount > 5 && sellerCount < maxThreads - minMiners) {
                System.out.println("Nouveau thread vendeur ajouté.");
                executor.execute(() -> {
                    sellStone();
                });
                sellerCount++;
            }

            // Simuler la production de pierres
            stoneCount += 1;

            // Simuler la vente de pierres
            if (stoneCount > 0) {
                sellStone();
                stoneCount--;
            }

            // Simuler la génération de gains
            earnings += 10; // Par exemple, 10 unités gagnées à chaque vente

            // Attendre un certain temps
            try {
                Thread.sleep(1000); // 1 seconde
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Attendre que les threads en cours se terminent
        executor.shutdown();
    }

    private static void mineStone() {
        try {
            // Simuler l'extraction de pierres (1 seconde)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sellStone() {
        try {
            // Simuler la vente de pierres (2 secondes)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
