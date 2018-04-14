/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple_word_guessing;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.*;

/**
 *
 * @author david
 */
public class Server extends Thread {

    private final Socket client_socket;
    
    // New words can be added to this array.
    private final String[] available_words = {"train", "potato", "floor", "shoe"};

    public Server(Socket client_socket) {
        this.client_socket = client_socket;
    }

    @Override
    public void run() {
        try {

            OutputStream server_output = this.client_socket.getOutputStream();
            DataOutputStream server_output_stream = new DataOutputStream(server_output);

            InputStream server_input = this.client_socket.getInputStream();
            DataInputStream server_input_stream = new DataInputStream(server_input);
           
            int random_word = (int) (Math.random() * available_words.length);

            String game_word = this.available_words[random_word];

            String internal_word = game_word;
            int word_length = game_word.length();
            int score = 500;

            String visible_word = "";
            
            for (int i = 0; i < word_length; i++) {
                visible_word += '_';
            }

            boolean end = false;
            String user_command;

            do {
                user_command = server_input_stream.readUTF();
                System.out.println("Input command: " + user_command);
                
                // Available commands: SHOW, CLUE, GUESS, END.
                if (user_command.equals("SHOW")) {

                    server_output_stream.writeUTF(visible_word);
                    
                } else if (user_command.equals("CLUE")) {
                    if (internal_word.length() > 0) {
                        
                        char clue_letter = internal_word.charAt(0);

                        internal_word = internal_word.substring(1);

                        int clue_position = visible_word.indexOf('_');

                        // In order to place the new character in its position, the visible
                        // word must be transformed into a char array.
                        char[] visible_word_char = visible_word.toCharArray();
                        visible_word_char[clue_position] = clue_letter;
                        visible_word = String.valueOf(visible_word_char);

                        score -= 50;

                        server_output_stream.writeUTF("New chracter: " + clue_letter + ", your word has been updated: " + visible_word);

                    } else {
                        server_output_stream.writeUTF("Sorry, there are no more clues available.");
                    }
                } else if (user_command.equals("GUESS")) {
                    server_output_stream.writeUTF("You have chosen to guess the word. Write it below");

                    String user_guessing_word = server_input_stream.readUTF();

                    if (user_guessing_word.equals(game_word)) {

                        server_output_stream.writeUTF("0 - ¡Congratulations!, you guessed the hidden word. You won!, your score: " + score);
                        end = true;
                    } else {
                        server_output_stream.writeUTF("1 - Sorry, you failed to guess the hidden word");
                        score -= 100;
                    }
                } else if (user_command.equals("END")) {
                    server_output_stream.writeUTF("Game over. Score: " + score);
                    end = true;
                } else {
                    server_output_stream.writeUTF("Command not recognized");
                }

            } while (!end);

            // The game data can be saved in a FTP remote server if wanted.
            /*
            String text_to_save = "Word: " + game_word + ", score: " + score;
            int archivo_subido = uploadToFTP(text_to_save);
            
            if (archivo_subido == 1) {
                System.out.println("Your game data has been uploaded successfully.");
            }
            else {
                System.out.println("An error occurred during the upload of your game data.");
            }
            */
            
            System.out.println("Closing connection");
            server_input_stream.close();
            server_output_stream.close();
            this.client_socket.close();

        } catch (Exception ex) {
            // The error is logged but the program continues.
            Logger.getLogger(Server.class.getName()).log(Level.WARNING, "An error occurred during the execution of the main loop", ex);
        }

    }

    private int uploadToFTP(String texto_guardar) {
        FTPClient cliente = new FTPClient();
        String server = "127.0.0.1";
        String user = "user";
        String password = "password";
                
        try (PrintWriter writer = new PrintWriter("project.txt", "UTF-8")) {
            writer.println(texto_guardar);
        }
        catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            
            // An error occurred, returning with code 0.
            return 0;
        }
        
        try {
            System.out.println("Connected to " + server);
            cliente.connect(server);
            
            boolean login = cliente.login(user, password);
            String path = "/";
            
            if (login) {
                cliente.changeWorkingDirectory(path);
                cliente.setFileType(FTP.BINARY_FILE_TYPE);

                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream("project.txt"))) {
                    cliente.storeFile("project.txt", in);
                }
                cliente.logout();
                cliente.disconnect();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            
            // An error occurred, returning with code 0.
            return 0;
        }
        
        // Everything is OK, returning with code 1.
        return 1;
    }

}
