package com.picpaydesafio.services;

import com.picpaydesafio.domain.transaction.Transaction;
import com.picpaydesafio.domain.user.User;
import com.picpaydesafio.dtos.TransactionDTO;
import com.picpaydesafio.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private NotificationService notificationService;

    public TransactionService(
            UserService userService,
            TransactionRepository transactionRepository,
            RestTemplate restTemplate,
            NotificationService notificationService
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.notificationService = notificationService;
    }

    public Transaction createTransaction(TransactionDTO transactionDTO) throws Exception {
        User sender = userService.findUserById(transactionDTO.senderId());
        User receiver = userService.findUserById(transactionDTO.receiverId());

        userService.validateTransaction(sender, transactionDTO.value());

//        boolean isAuthorized = this.authorizeTransaction(sender, transactionDTO.value());

//        if(!isAuthorized){
//            throw  new Exception("Transação não autorizada");
//        }

        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimeStamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transactionDTO.value()));
        receiver.setBalance(receiver.getBalance().add(transactionDTO.value()));

        this.transactionRepository.save(transaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transação realizada com sucesso");
        this.notificationService.sendNotification(receiver, "Transação realizada com sucesso");

        return transaction;
    }

//    public boolean authorizeTransaction(User sender, BigDecimal value){
//        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);
//
//        if(authorizationResponse.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(authorizationResponse.getBody()).get("status") == "success"){
//            return true;
//        }
//        return false;
//    }
}
