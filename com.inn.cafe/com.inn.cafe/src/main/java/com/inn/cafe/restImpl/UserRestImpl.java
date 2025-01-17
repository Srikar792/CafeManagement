package com.inn.cafe.restImpl;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUpUser(Map<String, String> requestMap) {
        try {
            return userService.signUp(requestMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @Override
    public ResponseEntity<String> loginUser(Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            return userService.getAllUsers();
        }
        catch(Exception exception){
            exception.printStackTrace();
        }

        return  new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try{
             return userService.update(requestMap);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return  CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

