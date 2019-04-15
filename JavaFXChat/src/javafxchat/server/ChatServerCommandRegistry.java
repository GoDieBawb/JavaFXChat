/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxchat.server;

import fatwalrus.commands.CommandRegistry;
import java.util.Arrays;

/**
 *
 * @author Bob
 */
public class ChatServerCommandRegistry extends CommandRegistry {

    private final ChatServerApplication app;
    
    public ChatServerCommandRegistry(ChatServerApplication app) {
        this.app = app;
    }
    
    @Override
    public void run(String commandString) {
        
        String command   = commandString.split("_")[0];
        String[] args    = commandString.split("_");
        
        if(commandString.contains("_"))
            args = Arrays.copyOfRange(args, 1, args.length);
        
        switch(command) {
            default:
                System.out.println("ERROR: Improperly Registered Command: " + command);
                break;
        }

    }
    
    private void broadcastUserList() {
        
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATEUSER_");
        app.getUsers().entrySet().forEach((cur) -> {
            sb.append(cur.getValue());
            sb.append("_");
        });
        
        String message = sb.toString();
        System.out.println("List Message: " + message);        
        app.getServer().broadcastMessage(message);
        
    }    
    
    private void printUserList() {
        System.out.print("Current Users: ");
        app.getUsers().entrySet().forEach((cur) -> {
            System.out.print(cur.getValue() + " ");
        });
        System.out.println("");
    }    
    
}
