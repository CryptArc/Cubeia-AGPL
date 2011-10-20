package com.cubeia.poker.pot;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.poker.rake.RakeCalculator;

public class PotHolderTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testRakeCalculationGetsCallFlag() {
        RakeCalculator rakeCalculator = mock(RakeCalculator.class);
        PotHolder potHolder = new PotHolder(rakeCalculator);
        
        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(false));
        
        potHolder.call();
        potHolder.calculateRake();
        verify(rakeCalculator).calculateRakes(Mockito.anyCollection(), Mockito.eq(true));
    }

}
