package urils.ecaray.com.ecarutils.Util3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import urils.ecaray.com.ecarutils.R;

@SuppressWarnings("WrongConstant")
public class Util {

    private static final String TAG = "SDK_Sample.Util";

    private static Dialog mProgressDialog;
    private static Toast mToast;

    /*
     * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     *
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /*
     * 16进制数字字符集
     */
    private static String hexString = "0123456789ABCDEF";

    /*
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String toHexString(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    // 转换十六进制编码为字符串
    public static String hexToString(String s) {
        if ("0x".equals(s.substring(0, 2))) {
            s = s.substring(2);
        }
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] getHtmlByteArray(final String url) {
        URL htmlUrl = null;
        InputStream inStream = null;
        try {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = inputStreamToByte(inStream);

        return data;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "readFromFile: file not found");
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }


        if (offset < 0) {
            Log.e(TAG, "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0) {
            Log.e(TAG, "readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length()) {
            Log.e(TAG, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len];
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }

    public static int computeSampleSize(BitmapFactory.Options options,

                                        int minSideLength, int maxNumOfPixels) {

        int initialSize = computeInitialSampleSize(options, minSideLength,

                maxNumOfPixels);

        int roundedSize;

        if (initialSize <= 8) {

            roundedSize = 1;

            while (roundedSize < initialSize) {

                roundedSize <<= 1;

            }

        } else {

            roundedSize = (initialSize + 7) / 8 * 8;

        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,

                                                int minSideLength, int maxNumOfPixels) {

        double w = options.outWidth;

        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :

                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

        int upperBound = (minSideLength == -1) ? 128 :

                (int) Math.min(Math.floor(w / minSideLength),

                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {

            // return the larger one when there is no overlapping zone.

            return lowerBound;

        }

        if ((maxNumOfPixels == -1) &&

                (minSideLength == -1)) {

            return 1;

        } else if (minSideLength == -1) {

            return lowerBound;

        } else {

            return upperBound;

        }
    }

    /**
     * 以最省内存的方式读取图片
     */
    public static Bitmap readBitmap(final String path) {
        try {
            FileInputStream stream = new FileInputStream(new File(path
                    + "test.jpg"));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 8;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height,
                                          final int width, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0
                && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
                    : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight
                    + ", orig=" + options.outWidth + "x" + options.outHeight
                    + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG,
                    "bitmap decoded size=" + bm.getWidth() + "x"
                            + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
                    newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm,
                        (bm.getWidth() - width) >> 1,
                        (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG,
                        "bitmap croped size=" + bm.getWidth() + "x"
                                + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static final void showResultDialog(Context context, String msg,
                                              String title) {
        if (msg == null)
            return;
        String rmsg = msg.replace(",", "\n");
        new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg)
                .setNegativeButton("知道了", null).create().show();
    }

    public static final void showProgressDialog(Context context, String title,
                                                String message) {
        dismissDialog();
        if (TextUtils.isEmpty(title)) {
            title = "请稍候";
        }
        if (TextUtils.isEmpty(message)) {
            message = "正在加载...";
        }
        mProgressDialog = ProgressDialog.show(context, title, message);
    }

    public static final void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     * @param logLevel 填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public static final void toastMessage(final Activity activity,
                                          final String message, String logLevel) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    /**
     * 打印消息并且用Toast显示消息
     *
     * @param activity
     * @param message
     * @param logLevel 填d, w, e分别代表debug, warn, error; 默认是debug
     */
    public static final void toastMessage(final Activity activity,
                                          final String message) {
        toastMessage(activity, message, null);
    }

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) {
        Log.v(TAG, "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Log.v(TAG, "image download finished." + imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "getbitmap bmp fail---");
            return null;
        }
        return bitmap;
    }

    public static void clearDir(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            for (File item : dir.listFiles()) {
                item.delete();
            }
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static boolean hasWechat(Context context) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> list = packageManager
                .getInstalledPackages(PackageManager.GET_PERMISSIONS);
        StringBuilder stringBuilder = new StringBuilder();

        for (PackageInfo packageInfo : list) {
            if (packageInfo.packageName.equals("com.tencent.mm")) {
                return true;

            }
        }

        return false;
    }

    /**
     * @throws Exception
     * @功能：设置控件的渐变动画
     * @param：
     * @return：
     */
    public static void setAlphaAnim(View view) {
        AlphaAnimation aAnim = new AlphaAnimation(0.0f, 1.0f);
        aAnim.setDuration(200);
        view.startAnimation(aAnim);

    }

    /**
     * @throws Exception
     * @功能：判断程序是否在前台
     * @param：
     * @return：
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean hasBrowser(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://"));

        List<ResolveInfo> list = pm.queryIntentActivities(intent,
                PackageManager.GET_INTENT_FILTERS);
        final int size = (list == null) ? 0 : list.size();
        return size > 0;
    }

    /**
     * @throws Exception
     * @功能：跳转到另外的Activity
     * @param：mClass:目标activity类的class
     * @return：
     */
    public static void openActivity(Context context, Class mClass) {
        Intent intent = new Intent();
        intent.setClass(context, mClass);
        context.startActivity(intent);

    }

    public static void openActivityForResult(Context context, Class mClass,
                                             int requestCode) {
        Intent intent = new Intent();
        intent.setClass(context, mClass);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void openActivityWithBundle(Context context, Class mClass,
                                              Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, mClass);
        context.startActivity(intent);
    }

    @SuppressLint("NewApi")
    public static void openActivityWithBundleForResult(Activity context,
                                                       Class mClass, int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, mClass);
        context.startActivityForResult(intent, requestCode, bundle);
    }

