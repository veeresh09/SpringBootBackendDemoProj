package com.example.sampleapp.repository;

import com.example.sampleapp.exception.EtAuthException;
import com.example.sampleapp.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class UserDataAccessService implements UserRepository{
    private static final String SQL_CREATE = "INSERT INTO SAMPLE_USERS(USER_ID,FIRST_NAME,LAST_NAME,EMAIL,PASSWORD) " +
            "VALUES(NEXTVAL('SAMPLE_USERS_SEQ'),?,?,?,?)";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM SAMPLE_USERS WHERE EMAIL = ?";
    private static final String SQL_FIND_BY_ID = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " +
            "FROM SAMPLE_USERS WHERE USER_ID = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD" +
            " FROM SAMPLE_USERS WHERE EMAIL = ?";
    private static final String SQL_FIND_ALL= "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL " +
            "" +
            "FROM SAMPLE_USERS";
    private static final String SQL_DELETE_USER_BY_ID = "DELETE FROM SAMPLE_USERS WHERE USER_ID = ?";
    private static final String SQL_UPDATE_USER_BY_ID = "UPDATE SAMPLE_USERS SET FIRST_NAME = ?, " +
            " LAST_NAME = ? WHERE USER_ID = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(User user) throws EtAuthException {
        String hashedPassword = BCrypt.hashpw(user.getPassword(),BCrypt.gensalt(10));
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, hashedPassword);
                return ps;
            },keyHolder);
            return (Integer) keyHolder.getKeys().get("USER_ID");
        }catch (Exception e){
            throw new EtAuthException(e.getMessage());
        }
    }

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL,new Object[]{email},userRowMapper);
            if(!BCrypt.checkpw(password,user.getPassword()))
                throw new EtAuthException("Invalid login credentials");
            return user;
        }
        catch (EmptyResultDataAccessException e){
            throw new EtAuthException("Invalid login credentials");
        }
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL,new Object[] {email},Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID,new Object[]{userId},userRowMapper);
    }

    @Override
    public List<Map<String, Object>> findAllUsers() {
        return jdbcTemplate.queryForList(SQL_FIND_ALL);
    }

    @Override
    public void deleteUser(Integer userId) throws EtAuthException {
        try{
        jdbcTemplate.update(SQL_DELETE_USER_BY_ID,new Object[] {userId});}
        catch (Exception e){
            throw new EtAuthException(e.getMessage());
        }
        return;
    }

    @Override
    public void updateUser(User user) throws EtAuthException {
        try{
        jdbcTemplate.update(SQL_UPDATE_USER_BY_ID,new Object[]{user.getFirstName(),
                user.getLastName(),user.getUserId()});}
        catch (Exception e){
            throw new EtAuthException(e.getMessage());
        }
    }

    private RowMapper<User> userRowMapper=((rs,rowNum)->{
        return new User(rs.getInt("USER_ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD")
                );
    });
}
