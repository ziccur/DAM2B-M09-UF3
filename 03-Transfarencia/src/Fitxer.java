import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Fitxer {
    private String nom;
    
    public Fitxer(String nom) {
        this.nom = nom;
    }
    
    public byte[] getContingut() {
        try {
            Path path = Paths.get(nom);
            if (!Files.exists(path)) return null;
            byte[] data = Files.readAllBytes(path);
            System.out.println("Contingut del fitxer a enviar: " + data.length + " bytes");
            return data;
        } catch (IOException e) {
            return null;
        }
    }
}