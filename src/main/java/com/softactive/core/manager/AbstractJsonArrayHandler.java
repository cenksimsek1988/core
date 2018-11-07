package com.softactive.core.manager;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class AbstractJsonArrayHandler<OUTPUT_COMPONENT> extends AbstractJsonHandler<JSONArray, OUTPUT_COMPONENT> {

	public AbstractJsonArrayHandler(Map<String, Object> sharedParams) {
		super(sharedParams);
	}
	private static final long serialVersionUID = 444721503233273186L;
	@Override
	protected JSONArray parseFromString(String inputString) throws JSONException{
		return new JSONArray(inputString); 
	}
}
