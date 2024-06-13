package com.inn.cafe.rest;


import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequestMapping(path = "/user")
public interface UserRest {

    @PostMapping(path = "/signup")
    public ResponseEntity<String> signUpUser(@RequestBody (required=true)Map<String,String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> loginUser(@RequestBody (required=true)Map<String,String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUsers();

    @PutMapping(path="/update")
    public ResponseEntity<String> updateUser(@RequestBody(required = true) Map<String, String> requestMap);


}
