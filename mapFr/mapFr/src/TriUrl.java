import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

class TriUrl implements Runnable {
    private Queue<String> sitesAExplorer;
    private Queue<HashMap<String, LinkedList<String>>> urlFind = new LinkedList<>();
    private HashMap<String, LinkedList<String>> urlsVisitees;
    private int nbTri;

    public TriUrl(Queue<String> sitesAExplorer, Queue<HashMap<String, LinkedList<String>>> urlFind,
            HashMap<String, LinkedList<String>> urlsVisitees) {
        this.sitesAExplorer = sitesAExplorer;
        this.urlFind = urlFind;
        this.urlsVisitees = urlsVisitees;
        this.nbTri = 0;
    }

    // changer la fonction de recherche et de tri

    public void run() {
        System.out.println("Thread de tri des URL");
        while (true) {
            // Vérifiez si l'URL a déjà été visité
            HashMap<String, LinkedList<String>> url = urlFind.poll();
            System.out.println(url);
            if (url != null) {
                // parcourir la liste des liens de l'url
                for (LinkedList<String> lUrl : url.values()) {
                    // parcourir la liste des liens de l'url
                    for (String s : lUrl) {
                        // vérfier si le lien est dans la liste des sites visités
                        if (!urlsVisitees.containsKey(s)) {
                            System.out.println("url pas visités : " + s);
                            // Ajouter l'URL à la liste des sites visités
                            urlsVisitees.put(s, lUrl);
                            sortieFichier(urlsVisitees, "urlsVisitees.json");

                        } else {
                            System.out.println("url déjà visitée : " + s);
                        }
                    }
                    nbTri++;
                }

            } else {
                // La liste "urlFind" est vide, donc attendez un moment avant de vérifier à
                // nouveau.
                try {
                    Thread.sleep(1000); // Attendez 1 seconde (ajustez la durée en fonction de vos besoins)
                    sortieFichier(urlsVisitees, "urlsVisitees.json");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortieFichier(HashMap<String, LinkedList<String>> urlsVisitees, String nomFichier) {
        // Écrire la HashMap dans un fichier JSON
        String json = "{ \n";
        for (String key : urlsVisitees.keySet()) {
            json += "  \"" + key + "\": [\n";
            LinkedList<String> lUrl = urlsVisitees.get(key);
            for (String url : lUrl) {
                json += "    \"" + url + "\",\n";
            }
            json = json.substring(0, json.length() - 2) + "\n";
            json += "  ],\n";
        }
        json = json.substring(0, json.length() - 2) + "\n"; // Enlever la dernière virgule
        json += "}\n";

        try {
            FileWriter fileWriter = new FileWriter("urlsVisitees" + nbTri + ".json   ");
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}