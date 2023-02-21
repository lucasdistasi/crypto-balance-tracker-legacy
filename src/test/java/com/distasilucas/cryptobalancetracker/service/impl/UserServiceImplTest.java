package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.User;
import com.distasilucas.cryptobalancetracker.repository.UserRepository;
import com.distasilucas.cryptobalancetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepositoryMock;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepositoryMock);
    }

    @Test
    void shouldReturnUser() {
        var userEntity = User.builder()
                .username("John")
                .build();

        when(userRepositoryMock.findByUsername("John")).thenReturn(Optional.of(userEntity));

        var user = userService.findByUsername("John");

        assertEquals(userEntity.getUsername(), user.getUsername());
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        when(userRepositoryMock.findByUsername("John")).thenReturn(Optional.empty());

        var usernameNotFoundException = assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername("John"));

        assertEquals("Username not found", usernameNotFoundException.getMessage());
    }

}