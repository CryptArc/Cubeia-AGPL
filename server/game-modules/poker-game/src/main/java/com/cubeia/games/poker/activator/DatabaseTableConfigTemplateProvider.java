package com.cubeia.games.poker.activator;

import java.util.List;

import com.cubeia.games.poker.entity.TableConfigTemplate;
import com.cubeia.games.poker.entity.TableConfigTemplateDao;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DatabaseTableConfigTemplateProvider implements TableConfigTemplateProvider {

	@Inject
	private TableConfigTemplateDao dao;
	
	@Override
	public List<TableConfigTemplate> getTemplates() {
		return dao.get();
	}
}
