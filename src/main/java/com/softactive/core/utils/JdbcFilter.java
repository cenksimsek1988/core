package com.softactive.core.utils;

import java.util.List;
import java.util.Map;

public interface JdbcFilter {

	public String convertToSql();

	public Map<String, Object> generateParamMap();

	public List<Object> generateWhereClauseObject();

}