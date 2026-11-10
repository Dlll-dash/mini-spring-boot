package com.mini.spring.web;

import com.mini.spring.SimpleTypeConverter;
import com.mini.spring.web.annotation.PathVariable;
import com.mini.spring.web.exception.MissingPathVariableException;
import com.mini.spring.web.exception.RequestParamException;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;
import java.util.Map;

public class PathVariableArgumentResolver implements ArgumentResolver{
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

    @Override
    public Object resolve(HttpServletRequest req, Parameter parameter) {
        PathVariable ann = parameter.getAnnotation(PathVariable.class);
        String varName = ann.value();
        if(varName.isEmpty()){
            if(parameter.isNamePresent()){
                varName = parameter.getName();
            }else{
                throw new RequestParamException("Name for argument of type ["
                        +parameter.getType()
                        + "] not specified, and parameter name information not available via reflection." +
                        " Ensure that the compiler uses the '-parameters' flag.");
            }
        }
        Map<String,String> attribute = (Map<String,String>)req.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String value = attribute.get(varName);
        if(value != null){
            try {
                Object o = SimpleTypeConverter.convertIfNecessary(value, parameter.getType());
                return o;
            }catch (RuntimeException e){
                throw new RequestParamException("Cannot convert [" + value + "] to " + parameter.getType().getSimpleName());
            }
        }
        throw new MissingPathVariableException("Cannot find path variable ["+varName+"] in path "+req.getRequestURI());
    }
}
