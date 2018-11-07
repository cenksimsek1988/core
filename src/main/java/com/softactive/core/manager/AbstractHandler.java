package com.softactive.core.manager;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nonnull;

import com.softactive.core.exception.MyError;
import com.softactive.core.exception.MyException;
import com.softactive.core.object.Base;
import com.softactive.core.utils.ParserUtils;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractHandler<INPUT, PARSED_INPUT, INPUT_BODY, PARSED_INPUT_COMPONENT, OUTPUT_COMPONENT, OUTPUT> extends ParserUtils implements Serializable {
	private static final long serialVersionUID = -7865217654203065084L;
	public static final String TYPE_STRING = "String";
	public static final String TYPE_INTEGER_ID = "IntegerId";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_DATE = "Date";

	public static final String SAVE_WORLD_BANK_INDICATOR_STATUS = "indicator";
	public static final String SAVE_RISK_FACTOR_STATUS = "risk_factor";
	public static final String SAVE_HISTORICAL_SATUS = "historical";
	public static final String SAVE_UPDATE_STATUS = "update";

	@Getter @Setter
	private PostTask post;
	@Setter
	@Getter
	private Integer progress = null;
	protected Map<String, Object> sharedParams = null;
	
	public AbstractHandler(Map<String, Object> sharedParams) {
		this.sharedParams = sharedParams;
	}

	public Map<String, Object> handle(INPUT f) {
		PARSED_INPUT parsedInput;
		try {
			parsedInput = parsedInput(f);
		} catch (MyException e) {
			MyError er = new MyError(ERROR_INVALID_RESPONSE_FORMAT, e.getMsg());
			sharedParams.put(PARAM_ERROR, er);
			sharedParams.put(PARAM_SUCCESS, false);
			onError();
			return sharedParams;
		}
		try {
			mapMetaData(parsedInput);
		} catch (MyException e) {
			MyError er = new MyError(ERROR_PARSING_METADATA, e.getMsg() + " while mapping meta data");
			sharedParams.put(PARAM_ERROR, er);
			sharedParams.put(PARAM_SUCCESS, false);
			onError();
			return sharedParams;
		}
		OUTPUT list = null;
		INPUT_BODY inputList = null;
		try {
			inputList = inputBody(parsedInput);
		} catch (MyException e1) {
			MyError er = new MyError(ERROR_PARSING_LIST, e1.getMsg());
			sharedParams.put(PARAM_ERROR, er);
			sharedParams.put(PARAM_SUCCESS, false);
			onError();
			return sharedParams;
		}
		list = output(inputList);
		if(list == null) {
			MyError er = new MyError(ERROR_PARSING_LIST, "null output");
			System.out.println(er);
			sharedParams.put(PARAM_ERROR, er);
			sharedParams.put(PARAM_SUCCESS, false);
			onError();
			return sharedParams;
		} else if(isOutputInvalid(list)) {
			MyError er = new MyError(ERROR_PARSING_LIST, "empty output");
			System.out.println(er);
			sharedParams.put(PARAM_ERROR, er);
			sharedParams.put(PARAM_SUCCESS, false);
			onError();
			return sharedParams;
		} else {
			sharedParams.put(PARAM_SUCCESS, true);
			onListSuccessfullyParsed(list);
		}
		boolean hasNext = hasNext(sharedParams);
		sharedParams.put(PARAM_HAS_NEXT, hasNext);
		if(!hasNext) {
			if(post!=null) {
				post.onPost(sharedParams);
			}
		}
		return sharedParams;
	}
	
	protected abstract boolean isOutputInvalid(@Nonnull final OUTPUT output);

	protected void saveUpdateError(Map<String, Object> sharedParams) {}

	protected void saveUpdateError(Base rf, String msg, int errorType) {}

	public void onListSuccessfullyParsed(OUTPUT list) {
		sharedParams.put(PARAM_LIST, list);
	}

	public void onError() {
		printError();
	}

	protected abstract PARSED_INPUT parsedInput(final INPUT rowInput) throws MyException;

	protected abstract void mapMetaData(final PARSED_INPUT r) throws MyException;

	protected abstract boolean hasNext(Map<String, Object> metaMap);

	protected abstract INPUT_BODY inputBody(PARSED_INPUT r) throws MyException;

	protected abstract OUTPUT output(INPUT_BODY array);

	public void printError() {
		MyError er = (MyError) sharedParams.get(PARAM_ERROR);
		if(er!=null) {
			System.out.println("type: " + er.getType() + "\nmessage: " + er.getResult());
		}
	}

	protected abstract OUTPUT_COMPONENT outputComponent(PARSED_INPUT_COMPONENT o) throws MyException;

	public void clear() {
		progress = null;
	}

}
