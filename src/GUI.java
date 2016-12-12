import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;


public class GUI{
	
	public static JFrame frame=new JFrame("Five Crowns: The Five Suited Card Game");
	public static ImagePanel cardPanel=new ImagePanel();
	private static JLabel wildLabel=new JLabel();
	private static JLabel outLabel=new JLabel();
	private static JLabel turnLabel=new JLabel();
	private static JButton drawPileButton=new JButton();
	private static JButton discardButton=new JButton();
	private static JTable playerList;
	private static JScrollPane listJSP=new JScrollPane();
	private static JTextArea directionArea=new JTextArea();
	private static JMenuItem newGame;
	
	public static JButton endButton=new JButton();
	public static JButton outButton=new JButton();
	public static JTextArea console=new JTextArea();
	
	private static ArrayList<CardButton> cardButtons;
	public static int selectedCard=-1;
	
	public static final Dimension FRAME_SIZE=new Dimension(1020,750);
	public static final Dimension SMALL_SIZE=new Dimension(800,600);
	
	public static final Font defaultFont=new Font("sans serif", Font.BOLD, 22);
	private static final Font consoleFont=new Font("sans serif", Font.ITALIC, 26);
	private static final Font smallFont=new Font("sans serif", Font.BOLD, 18);
	
	public static volatile boolean isDiscardPressed=false;
	public static volatile boolean isDrawPressed=false;
	public static volatile boolean isEnabled=false;
	public static volatile boolean isDiscarding=false;
	public static volatile boolean isTurnOver=false;
	public static volatile boolean isGoOutPressed=false;
	public static boolean isSmall=false;
	
	/*TODO:
	 * pre-load tutorial pictures?
	 * line up totals for points dialog
	 * threads
	 * remove all println's
	 * use less memory
	 * wrap directionArea for long player names?
	 * add reset button to go out dialog
	 * fix turn rotation
	 * cheat codes!
	 * game where you unlock different codes that do stuff
	 * music
	 */
	
