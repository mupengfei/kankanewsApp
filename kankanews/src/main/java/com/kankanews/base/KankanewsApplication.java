package com.kankanews.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.kankanews.config.AndroidConfig;
import com.kankanews.utils.CommonUtils;
import com.kankanews.utils.SharePreferenceUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.umeng.socialize.PlatformConfig;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

public class KankanewsApplication extends Application {

    private static KankanewsApplication mInstance;
    private static SharePreferenceUtils shareUtils;

    private static ImageLoaderConfiguration mImageLoaderConfig;

    private static boolean isStart;

    public TreeMap<String, HttpHandler> mHttpHandlereds = new TreeMap<String, HttpHandler>();

    public LinkedList<BaseActivity> mBaseActivityList = new LinkedList<BaseActivity>();

    private BaseActivity mainActivity;

    private long front;
    private long later;
    private Typeface tf;
    private DbUtils dbUtils;
    private int width;

    public Typeface getTf() {
        return tf;
    }

    public void setTf(Typeface tf) {
        this.tf = tf;
    }

    public static KankanewsApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        mInstance = this;
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = activityManager.getMemoryClass();

        WindowManager manager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        width = display.getWidth();
        int height = display.getHeight();
        AssetManager mgr = getAssets();
        tf = Typeface.createFromAsset(mgr, "nomal.TTF");

        initDB();
        File cacheDir = CommonUtils.getImageCachePath(getApplicationContext());
        initImageLoader(this, cacheDir);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        PlatformConfig.setWeixin("wx3f0cb92ed4db29b6", "4ead2f0961bd6952570453d03562200e");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("726832296","f187c0a4a06156801378d45e20ba1ca6");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("1103880827", "y99xCuBUAIJ0IB8x");
        // QQ和Qzone appid appkey
    }

    public void initDB() {

        dbUtils = DbUtils.create(this, "kankan", 10, new DbUtils.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
//                try {
//                    arg0.dropTable(New_News_Home.class);
//                    arg0.dropTable(New_News_Top.class);
//                    arg0.dropTable(New_News.class);
//                    arg0.dropTable(New_Recommend.class);
//                    arg0.dropTable(New_LivePlay.class);
//                    arg0.dropTable(New_Colums.class);
//                    arg0.dropTable(New_Colums_Second.class);
//                    arg0.dropTable(New_Colums_Info.class);
//                    arg0.dropTable(Content_News.class);
//                    arg0.dropTable(Advert.class);
//                    arg0.dropTable(RevelationsHomeList.class);
//                    arg0.dropTable(New_HomeCate.class);
//                    arg0.dropTable(LiveLiveObj.class);
//                    arg0.dropTable(SerializableObj.class);
//                    arg0.createTableIfNotExist(New_News_Home.class);
//                    arg0.createTableIfNotExist(New_News_Top.class);
//                    arg0.createTableIfNotExist(New_News.class);
//                    arg0.createTableIfNotExist(New_Recommend.class);
//                    arg0.createTableIfNotExist(New_LivePlay.class);
//                    arg0.createTableIfNotExist(New_Colums.class);
//                    arg0.createTableIfNotExist(New_Colums_Second.class);
//                    arg0.createTableIfNotExist(New_Colums_Info.class);
//                    arg0.createTableIfNotExist(Content_News.class);
//                    arg0.createTableIfNotExist(Advert.class);
//                    arg0.createTableIfNotExist(RevelationsHomeList.class);
//                    arg0.createTableIfNotExist(New_HomeCate.class);
//                    arg0.createTableIfNotExist(LiveLiveObj.class);
//                    arg0.createTableIfNotExist(SerializableObj.class);
//                } catch (DbException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        });
        dbUtils.configAllowTransaction(true);
    }

    public void initImageLoader(Context context, File cacheDir) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .diskCache(new MyUnlimitedDiscCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(50 * 1024 * 1024)
                .memoryCache(new WeakMemoryCache())
                // .memoryCacheSize(20*1024*1024)
                // .diskCacheExtraOptions(480, 320, null)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    class MyUnlimitedDiscCache extends BaseDiskCache {

        public MyUnlimitedDiscCache(File cacheDir) {
            this(cacheDir, new MyFileNameGenerator());
        }

        public MyUnlimitedDiscCache(File cacheDir,
                                    FileNameGenerator fileNameGenerator) {
            super(cacheDir);
        }

    }

    class MyFileNameGenerator implements FileNameGenerator {

        @Override
        public String generate(String imageUri) {
            byte[] md5 = getMD5(CommonUtils.UrlToFileName(imageUri).getBytes());
            BigInteger bi = new BigInteger(md5).abs();
            return bi.toString(RADIX);
        }

        private static final String HASH_ALGORITHM = "MD5";
        private static final int RADIX = 10 + 26; // 10 digits + 26 letters

        private byte[] getMD5(byte[] data) {
            byte[] hash = null;
            try {
                MessageDigest digest = MessageDigest
                        .getInstance(HASH_ALGORITHM);
                digest.update(data);
                hash = digest.digest();
            } catch (NoSuchAlgorithmException e) {
                L.e(e);
            }
            return hash;
        }

    }

    public SharePreferenceUtils getSpUtil() {

        if (shareUtils == null) {
            shareUtils = new SharePreferenceUtils(mInstance,
                    AndroidConfig.SHARENAME);
        }

        return shareUtils;
    }

    public void addActivity(BaseActivity activity) {
        mBaseActivityList.add(activity);
    }

    public void removeActivity(BaseActivity activity) {
        if (activity != this.mainActivity)
            mBaseActivityList.remove(activity);
    }

    public void exit() {

        for (BaseActivity activity : mBaseActivityList) {
            if (activity != null) {
                //TODO
//                activity.finishNoRemove();
            }
        }
        isStart = false;

        System.exit(0);
    }

    /*
     * 闂佸尅鎷锋慨锝勭劍婢у秹寮垫繅顪﹖ivity
     */
    public void allFinish() {
        for (Activity activity : mBaseActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


    public DbUtils getDbUtils() {
        return dbUtils;
    }

    int i = 0;

    public void shutDown() {
        i++;
        if (i < 2) {
            Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
            front = System.currentTimeMillis();
            return;
        }
        if (i >= 2) {
            later = System.currentTimeMillis();
            if (later - front > 2000) {
                Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
                front = System.currentTimeMillis();
                i = 1;
            } else {

                // File videoCachePath =
                // CommonUtils.getVideoCachePath(mInstance);
                // File videoCachePath

                exit();
                i = 0;
            }
        }
    }

    private int[] arrayid;
    private int position;
    public int Coupontime;

    public int[] getArrayid() {
        return arrayid;
    }

    public void setArrayid(int[] arrayid) {
        this.arrayid = arrayid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BaseActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(BaseActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void changeMainActivityDayMode() {
        //TODO
//        if (this.mainActivity != null) {
//            if (this.mainActivity.mSpUtils.getIsDayMode())
//                this.mainActivity.chage2Day();
//            else
//                this.mainActivity.chage2Night();
//        }
    }

    public static boolean isStart() {
        return isStart;
    }

    public static void setStart(boolean isStart) {
        KankanewsApplication.isStart = isStart;
    }
}
