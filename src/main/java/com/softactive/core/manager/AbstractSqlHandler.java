package com.softactive.core.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.softactive.core.exception.MyException;
import com.softactive.core.object.Base;
import com.softactive.core.repository.AbstractDataService;

public abstract class AbstractSqlHandler<DATA_OBJECT extends Base, OUTPUT_COMPONENT> extends AbstractHandler<AbstractDataService<DATA_OBJECT>, List<DATA_OBJECT>, List<DATA_OBJECT>, DATA_OBJECT, OUTPUT_COMPONENT, List<OUTPUT_COMPONENT>> {
	public AbstractSqlHandler(Map<String, Object> sharedParams) {
		super(sharedParams);
	}

	private static final long serialVersionUID = 2985193212196151772L;

	@Override
	protected List<DATA_OBJECT> parsedInput(AbstractDataService<DATA_OBJECT> rowInput) throws MyException {
		return rowInput.query(rowInput.initQuery());
	}

	@Override
	protected void mapMetaData(List<DATA_OBJECT> r) throws MyException {
	}

	@Override
	protected boolean hasNext(Map<String, Object> metaMap) {
		return false;
	}

	@Override
	protected List<DATA_OBJECT> inputBody(List<DATA_OBJECT> r) {
		return r;
	}

	@Override
	protected List<OUTPUT_COMPONENT> output(List<DATA_OBJECT> array) {
		List<OUTPUT_COMPONENT> list = new ArrayList<OUTPUT_COMPONENT>();
		for(DATA_OBJECT o:array) {
			try {
				list.add(outputComponent(o));
			} catch (MyException e) {
				System.out.println(e);
			}
		}
		return list;
	}

}
