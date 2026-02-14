package cl.duocuc.darmijo.users.controllers;

import cl.duocuc.darmijo.core.exceptions.AuthorityException;
import cl.duocuc.darmijo.core.exceptions.ResourceNotFoundException;
import cl.duocuc.darmijo.users.models.AuthenticateUserParams;
import cl.duocuc.darmijo.users.models.ForgotPasswordParams;
import cl.duocuc.darmijo.users.models.User;
import cl.duocuc.darmijo.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(
        @RequestBody AuthenticateUserParams params
    ) throws AuthorityException {
        log.info("Authenticating user: {}", params);
        User user = userService.authenticate(params.getEmail(), params.getPassword());
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/forgot_password")
    public ResponseEntity<?> authenticateUser(
        @RequestBody ForgotPasswordParams params
    ) throws AuthorityException, ResourceNotFoundException {
        User user = userService.getUserByEmail(params.getEmail());
        userService.resetPassword(user.getId(), "1234");
        return ResponseEntity.ok(user);
    }
}
