package com.kankanews.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.kankanews.bean.ValidateInfo;
import com.kankanews.bean.VideoUploadResult;
import com.kankanews.config.AndroidConfig;
import com.kankanews.utils.volleyrequest.CustomRequest;
import com.kankanews.utils.volleyrequest.CustomRequestArray;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class NetUtils {

    private static NetUtils instance;
    private static Random _random = new Random();
    private RequestQueue mRequestQueue;
    private CustomRequest mCustomRequest;
    private CustomRequestArray mCustomRequestArray;
    private Context mContext;

    private String separator = "__";

    private NetUtils(Context mContext) {
        this.mContext = mContext;
        mRequestQueue = Volley.newRequestQueue(mContext);
    }

    ;

    public static NetUtils getInstance(Context mContext) {
        if (instance == null) {
            instance = new NetUtils(mContext);
        }
        return instance;
    }

    /**
     * 获取新闻详细 数据
     */
    public void getNewsContentDataPush(String news_id,
                                       Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NewContentPush
                        + news_id, null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取新闻首页列表
     */
    public void getNewsHomeList(Listener<JSONObject> reponseListener,
                                ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.GET,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_HOME_DATA
                        + "?_random=" + _random.nextInt(10000), null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取主页条目
     */
    public void getNewHomeCateData(Listener<JSONArray> reponseListener,
                                   ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_HomeCateData,
                null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);

    }

    /**
     * 获取首页数据
     */
    public void getNewHomeData(String lastnewstime, String chassid, String sp,
                               Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_HomeData
                        + chassid + "/sp/" + sp + "/timestamp/" + lastnewstime,
                null, reponseListener, errorListener);

        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取首页随机数据
     */
    public void getNewHomeChange(String classid, String time, int pageNum,
                                 Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_HOME_DATA_CHANGE
                        + classid + "/timestamp/" + time + "/page/" + pageNum
                        + "/version/1", null, reponseListener, errorListener);

        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取首页投票随机数据
     */
    public void getNewHomeVoteChange(String classid, String voteId,
                                     Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST
                        + AndroidConfig.NEWS_HOME_VOTE_DATA_CHANGE + classid
                        + "/vot/" + voteId, null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 提交投票结果
     */
    public void putVoteAnswer(String classid, String voteId, String optionId,
                              Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_HOME_VOTE_PUT
                        + classid + "/vot/" + voteId + "/opt/" + optionId,
                null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取新闻列表
     */
    public void getNewsList(String classid, String lastnewstime,
                            Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_LIST_DATA
                        + classid + "/timestamp/" + lastnewstime, null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取新闻点击量
     */
    public void getNewNewsClickData(String midtype,
                                    Listener<JSONArray> reponseListener, ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.New_NewsClick + midtype, null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 添加新闻点击量 mid id tjid
     */
    public void addNewNewsClickData(String id) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.New_NewsAddClick + id, null, null, null);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获取新闻详情
     */
    public void getNewNewsContent(String mid, String type,
                                  Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_NewsContent + mid
                        + "/mtype/" + (Integer.valueOf(type) % 10), null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取新闻内容页数据
     */
    public void getNewsContent(String id, String type,
                               Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NewsContent + type
                        + "_" + id, null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取直播数据
     */
    public void getNewLivePlayData(Listener<JSONArray> reponseListener,
                                   ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_LivePlay, null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获取直播数据
     */
    public void getLiveList(Listener<JSONObject> reponseListener,
                            ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.LIVE_LIST_URL + "?r="
                        + UUIDUtils.getUUID(8), null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取直播频道数据
     */
    public void getChannelList(Listener<JSONObject> reponseListener,
                               ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.LIVE_CHANNEL_URL
                        + "?r=" + UUIDUtils.getUUID(8), null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取栏目列表
     */
    public void getNewColumsData(Listener<JSONArray> reponseListener,
                                 ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                // AndroidConfig.New_NETHOST +
                AndroidConfig.New_Colums, null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获取栏目列表带二级菜单
     */
    public void getNewColumsSecondData(Listener<JSONArray> reponseListener,
                                       ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.New_Colums_Second_Level, null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获取栏目节目列表
     *
     * @param datestamp 选择日期的时间戳,可以为空，默认为当天
     * @param newstime  用于分页，可以为空，默认为第一页
     */
    public void getNewColumsInfoData(String classid, String datestamp,
                                     String newstime, Listener<JSONArray> reponseListener,
                                     ErrorListener errorListener) {
        datestamp = TextUtils.isEmpty(datestamp) ? "" : "/day/" + datestamp;
        newstime = TextUtils.isEmpty(newstime) ? "" : "/timestamp/" + newstime;
        mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_Colums_Info
                        + classid + datestamp + newstime, null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获取专题详情
     */
    public void getSubjectData(String ztid,
                               Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.New_Subject + ztid,
                null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取专题详情(V3.0)
     */
    public void getTopicData(String appClassId,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.KANKAN_HOST + AndroidConfig.NEWS_LIST_DATA
                        + appClassId, null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获得热门推荐接口
     */
    public void getRecommendData(Listener<JSONArray> reponseListener,
                                 ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.POST,
                AndroidConfig.New_Recommend, null, reponseListener,
                errorListener);
        mRequestQueue.add(mCustomRequestArray);
    }

    /**
     * 获得热门推荐接口
     */
    public void getAdert(Map<String, String> params,
                         Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.ADVERT_GET, params, reponseListener,
                errorListener);

        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取搜索条目
     */
    public void getSearchData(String searchContent, int pageNum,
                              Listener<JSONArray> reponseListener, ErrorListener errorListener) {
        try {
            searchContent = URLEncoder.encode(searchContent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
                AndroidConfig.KANKAN_HOST + AndroidConfig.SEARCH_GET + "?w="
                        + searchContent + "&p=" + pageNum, null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);

    }

    /**
     * 获取搜索条目
     */
    public void getSearchHotWord(Listener<JSONArray> reponseListener,
                                 ErrorListener errorListener) {
        mCustomRequestArray = new CustomRequestArray(Request.Method.GET,
                AndroidConfig.KANKAN_HOST + AndroidConfig.SEARCH_HOT_WORD,
                null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequestArray);

    }

    /**
     * 获取报料首页条目
     */
    public void getRevelationsHomeList(Listener<JSONObject> reponseListener,
                                       ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(
                Request.Method.GET,
                AndroidConfig.KANKAN_HOST + AndroidConfig.REVELATIONS_HOME_DATA,
                null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取报料活动详情
     */
    public void getRevelationsActivityList(String aid, String timestamp,
                                           Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.GET,
                AndroidConfig.KANKAN_HOST
                        + AndroidConfig.REVELATIONS_ACTIVITY_DATA + "/aid/"
                        + aid + "/timestamp/" + timestamp, null,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 获取报料更多
     */
    public void getRevelationsBreaknewsMore(String timestamp,
                                            Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        mCustomRequest = new CustomRequest(Request.Method.GET,
                AndroidConfig.KANKAN_HOST
                        + AndroidConfig.REVELATIONS_BREAKNEWS_MORE_DATA
                        + timestamp, null, reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    /**
     * 提交报料内容
     */
    public void postRevelationContent(String tel, String content,
                                      String imageUrls, Listener<JSONObject> reponseListener,
                                      ErrorListener errorListener) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phonenum", tel);
        params.put("newstext", content);
        params.put("imagegroup", imageUrls);
        mCustomRequest = new CustomRequest(Request.Method.POST,
                AndroidConfig.REVELATIONS_CONTENT_POST, params,
                reponseListener, errorListener);
        mRequestQueue.add(mCustomRequest);
    }

    public void getAnalyse(Context context, String type, String title,
                           String titleUrl) {
        String url;
        try {
            url = AndroidConfig.New_NewsAnalyse + "?itemType=" + type
                    + "&pageTitle=" + URLEncoder.encode(title, "utf-8")
                    + "&pageURL=" + titleUrl;
            new AnalyseGetThread(url, context).start();
        } catch (UnsupportedEncodingException e1) {
            Log.e("getAnalyse", e1.getLocalizedMessage());
        }
    }

    private class AnalyseGetThread extends Thread {
        private String url;
        private Context context;

        AnalyseGetThread(String url, Context context) {
            this.url = url;
            this.context = context;
        }

        @Override
        public void run() {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager
                        .getActiveNetworkInfo();
                HttpGet httpRequest = new HttpGet(url);
                // httpRequest.setHeader("MOBILE_DEVICE_INFO",
                // android.os.Build.MODEL);
                String operatorName = telephonyManager.getNetworkOperatorName()
                        .trim().equals("") ? "null" : telephonyManager
                        .getNetworkOperatorName();
                httpRequest.setHeader(
                        "User-Agent",
                        "kankanapp(" + android.os.Build.MODEL + separator
                                + "kankanapp" + separator
                                + CommonUtils.getVersion(context) + separator
                                + "Android" + separator + "Android"
                                + android.os.Build.VERSION.RELEASE + separator
                                + operatorName + separator
                                + activeNetInfo.getTypeName() + ")");
                HttpResponse httpResponse;
                httpResponse = new DefaultHttpClient().execute(httpRequest);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("AnalyseGetThread", e.getLocalizedMessage()
                        + "");
            }
        }
    }

    public static VideoUploadResult getTokenUploadVideo(File video,
                                                        String deviceId) {
        // BufferedReader responseReader = null;
        HttpURLConnection conn = null;
        StringBuffer responseContent = new StringBuffer();
        HttpGet httpGet = null;
        HttpResponse httpResponse = null;
        try {
            String path = AndroidConfig.REVELATIONS_GET_VIDEO_UPLOAD_TOKEN
                    + "?name=" + video.getName() + "_" + deviceId + "&size="
                    + video.length();
            httpGet = new HttpGet(path);
            httpResponse = new DefaultHttpClient().execute(httpGet);
            responseContent.append(EntityUtils.toString(httpResponse
                    .getEntity()));
        } catch (Exception e) {
            DebugLog.e(e.getLocalizedMessage());
            VideoUploadResult result = new VideoUploadResult();
            result.setSuccess(false);
            return result;
        } finally {
        }
        return JsonUtils.toObject(responseContent.toString(),
                VideoUploadResult.class);
    }

    public static VideoUploadResult valiedateUploadVideo(String token,
                                                         File video, String deviceId) {
        HttpGet httpGet = null;
        HttpResponse httpResponse = null;
        BufferedReader responseReader = null;
        // HttpURLConnection conn = null;
        StringBuffer responseContent = new StringBuffer();
        try {
            String path = AndroidConfig.REVELATIONS_VIDEO_UPLOAD + "?name="
                    + video.getName() + "_" + deviceId + "&size="
                    + video.length() + "&token=" + token;
            // URL url = new URL(path);
            httpGet = new HttpGet(path);
            httpResponse = new DefaultHttpClient().execute(httpGet);
            // conn = (HttpURLConnection) url.openConnection();
            // conn.setConnectTimeout(5 * 1000);
            // conn.setRequestMethod("GET");
            // InputStream inStream = conn.getInputStream();
            // responseReader = new BufferedReader(new InputStreamReader(
            // conn.getInputStream()));
            // while (responseReader.ready()) {
            // responseContent.append(responseReader.readLine());
            // }
            responseContent.append(EntityUtils.toString(httpResponse
                    .getEntity()));
        } catch (Exception e) {
            DebugLog.e(e.getLocalizedMessage());
            VideoUploadResult result = new VideoUploadResult();
            result.setSuccess(false);
            return result;
        } finally {
        }
        return JsonUtils.toObject(responseContent.toString(),
                VideoUploadResult.class);
    }

    public static VideoUploadResult postVideo(File video, String token,
                                              long from, long to) {
        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        StringBuffer responseContent = null;
        BufferedReader responseReader = null;
        FileChannel channel = null;
        try {
            String uriAPI = "http://i.kankanews.com:8080/upload.do?token="
                    + token + "&name=" + video.getName();
            String BOUNDARY = UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";

            URL uri = new URL(uriAPI);

            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(5000 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post方式
            conn.setRequestProperty("connection", "keep-alive");

            channel = new FileInputStream(video).getChannel();
            long contentLength = to - from;

            conn.setRequestProperty("Content-Length", contentLength + "");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);
            conn.setRequestProperty("content-range", "bytes " + from + "-" + to
                    + "/" + video.length());

            outStream = new DataOutputStream(conn.getOutputStream());
            channel.transferTo(from, to - from, Channels.newChannel(outStream));

            responseReader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            responseContent = new StringBuffer();
            // while (responseReader.ready()) {
            // responseContent.append(responseReader.readLine());
            // }
            String str;
            while ((str = responseReader.readLine()) != null) {
                responseContent.append(str);
            }
        } catch (Exception e) {
            DebugLog.e(e.getLocalizedMessage());
            VideoUploadResult result = new VideoUploadResult();
            result.setSuccess(false);
            return result;
        } finally {
            try {
                outStream.close();
                channel.close();
                conn.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return JsonUtils.toObject(responseContent.toString(),
                VideoUploadResult.class);
    }

    public static Map<String, String> sendImage(String fileUrl) {
        Map<String, String> result = new HashMap<String, String>();
        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        ByteArrayInputStream tarFile = null;
        BufferedReader responseReader = null;
        try {
            File srcFile = new File(fileUrl);
            String uriAPI = AndroidConfig.REVELATIONS_IMAGE_POST;
            String BOUNDARY = java.util.UUID.randomUUID().toString();
            String PREFIX = "--", LINEND = "\r\n";
            String MULTIPART_FROM_DATA = "multipart/form-data";
            String CHARSET = "UTF-8";

            URL uri = new URL(uriAPI);

            StringBuilder sb1 = new StringBuilder();
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);
            sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + "test.jpg" + "\"" + LINEND);
            sb1.append("Content-Type: multipart/form-data; charset=" + CHARSET
                    + LINEND);
            sb1.append(LINEND);

            StringBuilder sb2 = new StringBuilder();
            sb2.append(LINEND);
            sb2.append(PREFIX);
            sb2.append(BOUNDARY);
            sb2.append(PREFIX);

            conn = (HttpURLConnection) uri.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // Post方式
            conn.setRequestProperty("connection", "keep-alive");

            ByteArrayOutputStream fileOut = ImgUtils.getSmallBitmap(fileUrl);

            long fileLength = fileOut.toByteArray().length;

            Log.e("UPLOAD_FILE_LENGTH", fileLength + "");
            tarFile = new ByteArrayInputStream(fileOut.toByteArray());

            long contentLength = fileLength + sb1.toString().getBytes().length
                    + sb2.toString().getBytes().length;

            conn.setRequestProperty("Content-Length", contentLength + "");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);

            outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb1.toString().getBytes());

            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = tarFile.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.write(sb2.toString().getBytes());

            responseReader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuffer responseContent = new StringBuffer();
            String str;
            while ((str = responseReader.readLine()) != null) {
                responseContent.append(str);
            }
            result.put("ResponseCode", conn.getResponseCode() + "");
            result.put("ResponseContent", responseContent.toString());

        } catch (Exception e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
            result.put("ResponseCode", "ERROR");
            result.put("ResponseContent", "ERROR");
        } finally {
            try {
                tarFile.close();
                outStream.close();
                conn.getInputStream().close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
            }
        }
        return result;
    }

    public static Map<String, String> sendRevelationsContent(String tel,
                                                             String content, String imgUrls, String videoUrl, String aId) {
        Map<String, String> result = new HashMap<String, String>();
        HttpPost httpPost = new HttpPost(AndroidConfig.REVELATIONS_CONTENT_POST);
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phonenum", tel));
        params.add(new BasicNameValuePair("newstext", content));
        if (videoUrl != null)
            params.add(new BasicNameValuePair("filename", videoUrl));
        else
            params.add(new BasicNameValuePair("imagegroup", imgUrls));
        if (aId != null && !aId.trim().equals(""))
            params.add(new BasicNameValuePair("aid", aId));
        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);

            result.put("ResponseCode", httpResponse.getStatusLine()
                    .getStatusCode() + "");
            result.put("ResponseContent",
                    EntityUtils.toString(httpResponse.getEntity()));
            return result;
        } catch (ClientProtocolException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        }
        result.put("ResponseCode", "ERROR");
        result.put("ResponseContent", "ERROR");
        return result;
    }

    public static Map<String, String> sendRevelationsValidateMessage(
            Context context, String tel) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = telephonyManager.getNetworkOperatorName().trim()
                .equals("") ? "null" : telephonyManager
                .getNetworkOperatorName();
        ValidateInfo info = new ValidateInfo();
        info.setDeviceName(android.os.Build.MODEL);
        info.setDeviceVersion(android.os.Build.VERSION.RELEASE);
        info.setServiceProvider(operatorName);
        info.setTelephone(tel);
        String data = JsonUtils.toString(info);
        RsaUtils s = new RsaUtils();
        try {
            data = new String(Base64Utils.encode(s.encrypt(data.getBytes())));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Map<String, String> result = new HashMap<String, String>();
        HttpPost httpPost = new HttpPost(
                AndroidConfig.REVELATIONS_VALIDATE_MESSAGE);
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", data));
        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);

            result.put("ResponseCode", httpResponse.getStatusLine()
                    .getStatusCode() + "");
            result.put("ResponseContent",
                    EntityUtils.toString(httpResponse.getEntity()));
            return result;
        } catch (ClientProtocolException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        }
        result.put("ResponseCode", "ERROR");
        result.put("ResponseContent", "ERROR");
        return result;
    }

    public static Map<String, String> validateRevelationsValidateMessage(
            String telephone, String validateCode) {
        Map<String, String> result = new HashMap<String, String>();
        HttpPost httpPost = new HttpPost(AndroidConfig.REVELATIONS_VALIDATE);
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("telephone", telephone));
        params.add(new BasicNameValuePair("validateCode", validateCode));
        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);

            result.put("ResponseCode", httpResponse.getStatusLine()
                    .getStatusCode() + "");
            result.put("ResponseContent",
                    EntityUtils.toString(httpResponse.getEntity()));
            return result;
        } catch (ClientProtocolException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e("IMG_UTILS", e.getLocalizedMessage(), e);
        }
        result.put("ResponseCode", "ERROR");
        result.put("ResponseContent", "ERROR");
        return result;
    }
}
