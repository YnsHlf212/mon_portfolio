package vue;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controleur.Controle;
import controleur.Global;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class ChoixJoueur extends JFrame implements Global{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtPseudo;
	private Integer numPerso;
	private JLabel lblPersonnage;
	private Controle controle;
	private JTextField txtAge;
	private static String pseudo;
	
	/**
	 * Create the frame.
	 * @return 
	 */
	
	public void lblPrecedent_clic(){
		numPerso= ((numPerso+1)%NBPERSOS)+1;
		affichePerso();
		
	}
	
	public void lblSuivant_clic(){
		numPerso= ((numPerso)%NBPERSOS)+1;
		affichePerso();
		
	}

	public void lblGo_clic(){
		if (txtPseudo.getText().equals("") || txtAge.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "Pseudo et age obligatoires !");
			txtPseudo.requestFocus();
		}else {
			if (Integer.parseInt(txtAge.getText()) < 18) {
			JOptionPane.showMessageDialog(null, "Pour jouer il faut être majeure");
			this.dispose();
			
			}else {
			System.out.println("7"+txtPseudo.getText()+"7");
			controle.evenementVue(this, PSEUDO+SEPARE+txtPseudo.getText()+SEPARE+numPerso);
			setPseudo(txtPseudo.getText());
			
			}
		}
		
	}
	
	public void btnInfo_click(){
		JOptionPane.showMessageDialog(null, "Créateur du mini-jeu : YANIS ADIDI - 2025"
				+ "\n Ce mini jeu regroupe des joueurs sur une grande arène. Le but ? "
				+ "Ce battre jusquà ce qu'il n'en reste qu'un ! ");
		
	}
	
	private void souris_normale() {
		contentPane.setCursor(new Cursor(DEFAULT_CURSOR));
	}
	
	private void souris_doigt() {
		contentPane.setCursor(new Cursor(HAND_CURSOR));
	}
	
	private void affichePerso() {
		lblPersonnage.setIcon(new ImageIcon(PERSO+numPerso+MARCHE+"1d"+DROITE+EXTIMAGE));
	}
	public ChoixJoueur(Controle controle) {
		setTitle("Choice");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 416, 339);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPrecedent = new JLabel("");
		lblPrecedent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lblPrecedent_clic();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				souris_doigt();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				souris_normale();
			}
		});
		
	
		
		lblPersonnage = new JLabel("");
		lblPersonnage.setHorizontalAlignment(SwingConstants.CENTER);
		lblPersonnage.setBounds(141, 111, 121, 121);
		contentPane.add(lblPersonnage);
		
		txtPseudo = new JTextField();
		txtPseudo.setBounds(141, 243, 121, 20);
		contentPane.add(txtPseudo);
		txtPseudo.setColumns(10);
		lblPrecedent.setBounds(60, 145, 54, 46);
		contentPane.add(lblPrecedent);
		
		JLabel lblSuivant = new JLabel("");
		lblSuivant.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lblSuivant_clic();			}
			@Override
			public void mouseEntered(MouseEvent e) {
				souris_doigt();
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				souris_normale();
			}
		});
		lblSuivant.setBounds(284, 145, 54, 46);
		contentPane.add(lblSuivant);
		
		JLabel lblGo = new JLabel("");
		lblGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lblGo_clic();			
				}
			@Override
			public void mouseEntered(MouseEvent e) {
				souris_doigt();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				souris_normale();
			}
		});
		lblGo.setBounds(311, 195, 63, 68);
		contentPane.add(lblGo);
		
		JButton btnInfo = new JButton("Info");
		btnInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnInfo_click();
			}
		});
		btnInfo.setBounds(311, 276, 63, 16);
		contentPane.add(btnInfo);
		
		txtAge = new JTextField();
		txtAge.setColumns(10);
		txtAge.setBounds(141, 274, 121, 20);
		contentPane.add(txtAge);
		
		JLabel lblFond = new JLabel("");
		lblFond.setBounds(0, 0, 400, 301);
		lblFond.setIcon(new ImageIcon(FONDCHOIX));
		contentPane.add(lblFond);
		
		txtPseudo.requestFocus();
		this.controle=controle;
		numPerso=1;
		affichePerso();
		
	}

	public static String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		ChoixJoueur.pseudo = pseudo;
	}
}
