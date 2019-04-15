/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.client;

import fatwalrus.commands.CommandRegistry;
import java.util.Arrays;
import javafx.application.Platform;

/**
 *
 * @author Bob
 */
public class ChatClientCommandRegistry extends CommandRegistry {

    private final ChatClientApplication app;
    
    public ChatClientCommandRegistry(ChatClientApplication app) {
        this.app = app;
        commands.add("WELCOME");
        commands.add("TAKEN");
        commands.add("CHAT");
        commands.add("READY");
        commands.add("UPDATEUSER");
        commands.add("NEWCON");
        commands.add("EXCON");
    }
    
    @Override
    public void run(String commandString) {
        
        String command   = commandString.split("_")[0];
        String[] args    = commandString.split("_");
        
        if(commandString.contains("_"))
            args = Arrays.copyOfRange(args, 1, args.length);
        
        switch(command) {
            case "WELCOME":
                onJoin();
                break;
            case "TAKEN":
                onReject("Username Taken");
                break;
            case "CHAT":
                onChat(args[0], args[1]);
                break;
            case "READY":
                onReady();
                break;
            case "UPDATEUSER":
                receiveUserList(args);
                break;
            case "NEWCON":
                onUserJoin(args[0]);
                break;
            case "EXCON":
                onUserLeave(args[0], args[1]);
                break;
            default:
                System.out.println("ERROR: Improperly Registered Command: " + command);
                break;
        }

    }
    
    public void onReady() {
        attemptJoin();
    }
    
    private void attemptJoin() {
        String message = "JOIN_" + app.getUsername();
        app.getClient().sendMessage(message.getBytes());
    }
    
    public void onJoin() {
        Platform.runLater(()->app.getGui().showChatScreen());
        requestUserList();
    }
    
    public void onReject(String reason) {
        Platform.runLater(()->app.getGui().showConnectionScreen());
        Platform.runLater(()->app.getGui().alert("Connection Rejected: " + reason));
        app.getClient().stop();
    }
    
    private void requestUserList() {
        app.getClient().sendMessage("USERLIST".getBytes());
    }
    
    public void receiveUserList(String[] users) {
        Platform.runLater(()->app.getGui().clearUserList());
        for (String user : users) {
            Platform.runLater(()->app.getGui().addUser(user));
        }
    }
    
    public void onChat(String username, String message) {
        Platform.runLater(()->app.getGui().writeMessage(username + ": " + message));
    }
    
    public void onUserJoin(String username) {
        Platform.runLater(()->app.getGui().addUser(username));
        Platform.runLater(()->app.getGui().writeMessage(username + " has joined the server..."));
    }
    
    public void onUserLeave(String username, String reason) {
        Platform.runLater(()->app.getGui().removeUser(username));
        Platform.runLater(()->app.getGui().writeMessage(username + " has left: " + reason));
    }    
    
}
