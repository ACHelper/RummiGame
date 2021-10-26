package kz.edu.nu.cs.se.hw;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Deck {
    List<String> mainDeck;
    Stack<String> discardPile;
    List<MeldingDeck> meldingDecks;

    public Deck() {
        mainDeck = new ArrayList<>();
        discardPile = new Stack<String>();
        meldingDecks = new ArrayList<MeldingDeck>();

        for (String s : Card.getInstance().suits) {
            for (String r : Card.getInstance().ranks) {
                mainDeck.add(r + s);
            }
        }
    }

    public void setShuffledDeck(List<String> shuffledDeck) {
        mainDeck.clear();
        for (int i = 0; i < shuffledDeck.size(); i++) {
            mainDeck.add(shuffledDeck.get(i));
        }
    }

    public String takeCardFromMainDeck() {
        if (mainDeck.size() == 0) {
            turnOver();
            discardPile.push(takeCardFromMainDeck());
        }

        String card = mainDeck.get(mainDeck.size() - 1);
        mainDeck.remove(mainDeck.size()-1);

        return card;
    }

    public String takeCardFromDiscardPile() {
        return discardPile.pop();
    }

    private void turnOver() {
        while (!discardPile.isEmpty()) {
            mainDeck.add(discardPile.pop());
        }
    }

    protected void transferCardsToMeldingDeck(String[] cards, boolean isRankType) {
        MeldingDeck meldDeck = new MeldingDeck(isRankType);
        for (String card : cards) {
            meldDeck.meldingCards.add(card);
        }
        meldingDecks.add(meldDeck);
    }
}
