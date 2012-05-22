package com.cubeia.poker.rounds;

import com.cubeia.poker.PokerContext;
import com.cubeia.poker.PokerState;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.adapter.ServerAdapter;
import com.cubeia.poker.pot.Pot;
import com.cubeia.poker.pot.PotHolder;
import com.cubeia.poker.states.ServerAdapterHolder;
import com.cubeia.poker.timing.Periods;
import com.cubeia.poker.timing.TimingProfile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RoundHelperTest {

    @Mock
    private PokerContext context;

    @Mock
    private ServerAdapterHolder serverAdapterHolder;

    @Mock
    private ServerAdapter serverAdapter;

    @Mock
    private TimingProfile timingProfile;

    private RoundHelper roundHelper;

    @Before
    public void setup() {
        initMocks(this);
        when(serverAdapterHolder.get()).thenReturn(serverAdapter);
        when(context.getTimingProfile()).thenReturn(timingProfile);
        when(context.getTotalPotSize()).thenReturn(50L);
        when(timingProfile.getTime(Periods.ACTION_TIMEOUT)).thenReturn(10L);
        roundHelper = new RoundHelper(context, serverAdapterHolder);
    }

    @Test
    public void testRequestAction() {
        ActionRequest actionRequest = mock(ActionRequest.class);
        roundHelper.requestAction(actionRequest);

        verify(actionRequest).setTotalPotSize(50L);
        verify(actionRequest).setTimeToAct(10L);
        verify(serverAdapter).requestAction(actionRequest);
    }

    @Test
    public void requestMultipleActions() {
        ActionRequest actionRequest1 = mock(ActionRequest.class);
        ActionRequest actionRequest2 = mock(ActionRequest.class);

        Collection<ActionRequest> requests = Arrays.asList(actionRequest1, actionRequest2);
        roundHelper.requestMultipleActions(requests);

        verify(actionRequest1).setTimeToAct(10L);
        verify(actionRequest2).setTimeToAct(10L);
        verify(serverAdapter).requestMultipleActions(requests);
    }
}
