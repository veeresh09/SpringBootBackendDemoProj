package com.example.sampleapp.dao;

import com.example.sampleapp.exception.EtAuthException;
import com.example.sampleapp.model.User;
import com.example.sampleapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserAccessService implements UserDao{
    @Autowired
    UserRepository userRepository;
    @Override
    public User validateUser(String email, String Password) throws EtAuthException {
        if(email!= null) email = email.toLowerCase();
        return userRepository.findByEmailAndPassword(email, Password);
    }
    @Override
    public User registerUser(User user) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        String email = user.getEmail();
        if(!pattern.matcher(email).matches()){
            throw new EtAuthException("Invalid email format");
        }
        Integer count = userRepository.getCountByEmail(email);
        if(count>0)
            throw new EtAuthException("Email already in use");
        Integer userId = userRepository.create(user);
        return userRepository.findById(userId);
    }

    @Override
    public List<Map<String, Object>> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public void deleteUser(Integer userId) throws EtAuthException {
        userRepository.deleteUser(userId);
    }

    @Override
    public void editUserById(User user) throws EtAuthException {
        userRepository.updateUser(user);
    }

}
