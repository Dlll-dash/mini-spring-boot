package com.mini.spring.web;

import com.mini.spring.BeanFactoryAware;
import com.mini.spring.MiniBeanFactory;
import com.mini.spring.annotation.AnnotationUtils;
import com.mini.spring.annotation.Controller;
import com.mini.spring.web.annotation.RequestMapping;
import com.mini.spring.web.annotation.RequestMethod;
import com.mini.spring.web.exception.RequestMethodNotSupportedException;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class HandlerMapping implements BeanFactoryAware {

    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE =
            HandlerMapping.class.getName() + ".uriTemplateVariables";

    private final Map<String, List<Handler>> handlers = new HashMap<>();

    private final PathMatcher matcher = new PathMatcher();

    @Override
    public void setBeanFactory(MiniBeanFactory factory) {
        for(String beanName : factory.getBeanDefinitionNames()){
            Class<?> beanType = factory.getBeanDefinition(beanName).getBeanType();
            if(beanType!=null && isHandler(beanType)) {
                //获取类级的前缀
                String prefix = pathOf(mappingOf(beanType));

                for (Method m : beanType.getDeclaredMethods()) {
                    //拿到带RequestMapping的注解（@GetMapping,@PostMapping...）,或者RequestMapping自己
                    Annotation requestMapping = mappingOf(m);
                    if(requestMapping==null)continue;
                    RequestMethod requestMethod;

                    if(requestMapping instanceof RequestMapping){
                        requestMethod = ((RequestMapping) requestMapping).method();
                    }else {
                        requestMethod = requestMapping.annotationType().getAnnotation(RequestMapping.class).method();
                    }
                    //获取Handler,加入Handler列表
                    Handler handler = new Handler(beanName, m, requestMethod);
                    List<Handler> handlerList = handlers.get(prefix + pathOf(requestMapping));
                    if(handlerList == null){
                        handlerList = new ArrayList<>();
                    }
                    handlerList.add(handler);
                    //加上前缀
                    handlers.put(prefix + pathOf(requestMapping), handlerList);

                }
            }
        }
    }

    /**
     * 搜索RequestMapping标记注解
     * @param el
     * @return
     */
    private Annotation mappingOf(AnnotatedElement el){
        for(Annotation ann : el.getAnnotations()){
            Class<?> type = ann.annotationType();
            if(type== RequestMapping.class|| AnnotationUtils.findAnnotation(type, RequestMapping.class)!=null){
                //如果是类级的直接返回@RequestMapping本身；是方法级的就返回原注释(@GetMapping,@PostMapping...)
                return ann;
            }
        }
        return null;
    }

    /**
     * 通过反射调用注解的方法，获取注解中的路径属性
     * @param requestMapping
     * @return
     */
    private String pathOf(Annotation requestMapping){
        if(requestMapping == null){
            return "";
        }
        try {
            return (String)requestMapping.annotationType()
                    .getDeclaredMethod("value").invoke(requestMapping);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据路径和请求方法获取Handler
     * @param req
     * @return
     */
    public Handler getHandler(HttpServletRequest req){
        String requestMethod = req.getMethod();
        String path = req.getRequestURI();

        List<Match> matches = new ArrayList<>();
        boolean pathMatched = false;

        for(Map.Entry<String, List<Handler>> entry : handlers.entrySet()) {
            List<Handler> handlerList = entry.getValue();
            String pattern = entry.getKey();
            Map<String, String> vars = matcher.match(pattern, path);
            if(vars == null)continue;
            pathMatched = true;
            int score = matcher.literalSegments(pattern);

            for (Handler handler : handlerList) {
                //遍历Handler列表，匹配请求方法
                if (!String.valueOf(handler.getRequestMethod()).equals(requestMethod)) {
                    continue;
                }
                matches.add(new Match(pattern,handler,vars,score));
            }
        }
        if(matches.isEmpty()){
            if(!pathMatched){
                //没有任何模式匹配
                return null;
            }
            //有模式匹配，但是没有这个请求方法的handler
            throw new RequestMethodNotSupportedException(requestMethod,path);
        }
        //从大到小排序，选择匹配度最大的返回
        matches.sort(Comparator.comparingInt(Match::score).reversed());
        Match best = matches.get(0);
        if(matches.size()>1){
            if(best.score==matches.get(1).score){
                //如果前两名匹配度相同，就报错
                throw new IllegalStateException("Ambiguous handler methods mapped for '" + path
                        + "': {" + best.pattern + ", " + matches.get(1).pattern + "}");
            }
        }

        //将参数值存到请求当中
        req.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, best.vars);
        return best.handler;
    }

    private boolean isHandler(Class<?> type){
        return AnnotationUtils.findAnnotation(type,Controller.class)!=null;
    }

    /**
     * 比较每个pattern的数据载体
     */
    private record Match(String pattern, Handler handler, Map<String, String> vars, int score) { }


}
