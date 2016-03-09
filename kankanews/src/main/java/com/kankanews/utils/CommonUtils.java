package com.kankanews.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.nostra13.universalimageloader.utils.L;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import static android.os.Environment.MEDIA_MOUNTED;

public class CommonUtils {

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            boolean available = info.isAvailable();
            if (!available) {
                ToastUtils.ErrorToastNoNet(context);
            }
            return available;
        }
        ToastUtils.ErrorToastNoNet(context);
        return false;
    }

    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailableNoToast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            boolean available = info.isAvailable();
            return available;
        }
        return false;
    }

    /**
     * 检查是否是WIFI
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }
        return false;
    }

    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    public static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * 检查SD卡是否存在
     */
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     *
     * @param str
     * @return
     */
    public static String stringFilter(String str) {
        str = str.replaceAll("【", "[ ").replaceAll("】", "] ")
                .replaceAll("！", "! ").replaceAll("？", "? ")
                .replaceAll("，", ", ").replaceAll("。", ". ")
                .replaceAll(":", ": ");
        return str;
    }

    // 开始下载更新
    public static void startUpdateAPK(final Context context, final String url,
                                      final ProgressDialog pBar, final Handler handler) {
        pBar.show();
        final File file = getAPKPath(context);
        if (file.exists()) {
            file.delete();
        }

        new Thread() {
            @Override
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            if (length > 0) {
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down(context, handler, pBar);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void down(final Context context, Handler handler,
                             final ProgressDialog pBar) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                pBar.cancel();
                update(context);
            }
        });
    }

    private static void update(Context context) {

        String apkPath = getAPKPath(context).getAbsolutePath();
        root(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static void root(String path) {
        try {
            Runtime.getRuntime().exec("chmod 777 " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getAPKPath(Context context) {

        File externalStorageDirectory = null;

        if (checkSdCard()) {
            externalStorageDirectory = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "transportApk");

        } else {
            externalStorageDirectory = new File(context.getCacheDir(),
                    "transportApk");
        }
        if (externalStorageDirectory.exists()) {
            externalStorageDirectory.mkdirs();

        }
        return new File(externalStorageDirectory, "update.apk");
    }

    public static File getImagePath(Context context, String imaName) {

        File externalStorageDirectory = null;

        externalStorageDirectory = new File(
                Environment.getExternalStorageDirectory(),
                "/DCIM/Camera/transport");
        if (!externalStorageDirectory.exists()) {
            externalStorageDirectory.mkdirs();
        }

        return new File(externalStorageDirectory, imaName);
    }

    public static boolean versionCompare(Context context, String version) {

        String versionName = getVersionName(context);

        if (versionName.equals(version)) {// 一样不需要更新
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /*
     * MD5加密
     */
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String generate(String imageUri) {
        byte[] md5 = getMD5(CommonUtils.UrlToFileName(imageUri).getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX);
    }

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    private static byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            L.e(e);
        }
        return hash;
    }

    /**
     * 获取系统可用内存
     */
    public static long getSystemMemoryInfo(Context context) {
        ActivityManager myActivityManager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        // 然后获得MemoryInfo类型对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // 然后，使用getMemoryInfo(memoryInfo)方法获得系统可用内存，此方法将内存大小保存在memoryInfo对象上
        myActivityManager.getMemoryInfo(memoryInfo);
        // 然后，memoryInfo对象上的availmem值即为所求
        return memoryInfo.availMem;
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context
                .checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取图片缓存地址
     */
    public static File getImageCachePath(Context context) {
        return getOwnCacheDirectory(context, "kankanxinwen/cache/image");

    }

    /**
     * 获取拍照图片缓存
     */
    public static File getCameraImageCachePath(Context context) {
        return getOwnCacheDirectory(context, "kankanxinwen/camera");
    }

    /**
     * 获取视频缓存地址
     */
    public static File getVideoCachePath(Context context) {
        return getOwnCacheDirectory(context, "kankanxinwen/Cache/video");
    }

    /**
     * Returns specified application cache directory. Cache directory will be
     * created on SD card by defined path if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on
     * device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir",
     *                 "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(),
                    cacheDir);
        }
        if (appCacheDir == null
                || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 将URL中文件名分析出来 为防止冲突 取倒数第二个斜杠
     *
     * @param url
     * @return
     */
    public static String UrlToFileName(String url) {
        if (url.length() > 0) {
            String[] strs = url.split("/");
            return strs[strs.length - 2] + strs[strs.length - 1];
        } else {
            return "";
        }
    }

    /**
     * 将URL中各式取出
     *
     * @param url
     * @return
     */
    public static String UrlToFileFormat(String url) {
        if (url.length() > 0) {
            int lastIndexOf = url.lastIndexOf('.');
            return url.substring(lastIndexOf + 1, url.length());
        } else {
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "no_version";
        }
    }

    /**
     * 将数字转化为固定格式
     *
     * @param str
     * @return
     */
    public static String NumFormat(String str) {
        NumberFormat nf = new DecimalFormat("###,###,###");
        String str_result = nf.format(Integer.valueOf(str));
        return str_result;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful. If a
     * deletion fails, the method stops attempting to delete and returns
     * "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static Bitmap addBitmapToCache(String path, Bitmap bitmap,
                                          Map<String, SoftReference<Bitmap>> imageCache) {
        // 强引用的Bitmap对象
        // 软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
        // 添加该对象到Map中使其缓存
        imageCache.put(path, softBitmap);
        return softBitmap.get();
    }

    public static Bitmap getBitmapByPath(String path,
                                         Map<String, SoftReference<Bitmap>> imageCache) {
        // 从缓存中取软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = imageCache.get(path);
        // 判断是否存在软引用
        if (softBitmap == null) {
            return null;
        }
        // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
        Bitmap bitmap = softBitmap.get();
        return bitmap;
    }

    public static String doWebpUrl(String imgUrl) {
        String urlToFileFormat = UrlToFileFormat(imgUrl);
        if (!"gif".equalsIgnoreCase(urlToFileFormat)) {
            int lastIndexOf = imgUrl.lastIndexOf('.');
            if (lastIndexOf == -1)
                return imgUrl;
            imgUrl = imgUrl.substring(0, lastIndexOf);
            imgUrl += ".webp";
        }
        return imgUrl;
    }

    public static String getDeviceID(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }
}
