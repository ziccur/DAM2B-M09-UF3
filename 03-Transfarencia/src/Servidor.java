import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private ServerSocket serverSocket;

    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> localhost:" + PORT);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
                gestionarClient(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gestionarClient(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            while (true) {
                String nomFitxer = (String) in.readObject();
                System.out.println("Esperant el nom del fitxer del client...");

                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
                    break;
                }

                Fitxer fitxer = new Fitxer(nomFitxer);
                byte[] dades = fitxer.getContingut();
                
                if (dades == null) {
                    out.writeObject(new byte[0]);
                    out.flush();
                } else {
                    out.writeObject(dades);
                    out.flush();
                    System.out.println("Fitxer enviat al client: " + nomFitxer);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}