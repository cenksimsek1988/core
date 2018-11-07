package com.softactive.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.softactive.core.exception.MyError;
import com.softactive.core.exception.MyException;

public abstract class AbstractJsonHandler<PARSED_INPUT, OUTPUT_COMPONENT>
extends AbstractHandler<String, PARSED_INPUT, JSONArray, JSONObject, OUTPUT_COMPONENT, List<OUTPUT_COMPONENT>> {

	public AbstractJsonHandler(Map<String, Object> sharedParams) {
		super(sharedParams);
	}

	private static final long serialVersionUID = 3758384580767903932L;

	@Override
	protected List<OUTPUT_COMPONENT> output(JSONArray array) {
		List<OUTPUT_COMPONENT> list = new ArrayList<>();
		if(array==null) {
			MyError er = new MyError(ERROR_NULL_JSON_ARRAY, "JSON Array is not found. ");
			sharedParams.put(PARAM_ERROR, er);
			return list;
		}
		if (array.length()==0) {
			MyError er = new MyError(ERROR_EMPTY_JSON_ARRAY, array.toString());
			sharedParams.put(PARAM_ERROR, er);
			return list;
		}
		for (int i = 0; i < array.length(); i++) {
			OUTPUT_COMPONENT p = null;
			JSONObject jo = null;
			try {
				jo = array.getJSONObject(i);
			} catch (JSONException e1) {
				MyError er = new MyError(ERROR_INVALID_JSON_OBJECT_FORMAT, array.toString());
				sharedParams.put(PARAM_ERROR, er);
				return list;
			}
			try {
				p = outputComponent(jo);
			} catch (MyException e) {
				MyError er = new MyError(ERROR_INVALID_VALUE, e.getMsg() + "/n" + jo.toString());
				sharedParams.put(PARAM_ERROR, er);
			}
			if (p != null) {
				list.add(p);
			}
		}
		return list;
	}
	
	@Override
	protected PARSED_INPUT parsedInput(String in) throws MyException {
		try {
			return parseFromString(in);
			
		} catch (JSONException e) {
			throw new MyException("Input format is not as expected. input as string: " + in);
		}
	}
	
	protected abstract PARSED_INPUT parseFromString(String inputString) throws JSONException;

	protected Object resolveValidValue(JSONObject jo, String key, String type, @Nullable Integer expectedLenght) throws MyException {
		switch (type) {
		case TYPE_STRING:
			return resolveValidString(jo, key, expectedLenght);
		case TYPE_INTEGER_ID:
			return resolveValidIntegerId(jo, key, expectedLenght);
		default:
			System.out.println("Cannot get valid value: Type is uknown: " + type);
			return null;
		}
	}

	protected int resolveValidInteger(JSONObject jo, String key) throws MyException {
		Integer i = null;
		try {
			i = jo.getInt(key);
		} catch (JSONException e) {
			throw new MyException("No Integer value (key: " + key + ") found in JSON Object. ");
		}
		return i;
	}

	protected JSONObject resolveValidJSONObject(JSONObject jo, String key) throws MyException {
		try {
			return jo.getJSONObject(key);
		} catch (JSONException e) {
			throw new MyException("No JSON Object value found in JSON Object with key: " + key + ". ");
		}
	}

	protected String optValidString(JSONObject jo, String key, @Nullable Integer expectedLenght) throws MyException {
		String s;
		try {
			s = jo.getString(key);
		} catch (JSONException e) {
			throw new MyException("No String value found in JSON Object with key: " + key + ". ");
		}
		try {
			return resolveValidString(s, expectedLenght);
		} catch (MyException e){
			return null;
		}
	}

	protected String resolveValidString(JSONObject jo, String key, @Nullable Integer expectedLenght) throws MyException {
		String s;
		try {
			s = jo.getString(key);
		} catch (JSONException e) {
			throw new MyException("No String value found in JSON Object with key: " + key + ". ");
		}
		try {
			s = resolveValidString(s, expectedLenght);
		} catch (MyException e){
			e.setMsg(e.getMessage() + " (key: " + key + ") ");
			throw e;
		}
		return s;
	}

	protected Integer optValidIntegerId(JSONObject jo, String key, @Nullable Integer expectedLenght) throws MyException {
		Integer i = resolveValidInteger(jo, key);
		if (i < 0) {
			return null;
		}
		if (expectedLenght != null) {
			if (i >= (10 ^ expectedLenght)) {
				return null;
			}
		}
		return i;
	}

	protected Integer resolveValidIntegerId(JSONObject jo, String key, @Nullable Integer expectedLenght) throws MyException {
		Integer i = resolveValidInteger(jo, key);
		if (i < 0) {
			throw new MyException("Integer id (key: " + key + ") value is negative. value: " + i + ". ");
		}
		if (expectedLenght != null) {
			if (i >= (10 ^ expectedLenght)) {
				throw new MyException("Integer id (key: " + key + ") value is longer then expected. expected lenght: " + expectedLenght +
						". value: " + i + ". ");
			}
		}
		return i;
	}
	protected LocalDate resolveValidDate(JSONObject jo, String key) throws MyException {
		String s = null;
		LocalDate date = null;
		try {
			s = jo.getString(key);
		} catch (JSONException e) {
			throw new MyException("No Date value (key: " + key + ") found in JSON Object. ");
		}
		try {
			date = resolveValidDate(s);
		} catch (MyException e) {
			e.setMsg(e.getMessage() + "(key: " + key + ")");
			throw e;
		}
		return date;
	}

	protected double resolveValidDouble(JSONObject jo, String key) throws MyException {
		Double value = null;
		try {
			value = jo.getDouble(key);
		} catch (JSONException e) {
			throw new MyException("Double value (key: " + key + ") is not found.");
		}
		return value;
	}
}
