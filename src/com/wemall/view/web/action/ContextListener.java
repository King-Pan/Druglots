package com.wemall.view.web.action;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class ContextListener implements ServletContextListener{
	
	 public ContextListener() {
	    }
	 
	 private java.util.Timer timer = null;
	 
	 //时间间隔(一天)
	 private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

	
	
	/**
     * 初始化定时器
     * web 程序运行时候自动加载
     */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		 timer = new java.util.Timer(true);
		 System.out.println("定时器已启动");
		 //设置执行时间
        Calendar calendar =Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10); // 时   
        calendar.set(Calendar.MINUTE, 0);//分
        calendar.set(Calendar.SECOND, 0);//秒
        Date date=calendar.getTime();
        
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            date = this.addDay(date, 1);
        }
       
        //每天的date时刻执行task，每隔（24小时）时间重复执行
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
         timer.schedule(new TimerRunAction(arg0.getServletContext()), date,PERIOD_DAY);
	}
	
	
	 /**
     * 当服务器停止时，定时器销毁
     */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		//销毁定时器
		timer.cancel();
	}
	
	// 增加或减少天数
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

}
