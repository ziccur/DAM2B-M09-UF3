import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta() throws IOException {
        socket = new Socket("localhost", 9999);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        
        new Thread(() -> {
            try {
                while (!sortir) {
                    String missatge = (String) in.readObject();
                    String codi = Missatge.getCodiMissatge(missatge);
                    String[] parts = Missatge.getPartsMissatge(missatge);
                    
                    if (parts.length < 2) continue;
                    
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            System.out.println("Missatge personal de ["+parts[1]+"]: "+parts[2]);
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println("[GRUP] "+parts[1]);
                            break;
                    }
                }
            } catch (Exception e) {
                sortir = true;
            }
        }).start();
    }

    public void enviar(String missatge) throws IOException {
        out.writeObject(missatge);
    }

    public void tancar() {
        try {
            socket.close();
            sortir = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ClientXat client = new ClientXat();
        client.connecta();
        Scanner scanner = new Scanner(System.in);
        
        while (!client.sortir) {
            System.out.println("1. Nom\n2. Enviar privat\n3. Grup\n4. Sortir");
            String opcio = scanner.nextLine();
            
            switch (opcio) {
                case "1":
                    System.out.print("Nom: ");
                    client.enviar(Missatge.getMissatgeConectar(scanner.nextLine()));
                    break;
                case "2":
                    System.out.print("Destinatari: ");
                    String dest = scanner.nextLine();
                    System.out.print("Missatge: ");
                    client.enviar(Missatge.getMissatgePersonal(dest, scanner.nextLine()));
                    break;
                case "3":
                    System.out.print("Missatge grup: ");
                    client.enviar(Missatge.getMissatgeGrup(scanner.nextLine()));
                    break;
                case "4":
                    client.enviar(Missatge.getMissatgeSortirClient(""));
                    client.tancar();
                    break;
            }
        }
        scanner.close();
    }
}