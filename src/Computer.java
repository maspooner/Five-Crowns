import java.util.ArrayList;
import java.util.Arrays;

public class Computer extends Player {
	Computer(String name) {
		super(name);
	}
	
	@Override
	public void playCard(){
		Card topCard = Table.getTopCard();
		
		// Debug
//		System.out.println(playerName+"\'s cards:");
//		myHand.print();
//		System.out.println();

		// See if the card on the discard pile will create a match
		
		// Debug
//		System.out.println("See if the card on the discard pile will create a match");
		
		int iDiscard = -1;
		for (int i = 0; i < myHand.numCards() && iDiscard==-1; i++){
			// Create a hand using the top card of the discard in place of this card
			Hand testHand = new Hand(myHand);
			
			// Replace card i with the top of the discard
			testHand.setCard(i, topCard);

			// See if that hand allows us to go out
			if (canGoOut(testHand, false)!=null)
				iDiscard = i;
		}

		// If the top discard allows us to go out, then pick it up
		if (iDiscard != -1) {
			// Debug
//			System.out.print("Picked up the following card:  ");
//			topCard.print();
//			System.out.println();

			choiceCard = myHand.removeCard(iDiscard);
			TakeCard(Table.Discards.drawCard());
			Table.Discards.theStack.add(choiceCard);
		}
		else {
			// See if the top discard matches any of my cards or is a wild card
	
			// Debug
//			System.out.println("See if the top discard matches any of my cards or is a wild card");

			if (topCard.isWild() || myHand.matchesNumber(topCard)) {
				// Debug
//				System.out.print("Picked up the following card:  ");
//				topCard.print();
//				System.out.println();

				// Take the top card from the Discards pile
				TakeCard(Table.Discards.drawCard());

				// Find a card to discard
				choiceCard = findDiscard();
			}
			else {
				// Draw a card from the top of the Draw pile

				// Debug
//				System.out.println("Draw a card from the top of the Draw pile");

				Card topDrawCard =Table.DrawPile.drawCard();
				
				// Debug
//				System.out.print("Drew the following card:  ");
//				topDrawCard.print();
//				System.out.println();

				// See if that card will create a match
				for (int i = 0; i < myHand.numCards() && iDiscard==-1; i++){
					// Create a hand using the card from the draw pile
					Hand testHand=new Hand(myHand);
					
					// Replace card i with the top of the discard
					testHand.setCard(i, topDrawCard);
					
					// Debug
//					System.out.println("Test cards with Draw pile card:");
//					testHand.print();
//					System.out.println();

					// See if that hand allows us to go out
					if (canGoOut(testHand, false)!=null)
						iDiscard = i;
				}

				// If the top of the draw pile allows us to go out, then remove the card it replaces
				if (iDiscard != -1) {
					choiceCard = myHand.removeCard(iDiscard);
					TakeCard(topDrawCard);
				}
				else {
					TakeCard(topDrawCard);

					// Find a card to discard
					choiceCard = findDiscard();
				}
			}
			
			// Debug
//			System.out.print("Discard the following card:  ");
//			choiceCard.print();
//			System.out.println();

			Table.Discards.theStack.add(0, choiceCard);
		}
		
		//check if can go out
		ArrayList<Hand> groups=canGoOut(myHand, false);
		if(groups!=null){
			isOut=true;
			Dialogs.showGroupedCardsDialog(groups, true);
		}
		GUI.cardPanel.removeAll();
		GUI.cardPanel.repaint();
		//update GUI
		GUI.changeTopCard();
	}
	
	protected Card findDiscard(){
		int discardIndex = -1;
		ArrayList<Card> cards=myHand.cards;

		// Debug
//		System.out.println("Find discard for following cards:");
		myHand.print();
		System.out.println();

		// Count the number of each card
		int[] totals = new int[12];
		
		for (int i=0; i<cards.size(); i++) {
			int cardValue = cards.get(i).number;
			totals[cardValue-3]++;
		}
		
		// Debug
//		System.out.println("Total of each card:");
//		for (int i=0; i<12; i++)
//			System.out.println(i+3+":"+totals[i]);
//		System.out.println();

		// Find a value that has only 1 card (or 2 cards if none at 1)
		// Throwing away a high card
		int discardValue = -1;
		for (int i = 1; i<3 && discardValue == -1; i++){
			for (int j=10; j>=0 && discardValue == -1; j--){
				if (totals[j] == i && j+3 != Table.currentWild)
					discardValue = j + 3;
			}
		}

		// Debug
//		System.out.println("Discard value is:"+discardValue);

		// Remove the card that matches the discardValue
		for (int i=0; i<cards.size() && discardIndex == -1; i++) {
			if (cards.get(i).number==discardValue) {
				discardIndex = i;
			}
		}

		// Debug
//		System.out.println("Discard index is:"+discardIndex);
		
		Card returnCard = cards.remove(discardIndex);

		return returnCard;
	}

