package com.mini.boot.context.annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * .imports 外部文件自动配置类
 */
public class ImportCandidates {
    //路径模板
    private static final String LOCATION = "META-INF/spring/%s.imports";
    //注释符号，遇到#开头的跳过
    private static final String COMMENT_START = "#";

    private final List<String> candidates;

    private ImportCandidates(List<String> candidates){
        this.candidates=List.copyOf(candidates);
    }

    /**
     * 获取自动配置类的候选名单
     * @return 返回快照
     */
    public List<String> getCandidates(){
        return this.candidates;
    }

    /**
     * 从路径中加载import candidate的名称
     * @param annotation
     * @param classLoader
     * @return
     */
    public static ImportCandidates load(Class<?> annotation,ClassLoader classLoader){
        if(annotation==null){
            throw new IllegalArgumentException("annotation cannot be null");
        }
        String location = String.format(LOCATION,annotation.getName());
        Enumeration<URL> urls = findUrlsInClasspath(classLoader,location);
        List<String> importCandidates = new ArrayList<>();
        while(urls.hasMoreElements()){
            URL url = urls.nextElement();
            importCandidates.addAll(readCandidateConfiguration(url));
        }
        return new ImportCandidates(importCandidates);
    }

    private static Enumeration<URL> findUrlsInClasspath(ClassLoader classLoader,String location){
        try{
            return classLoader.getResources(location);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to load configurations from location [" + location + "]", e);
        }

    }

    /**
     * 用IO流读取文件字符串，跳过#注释
     */
    private static List<String> readCandidateConfiguration(URL url){
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))){
            List<String> candidates=new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null){
                //去掉注释
                line = stripComment(line);
                //去掉首尾空格
                line = line.trim();
                if(line.isEmpty()){
                    continue;
                }
                candidates.add(line);
            }
            return candidates;

        }catch (IOException e){
            throw new IllegalArgumentException("Unable to load configurations from location [" + url + "]", e);
        }
    }

    /**
     * 截取、去除注释
     * @param line
     * @return
     */
    private static String stripComment(String line){
        int commentStart = line.indexOf(COMMENT_START);
        if(commentStart==-1){
            return line;
        }
        return line.substring(0,commentStart);
    }
}
