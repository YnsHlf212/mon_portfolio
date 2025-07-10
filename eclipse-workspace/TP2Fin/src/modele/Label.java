package modele;

import java.io.Serializable;

import javax.swing.JLabel;

public class Label implements Serializable{
	private static int nbLabel;
	private int numLabel;
	private JLabel jLabel;

	public Label(int numLabel, JLabel jLabel) {
		this.numLabel = numLabel;
		this.jLabel = jLabel;
	}
	
	public static int getNbLabel() {
		return nbLabel;
	}

	public static void setNbLabel(int nbLabel) {
		Label.nbLabel = nbLabel;
	}

	public int getNumLabel() {
		return numLabel;
	}

	public JLabel getjLabel() {
		return jLabel;
	}
	
	
}
