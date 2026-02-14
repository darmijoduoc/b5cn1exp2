package cl.duocuc.darmijo.users.controllers;

import cl.duocuc.darmijo.core.exceptions.AuthorityException;
import cl.duocuc.darmijo.core.exceptions.ResourceNotFoundException;
import cl.duocuc.darmijo.users.models.*;
import cl.duocuc.darmijo.users.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;
    
    public UsersController(UserService userService) {
        this.userService = userService;
    }
    
    @PostConstruct
    public void init() {
        String password = "p4ssw0rD!";
        List.of(
            new CreateUserParams("jp@localhost", "Juan Perez", password, "ADMIN"),
            new CreateUserParams("da@localhost", "Diego Armijo", password, "ADMIN"),
            new CreateUserParams("hs@localhost", "Hari Seldon", password, "WORKER"),
            new CreateUserParams("gd@localhost", "Gaal Dornick", password, "WORKER"),
            new CreateUserParams("mh@localhost", "Hover Mallow", password, "WORKER")
        ).forEach(this::postUser);
    }
    
    @PostMapping
    public ResponseEntity<?> postUser(
        @RequestBody CreateUserParams params
    ) {
        log.info("Creating user: {}", params);
        Optional<User> user = userService.createUser(
            params.getEmail(),
            params.getDisplayName(),
            params.getRol(),
            params.getPassword()
        );
        log.info("Created user: {}", params);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/{id}/roles")
    public ResponseEntity<?> addRole(
        @PathVariable long id,
        @RequestBody AddRoleParams params
    ) throws AuthorityException {
        log.info("Adding role: {} to User {}", params.getRole(), id);
        Optional<User> user = userService.addRole(
           id, params.getRole()
        );
        log.info("Current role: {} to User {}", user.get().getRol(), id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    public ResponseEntity<?> getUsers() {
        log.info("Retrieving all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
        @PathVariable long id
    ) throws ResourceNotFoundException {
        log.info("Retrieving user: {}", id);
        Optional<User> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
        @RequestBody UpdateUserParams params,
        @PathVariable long id
    ) throws AuthorityException {
        log.info("Updating user: {}", params);
        Optional<User> user = userService.updateUser(
            id,
            params.getEmail(),
            params.getDisplayName(),
            params.getPassword(),
            params.getNewPassword()
        );
        log.info("Updated user: {}", params);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
        @PathVariable long id
    ) throws ResourceNotFoundException {
        log.info("Deleting user: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(
        @RequestBody AuthenticateUserParams params
    ) throws AuthorityException {
        User user = userService.authenticate(params.getEmail(), params.getPassword());
        return ResponseEntity.ok(user);
    }
    
}
