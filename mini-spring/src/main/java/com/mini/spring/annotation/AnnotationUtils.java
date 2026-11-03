package com.mini.spring.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtils {

    //跳过java自带的元注解，Retention,Target等
    private final static String SKIP_ANNOTATION_PREFIX="java.lang.";

    /**
     * 外部调用入口
     * @param cls
     * @param target
     * @return
     * @param <A>
     */
    public static <A extends Annotation> A findAnnotation(Class<?> cls, Class<A> target){
        return findAnnotation(cls,target,new HashSet<>());
    }

    /**
     * 递归查找目标元注解
     * @param cls
     * @param target
     * @param visited
     * @return
     * @param <A>
     */
    private static <A extends Annotation> A findAnnotation(Class<?> cls, Class<A> target, Set<Class<?>> visited){
        if(cls==null||!visited.add(cls))return null;
        A direct = cls.getAnnotation(target);
        if(direct!=null)return direct;
        //继续递归向上找
        Annotation[] annotations = cls.getAnnotations();
        for(Annotation ann : annotations){
            if(ann.annotationType().getName().startsWith(SKIP_ANNOTATION_PREFIX))continue;
            A found = findAnnotation(ann.annotationType(),target,visited);
            if(found!=null){
                return found;
            }
        }
        return null;
    }
}
