package com.kankanews.base;

import org.json.JSONObject;

import java.io.Serializable;

public abstract class BaseBean<T> implements Serializable {

	public boolean checkJson(JSONObject jo) {

		String optString = jo.optString("return_num");
		if (optString != null) {
			if (optString.equals("1")) {
				return true;
			} else {
				String errorMsg = jo.optString("return_msg");
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 将Bean实例转化为json对象
	 * 
	 * @return
	 */
	public abstract JSONObject toJSON();

	/**
	 * 将json对象转化为Bean实例
	 * 
	 * @param jsonObj
	 * @return
	 */
	public abstract T parseJSON(JSONObject jsonObj);
	
}
