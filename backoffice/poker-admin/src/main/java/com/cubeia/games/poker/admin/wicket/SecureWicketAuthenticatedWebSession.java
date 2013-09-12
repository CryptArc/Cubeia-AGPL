package com.cubeia.games.poker.admin.wicket;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecureWicketAuthenticatedWebSession extends AuthenticatedWebSession {
	private static final long serialVersionUID = 3355101222374558750L;

	private static final Logger log = Logger.getLogger(SecureWicketAuthenticatedWebSession.class);

	 @SpringBean(name = "authenticationManager")
	private AuthenticationManager authenticationManager;

	private String username;

	public SecureWicketAuthenticatedWebSession(Request request) {
		super(request);
		injectDependencies();
		ensureDependenciesNotNull();
	}

	private void ensureDependenciesNotNull() {
		if (authenticationManager == null) {
			throw new IllegalStateException("An authenticationManager is required.");
		}
	}

	private void injectDependencies() {
		Injector.get().inject(this);
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public boolean authenticate(String username, String password) {
		this.username = username;
		boolean authenticated = false;
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			authenticated = authentication.isAuthenticated();
		} catch (AuthenticationException e) {
			log.warn(String.format("User '%s' failed to login. Reason: %s", username, e.getMessage()));
			authenticated = false;
		}
		return authenticated;
	}

	@Override
	public Roles getRoles() {
		Roles roles = new Roles();
		getRolesIfSignedIn(roles);
		return roles;
	}

	private void getRolesIfSignedIn(Roles roles) {
		if (isSignedIn()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				addRolesFromAuthentication(roles, authentication);
			} else {
				log.warn("Authentication is now null for user");
			}
		}
	}

	private void addRolesFromAuthentication(Roles roles, Authentication authentication) {
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			roles.add(authority.getAuthority());
		}
	}
}
