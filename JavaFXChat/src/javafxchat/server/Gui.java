/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.server;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Bob
 */
public class Gui {
    
    private final ChatServerApplication app;
    private final Stage                 primaryStage;
    private final ListView<String>      userList     = new ListView<>();
    private final TextArea              area         = new TextArea();
    private final Text                  actionTarget = new Text();    
    
    public Gui(ChatServerApplication app, Stage primaryStage) {
        this.app          = app;
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chat Server");
        Parent root = getConnectViewGroup();
        primaryStage.setScene(new Scene(root, 750, 500));
        primaryStage.getScene().getStylesheets().add("Style/Style.css");
        primaryStage.show();
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void showConnectionScreen() {
        Parent root = getConnectViewGroup();
        primaryStage.setScene(new Scene(root, 750, 500));
        primaryStage.getScene().getStylesheets().add("Style/Style.css");
        primaryStage.show();
    }
    
    public void showChatScreen() {
        Parent root = getChatViewGroup();
        primaryStage.setScene(new Scene(root, 750, 500));
    }    
    
    private Parent getConnectViewGroup() {
    
        GridPane  grid         = new GridPane();
        Text      titleText    = new Text();
        Label     nameLabel    = new Label();
        Label     portLabel    = new Label();
        TextField nameEntry    = new TextField();
        TextField portEntry    = new TextField();
        Button    startButton  = new Button();
        
        titleText.setText("Dumb Chat Server");        
        nameLabel.setText("Server Name");
        portLabel.setText("Port");

        startButton.setText("Start Server");
        
        GridPane.setRowIndex(titleText, 0);
        GridPane.setColumnIndex(titleText, 1);
        
        titleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        titleText.setId("welcomeText");
        
        GridPane.setHalignment(titleText, HPos.CENTER);
        
        GridPane.setRowIndex(nameLabel, 4);
        GridPane.setColumnIndex(nameLabel, 0);
        
        GridPane.setRowIndex(nameEntry, 4);
        GridPane.setColumnIndex(nameEntry, 1);
        
        GridPane.setRowIndex(portLabel, 5);
        GridPane.setColumnIndex(portLabel, 0);
        
        GridPane.setRowIndex(portEntry, 5);
        GridPane.setColumnIndex(portEntry, 1);        
        
        GridPane.setRowIndex(startButton, 9);
        GridPane.setColumnIndex(startButton, 1);
        
        GridPane.setRowIndex(actionTarget, 11);
        GridPane.setColumnIndex(actionTarget, 1);        
        
        actionTarget.setId("actionTarget");
        
        GridPane.setHalignment(portLabel, HPos.RIGHT);
        GridPane.setHalignment(startButton, HPos.CENTER);
        GridPane.setHalignment(actionTarget, HPos.RIGHT);
        
        grid.setAlignment(Pos.CENTER);
        
        grid.setHgap(15);
        grid.setVgap(15);
        
        grid.setStyle("-fx-background-color: #C0C0C0;");
        
        grid.getChildren().addAll(titleText, nameLabel, nameEntry, portLabel, portEntry, startButton, actionTarget);
        
        startButton.setOnAction((ActionEvent event) -> {
            
            if (nameEntry.getText().equals("")) {
                actionTarget.setText("Please enter a server name");
                return;
            }
            
            if (portEntry.getText().equals("")) {
                actionTarget.setText("Please enter a port");
                return;
            }

            primaryStage.setTitle(nameEntry.getText());
            app.startServer(portEntry.getText());
            
            
        });
        
        return grid;
        
    }
    
    private Parent getChatViewGroup() {
        
        TextField field = new TextField();
        
        field.setOnKeyPressed((KeyEvent e) -> {
            
            if (e.getCode() == KeyCode.ENTER) {
                field.setText("");
            }
            
        });
        
        field.setPrefWidth(750);
        field.setPrefHeight(30);

        field.setLayoutX(0);
        field.setLayoutY(470);
        
        
        
        area.setPrefHeight(470);
        area.setPrefWidth(550);
        area.setEditable(false);
        
        area.setLayoutX(200);

        userList.setPrefHeight(470);
        userList.setPrefWidth(200);
        
        Group root = new Group(area, field, userList);
        return root;
        
    }   
    
    protected void addUser(String name) {
        userList.getItems().add(name);
    }
    
    protected void removeUser(String name) {
        userList.getItems().remove(name);
    }
    
    protected void writeMessage(String message) {
        area.appendText(message+"\n");
    }
    
    protected void alert(String alert) {
        actionTarget.setText(alert);
    }    
    
}
