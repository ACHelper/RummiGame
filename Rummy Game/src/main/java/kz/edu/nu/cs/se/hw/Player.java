package kz.edu.nu.cs.se.hw;

import java.util.ArrayList;
import java.util.List;

public class Player {
    String name;
    List<String> hand;
    boolean isLastCardFromDiscardPile;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<String>();
        isLastCardFromDiscardPile = false;
    }

    public boolean playerHasCards(String[] cards) {
        for (String card : cards) {
            if (!hand.contains(card)) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidMeld(String[] cards) {
        Card cardSingleton = Card.getInstance();

        if (cards.length < 3) return false;
        int[] ranks = new int[14];
        int[] suits = new int[5];

        for (String card : cards) {
            suits[cardSingleton.suitToNumeric.get(card.charAt(card.length()-1))]++;
        }

        // check for the suit meld type
        boolean oneSuitFound = false;
        char suit = 'a';
        for (int i = 0; i < suits.length; i++) {
            if (!oneSuitFound && suits[i] > 0) {
                oneSuitFound = true;
                suit = cardSingleton.numericToSuit.get(i);
            } else if (oneSuitFound && suits[i] > 0) {
                oneSuitFound = false;
                break;
            }
        }

        if (oneSuitFound) {
            // TODO: check suits ranking. return true if valid and false otherwise
            // Find the minimum rank to check its upper neighbours and validate
            int cur = 1000;
            char minLetter = 'K';
            for (String card : cards) {
                char firstLetter = card.charAt(0);
                if (cardSingleton.rankToNumeric.get(firstLetter) < cur) {
                    cur = cardSingleton.rankToNumeric.get(firstLetter);
                    minLetter = firstLetter;
                }
            }

            // Validate all next cards
            int meldSize = cards.length;
            while (meldSize > 0) {
                String nextCard = getUpperNeighbour(minLetter, String.valueOf(suit));
                if (!hand.contains(nextCard)) return false;

                minLetter = nextCard.charAt(0);
                meldSize--;
            }

            return true;
        }
        // check for the suit meld type




        // Code below checks for the rank of meld type
        if (cards.length > 5) return false;
        for (String card : cards) {
            char rank = card.charAt(0);
            ranks[cardSingleton.rankToNumeric.get(rank)]++;
        }

        // Check for the number of instances according to rank
        // So we cannot get { 4M, 4C, 4H, 5E }
        boolean foundOneRankInstance = false;
        for (int i = 0; i < ranks.length; i++) {
            if (!foundOneRankInstance && ranks[i] > 0) {
                foundOneRankInstance = true;
            } else if (foundOneRankInstance && ranks[i] > 0) {
                return false;
            }
        }

        return true;
    }

    private String getUpperNeighbour(Character rank, String suit) {
        if (rank >= '2' && rank <= '9') {
            int upperRankVal = Character.getNumericValue(rank);
            upperRankVal++;
            return upperRankVal + suit;
        } else if (rank == 'A') {
            return "2" + suit;
        } else if (rank == '1') {
            return "J" + suit;
        } else if (rank == 'J') {
            return "Q" + suit;
        } else if (rank == 'Q') {
            return "K" + suit;
        }

        return null;
    }

    public void discardMeldCards(String[] cards) {
        for (String card : cards) {
            hand.remove(card);
        }
    }
}
