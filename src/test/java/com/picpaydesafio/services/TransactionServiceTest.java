package com.picpaydesafio.services;

import com.picpaydesafio.domain.user.User;
import com.picpaydesafio.domain.user.UserType;
import com.picpaydesafio.dtos.TransactionDTO;
import com.picpaydesafio.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthorizationService authorizationService;

    @Autowired
    @InjectMocks
    private TransactionService transactionService;


    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Should create transaction successfully when everything is OK")
    void createTransactionCase1() throws Exception {
        User sender = new User(1L, "Anthony", "Stark", "123456789",
                "ironman@email.com","123", new BigDecimal(100), UserType.COMMON);

        User receiver = new User(2L, "Steve", "Rogers", "987654321",
                "captainamerica@email.com", "123", new BigDecimal(100), UserType.COMMON );


        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction()).thenReturn(true);

        TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10), 1L, 2L);
        transactionService.createTransaction(transactionDTO);

        verify(transactionRepository, times(1)).save(any());

        sender.setBalance(new BigDecimal(90));
        verify(userService, times(1)).saveUser(sender);

        receiver.setBalance(new BigDecimal(110));
        verify(userService, times(1)).saveUser(receiver);

        verify(notificationService, times(1)).sendNotification(sender, "Transação realizada com sucesso");
        verify(notificationService, times(1)).sendNotification(receiver, "Transação realizada com sucesso");
    }

    @Test
    @DisplayName("Should throw Exception when Transaction is not allowed")
    void createTransactionCase2() throws Exception {
        User sender = new User(1L, "Anthony", "Stark", "123456789",
                "ironman@email.com","123", new BigDecimal(100), UserType.COMMON);

        User receiver = new User(2L, "Steve", "Rogers", "987654321",
                "captainamerica@email.com", "123", new BigDecimal(100), UserType.COMMON );


        when(userService.findUserById(1L)).thenReturn(sender);
        when(userService.findUserById(2L)).thenReturn(receiver);

        when(authorizationService.authorizeTransaction()).thenReturn(false);

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            TransactionDTO transactionDTO = new TransactionDTO(new BigDecimal(10), 1L, 2L);
            transactionService.createTransaction(transactionDTO);
        });

        Assertions.assertEquals("Transação não autorizada", thrown.getMessage());
    }
}