package com.wemall.manage.seller.tools;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar; 

public class DateUtil {
	
	/** 
     * @param yyyy-MM-dd 
     * @return 
     */  
    public static final String patternA = "yyyy-MM-dd";  
    /** 
     * @param yyyyMMdd 
     * @return 
     */  
    public static final String patternB = "yyyyMMdd";  
    /** 
     * @param yyyy-MM-dd HH-mm-ss 
     * @return 
     */  
    public static final String patternC = "yyyy-MM-dd HH-mm-ss";  
    /** 
     * @param yyyy:MM:dd HH:mm:ss 
     * @return 
     */  
    public static final String patternD = "yyyy-MM-dd HH:mm:ss";  
    /** 
     * @param yyyy-MM-dd HH:mm:ss 
     * @return 
     */  
    public static final String patternE = "yyyy-MM-dd HH:mm";  
      
    /** 
     * @param yyyyMMddHHmmss 
     * @return 
     */  
    public static final String patternF = "yyyyMMddHHmmss";  
      
    public static final String patternG = "yyyy年MM月dd日";  
      
    /** 
     * @param yyyy-MM 
     * @return 
     */  
    public static final String patternH = "yyyy-MM";  
      
    public static final String patternI = "yyyyMM";  
    

	public Date strToDate(String strDate) {
		
		if(strDate==null){
			return new Date();
		}
		
		strDate = strDate.replaceAll("：", ":");
		
		if(strDate.equals("")){
			return new Date();
		}			
		
		return parseDateTime(strDate, "yyyy-MM-dd HH:mm:ss");
	}
	
	public Date DTStringtoDate(String dtToDate){
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		   ParsePosition pos = new ParsePosition(0);   
		   java.util.Date datetime = formatter.parse(dtToDate, pos); 
		   java.sql.Timestamp ts = null;  
		  
		   if(datetime != null){ 
		   
		    ts = new java.sql.Timestamp(datetime.getTime());
		   }
		  
		   return ts; 
		}
	
	/* 
     * 将时间转换为时间戳
     */    
    public Long dateToStamp(String dateStr) throws ParseException{
    	if(dateStr==null || dateStr.equals("")) return 0L;
    	
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(dateStr);
        long ts = date.getTime();
        return ts;
    }
	
    /* 
     * 将时间戳转换为时间
     */
    public String stampToDate(Long stamp){
    	if(stamp==null || stamp==0L) return "";
    	
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(stamp);
        String res = simpleDateFormat.format(date);
        return res;
    }
    
    public static Date stampToDate1(Long stamp) {
    	if(stamp==null || stamp==0L) return null;
    	Date date = new Date(stamp);
		return date;
	}
    
	private synchronized Date parseDateTime(String strDate, String format){
		
		Date date = null;
		
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			ParsePosition pos = new ParsePosition(0);
			date = df.parse(strDate, pos);
		} catch (Exception e) {
			date = new Date();
			e.printStackTrace();
		}
		
		if(date==null){
			date = new Date();
		}
		