	public GUI(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				init();
			}
		});
	}
	
	private static void setupSplitPane(JSplitPane jsp){
		jsp.setDividerSize(1);
		jsp.setEnabled(false);
	}
	
	public static void setText(String s){
		console.setText(s);
	}
	
	public static void setNewGameEnabled(boolean enabled){
		newGame.setEnabled(enabled);
	}
	
	private void changeWindowSize(){
		init();
		setNewGameEnabled(!Main.isRunning);
		if(Main.isRunning)
			createCardButtons();
	}
	
	private void init(){
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame.setSize(isSmall ? SMALL_SIZE : FRAME_SIZE);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(IO.getImage(IO.CARD_BACK));
		frame.setVisible(true);
		frame.setJMenuBar(getMenuBar());
		
		cardPanel.setLayout(null);
		cardPanel.setImage(IO.getImage(IO.WOOD));
		
		//split panes
		JSplitPane playAreaStats=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getPlayArea(), getStatsPanel());
		setupSplitPane(playAreaStats);
		playAreaStats.setDividerLocation(isSmall ? 520 : 720);
		
		JSplitPane allPanels=new JSplitPane(JSplitPane.VERTICAL_SPLIT, playAreaStats, cardPanel);
		setupSplitPane(allPanels);
		allPanels.setDividerLocation(isSmall ? 350 : 480);
		frame.setContentPane(allPanels);
	}
	
	private static ImagePanel getPlayArea(){
		ImagePanel playArea=new ImagePanel();
		playArea.setLayout(null);
		playArea.setImage(IO.getImage(IO.GREEN_FELT));
		console.setText("~~~  Five Crowns  ~~~");
		console.setFont(consoleFont);
		console.setEditable(false);
		console.setBounds(0, 0, 820, 50);
		console.repaint();
		playArea.add(console);
		endButton.setEnabled(false);
		endButton.setIcon(new ImageIcon(IO.getImage(IO.END_BUTTON)));
		endButton.setBounds(0, 50, 150, 50);
		endButton.repaint();
		endButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(!isEnabled)
					isTurnOver=true;
			}
		});
		playArea.add(endButton);
		outButton.setEnabled(false);
		outButton.setIcon(new ImageIcon(IO.getImage(IO.OUT_BUTTON)));
		outButton.setBounds(160, 50, 150, 50);
		outButton.repaint();
		outButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				isGoOutPressed=true;
				isTurnOver=true;
			}
		});
		playArea.add(outButton);
		
		//decks
		if(isSmall)
			discardButton.setBounds(105, 120, 150, 210);
		else
			discardButton.setBounds(125, 150, 150, 210);
		discardButton.repaint();
		discardButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(isEnabled)
					isDiscardPressed=true;
			}
		});
		playArea.add(discardButton);
		
		if(isSmall)
			drawPileButton.setBounds(285, 120, 150, 210);
		else
			drawPileButton.setBounds(325, 150, 150, 210);
		try {
			drawPileButton.setIcon(new ImageIcon(IO.getImage(IO.CARD_BACK)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		drawPileButton.repaint();
		drawPileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(isEnabled && !isDiscarding)
					isDrawPressed=true;
			}
		});
		playArea.add(drawPileButton);
		return playArea;
	}
	
	private static ImagePanel getStatsPanel(){
		ImagePanel stats=new ImagePanel();
		stats.setLayout(null);
		
		if(isSmall)
			listJSP.setBounds(0, 0, 270, 150);
		else
			listJSP.setBounds(0, 0, 290, 250);
		listJSP.repaint();
		stats.add(listJSP);
		
		directionArea.setEditable(false);
		directionArea.setFont(new Font("sans serif", Font.PLAIN, 18));
		if(isSmall)
			directionArea.setBounds(0, 150, 270, 110);
		else
			directionArea.setBounds(0, 250, 290, 100);
		directionArea.repaint();
		stats.add(directionArea);
		
		wildLabel.setFont(isSmall ? smallFont : defaultFont);
		if(isSmall)
			wildLabel.setBounds(0, 260, 290, 30);
		else
			wildLabel.setBounds(0, 350, 290, 40);
		wildLabel.repaint();
		stats.add(wildLabel);
		outLabel.setFont(isSmall ? smallFont : defaultFont);
		if(isSmall)
			outLabel.setBounds(0, 290, 290, 30);
		else
			outLabel.setBounds(0, 390, 290, 40);
		outLabel.repaint();
		outLabel.setOpaque(true);
		stats.add(outLabel);
		turnLabel.setFont(isSmall ? smallFont : defaultFont);
		if(isSmall)
			turnLabel.setBounds(0, 320, 290, 30);
		else
			turnLabel.setBounds(0, 430, 290, 40);
		turnLabel.repaint();
		stats.add(turnLabel);
		return stats;
	}
	
	private JMenuBar getMenuBar(){
		JMenuBar bar=new JMenuBar();
		//file menu
		JMenu file=new JMenu("File");
		newGame=new JMenuItem("New Game");
		newGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Main.isStarting=true;
			}
		});
		JMenuItem prefs=new JMenuItem("Manual Save");
		prefs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				IO.writeToFile();
			}
		});
		JMenuItem scores=new JMenuItem("Lowest Scores");
		scores.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Dialogs.showHighScoresDialog(null);
			}
		});
		JMenuItem exit=new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		file.add(newGame);
		file.add(prefs);
		file.add(scores);
		file.add(exit);
		//sizes menu
		JMenu sizes=new JMenu("Window Size");
		ButtonGroup bGroup=new ButtonGroup();
		ActionListener sizesListener=new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				isSmall=ae.getActionCommand().equals("2");
				changeWindowSize();
			}
		};
		JRadioButtonMenuItem radio=new JRadioButtonMenuItem("1020x750");
		radio.setActionCommand("1");
		radio.addActionListener(sizesListener);
		if(!isSmall)
			radio.setSelected(true);
		bGroup.add(radio);
		sizes.add(radio);
		
		radio=new JRadioButtonMenuItem("800x600");
		radio.setActionCommand("2");
		radio.addActionListener(sizesListener);
		if(isSmall)
			radio.setSelected(true);
		bGroup.add(radio);
		sizes.add(radio);
		
		//help menu
		JMenu help=new JMenu("Help");
		JMenuItem rules=new JMenuItem("Rules");
		rules.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Dialogs.showTextHelpDialog();
			}
		});
		help.add(rules);
		JMenuItem tutor=new JMenuItem("Tutorial");
		tutor.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Dialogs.showPictureHelpDialog();
			}
		});
		help.add(tutor);
		JMenuItem about=new JMenuItem("About");
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				Dialogs.showAboutDialog();
				
			}
		});
		help.add(about);
		
		bar.add(file);
		bar.add(sizes);
		bar.add(help);
		return bar;
	}
	
	public static void updateUI(){
		//use: update every turn
		if(Table.isSomeoneOut()){
			outLabel.setBackground(Color.RED);
			outLabel.setText("Someone is out");
		}
		else{
			outLabel.setBackground(Color.GREEN);
			outLabel.setText("No one is out");
		}
		turnLabel.setText("It\'s "+Table.getCurrentPlayer().playerName+"\'s turn");
		turnLabel.repaint();
		setText("");
		directionArea.setText(Table.getDirection());
	}
	
	public static void updateRoundUI(){
		//use: reset after every round/new game
		
		//update wildLabel
		if(Table.currentWild<=10)
			wildLabel.setText(Table.currentWild+"\'s are wild");
		else{
			String wildText="";
			switch(Table.currentWild){
				case 11: wildText="Jack"; break;
				case 12: wildText="Queen"; break;
				case 13: wildText="King"; break;
			}
			wildLabel.setText(wildText+"s are wild");
		}
		outLabel.setBackground(Color.GREEN);
		outLabel.setText("No one is out");
		setText("");
		
		if(playerList==null){
			initTable();
		}
		else{
			updateTable();
		}
	}
	
	public static void updateTable(){
		for(int i=0;i<Table.numPlayers;i++){
			playerList.getModel().setValueAt(Table.Players[i].playerName, i, 0);
			playerList.getModel().setValueAt(Table.Players[i].score, i, 1);
		}
		playerList.repaint();
	}
	
	private static void initTable(){
		String[] columnTitles=new String[]{"Name", "Score"};
		Object[][] tableData=new Object[Table.numPlayers][2];
		for(int i=0;i<Table.numPlayers;i++){
			tableData[i][0]=Table.Players[i].playerName;
			tableData[i][1]=Table.Players[i].score;
		}
		playerList=new JTable(tableData, columnTitles);
		playerList.setRowHeight(50);
		playerList.setEnabled(false);
		playerList.getTableHeader().setReorderingAllowed(false);
		playerList.getTableHeader().setResizingAllowed(false);
		playerList.repaint();
		listJSP.setViewportView(playerList);
	}
	
	public static void resetUI(){
		console.setText("~~~  Five Crowns  ~~~");
		cardPanel.removeAll();
		cardPanel.repaint();
		discardButton.setIcon(null);
		outLabel.setText("");
		outLabel.setBackground(null);
		turnLabel.setText("");
		wildLabel.setText("");
	}
	
	public static void createCardButtons(){
		cardPanel.removeAll();
		cardButtons=new ArrayList<CardButton>();
		for(int i=0;i<Table.getCurrentPlayer().myHand.cards.size();i++){
			addCardButton(i);
		}
		cardPanel.repaint();
	}
	
	private static void addCardButton(int i){
		CardButton button=new CardButton();
		try {
			button.setIcon(IO.getImageIcon(Table.getCurrentPlayer().myHand.getCard(i)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		button.addActionListener(swapAL);
		button.setActionCommand(Integer.toString(i));
		button.setBounds(isSmall ? i*50 : i*60+20, 0, 150, 210);
		button.repaint();
		cardButtons.add(button);
		cardPanel.add(button, 0);
	}
	
	public static void changeTopCard(){
		try{
			discardButton.setIcon(IO.getImageIcon(Table.getTopCard()));
		}
		catch(Exception e){
			discardButton.setIcon(null);
		}
	}
	
	private static ActionListener swapAL=new ActionListener(){
		public void actionPerformed(ActionEvent ae) {
			int i=Integer.parseInt(ae.getActionCommand());
			Hand currentHand=Table.getCurrentPlayer().myHand;
			if(selectedCard!=-1){
				if(selectedCard!=i){
					currentHand.swap(i, selectedCard);
					cardButtons.get(selectedCard).setIcon(IO.getImageIcon(currentHand.getCard(selectedCard)));
				}
				selectedCard=-1;
				cardButtons.get(i).setIcon(IO.getImageIcon(currentHand.getCard(i)));
			}
			else{
				selectedCard=i;
				cardButtons.get(i).setIcon(IO.getInvertedIcon(currentHand.getCard(i)));
			}
		}
	};
	
	@SuppressWarnings("serial")
	public static class CardButton extends JButton{
		boolean isSelected=false;
	}
	
	@SuppressWarnings("serial")
	public static class ImagePanel extends JPanel{
		private Image image;
		public void setImage(Image i){
			image=i;
		}
		@Override
		protected void paintComponent(Graphics g) {
			if(image!=null)
				g.drawImage(image, 0, 0, null);
			else
				super.paintComponent(g);
		}
	}
}
