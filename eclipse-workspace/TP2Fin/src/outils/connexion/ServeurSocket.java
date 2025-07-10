package outils.connexion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Gestion d'un serveur : attente de connexions de clients
 * @author emds
 *
 */
public class ServeurSocket extends Thread {

	// propri�t�s
	private Object leRecepteur ;
	private ServerSocket serverSocket ;
	
	/**
	 * Constructeur
	 * @param leRecepteur
	 * @param port
	 */
	public ServeurSocket(Object leRecepteur, int port) {
		this.leRecepteur = leRecepteur ;
		// cr�ation du socket serveur d'�coute des clients
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("erreur grave cr�ation socket serveur : "+e);
			System.exit(0);
		}
		// d�marrage du thread d'�coute (attente d'un client)
		this.start();
	}
	
	/**
	 * M�thode thread qui va attendre la connexion d'un client
	 */
	public void run() {
		Socket socket ;
		// boucle infinie pour attendre un nouveau client
		while (true) {
			try {
				System.out.println("le serveur attend");
				socket = serverSocket.accept();
				System.out.println("un client s'est connect�");
				new Connection(socket, leRecepteur);
			} catch (IOException e) {
				System.out.println("erreur sur l'objet serverSocket : "+e);
				System.exit(0);
			}
		}
	}
	
}
