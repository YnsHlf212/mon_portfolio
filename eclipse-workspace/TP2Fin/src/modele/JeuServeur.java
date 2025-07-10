package modele;

import java.util.ArrayList;
import java.util.Hashtable;

import controleur.Controle;
import controleur.Global;
import outils.connexion.Connection;

public class JeuServeur extends Jeu implements Global{
	private ArrayList<Mur> lesMurs = new ArrayList<Mur>();
	private Hashtable<Connection,Joueur> lesJoueurs = new Hashtable<Connection,Joueur>();
	private ArrayList<Joueur> lesJoueursDansLordre = new ArrayList<Joueur>();

	@Override
	public void SetConnection(Connection connection) {
		lesJoueurs.put(connection, new Joueur(this));
		
		
	}

	@Override
	public void reception(Connection connection, Object info) {
		String[] infos = ((String) info).split(SEPARE);
		switch (Integer.parseInt(infos[0])) {
			case PSEUDO:
				controle.evenementModele(this, "envoi panel murs", connection);
				for (Joueur joueur : lesJoueursDansLordre) {
					super.envoi(connection, joueur.getlabel());
					super.envoi(connection, joueur.getMessage());
				}
				lesJoueurs.get(connection).initPerso(infos[1], Integer.parseInt(infos[2]),lesJoueurs,lesMurs);
				this.lesJoueursDansLordre.add(this.lesJoueurs.get(connection));
			break;
		}
	}

	@Override
	public void deconnection(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	public void envoi(Object info) {
		for (Connection cle : lesJoueurs.keySet()) {
			super.envoi( cle, info);
		}
		
	}

	public JeuServeur(Controle controle){
		super.controle=controle;
		Label.setNbLabel(0);
	}

	public void constructionMurs(){
		for (int i=0; i<NBMURS;i++) {
			lesMurs.add(new Mur());
			controle.evenementModele(this, "ajout mur", lesMurs.get(lesMurs.size()-1).getlabel().getjLabel());
		}
	}
	
	public void nouveauLabelJeu(Label label) {
		controle.evenementModele(this, "ajout joueur", label.getjLabel());
	}
}
