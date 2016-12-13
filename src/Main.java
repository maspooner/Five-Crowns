/*
 * version 1.3.1
 * Five Crowns
 * by Matt, Peter, and Emily Spooner
 * 
 * Created: Jun 2013
 * Last edited: Sep 27 2013
 */

public class Main{
	public static final boolean IS_TEST = true;
	
	private static Thread currentGame;
	
	public static volatile boolean isStarting=false;
	public static volatile boolean isRunning=false;
	public static boolean isFirstTime=false;
	
	public static final String version="1.3.1";
	
	public static void main(String[] args){
		IO.loadCards();
		IO.setupHighScores();
		new GUI();
		GUI.resetUI();
		if(isFirstTime)
			Dialogs.showFirstTimeDialog();
		while(true){
			while(!isStarting){
			}
			isStarting=false;
			currentGame=new Thread(new Runnable(){
				public void run() {
					IO.writeToFile();
					if(setup()){
						isRunning=true;
						GUI.setNewGameEnabled(false);
						gameLoop();
					}
					GUI.setNewGameEnabled(true);
					isRunning=false;
				}
			});
			currentGame.start();
		}
	}
	
	private static void gameLoop(){
		//Pick random first player
		Table.pickRandomDealer();
		do{
			GUI.updateRoundUI();
			//Deal
			Table.deal();
			
			// Deal one card to the discard pile
			Table.Discards.theStack.add(0, Table.DrawPile.drawCard());
			
			Table.nextDealer();
			
			GUI.updateUI();
			//Player's turn
			Table.getCurrentPlayer().playCard();
			
			while(!Table.getCurrentPlayer().isOut){
				// Change to the next player
				Table.changePlayer();
				
				GUI.updateUI();
				
				//Player's turn
				Table.getCurrentPlayer().playCard();
				
			}
			
			Player current=Table.getCurrentPlayer();
			current.scoreToAdd=0;
			current.oldScore=current.score;
			
			//make everyone else group their cards
			for(int i=0;i<Table.numPlayers-1;i++){
				Table.changePlayer();
				Player p=Table.getCurrentPlayer();
				GUI.updateUI();
				if(!p.isOut){
					p.playCard();
					
					if(!p.isOut){
						p.scoreToAdd=p.groupCards();
						// Debug
//						System.out.println(p.playerName+" cards:");	
//						for (int j=0; j<groups.size(); j++)
//							groups.get(j).printLine();
						p.oldScore=p.score;
						p.score+=p.scoreToAdd;
					}
					else{
						p.scoreToAdd=0;
						p.oldScore=p.score;
					}
						
				}
				else{
					p.scoreToAdd=0;
					p.oldScore=p.score;
				}
			}
			
			//print scores
			Dialogs.showScoresDialog();
			
			Table.newRound();
			
		} while(Table.currentWild!=Table.MAX_HAND+1);
		
		GUI.resetUI();
		GUI.updateTable();
		
		Player winner=Table.Players[Table.findWinner()];
		Dialogs.showWinDialog(winner.playerName);
		if(winner instanceof Human)
			Dialogs.showHighScoresDialog(winner);
		
		Table.resetTable();
	}
	
	public static void main2(String[] args){
		//TODO remove
		Table.currentWild=6;
		Hand myHand=new Hand();
		myHand.addCard(new Card(3,1));
		myHand.addCard(new Card(4,1));
		myHand.addCard(new Card(6,4));
		myHand.addCard(new Card(14,6));
		
		myHand.addCard(new Card(7,3));
		myHand.addCard(new Card(8,5));
		
//		myHand.addCard(new Card(10,1));
//		
//		myHand.addCard(new Card(10,1));
//		myHand.addCard(new Card(10,4));
//		myHand.addCard(new Card(4,3));
//		myHand.addCard(new Card(9,3));
		
		Computer c=new Computer("Test");
		c.myHand=myHand;
		c.groupCards();
//		Table.currentWild=5;
//		Computer c=new Computer("computer");
//		c.myHand=myHand;
//		ArrayList<Hand> hands=new ArrayList<Hand>();
//		hands.add(myHand);
//		System.out.println(c.groupCards(hands));
//		c.goOut();
//		System.out.println(c.isOut);
	}
	
	private static boolean setup(){
		Table.DrawPile.init();
		Table.DrawPile.shuffle();
		
		int num=Dialogs.showSetupDialog();
		if(num==0)
			return false;
		Table.setNumPlayers(num);
		Dialogs.showPlayerDialog();
		return true;
	}
}
