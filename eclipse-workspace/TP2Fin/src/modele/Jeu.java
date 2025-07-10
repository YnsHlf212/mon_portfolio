package modele;

import controleur.Controle;
import outils.connexion.Connection;

public abstract class Jeu {
	protected Controle controle;
	
	public abstract void SetConnection(Connection connection);
	
	public abstract void reception(Connection connection, Object info);
	
	public void envoi(Connection connection, Object info) {
		connection.envoi(info); 
	}
	
	public abstract void deconnection(Connection connection);
}
