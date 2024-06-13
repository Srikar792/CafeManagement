package com.inn.cafe.JWT;

import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;


@Service
public class CustomerUserDetailsService implements UserDetailsService {


    @Autowired
    UserDao userDao;

    private com.inn.cafe.POJO.User user;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        user = userDao.findByEmail(email);
        if(!Objects.isNull(user)){
            return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),new ArrayList<>());
        }
        else{
            throw new UsernameNotFoundException("User Not found");
        }
    }

    public User getUserDetails(){
        return user;
    }
}
