package com.softactive.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Interval;

import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class SimpleJdbcFilter implements JdbcFilter {

	private String columnName;
	private Relation relation;
	private Object columnValue;

	@Override
	public String convertToSql() {
		if (relation == Relation.EQ && columnValue == null) {
			return columnName + " is null";
		} else if (relation == Relation.NEQ && columnValue == null) {
			return columnName + " is not null";
		} else if (relation == Relation.IN || relation == Relation.NIN) {
			return columnName + relation.getOperator() + " ("
					+ StringUtils.repeat("?", ",", ((List<?>) columnValue).size()) + ") ";
		} else
			return columnName + relation.getOperator() + "?";
	}

	@Override
	public Map<String, Object> generateParamMap() {
		Map<String, Object> map = Maps.newHashMap();
		if (relation == Relation.EQ && columnValue == null)
			return map;
		else {
			if (!(columnValue instanceof Interval)) {
				throw new IllegalArgumentException("Interval was expected for Relation.BETWEEN");
			}
			map.put("start_" + columnName.replace(".", "") + Math.abs(this.hashCode()),
					((Interval) columnValue).getStart().toLocalDate().toDate());
			map.put("end_" + columnName.replace(".", "") + Math.abs(this.hashCode()),
					((Interval) columnValue).getEnd().toLocalDate().toDate());
		}

		return map;
	}

	public enum Relation {
		EQ(" = "), NEQ(" != "), IN(" in "), NIN(" not in ");

		private String operator;

		private Relation(String operator) {
			this.operator = operator;
		}

		public String getOperator() {
			return operator;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Object> generateWhereClauseObject() {
		List<Object> returnList = new ArrayList<Object>();
		if (columnValue != null) {
			if (columnValue instanceof List) {
				returnList.addAll((List) columnValue);
			} else {
				returnList.add(columnValue);
			}
		}
		return returnList;
	}

}