    /**
     * @throws Exception
     * @功能：获取系统版本
     * @param：
     * @return：
     */
    public static int getAndroidOSVersion() {
        int osVersion;
        try {
            osVersion = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            osVersion = 0;
        }

        return osVersion;
    }



    //设置本地文件webview参数
    public static WebSettings setUrlLocal(WebView webView, String url) {
        WebSettings bs = webView.getSettings();
        bs.setBuiltInZoomControls(false);
        bs.setJavaScriptEnabled(true);
        bs.setSupportZoom(false);// 设置自动缩放
        bs.setUseWideViewPort(false);
        bs.setDisplayZoomControls(false);
        bs.setLoadWithOverviewMode(true);
        bs.setJavaScriptCanOpenWindowsAutomatically(true);
        bs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        bs.setLoadWithOverviewMode(true);
        bs.setDefaultTextEncodingName("UTF-8");

        webView.loadUrl(url);
        webView.setHorizontalScrollBarEnabled(false);// 水平不显示
        webView.setVerticalScrollBarEnabled(false); // 垂直不显示
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 18) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        }
        return bs;
    }

    //设置网络webview参数
    public static WebSettings setUrlWithServer(WebView webView, String url) {
        WebSettings bs = webView.getSettings();
        bs.setBuiltInZoomControls(true);
        bs.setJavaScriptEnabled(true);
        bs.setSupportZoom(true);// 设置自动缩放
        bs.setUseWideViewPort(true);
        bs.setDisplayZoomControls(false);
        bs.setLoadWithOverviewMode(true);
        bs.setJavaScriptCanOpenWindowsAutomatically(true);
        bs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        bs.setLoadWithOverviewMode(true);
        bs.setDefaultTextEncodingName("UTF-8");
        bs.setCacheMode(WebSettings.LOAD_NO_CACHE);


        webView.loadUrl(url);
        webView.setHorizontalScrollBarEnabled(false);// 水平不显示
        webView.setVerticalScrollBarEnabled(false); // 垂直不显示
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 18) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        }
        return bs;
    }

    //获取内存信息
    public static void getMemInfo(Context context){
        long MEM_UNUSED;
        // 得到ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // 取得剩余的内存空间

        MEM_UNUSED = mi.availMem / 1024;
    }
}