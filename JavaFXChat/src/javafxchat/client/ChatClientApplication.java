/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.client;

import fatwalrus.client.Client;
import java.net.UnknownHostException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *
 * @author Bob
 */
public class ChatClientApplication extends Application {

    private Gui    gui;
    private Client client;
    private String username;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        gui = new Gui(this, primaryStage);
    }
    
    @Override
    public void stop() {
        if(client != null)
            client.stop();
    }
    
    protected void startClient(String userName, String address) {
        
        if (!address.contains(":")) {
            Platform.runLater(() -> gui.alert("Address format: <ip>:<port>"));
            return;
        }
                
        String ip     = address.split(":")[0];
        this.username = userName;
        int port      = -1;
        
        try {
            port   = Integer.valueOf(address.split(":")[1]);
        }
        
        catch (NumberFormatException e) {
            Platform.runLater(() -> gui.alert("Port must be a number"));
            return;
        }

        try {
            
            client = new Client(ip, port, true) {
                
                @Override
                public void onServerStop() {
                    Platform.runLater(() -> gui.showConnectionScreen());
                    Platform.runLater(() -> gui.alert("Server has been stopped"));
                }
                
                @Override 
                public void onConnectionLost() {
                    Platform.runLater(() -> gui.showConnectionScreen());
                    Platform.runLater(() -> gui.alert("Lost connection to server"));
                }
                
                @Override
                public void onConnectFailed() {
                    Platform.runLater(() -> gui.alert("Cannot connect to server"));
                }
                
            };
            
            client.RegisterCommands(new ChatClientCommandRegistry(this));
            client.start();
        }
        
        catch (Exception e) {
            
            if (e instanceof UnknownHostException) {
                Platform.runLater(() -> gui.alert("Unknown Host: " + ip));
            }
            
            else {
                e.printStackTrace();
            }
            
        }
        
    }
    
    protected void sendChat(String message) {
        String send = "CHAT_" + message;
        client.sendMessage(send.getBytes());
    }    
    
    protected Client getClient() {
        return client;
    }
    
    protected Gui getGui() {
        return gui;
    }
    
    protected String getUsername() {
        return username;
    }
    
}
