package kz.edu.nu.cs.se.hw;
import java.util.*;

/**
 * Starter code for a class that implements the <code>PlayableRummy</code>
 * interface. A constructor signature has been added, and method stubs have been
 * generated automatically in eclipse.
 *
 * Before coding you should verify that you are able to run the accompanying
 * JUnit test suite <code>TestRummyCode</code>. Most of the unit tests will fail
 * initially.
 *
 * @see PlayableRummy
 * @see TestRummyCode
 *
 */
public class Rummy implements PlayableRummy {
    public String[] playersStrArray;
    public Player[] players;
    public Deck deck;
    public Steps gameState;
    public int currentPlayer;
    public Card cardSingleton;

    public Rummy(String... players) throws RummyException {
        if (players.length > 6)
            throw new RummyException("Expected fewer players", 8);

        if (players.length < 2)
            throw new RummyException("Not enough players", 2);

        playersStrArray = players;

        // Initialize the players object
        int len = players.length;
        this.players = new Player[len];
        for (int i = 0; i < len; i++) {
            this.players[i] = new Player(players[i]);
        }

        deck = new Deck();
        gameState = Steps.WAITING;
        cardSingleton = Card.getInstance();
    }

    @Override
    public String[] getPlayers() {
        return playersStrArray;
    }

