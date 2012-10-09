package com.cubeia.poker.hand.eval;

import com.cubeia.poker.hand.Hand;
import com.cubeia.poker.hand.HandStrength;
import com.cubeia.poker.hand.HandType;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HandTypeCheckCalculatorTest {

    private HandTypeCheckCalculator calculator = new HandTypeCheckCalculator();

    @Test
    public void testCheckStraightFlush() throws Exception {
        Hand hand = new Hand("TH KH QH AH JH");
        Hand sortedHand = new Hand("AH KH QH JH TH");
        HandStrength handStrength = calculator.checkStraightFlush(hand);
        // Technically, this is a royal, but we can live with calling it a straight flush.
        assertThat(handStrength.getHandType(), is(HandType.STRAIGHT_FLUSH));
        assertThat(handStrength.getCards(), is(sortedHand.getCards()));
    }
}
