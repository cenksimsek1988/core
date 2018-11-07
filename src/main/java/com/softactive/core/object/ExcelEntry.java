package com.softactive.core.object;

import java.util.Set;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;


public class ExcelEntry{
	@Getter @Setter
	private TreeMap<String, String> extras = new TreeMap<>();
	
	protected TreeMap<String, String> preColumnsMap(){
		return new TreeMap<String, String>();
	};
	
	protected void addPostColumns(TreeMap<String, String> entryMap){
	};
	
	public TreeMap<String, String> map(){
		TreeMap<String, String> answer = preColumnsMap();
		for(String columnName:extras.keySet()) {
			answer.put(columnName, extras.get(columnName));
		}
		addPostColumns(answer);
		return answer;
	}
	
	
	
	
	
	
	
	
	
	
	
	// will be removed in next versions
	protected TreeMap<String, String> prices = new TreeMap<>();
	public void addPrice(String date, String price) {
		prices.put(date, price);
	}
	
	public void removePrice(String pseudoDate) {
		prices.remove(pseudoDate);
	}
	public Set<String> getPriceKeys(){
		return prices.keySet();
	}
	public String getPrice(String pseudoDate) {
		return prices.get(pseudoDate);
	}
	public TreeMap<String, String> getValuesAsMap(){
		TreeMap<String, String> answer = preColumnsMap();
		for(String date:prices.keySet()) {
			answer.put(date, String.valueOf(prices.get(date)));
		}
		for(String columnName:extras.keySet()) {
			answer.put(columnName, extras.get(columnName));
		}
		addPostColumns(answer);
		return answer;
	}
}
