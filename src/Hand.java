import java.util.ArrayList;


public class Hand{
	
	ArrayList<Card> cards=new ArrayList<Card>();
	
	Hand(){
	}
	Hand(ArrayList<Card> newCards) {
		cards = newCards;
	}
	Hand(Hand newHand){
		cards.addAll(newHand.cards);
	}
	
	public Card play(int index){
		return cards.remove(index);
	}
	
	public void sort(boolean isBackwards){
		for(int i=0;i<(cards.size()-1);i++){
		    for(int j=0;j<(cards.size()-1);j++){
	        	if(isBackwards){
	        		if(cards.get(j).number<cards.get(j+1).number)
		                swap(j,j+1);
	        	}
	        	else{
		            if(cards.get(j).number>cards.get(j+1).number)
		                swap(j,j+1);
	        	}
		    }
		}
	}
	
	public void swap(int i, int j){
		Card temp=cards.get(i);
		cards.set(i,cards.get(j));
		cards.set(j,temp);
	}
	public void print(){
		for(int i=0;i<cards.size();i++){
		    System.out.print((i+1)+". ");
		    cards.get(i).print();
		    System.out.println();
		}
		System.out.println();
	}
	public void printLine(){
		if(cards.size()==0){
			System.out.println("None");
			return;
		}
		cards.get(0).print();
		for(int i=1;i<cards.size();i++){
			System.out.print(", ");
			cards.get(i).print();
		}
		System.out.println();
	}
	
	public int numCards(){
		return cards.size();
	}
	
	public Card getCard(int i){
		return cards.get(i);
	}
	
	public void setCard(int i, Card newCard){
		cards.set(i, newCard);
	}
	
	public void addCard(Card newCard){
		cards.add(newCard);
	}
	
	public Card removeCard(int i){
		return cards.remove(i);
	}
	
	public int getTotalPoints(){
		int total=0;
		for(int i=0;i<cards.size();i++){
			total+=cards.get(i).getPoints();
		}
		return total;
	}
	
	public boolean matchesNumber(Card card) {
		boolean matches = false;
		
		for(int i=0; i<cards.size() && !matches; i++)
			matches = (cards.get(i).number == card.number);
		
		return matches;
	}
	
	public boolean findDifference(Hand subset){
		return cards.removeAll(subset.cards);
	}

}