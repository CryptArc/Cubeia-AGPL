package com.cubeia.games.poker.activator;

import static com.cubeia.games.poker.lobby.PokerLobbyAttributes.TABLE_TEMPLATE;

import java.util.Collections;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.games.poker.entity.TableConfigTemplate;
 
public class TemplateLobbyTableFilterTest {

	@Test
	public void simpleFind() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getId()).thenReturn(1);
		Map<String, AttributeValue> map = Collections.singletonMap(TABLE_TEMPLATE.name(), AttributeValue.wrap(1));
		Assert.assertTrue(new TemplateLobbyTableFilter(templ).accept(map));
	}
	
	@Test
	public void simpleMiss() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getId()).thenReturn(2); // ID MISS
		Map<String, AttributeValue> map = Collections.singletonMap(TABLE_TEMPLATE.name(), AttributeValue.wrap(1));
		Assert.assertFalse(new TemplateLobbyTableFilter(templ).accept(map));
	}
	
	@Test
	public void typeMiss() {
		TableConfigTemplate templ = Mockito.mock(TableConfigTemplate.class);
		Mockito.when(templ.getId()).thenReturn(1); 
		Map<String, AttributeValue> map = Collections.singletonMap(TABLE_TEMPLATE.name(), AttributeValue.wrap("1")); // Wrong attribute type
		Assert.assertFalse(new TemplateLobbyTableFilter(templ).accept(map));
	}
}
