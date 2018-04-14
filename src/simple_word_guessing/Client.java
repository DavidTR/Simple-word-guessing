/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple_word_guessing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author david
 */
public class Client {
    
    public Client() {
        
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        try {
            Socket client_socket = new Socket(host, port);
            OutputStream client_output = client_socket.getOutputStream();
            DataOutputStream client_output_stream = new DataOutputStream(client_output);

            InputStream client_input = client_socket.getInputStream();
            DataInputStream client_input_stream = new DataInputStream(client_input);


            System.out.println("Client initialized");
            
            boolean exit = false;
            Scanner scanner = new Scanner(System.in);
            
            do {
                System.out.print("Write a command: ");
                String user_command = scanner.next();
                
                client_output_stream.writeUTF(user_command);
                                
                System.out.println(" - Server answer: " + client_input_stream.readUTF());
                if (user_command.equals("GUESS")) {

                    System.out.print("Write the word you have in mind: ");
                    user_command = scanner.next();            
                    client_output_stream.writeUTF(user_command);
                    String guess_server_answer = client_input_stream.readUTF();
                    System.out.println(" - Server answer (GUESS): " + guess_server_answer);
                    
                    // If the user guessed the word, the server answer will begin with 0, what means that the
                    // the connection must be closed in the client side.
                    if (guess_server_answer.startsWith("0"))
                        exit = true;
                }
                else if (user_command.equals("END")) {
                    exit = true;
                }

            } while(!exit);
            
            System.out.println("Client finished");
            client_input_stream.close();
            client_output_stream.close();
            client_socket.close();
            
        } catch (Exception ex) {
            System.out.println("An error occurred: " + ex);
            System.exit(1);
        }
        
        
    }
}
