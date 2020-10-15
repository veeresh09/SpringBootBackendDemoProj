package com.example.sampleapp.repository;

import com.example.sampleapp.exception.EtAuthException;
import com.example.sampleapp.model.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    Integer create (User user) throws EtAuthException;
    User findByEmailAndPassword(String email ,String Password ) throws EtAuthException;
    Integer getCountByEmail(String email);
    User findById(Integer userId);
    List<Map<String, Object>> findAllUsers();
    void deleteUser(Integer userId) throws EtAuthException;
    void updateUser(User user) throws EtAuthException;
}
