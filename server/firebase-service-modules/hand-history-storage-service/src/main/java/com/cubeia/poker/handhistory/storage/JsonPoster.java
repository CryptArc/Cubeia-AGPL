/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.handhistory.storage;

import static org.apache.http.protocol.HTTP.CONTENT_TYPE;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.cubeia.poker.handhistory.api.HandHistoryPersistenceService;
import com.cubeia.poker.handhistory.api.HistoricHand;

public class JsonPoster implements HandHistoryPersistenceService {
	
	private static final String APPLICATION_JSON = "application/json";
	
	private final Logger log = Logger.getLogger(getClass());
	private final ObjectMapper mapper = new ObjectMapper();
	
	private String baseUrl;
	
	// TODO: FIX XONFIG
	/*public JsonPoster(HandHistoryConfig configuration) {
		URL url = configuration.getJsonIndexUrl();
		if(url != null) {
			baseUrl = url.toExternalForm();
			if(!baseUrl.endsWith("/")) {
				baseUrl += "/";
			}
			log.info("Enabling indexing with base URL: " + baseUrl);
		} else {
			log.info("Indexing disabled");
		}
	}*/
	
	@Override
	public void persist(HistoricHand hand) {
		if(baseUrl != null) {
			log.debug("Indexing hand " + hand.getId());
			DefaultHttpClient dhc = new DefaultHttpClient();
			HttpPut method = new HttpPut(baseUrl + hand.getId());
			method.addHeader(CONTENT_TYPE, APPLICATION_JSON);
			try {
				String json = mapper.writeValueAsString(hand);
				if(log.isTraceEnabled()) {
					log.trace("User " + hand.getId() + " JSON: " + json);
				}
				StringEntity ent = new StringEntity(json, "UTF-8");
				ent.setContentType(APPLICATION_JSON);
				method.setEntity(ent);
				HttpResponse resp = dhc.execute(method);
				if(!String.valueOf(resp.getStatusLine().getStatusCode()).startsWith("2")) {
					log.warn("Failed to index user " + hand.getId() + "; Server responded with status: " + resp.getStatusLine());
				} else {
					HttpEntity entity = resp.getEntity();
					String string = EntityUtils.toString(entity, "UTF-8");
					if(log.isTraceEnabled()) {
						log.trace("User " + hand.getId() + " return JSON: " + string);
					}
					/*JsonNode node = mapper.readTree(string);
					if(!node.get("ok").asBoolean()) {
						log.warn("Failed to index user " + hand.getId() + "; Response JSON: " + string);
					} */
				}
			} catch(Exception e) {
				log.error("Failed to index user " + hand.getId(), e);
			}
		}
	}
}
