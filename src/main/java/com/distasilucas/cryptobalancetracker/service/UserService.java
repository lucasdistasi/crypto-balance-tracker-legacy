package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.entity.User;

public interface UserService {

    User findByUsername(String username);
}