    @Override
    public int getNumPlayers() {
        return players.length;
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public int getNumCardsInDeck() {
        return deck.mainDeck.size();
    }

    @Override
    public int getNumCardsInDiscardPile() {
        return deck.discardPile.size();
    }

    @Override
    public String getTopCardOfDiscardPile() throws RummyException {
        return deck.discardPile.peek();
    }

    @Override
    public String[] getHandOfPlayer(int player) throws RummyException {
        if (player >= getNumPlayers())
            throw new RummyException("Not valid index of player", 10);

        int len = players[player].hand.size();
        String[] playerHand = new String[len];
        for (int i = 0; i < len; i++) {
            playerHand[i] = players[player].hand.get(i);
        }

        return playerHand;
    }

    @Override
    public int getNumMelds() {
        return deck.meldingDecks.size();
    }

    @Override
    public String[] getMeld(int i) throws RummyException {
        if (i >= getNumMelds() || i < 0)
            throw new RummyException("Not valid index of meld", 11);

        String[] meld = deck.meldingDecks.get(i).meldingCards.toArray(new String[0]);

        return meld;
    }

    @Override
    public void rearrange(String card) throws RummyException {
        if (gameState != Steps.WAITING)
            throw new RummyException("Expected waiting state", 3);

        deck.mainDeck.remove(card);
        deck.mainDeck.add(card);
    }

    @Override
    public void shuffle(Long l) throws RummyException {
        if (gameState != Steps.WAITING)
            throw new RummyException("Expected waiting state", 3);

        Random rand = new Random(l);
        List<String> sortedDeck = deck.mainDeck;
        List<String> shuffledDeck = new ArrayList<String>();
        for (int i = 0; i < 65; i++) {
            int randIdx = rand.nextInt(sortedDeck.size());
            String nextCard = sortedDeck.get(randIdx);

            shuffledDeck.add(nextCard);
            sortedDeck.remove(nextCard);
        }

        deck.setShuffledDeck(shuffledDeck);
    }

    @Override
    public Steps getCurrentStep() {
        return gameState;
    }

    @Override
    public int isFinished() {
        if (players[currentPlayer].hand.size() > 1)
            return -1;

        return currentPlayer;
    }

    @Override
    public void initialDeal() throws RummyException {
        if (gameState != Steps.WAITING)
            throw new RummyException("Expected waiting state", 3);

        int numToDistribute = 0;
        switch (getNumPlayers()) {
            case 2: {
                numToDistribute = 10;
                break;
            }
            case 3: case 4: {
                numToDistribute = 7;
                break;
            }
            case 5: case 6: {
                numToDistribute = 6;
                break;
            }
        }

        for (int i = 0; i < numToDistribute; i++) {
            for (int j = 0; j < getNumPlayers(); j++) {
                players[j].hand.add(deck.takeCardFromMainDeck());
            }
        }
        deck.discardPile.push(deck.takeCardFromMainDeck());

        currentPlayer = 0;
        gameState = Steps.DRAW;
    }

    @Override
    public void drawFromDiscard() throws RummyException {
        if (gameState != Steps.DRAW)
            throw new RummyException("Expected draw state", 4);

        String card = deck.takeCardFromDiscardPile();
        players[currentPlayer].hand.add(card);
        players[currentPlayer].isLastCardFromDiscardPile = true;

        gameState = Steps.MELD;
    }

    @Override
    public void drawFromDeck() throws RummyException {
        if (gameState != Steps.DRAW)
            throw new RummyException("Expected draw state", 4);

        String card = deck.takeCardFromMainDeck();
        players[currentPlayer].hand.add(card);

        gameState = Steps.MELD;
    }

    @Override
    public void meld(String... cards) throws RummyException {
        if (gameState != Steps.MELD && gameState != Steps.RUMMY)
            throw new RummyException("Expected meld or rummy step", 15);

        // check that we have meld in our hands and add the meld if the user possess it
        if (!players[currentPlayer].playerHasCards(cards)) {
            throw new RummyException("Expected cards", 7);
        }

        if (!players[currentPlayer].isValidMeld(cards)) {
            throw new RummyException("Not valid meld", 1);
        }

        int[] suits = new int[5];
        for (String card: cards) {
            suits[cardSingleton.suitToNumeric.get(card.charAt(card.length()-1))]++;
        }

        boolean oneSuitFound = false;
        for (int i = 0; i < suits.length; i++) {
            if (!oneSuitFound && suits[i] > 0) {
                oneSuitFound = true;
            } else if (oneSuitFound && suits[i] > 0) {
                oneSuitFound = false;
                break;
            }
        }

        boolean isRankType = oneSuitFound == false;
        deck.transferCardsToMeldingDeck(cards, isRankType);
        players[currentPlayer].discardMeldCards(cards);

        if (players[currentPlayer].hand.size() <= 1 && gameState == Steps.RUMMY) {
            gameState = Steps.FINISHED;
        }
    }

    @Override
    public void addToMeld(int meldIndex, String... cards) throws RummyException {
        if (gameState != Steps.MELD && gameState != Steps.RUMMY)
            throw new RummyException("Expected meld or rummy step", 15);

        for (String card : cards)
            if (!players[currentPlayer].hand.contains(card))
                return;

        for (String card : cards)
            deck.meldingDecks.get(meldIndex).meldingCards.add(card);

        players[currentPlayer].discardMeldCards(cards);

        if (players[currentPlayer].hand.size() <= 1 && (gameState == Steps.RUMMY))
            gameState = Steps.FINISHED;
    }

    @Override
    public void declareRummy() throws RummyException {
        if (gameState != Steps.MELD)
            throw new RummyException("Expected meld step", 5);

        gameState = Steps.RUMMY;
    }

    @Override
    public void finishMeld() throws RummyException {
        if (gameState == Steps.MELD) {
            gameState = Steps.DISCARD;
            return;
        } else if (gameState == Steps.RUMMY) {
            if (players[currentPlayer].hand.size() <= 1) {
                gameState = Steps.FINISHED;
            } else {
                gameState = Steps.DISCARD;
            }
            throw new RummyException("Rummy not demonstrated", 16);
        }

        throw new RummyException("Expected meld or rummy step", 15);
    }

    @Override
    public void discard(String card) throws RummyException {
        if (gameState != Steps.DISCARD)
            throw new RummyException("Expected discard state", 6);

        String topCard = players[currentPlayer].hand.get(players[currentPlayer].hand.size()-1);
        if (topCard.equals(card) && players[currentPlayer].isLastCardFromDiscardPile)
            throw new RummyException("Not valid discard", 13);

        if (!players[currentPlayer].hand.contains(card))
            throw new RummyException("Expected cards", 7);

        players[currentPlayer].hand.remove(card);
        deck.discardPile.push(card);

        players[currentPlayer].isLastCardFromDiscardPile = false;
        if (players[currentPlayer].hand.isEmpty()) {
            gameState = Steps.FINISHED;
            isFinished();
        } else {
            if (currentPlayer + 1 < getNumPlayers()) {
                currentPlayer++;
            } else {
                currentPlayer = 0;
            }
            gameState = Steps.DRAW;
        }
    }
}
