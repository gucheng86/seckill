-- 秒杀执行的存储过程：插入购买明细和更新库存

-- 定义存储过程，in表示输入，out表示输出
CREATE PROCEDURE `seckill`.`execute_seckill`
(in v_seckill_id bigint, in v_phone bigint, in v_kill_time timestamp, out r_result int)
BEGIN
    DECLARE insert_count int DEFAULT 0;
    START TRANSACTION;
    -- 1.插入秒杀明细
    insert ignore into success_killed(seckill_id, user_phone, create_time)
        values(v_seckill_id, v_phone, v_kill_time);
    -- row_count()返回上一条修改类型sql的影响行数
    select row_count() into insert_count;
    IF(insert_count = 0) THEN
        ROLLBACK ;
        set r_result = -1;  -- 没有插入，重复秒杀
    ELSEIF(insert_count < 0) THEN
        ROLLBACK ;
        set r_result = -2;  -- 执行出错，系统错误
    ELSE
        -- 2.更新库存
        update seckill
            set number = number - 1
            where seckill_id = v_seckill_id
            and end_time > v_kill_time
            and start_time < v_kill_time
            and number > 0;
        select row_count() into insert_count;
        IF(insert_count = 0) THEN
            ROLLBACK;
            set r_result = 0;   -- 更新失败，秒杀结束
        ELSEIF(insert_count < 0) THEN
            ROLLBACK;
            set r_result = -2;  -- 系统错误
        ELSE
            COMMIT;
            set r_result = 1;   -- 更新成功
        END IF;
    END IF;
END


-- 调用存储过程
set @r_result=-3;
-- 执行存储过程
call execute_seckill(1003, 13502178891, now(), @r_result);
select @r_result

-- 存储过程
-- 1.存储过程优化的是事务行级锁持有的时间，
-- 2.不要过度依赖存储过程，
-- 3.对简单的逻辑可以应用存储过程，