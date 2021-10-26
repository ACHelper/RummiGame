package kz.edu.nu.cs.se.hw;

import java.util.HashMap;

public class Card {
    private static Card cardTypes;
    final String[] suits = new String[] { "C", "D", "H", "S", "M" };
    final String[] ranks = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

    // Hashmaps are used for the validation of melding
    protected final HashMap<Character, Integer> rankToNumeric = new HashMap<Character, Integer>();
    protected final HashMap<Integer, Character> numericToRank = new HashMap<Integer, Character>();
    protected final HashMap<Character, Integer> suitToNumeric = new HashMap<Character, Integer>();
    protected final HashMap<Integer, Character> numericToSuit = new HashMap<Integer, Character>();


    // Create Singleton object
    private Card() {
        for (int i = 2; i <= 10; i++) {
            String num = String.valueOf(i);
            rankToNumeric.put(num.charAt(0), i);
        }
        rankToNumeric.put('A', 1);
        rankToNumeric.put('J', 11);
        rankToNumeric.put('Q', 12);
        rankToNumeric.put('K', 13);

        for (int i = 2; i <= 9; i++) {
            numericToRank.put(i, (char)(i + '0'));
        }
        numericToRank.put(1, 'A');
        numericToRank.put(10, '1');
        numericToRank.put(11, 'J');
        numericToRank.put(12, 'Q');
        numericToRank.put(13, 'K');

        suitToNumeric.put('C', 0);
        suitToNumeric.put('D', 1);
        suitToNumeric.put('H', 2);
        suitToNumeric.put('M', 3);
        suitToNumeric.put('S', 4);

        numericToSuit.put(0, 'C');
        numericToSuit.put(1, 'D');
        numericToSuit.put(2, 'H');
        numericToSuit.put(3, 'M');
        numericToSuit.put(4, 'S');
    }

    public static Card getInstance() {
        if (cardTypes == null) {
            cardTypes = new Card();
        }
        return cardTypes;
    }
}
