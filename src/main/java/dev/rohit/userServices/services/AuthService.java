package dev.rohit.userServices.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rohit.userServices.dtos.EmailFormatDTO;
import dev.rohit.userServices.dtos.UserDTO;
import dev.rohit.userServices.exception.TokenAlreadyExpire;
import dev.rohit.userServices.exception.UserAlreadyExists;
import dev.rohit.userServices.models.Session;
import dev.rohit.userServices.models.SessionStatus;
import dev.rohit.userServices.models.User;
import dev.rohit.userServices.repositories.SessionRepository;
import dev.rohit.userServices.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SecretKey secretKey;

    // Create bean and use it.
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public AuthService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        secretKey = Jwts.SIG.HS256.key().build();
    }

    public ResponseEntity<UserDTO> login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserAlreadyExists("User with email: "+email+ " do not exists");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new UserAlreadyExists("Password does not match");
        }

        Map<String, Object> jwtData = new HashMap<>();
        jwtData.put("email", email);
        jwtData.put("createdAt", new Date());
        jwtData.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        String token = Jwts
                .builder()
                .claims(jwtData)
                .signWith(secretKey)
                .compact();

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);

        sessionRepository.save(session);

        UserDTO userDTO = UserDTO.from(user);

        MultiValueMap<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + token);
        return new ResponseEntity<UserDTO>(userDTO, headers, HttpStatus.OK);
    }

    public void logout(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository
                .findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return;
        }

        Session session = sessionOptional.get();

        session.setSessionStatus(SessionStatus.ENDED);

        sessionRepository.save(session);
    }

    public UserDTO signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()){
            throw new UserAlreadyExists("User with email: "+email+ " already exists");
        }

        try {
            kafkaTemplate.send("sendEmail",objectMapper.writeValueAsString(getMessage(user)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
    }

    private EmailFormatDTO getMessage(User user){
        EmailFormatDTO message = new EmailFormatDTO();
        message.setTo(user.getEmail());
        message.setContent("Sign up successfully");
        message.setSubject("Sign up success");
        message.setFrom("rohitdhiman01021999@gmail.com");
        return message;
    }

    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return SessionStatus.ENDED;
        }

        Session session = sessionOptional.get();

        if (!session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.ENDED;
        }

        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
//        Write logic if verification fails exception is JwtException / SignatureException
//        so put it in try and catch and throw a meaning full error message to the front-end
//        like token provide is wrong please log in again

        String email = (String) claimsJws.getPayload().get("email");
        Date expiryAt = (Date) claimsJws.getPayload().get("expiryAt");

        if(new Date().getTime() > expiryAt.getTime()) {
            throw new TokenAlreadyExpire("Token has taken");
        }

        return SessionStatus.ACTIVE;
    }
}