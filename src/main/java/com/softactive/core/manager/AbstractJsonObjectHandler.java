package com.softactive.core.manager;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractJsonObjectHandler<OUTPUT_COMPONENT> extends AbstractJsonHandler<JSONObject, OUTPUT_COMPONENT> {
	public AbstractJsonObjectHandler(Map<String, Object> sharedParams) {
		super(sharedParams);
	}
	private static final long serialVersionUID = -1370856627336689478L;
	@Override
	protected JSONObject parseFromString(String inputString) throws JSONException{
		return new JSONObject(inputString); 
	}

}
