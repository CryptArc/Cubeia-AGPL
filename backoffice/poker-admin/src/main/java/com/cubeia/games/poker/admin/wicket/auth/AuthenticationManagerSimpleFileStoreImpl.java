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

package com.cubeia.games.poker.admin.wicket.auth;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component("xmlAuthenticationManager")
public class AuthenticationManagerSimpleFileStoreImpl implements AuthenticationProvider {

	private static final String DEFAULT_BACKOFFICE_USER_LIST_XML = "poker-admin-user-list-default.xml";

	private static final String OVERRIDE_BACKOFFICE_USER_LIST_XML = "poker-admin-user-list.xml";

	private Logger log = LoggerFactory.getLogger(AuthenticationManagerSimpleFileStoreImpl.class);

	private Map<String, BackofficeUserDTO> userMap = new HashMap<String, BackofficeUserDTO>();

	public AuthenticationManagerSimpleFileStoreImpl() {
		try {
			JAXBContext ctx = JAXBContext.newInstance(
					BackofficeUserDTO.class, 
					BackofficeUserListDTO.class);

			Unmarshaller unmarshaller = ctx.createUnmarshaller();

			InputStream is = null;

			try {
				is = new ClassPathResource(OVERRIDE_BACKOFFICE_USER_LIST_XML).getInputStream();
				log.debug("Using {} for admin authentication.", OVERRIDE_BACKOFFICE_USER_LIST_XML);
			} catch (Exception e) {// Ignore            	
			}

			if(is == null){
				log.debug(OVERRIDE_BACKOFFICE_USER_LIST_XML + " not found, falling back to " + DEFAULT_BACKOFFICE_USER_LIST_XML);
				try {
					is = new ClassPathResource(DEFAULT_BACKOFFICE_USER_LIST_XML).getInputStream();
					log.debug("Using {} for admin authentication.", DEFAULT_BACKOFFICE_USER_LIST_XML);
				} catch (Exception e) {
					log.error("No user definition file was found.");
				}
			}

			if (is == null) {
				throw new RuntimeException("resource not found: " + DEFAULT_BACKOFFICE_USER_LIST_XML);
			}           

			BackofficeUserListDTO userList = (BackofficeUserListDTO) unmarshaller.unmarshal(is);
			for (BackofficeUserDTO u : userList.getUsers()) {
				userMap.put(u.getUserName(), u);
			}

			log.debug("read {} users from file store", userMap.size());
		} catch (Exception e) {
			log.error("error reading user list", e);
			throw new RuntimeException("error reading user list", e);
		}
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        if (authenticate(name, password)) {
			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			Set<BackofficeRole> rolesForUser = getRolesForUser(name);
			for (BackofficeRole br : rolesForUser) {
				grantedAuths.add(new SimpleGrantedAuthority(br.name()));
			}
			return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
				
		} else {
			throw new BadCredentialsException("Authentication failed for "+authentication.getPrincipal());
		}
		
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

    public boolean authenticate(String userName, String password) {
        BackofficeUserDTO u = userMap.get(userName);
        if (u == null) {
            return false;
        } else {
            return u.getPassword().equals(password);
        }
    }

    public Set<BackofficeRole> getRolesForUser(String userName) {
        BackofficeUserDTO u = userMap.get(userName);
        if (u == null) {
            return null;
        } else {
            return new HashSet<BackofficeRole>(u.getRoles());
        }
    }
}
