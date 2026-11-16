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

    /**
     * 符合子类支持的MediaType
     * @param clazz
     * @param type
     * @return
     */
    @Override
    public boolean canWrite(Class<?> clazz,String type) {
        return supports(clazz)&&canWrite(type);
    }

    /**
     * 通配符匹配
     * @param type
     * @return
     */
    protected boolean canWrite(String type){
        if(type==null){
            return true;
        }
        //通配符匹配
        for(String mediaType: supportedMediaTypes){
            if(isCompatible(mediaType,type)){
                return true;
            }
        }
        return false;
    }

    /**
     * 子类覆写，子类各自实际支持的逻辑
     * @param clazz
     * @return
     */
    protected abstract boolean supports(Class<?> clazz);

    /**
     * 提供给子类调用，设置各自独立的支持类型
     * @param supportedMediaTypes
     */
    public void setSupportedMediaTypes(List<String> supportedMediaTypes) {
        this.supportedMediaTypes = List.copyOf(supportedMediaTypes);
    }

    /**
     * 对两个类型进行通配符匹配
     * @param s1
     * @param s2
     * @return
     */
    protected boolean isCompatible(String s1,String s2){
        if(s1==null||s2==null){
            return true;
        }
        String[] split1 = s1.split(";");
        String[] split2 = s2.split(";");
        return doCompare(split1[0],split2[0]);
    }

    /**
     * 递归进行匹配，首先以{@code /}将两个初始串分割成前后两个部分，两两进行对应匹配；
     * 对存在{@code +}后缀匹配分割成前缀和后缀，再两两进行对应匹配；
     * 对通配符{@code *}直接返回匹配结果为{@code true}；
     * 最后进行字面匹配
     * @param s1
     * @param s2 任意两个 {@code mediaType} 字符串
     * @return
     */
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
