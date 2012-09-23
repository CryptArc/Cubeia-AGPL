package com.cubeia.games.poker.activator;

import java.util.List;

import com.cubeia.games.poker.entity.TableConfigTemplate;

public interface TableConfigTemplateProvider {

	public List<TableConfigTemplate> getTemplates();
	
}
