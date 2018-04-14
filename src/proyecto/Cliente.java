/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

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
public class Cliente {
    
    public Cliente() {
        
    }
    
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5000;
        try {
            Socket socket_cliente = new Socket(host, puerto);
            OutputStream output_cliente = socket_cliente.getOutputStream();
            DataOutputStream salida_cliente = new DataOutputStream(output_cliente);

            InputStream input_cliente = socket_cliente.getInputStream();
            DataInputStream entrada_cliente = new DataInputStream(input_cliente);


            System.out.println("Cliente iniciado");
            
            boolean salir = false;
            Scanner scanner = new Scanner(System.in);
            
            do {
                System.out.print("Escribe un comando: ");
                String comando_usuario = scanner.next();
                
                salida_cliente.writeUTF(comando_usuario);
                                
                System.out.println(" - Respuesta del servidor: " + entrada_cliente.readUTF());
                if (comando_usuario.equals("ADIVINAR")) {
                    
                    // Si el cliente decide adivinar, hay que permitirle introducir la palabra.
                    // Adicionalmente, el servidor escribirá otro mensaje indicando si la palabra
                    // propocionada por el usuario es correcta.
                    System.out.print("Escribe la palabra que tienes en mente: ");
                    comando_usuario = scanner.next();            
                    salida_cliente.writeUTF(comando_usuario);
                    String respuesta_adivinar = entrada_cliente.readUTF();
                    System.out.println(" - Respuesta del servidor (ADIVINAR): " + respuesta_adivinar);
                    
                    // Si el usuario ha acertado, la respuesta empezará por el código 0, indicándonos que
                    // debemos cerrar la conexión en el lado del cliente, pues en el lado del servidor también
                    // se cerrará.
                    if (respuesta_adivinar.startsWith("0"))
                        salir = true;
                }
                else if (comando_usuario.equals("TERMINAR")) {
                    salir = true;
                }

            } while(!salir);
            
            System.out.println("Cliente terminado");
            entrada_cliente.close();
            salida_cliente.close();
            socket_cliente.close();
            
        } catch (Exception ex) {
            System.out.println("Ha ocurrido un error durante la ejecución: " + ex);
        }
        
        
    }
}
