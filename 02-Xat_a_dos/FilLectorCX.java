import java.io.ObjectInputStream;

public class FilLectorCX implements Runnable {
    private ObjectInputStream input;

    public FilLectorCX(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = (String) input.readObject()) != null) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (Exception e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}