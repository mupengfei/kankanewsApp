package com.kankanews.bean;

import com.kankanews.base.BaseBean;

import org.json.JSONObject;

public class NewsColumsSecond extends BaseBean<NewsColumsSecond> {

	private String id;// classId
	private String name;
	private String tvLogo;
	private String timeslotS;
	private String timeslotE;

	@Override
	public JSONObject toJSON() {
		return null;
	}

	@Override
	public NewsColumsSecond parseJSON(JSONObject jsonObj) {
		if (jsonObj != null) {
			id = jsonObj.optString("classid");
			name = jsonObj.optString("classname");
			tvLogo = jsonObj.optString("tvlogo");
			timeslotS = jsonObj.optString("timeslot_s");
			timeslotE = jsonObj.optString("timeslot_e");
			return this;
		} else {
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTvLogo() {
		return tvLogo;
	}

	public void setTvLogo(String tvLogo) {
		this.tvLogo = tvLogo;
	}

	public String getTimeslotS() {
		return timeslotS;
	}

	public void setTimeslotS(String timeslotS) {
		this.timeslotS = timeslotS;
	}

	public String getTimeslotE() {
		return timeslotE;
	}

	public void setTimeslotE(String timeslotE) {
		this.timeslotE = timeslotE;
	}

}
