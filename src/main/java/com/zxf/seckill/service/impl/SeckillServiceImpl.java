package com.zxf.seckill.service.impl;

import com.zxf.seckill.dao.SeckillDAO;
import com.zxf.seckill.dao.SuccessKilledDAO;
import com.zxf.seckill.dto.Exposer;
import com.zxf.seckill.dto.SeckillExecution;
import com.zxf.seckill.entity.Seckill;
import com.zxf.seckill.entity.SuccessKilled;
import com.zxf.seckill.enums.SeckillStateEnum;
import com.zxf.seckill.exception.RepeatKillException;
import com.zxf.seckill.exception.SeckillCloseException;
import com.zxf.seckill.exception.SeckillException;
import com.zxf.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service    //加入到IOC容器中
public class SeckillServiceImpl implements SeckillService {
    //日志对象
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //加入一个混淆秒杀接口的字符串salt
    private final String salt = "shsdssljdd'1.";

    @Autowired
    private SeckillDAO seckillDAO;

    @Autowired
    private SuccessKilledDAO successKilledDAO;

    //查询前5条秒杀商品
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDAO.queryAll(0, 4);
    }

    @Override
    public Seckill getSeckillById(long seckillId) {
        return seckillDAO.queryById(seckillId);
    }

    /**
     * 在秒杀开始时输出秒杀商品地址，否则返回时间
     * @param seckillId 秒杀商品id
     * @return 秒杀接口
     */
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //根据id获取商品
        Seckill seckill = seckillDAO.queryById(seckillId);

        //1.查不到这个秒杀产品的记录
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        //2.秒杀时间没到
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        //3.秒杀开启，返回秒杀商品id以及md5
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    //使用md5加密算法
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }


    /**
     * 秒杀成功：减库存，增加明细；失败：抛出异常，事务回滚
     * @param seckillId 秒杀商品id
     * @param userPhone 用户标识
     * @param md5 md5加密串
     * @return 返回秒杀执行的结果
     * @throws SeckillException 秒杀异常
     * @throws RepeatKillException 重复秒杀异常
     * @throws SeckillCloseException 秒杀关闭异常
     */
    @Override
    @Transactional
    /**\
     * 使用注解控制事务方法的优点:
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        //1.秒杀数据被重写
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑
        Date nowTime = new Date();
        try {
            //减库存
            int updateCount = seckillDAO.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                //没用更新库存记录，说明秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                //更新了库存记录，秒杀成功，增加明细
                int insertCount = successKilledDAO.insertSuccessKilled(seckillId, userPhone);
                //检查该明细是否被重复插入，即重复秒杀
                if (insertCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    //秒杀成功,得到成功的明细记录，
                    SuccessKilled successKilled = successKilledDAO.queryByIdWithSeckill(seckillId, userPhone);
                    //返回成功秒杀的信息，将state和stateInfo封装到枚举类中
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 编译期异常转换为运行期异常
            throw new SeckillException("seckill inner error: " + e.getMessage());
        }
    }


}

