import java.util.ArrayList;
import java.util.Random;

public class Deck{
	
	ArrayList<Card> theStack = new ArrayList<Card>();
	
	public void init(){
		//colors
		for(int suit=1; suit<=5;suit++){		
			// Two of each card for this color
			for(int Num=3; Num<=13;Num++){
				for(int j=1;j<=2;j++){
					theStack.add(new Card(Num,suit));
				}
			}
		}
		// Wild cards
		for(int j=1;j<=6;j++){
			theStack.add(new Card(14,6));
		}
	}
	
	public void shuffle(){
		Random rand=new Random();
		for(int i=0;i<theStack.size();i++){
			int j= rand.nextInt(theStack.size()-1);
			Card tempCard=theStack.get(i);
			theStack.set(i, theStack.get(j));
			theStack.set(j, tempCard);
		}
	}
	
	public void print(){
		for(int i=0;i<116;i++){
			theStack.get(i).print();
			System.out.print(", ");
			if(i%10 == 0)
				System.out.println();
		}
	}
	
	public Card drawCard(){
		if(Table.DrawPile.theStack.size()==0){
			// Remove the top card from the Discards pile
			Card topCard = Table.Discards.theStack.remove(0);
			
			Table.Discards.shuffle();
			while (Table.Discards.theStack.size()!=0){
				Card nextCard=Table.Discards.theStack.remove(0);
				Table.DrawPile.theStack.add(0,nextCard);
			}
			
			// Put the top card back into the Discards pile
			Table.Discards.theStack.add(topCard);
		}
		return theStack.remove(0);
	}
}
