package com.zxf.seckill.mq;

import com.zxf.seckill.config.RabbitMQConfig;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.dto.SeckillResult;
import com.zxf.seckill.entity.RabbitMessage;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.enums.SeckillStateEnum;
import com.zxf.seckill.exception.DataRewriteException;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import com.zxf.seckill.exception.UnderStockException;
import com.zxf.seckill.service.SeckillService;
import com.zxf.seckill.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 当监听到队列中有消息时，会进行接收并处理
 */
@Component
@EnableRabbit
public class RabbitMQReceiver {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 将处理的结果放入到redis中，由客户端定时轮询处理结果
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(RabbitMessage rabbitMessage) {
        logger.debug("消费者" + this + "收到MQ消息：" + rabbitMessage);

        //获取id
        long seckillId = rabbitMessage.getSeckillId();
        long userPhone = rabbitMessage.getUserPhone();
        String md5 = rabbitMessage.getMd5();

        //秒杀结果
        SeckillExecution execution = null;
        SeckillResult<SeckillExecution> result;
        try{
            //1.判断秒杀时间
            Seckill seckill = (Seckill)redisUtil.get("seckill_" + seckillId);
            Date nowTime = new Date();
            //秒杀时间超时
            if(nowTime.getTime() > seckill.getEndTime().getTime()) {
                throw new SeckillCloseException("seckill timeout");
            }

            //2.判断是否已有订单记录
            if(redisUtil.getOrder(seckillId, userPhone) != null) {
                throw new RepeatKillException("seckill repeat");
            }

            //3.执行秒杀操作（减库存；加订单）
            execution = seckillService.executeSeckillMQ(seckillId, userPhone, md5);

        } catch(SeckillCloseException e1) {
            //秒杀结果：超时
            execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.TIME_END);
        } catch(RepeatKillException e2) {
            //秒杀结果：重复秒杀
            execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.REPEAT_KILL);
        } catch(UnderStockException e3) {
            //秒杀结果：库存不足
            execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.UNDER_STOCK);
        } catch(DataRewriteException e4) {
            //秒杀结果：数据重写
            execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.DATA_REWRITE);

        } catch (Exception e) {
            //秒杀结果：系统错误
            execution = SeckillExecution.unsuccess(seckillId, SeckillStateEnum.INNER_ERROR);
        } finally {
            result = SeckillResult.success(execution);
            //将秒杀结果放入redis
            redisUtil.putResult(seckillId, userPhone, result);
        }
    }
}
