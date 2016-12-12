import java.util.ArrayList;

public abstract class Player{
	
	String playerName;
	Hand myHand;
	Card choiceCard;
	int score, scoreToAdd, oldScore;
	boolean isOut;
	
	Player(String name){
		playerName=name;
		myHand=new Hand();
		score=0;
		oldScore=0;
		scoreToAdd=0;
		isOut=false;
	}
	
	public void TakeCard(Card newCard){ 
		myHand.cards.add(newCard);
	}
	
	public abstract void playCard();
	public abstract int groupCards();
	
	protected boolean isSequence(Hand inputHand){
		// Make a copy of the input cards as this method will change some values
		ArrayList<Card> testCards = new ArrayList<Card>(inputHand.cards.size());
		for (Card card : inputHand.cards){
			testCards.add(new Card(card));
		}
		
		final int size=testCards.size();
		
		//check if all wilds
		boolean allWilds=true;
		for(int j=0;j<size-1 && allWilds;j++){
			if(!testCards.get(j).isWild())
				allWilds=false;
		}
		if(allWilds)
			return true;
		//check for same suit
		int suit=0;
		for(int i=0;i<size;i++){
			if(!testCards.get(i).isWild()){
				suit=testCards.get(i).suit;
				break;
			}
		}
		for(int i=0;i<size;i++){
			if(!testCards.get(i).isWild()){
				if(suit!=testCards.get(i).suit)
					return false;
			}
		}
		//handle if the first card is a wild
		if(testCards.get(0).isWild()){
			// find first non-wild card
			int k=0;
			for(int i=1;i<size-1 && k==0;i++){
				if(!testCards.get(i).isWild()){
					k=i;
					//set the first wild card to this number
					testCards.get(0).number=testCards.get(k).number-i;
					if(testCards.get(0).number<3)
						return false;
				}
				if(testCards.get(i).number<3)
					return false;
			}
		}
		boolean isSequence=true;
		//check for a sequence
		for(int i=1;i<size && isSequence; i++){
			if(testCards.get(i).isWild())
				testCards.get(i).number=testCards.get(0).number+i;
			else if(testCards.get(i).number!=testCards.get(0).number+i)
				isSequence=false;
		}
		return isSequence && testCards.get(testCards.size()-1).number<14;
	}
	
	protected boolean isSet(Hand testHand){
		final int size=testHand.cards.size();
		ArrayList<Card> testCards=testHand.cards;
		if(size<3)
			return false;
		//check if all wilds
		boolean allWilds=true;
		for(int j=0;j<size-1 && allWilds;j++){
			if(!testCards.get(j).isWild())
				allWilds=false;
		}
		if(allWilds)
			return true;
		int num=0;
		for(int i=0;i<size;i++){
			if(!testCards.get(i).isWild()){
				num=testCards.get(i).number;
				break;
			}
		}
		for(int i=0;i<size;i++){
			if(!testCards.get(i).isWild()){
				if(num!=testCards.get(i).number)
					return false;
			}
		}
		return true;
	}
}


