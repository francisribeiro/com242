package View;

import App.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.InetAddress;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.setProperty("java.net.preferIPv4Stack" , "true"); // Desativa o IPV6
        String localhost = InetAddress.getLocalHost().toString().split("/", 2)[1]; // Endereço de localhost
        System.setProperty("jgroups.bind_addr" , localhost); // Para funcionar sem rede

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tela.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> {
            try {
                if (controller.getChat() != null) {
                    controller.getChat().sendMessage(controller.getNick().toUpperCase() + " foi desconectado do grupo!");
                    controller.getChat().disconect();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
