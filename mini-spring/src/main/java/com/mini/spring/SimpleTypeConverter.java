package com.mini.spring;

public class SimpleTypeConverter {
    /**
     * 简单类型转换器，将String转为Object
     * @param value
     * @param requiredType
     * @return
     */
    public static Object convertIfNecessary(String value,Class<?> requiredType){
        if(requiredType==String.class){
            return value;
        }
        if(!requiredType.isPrimitive()&&value.isEmpty()){
            return null;
        }
        if(requiredType==Integer.class||requiredType==int.class){
            return Integer.parseInt(value);
        }
        if(requiredType==Boolean.class||requiredType==boolean.class){
            return Boolean.parseBoolean(value);
        }
        if(requiredType==Double.class||requiredType==double.class){
            return Double.parseDouble(value);
        }

        throw new IllegalStateException("unsupported type: "+requiredType.getName());
    }
}
