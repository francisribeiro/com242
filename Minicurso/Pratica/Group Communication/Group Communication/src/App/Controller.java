package App;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;


public class Controller implements Initializable, Serializable {

    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private TextField nickname;
    @FXML
    private VBox vBoxGroupSelection;
    @FXML
    private Pane paneGroups;
    @FXML
    private Pane paneChatArea;
    @FXML
    private Label labelChatName;

    private Chat chat;
    private ArrayList<Group> groups;
    private ArrayList<Button> groupButtons;
    private String nick;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groups = new ArrayList<>();

        groups.add(new Group("SD na veia (PASS: 123)", "123"));
        groups.add(new Group("Futebol Boladão (PASS: 123)", "123"));
        groups.add(new Group("18+ [boletos, decepções e afins] (PASS: 123)", "123"));
        groups.add(new Group("Xuxa só para Baixinhos (PASS: 123)", "123"));

        createGroupList();
    }

    private void createGroupList() {
        groupButtons = new ArrayList<>();
        vBoxGroupSelection.getChildren().clear();

        for (Group group : groups) {
            Button button = new Button(group.getName());
            button.setMinSize(252, 40);

            button.setOnAction((event) -> {
                try {
                    joinGroup(button);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            });

            groupButtons.add(button);
            vBoxGroupSelection.getChildren().add(button);
        }
    }

    @FXML
    private void joinGroup(Button button) throws Exception {
        if (!nickname.getText().isEmpty()) {
            nick = nickname.getText();
            textArea.setText("");

            for (int i = 0; i < groupButtons.size(); i++) {
                if (button.equals(groupButtons.get(i))) {
                    JPasswordField password = new JPasswordField();

                    int passwordBox = JOptionPane.showConfirmDialog(
                            null, password, "Senha do Grupo",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (passwordBox == JOptionPane.OK_OPTION) {
                        String typedPassword = new String(password.getPassword());

                        if (typedPassword.equals(groups.get(i).getPassword())) {
                            paneChatArea.setDisable(false);
                            paneGroups.setDisable(true);
                            labelChatName.setText("Chat: " + groupButtons.get(i).getText());
                            chat = new Chat(this);
                            chat.start(groups.get(i).getName());
                            chat.sendMessage(nick.toUpperCase() + " entrou no Grupo!");
                        } else
                            JOptionPane.showMessageDialog(password, "Senha errada!");
                    }
                }
            }
        } else
            JOptionPane.showMessageDialog(null, "Informe um apelido!", "Erro ao entrar no grupo", JOptionPane.ERROR_MESSAGE);
    }

    @FXML
    private void disconect() throws Exception {
        paneGroups.setDisable(false);
        paneChatArea.setDisable(true);
        labelChatName.setText("Chat: Nenhum");
        chat.sendMessage(nick.toUpperCase() + " saiu do grupo!");
        textArea.setText("");
        chat.disconect();
        chat = null;
    }

    @FXML
    private void send() throws Exception {
        String msg = textField.getText();

        if (!msg.isEmpty())
            chat.sendMessage(nick.toUpperCase() + ": " + msg);

        textField.setText("");
    }

    public Chat getChat() {
        return chat;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public String getNick() {
        return nick;
    }
}
