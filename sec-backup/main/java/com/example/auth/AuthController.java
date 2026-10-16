package com.example.auth;

import com.example.Spring.Authentication.*;
import com.example.user.AppUser;
import com.example.user.AppUserRepository;
import com.example.Spring.Authentication.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.Map;


@RestController 
@RequestMapping ("/api/auth")
public class AuthController {

    private final AppUserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(
        AppUserRepository users,
        PasswordEncoder encoder
    ){
        this.users = users;
        this.encoder = encoder;
    }

    public record RegisterRequest(
        String name, String email, String password
    ){}

    public record LoginRequest(
        String email, String password
    ){}

    public record UserResponse(
        Long id, String name, String email 
        ){}

        @PostMapping("/register")
        public ResponseEntity<?> register(
            @RequestBody RegisterRequest data
        ){
            if(data.name() == null || data.name().isBlank()
            || data.email() == null || data.email().isBlank()
            || data.password().length()<8){
                return ResponseEntity.badRequest().body(
                    Map.of("message",
                     "enter a name, email , and password of at least 8 character.")

                );

            }

            String hashedPassword = encoder.encode(data.password());

            String email = (data.email());

            AppUser user = new AppUser(
                data.name().trim(),
                email,
                hashedPassword);

                users.save(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("message",
                        "Registration siccessful"
                    )
                );
              
        }

        @PostMapping("/login")
        public ResponseEntity<?> login(
            @RequestBody LoginRequest data,
            HttpServletRequest request

        ){
            if(data.email() == null || data.password() == null){
                return ResponseEntity.badRequest().body(
                    Map.of("message", "email and password required")
                );
            }

            String email = data.email().trim().toLowerCase();

            var optionalUser = users.findByEmailIgnoreCase(email);

            if(optionalUser.isEmpty()
                || !encoder.matches(data.password(),
            optionalUser.get().getPassword())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message","invalid meail or passord")

                );
            }

            AppUser user = optionalUser.get();

            HttpSession oldSession = request.getSession(true);
            oldSession.setAttribute("userId", user.getId());

            return ResponseEntity.ok(
                Map.of(
                    "message", "login successful",
                    "user",new UserResponse(
                        user.getId(),
                    user.getName(),
                user.getEmail())
                )
            );      
        }

        @GetMapping ("/me")
        public ResponseEntity<?> me(
            HttpServletRequest request
        ){
            HttpSession session = request.getSession(false);
            if (session == null
                || session.getAttribute("userId") == null
            ){
                return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
                ).body(Map.of("message", "please log in"));
            }
            Long userId = (Long) session.getAttribute("userId");

            return users.findById(userId)
            .<ResponseEntity<?>>map(user ->
                ResponseEntity.ok(new UserResponse(
                    user.getId(),
                user.getName(),
            user.getEmail()
        ))
            )
            .orElseGet(()-> ResponseEntity.status(HttpStatus.UNAUTHORIZED

            ).body(Map.of("messaage", "user not found")));
        }

        @PostMapping ("/logout")
        public ResponseEntity<?> logout(
            HttpServletRequest request
        ){
            HttpSession session = request.getSession(false);

            if(session!= null){
                session.invalidate();
            }

            return ResponseEntity.ok(
                Map.of("message", "logout successful")
            );
        }
    
}
