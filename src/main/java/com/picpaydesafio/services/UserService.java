package com.picpaydesafio.services;

import com.picpaydesafio.domain.user.User;
import com.picpaydesafio.domain.user.UserType;
import com.picpaydesafio.dtos.UserDTO;
import com.picpaydesafio.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuário do tipo Lojista não está autorizado a realizar transação");
        }

        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Saldo insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.userRepository.findById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user){
        this.userRepository.save(user);
    }

    public User createUser(UserDTO userDTO) {
        User newUser = new User(userDTO);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> registerMultipleUsers(List<UserDTO> usersDTO){
        List<User> users = new ArrayList<>();
        usersDTO.forEach(userDTO -> {
            User user = new User(userDTO);
            users.add(user);
            this.saveUser(user);
        });
        return users;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }
}
