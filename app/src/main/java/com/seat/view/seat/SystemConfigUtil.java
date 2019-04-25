package com.seat.view.seat;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SystemConfigUtil {

	public static final boolean sRelease = false;
	
	/**
	 * 得到当前的手机网络类型
	 * 
	 * @param context
	 * @return
	 */ 
	public static String getCurrentNetType(Context context) { 
	    String type = ""; 
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo info = cm.getActiveNetworkInfo(); 
	    if (info == null) { 
	        type = "null"; 
	    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) { 
	        type = "wifi"; 
	    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { 
	        int subType = info.getSubtype(); 
	        if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS 
	                || subType == TelephonyManager.NETWORK_TYPE_EDGE) { 
	            type = "2g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) { 
	            type = "3g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准 
	            type = "4g"; 
	        } 
	    } 
	    return type; 
	}
	
	
	//界面的高度与宽度
	public static int screen_w=480;
	public static int screen_h=800;
	public static int statusBarH=0;
	
	
	//界面的比例
	public static float scaleX=1f;
	public static float scaleY=1f;
	
	
	//初始化参数
	public static void initParamas(Context context)
	{
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		screen_w = display.getWidth();//宽度
		screen_h = display.getHeight() ;//高度
		statusBarH = getStatusHeight(context);
		scaleX = (screen_w/480f);
		scaleY = (screen_h/800f);
		
	}
	
	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
	 
	    int statusHeight = -1;
	    try {
	        Class clazz = Class.forName("com.android.internal.R$dimen");
	        Object object = clazz.newInstance();
	        int height = Integer.parseInt(clazz.getField("status_bar_height")
	                .get(object).toString());
	        statusHeight = context.getResources().getDimensionPixelSize(height);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return statusHeight;
	}
	
	public static float getScale()
	{
		
		if(scaleX<scaleY)
			return scaleX;
		else
			return scaleY;
	}
	
	/**
	 * 获取手机ip地址
	 * 
	 * @return
	 */ 
	public static String getPhoneIp() { 
	    try { 
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
	            NetworkInterface intf = en.nextElement(); 
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) { 
	                InetAddress inetAddress = enumIpAddr.nextElement(); 
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) { 
	                    // if (!inetAddress.isLoopbackAddress() && inetAddress 
	                    // instanceof Inet6Address) { 
	                    return inetAddress.getHostAddress().toString(); 
	                } 
	            } 
	        } 
	    } catch (Exception e) { 
	    } 
	    return ""; 
	}
	
	/*********************
	*获取设备IMEI
	*
	****************/
	
	public static String getIMEI(Context context) {

		String imei = ((TelephonyManager)context.getSystemService(

				   Context.TELEPHONY_SERVICE)).getDeviceId();
		
	    return imei;

	}
	
	/*************
	 * 获取App版本号
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context)
	{
		 String version="";
		 // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			version =  packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return version;
	}
	
	
	public static String WEIXIN_APP_ID="";
	public static String WEIXIN_SECRET="";
	/*********************
	 * 注册微信信息
	 * @param appId
	 * @param appSecret
	 */
	public static void  registerWEIXIN(String appId,String appSecret)
	{
		WEIXIN_APP_ID = appId;
		WEIXIN_SECRET = appSecret;
	}
	
	public static String QQ_APP_ID="";
	public static String QQ_SECRET="";
	/**********
	 * 注册QQ
	 * @param appId
	 * @param appSecret
	 */
	public static void registerQQ(String appId,String appSecret)
	{
		QQ_APP_ID = appId;
		QQ_SECRET = appSecret;
	}
	
	public static String SINA_APP_ID="";
	public static String SINA_APP_SECRET="";
	/**************
	 * 注册新浪信息
	 * @param appId
	 * @param appSecret
	 */
	public static void registerSina(String appId,String appSecret)
	{
		SINA_APP_ID = appId;
		SINA_APP_SECRET = appSecret;
	}
	
	/******************'
	 * 获取当前网络状态
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
			.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) { 
				return mNetworkInfo.getType(); 
			} 
		} 
		return -1; 
	} 
	
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    } 
	
    
    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */ 
    public static int px2sp(Context context, float pxValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (pxValue / fontScale + 0.5f); 
    } 
   
    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */ 
    public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    } 
    
    /**
     * Recursively sets a {@link Typeface} to all
     * {@link TextView}s in a {@link ViewGroup}.
     */
    public static final void setAppFont(ViewGroup mContainer, Typeface mFont, boolean reflect)
    {
        if (mContainer == null || mFont == null) return;
     
        final int mCount = mContainer.getChildCount();
     
        // Loop through all of the children.
        for (int i = 0; i < mCount; ++i)
        {
            final View mChild = mContainer.getChildAt(i);
            if (mChild instanceof TextView)
            {
                // Set the font if it is a TextView.
                ((TextView) mChild).setTypeface(mFont);
            }
            else if (mChild instanceof ViewGroup)
            {
                // Recursively attempt another ViewGroup.
                setAppFont((ViewGroup) mChild, mFont,true);
            }
            else if (reflect)
            {
                try {
                    Method mSetTypeface = mChild.getClass().getMethod("setTypeface", Typeface.class);
                    mSetTypeface.invoke(mChild, mFont); 
                } catch (Exception e) { /* Do something... */ }
            }
        }
    }
}
