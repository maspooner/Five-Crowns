

public class Card{
	
	//card counts
	//2 of each card
	//6 jokers
	//11 cards per suit (3 to king)
	//116 cards total
	
	//fields
	int number;
	int suit;
	String suitString;
	String name;
	
	// Copy constructor
	Card(Card cloneCard){
		this.name = cloneCard.name;
		this.number = cloneCard.number;
		this.suit = cloneCard.suit;
		this.suitString = cloneCard.suitString;
	}
	
	Card(int number, int suit){
		this.number=number;
		this.suit=suit;
		
		switch (number){
			case 3: name="Three"; break;
			case 4: name="Four"; break;
			case 5: name="Five"; break;
			case 6: name="Six"; break;
			case 7: name="Seven"; break;
			case 8: name="Eight"; break;
			case 9: name="Nine"; break;
			case 10: name="Ten"; break;
			case 11: name="Jack"; break;
			case 12: name="Queen"; break;
			case 13: name="King"; break;
			case 14: name=""; break;
		}
		switch(suit){
			case 1: suitString="Spades"; break;
			case 2: suitString="Clubs"; break;
			case 3: suitString="Diamonds"; break;
			case 4: suitString="Hearts"; break;
			case 5: suitString="Stars"; break;
			case 6: suitString="Joker"; break;
		}
	}
	
	public void print(){
		System.out.print(getName());
	}
	
	public String getName(){
		if(suit==6)
			return suitString;
		else
			return name+" of "+suitString;
	}
	
	public boolean isEqual(Card card){
		return suit==card.suit&&number==card.number;
	}
	
	public int getPoints(){
		return (number>=3) && (number<=13) && (number!=Table.currentWild) ? number : 20;
	}
	
	public boolean isWild(){
		return number==14 || suit==6 || number==Table.currentWild;
	}
	
	public String getPictureFileName(){
		return number+suitString;
	}
}