package com.mini.spring.beans;

public class BeanDefinition {

    private String beanName;

    private Class<?> beanType;

    private Object[] constructArgumentValues;

    private String factoryBeanName;

    private String factoryMethodName;

    private boolean primary = false;

    public BeanDefinition(String beanName,Class<?> beanType){
        this.beanName=beanName;
        this.beanType=beanType;
    }

    public BeanDefinition(String beanName,Class<?> beanType,Object[] args){
        this.beanName=beanName;
        this.beanType=beanType;
        this.constructArgumentValues=args;
    }

    public BeanDefinition() {}

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public Object[] getConstructArgumentValues(){
        return constructArgumentValues;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName){
        this.factoryBeanName=factoryBeanName;
    }

    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    public void setFactoryMethodName(String factoryMethodName){this.factoryMethodName=factoryMethodName;}

    public boolean isPrimary(){
        return primary;
    }

    public void setPrimary(boolean primary){
        this.primary = primary;
    }
}
