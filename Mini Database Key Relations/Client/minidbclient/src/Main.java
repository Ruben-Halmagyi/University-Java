import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Main extends Application implements Initializable {

    JSONObject db = new JSONObject();
    ArrayList<JSONObject> arrayList=new ArrayList<>();
    JSONObject db_data = new JSONObject();

    void add(){
        JSONObject o1=new JSONObject();
        o1.append("db_name","szia");
        JSONObject o2=new JSONObject();
        o2.append("db_name","buzi");
        arrayList.add(o1);
        arrayList.add(o2);
        JSONArray jsonArray = new JSONArray(arrayList);
        db.append("databases",jsonArray);
    }


    //Menubar
    @FXML private TreeView<String> fa;
    @FXML private Button createdb_btn;
    @FXML private Button shutdownServer_btn;

    //Create DB
    @FXML private Pane createdb_panel;
    @FXML private Pane dropdb_panel;

    @FXML private TextField createDB_name;
    @FXML private Button createDB_submit;
    @FXML private Label errorLabel;

    //Drop DB
    @FXML private Pane dropDB_Pane;
    @FXML private Button dropDB_Button;
    @FXML private Label dropDB_ErrorLabel;
    @FXML private Label dropDB_Failed;
    @FXML private ChoiceBox<String> dropDB_ChoiceBox;

    private void createTreeView(){

    }


    @FXML
    private void updateErrorLabel(int error){
        switch(error){
            case 0:
                errorLabel.setText("Database name is already taken.");
                errorLabel.setVisible(true);
                break;
            case 1:
                errorLabel.setText("Database name can't contain only whitespaces!");
                errorLabel.setVisible(true);
                break;
            default:
                break;
        }
    }

    private DBServer dbServer = new DBServer("localhost",5555);
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainFrame.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        add();
    }

    @FXML
    void createDBClick(){
        createdb_panel.setVisible(true);
    }

    private boolean isBlank(String s){
        for (int i=0;i<s.length();i++) {
            if(s.charAt(i)!='\t' && s.charAt(i)!=' ' && s.charAt(i)!='\n')
                return false;
        }
        return true;
    }

    void getDBData(){
        JSONObject request = new JSONObject();
        request.put("type",Constants.GET_DATA);
        try {
            JSONObject result = dbServer.sendData(request);
            db_data = result.getJSONObject("data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String delete_whitespace_from_front(String s){
        while (s.charAt(0)=='\t' || s.charAt(0)==' ' || s.charAt(0)=='\n' )
            s=s.substring(1);
        return s;
    }

    @FXML
    void createDBSubmit(){
        String db_name=createDB_name.getText();
        if (isBlank(db_name)) {
            updateErrorLabel(1);
        }
        else{
            db_name = delete_whitespace_from_front(db_name);
            JSONObject obj = new JSONObject();
            obj.put("type",Constants.CREATE_DB);
            obj.put("db_name",db_name);
            try {
                dbServer.sendData(obj);
                //TODO: Check for errors
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void shutdownServer(){

    }

    void update_ChoiceBox(JSONObject result){

        dropDB_ChoiceBox.setValue("Select Database");

        JSONArray jsonArray = db_data.getJSONObject("data").getJSONArray("databases");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject dbname = (JSONObject) jsonArray.get(i);
            dropDB_ChoiceBox.getItems().add(dbname.getString("dbName"));
        }
    }

    @FXML
    void dropDB_Button_Click(){
        dropDB_Pane.setVisible(true);
    }

    @FXML
    void drop_selected_DB_ChoiceBox(){
        String choice = dropDB_ChoiceBox.getValue();
        if (isBlank(choice) || choice.equals("Select Database")) {
            dropDB_ErrorLabel.setVisible(true);
        }
        else{
            JSONObject obj = new JSONObject();
            obj.put("type",Constants.DROP_DB);
            obj.put("db_name",choice);
            try {
                JSONObject result = dbServer.sendData(obj);
                //TODO: Check for errors
                if(result.getInt("status") == Constants.OK){
                    getDBData();
                }
                else{
                    dropDB_Failed.setVisible(true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    void notDeleteDB(){
        dropdb_panel.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        add();
        getDBData();

        TreeItem<String> rootItem = new TreeItem<> ("Databases");
        rootItem.setExpanded(true);
        for (int i = 0; i < arrayList.size(); i++) {
            TreeItem<String> item = new TreeItem<> (arrayList.get(i).get("db_name").toString());
            TreeItem<String> table = new TreeItem<>("Tables");
            table.setExpanded(true);
            TreeItem<String> key = new TreeItem<>("Key");
            key.setExpanded(true);
            item.getChildren().add(table);
            item.getChildren().add(key);
            rootItem.getChildren().add(item);
        }
        fa.setRoot(rootItem);
    }
}
