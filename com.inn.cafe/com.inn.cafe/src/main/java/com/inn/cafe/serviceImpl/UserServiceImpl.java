package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtils;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            if(validateSignUpMap(requestMap)){
                User user= userDao.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getDataFromRequestMap(requestMap));
                    return CafeUtils.getResponse(CafeConstants.REGISTRATION_SUCCESS,HttpStatus.OK);
                }
                else{
                    return CafeUtils.getResponse(CafeConstants.ALREADY_EXISTS,HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return CafeUtils.getResponse(CafeConstants.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);

            }

        }catch(Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (authentication.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtils.generateToken(customerUserDetailsService.getUserDetails().getEmail(), customerUserDetailsService.getUserDetails().getRole()) + "\"}",HttpStatus.OK);
                }
                else{
                    return new ResponseEntity<String>("{\"message\": \""+"Wait For admin approval"+"\"}",HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return CafeUtils.getResponse("Invalid Credentials", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUsers(),HttpStatus.OK);

            }
            else{
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<User> optional= userDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    sendMailToAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                    return CafeUtils.getResponse("User status updated successfully",HttpStatus.OK);
                }
                else{
                     return CafeUtils.getResponse("User Id does not exists",HttpStatus.OK);
                }

            }
            else{
                return CafeUtils.getResponse(CafeConstants.UNAUTHORIZED,HttpStatus.UNAUTHORIZED);
            }

        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponse(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAdmin(String status, String user, List<String> allAdmin) {
            allAdmin.remove(jwtFilter.getCurrentUser());
            if(status!=null && status.equalsIgnoreCase("true")){
                emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account approved","User: "+user+" is approved by ADMIN" + jwtFilter.getCurrentUser() ,allAdmin);
            }
            else{
                emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account disabled","User: "+user+" is disabledw by ADMIN" + jwtFilter.getCurrentUser() ,allAdmin);
            }
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if(requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("contactNumber") && requestMap.containsKey("password")){
            return true;
        }
        else{
            return false;
        }

    }

    private User getDataFromRequestMap(Map<String, String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
}
