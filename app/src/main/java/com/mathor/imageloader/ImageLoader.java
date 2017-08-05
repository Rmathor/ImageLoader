package com.mathor.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

/**
 * Author: mathor
 * Date : on 2017/7/14 7:23
 * 图片加载工具类
 */

public class ImageLoader {

    /**
     * 用于缓存图片
     */
    private static LruCache<String, Bitmap> mMemoryCache;

    /**
     * imageLoader实例
     */
    private static ImageLoader mImageLoader;

    private ImageLoader() {

        //计算可用于缓存的内存大小
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        //取内存的1/8用于图片缓存
        int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //返回图片所占用的内存大小
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 获取ImageLoader实例
     *
     * @return
     */
    public static ImageLoader getInstance() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader();
        }
        return mImageLoader;
    }

    /**
     * 将一张图片放到缓存中
     *
     * @param key    存放图片的路径
     * @param bitmap 缓存的图片
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中获取图片
     *
     * @param key
     * @return
     */
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 计算图片的缩放比率
     *
     * @param options
     * @param reqWidth
     * @return
     */
    public static int calculateSampleSize(BitmapFactory.Options options, int reqWidth) {
        //源图片的宽
        int width = options.outWidth;
        int inSampleSize = 1;
        if (width > reqWidth) {
            //计算源图片的宽与请求图片的宽的比率
            inSampleSize = Math.round(width / reqWidth);
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth) {
        //第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateSampleSize(options, reqWidth);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }
}
