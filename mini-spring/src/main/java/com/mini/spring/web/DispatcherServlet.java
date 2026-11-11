package com.mini.spring.web;

import com.mini.spring.MiniBeanFactory;
import com.mini.spring.web.exception.RequestMethodNotSupportedException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DispatcherServlet extends HttpServlet {
    private final MiniBeanFactory factory;
    private HandlerMapping handlerMapping;
    private final HandlerAdapter adapter;

    public DispatcherServlet(MiniBeanFactory factory) {
        this.factory = factory;
        this.adapter = new HandlerAdapter(factory);
    }


    /**
     * 负责接到请求后进行处理
     * @param req
     * @param res
     * @throws IOException
     */
    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        //获取请求路径
        String path = req.getRequestURI();
        if(handlerMapping==null){
            //懒加载
            handlerMapping = factory.getBean(HandlerMapping.class);
        }
        //获取路径对应的方法 handler
        Handler handler;
        try {
            handler = handlerMapping.getHandler(req);
        }catch (RequestMethodNotSupportedException e){
            WebUtils.write(res,e.getStatusCode(),e.getMessage());
            return;
        }
        if(handler == null){
            String msg = "handler not found in path: "+path;
            WebUtils.write(res,404,msg);
            return;
        }
        //执行路径对应的方法
        adapter.handle(handler,req,res);
    }
}
