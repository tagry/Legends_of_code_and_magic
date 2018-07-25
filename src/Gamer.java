import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Gamer {
	boolean isMe;

	int playerHealth;
	int playerMana;
	int playerDeck;
	int playerRune;

	int nbCards;

	int nbCardOnBoard;

	List<Card> cardsOwned = new ArrayList<>();// just for me

	public void refreshPlayer(int playerHealth, int playerMana, int playerDeck, int playerRune) {
		this.playerHealth = playerHealth;
		this.playerMana = playerMana;
		this.playerDeck = playerDeck;
		this.playerRune = playerRune;
	}

	public void cleanHand() {
		nbCards = 0;
		cardsOwned.clear();
	}

	public void addCard(int cardNumber, int instanceId, int location, int cardType, int cost, int attack, int defense,
			String abilities, int myHealthChange, int opponentHealthChange, int cardDraw) {
		cardsOwned.add(new Card(cardNumber, instanceId, location, cardType, cost, attack, defense, abilities,
				myHealthChange, opponentHealthChange, cardDraw));

		nbCards++;
	}

	@Override
	public String toString() {
		String result = "Health : " + playerHealth + "\n";
		result += "Mana : " + playerMana + "\n";
		result += "Deck : " + playerDeck + "\n";
		result += "Rune : " + playerRune + "\n";
		result += "Cards on board : " + nbCardOnBoard + "\n";
		result += "Cards owned : " + nbCards + "\n";

		return result;
	}

}

class Board {
	int turnPlayed = 0;

	Gamer playerMe = new Gamer();
	Gamer playerHim = new Gamer();

	List<Card> myCardsOnBoard = new ArrayList<>();
	List<Card> hisCardsOnBoard = new ArrayList<>();

	public void refreshMe(int playerHealth, int playerMana, int playerDeck, int playerRune) {
		this.playerMe.refreshPlayer(playerHealth, playerMana, playerDeck, playerRune);
	}

	public void refreshHim(int playerHealth, int playerMana, int playerDeck, int playerRune) {
		this.playerHim.refreshPlayer(playerHealth, playerMana, playerDeck, playerRune);
	}

	public void cleanAll() {
		playerMe.cleanHand();
		playerHim.cleanHand();

		myCardsOnBoard.clear();
		hisCardsOnBoard.clear();
	}

	@Override
	public String toString() {
		String result = "ME :\n";
		result += playerMe.toString();

		result += "HIM : \n";
		result += playerHim.toString();

		result += "\n\nTurn Played : " + turnPlayed;

		return result;
	}
}

class Card {
	int cardNumber;
	int instanceId;
	int location;
	int cardType;
	int cost;
	int attack;
	int defense;
	String abilities;
	int myHealthChange;
	int opponentHealthChange;
	int cardDraw;

	public Card(int cardNumber, int instanceId, int location, int cardType, int cost, int attack, int defense,
			String abilities, int myHealthChange, int opponentHealthChange, int cardDraw) {
		this.cardNumber = cardNumber;
		this.instanceId = instanceId;
		this.location = location;
		this.cardType = cardType;
		this.cost = cost;
		this.attack = attack;
		this.defense = defense;
		this.abilities = abilities;
		this.myHealthChange = myHealthChange;
		this.opponentHealthChange = opponentHealthChange;
		this.cardDraw = cardDraw;
	}
}

class Move {
	String result = "";

	public void clear() {
		result = "";
	}

	public void addPick(int id) {
		if (result.isEmpty())
			result = "PICK " + id;
	}

	public void addSummon(int id) {
		if (result.isEmpty())
			result = "SUMMON " + id;
		else
			result += ";SUMMON " + id;
	}

	public void addAttack(int idAttacker, int idTarget) {
		if (result.isEmpty())
			result = "ATTACK " + idAttacker + " " + idTarget;
		else
			result += ";ATTACK " + idAttacker + " " + idTarget;
	}

	@Override
	public String toString() {
		if (result.isEmpty())
			return ("PASS");
		else
			return result;
	}
}

abstract class Strategy {
	Move moves = new Move();

	public void play(Board board) {
		moves.clear();

		if (board.turnPlayed < 30)
			playDraft(board);
		else
			playBataille(board);

		System.out.println(moves);
	}

	public abstract void playDraft(Board board);

	public abstract void playBataille(Board board);

	public boolean decideToPutOn(Board board) {
		Card cardMinMana = getMinManaOwned(board, new ArrayList<>());

		return cardMinMana != null && board.myCardsOnBoard.size() < 6 && cardMinMana.cost <= board.playerMe.playerMana
				&& board.myCardsOnBoard.size() < 6;
	}

	public boolean decideToAttack(Board board) {

		return board.myCardsOnBoard.size() > 0;
	}

	/*
	 * IN MY HAND
	 */

