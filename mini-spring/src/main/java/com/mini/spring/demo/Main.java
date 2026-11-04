package com.mini.spring.demo;

import com.mini.spring.MiniContext;
import com.mini.spring.annotation.ComponentScan;
import com.mini.spring.annotation.Import;
import com.mini.spring.config.TransactionConfig;
import com.mini.spring.web.HandlerMapping;

import java.util.Arrays;

@Import({TransactionConfig.class, HandlerMapping.class})
@ComponentScan({"com.mini.spring.exception","com.mini.spring.demo"})
public class Main {

    public static void main(String[] args) {
        var ctx = new MiniContext(Main.class);
        ctx.refresh();

        System.out.println("---①：配方名单---");
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));

        System.out.println("---②：测试循环依赖注入bean---");
        System.out.println(ctx.getBean(OrderService.class).orderOf(42) + "\n" + ctx.getBean(UserRepository.class).orderOf(21));

        System.out.println("---③：测试回滚---");
        try {
            System.out.println(ctx.getBean(OrderService.class).orderOf(-1));
        }catch (Exception e){
            System.out.println("caught: "+e.getMessage());
        }

        System.out.println("---④：第二个 @Transactional 方法---");
        System.out.println(ctx.getBean(OrderService.class).cancel(7));

        System.out.println("---⑤：没有 @Transactional 的方法---");
        System.out.println(ctx.getBean(OrderService.class).describe(7));

        System.out.println("---⑥：发布事件---");
        ctx.publishEvent(new OrderPlacedEvent(ctx.getBean(OrderService.class)));

        System.out.println("builder=[" + ctx.getBean("builder") + "] greeting=[" + ctx.getBean("greeting") + "]");

        System.out.println(ctx.getBean(Greeter.class).greet());

        HandlerMapping hm = ctx.getBean(HandlerMapping.class);
        System.out.println(hm.getHandler("/hello"));
        System.out.println(hm.getHandler("/nope"));
    }
}
