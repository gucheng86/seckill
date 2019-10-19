//电话加入cookie失败，没用引用到cookie.js
//存放主要交互逻辑
//javascript 模块化
var seckill = {
    //封装秒杀相关的ajxa的url
    URL: {
        now :function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/'+seckillId+'/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //详情页秒杀逻辑
    detail: {
        //详情页初始化时执行
        init: function (params) {
            //手机验证和登录，计时交互
            //获取手机号，从cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if(!seckill.validatePhone(killPhone)) {
                //获取手机号输入框
                var killPhoneModal = $('#killPhoneModal')
                //bootstrap的组件方法
                killPhoneModal.modal({
                    show:true,       //显示弹出框
                    backdrop: 'static', //禁止位置关闭
                    keyboard: false     //关闭键盘事件
                });
                //提交按钮的事件
                $('#killPhoneBtn').click(function () {
                    //获取input中的值
                    var inputPhone = $('#killPhoneKey').val();

                    if(seckill.validatePhone(inputPhone)) {
                        //将电话写入cookie，设置有效期和有效路径
                        $.cookie('killPhone', inputPhone, {expires:7, path:'/seckill'})
                        console.log("killPhone", $.cookie('killPhone'))
                        //刷新页面
                        window.location.reload();
                    } else {    //
                        //先隐藏，再显示。动态效果
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }

            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //2登录后的计时交互
            //获取系统时间
            $.get(seckill.URL.now(), {}, function (result) {
                if(result && result['success']) {
                    var nowTime = result['data'];
                    //计时显示
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result', result)
                }
            })
        }
    },

    //1.验证手机号
    validatePhone: function(phone){
        if(phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    //2.计时逻辑
    countdown: function(seckillId, nowTime, startTime, endTime) {
        //获取计时结点
        var seckillBox = $('#seckill-box')

        //时间判断
        if(nowTime > endTime) {
            //秒杀结束结点
            seckillBox.html('秒杀结束!');
        } else if(nowTime < startTime) {
            //秒杀未开始,计时事件绑定
            var killTime = new Date(startTime + 1000);  // 防止时间偏移
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒 ');
                seckillBox.html(format);
            }).on('finish.countdown', function () { //时间完成后回调事件
                //获取秒杀地址,控制现实逻辑,执行秒杀
                console.log('______fininsh.countdown');
                //获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },

    //3.秒杀执行逻辑
    handlerSeckill: function(seckillId, node) {
        //获取秒杀地址，控制显示逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>').show(300);
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数进行交互流程
            if(result && result['success']) {
                var exposer = result['data'];   //秒杀地址
                //秒杀接口的逻辑
                if(exposer['exposed']) {    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5']
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl: " + killUrl);

                    //秒杀按钮的事件，只绑定一次
                    $('#killBtn').one('click', function () {
                        //绑定执行秒杀请求的操作
                        //1.先禁用按钮
                        $(this).addClass('disabled');
                        //2.再发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if(result && result['success']){
                                //获取秒杀信息
                                var killResult = result['data']
                                var state = killResult['state']
                                var stateInfo = killResult['stateInfo']

                                //3.显示秒杀结果到节点中
                                node.html('<span class="label label-succee">' +stateInfo + '</span>')
                            }
                        })
                    })
                } else {    //未开启秒杀
                    var now = exposer['now']
                    var start = exposer['start']
                    var end = exposer['end']
                    //重新进入计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }
            }else {
                console.log('result: ' + result);
            }
        })
    }
}
