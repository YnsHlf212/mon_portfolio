package modele;

import javax.swing.JPanel;

import controleur.Controle;
import outils.connexion.Connection;

public class JeuClient extends Jeu {
	private Connection connection;

	@Override
	public void SetConnection(Connection connection) {
		this.connection=connection;
	}

	@Override
	public void reception(Connection connection, Object info) {
		if (info instanceof JPanel) {
			controle.evenementModele(this, "ajout panel murs", info);
		}
		else if(info instanceof Label) {
			controle.evenementModele(this, "ajout joueur", info);
		}
	}

	@Override
	public void deconnection(Connection connection) {
		// TODO Auto-generated method stub
	}
	
	public void envoi(Object info) {
		super.envoi(connection,info); 
	}
	
	public JeuClient(Controle controle){
		super.controle=controle;
	}
}
