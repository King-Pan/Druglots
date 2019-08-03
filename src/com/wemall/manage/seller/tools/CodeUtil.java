package com.wemall.manage.seller.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeUtil {
	//常量池
	private static ArrayList<Object> pools = charPools();

	public static String generateCode(String head) {
		String date = DateUtil.dateToString(new Date(), DateUtil.patternB);
			 return head+date+randomCode();
	}
	
	public static void main(String[] args) {
		
			System.out.println(generateCode("KW",null));
			
		
	}
	
	/**
	 * 生成编码
	 * @param head
	 * @param maxCode
	 * @return
	 */
	public static String generateCode(String head,String maxCode) {
		String date = DateUtil.dateToString(new Date(), DateUtil.patternB);
		if(maxCode!=null) {
			Matcher matcher = Pattern.compile("[0-9]").matcher(maxCode);
			int index = 0;
			if(matcher.find()) {
				index = matcher.start();
			} 
			int headDateLength = index+date.length();
			Integer count = Integer.parseInt(maxCode.substring(headDateLength+1));//获取最大编码的最后五位数字
			String oldDate = maxCode.substring(index, headDateLength);//获取最大编码的日期
			if(date.equals(oldDate)) {
				count++;
				return head+date+String.format("%05d", count);
			}
		}
		return head+date+"00001";
	}


	/**
	 * 生成yyyyMMdd时间格式的字符串
	 * 
	 * @return
	 */
	public static String generateDate() {
		return DateUtil.dateToString(new Date(), DateUtil.patternB);
	}

	// 随机生成5位数
	public static String randomCode() {
		
		String randCode = "";
		for(int i=0;i<5;i++) {
			int rand = (int) (Math.random()*36);
			randCode += pools.get(rand);
		}
		return randCode;
	}
	
	private static ArrayList<Object> charPools(){
		ArrayList<Object> a = new ArrayList<>();
		for(int i=65;i<91;i++) {
			a.add((char)i);
		}
		for(int i=0;i<10;i++) {
			a.add(i);
		}
		return a;
	}
}
