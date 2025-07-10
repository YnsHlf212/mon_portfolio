package vue;

import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controleur.Controle;
import controleur.Global;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Arene extends JFrame implements Global{

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtSaisie;
	private JPanel jpnJeu;
	private JPanel jpnMurs;
	private boolean client;
	private String txtSaisie_keyPressed;
	private Controle controle;
	
	public void ajoutMur(JLabel mur) {
		jpnMurs.add(mur);
		jpnMurs.revalidate();
		jpnMurs.repaint();
	}
	
	public void ajoutJoueur(JLabel objet) {
		jpnJeu.add(objet);
		jpnJeu.revalidate();
		jpnJeu.repaint();
	}
	
	public void ajoutModifJoueur(int num, JLabel unLabel ) {
		try {
			jpnJeu.remove(num);
			System.out.println(unLabel);
		} catch (Exception ArrayIndexOutOfBoundsException) {
			// TODO Auto-generated catch block
		}
		jpnJeu.add(unLabel,num);
		jpnJeu.repaint();
	}
	
	public void ajoutPanelMurs(JPanel objet) {
		jpnMurs.add(objet);
		jpnMurs.repaint();
		contentPane.requestFocus();
	}
	
	public JPanel getJpnMurs() {
		return jpnMurs;
	}
	
	
	public void txtSaisie_keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_ENTER) {
			if (true ) {
				
			}
		}
	}
	
	/**
	 * Create the frame.
	 */
	public Arene(String typeJeu, Controle controle) {
		 this.client = typeJeu.contains("client");
		 this.controle = controle;		
		
		
		setTitle("Arena");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		setBounds(100, 100, L_ARENE+3*MARGE, H_ARENE + H_CHAT);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		jpnJeu = new JPanel();
		jpnJeu.setLayout(null);
		jpnJeu.setOpaque(false);
		jpnJeu.setBounds(0, 0, L_ARENE, H_ARENE);
		contentPane.add(jpnJeu);
		
		jpnMurs = new JPanel();
		jpnMurs.setLayout(null);
		jpnMurs.setOpaque(false);
		jpnMurs.setBounds(0, 0, L_ARENE, H_ARENE);
		contentPane.add(jpnMurs);
		
		JLabel lblFond = new JLabel("");
		lblFond.setBounds(0, 0, L_ARENE, H_ARENE);
		lblFond.setIcon(new ImageIcon(FONDARENE));
		contentPane.add(lblFond);
		
		if (client) {
			txtSaisie = new JTextField();
			txtSaisie.setBounds(0, H_ARENE, L_ARENE, H_SAISIE);
			contentPane.add(txtSaisie);
			txtSaisie.setColumns(10);
			txtSaisie.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					txtSaisie_keyPressed(arg0);
				}
			});
		}
		
		JScrollPane jspChat = new JScrollPane();
		jspChat.setBounds(0, H_ARENE + H_SAISIE, L_ARENE, H_CHAT-H_SAISIE- 7*MARGE);
		contentPane.add(jspChat);
		jspChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JTextArea txtChat = new JTextArea();
		jspChat.setViewportView(txtChat);
		
		
		
	}
	
	
}
