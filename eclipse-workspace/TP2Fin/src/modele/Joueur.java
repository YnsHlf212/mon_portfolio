package modele;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import controleur.Global;
import modele.Label;
import outils.connexion.Connection;
import vue.ChoixJoueur;
import modele.JeuServeur;

public class Joueur extends Objet implements Global {
	private String pseudo;
	private int  numPerso;
	private Label message;
	private JeuServeur jeuServeur;
	private int vie; // vie restante du joueur
	private int orientation; // tourné vers la gauche (0) ou vers la droite (1)
	private int etape; // numéro d'étape dans l'animation
	
	public Label getMessage() {
		return message;
	}
	
	public Joueur(JeuServeur jeuServeur) {
		this.jeuServeur = jeuServeur;
		this.vie = 10;
		this.etape = 1;
		this.orientation = DROITE;
	}
	
	public void initPerso(String pseudo,int  numPerso, Hashtable<Connection, Joueur> lesJoueurs,ArrayList<Mur> lesMurs) {
		this.pseudo = pseudo;
		this.numPerso = numPerso;
		
		JLabel objetPersonnage =new JLabel();
		objetPersonnage.setHorizontalAlignment(SwingConstants.CENTER);
		objetPersonnage.setVerticalAlignment(SwingConstants.CENTER);
		
		Label labelPersonnage = new Label(Label.getNbLabel(), objetPersonnage);
		Label.setNbLabel(Label.getNbLabel()+1);
		
		this.label = labelPersonnage;
		jeuServeur.nouveauLabelJeu(labelPersonnage);
		
		JLabel objetMessage =new JLabel();
		objetMessage.setHorizontalAlignment(SwingConstants.CENTER);
		
		Label labelMessage = new Label(Label.getNbLabel(), objetMessage);
		Label.setNbLabel(Label.getNbLabel()+1);
		
		this.message = labelMessage;
		jeuServeur.nouveauLabelJeu(labelMessage);
		
		objetMessage.setFont(new Font("Dialog", Font.PLAIN, 10));
		
		premierePosition(lesJoueurs, lesMurs);
	    
		affiche(MARCHE, etape);
	}
	
	private boolean toucheJoueur(Hashtable<Connection, Joueur> lesJoueurs ) {
		for (Joueur nvJoueur : lesJoueurs.values()) {
			if (nvJoueur.equals(this)) {
				
			}else {
				return toucheObjet(nvJoueur);
			}
		}
		return false;
	}
	
	private boolean toucheMur(ArrayList<Mur> lesMurs) {
		for (Mur nvMur : lesMurs) {
				return toucheObjet(nvMur);
		}
		return false;
	}
	
	public void premierePosition(Hashtable<Connection, Joueur> lesJoueurs,ArrayList<Mur> lesMurs) {
		label.getjLabel().setBounds(0, 0, L_PERSO, H_PERSO);
		do {
			 posX = (int) Math.round(Math.random()*(L_ARENE-L_PERSO));
			 posY = (int) Math.round(Math.random()*(H_ARENE-H_PERSO-H_MESSAGE));
			}while(toucheJoueur(lesJoueurs) || toucheMur(lesMurs)) ; 
	}
	
	public void affiche(String etat, int etape) {
		label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
		label.getjLabel().setIcon(new ImageIcon(PERSO+numPerso+etat+etape+"d"+orientation+EXTIMAGE));
		//System.out.print(PERSO+numPerso+etat+etape+"d"+orientation+EXTIMAGE); //confirmation
		
		message.getjLabel().setBounds(posX, posY+45, L_PERSO+10, H_MESSAGE);
		message.getjLabel().setText(pseudo+":"+vie);	
		//System.out.print("\npseudo :"+pseudo+"."); //Test 
		
		jeuServeur.envoi(label);
		jeuServeur.envoi(message);
	}
	
}
