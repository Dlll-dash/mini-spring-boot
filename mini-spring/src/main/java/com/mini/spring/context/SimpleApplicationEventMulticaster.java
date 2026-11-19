package com.mini.spring.context;

import com.mini.spring.beans.BeanDefinition;
import com.mini.spring.beans.MiniBeanFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleApplicationEventMulticaster implements ApplicationEventMulticaster{

    private Set<String> applicationListenerBeans = new LinkedHashSet<>();

    private final MiniBeanFactory factory;

    public SimpleApplicationEventMulticaster(MiniBeanFactory factory){
        this.factory=factory;
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        applicationListenerBeans.add(listenerBeanName);
    }

    /**
     * 广播事件，遍历容器内的listener，分发事件
     * @param event
     */
    @Override
    public void multicastEvent(ApplicationEvent event){

        Class<?>eventType=event.getClass();
        for(String name : applicationListenerBeans){
            BeanDefinition listener = factory.getBeanDefinition(name);
            Class<?>listenerType=listener.getBeanType();
            //先判断一下是否匹配
            if(supportEvent(listenerType,eventType)) {
                invoke(factory.getBean(name), event);
            }
        }

    }

    /**
     * 通知监听器，某事件发生，对应的监听器进行反应
     * @param listener 原始类型，不带未知类型ApplicationListener<?>
     * @param event 需要通知的事件
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    private void invoke(ApplicationListener listener,ApplicationEvent event){
        listener.onApplicationEvent(event);
    }

    private boolean supportEvent(Class<?> listenerType,Class<?> eventType){
        Type[] genericInterfaces = listenerType.getGenericInterfaces();
        for(Type type:genericInterfaces){
            if(!(type instanceof ParameterizedType pt)){
                //普通接口
                continue;
            }

            if(pt.getRawType() != ApplicationListener.class){
                //不是Listener泛型接口
                continue;
            }
            Type arg=((ParameterizedType) type).getActualTypeArguments()[0];
            if(!(arg instanceof Class<?> declaredType)){
                //TypeVariable
                return true;
            }

            if (!declaredType.isAssignableFrom(eventType)) {
                System.out.println("skip " + type.getTypeName() + " for " + eventType.getSimpleName());
                return false;
            }
        }
        //一个也没找到，也不排除。直接返回true，有问题直接报错 ClassCastException
        return true;

    }
}
