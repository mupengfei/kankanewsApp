package com.kankanews.bean;

import java.io.Serializable;
import java.util.List;

public class RevelationsHomeList implements Serializable {
	private String id;
	private List<RevelationsActivities> activity;
	private List<RevelationsBreaknews> breaknews;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RevelationsActivities> getActivity() {
		return activity;
	}

	public void setActivity(List<RevelationsActivities> activity) {
		this.activity = activity;
	}

	public List<RevelationsBreaknews> getBreaknews() {
		return breaknews;
	}

	public void setBreaknews(List<RevelationsBreaknews> breaknews) {
		this.breaknews = breaknews;
	}

}
