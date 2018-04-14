/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple_word_guessing;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author david
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            
            int max_clients = 10;
            int client_counter = 0;
            int port = 5000;
            ArrayList<Server> thread_list = new ArrayList<>();
            ServerSocket socket_servidor = new ServerSocket(port);
            
            System.out.println("Waiting for new connections in port: " + port);
            
            while (client_counter < max_clients) {
                
                Socket client_socket = socket_servidor.accept();                
                System.out.println("New connection, serving client #" + client_counter + " with a thread.");
                
                Server server_thread = new Server(client_socket);
                thread_list.add(server_thread);
                server_thread.start();
                
                client_counter++;
            }
            
            // All thread must end before exiting the program.
            for(int i=0; i<thread_list.size(); i++)
                thread_list.get(i).join();

        } catch (Exception ex) {
            System.out.println("An error occurred: " + ex);
            System.exit(1);
        }
    }
}
