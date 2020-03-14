package com.itheima;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.domain.User;
import com.itheima.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MySpringBootApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRepository repository;

    @Test
    public void test() throws JsonProcessingException {
        //1.从redis中获得数据库，数据的形式json字符串
        String userListJson = redisTemplate.boundValueOps("user.findAll").get();
        //2.判断redis中是否存在数据
        if (userListJson == null) {
            //3.如果不存在，从数据库查询
            List<User> all = repository.findAll();
            //4.将查询出的数据存储到redis缓存中
            //将list集合转换成json格式的字符串 使用jackson进行转换
            ObjectMapper objectMapper = new ObjectMapper();
            userListJson = objectMapper.writeValueAsString(all);
            redisTemplate.boundValueOps("user.findAll").set(userListJson);
            System.out.println("===============从mysql数据库中获得user的数据===============");
        } else {
            System.out.println("===============从redis缓存中获得user的数据===============");
        }
        //5.将返回的数据在控制台打印
        System.out.println(userListJson);
    }
}
