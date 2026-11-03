package com.mini.spring.annotation;

import java.lang.annotation.Annotation;

public interface TypeFilter {

    boolean match(Class<?> candidate);



    /**
     * ------内嵌实现类-------
     */

    /**
     * 用于注解类型的过滤器，能过滤元注解
     */
    class AnnotationTypeFilter implements TypeFilter{

        private final Class<? extends Annotation> annotationType;

        private final boolean considerMetaAnnotations;

        public AnnotationTypeFilter(Class<? extends Annotation> annotationType){
            this(annotationType,true);
        }

        public AnnotationTypeFilter(Class<? extends Annotation> annotationType,boolean considerMetaAnnotations) {
            this.annotationType = annotationType;
            this.considerMetaAnnotations=considerMetaAnnotations;
        }

        @Override
        public boolean match(Class<?> candidate) {
            return considerMetaAnnotations ?
                    AnnotationUtils.findAnnotation(candidate,annotationType)!=null:
                    candidate.isAnnotationPresent(annotationType);
        }

    }

    /**
     * 用于可赋值类型的过滤器
     */
    class AssignableTypeFilter implements TypeFilter{
        private final Class<?> targetType;

        public AssignableTypeFilter(Class<?> targetType) {
            this.targetType = targetType;
        }

        @Override
        public boolean match(Class<?> candidate) {
            return targetType.isAssignableFrom(candidate);
        }
    }


}
