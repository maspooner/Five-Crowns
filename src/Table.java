import java.util.Random;


public class Table {
	static Deck Discards=new Deck();
	static Deck DrawPile=new Deck();
	static Player [] Players;
	static int currentPlayer=0;
	static int currentDealer=0;
	static int numPlayers=0;
	static int currentWild=3;
	static final int MAX_HAND=13;
	
	public static void setNumPlayers(int i){
		numPlayers=i;
		Players=new Player[numPlayers];
	}
	public static void changePlayer(){
		currentPlayer=nextPlayer();
	}

	public static Player getCurrentPlayer(){
		return Players[currentPlayer];
	}
	
	public static int nextPlayer(){
		int nextPlayer;
		nextPlayer=currentPlayer+1;
		if(nextPlayer==numPlayers)
			nextPlayer=0;
		return nextPlayer;
	}
	
	public static Card getTopCard(){
		return Discards.theStack.get(0);
	}
		
	public static void resetTable(){
		for(int i=0;i<numPlayers;i++){
			Player player=Players[i];
			player.isOut=false;
			while(player.myHand.cards.size()!=0){
				DrawPile.theStack.add(player.myHand.removeCard(0));
			}
		}
		while (Discards.theStack.size()!=0){
			Card nextCard=Discards.theStack.remove(0);
			DrawPile.theStack.add(nextCard);
		}
		DrawPile.shuffle();
		currentWild=3;
	}
	
	public static void newRound(){
		for(Player player : Players){
			player.isOut=false;
			if(player instanceof Human){
				Human h=(Human) player;
				h.actionCommand="";
			}
			while(player.myHand.cards.size()!=0){
				DrawPile.theStack.add(player.myHand.removeCard(0));
			}
		}
		while (Discards.theStack.size()!=0){
			Card nextCard=Discards.theStack.remove(0);
			DrawPile.theStack.add(nextCard);
		}
		DrawPile.shuffle();
		currentWild++;
	}
	
	public static int findWinner(){
		int winner=0;
		for(int i=1;i<Players.length;i++){
			if(Players[i].score<Players[winner].score)
				winner=i;
		}
		return winner;
	}
	
	public static String getDirection(){
		String direction="";
		int lineBreak=0;
		for(int i=currentPlayer;i<numPlayers;i++){
			direction+=Players[i].playerName;
			if(!(currentPlayer==0 && i==numPlayers-1))
				direction+=" >> ";
			lineBreak++;
			if(lineBreak%2==0)
				direction+="\n";
		}
		for(int j=0;j<currentPlayer;j++){
			direction+=Players[j].playerName;
			if(j!=currentPlayer-1)
				direction+=" >> ";
			lineBreak++;
			if(lineBreak%2==0)
				direction+="\n";
		}
		return direction;
	}
	
	public static void deal(){
		for(int i=0;i<currentWild;i++){
			for(int j=0;j<numPlayers;j++){
				Table.Players[j].TakeCard(DrawPile.drawCard());
			}
		}
	}
	
	public static void pickRandomDealer(){
		Random r=new Random();
		currentPlayer=r.nextInt(numPlayers);
		currentDealer=currentPlayer;
	}
	
	public static void nextDealer(){
		currentPlayer=currentDealer+1==numPlayers?0:currentDealer+1;
		currentDealer=currentPlayer;
		System.out.println(getCurrentPlayer().playerName+" goes first");
	}
	
	public static boolean isSomeoneOut(){
		for(int i=0;i<numPlayers;i++){
			if(Players[i].isOut && i!=currentPlayer)
				return true;
		}
		return false;
	}
}
