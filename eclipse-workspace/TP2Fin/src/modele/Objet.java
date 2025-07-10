package modele;


public abstract class Objet {
	protected int posX, posY;
	protected Label label;
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	public Label getlabel() {
		return label;
	}
	
	/**
	* contrôle si l'objet actuel touche l'objet passé en paramètre
	* @param objet
	* @return vrai si les 2 objets se touchent
	*/
	public boolean toucheObjet (Objet objet) {
		if (objet.label==null) {
			return false ;
		}else{
			if (objet.label.getjLabel()==null) {
				return false ;
			}else{
				int l_obj = objet.label.getjLabel().getWidth() ;
				int h_obj = objet.label.getjLabel().getHeight() ;
				int l_this = this.label.getjLabel().getWidth() ;
				int h_this = this.label.getjLabel().getHeight() ;
				return(!((this.posX+l_this<objet.posX ||
						this.posX>objet.posX+l_obj) ||
						(this.posY+h_this<objet.posY ||
								this.posY>objet.posY+h_obj))) ;
			}
		}
	}
}
