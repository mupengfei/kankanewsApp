package com.kankanews.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MessagePushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

//		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//
//		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
//				.getAction())) {
//		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
//				.getAction())) {
//		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
//				.getAction())) {
//			if (bundle != null) {
//				KankanewsApplication a;
//				a = (KankanewsApplication) (context.getApplicationContext());
//
//				String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
//				if (extra != null && !extra.equals("")) {
//					try {
//						JSONObject jsonObject = new JSONObject(extra);
//						String newsId = jsonObject.optString("PUSH_NEWS_ID");
//						String liveId = jsonObject.optString("LIVE_ID");
//						if (newsId != null && !newsId.equals("")) {
//							// 打开自定义的Activity
//							if (a.getMainActivity() == null && !a.isStart()) {
//								Intent i = new Intent(context,
//										StartupActivity.class);
//								bundle.putString("PUSH_NEWS_ID", newsId);
//								i.putExtras(bundle);
//								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//								context.startActivity(i);
//							} else {
//								NetUtils netUtils = NetUtils
//										.getInstance(context);
//								netUtils.getNewsContentDataPush(newsId,
//										new Response.Listener<JSONObject>() {
//											@Override
//											public void onResponse(
//													JSONObject jsonObject) {
//												if (jsonObject == null
//														|| jsonObject
//																.toString()
//																.trim()
//																.equals("")) {
//													return;
//												}
//												New_News_Home news = new New_News_Home();
//												try {
//													news.parseJSON(jsonObject);
//													openNews(context, news);
//												} catch (NetRequestException e) {
//													e.printStackTrace();
//												}
//											}
//										}, new Response.ErrorListener() {
//											@Override
//											public void onErrorResponse(
//													VolleyError error) {
//												return;
//											}
//										});
//							}
//						} else if (liveId != null && !liveId.equals("")) {
//							if (a.getMainActivity() == null && !a.isStart()) {
//								Intent i = new Intent(context,
//										StartupActivity.class);
//								bundle.putString("LIVE_ID", liveId);
//								i.putExtras(bundle);
//								i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//								context.startActivity(i);
//							}
//						}
//					} catch (JSONException e) {
//						Log.e("", "", e);
//					}
//				}
//			}
//		} else {
//		}

	}
//
//	private void openNews(final Context context, New_News_Home news) {
//		//
//		if (news.getTitle() == null || news.getTitle().trim().equals("")) {
//			return;
//		}
//
//		final int news_type = Integer.valueOf(news.getType());
//		NewsHomeModuleItem moduleItem = new NewsHomeModuleItem();
//		moduleItem.setId(news.getMid());
//		moduleItem.setO_cmsid(news.getMid());
//		moduleItem.setAppclassid(news.getZtid());
//		moduleItem.setTitle(news.getTitle());
//		moduleItem.setTitlepic(news.getTitlepic());
//		moduleItem.setTitleurl(news.getTitleurl());
//		if (news_type % 10 == 1) {
//			moduleItem.setType("video");
//			this.startAnimActivityByNewsHomeModuleItem(context,
//					NewsContentActivity.class, moduleItem);
//		} else if (news_type % 10 == 2) {
//			moduleItem.setType("album");
//			this.startAnimActivityByNewsHomeModuleItem(context,
//					NewsAlbumActivity.class, moduleItem);
//		} else if (news_type % 10 == 6) {
//		} else if (news_type % 10 == 5 || news_type % 10 == 8
//				|| news.getZtype().equals("1")) {
//			moduleItem.setType("topic");
//			moduleItem.setAppclassid(news.getLabels());
//			NetUtils netUtils = NetUtils.getInstance(context);
//			netUtils.getTopicData(moduleItem.getAppclassid(),
//					new Listener<JSONObject>() {
//						@Override
//						public void onResponse(JSONObject json) {
//							NewsHomeModule moduleItem = JsonUtils.toObject(
//									json.toString(), NewsHomeModule.class);
//							NewsHomeModuleItem homeModuleItem = new NewsHomeModuleItem();
//							homeModuleItem.setAppclassid(moduleItem
//									.getAppclassid());
//							if (moduleItem.getCategory() != null
//									&& !moduleItem.getCategory().trim()
//											.equals("")
//									&& !moduleItem.getCategory().trim()
//											.equalsIgnoreCase("list")) {
//								startAnimActivityByNewsHomeModuleItem(context,
//										NewsTopicActivity.class, homeModuleItem);
//							} else {
//								startAnimActivityByNewsHomeModuleItem(context,
//										NewsTopicListActivity.class,
//										homeModuleItem);
//							}
//						}
//					}, new ErrorListener() {
//						@Override
//						public void onErrorResponse(VolleyError arg0) {
//							return;
//						}
//					});
//		} else {
//			moduleItem.setType("outlink");
//			this.startAnimActivityByNewsHomeModuleItem(context,
//					NewsOutLinkActivity.class, moduleItem);
//		}
//	}
//
//	public void startAnimActivityByNewsHomeModuleItem(Context context,
//			Class<?> cla, NewsHomeModuleItem moduleItem) {
//		Intent intent = new Intent(context, cla);
//		intent.putExtra("_NEWS_HOME_MODEULE_ITEM_", moduleItem);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		context.startActivity(intent);
//	}
}
