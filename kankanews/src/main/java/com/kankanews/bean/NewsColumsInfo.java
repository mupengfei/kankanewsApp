package com.kankanews.bean;

import com.kankanews.base.BaseBean;
import com.kankanews.interfaz.CanSharedObject;
import com.lidroid.xutils.db.annotation.Id;

import org.json.JSONObject;

public class NewsColumsInfo extends BaseBean<NewsColumsInfo> implements
		CanSharedObject {
	@Id
	private int id;
	private String mid;
	private String title;
	private String titlepic;
	private String sharedPic;
	private String titleurl;
	private String videoscale;
	private String type;
	private String newstime;
	private String date;
	private String tvLogo;
	private String videoUrl;
	private String intro;
	private String episode;

	private String classId;// 用来区分是什么栏目上的新闻

	private String sharetitle;

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NewsColumsInfo parseJSON(JSONObject jsonObj) {
		mid = jsonObj.optString("id");
		title = jsonObj.optString("title");
		titlepic = jsonObj.optString("titlepic");
		sharedPic = jsonObj.optString("sharepic");
		titleurl = jsonObj.optString("titleurl");
		videoscale = jsonObj.optString("videoscale");
		videoUrl = jsonObj.optString("videourl");
		type = jsonObj.optString("type");
		newstime = jsonObj.optString("newstime");
		date = jsonObj.optString("date");
		tvLogo = jsonObj.optString("tvlogo");
		intro = jsonObj.optString("intro");
		episode = jsonObj.optString("episode");
		return this;
	}

	public String getId() {
		return mid;
	}

	public void setId(String id) {
		this.mid = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitlepic() {
		return titlepic;
	}

	public void setTitlepic(String titlepic) {
		this.titlepic = titlepic;
	}

	public String getTitleurl() {
		return titleurl;
	}

	public void setTitleurl(String titleurl) {
		this.titleurl = titleurl;
	}

	public String getVideoscale() {
		return videoscale;
	}

	public void setVideoscale(String videoscale) {
		this.videoscale = videoscale;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNewstime() {
		return newstime;
	}

	public void setNewstime(String newstime) {
		this.newstime = newstime;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getSharedPic() {
		return sharedPic;
	}

	public void setSharedPic(String sharedPic) {
		this.sharedPic = sharedPic;
	}

	public String getTvLogo() {
		return tvLogo;
	}

	public void setTvLogo(String tvLogo) {
		this.tvLogo = tvLogo;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getEpisode() {
		return episode;
	}

	public void setEpisode(String episode) {
		this.episode = episode;
	}

	@Override
	public String getShareTitle() {
		// TODO Auto-generated method stub
		if (this.sharetitle == null)
			return title;
		return this.sharetitle;
	}

	@Override
	public void setSharedTitle(String shareTitle) {
		// TODO Auto-generated method stub
		this.sharetitle = shareTitle;
	}

	@Override
	public String getShareIntro() {
		return this.getIntro();
	}

}
