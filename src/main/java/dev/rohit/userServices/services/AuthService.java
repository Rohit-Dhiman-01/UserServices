package dev.rohit.userServices.services;

import dev.rohit.userServices.dtos.UserDTO;
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
            return null;
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password / username does not match");
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

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
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
//       "Find its data type " expiryAt = (Long) claimsJws.getPayload().get("expiryAt");

//        if(new Date() > expiryAt) {
//            the token has expired
//        }

        return SessionStatus.ACTIVE;
    }
}