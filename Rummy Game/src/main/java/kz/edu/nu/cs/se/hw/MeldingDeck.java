package kz.edu.nu.cs.se.hw;

import java.util.ArrayList;
import java.util.List;

public class MeldingDeck {
    List<String> meldingCards;
    boolean isRankType; // meld type can be based on the rank or on the suit

    public MeldingDeck(boolean isRankType) {
        meldingCards = new ArrayList<String>();
        this.isRankType = isRankType;
    }
}
