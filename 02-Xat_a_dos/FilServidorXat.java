import java.io.ObjectInputStream;

public class FilServidorXat implements Runnable {
    private ObjectInputStream input;

    public FilServidorXat(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while (!(missatge = (String) input.readObject()).equals(ServidorXat.MSG_SORTIR)) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}