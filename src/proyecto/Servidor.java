/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

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
public class Servidor extends Thread {

    private Socket socket_cliente;
    private String[] palabras_disponibles = {"tren", "patata", "suelo", "zapato"};

    public Servidor(Socket socket_cliente) {
        this.socket_cliente = socket_cliente;
    }

    @Override
    public void run() {
        try {

            OutputStream output_servidor = this.socket_cliente.getOutputStream();
            DataOutputStream salida_servidor = new DataOutputStream(output_servidor);

            InputStream input_servidor = this.socket_cliente.getInputStream();
            DataInputStream entrada_servidor = new DataInputStream(input_servidor);

            // Se selecciona una palabra aleatoriamente de entre las disponibles.
            int palabra_aleatoria = (int) (Math.random() * palabras_disponibles.length);

            // Palabra que tendrá que adivinar el usuario.
            String palabra_juego = this.palabras_disponibles[palabra_aleatoria];
            // System.out.println(palabra_juego);

            // Usaremos esta variable para dar pistas al usuario.
            String palabra_interna = palabra_juego;

            int tamaño_palabra = palabra_juego.length();

            // El cliente empieza con una puntuación de 500.
            int puntuacion = 500;

            String palabra_usuario = "";

            // De cara al usuario empezamos con una palabra vacía, que se visualizará
            // como barras bajas, tantas como caracteres tenga la palabra de la partida.
            for (int i = 0; i < tamaño_palabra; i++) {
                palabra_usuario += '_';
            }

            boolean salir = false;
            String comando_usuario;

            do {
                comando_usuario = entrada_servidor.readUTF();
                System.out.println("Comando entrante: " + comando_usuario);
                
                // Comandos disponibles: IMPRIMIR, PISTA, ADIVINAR, TERMINAR.
                if (comando_usuario.equals("IMPRIMIR")) {

                    salida_servidor.writeUTF(palabra_usuario);
                    
                } else if (comando_usuario.equals("PISTA")) {
                    if (palabra_interna.length() > 0) {
                        
                        // Se toma el primer carácter 
                        char caracter_pista = palabra_interna.charAt(0);

                        // Se elimina el primer carácter de la palabra interna, se puede hacer
                        // con substring.
                        palabra_interna = palabra_interna.substring(1);

                        // Se cambiará la primera barra baja de la palabra del usuario por el carácter
                        // seleccionado para la pista.
                        int posicion_pista = palabra_usuario.indexOf('_');

                        // Para sustituir el primer carácter '_' de la palabra que ve el usuario
                        // por la pista seleccionada, tenemos que pasar la String a array de char,
                        // luego la pasaremos de nuevo a String.
                        char[] palabra_usuario_char = palabra_usuario.toCharArray();
                        palabra_usuario_char[posicion_pista] = caracter_pista;
                        palabra_usuario = String.valueOf(palabra_usuario_char);

                        // Finalmente, se reduce la puntuación del usuario en 50, por usar una pista.
                        puntuacion -= 50;

                        salida_servidor.writeUTF("Nuevo carácter: " + caracter_pista + ", tu palabra actualizada: " + palabra_usuario);

                    } else {
                        // Si ya no hay más pistas que ofrecer, se muestra un mensaje.
                        salida_servidor.writeUTF("No es posible proporcionar una pista.");
                    }
                } else if (comando_usuario.equals("ADIVINAR")) {
                    salida_servidor.writeUTF("Has decidido adivinar la palabra, escríbela a continuación.");

                    String palabra_adivinar = entrada_servidor.readUTF();

                    if (palabra_adivinar.equals(palabra_juego)) {
                        // Si el usuario acierta, se muestra un mensaje y se cierra la conexión. 
                        // Se añade un código numérico que indicará al cliente si debe (0) o no (1) 
                        // cerrar su conexión.
                        salida_servidor.writeUTF("0 - ¡Enhorabuena!, has acertado la palabra oculta. Terminando partida, tu puntuación: " + puntuacion);
                        salir = true;
                    } else {
                        // Si el usuario no acierta, se muestra un mensaje y se restan 100 puntos a su puntuación.
                        salida_servidor.writeUTF("1 - Lo siento, no has acertado.");
                        puntuacion -= 100;
                    }
                } else if (comando_usuario.equals("TERMINAR")) {
                    salida_servidor.writeUTF("Partida terminada. Tu puntuación: " + puntuacion);
                    salir = true;
                } else {
                    salida_servidor.writeUTF("Comando no reconocido");
                }

            } while (!salir);

            // Se sube un archivo con la palabra y la puntuación del usuario a un servidor FTP local.
            // Si todo ha ido bien se devuelve un código 1.
            /*
            String texto_guardar = "Palabra a adivinar: " + palabra_juego + ", puntuación: " + puntuacion;
            int archivo_subido = subirArchivoFTP(texto_guardar);
            
            if (archivo_subido == 1) {
                System.out.println("Los datos de la partida se han guardado satisfactoriamente.");
            }
            else {
                System.out.println("Ha ocurrido un error al guardar los datos de la partida.");
            }
            */
                
            System.out.println("Conexión cerrada");
            entrada_servidor.close();
            salida_servidor.close();
            this.socket_cliente.close();

        } catch (Exception ex) {
            System.out.println("Ha ocurrido un error durante la ejecución del bucle principal: " + ex);
        }

    }

    private int subirArchivoFTP(String texto_guardar) {
        FTPClient cliente = new FTPClient();
        String servidor = "127.0.0.1";
        String usuario = "usuario";
        String pasw = "clave";
        
        // Guardamos el texto proporcionado en un archivo. Se sobreescribirá si ya existe.
        // Si ocurre algún error devolvemos un código 0, de lo contrario se devuelve un 
        // código 1.
        try {
            PrintWriter writer = new PrintWriter("proyecto.txt", "UTF-8");
            writer.println(texto_guardar);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        
        try {
            System.out.println("Conectándose a " + servidor);
            
            cliente.connect(servidor);
            
            boolean login = cliente.login(usuario, pasw);

            String directorio = "/";
            if (login) {
                                
                cliente.changeWorkingDirectory(directorio);

                cliente.setFileType(FTP.BINARY_FILE_TYPE);

                BufferedInputStream in = new BufferedInputStream(new FileInputStream("proyecto.txt"));

                cliente.storeFile("proyecto.txt", in);

                in.close();
                cliente.logout();
                cliente.disconnect();
                
            }
        } catch (IOException io) {
            io.printStackTrace();
            return 0;
        }
        
        // Todo ha ido correctamente, se devuelve un código 1.
        return 1;
    }

}
