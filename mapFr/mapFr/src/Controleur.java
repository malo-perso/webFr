import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controleur {

    public static void main(String[] args) {

        // début
        System.out.println("Début du programme");

        Queue<String> sitesAExplorer = new LinkedList<>();
        Queue<HashMap<String, LinkedList<String>>> urlFind = new LinkedList<>();
        HashMap<String, LinkedList<String>> urlsVisitees = new HashMap<>();

        // URL de départ
        // sitesAExplorer.add("https://www.example.com");
        // sitesAExplorer.add("https://fr.wikipedia.org/wiki/Wikip%C3%A9dia");
        // sitesAExplorer.add("https://www.service-public.fr");
        sitesAExplorer.add("https://www.lemonde.fr/");

        ExecutorService executorCrawler = Executors.newFixedThreadPool(2);
        ExecutorService executorTriUrl = Executors.newFixedThreadPool(1);

        // condition ne marche pas
        while (!sitesAExplorer.isEmpty() && urlFind.size() < 1 || urlsVisitees.size() < 1000) {
            executorCrawler.execute(new Crawler(sitesAExplorer, urlFind, urlsVisitees));
            executorTriUrl.execute(new TriUrl(sitesAExplorer, urlFind, urlsVisitees));
        }

        // Attendre que les threads se terminent
        while (!executorCrawler.isTerminated() || !executorTriUrl.isTerminated()) {
            // Attendre
        }

        // Attendre que les threads se terminent
        executorCrawler.shutdown();
        executorTriUrl.shutdown();

        try {
            executorCrawler.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            executorTriUrl.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // fin
        System.out.println("Fin du programme");
    }
}
