package com.mini.spring.demo;

import com.mini.spring.annotation.*;
import com.mini.spring.jdbc.core.JdbcTemplate;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init(){
        System.out.println("@PostConstruct>>OrderService");
    }

    @Transactional
    public String orderOf(long userId) {
        if(userId<0){
            throw new RuntimeException("orderOf "+userId+" failed");
        }
        return "order of " + userRepository.findName(userId);
    }

    @Timed
    @Transactional
    public String cancel(long userId){
        return "cancel order of "+userRepository.findName(userId);
    }

    @Timed
    @Override
    public String describe(long userId) {
        return "describe user#" + userId;
    }

    @Transactional
    public void placeOrder(String name) {
        jdbcTemplate.update("INSERT INTO t_user(id,name) VALUES (?,?)",12, name);
        throw new RuntimeException("boom");
    }


}
