import java.util.ArrayList;


public class Human extends Player {

	String actionCommand="";
	
	Human(String name) {
		super(name);
	}
	
	@Override
	public void playCard(){
		//TODO test \/
		GUI.endButton.setEnabled(false);
		GUI.endButton.repaint();
		// Announce current player's turn
		Dialogs.showTurnChangeDialog();
		
		// Print player's hand
		GUI.createCardButtons();
		
		// Print the top card in the discard pile
		GUI.changeTopCard();
		//draw card
		GUI.setText("Draw a card from the draw pile or discards.");
		Card newCard;
		GUI.isEnabled=true;
		while(!GUI.isDrawPressed && !GUI.isDiscardPressed){
		}
		if(GUI.isDiscardPressed){
			GUI.isDiscardPressed=false;
			newCard=Table.Discards.drawCard();
		}
		else{
			GUI.isDrawPressed=false;
			newCard=Table.DrawPile.drawCard();
		}
		TakeCard(newCard);
		GUI.setText("You picked up a "+newCard.getName());
		GUI.selectedCard=-1;
		GUI.changeTopCard();
		GUI.createCardButtons();
		
		while(true){
			GUI.isDiscarding=true;
			while(!GUI.isDiscardPressed){
			}
			GUI.isDiscardPressed=false;
			if(GUI.selectedCard!=-1){
				choiceCard = myHand.removeCard(GUI.selectedCard);
				Table.Discards.theStack.add(0, choiceCard);
				GUI.setText("You discarded a "+choiceCard.getName());
				break;
			}
		}
		GUI.isDiscarding=false;
		GUI.selectedCard=-1;
		GUI.changeTopCard();
		GUI.createCardButtons();
		GUI.isEnabled=false;
		
		GUI.endButton.setEnabled(true);
		GUI.outButton.setEnabled(true);
		while(!GUI.isTurnOver){
		}
		GUI.isTurnOver=false;
		GUI.endButton.setEnabled(false);
		GUI.outButton.setEnabled(false);
		if(GUI.isGoOutPressed){
			GUI.isGoOutPressed=false;
			goOut();
		}
		GUI.cardPanel.removeAll();
		GUI.cardPanel.repaint();
	}

	public void goOut(){
		boolean[] used=new boolean[myHand.numCards()];
		ArrayList<Integer> group;
		ArrayList<Hand> totalGroups=new ArrayList<Hand>();
		while(true){
			group=Dialogs.showOutDialog(used, false);
			Hand groupedCards=new Hand();
			for(int j=0;j<group.size();j++){
				groupedCards.addCard(myHand.getCard(group.get(j)));
			}
			if(actionCommand.equals("seq")){
				if(isSequence(groupedCards)){
					for(int k=0;k<group.size();k++){
						used[group.get(k)]=true;
					}
				}
				else{
					Dialogs.showErrorDialog();
					continue;
				}
			}
			else if(actionCommand.equals("set")){
				if(isSet(groupedCards)){
					for(int k=0;k<group.size();k++){
						used[group.get(k)]=true;
					}
				}
				else{
					Dialogs.showErrorDialog();
					continue;
				}
			}
			else if(actionCommand.equals("can"))
				return;
			totalGroups.add(groupedCards);
			boolean done=true;
			for(int k=0;k<used.length;k++){
				if(used[k]==false){
					done=false;
					break;
				}
			}
			if(!done)
				continue;
			isOut=true;
			Dialogs.showGroupedCardsDialog(totalGroups, true);
			return;
		}
	}
	
	@Override
	public int groupCards() {
		boolean[] used=new boolean[myHand.numCards()];
		ArrayList<Integer> group;
		ArrayList<Hand> totalGroups=new ArrayList<Hand>();
		while(true){
			group=Dialogs.showOutDialog(used, true);
			Hand groupedCards=new Hand();
			for(int j=0;j<group.size();j++){
				groupedCards.addCard(myHand.getCard(group.get(j)));
			}
			if(actionCommand.equals("seq")){
				if(isSequence(groupedCards)){
					for(int k=0;k<group.size();k++){
						used[group.get(k)]=true;
					}
				}
				else{
					Dialogs.showErrorDialog();
					continue;
				}
			}
			else if(actionCommand.equals("set")){
				if(isSet(groupedCards)){
					for(int k=0;k<group.size();k++){
						used[group.get(k)]=true;
					}
				}
				else{
					Dialogs.showErrorDialog();
					continue;
				}
			}
			else if(actionCommand.equals("done")){
				int points=0;
				for(int i=0;i<used.length;i++){
					if(used[i]==false){
						points+=myHand.getCard(i).getPoints();
						groupedCards.addCard(myHand.getCard(i));
					}
				}
				totalGroups.add(groupedCards);
				Dialogs.showGroupedCardsDialog(totalGroups, false);
				return points;
			}
			else if(actionCommand.equals("")){
				System.out.println("ELSE REACHED");
				continue;
			}
			totalGroups.add(groupedCards);
			boolean done=true;
			for(int k=0;k<used.length;k++){
				if(used[k]==false){
					done=false;
					break;
				}
			}
			if(!done)
				continue;
			isOut=true;
			Dialogs.showGroupedCardsDialog(totalGroups, false);
			return 0;
		}
	}
}
