package com.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class RevelationsActicityObjBreakNewsList implements Serializable {
	private String id;
	private RevelationsActivities activity;
	private List<RevelationsBreaknews> breaknews;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RevelationsActivities getActivity() {
		return activity;
	}

	public void setActivity(RevelationsActivities activity) {
		this.activity = activity;
	}

	public List<RevelationsBreaknews> getBreaknews() {
		return breaknews;
	}

	public void setBreaknews(List<RevelationsBreaknews> breaknews) {
		this.breaknews = breaknews;
	}

}
