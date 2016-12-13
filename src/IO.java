import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class IO {
	private static final ImageIcon[][] cardIcons=new ImageIcon[11][5];
	private static ImageIcon jokerIcon;
	private static final ImageIcon[][] reverseCardIcons=new ImageIcon[11][5];
	private static ImageIcon reverseJokerIcon;
	
	static final String GREEN_FELT="felt";
	static final String CARD_BACK="cardBack";
	static final String END_BUTTON="endButton";
	static final String OUT_BUTTON="outButton";
	static final String WOOD="wood";
	static final String helpText="This game requires a special deck because it is a special game. It has five suits. The new additional suit is called Stars.\n" +
			"Within each suit, there are 11 instead of the 13 cards contained in the standard suit. The ace and the deuce are left out.\n" +
			"This means that each suit will have 3 - King. There are also 3" +
			" jokers per deck, six in all.\n\nThe object of this game is to have the fewest number of points after playing 11 hands. Each hand has a progressive\n" +
			" increase in the number of cards dealt out. The first hand has three cards. The second has four cards, and so on up\nto 13 cards for each player on the" +
			" eleventh hand. Upon each deal the number of cards dealt also indicates the wild\ncard. For example, on the first hand, the three is considered wild" +
			" because three cards were dealt to each player.\nThis card is wild, in addition to the cards already designated as wild.\n\nWhen the appropriate number of" +
			" cards are dealt out, the dealer puts the remaining cards in the middle of the table\nand flips over another card. Play then proceeds to the left of the" +
			" dealer. The player can either draw a card from the\npile or the top card from the discard pile. He must discard at the end of his turn. Each player" +
			" attempts to create\na hand that is completely comprised of \"runs\" or \"sets\" with one card left over to discard.\n\nA run is basically three or more" +
			" cards of the same suit all in a row. A set is three or more cards in a row all with\nthe same number or letter designation (i.e. J,J,J). Within these" +
			" runs and sets any number of cards can be wild cards,\nand the wild cards can take the place of any card. For example, a set could be made up of Jack," +
			"Wild,Wild. When a\nplayer succeeds in creating such a hand, he lays it out on the table. Every player then has one turn to make the best\nhand he or she" +
			" can, then laying down all sets and runs. Whatever cards remain in the hands of the players are\ncounted as points and written on a piece of paper and" +
			" tallied as the game progresses. Points are scored according\nto the face value of the cards, Jack = 11, Queen = 12, King = 13. Wild cards are equal to" +
			" twenty points (including\n the numbered wild card designated for that turn).\n\nWhen all 11 hands are played the player with the lowest score is the victor.";
	static final String aboutText="Five Crowns v"+Main.version+"\n\nProject Timeline: June-July 2013\n\nMade by:\n   coders:   Matt Spooner, Peter Spooner\n   graphic artist:   Emily Spooner";
	static final String welcomeText="Welcome to Five Crowns!\nIf this is your first time playing this game, please view the rules under File -> Help -> Rules.\nIf you are " +
			"familiar with the rules but you have not used this application before,\n   please view the tutorial under File -> Help -> Tutorial.\nTo start a game, go to File -> New Game.";
	private static final String FILE_PATH = "cards/";
	private static final String TUTORIAL_PATH = "tutorial/";
	private static final String SCORES_FILE = "scores.txt";
	static int pictureCounter=0;
	private static final int TOTAL_PICTURES=20;
	static String[] highScoreNames;
	static int[] highScoreScores;
	static int savedXPosition;
	static int savedYPosition;
	//TODO save scores inside jar
	private static BufferedImage loadImage(String fileName){
		BufferedImage i= null;
		try {
			String imageFileName = FILE_PATH + fileName + ".png";
			if(Main.IS_TEST){
				i = ImageIO.read(new File(imageFileName));
			}
			else{
				i = ImageIO.read(IO.class.getResourceAsStream("/" + imageFileName));
			}
			
		} catch(Exception e){
			System.err.println("Couldn't read file.");
			e.printStackTrace();
		}
		return i;
	}
	
	private static Image loadTutorial(String fileName){
		BufferedImage i= null;
		try {
			String imageFileName=TUTORIAL_PATH+fileName+".png";
			//TODO change \/
//			i=ImageIO.read(IO.class.getResourceAsStream(imageFileName));
			i=ImageIO.read(new File(imageFileName));
		}catch(Exception e){
			System.err.println("Couldn't read file.");
			GUI.setText("ERROR");
			e.printStackTrace();
		}
		Dimension d=GUI.isSmall ? GUI.SMALL_SIZE : GUI.FRAME_SIZE;
		return i.getScaledInstance(d.width, d.height, 0);
	}
	
	public static ImageIcon getImageIcon(Card c){
		if(c.number!=14){
			int i=c.number-3;
			int j=c.suit-1;
			return cardIcons[i][j];
		}
		else
			return jokerIcon;
	}
	
	public static ImageIcon getImageIcon(){
		pictureCounter++;
		return pictureCounter-1==TOTAL_PICTURES ? null : new ImageIcon(loadTutorial(Integer.toString(pictureCounter)));
	}
	
	public static BufferedImage getImage(String s){
		return loadImage(s);
	}
	
	public static void loadCards(){
		for(int i=0;i<11;i++){
			for(int j=0;j<5;j++){
				String temp=new Card(i+3,j+1).getPictureFileName();
				cardIcons[i][j]=new ImageIcon(loadImage(temp));
				reverseCardIcons[i][j]=new ImageIcon(invertColors(loadImage(temp)));
			}
		}
		String joker=new Card(14,6).getPictureFileName();
		jokerIcon=new ImageIcon(loadImage(joker));
		reverseJokerIcon=new ImageIcon(invertColors(loadImage(joker)));
	}
	
	private static BufferedImage invertColors(BufferedImage i){
		for(int x=0;x<i.getWidth();x++){
			for(int y=0;y<i.getHeight();y++){
				int rgb=i.getRGB(x, y);
				Color c=new Color(rgb, true);
				c=new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue());
				i.setRGB(x, y, c.getRGB());
			}
		}
		return i;
	}
	
	public static ImageIcon getInvertedIcon(Card c){
		if(c.number!=14){
			int i=c.number-3;
			int j=c.suit-1;
			return reverseCardIcons[i][j];
		}
		else
			return reverseJokerIcon;
	}
	
	public static void writeToFile(){
		try {
			File file=new File(SCORES_FILE);
			String rawData=getHighScoresData();
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(rawData.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createFile(){
		try {
			File file=new File(SCORES_FILE);
			file.createNewFile();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void createDefaultHighScores(){
		highScoreNames=new String[10];
		highScoreScores=new int[10];
		Arrays.fill(highScoreNames, "-");
		Arrays.fill(highScoreScores, 999999);
		savedXPosition=0;
		savedYPosition=0;
	}
	
	private static String getHighScoresData() {
		StringBuilder data=new StringBuilder();
		for(String str : highScoreNames){
			data.append(str+"!");
		}
		data.append("@");
		for(int i : highScoreScores){
			data.append(i+"!");
		}
		data.append("@");
		if(GUI.frame.isVisible()){
			savedXPosition=GUI.frame.getLocationOnScreen().x;
			savedYPosition=GUI.frame.getLocationOnScreen().y;
		}
		data.append(savedXPosition+"!"+savedYPosition+"!"+GUI.isSmall);
		return data.toString();
	}
	
	private static void readFromFile(File f){
		byte[] rawData=new byte[0];
		try {
			FileInputStream fis=new FileInputStream(f);
			int bytesThere=fis.available();
			rawData=new byte[bytesThere];
			fis.read(rawData);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] data=new String(rawData).split("[@]+");
		highScoreNames=data[0].split("[!]+");
		String[] scores=data[1].split("[!]+");
		String[] options=data[2].split("[!]+");
		highScoreScores=new int[10];
		for(int i=0;i<10;i++)
			highScoreScores[i]=Integer.parseInt(scores[i]);
		savedXPosition=Integer.parseInt(options[0]);
		savedYPosition=Integer.parseInt(options[1]);
		GUI.frame.setLocation(savedXPosition, savedYPosition);
		GUI.isSmall=options[2].equals("true");
	}
	
	public static void updateScores(Player winner){
		if(winner!=null){
			int score=winner.score;
			if(score>highScoreScores[9])
				return;
			for(int i : highScoreScores){
				if(score<i){
					highScoreScores[9]=score;
					highScoreNames[9]=winner.playerName;
					break;
				}
			}
			sortScores();
			writeToFile();
		}
	}
	
	private static void sortScores(){
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(highScoreScores[j]>highScoreScores[j+1]){
					String tempStr=highScoreNames[j+1];
					highScoreNames[j+1]=highScoreNames[j];
					highScoreNames[j]=tempStr;
					int tempInt=highScoreScores[j+1];
					highScoreScores[j+1]=highScoreScores[j];
					highScoreScores[j]=tempInt;
				}
			}
		}
	}
	
	public static String printHighScores(Player winner){
		updateScores(winner);
		String s="";
		for(int i=0;i<10;i++)
			s+=highScoreNames[i]+": "+highScoreScores[i]+"\n";
		return s;
	}

	public static void setupHighScores() {
		File f=null;
		try {
			f = new File(SCORES_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!f.exists()){
			System.out.println("Creating file...");
			createFile();
			createDefaultHighScores();
			writeToFile();
			Main.isFirstTime=true;
		}
		else{
			readFromFile(f);
		}
		sortScores();
		writeToFile();
	}
}
