package com.usercenter.controller;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.usercenter.common.BaseResponse;
import com.usercenter.common.ResultUtils;
import com.usercenter.model.domain.Sms;
import com.usercenter.model.request.SendSmsCodeRequest;
import com.usercenter.utils.AlgorithmUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Date 2023/6/23 13:44
 * author:wyf
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @PostMapping("/sendCode")
    public BaseResponse<SmsSingleSenderResult> sms(@RequestBody SendSmsCodeRequest sms){
        int appId=1400833068;
        String appKey="5232b013502e7cc7ae035206829b7551";
        int templateId=1836568;
        String smsSign="xmmxffCom网";
        String code = AlgorithmUtils.generatedCode();

        //将验证码存入缓存
        String redisKey=String.format("aoyou:user:smsCode:%s",sms.getPhoneNumber());
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(redisKey,code,5, TimeUnit.MINUTES);
        SmsSingleSenderResult result=new SmsSingleSenderResult();
        try{
            String[]params={code};
            SmsSingleSender sSender=new SmsSingleSender(appId,appKey);
            result = sSender.sendWithParam("86", sms.getPhoneNumber(), templateId, params,smsSign, "", "");
            System.out.println(result);
        }catch (HTTPException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResultUtils.success(result);
    }
}
