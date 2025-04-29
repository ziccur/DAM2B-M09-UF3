import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean running = true;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        running = false;
        if (output != null) output.close();
        if (input != null) input.close();
        if (clientSocket != null) clientSocket.close();
        if (serverSocket != null) serverSocket.close();
        System.out.println("Servidor aturat.");
    }

    public String getNom(ObjectInputStream input) throws IOException, ClassNotFoundException {
        return (String) input.readObject();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();
            servidor.clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + servidor.clientSocket.getRemoteSocketAddress());
            servidor.output = new ObjectOutputStream(servidor.clientSocket.getOutputStream());
            servidor.input = new ObjectInputStream(servidor.clientSocket.getInputStream());
            
            String nom = servidor.getNom(servidor.input);
            System.out.println("Nom rebut: " + nom);
            
            FilServidorXat fil = new FilServidorXat(servidor.input);
            Thread thread = new Thread(fil);
            thread.start();
            System.out.println("Fil de xat creat.");
            
            Scanner scanner = new Scanner(System.in);
            while (servidor.running) {
                System.out.print("Missatge ('sortir' per tancar): ");
                String missatge = scanner.nextLine();
                
                if (missatge.equals(MSG_SORTIR)) {
                    servidor.output.writeObject(missatge);
                    servidor.output.flush();
                    break;
                }
                
                try {
                    servidor.output.writeObject(missatge);
                    servidor.output.flush();
                } catch (SocketException e) {
                    System.out.println("Client desconnectat.");
                    break;
                }
            }
            
            scanner.close();
            thread.join();
            servidor.pararServidor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}