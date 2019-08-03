package com.wemall.core.tools;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtil {
	
	/**
	 * 将json转化为实体POJO
	 * 
	 * @param jsonStr
	 * @param obj
	 * @return
	 */
	public static <T> T JSONToObj(String jsonStr, Class<T> obj) {
		T t = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			jsonStr = new String(jsonStr.getBytes("iso-8859-1"), "UTF-8");
			t = objectMapper.readValue(jsonStr, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static <T> T JSONToObj4Inside(String jsonStr, Class<T> obj) {
		T t = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			t = objectMapper.readValue(jsonStr, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 将实体POJO转化为JSON
	 * 
	 * @param obj
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static <T> JSONObject objectToJson(T obj) throws JSONException,
			IOException {
		ObjectMapper mapper = new ObjectMapper();
		// Convert object to JSON string
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			throw e;
		}
		return JSONObject.fromObject(jsonStr);
	}

	/**
	 * 将对象转换为传入类型的List
	 * 
	 * @param object
	 * @param objectClass
	 * @return
	 */
	public static <T> List<T> toList(Object object, Class<T> objectClass) {
		JSONArray jsonArray = JSONArray.fromObject(object);
		return (List<T>) JSONArray.toCollection(jsonArray, objectClass);
	}

	public static Map<String, Object> convertBean(Object bean)
			throws IntrospectionException, IllegalAccessException,
			InvocationTargetException {
		Class type = bean.getClass();
		Map<String, Object> returnMap = new HashMap<String, Object>();
		BeanInfo beanInfo = Introspector.getBeanInfo(type);

		PropertyDescriptor[] propertyDescriptors = beanInfo
				.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();
			if (!propertyName.equals("class")) {
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					returnMap.put(propertyName, result);
				} else {
					returnMap.put(propertyName, "");
				}
			}
		}
		return returnMap;
	}
}
