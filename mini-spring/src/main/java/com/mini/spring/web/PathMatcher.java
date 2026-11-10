package com.mini.spring.web;

import com.mini.spring.web.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathMatcher {

    private static final String DEFAULT_PATH_SEPARATOR = "/";

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");

    /**
     * 将请求的路径匹配{@code #{RequestMethod}Mapping}({@link GetMapping}...等) 注解内的模式字符串
     * @param pattern 接口注解中匹配的路径模式串
     * @param path 请求的路径
     * @return
     */
    Map<String,String>match(String pattern,String path){
        String[] pattDirs = pattern.split(DEFAULT_PATH_SEPARATOR);
        String[] pathDirs = path.split(DEFAULT_PATH_SEPARATOR);

        Map<String,String> map = new HashMap<>();

        if(pattDirs.length!=pathDirs.length){
            return null;
        }

        for(int i =0;i< pattDirs.length;i++){
            if(matchPattern(pattDirs[i])){
                String key = pattDirs[i].substring(1, pattDirs[i].length()-1);
                map.put(key,pathDirs[i]);
                continue;
            }
            if(!pattDirs[i].equals(pathDirs[i])){
                return null;
            }
        }
        return map;
    }

    private boolean matchPattern(String str){
        Matcher matcher = VARIABLE_PATTERN.matcher(str);
        return matcher.matches();
    }

    public int literalSegments(String pattern){
        int cnt = 0;
        for(String patt : pattern.split(DEFAULT_PATH_SEPARATOR)){
            if(!patt.isEmpty()&&!matchPattern(patt)){
                cnt++;
            }
        }
        return cnt;
    }

}
