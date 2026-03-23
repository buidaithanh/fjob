package vn.baymax.fjob.controller;

import org.springdoc.core.annotations.ParameterObject;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User API", description = "Manage users in the system")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Create new user", description = "Create a new user account in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
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

    @Operation(summary = "Delete user", description = "Delete a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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

    @Operation(summary = "Get user by id", description = "Retrieve detail information of a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully", content = @Content(schema = @Schema(implementation = ResUserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable long id) throws IdInvalidException {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new IdInvalidException("user with id: " + id + " not found");
        }
        return ResponseEntity.ok(this.userService.mappingUserToResUserDTO(user));
    }

    @Operation(summary = "Get all users", description = "Retrieve list of all users in the system")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Parameter(hidden = true) @Filter Specification<User> spec,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUsers(spec, pageable));
    }

    @Operation(summary = "Update user", description = "Update user information by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
