import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new weatherappgui().setVisible(true);

                System.out.println(weatherapp.getCurrentTime());
            }
        });
    }
}
