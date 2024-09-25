package com.picpaydesafio.repositories;

import com.picpaydesafio.domain.user.User;
import com.picpaydesafio.domain.user.UserType;
import com.picpaydesafio.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get User successfully by Document from DB")
    void findUserByDocumentCase1() {
        String document = "123456789";
        UserDTO userDTO = new UserDTO(
                "Peter",
                "Parker",
                document,
                new BigDecimal("100"),
                "peter.parker@email.com",
                "123",
                UserType.COMMON
        );

        this.createUser(userDTO);

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get User by Document from DB when user not exists")
    void findUserByDocumentCase2() {
        String document = "123456789";

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();
    }


    private void createUser(UserDTO userDTO){
        User newUser = new User(userDTO);
        this.entityManager.persist(newUser);
        this.entityManager.close();
    }
}