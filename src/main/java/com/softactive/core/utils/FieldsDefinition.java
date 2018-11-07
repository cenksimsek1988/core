package com.softactive.core.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = "field", includeFieldNames = false)
public class FieldsDefinition {
	private Field field;
	private JdbcColumn jdbcColumn;
	private List<FieldsDefinition> fieldsDefinitionList;
	private FieldsDefinition parentFieldsDefinition;

	public FieldsDefinition(Field field, JdbcColumn jdbcColumn) {
		this.field = field;
		this.jdbcColumn = jdbcColumn;
	}

	private void setParentFieldsDefinition(FieldsDefinition parentFieldsDefinition) {
		this.parentFieldsDefinition = parentFieldsDefinition;
	}

	public void addChildFieldsDefinition(FieldsDefinition child) {
		if (fieldsDefinitionList == null) {
			fieldsDefinitionList = new ArrayList<FieldsDefinition>();
		}
		child.setParentFieldsDefinition(this);
		fieldsDefinitionList.add(child);
	}

	public List<FieldsDefinition> getJoinedKeyFieldsDefinitions() {
		List<FieldsDefinition> returnList = new ArrayList<FieldsDefinition>();
		if (fieldsDefinitionList == null) {
			if (isKeyToTheRoot()) {
				returnList.add(this);
			}
		} else {
			for (FieldsDefinition fd : getFieldsDefinitionList()) {
				returnList.addAll(fd.getJoinedKeyFieldsDefinitions());
			}
		}
		return returnList;
	}

	public boolean isKeyToTheRoot() {
		if (parentFieldsDefinition == null) {
			return jdbcColumn.keyValue() == JdbcColumn.KEY;
		} else {
			return jdbcColumn.keyValue() == JdbcColumn.KEY && parentFieldsDefinition.isKeyToTheRoot();
		}
	}

	public String getFieldNameObjectToRoot() {
		if (parentFieldsDefinition == null) {
			return field.getName();
		} else {
			return parentFieldsDefinition.getFieldNameObjectToRoot() + "." + field.getName();
		}
	}

	public FieldsDefinition getJoinedFieldsDefinition() {
		if (!jdbcColumn.joinField().isEmpty()) {
			for (FieldsDefinition fd : fieldsDefinitionList) {
				if (fd.jdbcColumn.field().equalsIgnoreCase(jdbcColumn.joinField())) {
					return fd;
				}
			}
		}
		return null;
	}

	public String getFieldNameObjectToLeaf() {
		FieldsDefinition fd = getJoinedFieldsDefinition();
		if (fd != null) {
			return field.getName() + "." + fd.getFieldNameObjectToLeaf();
		}
		return field.getName();
	}
}