	@Override
	public int groupCards() {
		int points=0;
		boolean isLastGroupAGroup=true;
		ArrayList<Hand> groups=canGoOut(myHand, true);
		
		for(Hand h : groups){
			if(!(isSet(h) && isSequence(h)))
				isLastGroupAGroup=false;
		}
		if(!isLastGroupAGroup){
			for(int i=0;i<groups.get(groups.size()-1).cards.size();i++){
				Card c=groups.get(groups.size()-1).getCard(i);
				if(c.isWild() && myHand.cards.size()>5){
					groups.get(0).addCard(groups.get(groups.size()-1).removeCard(i));
					groups.get(0).cards=find(groups.get(0), 4).cards;
					System.out.println("\n\n***WILD REMOVED***\n\n");
					continue;
				}
				points+=c.getPoints();
			}
		}
		Dialogs.showGroupedCardsDialog(groups, false);
		return points;
	}

	
	protected ArrayList<Hand> canGoOut(Hand tempHand, boolean isMandatory){
		GUI.setText(playerName+" is thinking...");
		ArrayList<Hand> groups=new ArrayList<Hand>();
		Hand copy=new Hand(tempHand);

		while(copy.cards.size()!=0){
			copy.sort(false);
			int size=copy.cards.size();
			
			if(size<3 && size!=0){
				//debug
//				System.out.println(size+" left over");
//				System.out.println();
//				for(Hand h : groups)
//					h.printLine();
//				System.out.println("\nleft over:");
//				for(Card c : copy.cards){
//					c.print();
//					System.out.println();
//				}
				if(isMandatory){
					groups.add(copy);
					return groups;
				}	
				else
					return null;
			}
			else{
				//check if as it is it is a proper group
				if(isSet(copy) || isSequence(copy)){
					groups.add(copy);
					break;
				}
				else{
					Hand newGroup=getGroup(copy);
					if(newGroup==null){
						//debug
//						System.out.println();
//						for(Hand h : groups)
//							h.printLine();
//						System.out.println("\nleft over:");
//						for(Card c : copy.cards){
//							c.print();
//							System.out.println();
//						}
						if(isMandatory){
							groups.add(copy);
							return groups;
						}	
						else
							return null;
					}
					groups.add(newGroup);
				}
			}
		}
//		System.out.println();
//		for(Hand h : groups)
//			h.printLine();
		return groups;
	}

	private Hand find(Hand hand, int numberChosen){
		Hand copy=new Hand(hand);
		Hand wilds=new Hand();
		copy.sort(true);
		if(copy.cards.size()>5){
			for(int i=0;i<copy.cards.size();i++){
				Card c=copy.cards.get(i);
				if(c.isWild()){
					wilds.addCard(c);
					copy.cards.remove(c);
				}
			}
		}
		for(int j=0;j<wilds.cards.size()+1;j++){
			if(j!=0 && wilds.cards.size()!=0)
				copy.addCard(wilds.cards.remove(j-1));
			for(int i=numberChosen; i>=3;i--){
//				System.out.println("\nfinding "+i);
				Permutation perm=new Permutation(copy, i);
				//debug
//				int k=0; 
				while(true){
					Hand next=new Hand(perm.next());
					if(next.cards==null)
						break;
//					k++;
//					System.out.print(k+" ");
//					next.printLine();
					if(isSequence(next) || isSet(next)){
//						System.out.println("Found group of "+i);
						return next;
					}
				}
			}
		}
		return null;
	}
	
	private Hand getGroup(Hand hand){
		Hand matchedCards=new Hand();
		switch(hand.cards.size()){
			case 3:
				matchedCards=find(hand, 3);
				break;
			case 4:
			case 6:
			case 7:
				matchedCards=find(hand, 4);
				break;
			case 5:
				matchedCards=find(hand, 5);
				break;
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				matchedCards=find(hand, 6);
				break;
			default:
				System.err.println("error");
				System.exit(1);
		}
		if(matchedCards==null)
			return null;
		hand.cards.removeAll(matchedCards.cards);
		return matchedCards;
	}
	
	private static class Permutation{
		 private Card[] arr;
		 private int[] permSwappings;

		 public Permutation(Hand h, int permSize) {
			  this.arr = h.cards.toArray(new Card[h.cards.size()]);
			  this.permSwappings = new int[permSize];
			  for(int i = 0;i < permSwappings.length;i++)
			     permSwappings[i] = i;
		 }

		 public ArrayList<Card> next() {
			  if (arr == null)
			   return null;
	
			  Card[] res = Arrays.copyOf(arr, permSwappings.length);
			  //Prepare next
			  int i = permSwappings.length-1;
			  while (i >= 0 && permSwappings[i] == arr.length - 1) {
				   swap(i, permSwappings[i]); //Undo the swap represented by permSwappings[i]
				   permSwappings[i] = i;
				   i--;
			  }
			  
			  if (i < 0)
				  arr = null;
			  else {
				   int prev = permSwappings[i];
				   swap(i, prev);
				   int next = prev + 1;
				   permSwappings[i] = next;
				   swap(i, next);
			  }
	
			  return new ArrayList<Card>(Arrays.asList(res));
		 }

		 private void swap(int i, int j) {
			 Card temp = arr[i];
			 arr[i] = arr[j];
			 arr[j] = temp;
		 }
	}
}
