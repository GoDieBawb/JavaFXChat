/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.server;

import fatwalrus.commands.CommandRegistry;
import fatwalrus.network.ClientConnection;
import java.util.Arrays;
import javafx.application.Platform;

/**
 *
 * @author Bob
 */
public class ChatConnectionCommandRegistry extends CommandRegistry {

    private final ChatServerApplication app;
    private       ClientConnection      cc;
    
    public ChatConnectionCommandRegistry(ChatServerApplication app) {
        this.app = app;
        commands.add("JOIN");
        commands.add("CHAT");
        commands.add("USERLIST");
    }
    
    public void setClientConnection(ClientConnection cc) {
        this.cc = cc;
    }
    
    @Override
    public void run(String commandString) {
        
        String command   = commandString.split("_")[0];
        String[] args    = commandString.split("_");
        
        if(commandString.contains("_"))
            args = Arrays.copyOfRange(args, 1, args.length);
        
        switch(command) {
            case "JOIN":
                attemptJoin(args[0], cc.getId());
                break;
            case "CHAT":
                onChat(cc.getId(), args[0]);
                break;
            case "USERLIST":
                sendUserList(cc.getId());
                break;
            default:
                System.out.println("ERROR: Improperly Registered Command: " + command);
                break;
        }

    }
    
    private void attemptJoin(String username, String id) {
        
        if (app.getUsers().containsValue(username)) {
            app.getServer().sendMessage(id, "TAKEN");
        }
        
        else {
            app.getServer().sendMessage(id, "WELCOME");
            app.getUsers().put(id, username);
            Platform.runLater(()->app.getGui().addUser(username));
            app.getServer().broadcastMessage("NEWCON_" + username);
            Platform.runLater(()->app.getGui().writeMessage(username + " has joined the server..."));
        }
        
    }

    private void onChat(String id, String message) {
        String username = app.getUsers().get(id);
        String send     = "CHAT_" + username + "_" + message;
        app.getServer().broadcastMessage(send);
        Platform.runLater(()->app.getGui().writeMessage(username + ": " + message));
    }    
    
    private void sendUserList(String id) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATEUSER_");
        app.getUsers().entrySet().forEach((cur) -> {
            sb.append(cur.getValue());
            sb.append("_");
        });
        
        String message = sb.toString();    
        app.getServer().sendMessage(id, message);
        
    }    
    
}
