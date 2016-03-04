package com.kankanews.config;

public class AndroidConfig {

    public static final int FRAGMENTREFRESHTIME = 1000 * 60 * 10;// fragment刷新间隔时间

    public static final long _NEWS_CONTENT_SAVE_OK_TIME_ = 24 * 60 * 60 * 1000;// 新闻内容页缓存过期时间

    public static final long _NEWS_LIST_SAVE_OK_TIME_ = 60 * 60 * 1000;// 新闻内容页缓存过期时间

    public static final String SHARENAME = "transportationAndroid";

    // 主页数据
    public static final String MAINDATA = "/info/public_timeline/";

    // 最热数据
    public static final String HOTDATA = "/info/public_hotline/";

    // 新闻详细数据
    public static final String NewContent = "/info/show/";

    // 登录回调
    public static final int Drawer_login_requestCode = 1001;
    public static final int Drawer_login_resultCode = 1002;

    // 评论回调
    public static final int Comment_requestCode = 2001;
    public static final int Comment_resultCode = 2002;

    // 设置回调
    // public static final int Set_requestCode = 3001;
    public static final int Set_resultCode = 3002;

    // 选择图片回调
    public static final int REVELATIONS_FRAGMENT_REQUEST_NO = 4001;
    public static final int REVELATIONS_FRAGMENT_RESULT_OK = 4002;
    public static final int REVELATIONS_FRAGMENT_RESULT_CANCEL = 4003;

    public static final int REVELATIONS_FRAGMENT_PHOTO_REQUEST_NO = 5001;
    public static final int REVELATIONS_VIDEO_REQUEST_NO = 6001;

    // 事件统计-ID
    // 视频播放
    public static final String video_play_event = "Event_play"; // "play"
    // 全屏
    public static final String video_fullscreen_event = "Event_fullscreen"; // "fullscreen"

    // ------------------------------------------------------------------看看新闻------------------------------------------------------------------------
    public static final String KANKAN_HOST = "http://api.app.kankanews.com/kankan";

    // 首页条目数据
    public static final String New_HomeCateData = "/v5/info/item_timeline";
    // 首页数据
    public static final String New_HomeData = "/v5/info/public_timeline/";
    // 主页数据
    public static final String NEWS_HOME_DATA = "/v5/KKAppIndex/version/2";
    // 新闻详细数据(推送版)
    public static final String NewContentPush = "/v5/boardcast/appid/";
    // 主页随机数据
    public static final String NEWS_HOME_DATA_CHANGE = "/v5/KKAppList/command/random/appclassid/";
    // 主页投票随机数据
    public static final String NEWS_HOME_VOTE_DATA_CHANGE = "/v5/KKAppVote/command/random/appclassid/";
    // 提交投票结果
    public static final String NEWS_HOME_VOTE_PUT = "/v5/KKAppVote/command/vote/appclassid/";
    // 列表页列表数据
    public static final String NEWS_LIST_DATA = "/v5/KKAppList/command/normal/appclassid/";
    // 新闻点击量
    public static final String New_NewsClick = "http://m.kankanews.com/web/clickList?list=";
    // 添加新闻点击量
    public static final String New_NewsAddClick = "http://m.kankanews.com/web/clickCollector?";
    // 新闻详情
    public static final String New_NewsContent = "/v5/content/video_detail/";
    // 新闻详情
    public static final String NewsContent = "/v5/artical/detail/";
    // 直播数据
    public static final String New_LivePlay = "/v5/live/timeline/";
    // 直播数据
    public static final String LIVE_LIST_URL = "/v5/liveAPP/stream/live";
    // 直播频道数据
    public static final String LIVE_CHANNEL_URL = "/v5/liveAPP/stream/channel";
    // 栏目列表
    public static final String New_Colums = "http://api.app.kankanews.com/index.php/kankan/v5/channel/timeline";
    // 栏目列表_二级菜单
    public static final String New_Colums_Second_Level = "http://api.app.kankanews.com/index.php/kankan/v5/channel/tvcolumn";
    // 栏目节目列表
    public static final String New_Colums_Info = "/v5/channel/tvinfolist/";

    // 栏目时间回调
    public static final int Colums_Time_requestCode = 10001;
    public static final int Colums_Time_resultCode = 10002;
    // 专题接口
    public static final String New_Subject = "/v5/info/focus_timeline/";

    // 获得热门推荐接口
    public static final String New_Recommend = "http://api.app.kankanews.com/index.php/kankan/v5/content/recommend_timeline";

    // ------------------------------------------------------------------报料------------------------------------------------------------------------
    // 最大图片可选
    public static final int MAX_PIC_SELECTED = 9;
    // 报料列表数据
    public static final String REVELATIONS_HOME_DATA = "/v5/newsbreak/breakinfo/index";
    // 报料活动数据
    public static final String REVELATIONS_ACTIVITY_DATA = "/v5/newsbreak/breakinfo/activity";
    // 报料列表更多
    public static final String REVELATIONS_BREAKNEWS_MORE_DATA = "/v5/newsbreak/breakinfo/more/timestamp/";
    // 报料图片上传接口
    public static final String REVELATIONS_IMAGE_POST = "http://api.app.kankanews.com/kankan/v5/newsbreak/pic/1";
    // 报料视频上传获取token接口
    public static final String REVELATIONS_GET_VIDEO_UPLOAD_TOKEN = "http://i.kankanews.com:8080/getToken.do";
    public static final String REVELATIONS_VIDEO_UPLOAD = "http://i.kankanews.com:8080/upload.do";

    public static final String RESPONSE_CODE_OK = "200";

    // 报料信息上传接口
    public static final String REVELATIONS_CONTENT_POST = "http://api.app.kankanews.com/kankan/v5/newsbreak/message/1";
    // 报料手机号发送验证码接口
    public static final String REVELATIONS_VALIDATE_MESSAGE = "http://sso.kankanews.com/register/sendValidateMessage.do";
    // 报料手机号验证接口
    public static final String REVELATIONS_VALIDATE = "http://sso.kankanews.com/register/validateMessageCode.do";

    public static final long MAX_SEL_IMG_LENGTH = 20971520;

    // 广告获取接口
    public static final String ADVERT_GET = "http://afp.csbew.com/s.htm";

    // 广告线上ID
    public static final String ADVERT_ID = "107167";
    // 广告测试ID
    public static final String ADVERT_ID_DEBUG = "108535";

    public static final int MAX_SEARCH_HIS_NUM = 5;
    // 内容搜索接口
    public static final String SEARCH_GET = "/v5/search";

    public static final String SEARCH_HOT_WORD = "/v5/hot/words";

    // 后台分析接口
    public static final String New_NewsAnalyse = "http://api.kankanews.com/kkstat/kkapp/collect/kankanapp.json";

    public static final String Shared_Icon = "http://static.statickksmg.com/image/2015/07/01/4601965e14483b182a9b7860072f5405.jpg";

}
