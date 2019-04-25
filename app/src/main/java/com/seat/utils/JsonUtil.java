/**    
 * 文件名：JsonUtil.java    
 *    
 * 版本信息：    
 * 日期：2015-4-9    
 * Copyright 足下 Corporation 2015     
 * 版权所有    
 *    
 */
package com.seat.utils;

import com.alibaba.fastjson.JSONObject;


public class JsonUtil {
	
	public static String toJsonString(Object object){
		
		String result = "";
		
		try {
			
			result = JSONObject.toJSONString(object);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	public static <T> T toObject(String json, Class<T> clazz){
		
		T instance_class = null;
		
		try {
			
			instance_class = JSONObject.parseObject(json, clazz);
			
		} catch (Exception e) {
			// TODO: handle exception
			try {
				instance_class = clazz.newInstance();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return instance_class; 
	}
}
