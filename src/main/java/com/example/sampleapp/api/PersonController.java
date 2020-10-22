package com.example.sampleapp.api;

import com.example.sampleapp.Constants;
import com.example.sampleapp.exception.EtAuthException;
import com.example.sampleapp.exception.ResourceNotFoundException;
import com.example.sampleapp.model.Person;
import com.example.sampleapp.repository.PersonRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

@RestController
@RequestMapping("api/persons")
//@CrossOrigin(origins = "*")
public class PersonController {
    @Autowired
    PersonRepository personRepository;
    @GetMapping("/restricted/getall")
    public ResponseEntity<Map<String,Object>> getAllPersons(HttpServletRequest request){
        int userId = (Integer) request.getAttribute("userId");

        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
         map.put("Persons",personRepository.findAll());
         return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<Map<String ,String>> insertPerson(@Valid @RequestBody Person person){
        String hashedPassword = BCrypt.hashpw(person.getPassword(),BCrypt.gensalt(10));
        person.setPassword(hashedPassword);
        Person person1 =  personRepository.save(person);
        Map<String,String> map = generateJWTToken(person1);
        map.put("message","Successfully registered");
        map.put("userId",valueOf(person1.getId()));
        return new ResponseEntity<>(map,HttpStatus.CREATED);
    }


    @DeleteMapping("/restricted/{userId}")
    public ResponseEntity<?> deletePerson(HttpServletRequest request, @PathVariable Long userId){
        int userId1 = (Integer) request.getAttribute("userId");
        return personRepository.findById(userId).map(person -> {
            personRepository.delete(person);
            return ResponseEntity.ok().build();
        }).orElseThrow(()->new ResourceNotFoundException("Person Not found"));
    }
    @PutMapping("/restricted/{userId}")
    public Person updatePerson(HttpServletRequest request,@PathVariable Long userId, @Valid @RequestBody Person person){
        int userId1 = (Integer) request.getAttribute("userId");
        return personRepository.findById(userId).map(person1 -> {
            person1.setEmail(person.getEmail());
            person1.setFirst_name(person.getFirst_name());
            person1.setLast_name(person.getLast_name());
            return personRepository.save(person1);
        }).orElseThrow(()->new ResourceNotFoundException("Person Not found"));
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> loginUser(@RequestBody Map<String,Object> userMap) throws EtAuthException{
        Person person = personRepository.findByEmail((String) userMap.get("email")).get(0);
        String password = (String) userMap.get("password");
        if(person!=null){
            if(!BCrypt.checkpw(password,person.getPassword()))
                throw new EtAuthException("Invalid login credentials");
            Map<String,String> map = generateJWTToken(person);
            map.put("message","Login Successful");
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        Map<String,String> map = new HashMap<>();
        map.put("message","Person Not Found");
        return new ResponseEntity<>(map,HttpStatus.NOT_FOUND);
    }
    @PostMapping("/temp")
    public ResponseEntity<Map<String,Object>>getPersonByEmail(@RequestBody Map<String,Object> userMap){
        Map<String,Object> map = new HashMap<>();
        List<Person> rand= personRepository.findByEmail((String) userMap.get("email"));
        map.put("something", rand.get(0));
        map.put("message","registered successfully");
        map.put("Persons",personRepository.findByEmail((String) userMap.get("email")));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    private Map<String,String> generateJWTToken(Person person){
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", person.getId())
                .claim("email", person.getEmail())
                .claim("firstName", person.getFirst_name())
                .claim("lastName", person.getLast_name())
                .compact();
        Map<String,String> map = new HashMap<>();
        map.put("token",token);
        return map;
    }
    @GetMapping("/test/{startsWith}")
    public ResponseEntity<Map<String,Object>> getPersonByCondition(@PathVariable String startsWith){
        List<Person> ans= personRepository.Userh('%'+startsWith+"%");
        Map<String,Object> map = new HashMap<>();
        map.put("userList",ans);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
}
