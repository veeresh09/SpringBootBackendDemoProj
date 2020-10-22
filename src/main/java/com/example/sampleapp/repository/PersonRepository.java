package com.example.sampleapp.repository;

import com.example.sampleapp.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByEmail(String email);
   @Query("SELECT p FROM Person p WHERE p.first_name like ?1")
   List<Person>Userh(String first_name);
}
