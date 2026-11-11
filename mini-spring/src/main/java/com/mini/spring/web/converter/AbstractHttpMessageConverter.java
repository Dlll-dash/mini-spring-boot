package com.mini.spring.web.converter;

import java.util.Collections;
import java.util.List;

public abstract class AbstractHttpMessageConverter implements HttpMessageConverter<Object>{

    private List<String> supportedMediaTypes = Collections.emptyList();

    @Override
    public boolean canRead(Class<?> clazz, String type) {
        return supports(clazz)&&canRead(type);
    }

    protected boolean canRead(String type){
        if(type==null){
            return true;
        }
        //暂时先硬编码匹配
        for(String mediaType: supportedMediaTypes){
            if(isCompatible(mediaType,type)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz,String type) {
        return supports(clazz)&&canWrite(type);
    }

    protected boolean canWrite(String type){
        //本应是通配符匹配 text/* -> text/plain,text/html
        if(type==null){
            return true;
        }
        //暂时先硬编码匹配
        for(String mediaType: supportedMediaTypes){
            if(isCompatible(mediaType,type)){
                return true;
            }
        }
        return false;
    }

    /**
     * 子类覆写
     * @param clazz
     * @return
     */
    protected abstract boolean supports(Class<?> clazz);

    public void setSupportedMediaTypes(List<String> supportedMediaTypes) {
        this.supportedMediaTypes = List.copyOf(supportedMediaTypes);
    }

    protected boolean isCompatible(String s1,String s2){
        if(s1==null||s2==null){
            return true;
        }
        String[] split1 = s1.split(";");
        String[] split2 = s2.split(";");
        return doCompare(split1[0],split2[0]);
    }

    private boolean doCompare(String s1,String s2){
        //一轮切割
        if(s1.contains("/")&&s2.contains("/")){
            String[] ss1 = s1.split("/");
            String[] ss2 = s2.split("/");
            return doCompare(ss1[0],ss2[0])&&doCompare(ss1[1],ss2[1]);
        }

        //二轮切割
        if(s1.contains("+")||s2.contains("+")){
            // 字符串后面拼接一个"+"保证切割出来数组大小至少为2,避免越界;
            // limit为3保证已有"+"情况下不会切出单个"+"的情况,避免进入死循环导致栈溢出
            String[] ss1 = (s1+"+").split("\\+",3);
            String[] ss2 = (s2+"+").split("\\+",3);

            return doCompare(ss1[0],ss2[0])&&doCompare(ss1[1],ss2[1]);
        }

        //通配符匹配
        if(s1.equals("*")||s2.equals("*")){
            return true;
        }

        //字面匹配
        return s1.equals(s2);
    }
}
