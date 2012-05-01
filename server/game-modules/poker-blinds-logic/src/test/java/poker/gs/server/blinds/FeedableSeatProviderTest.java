package poker.gs.server.blinds;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class FeedableSeatProviderTest extends TestCase {

    private FeedableSeatProvider provider;

    private Random randomizer = mock(Random.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        provider = new FeedableSeatProvider(randomizer);
    }

    public void testGetRandomSeatId() {
        // Given
        List<Integer> seatIds = Arrays.asList(3, 6, 9);
        when(randomizer.nextInt(3)).thenReturn(1);

        // When
        int randomSeatId = provider.getRandomSeatId(seatIds);

        // Then
        assertEquals(6, randomSeatId);
        verify(randomizer).nextInt(3);
    }

    public void testFeedNextValue() {
        // Given
        List<Integer> seatIds = Arrays.asList(3, 6, 9);
        when(randomizer.nextInt(3)).thenReturn(0);
        provider.feedNextSeatId(9);

        // When
        int randomSeatId = provider.getRandomSeatId(seatIds);

        // Then
        assertEquals(9, randomSeatId);
        verify(randomizer, never()).nextInt(anyInt());
    }

    public void testNextFedValueNotContainedInSeatIdList() {
        // Given
        List<Integer> seatIds = Arrays.asList(3, 6, 9);
        provider.feedNextSeatId(7);

        // When
        int randomSeatId = provider.getRandomSeatId(seatIds);

        // Then
        assertEquals(3, randomSeatId);
        verify(randomizer).nextInt(3);
    }
}
