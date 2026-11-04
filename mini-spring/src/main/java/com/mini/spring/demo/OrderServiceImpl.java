package com.mini.spring.demo;

import com.mini.spring.annotation.*;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    UserRepository userRepository;

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
}
