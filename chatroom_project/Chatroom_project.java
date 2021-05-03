
package chatroom_project;

import java.net.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Chatroom_project {

    static String username;
    static InetAddress address;
    static int port;
    
    static boolean encryptMode = false;
    static String encryptKey;
    
    //Volatile keyword because it is used to keep threads safe
    static volatile boolean exit = false;
    
    public static void main(String[] args) {
        
        
        // TODO code application logic here
        Scanner input = new Scanner(System.in);
        System.out.println(" ---- UDP CHATROOM ----");
        
        //Get the multicast host IP address
        
        boolean hostTest;
        do {
            hostTest = false;
            System.out.print("Enter Multicast IP Address [225.0.0.0 - 239.255.255.255]:");
            String ipAddress = input.nextLine();
        
            try {
            address = InetAddress.getByName(ipAddress);
            } catch (UnknownHostException e) {
                System.err.println("Host Unknown " + ipAddress);
                hostTest = true;
            }
        
        } while(hostTest);
        
        //Get UDP port number
        int number;
        do {
        System.out.print("Enter UDP Port Address [1024-65535]:");
        number = Integer.parseInt(input.nextLine());
        } while ((number <= 1024) || (number > 65535));
        port = number;
        
        
        
        try {
            //Create Muticast Socket
            MulticastSocket socket = new MulticastSocket(port);
            socket.setTimeToLive(0);
            socket.joinGroup(address);
            
            //Get username
            System.out.println();
            System.out.print("Enter Username:");
            username = input.nextLine();
            
            
            //GET MESSAGES FROM OTHER USERS
            //TODO
            Thread newThread = new Thread(new GetMessage(socket, address, port));
            newThread.start();
            
            System.out.println();
            System.out.println("-- Hello " + username + " --");
            System.out.println("-- You have entered the chatroom --");
            System.out.println("-- Type \"exit\" to leave -- ");
            System.out.println("-- Type \"-c\" to change username --");
            System.out.println("-- Type \"-e\" to activate encryption mode");
            System.out.println("-- Type \"-ex\" to deactivate encryption mode");
            System.out.println();
            
            //Get message from user and send it
            //Breaks when exit is typed
            boolean enterText = true;
            while(!exit) {
                
                
                //Message that is sent when someone enters the chat
                if(enterText) {
                    String enterMessage = "**" + username + " has joined the chat" + "**";
                    enterText = false;
                    
                    byte[] messageBuffer = enterMessage.getBytes();
                    DatagramPacket datagram = new DatagramPacket(messageBuffer, 
                    messageBuffer.length,address, port);
                    socket.send(datagram);
                }
                
                //Get message
                String message;
                message = input.nextLine();
                
                
                if(message.equals("exit")) {
                    //Exit message
                    exit = true;
                    message = "**" + username + " has left the chat" + "**";
                } else if(message.getBytes().length > 1000){
                    //Make sure the message is not over 1000 bytes
                    System.out.println("Message is to large");
                    message = "** " + username + " MESSAGE OVERLOAD **";
                
                } else if(message.equals("-c")){
                    //Change username
                    System.out.print("Enter new username:");
                    String oldUsername = username;
                    username = input.nextLine();
                    
                    System.out.println();
                    System.out.println("Username changed to " + username);
                    message = "**" + oldUsername + " has changed his username to " + username + "**";
                } else if(message.equals("-e")){
                    encryptMode = true;
                    
                    System.out.print("Enter key:");
                    encryptKey = input.nextLine();
                    
                    message = "**" + username + " has changed to encrypt mode";
                } else if(message.equals("-ex")){
                    encryptMode = false;
                    message = "**" + username + " has left encrypt mode";
                } else {
                    //Get current time and format it
                    LocalDateTime datetime = LocalDateTime.now();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
                    String formattedDate = datetime.format(format);
                
                    //Format the message
                    message = formattedDate + " " + username + "> " + message;
                    if(encryptMode){
                        try {
                            message = "*EX&" + EncryptMessage.encrypt(message, encryptKey);
                        } catch (Exception ex) {
                            System.out.println("Encryption Error");
                            ex.printStackTrace();
                        }
                    }
                }
                
                //Convert the data into bytes and send it
                byte[] messageBuffer = message.getBytes();
                DatagramPacket datagram = new DatagramPacket(messageBuffer, 
                        messageBuffer.length,address, port);
                socket.send(datagram);
                
                //Closes socket when exit is typed
                if(exit){
                    socket.leaveGroup(address);
                    socket.close();
                }
                
            }
            
        } catch (SocketException sx) {
            System.err.println("Multicast address incorrect");
        } catch (IOException ex) {
            System.err.println("Cannot create socket on port " + port);
        }
      
    }
    
    
}
