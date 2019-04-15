/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.server;

import fatwalrus.network.ClientConnection;
import fatwalrus.server.Server;
import java.net.BindException;
import java.util.HashMap;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 *
 * @author Bob
 */
public class ChatServerApplication extends Application {

    private Server server;
    private Gui    gui;
    
    private final HashMap<String, String> users = new HashMap<>();
    
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
    public void stop(){
        if (server != null)
            server.stop();
        System.out.println("Good Bye!");
    }    
    
    protected void startServer(String portString) {
        
        try {
            
            ChatServerApplication app = this;
            
            int port = -1;
            
            try {
                port   = Integer.valueOf(portString);
            }

            catch (NumberFormatException e) {
                Platform.runLater(() -> gui.alert("Port must be a number"));
                return;
            }            
            
            server = new Server(port, true) {
                
                @Override
                public void onClientConnected(ClientConnection cc) {
                    ChatConnectionCommandRegistry cr = new ChatConnectionCommandRegistry(app);
                    cr.setClientConnection(cc);
                    cc.registerCommands(cr);
                    cc.sendMessage("READY".getBytes());
                }
                
                @Override
                public void onClientDisconnected(ClientConnection cc, String reason) {
                    
                    final String id       = cc.getId();
                    final String username = users.get(cc.getId());
                    
                    System.out.println(id + " Disconnected: " + reason);
                    
                    Platform.runLater(()->gui.removeUser(username));
                    Platform.runLater(()->gui.writeMessage(username + " has left: " + reason));
                    
                    users.remove(id);
                    server.broadcastMessage("EXCON_" + username + "_" + reason);
                    
                }
                
            };
            
            server.registerServerCommands(new ChatServerCommandRegistry(this));
            server.start();
            Platform.runLater(()->gui.showChatScreen());
        }
        
        catch(Exception e) {
            
            Platform.runLater(()->gui.showConnectionScreen());
            if (server != null) server.stop();
            
            if (e instanceof BindException) {
                Platform.runLater(()->gui.alert("Address already in use"));
            }
            else {
                Platform.runLater(()->gui.alert("Error starting server"));
                e.printStackTrace();
            }
            
        }
        
        
    }
    
    protected Server getServer() {
        return server;
    }
    
    protected HashMap<String, String> getUsers() {
        return users;
    }
    
    protected Gui getGui() {
        return gui;
    }
    
}
