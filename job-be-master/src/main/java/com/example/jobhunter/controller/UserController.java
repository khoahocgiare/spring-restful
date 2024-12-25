package com.example.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter.model.User;
import com.example.jobhunter.model.response.ResCreateUserDTO;
import com.example.jobhunter.model.response.ResUserDTO;
import com.example.jobhunter.model.response.ResultPaginationDTO;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.service.error.IdInvalidException;
import com.example.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @ApiMessage("Get all users")
  @GetMapping
  public ResponseEntity<ResultPaginationDTO> getUsers(
      @Filter Specification<User> spec,
      Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(
        this.userService.getAllUsers(spec, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResUserDTO> getUserById(@PathVariable Long id) {
    var user = userService.getUserById(id);
    return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
  }

  @PostMapping
  public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
    var email = user.getEmail();
    if (userService.getUserByEmail(email) != null) {
      throw new IdInvalidException("Email already exists");
    }

    var hashPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashPassword);
    var newUser = userService.createUser(user);
    newUser.setPassword(null);
    return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResUserDTO> updateUser(@PathVariable Long id, @RequestBody User user)
      throws IdInvalidException {
    var updatedUser = userService.updateUser(id, user);

    if (updatedUser == null) {
      throw new IdInvalidException("User not found");
    }

    return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(updatedUser));
  }

  @DeleteMapping("/{id}")
  public String deleteUser(@PathVariable Long id) throws IdInvalidException {
    if (id > 1000) {
      throw new IdInvalidException("Id quá lớn");
    }
    userService.deleteUser(id);
    return "User deleted: ";
  }

}
