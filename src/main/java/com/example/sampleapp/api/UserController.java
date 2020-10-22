package com.example.sampleapp.api;

import com.example.sampleapp.Constants;
import com.example.sampleapp.dao.UserAccessService;
import com.example.sampleapp.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    UserAccessService userAccessService;
    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> registerUser(@RequestBody Map<String,Object> userMap){
        User user = new User(10,(String)userMap.get("firstName"),(String)userMap.get("lastName"),
                (String)userMap.get("email"),(String)userMap.get("password"));
        user = userAccessService.registerUser(user);
        Map<String,String> map = generateJWTToken(user);
        map.put("message","registered successfully");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(@RequestBody Map<String,Object> userMap){
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        User user = userAccessService.validateUser(email,password);
        Map<String,String> map = generateJWTToken(user);
        map.put("message","loggedIn successfully");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    private Map<String,String> generateJWTToken(User user){
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .compact();
        Map<String,String> map = new HashMap<>();
        map.put("token",token);
        return map;
    }
    @GetMapping("/categories")
    public ResponseEntity<Map<String,Object>> getAllUsers(HttpServletRequest request){
        int userId = (Integer) request.getAttribute("userId");
        //String email = (String) request.getAttribute("email");
        List<Map<String, Object>> users= userAccessService.findAllUsers();
        Map<String,Object> map = new HashMap<>();
        map.put("message","Fetch successful");
        map.put("UserList",users);
       // map.put("email",userId);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @DeleteMapping("/categories/{userId}")
    public ResponseEntity<Map<String,String>> deleteUserById(HttpServletRequest request,
                                                             @PathVariable("userId") Integer userId){
        int userId1 = (Integer) request.getAttribute("userId");
        userAccessService.deleteUser(userId);
        Map<String,String> map = new HashMap<>();
        map.put("message","User Deleted Successfully");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @PutMapping("/categories/{userId}")
    public ResponseEntity<Map<String ,String>> editUserById(HttpServletRequest request,
                                                            @PathVariable("userId") Integer userId,
                                                            @RequestBody Map<String,Object> userMap){
        int userId1 = (Integer) request.getAttribute("userId");
        User user = new User(userId,(String)userMap.get("firstName"),(String)userMap.get("lastName"),
                (String)userMap.get("email"),(String)userMap.get("password"));
        userAccessService.editUserById(user);
        Map<String,String> map = new HashMap<>();
        map.put("message","Edit completed successfully");
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
}
