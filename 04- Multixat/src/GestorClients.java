import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socket, ServidorXat servidor) throws IOException {
        this.client = socket;
        this.servidor = servidor;
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.in = new ObjectInputStream(client.getInputStream());
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                String missatgeRaw = (String) in.readObject();
                String codi = Missatge.getCodiMissatge(missatgeRaw);
                String[] parts = Missatge.getPartsMissatge(missatgeRaw);

                if (codi == null || parts == null) continue;

                switch (codi) {
                    case Missatge.CODI_CONECTAR:
                        nom = parts[1];
                        servidor.afegirClient(this); 
                        break;
                    case Missatge.CODI_SORTIR_CLIENT:
                        servidor.eliminarClient(nom);
                        sortir = true;
                        break;
                    case Missatge.CODI_SORTIR_TOTS:
                        servidor.finalitzarXat();
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        servidor.enviarMissatgePersonal(parts[1], nom, parts[2]); 
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        servidor.enviarMissatgeGrup(parts[1]);
                        break;
                }
            }
        } catch (Exception e) {
            servidor.eliminarClient(nom);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMissatge(String missatge) throws IOException {
        out.writeObject(missatge);
    }
}