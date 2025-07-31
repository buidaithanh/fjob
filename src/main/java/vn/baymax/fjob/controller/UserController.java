package vn.baymax.fjob.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.response.ResCreateUserDTO;
import vn.baymax.fjob.dto.response.ResUserDTO;
import vn.baymax.fjob.dto.response.ResultPaginationDTO;
import vn.baymax.fjob.service.UserService;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("create new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {

        boolean isEmailExsit = this.userService.isEmailExsit(user.getEmail());
        if (isEmailExsit) {
            throw new IdInvalidException("this email is already exsit");
        }
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = userService.handleSaveUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.mappingUserToResCreateUserDTO(newUser));

    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("user with id: " + id + " not found");
        }
        userService.deleteUserById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable long id) throws IdInvalidException {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("user with id: " + id + " not found");
        }
        return ResponseEntity.ok(this.userService.mappingUserToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUsers(spec, pageable));
    }

    @PutMapping("/users/update/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable long id, @RequestBody User user)
            throws IdInvalidException {
        User updatedUser = userService.updateUserById(id, user);
        if (updatedUser == null) {
            throw new IdInvalidException("user with id: " + id + " not found");
        }
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/")
    public String helloWorld() {

        return "hello world";
    }

}
