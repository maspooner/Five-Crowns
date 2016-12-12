import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

public class Dialogs {
	private static ArrayList<GUI.CardButton> outCardButtons;
	private static ArrayList<Integer> orderedCards=new ArrayList<Integer>();
	
	public static int showSetupDialog(){
		Object[] options=new Object[]{2,3,4,5,6,7};
		Integer choice=(Integer) JOptionPane.showInputDialog(GUI.frame, "How many players?", "Game Setup", JOptionPane.PLAIN_MESSAGE, null, options, 3);
		return choice!=null ? choice : 0;
	}
	
	public static void showPlayerDialog(){
		final JDialog d=new JDialog(GUI.frame, "Player Setup");
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setModal(true);
		d.setResizable(false);
		d.setLocation(GUI.frame.getLocation());
		
		JPanel panel=new JPanel();
		panel.setLayout(null);
		JTextField jft;
		JCheckBox jcb;
		int height=100;
		for(int i=0;i<Table.numPlayers;i++){
			jft=new JTextField("Player "+(i+1));
			jcb=new JCheckBox("Computer");
			jft.setBounds(50, i*55, 200, 50);
			jft.repaint();
			panel.add(jft);
			jcb.setBounds(300, i*55, 200, 50);
			jcb.repaint();
			panel.add(jcb);
			height+=50;
		}
		JButton okay=new JButton("Done");
		okay.setBounds(150, height-70, 150, 40);
		okay.repaint();
		okay.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae) {
				d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
			}
		});
		panel.add(okay);
		
		d.getContentPane().add(panel);
		d.setSize(new Dimension(450,height));
		d.setVisible(true);
		
		int iter=-1;
		String name="";
		for(int i=0;i<panel.getComponentCount();i++){
			Component comp=panel.getComponent(i);
			if(i!=panel.getComponentCount()-1){
				if(i%2==0){
					JTextField foo=(JTextField) comp;
					name=foo.getText();
				}
				else{
					iter++;
					JCheckBox bar=(JCheckBox) comp;
					if(bar.isSelected()){
						Table.Players[iter]=new Computer("Computer "+(iter+1));
					}
					else{
						Table.Players[iter]=new Human(name.equals("") ? "Player "+(iter+1) : name);
					}
				}
			}
		}
		d.setVisible(false);
	}
	
	public static void showTurnChangeDialog(){
		String message="It's "+Table.getCurrentPlayer().playerName+"\'s turn!";
		message+=Table.isSomeoneOut() ? "\nLAST TURN!" : "";
		JOptionPane.showMessageDialog(GUI.frame, message, "New Turn", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static ArrayList<Integer> showOutDialog(boolean[] used, boolean isMandatory){
		final JDialog d=new JDialog(GUI.frame, isMandatory ? "Go Out" : "Group Cards");
		
		ActionListener outAL=new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Human h= (Human) Table.getCurrentPlayer();
				h.actionCommand=ae.getActionCommand();
				d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
			}
		};
		d.setModal(true);
		d.setResizable(false);
		d.setLocation(GUI.frame.getLocation());
		
		GUI.ImagePanel outPanel=new GUI.ImagePanel();
		outPanel.setLayout(null);
		outPanel.setImage(IO.getImage(IO.WOOD));
		outPanel.setBounds(0, 0, GUI.isSmall ? GUI.SMALL_SIZE.width : GUI.FRAME_SIZE.width, 210);
		outPanel.repaint();
		
		orderedCards=new ArrayList<Integer>();
		outCardButtons=new ArrayList<GUI.CardButton>();
		for(int i=0;i<Table.getCurrentPlayer().myHand.cards.size();i++){
			if(used[i]==false){
				GUI.CardButton button=new GUI.CardButton();
				button.setIcon(IO.getImageIcon(Table.getCurrentPlayer().myHand.getCard(i)));
				button.addActionListener(selectAL);
				button.setActionCommand(Integer.toString(i));
				button.setBounds(GUI.isSmall ? i*51 : i*60+20, 0, 150, 210);
				button.repaint();
				outCardButtons.add(button);
				outPanel.add(button, 0);
			}
		}
		outPanel.repaint();
		
		JButton setButton=new JButton("Set");
		setButton.setBounds(GUI.isSmall ? 75 : 100, 230, 150, 50);
		setButton.repaint();
		setButton.addActionListener(outAL);
		setButton.setActionCommand("set");
		outPanel.add(setButton);
		
		JButton seqButton=new JButton("Sequence");
		seqButton.setBounds(GUI.isSmall ? 300 : 400, 230, 150, 50);
		seqButton.repaint();
		seqButton.addActionListener(outAL);
		seqButton.setActionCommand("seq");
		outPanel.add(seqButton);
		
		if(isMandatory){
			JButton doneButton=new JButton("Done");
			doneButton.setBounds(GUI.isSmall ? 525 : 700, 230, 150, 50);
			doneButton.repaint();
			doneButton.addActionListener(outAL);
			doneButton.setActionCommand("done");
			outPanel.add(doneButton);
		}
		else{
			JButton cancelButton=new JButton("Cancel");
			cancelButton.setBounds(GUI.isSmall ? 525 : 700, 230, 150, 50);
			cancelButton.repaint();
			cancelButton.addActionListener(outAL);
			cancelButton.setActionCommand("can");
			outPanel.add(cancelButton);
		}
		
		d.getContentPane().add(outPanel);
		d.setSize(GUI.isSmall ? GUI.SMALL_SIZE.width : GUI.FRAME_SIZE.width, 330);
		d.setVisible(true);
		return orderedCards;
	}
	
	public static void showScoresDialog(){
		String message="End of Round\n~~~~~~~~~~~~~~~~\nName\t\t\tTotal\n";
		for(int i=0;i<Table.numPlayers;i++){
			Player p=Table.Players[i];
			message+=p.playerName+": "+p.oldScore+" + "+p.scoreToAdd+" = \t"+p.score+"\n";
		}
		JTextArea jta=new JTextArea(message);
		jta.setFont(new Font("sans serif", Font.PLAIN, 18));
		jta.setTabSize(3);
		jta.setEditable(false);
		JOptionPane.showMessageDialog(GUI.frame, jta, "Round Over", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	public static void showErrorDialog(){
		JOptionPane.showMessageDialog(GUI.frame, "Not a set/sequence!", "Error", JOptionPane.ERROR_MESSAGE, null);
	}
	
	public static void showWinDialog(String winner){
		JOptionPane.showMessageDialog(GUI.frame, winner+" wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static void showGroupedCardsDialog(ArrayList<Hand> cards, boolean wentOut){
		String playerOut=Table.getCurrentPlayer().playerName;
		final JDialog dialog=new JDialog(GUI.frame, wentOut ? playerOut+" went out!" : playerOut+"\'s groupings", true);
		dialog.setResizable(false);
		dialog.getContentPane().setLayout(null);
		dialog.setLocation(GUI.frame.getLocation());
		
		JLabel text=new JLabel(wentOut ? playerOut+" is out!" : playerOut+"\'s cards:");
		text.setFont(GUI.defaultFont);
		text.setBounds(10, 0, 800, 50);
		text.repaint();
		dialog.getContentPane().add(text);
		int windowWidth=GUI.isSmall ? 0 : 20;
		for(int i=0;i<cards.size();i++){
			for(int j=0;j<cards.get(i).cards.size();j++){
				GUI.CardButton button=new GUI.CardButton();
				button.setIcon(IO.getImageIcon(cards.get(i).cards.get(j)));
				button.setBounds(windowWidth, 55, 150, 210);
				button.repaint();
				dialog.getContentPane().add(button, 0);
				windowWidth+=GUI.isSmall ? 30 : 50;
			}
			windowWidth+=GUI.isSmall ? 130 : 150;
		}
		JButton ok=new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
			}
		});
		ok.setBounds(50, 300, 150, 50);
		ok.repaint();
		dialog.getContentPane().add(ok);
		dialog.setSize(windowWidth<400 ? 400 : windowWidth, 400);
		dialog.setVisible(true);
	}
	
	public static void showFirstTimeDialog(){
		JOptionPane.showMessageDialog(GUI.frame, IO.welcomeText, "Welcome, first time user!", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static void showAboutDialog(){
		JOptionPane.showMessageDialog(GUI.frame, IO.aboutText, "About Five Crowns v"+Main.version, JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static void showTextHelpDialog(){
		JOptionPane.showMessageDialog(GUI.frame, IO.helpText, "How to Play", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static void showPictureHelpDialog(){
		final JDialog d=new JDialog(GUI.frame, "Tutorial", true);
		Dimension size=GUI.isSmall ? GUI.SMALL_SIZE : GUI.FRAME_SIZE;
		IO.pictureCounter=0;
		
		d.setResizable(false);
		d.setSize(size);
		d.setLocation(GUI.frame.getLocation());
		d.getContentPane().setLayout(null);
		final JButton click=new JButton();
		click.setBounds(0, 0, size.width, size.height);
		click.setIcon(IO.getImageIcon());
		click.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				ImageIcon i=IO.getImageIcon();
				if(i==null)
					d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
				else
					click.setIcon(i);
			}
		});
		d.getContentPane().add(click);
		d.setVisible(true);
	}
	
	public static void showHighScoresDialog(Player winner){
		JTextArea text=new JTextArea("Lowest Scores:\n"+IO.printHighScores(winner));
		text.setEditable(false);
		text.setFont(GUI.defaultFont);
		JOptionPane.showMessageDialog(GUI.frame, text, "Lowest Scores", JOptionPane.PLAIN_MESSAGE, null);
	}
	
	private static ActionListener selectAL=new ActionListener(){
		public void actionPerformed(ActionEvent ae) {
			int i=Integer.parseInt(ae.getActionCommand());
			Hand currentHand=Table.getCurrentPlayer().myHand;
			GUI.CardButton currentButton=(GUI.CardButton) ae.getSource();
			Card picture=currentHand.getCard(i);
			currentButton.setIcon(currentButton.isSelected ? IO.getImageIcon(picture) : IO.getInvertedIcon(picture));
			if(currentButton.isSelected)
				orderedCards.remove(orderedCards.indexOf(i));
			else
				orderedCards.add(i);
			currentButton.isSelected=!currentButton.isSelected;
		}
	};
}
