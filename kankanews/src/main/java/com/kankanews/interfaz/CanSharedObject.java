package com.kankanews.interfaz;

public interface CanSharedObject {
	String getTitle();

	String getShareTitle();

	String getTitleurl();

	String getTitlepic();

	String getSharedPic();

	String getIntro();

	void setSharedPic(String sharepic);

	String getShareIntro();

	void setSharedTitle(String shareTitle);
}
