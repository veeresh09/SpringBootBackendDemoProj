package com.example.sampleapp.dao;

import com.example.sampleapp.exception.EtAuthException;
import com.example.sampleapp.model.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    User validateUser(String email,String Password) throws EtAuthException;
    User registerUser(User user) throws EtAuthException;
    List<Map<String, Object>> findAllUsers();
    void deleteUser(Integer userId) throws EtAuthException;
    void editUserById(User user) throws EtAuthException;
}

