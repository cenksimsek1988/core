package com.softactive.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import lombok.Data;

public @Data class MultiJdbcFilter implements JdbcFilter {

	private Condition condition;
	private JdbcFilter[] filters;

	public MultiJdbcFilter(Condition condition, JdbcFilter... filter) {
		this.condition = condition;
		this.filters = filter;
	}

	@Override
	public String convertToSql() {
		String sql = " (";
		for (int i = 0; i < filters.length; i++) {
			if (i != filters.length - 1)
				sql += filters[i].convertToSql() + condition.getOperator();
			else
				sql += filters[i].convertToSql();
		}
		sql += ") ";
		return sql;
	}

	@Override
	public Map<String, Object> generateParamMap() {
		Map<String, Object> map = Maps.newHashMap();
		for (JdbcFilter filter : filters) {
			map.putAll(filter.generateParamMap());
		}
		return map;
	}

	public enum Condition {
		AND(" and "), OR(" or ");

		private String operator;

		private Condition(String operator) {
			this.operator = operator;
		}

		public String getOperator() {
			return operator;
		}
	}

	@Override
	public List<Object> generateWhereClauseObject() {
		List<Object> returnList = new ArrayList<Object>();
		for (int i = 0; i < filters.length; i++) {
			returnList.addAll(filters[i].generateWhereClauseObject());
		}
		return returnList;
	}

}