		return date; 
	}
	
	 
	/**
     * 使用用户格式提取字符串日期
     * @param strDate 日期字符串
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Date strParseDate(String strDate) {
        SimpleDateFormat df = new SimpleDateFormat(patternD);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
     * 使用用户格式提取字符串日期
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
     
    
    /** 
     * 格式化日期为yyyy-MM-dd HH:mm:ss
     * @param date 
     * @return 
     */  
    public static String formateDateHour(Date date){  
        return dateToString(date,patternD);  
    }  
      
    /** 
     * 格式化日期为yyyy-MM-dd 
     * @param date 
     * @return 
     */  
    public static String formateDate(Date date){  
        return dateToString(date,patternA);  
    }  
      
    /** 
     * 格式化日期为yyyy-MM 
     * @param date 
     * @return 
     */  
    public static String formateYearAndMonth(Date date){  
        return dateToString(date,patternH);  
    }  
      
    /** 
     * @param 取当天日期 
     * @return 
     */  
    public Date getDate() {  
        return Calendar.getInstance().getTime();  
    }  
    /** 
     * @param 取指定年月日的日期,格式为yyyy-MM-dd,HH-mm-ss 00-00-00 
     * @return 
     */  
    public static Date getDate(int year, int month, int day) {  
        Calendar cal = Calendar.getInstance();  
        cal.set(year, month-1, day,0,0,0);  
        return cal.getTime();  
          
    }  
    /** 
     * @param 取指定年,月,日,小时,分,秒的时间 
     * @return 
     */  
    public static Date getDate(int year,int month,int date,int hour,int mintue,int second)  
    {  
        Calendar cal=Calendar.getInstance();  
        cal.set(Calendar.YEAR,year);  
        cal.set(Calendar.MONTH,month-1);  
        cal.set(Calendar.DATE,date);  
        cal.set(Calendar.HOUR_OF_DAY,hour);  
        cal.set(Calendar.MINUTE,mintue);  
        cal.set(Calendar.SECOND,second);  
        return cal.getTime();  
    }  
  
  
    /** 
     * @param days=n n为-,则取n天前,n为+,则取n天后的日期 
     * @param date 
     * @param days 
     * @return 
     */  
    public static Date getSomeDaysBeforeAfter(Date date, int days){  
        GregorianCalendar gc =new GregorianCalendar();  
        gc.setTime(date);  
        gc.add(5, days);  
        gc.set(gc.get(Calendar.YEAR),gc.get(Calendar.MONTH),gc.get(Calendar.DATE));  
        return gc.getTime();  
    }  
    /** 
     * @param 取指定日期年份 
     * @return 
     */  
    public static int getDateYear(Date date){  
          
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.YEAR);  
    }  
    /** 
     * @param 取指定日期月份 
     * @return 
     */  
    public static int getDateMonth(Date date){  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.MONTH)+1;  
    }  
    /** 
     * @param 取指定日期日份 
     * @return 
     */  
    public static int getDateDay(Date date){  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.DATE);  
    }     
    /** 
     * @param 取指定日期小时 
     * @return 
     */  
    public static int getDateHour(Date date){  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.HOUR_OF_DAY);  
    }  
    /** 
     * @param 取指定日期分钟 
     * @return 
     */  
    public static int getDateMinute(Date date){  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.MINUTE);  
    }  
    /** 
     * @param 取指定日期的第二天的开始时间,小时,分,秒为00:00:00 
     * @return 
     */  
    public static Date getNextDayStartTime(Date date) {  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return getNextDayStart(c.get(Calendar.YEAR),  
                c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));  
    }  
  
  
    /** 
     * @param 取指定年,月,日的下一日的开始时间,小时,分,秒为00:00:00 
     * @param 主要是用来取跨月份的日期 
     * @return 
     */  
    public static Date getNextDayStart(int year, int monthIn, int date) {  
        int month = monthIn - 1;  
        boolean lastDayOfMonth = false;  
        boolean lastDayOfYear = false;  
          
        Calendar time = Calendar.getInstance();  
        time.set(year, month, date, 0, 0, 0);  
        Calendar nextMonthFirstDay = Calendar.getInstance();  
        nextMonthFirstDay.set(year, month + 1, 1, 0, 0, 0);  
          
        if (time.get(Calendar.DAY_OF_YEAR) + 1 == nextMonthFirstDay.get(Calendar.DAY_OF_YEAR))  
            lastDayOfMonth = true;  
          
        if (time.get(Calendar.DAY_OF_YEAR) == time.getMaximum(Calendar.DATE))  
            lastDayOfYear = true;  
          
        time.roll(Calendar.DATE, 1);  
          
        if (lastDayOfMonth)  
            time.roll(Calendar.MONTH, 1);  
          
        if (lastDayOfYear)  
            time.roll(Calendar.YEAR, 1);  
          
          
        return time.getTime();  
    }  
  
    /** 
     * @param 取指定日期的下一日的时间 
     * @return 
     */  
    public static Date nextDate(Date date)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.add(Calendar.DATE,1);  
        return cal.getTime();  
    }  
  
    /** 
     * @param 指定日期的下一日的开始时间,小时,分,秒为00:00:00 
     * @return 
     */  
    public static Date getStartDateNext(Date date)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.add(Calendar.DATE,1);  
        cal.set(Calendar.HOUR_OF_DAY,0);  
        cal.set(Calendar.MINUTE,0);  
        cal.set(Calendar.SECOND,0);  
        return cal.getTime();  
    }  
  
    /** 
     * @param 指定日期的开始时间,小时,分,秒为00:00:00 
     * @return 
     */  
    public static Date getStartDateDay(Date date)  
    {  
        if(date == null)  
            return null;  
        Calendar cal=Calendar.getInstance();  
        cal.setTime(date);  
        cal.set(Calendar.HOUR_OF_DAY,0);  
        cal.set(Calendar.MINUTE,0);  
        cal.set(Calendar.SECOND,0);  
        return cal.getTime();  
    }  
  
    /** 
     * @param 指定日期的结束时间,小时,分,秒为23:59:59 
     * @return 
     */  
    public static Date getEndDateDay(Date date)  
    {  
        if(date == null)  
            return null;  
        Calendar cal=Calendar.getInstance();  
        cal.setTime(date);  
        cal.set(Calendar.HOUR_OF_DAY,23);  
        cal.set(Calendar.MINUTE,59);  
        cal.set(Calendar.SECOND,59);  
        return cal.getTime();  
    }  
      
    /** 
     * @param 将指定日期,以指定pattern格式,输出String值 
     * @return 
     */  
    public static String dateToString(Date date ,String pattern) {  
        if (date == null) {  
            return "";  
        } else {  
            SimpleDateFormat format = new SimpleDateFormat(pattern);  
            return format.format(date);  
        }  
    }  
      
    public static String dateToString1(Date date, String formatIn) {  
        String format=formatIn;  
        if (date == null) {  
          return "";  
        }  
        if (format == null) {  
          format = patternD;  
        }  
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);  
        return sdf.format(date);  
      }  
  
  
    /** 
     * @param 将指定年,月,日的日期转为字符型,格式为yyyy-MM-dd 
     * @return 
     */  
    public static String dateToString(int year, int month, int day, String pattern) {  
        return dateToString(getDate(year, month, day), pattern);  
    }  
  
  
    /** 
     * @param 将指定字符型日期转为日期型,,格式为指定的pattern 
     * @return 
     */  
    public static Date stringToDate(String string, String pattern){  
        SimpleDateFormat format =  new SimpleDateFormat(pattern);
        try {  
            return format.parse(string);  
        } catch (ParseException e) {  
            return null;  
        }  
    }  
  
    /** 
     * @param 将指定字符型日期转为日期型,指定格式为yyyy-MM-dd 
     * @return 
     */  
    public static Date stringToDate(String string){  
        return stringToDate(string, patternA);  
    }  
  
    /** 
     * 获得两个日期之间间隔的天数 
     * @param startDate 起始年月日 
     * @param endDate 结束年月日 
     * @return int 
     */  
    public static int getDays(Date startDate, Date endDate) {  
        int elapsed = 0;  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(startDate);  
        Date d1 = cal.getTime();  
          
        cal.setTime(endDate);  
        Date d2 = cal.getTime();  
          
        long daterange = d2.getTime() - d1.getTime();  
        long time = 1000*3600*24; //一天的毫秒数  
        elapsed = (int) (daterange/time);  
        return elapsed;  
   }  
    public static Date formatStrDate(String date) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(date);
	}
    /** 
     * @param  根据传入日期，返回此月有多少天 
     * @param date 格式为  201408 
     * @return 
     */  
    public static  int getDayOfMonth(String date){  
        int year = Integer.parseInt(date.substring(0, 3));  
        int month = Integer.parseInt(date.substring(date.length()-1, date.length()));  
          
        Calendar cal = Calendar.getInstance();   
        cal.set(Calendar.YEAR,year);   
        cal.set(Calendar.MONTH, month-1);//Java月份才0开始算  6代表上一个月7月   
        int dateOfMonth = cal.getActualMaximum(Calendar.DATE);  
        return dateOfMonth;  
    }  
      
    /** 
     * @param 取指定日期月份前一月 
     * @return 
     */  
    public static int getLastDateMonth(Date date){  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        return c.get(Calendar.MONTH);  
    }  
    //取日期的当前月第一天  
    public static Date getMonthFirstDay(Date date){  
        return getDate(getDateYear(date), getDateMonth(date), 1);  
    }  
      
    //前月第一天  
    public static Date getLastDateMonthDay(Date date){  
        return getDate(getDateYear(date), getLastDateMonth(date), 1);  
    }
    
    public boolean compare(Date obj,Date cObj,SimpleDateFormat ft){
      if(ft==null){
        ft= new SimpleDateFormat(  
            "yy/MM/dd HH:mm");
      }
      String oStr = ft.format(obj);
      String cStr = ft.format(cObj);  
      int result = oStr.compareTo(cStr);
      //obj >= cObj
      if (result>=0) {  
          return true;
      }
      return false;
    }
    
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Long time=new Long(1509345274);  
        String d = format.format(time);  
        Date date=format.parse(d);  
        System.out.println("Format To String(Date):"+d);  
        System.out.println("Format To Date:"+date);  
	}
}  
	
	

