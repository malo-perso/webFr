import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Crawler implements Runnable {
    private Queue<String> sitesAExplorer;
    private Queue<HashMap<String, LinkedList<String>>> urlFind = new LinkedList<>();
    private HashMap<String, LinkedList<String>> urlsVisitees;
    private static Pattern patternOrg = Pattern.compile("\\bhttps?://\\S+\\.org\\b");
    private static Pattern patternFr = Pattern.compile("\\bhttps?://\\S+\\.fr\\b");
    private static int nbCrawler = 0;

    public Crawler(Queue<String> sitesAExplorer, Queue<HashMap<String, LinkedList<String>>> urlFind,
            HashMap<String, LinkedList<String>> urlsVisitees) {
        this.sitesAExplorer = sitesAExplorer;
        this.urlFind = urlFind;
        this.urlsVisitees = urlsVisitees;
    }

    @Override
    public void run() {
        nbCrawler++;
        if (nbCrawler < 1) {
            try {
                // attendre 20 secondes avant de commencer le crawl
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // System.out.println("Thread Crawler" + nbCrawler + " nb sites" +
        // urlsVisitees.size() + " nb urlFind" + urlFind.size());
        while (!sitesAExplorer.isEmpty()) {
            String url = sitesAExplorer.poll();
            System.out.println("crawl " + url);
            // Logique pour crawler l'URL et extraire les liens
            LinkedList liens = crawlerUrl(url);

            synchronized (urlFind) {
                urlFind.add(new HashMap<String, LinkedList<String>>() {
                    {
                        put(url, liens);
                    }
                });
            }

        }

    }

    private LinkedList<String> crawlerUrl(String url) {
        String content = getContent(url);
        LinkedList<String> lUrlTrouve = new LinkedList<>();

        if (content == null) {
            // L'URL a généré une erreur, passez à l'URL suivante
            return lUrlTrouve;
        }
        Matcher matcher = patternFr.matcher(content);

        while (matcher.find()) {
            String foundUrl = matcher.group();
            System.out.println(foundUrl);
            lUrlTrouve.add(foundUrl);
        }

        // Afficher le nombre de nouveaux sites trouvés en temps réel
        return lUrlTrouve; // Remplacez par la logique réelle
    }

    private static String getContent(String url) {
        StringBuilder content = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("Erreur lors de la récupération de l'URL : " + url);
            return null; // Marquer l'URL comme non récupérable
        }
        return content.toString();
    }

}
