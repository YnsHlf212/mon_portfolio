package controleur;

public interface Global {
	public static final int PORT=6666;
	
	//CHEMINS ET FONDS
	public static final String SEPARATOR = "//";
	public static final String CHEMIN = "media" + SEPARATOR;
	public static final String CHEMINFONDS = CHEMIN + "fonds" + SEPARATOR;
	public static final String FONDCHOIX = CHEMINFONDS + "fondchoix.jpg",
							   FONDARENE = CHEMINFONDS+"fondarene.jpg" ;
	
	//ACTION
	public static final int GAUCHE = 0,
								DROITE = 1;
	
	public static final String MARCHE = "marche" ,// les différents états
								BLESSE = "touche",
								MORT = "mort";
	
	public static final String SEPARE = "~";// caractère de séparation (volontairement rare)
	public static final int PSEUDO = 0 ;// le message contiendra le pseudo et numéro du
			 								// personnage 
	
	//PERSOS
	public static final int NBPERSOS = 5 ;// nombre de personnages
	
	public static final String CHEMINPERSOS = CHEMIN + "personnages" + SEPARATOR;
	public static final String PERSO = CHEMINPERSOS + "perso";
	public static final String EXTIMAGE = ".gif";
	
	public static final int H_PERSO = 44, // taille de l'image du personnage
								L_PERSO = 39 ;
	
	//MURS
	public static final int NBMURS = 20 ;// nombre de murs
	
	public static final String CHEMINMURS = CHEMIN + "murs" + SEPARATOR;
	public static final String MUR = CHEMINMURS + "mur.gif"; // image du mur
	public static final int H_MUR = 35,
							L_MUR = 34, // hauteur de l'image 
							H_MESSAGE = 8; // hauteur du message
	
	//DIMENSIONS 
	public static final int H_ARENE = 600,
								L_ARENE = 800,
								H_CHAT = 200,
								H_SAISIE = 25,
								MARGE = 5 ;// elle va servir pour les écarts entre différents objets
}
