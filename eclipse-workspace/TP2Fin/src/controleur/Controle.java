package controleur;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import modele.Jeu;
import modele.JeuClient;
import modele.JeuServeur;
import modele.Label;
import outils.connexion.ClientSocket;
import outils.connexion.Connection;
import outils.connexion.ServeurSocket;
import vue.Arene;
import vue.ChoixJoueur;
import vue.EntreeJeu;
import vue.Arene;

/**
 * Controleur de l'application
 * @author 
 *
 */
public class Controle implements Global{
	
	// propri�t�s
	private EntreeJeu frmEntreeJeu ;
	private Jeu leJeu;
	private Arene frmArene;
	private ChoixJoueur frmChoixJoueur;	
	private Connection connection;
	

	/**
	 * M�thode de d�marrage
	 * @param args
	 */
	public static void main(String[] args) {
		new Controle();
	}
	
	/**
	 * Constructeur
	 */
	private Controle() {
		this.frmEntreeJeu = new EntreeJeu(this) ;
		this.frmEntreeJeu.setVisible(true);
	}
	
	/* **********************************************************************************************
	 * Ev�nements provenant de la vue
	 * **********************************************************************************************/
	
	/**
	 * G�re les �v�nements provenant de la vue
	 * @param uneFrame
	 * @param info
	 */
	public void evenementVue(JFrame uneFrame, Object info) {
		// quelle est la frame qui demande ?
		if (uneFrame instanceof EntreeJeu) {
			evenementEntreeJeu(info);
		}
		if (uneFrame instanceof ChoixJoueur) {
			evenementChoixJoueur(info);
		}
		if (uneFrame instanceof Arene) {
			evenementArene(info);
		}
	}

	private void evenementChoixJoueur(Object info) {
		((JeuClient)this.leJeu).envoi(info);
		this.frmChoixJoueur.dispose();
		this.frmArene.setVisible(true);
	}

	/**
	 * G�re les �v�nements provenant de la frame EntreeJeu
	 * @param info
	 */
	private void evenementEntreeJeu(Object info) {
		if ((String)info=="serveur") {
			new ServeurSocket(this, PORT);
			this.leJeu = new JeuServeur(this);
			this.frmEntreeJeu.dispose();
			this.frmArene = new Arene("serveur", this);
			((JeuServeur)leJeu).constructionMurs();
			this.frmArene.setVisible(true);
			
		}else{
			if ((new ClientSocket((String)info, PORT, this)).isConnexionOk()) {
	
				this.leJeu = new JeuClient(this);
				this.leJeu.SetConnection(this.connection);
				
				this.frmEntreeJeu.dispose();
				
				this.frmArene = new Arene("client", this);
				this.frmChoixJoueur = new ChoixJoueur(this);
				this.frmChoixJoueur.setVisible(true);
			}
		}
	}
	
	private void evenementArene(Object info) {
		((JeuClient)this.leJeu).envoi(info);
		
	}
	
	public void SetConnection(Connection connection) {
		this.connection=connection;	
		if (leJeu instanceof JeuServeur) {
			leJeu.SetConnection(connection);
		}
	}	
	
	public void receptionInfo(Connection connection, Object info) {
		leJeu.reception(connection, info);
	}
	
	/* **********************************************************************************************
	 * Ev�nements provenant du mod�le
	 * **********************************************************************************************/

	public void evenementModele(Object unJeu, String ordre, Object info) {
		if (unJeu instanceof JeuServeur) {
			evenementJeuServeur(ordre, info);	
		}
		else if(unJeu instanceof JeuClient) {
			evenementJeuClient(ordre,info);
		}
	}
	
	
	public void evenementJeuServeur(String ordre, Object info) {
		if (ordre.equals("ajout mur")) {
			frmArene.ajoutMur((JLabel) info);
		}
		else if (ordre.equals("envoi panel murs")) {
			((JeuServeur)leJeu).envoi((Connection)info, frmArene.getJpnMurs());
		}
		else if (ordre.equals("ajout joueur")) {
			frmArene.ajoutJoueur((JLabel) info);
		}
	}
	
	public void evenementJeuClient(String ordre, Object info) {
		if (ordre.equals("ajout panel murs")) {
			frmArene.ajoutPanelMurs((JPanel) info);
		}
		else if (ordre.equals("ajout joueur")) {
			Label label = (Label) info;
			JLabel jlabel = label.getjLabel();
			frmArene.ajoutModifJoueur(label.getNumLabel(), jlabel);
		}
	}
}