	public Card getMaxAttackOwned(Board board, List<Integer> except) {
		Card result = null;
		int max = -1;

		for (Card card : board.playerMe.cardsOwned) {
			if (card.attack > max && !except.contains(card.instanceId)) {
				max = card.attack;
				result = card;
			}
		}

		return result;
	}

	public Card getMaxDefenseOwned(Board board, List<Integer> except) {
		Card result = null;
		int max = -1;

		for (Card card : board.playerMe.cardsOwned) {
			if (card.defense > max && !except.contains(card.instanceId)) {
				max = card.defense;
				result = card;
			}
		}

		return result;
	}

	/*
	 * ON BOARD MY SIDE
	 */

	public Card getMinManaOwned(Board board, List<Integer> except) {
		Card result = null;
		int min = 10000;

		for (Card card : board.playerMe.cardsOwned) {
			if (card.cost < min && !except.contains(card.instanceId)) {
				min = card.cost;
				result = card;
			}
		}

		return result;
	}

	public Card getMaxAttackMineOnBoard(Board board, List<Integer> except) {
		Card result = null;
		int max = -1;

		for (Card card : board.myCardsOnBoard) {
			if (card.attack > max && !except.contains(card.instanceId)) {
				max = card.attack;
				result = card;
			}
		}

		return result;
	}

	public Card getMaxDefenseMineOnBoard(Board board, List<Integer> except) {
		Card result = null;
		int max = -1;

		for (Card card : board.myCardsOnBoard) {
			if (card.defense > max && !except.contains(card.instanceId)) {
				max = card.defense;
				result = card;
			}
		}

		return result;
	}

	public Card getMinManaMineOnBoard(Board board, List<Integer> except) {
		Card result = null;
		int min = 10000;

		for (Card card : board.myCardsOnBoard) {
			if (card.cost < min && !except.contains(card.instanceId)) {
				min = card.cost;
				result = card;
			}
		}

		return result;
	}
}

class BasicStrategy extends Strategy {

	@Override
	public void playDraft(Board board) {
		List<Card> cardToPick = board.playerMe.cardsOwned;
		
		moves.addPick(getBetterToPickUp(cardToPick));
	}

	@Override
	public void playBataille(Board board) {
		if (decideToPutOn(board)) {
			Card minMana = getMinManaOwned(board, new ArrayList<>());

			moves.addSummon(minMana.instanceId);
		}

		if (decideToAttack(board)) {
			// Card maxAttack = getMaxAttackMineOnBoard(board, new ArrayList<>());
			for (Card card : board.myCardsOnBoard) {
				moves.addAttack(card.instanceId, -1);
			}
		}
	}
	
	public int getBetterToPickUp(List<Card> cards) {
		int result = 0;
		int maxRatio = -1;
		
		for(Card card : cards) {
			int ratio = card.attack + card.defense - card.cost;
			if(ratio > maxRatio) {
				maxRatio = ratio;
				result = card.instanceId;
			}
		}
		
		return result;
	}

}

class Player {

	public static Strategy strategy = new BasicStrategy();
	public static Board board = new Board();

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);

		// game loop
		while (true) {
			board.cleanAll();

			for (int i = 0; i < 2; i++) {
				int playerHealth = in.nextInt();
				int playerMana = in.nextInt();
				int playerDeck = in.nextInt();
				int playerRune = in.nextInt();

				if (i == 0)
					board.refreshMe(playerHealth, playerMana, playerDeck, playerRune);
				else
					board.refreshHim(playerHealth, playerMana, playerDeck, playerRune);

			}
			int opponentHand = in.nextInt();
			board.playerHim.nbCards = opponentHand;

			int cardCount = in.nextInt();
			for (int i = 0; i < cardCount; i++) {
				int cardNumber = in.nextInt();
				int instanceId = in.nextInt();
				int location = in.nextInt();
				int cardType = in.nextInt();
				int cost = in.nextInt();
				int attack = in.nextInt();
				int defense = in.nextInt();
				String abilities = in.next();
				int myHealthChange = in.nextInt();
				int opponentHealthChange = in.nextInt();
				int cardDraw = in.nextInt();
				
				if(instanceId == -1)
					instanceId = i;

				if (location == 0) {
					board.playerMe.addCard(cardNumber, instanceId, location, cardType, cost, attack, defense, abilities,
							myHealthChange, opponentHealthChange, cardDraw);
				} else if (location == 1) {
					board.myCardsOnBoard.add(new Card(cardNumber, instanceId, location, cardType, cost, attack, defense,
							abilities, myHealthChange, opponentHealthChange, cardDraw));

				} else if (location == -1) {
					board.hisCardsOnBoard.add(new Card(cardNumber, instanceId, location, cardType, cost, attack,
							defense, abilities, myHealthChange, opponentHealthChange, cardDraw));
				}
			}

			System.err.println(board);

			strategy.play(board);

			board.turnPlayed++;
		}
	}
}