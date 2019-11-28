package com.newland.intelligentserver.util;

import android.newland.telephony.ApnEntity;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by 002 on 2019/6/21.
 */
public class ReflectUtil
{
    // 反射方式调用方法，方法的参数是实体类的
    public static Object refelctInvoke(String jsonStr,String className)// 第二个参数是类名
    {
        // 反射方式实例化类，要有默认的构造方法才支持
        Class<?> cls=null;
        Object reflect_obj=null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            reflect_obj = mapper.readValue(jsonStr, cls);// 异常要添加对应的返回值说明
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reflect_obj;
    }

    // 反射方式调用方法，方法的参数是实体类的
    public static Object instanceApnEntity(String jsonStr)
    {
        ApnEntity apnEntity = new ApnEntity();
        String[] apn_values = jsonStr.split(" ");
        HashMap<String,String> newApn = new HashMap<>();
        for (String str:apn_values)
        {
            LoggerUtil.v("str="+str);
           String[] apn = str.split(":");
           newApn.put(apn[0],apn[1]);
        }
        // apnEntity的字段 id(int) name apn proxy port mmsPort server user password mmsc mcc mnc numeric authType type protocol roamingProtocol current carrierEnabled
        // bearer mvnoType mvnoMatchData pppNumber read_only(int)
        LoggerUtil.d("id="+newApn.get("id"));
        if(newApn.get("id")!=null)
            apnEntity.setId(newApn.get("id").equals("NULL")?0:Integer.parseInt(newApn.get("id")));
        if(newApn.get("name")!=null)
            apnEntity.setName(newApn.get("name").equals("NULL")?null:newApn.get("name"));
        if(newApn.get("apn")!=null)
            apnEntity.setApn(newApn.get("apn").equals("NULL")?null:newApn.get("apn"));
        if(newApn.get("mcc")!=null)
            apnEntity.setMcc(newApn.get("mcc").equals("NULL")?null:newApn.get("mcc"));
        if(newApn.get("mnc")!=null)
            apnEntity.setMnc(newApn.get("mnc").equals("NULL")?null:newApn.get("mnc"));
        if(newApn.get("type")!=null)
            apnEntity.setType(newApn.get("type").equals("NULL")?null:newApn.get("type"));
        if(newApn.get("proxy")!=null)
            apnEntity.setProxy(newApn.get("proxy").equals("NULL")?null:newApn.get("proxy"));
        if(newApn.get("port")!=null)
            apnEntity.setPort(newApn.get("port").equals("NULL")?null:newApn.get("port"));
        if(newApn.get("mmsProxy")!=null)
            apnEntity.setMmsProxy(newApn.get("mmsProxy").equals("NULL")?null:newApn.get("mmsProxy"));
        if(newApn.get("mmsPort")!=null)
            apnEntity.setMmsProxy(newApn.get("mmsPort").equals("NULL")?null:newApn.get("mmsPort"));
        if(newApn.get("server")!=null)
            apnEntity.setServer(newApn.get("server").equals("NULL")?null:newApn.get("server"));
        if(newApn.get("user")!=null)
            apnEntity.setUser(newApn.get("user").equals("NULL")?null:newApn.get("user"));
        if(newApn.get("password")!=null)
            apnEntity.setPassword(newApn.get("password").equals("NULL")?null:newApn.get("password"));
        if(newApn.get("mmsc")!=null)
            apnEntity.setMmsc(newApn.get("mmsc").equals("NULL")?null:newApn.get("mmsc"));
        if(newApn.get("numeric")!=null)
            apnEntity.setNumeric(newApn.get("numeric").equals("NULL")?null:newApn.get("numeric"));
        if(newApn.get("authType")!=null)
            apnEntity.setAuthType(newApn.get("authType").equals("NULL")?null:newApn.get("authType"));
        if(newApn.get("protocol")!=null)
            apnEntity.setProtocol(newApn.get("protocol").equals("NULL")?null:newApn.get("protocol"));
        if(newApn.get("roamingProtocol")!=null)
            apnEntity.setRoamingProtocol(newApn.get("roamingProtocol").equals("NULL")?null:newApn.get("roamingProtocol"));
        if(newApn.get("current")!=null)
            apnEntity.setCurrent(newApn.get("current").equals("NULL")?null:newApn.get("current"));
        if(newApn.get("carrierEnabled")!=null)
            apnEntity.setCarrierEnabled(newApn.get("carrierEnabled").equals("NULL")?null:newApn.get("carrierEnabled"));
        if(newApn.get("bearer")!=null)
            apnEntity.setBearer(newApn.get("bearer").equals("NULL")?null:newApn.get("bearer"));
        if(newApn.get("mvnoType")!=null)
            apnEntity.setMvnoType(newApn.get("mvnoType").equals("NULL")?null:newApn.get("mvnoType"));
        if(newApn.get("mvnoMatchData")!=null)
            apnEntity.setMvnoMatchData(newApn.get("mvnoMatchData").equals("NULL")?null:newApn.get("mvnoMatchData"));
        if(newApn.get("pppNumber")!=null)
            apnEntity.setPppNumber(newApn.get("pppNumber").equals("NULL")?null:newApn.get("pppNumber"));
        if(newApn.get("read_only")!=null)
            apnEntity.setReadOnly(newApn.get("read_only").equals("NULL")?0:Integer.parseInt(newApn.get("read_only")));
        return apnEntity;
    }

}
