import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean running = true;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Client connectat a localhost:9999");
    }

    public void enviarMissatge(String missatge) throws IOException {
        output.writeObject(missatge);
        output.flush();
    }

    public void tancarClient() throws IOException {
        running = false;
        if (output != null) output.close();
        if (input != null) input.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Escriu el teu nom: ");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);
            
            FilLectorCX fil = new FilLectorCX(client.input);
            Thread thread = new Thread(fil);
            thread.start();
            
            while (client.running) {
                System.out.print("Missatge ('sortir' per tancar): ");
                String missatge = scanner.nextLine();
                
                if (missatge.equals("sortir")) {
                    client.enviarMissatge(missatge);
                    break;
                }
                
                client.enviarMissatge(missatge);
            }
            
            scanner.close();
            thread.join();
            client.tancarClient();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}