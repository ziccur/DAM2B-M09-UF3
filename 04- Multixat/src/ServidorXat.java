import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String MSG_SORTIR = "sortir";
    private Hashtable<String, GestorClients> gestorClients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a localhost:" + PORT);
        while (!sortir) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
            GestorClients gestor = new GestorClients(clientSocket, this);
            new Thread(gestor).start();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        gestorClients.clear();
        sortir = true;
        pararServidor();
        System.exit(0);
    }

    public void afegirClient(GestorClients client) {
        gestorClients.put(client.getNom(), client);
        System.out.println(client.getNom() + " connectat.");
        String debugMsg = "DEBUG: multicast Entra: " + client.getNom();
        System.out.println(debugMsg);
        enviarMissatgeGrup(Missatge.getMissatgeGrup(debugMsg));
    }

    public void eliminarClient(String nom) {
        if (gestorClients.containsKey(nom)) {
            gestorClients.remove(nom);
            String debugMsg = "DEBUG: multicast Surt: " + nom;
            System.out.println(debugMsg);
            enviarMissatgeGrup(Missatge.getMissatgeGrup(debugMsg));
        }
    }

    public void enviarMissatgeGrup(String missatge) {
        gestorClients.forEach((nom, gestor) -> {
            try {
                gestor.enviarMissatge(missatge);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        GestorClients client = gestorClients.get(destinatari);
        if (client != null) {
            try {
                client.enviarMissatge(Missatge.getMissatgePersonal(remitent, missatge));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}