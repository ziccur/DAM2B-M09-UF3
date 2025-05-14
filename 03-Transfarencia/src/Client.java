import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp";
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
    }

    public void tancarConnexio() throws IOException {
        out.writeObject("sortir");
        socket.close();
    }

    public void rebreFitxers() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String nomFitxer = scanner.nextLine();
                
                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    tancarConnexio();
                    System.out.println("Sortint...");
                    break;
                }
                
                out.writeObject(nomFitxer);
                out.flush();
                
                byte[] dades = (byte[]) in.readObject();
                
                if (dades.length == 0) {
                    System.out.println("Error: Fitxer no trobat");
                    continue;
                }
                
                System.out.print("Nom del fitxer a guardar: ");
                String desti = scanner.nextLine();
                
                try (FileOutputStream fos = new FileOutputStream(DIR_ARRIBADA + File.separator + desti)) {
                    fos.write(dades);
                    System.out.println("Fitxer rebut i guardat com: " + desti);
                }
            }
        } catch (Exception e) {
            System.out.println("Error de connexió amb el servidor");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxers();
        } catch (IOException e) {
            System.out.println("Error de connexió");
        }
    }
}