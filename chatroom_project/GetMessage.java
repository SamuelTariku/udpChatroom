
package chatroom_project;

import java.io.*;
import java.net.*;

public class GetMessage implements Runnable{
    
    private final MulticastSocket socket; 
    private final InetAddress address; 
    private final int port; 

    public GetMessage(MulticastSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }
    
    @Override
    public void run() {
        while(!Chatroom_project.exit){
            
            String message;
            
            //Maximum length of any message should be 1000 bytes
            byte[] buffer = new byte[1000];
            DatagramPacket recieveData = new DatagramPacket(buffer, 
                    buffer.length, address, port);
            
            
            try {
                //Recieve datagram packets
                socket.receive(recieveData);
                message = new String(buffer,0,recieveData.getLength(),"UTF-8");
                
                String[] encryptTest = message.split("&");
                if(encryptTest[0].equals("*EX")){
                    if(Chatroom_project.encryptMode){
                        System.out.println();
                        try {
                            System.out.println(EncryptMessage.decrypt(encryptTest[1], Chatroom_project.encryptKey));
                        } catch (Exception ex) {
                            System.out.println("Encryption Error");
                            ex.printStackTrace();
                        }
                        System.out.println();
                    } else {
                        System.out.println("*********************************************************************");
                        System.out.println("This message has been encrypted. Enter encrypt mode by typing \"-e\"");
                        System.out.println("**********************************************************************");
                    }
                } else {    
                    System.out.println();
                    System.out.println(message);
                    System.out.println();
                }
            } catch (SocketException ex) {
                System.out.println("Connection is closed");
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        
    }
    
}
