package dyb.data.crawl.tool;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TimerTool {
    private static Logger logger= LoggerFactory.getLogger(TimerTool.class);
    private static ThreadLocal<Pair<Long,Long>> threadLocal=new ThreadLocal();

    /**
     * 初始化定时器
     * @param time
     */
    public static void initTimer(long time){
        long start=System.currentTimeMillis();
        threadLocal.set(Pair.of(time,start));
    }

    /**
     * 判断时间是否到
     * @return
     */
    public static boolean timerUp(){
//        logger.info("----------"+Thread.currentThread().getName());
        Pair<Long, Long> timePair = threadLocal.get();
        if(timePair==null)logger.error("threadLocal获取为空");
        long now=System.currentTimeMillis();
        if(timePair.getLeft()+timePair.getRight()<now){
            logger.info("计时器时间到");
            return true;
        }else{
            return false;
        }
    }

    /**
     * 重置时间
     */
    public static void resetTimer(){
//        logger.info("----------"+Thread.currentThread().getName());
        Pair<Long, Long> timePair = threadLocal.get();
        if(timePair==null)logger.error("threadLocal获取为空");
        long now=System.currentTimeMillis();        ;
        threadLocal.set(Pair.of(timePair.getLeft(),now));
    }

    public static void main(String[]args){
        Pair<Long,Long> longLongPair=Pair.of(12L,12L);
        List<Integer> nums=new ArrayList<>();
        for(int i=0;i<10000;i++){
            nums.add(i);
        }
        TimerTool.initTimer(100);
        for(int i:nums){
            TimerTool.timerUp();
            if(i%100==0){
                TimerTool.resetTimer();
            }
        }
        threadLocal.set(longLongPair);
        threadLocal.get();
        longLongPair=threadLocal.get();
        longLongPair=threadLocal.get();
        longLongPair=threadLocal.get();

        longLongPair=threadLocal.get();
        longLongPair.getRight();
    }
}
