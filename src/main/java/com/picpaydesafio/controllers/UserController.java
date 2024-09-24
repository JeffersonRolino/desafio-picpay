package com.picpaydesafio.controllers;

import com.picpaydesafio.domain.user.User;
import com.picpaydesafio.dtos.UserDTO;
import com.picpaydesafio.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO){
        User newUser = userService.createUser(userDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    /**
     * Utility method to facilitate register multiple users at once
     * @param usersDTO
     * @return
     */
    @PostMapping(value = "/register")
    public ResponseEntity<List<User>> registerMultipleUsers(@RequestBody List<UserDTO> usersDTO){
        List<User> users = this.userService.registerMultipleUsers(usersDTO);
        return new ResponseEntity<>(users, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = this.userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
