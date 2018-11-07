package com.softactive.core.repository;

import java.sql.SQLException;
import java.util.List;

import org.apache.xmlbeans.impl.piccolo.util.DuplicateKeyException;

import com.softactive.core.object.Base;
import com.softactive.core.object.CoreConstants;

public abstract class AbstractDataService<T extends Base> implements CoreConstants {

	public String initQuery() {
		return "select * from " + tableName();
	}

	public abstract String tableName();
	
	public T find(int id) {
		String sql = initQuery() + " where id=" + id + " limit 1";
		List<T> list = query(sql);
		if(list.size()==1) {
			return list.get(0);
		}
		return null;
	}
	
	public List<T> listOfObject(){
		return query(initQuery());
	}
	
	public void save(List<T> list) {
		for(T t:list) {
			save(t);
		}
	}
	
	public abstract T findUnique(T t);
	
	public int save(T t) {
		T in = findUnique(t);
		if(in==null) {
			try {
				return insert(t);
			} catch (DuplicateKeyException e) {
				System.out.println("Complex double unique key error");
				return -1;
			}
		} else {
			t.setId(in.getId());
			update(t);
			return t.getId();
		}
	}
	
	public abstract void update(T t);
//
	public abstract int insert(T t) throws DuplicateKeyException;
//	
	public abstract void delete(int id);

	public abstract List<T> query(String sql);
	
	public void delete(List<T> list) {
		for(T t:list) {
			delete(t.getId());
		}
	}
}
