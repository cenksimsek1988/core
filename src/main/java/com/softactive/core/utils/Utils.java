package com.softactive.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

public class Utils {

	public static boolean isEmpty(Object obj) {
		return obj == null || obj.equals("");
	}

	public static String formatMessage(String message, Object... parameters) {

		DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");

		for (Object object : parameters) {
			if (object instanceof Date)
				object = dFormat.format((Date) object);
		}

		message = MessageFormat.format(message, parameters);
		return message;
	}

	public static <O> List<String> extractStringFieldToList(List<O> list, String property) {
		List<String> returnList = new ArrayList<String>();
		for (O object : list) {
			try {
				String propertyObject = (String) PropertyUtils.getProperty(object, property);
				returnList.add(propertyObject);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return returnList;
	}

	@SuppressWarnings("rawtypes")
	public static Field[] getAllFields(Class c) {
		List<Field> fields = new ArrayList<Field>();
		getAllFieldsRec(c, fields);
		return fields.toArray(new Field[0]);
	}

	@SuppressWarnings("rawtypes")
	public static void getAllFieldsRec(Class c, List<Field> fields) {
		Class superClass = c.getSuperclass();
		if (superClass != null) {
			getAllFieldsRec(superClass, fields);
		}
		Field[] a = c.getDeclaredFields();
		for (Field f : a) {
			if (!f.getName().equals("serialVersionUID")) {
				fields.add(f);
			}
		}
	}

}