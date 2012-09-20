package com.cubeia.games.poker.entity;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class TableConfigTemplateDao {

	@Inject
	private Provider<EntityManager> em;
	
	@SuppressWarnings("unchecked")
	public List<TableConfigTemplate> get() {
		return em.get().createQuery("from TableConfigTemplate").getResultList();
	}
}
