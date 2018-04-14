/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author david
 */
public class Proyecto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            
            int numero_clientes = 10;
            int contador = 0;
            int puerto = 5000;
            ArrayList<Servidor> lista_hebras = new ArrayList<>();
            System.out.println("Esperando conexión en el puerto: " + puerto);

            // Se abre un socket que publicará un acceso en el puerto designado.
            ServerSocket socket_servidor = new ServerSocket(puerto);            
            
            // Se atiende a un máximo de "numero_clientes" clientes.
            while (contador < numero_clientes) {
                
                // Nos bloqueamos hasta que un cliente haga conexión.
                Socket scliente = socket_servidor.accept();
                
                System.out.println("Conexión entrante, se atiende al cliente número " + contador + " con una hebra.");
                
                // Atender al cliente con una hebra.
                Servidor hebra_servidor = new Servidor(scliente);
                
                // Se añade la hebra a un array para esperar a que todas terminen su ejecución antes
                // de acabar el programa.
                lista_hebras.add(hebra_servidor);
                hebra_servidor.start();
                
                contador++;
            }
            
            // Para terminar el programa, se espera a que todas las hebras servidor terminen.
            for(int i=0; i<lista_hebras.size(); i++)
                lista_hebras.get(i).join();

        } catch (Exception ex) {
            System.out.println("Se ha producido un error de comunicación: " + ex);
            System.exit(1);
        }
    }
    
}
