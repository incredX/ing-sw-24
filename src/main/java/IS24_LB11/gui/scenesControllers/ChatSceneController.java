package IS24_LB11.gui.scenesControllers;

import IS24_LB11.gui.phases.GameGUIState;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ChatSceneController {


    @FXML private TextArea messageBox;
    @FXML private ImageView userImageView;
    @FXML
    ListView chatPane;
    @FXML ListView statusList;
    @FXML
    BorderPane borderPane;
    @FXML ComboBox statusComboBox;

    @FXML private Button recordBtn;
    @FXML
    private Text chatTextArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button buttonSend;
    @FXML
    private Button closeChatButton;
    GameGUIState state;
    Stage stage;
    public ChatSceneController(GameGUIState state, Stage stage){
        this.state=state;
        this.stage=stage;
    }
    @FXML
    public void initialize(){
        //chatTextArea.getChildren().add(new Text(state.getPersonalChat()));
        buttonSend.setOnMouseClicked(mouseEvent -> send());
        closeChatButton.setOnMouseClicked(mouseEvent -> exit(stage));
    }
    public void exit(Stage stage)  {
        stage.hide();
    }

    public void send(){
        String[] strings = messageBox.getText().split(" ",3);
        if(strings[0].equals("help")){
            chatPane.getItems().add(new Text("Private chat command:"));
            chatPane.getItems().add(new Text("sendto username msg"));
            chatPane.getItems().add(new Text(""));

            chatPane.getItems().add(new Text("Public chat command:"));
            chatPane.getItems().add(new Text("sendtoall msg"));
            chatPane.getItems().add(new Text(""));
        }
        if (strings[0].equals("sendto"))
            state.sendMessage(strings[1],state.getUsername() ,strings[2]);
        else if (strings[0].equals("sendtoall"))
            state.sendToAll(state.getUsername(),strings[1]);
    }

    public void updateMessages() {
        System.out.println(state.getPersonalChat());
    }

    public void addToChat(String msg){
        chatPane.getItems().add(new Text(msg));
    }
}
