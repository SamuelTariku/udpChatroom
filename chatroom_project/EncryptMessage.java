
package chatroom_project;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class EncryptMessage {
    
    public static String encrypt(String message, String message_key) throws Exception{
        
        String key;
        
        
        //Key needs to be 128 bits (16 characters) so add 0s at end if key is small
        if(message_key.length() < 16){
            String filler = new String(new char[16-message_key.length()]).replace("\0", "0");
            key = message_key + filler;
        } else if(message_key.length() > 16){
            key = message_key.substring(0, 16);
        } else {
            key = message_key;
        }

        //Generate Secret Key and use AES cipher
        //This is not very secure but it works for now
        SecretKey securityKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, securityKey);
  
        byte[] messageBytes = message.getBytes("UTF8");
        
        
        byte[] messageEncrypt = null;
        try {
            messageEncrypt = cipher.doFinal(messageBytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Incorrect Key");
            return null;
        }
        
        //Using encoding to aviod special characters
        return Base64.getEncoder().encodeToString(messageEncrypt);
        
    }
    public static String decrypt(String message, String message_key) throws Exception{
        String key;
        //Fill the end of the string with 0s if it is smaller than 128 bits (16 characters)
        if(message_key.length() < 16){
            String filler = new String(new char[16-message_key.length()]).replace("\0", "0");
            key = message_key + filler;
        } else if(message_key.length() > 16){
            key = message_key.substring(0, 16);
        } else {
            key = message_key;
        }
        
        //Generate security key and use AES cipher
        SecretKey securityKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, securityKey);
        
        //Use Base64 encoder to aviod special characters
        
        byte[] messageBytes = null;
        try {
            byte[] messageDecoded = Base64.getDecoder().decode(message);
            messageBytes = cipher.doFinal(messageDecoded);
        } catch (IllegalBlockSizeException | BadPaddingException illegalBlockSizeException) {
            System.out.println("Incorrect Key");
            return null;
        }

        return new String(messageBytes, "UTF8");
        
    }
    /*
     public static void main(String args [])
    {
        String message = "some message";
        String key = "test";
        try {
            System.out.println(encrypt(message, key));
            System.out.println(decrypt(encrypt(message, key), key));
        } catch (Exception ex) {
            
        }
               
        
    }
    */
    
}
