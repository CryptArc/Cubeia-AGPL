package com.cubeia.games.poker.admin.wicket.search;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class UserTest {
	
	@Test
	public void testFromJson() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		User user = mapper.readValue(getClass().getResourceAsStream("/json/search/user1.json"), User.class);
		
		assertThat(user.getUserId(), CoreMatchers.is(1L));
		assertThat(user.getExternalUserId(), CoreMatchers.is("abc123"));
		assertThat(user.getUserName(), CoreMatchers.is("smaxxor1"));
		assertThat(user.getOperatorId(), CoreMatchers.is(10L));
		assertThat(user.getStatus(), CoreMatchers.is("ENABLED"));
		assertThat(user.getUserType(), CoreMatchers.is("USER"));
		assertThat(user.getUserInformation().getFirstName(), CoreMatchers.is("Tobias"));
		assertThat(user.getUserInformation().getLastName(), CoreMatchers.is("Testerblom"));
		assertThat(user.getUserInformation().getEmail(), CoreMatchers.is("a@example.com"));
		assertThat(user.getAttributes().get("attr1").getValue(), is("value1"));
		assertThat(user.getAttributes().get("attr2").getValue(), is("value2"));
	}

}
