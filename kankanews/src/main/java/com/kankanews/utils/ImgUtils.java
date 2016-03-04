package com.kankanews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.kankanews.kankanxinwen.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImgUtils {
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static DisplayImageOptions homeImageOptions;
    public static DisplayImageOptions liveImageOptions;
    public static ImageLoaderConfiguration picSetImageOptions;

    static {
        homeImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .showImageOnLoading(R.drawable.img_default_display) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.img_default_display)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.img_default_display) // 设置图片加载/解码过程中错误时候显示的图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                // EXACTLY_STRETCHED
                .decodingOptions(new BitmapFactory.Options())// 设置图片的解码配置
                // .delayBeforeLoading(int delayInMillis)//int
                // delayInMillis为你设置的下载前的延迟时间
                // 设置图片加入缓存前，对bitmap进行设置
                // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                .build();// 构建完成

        liveImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)
                // .showImageOnLoading(R.drawable.livebg2) //设置图片在下载期间显示的图片
                // .showImageForEmptyUri(R.drawable.livebg2)//设置图片Uri为空或是错误的时候显示的图片
                // .showImageOnFail(R.drawable.livebg2) //设置图片加载/解码过程中错误时候显示的图片
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                // EXACTLY_STRETCHED
                .decodingOptions(new BitmapFactory.Options())// 设置图片的解码配置
                // .delayBeforeLoading(int delayInMillis)//int
                // delayInMillis为你设置的下载前的延迟时间
                // 设置图片加入缓存前，对bitmap进行设置
                // .preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
                .build();// 构建完成
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
            return inSampleSize + 1;
        }
        return inSampleSize;
    }

    public static Bitmap decodeImage(String path, int showWidth, int showHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;
        if (showWidth != 0 && showHeight != 0)
            options.inSampleSize = calculateInSampleSize(options, showWidth,
                    showHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }

    public static ByteArrayOutputStream getSmallBitmap(String filePath) {
        Bitmap bm = decodeImage(filePath, 600, 800);
        long fileLength = new File(filePath).length();
        int scale = 60;
        if (fileLength > 2097152)
            scale = 20;
        if (fileLength <= 2097152 && fileLength > 1572864)
            scale = 40;
        if (fileLength <= 1572864 && fileLength > 1048576)
            scale = 60;
        if (fileLength <= 1048576 && fileLength > 524288)
            scale = 70;
        if (fileLength <= 524288 && fileLength > 262144)
            scale = 80;
        if (fileLength <= 262144)
            scale = 60;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG"))
            bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
        if (filePath.endsWith(".jpeg") || filePath.endsWith(".JPEG"))
            bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
        if (filePath.endsWith(".png") || filePath.endsWith(".PNG"))
            bm.compress(Bitmap.CompressFormat.JPEG, scale, baos);
        return baos;
    }

    public static Bitmap getNetImage(String url) {
        InputStream in = null;
        try {
            // 建立网络连接
            URL imageURl = new URL(url);
            URLConnection con = imageURl.openConnection();
            con.connect();
            in = con.getInputStream();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("getNetImage", e.getLocalizedMessage(), e);
            return null;
        }
        Bitmap bit = BitmapFactory.decodeStream(in);
        return bit;
    }

    public static boolean saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
}
