package com.mini.spring;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class StandardEnvironment implements Environment{
    Map<String,String> systemProperties = new ConcurrentHashMap<>(16);

    StandardEnvironment(){
        Properties properties = System.getProperties();
        for(String key : properties.stringPropertyNames()){
            systemProperties.put(key,properties.getProperty(key));
        }
    }

    @Override
    public String getProperty(String key) {
        return systemProperties.get(key);
    }

    @Override
    public String resolvePlaceholders(String text) {
        String prefix = "${";
        String suffix = "}";
        String split = ":";

        int preIdx = text.indexOf(prefix);

        int startIdx = preIdx;
        if(startIdx==-1){
            //如果没有前缀，直接返回原字符串
            return text;
        }
        startIdx += prefix.length();
        int nestedIdx = text.indexOf(prefix,startIdx);
        int endIdx = text.indexOf(suffix,startIdx);

        if (endIdx == -1 || (nestedIdx != -1 && nestedIdx < endIdx)) {
            // 没闭合：抄到 ${ 为止，后面的继续递归
            return text.substring(0, startIdx) + resolvePlaceholders(text.substring(startIdx));
        }

        //剥离前后缀
        String part = text.substring(startIdx,endIdx);
        String prePart = text.substring(0,preIdx);
        String sufPart = text.substring(endIdx+suffix.length());

        int splitIdx = part.indexOf(split);
        String key = part;
        String defaultValue = null;

        if(splitIdx!=-1){
            //有分隔符
            key = part.substring(0,splitIdx);
            defaultValue = part.substring(splitIdx+1);

        }

        //从系统变量中获取值
        String result = getProperty(key);

        if(result==null){
            //系统中没有值就使用默认值
            if(defaultValue!=null){
                result = defaultValue;
            }else{
                //默认值都没有就直接保留原文，包括${}
                result = text.substring(preIdx,endIdx+1);
            }
        }
        //递归处理多个值
        sufPart=resolvePlaceholders(sufPart);

        return new StringBuilder(prePart)
                .append(result)
                .append(sufPart)
                .toString();
    }
}
