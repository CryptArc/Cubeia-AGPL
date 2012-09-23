package com.cubeia.games.poker.activator;

import static com.cubeia.poker.timing.Timings.EXPRESS;
import static com.cubeia.poker.variant.PokerVariant.TELESINA;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.google.inject.Guice;
import com.google.inject.Inject;

public class ParticipantFactoryImplTest {


	@Inject
	private ParticipantFactory fact;
	
	@Before
	public void setup() throws Exception {
		Guice.createInjector(new TestActivatorModule()).injectMembers(this);
	}
	
	@Test
	public void testSimpleCreation() {
		TableConfigTemplate templ = createTemplate();
		PokerParticipant part = fact.createParticipantFor(templ);
		Assert.assertNotNull(part);
		Assert.assertNotNull(part.getRngProvider());
		Assert.assertNotNull(part.getCashGameBackendService());
		Assert.assertEquals(templ, part.getTemplate());
		Assert.assertEquals(6, part.getSeats());
	}
	
	
	// --- PRIVATE METHODS --- //

	private TableConfigTemplate createTemplate() {
		TableConfigTemplate templ = new TableConfigTemplate();
		templ.setAnte(10);
		templ.setSeats(6);
		templ.setTiming(EXPRESS);
		templ.setVariant(TELESINA);
		return templ;
	}
}
