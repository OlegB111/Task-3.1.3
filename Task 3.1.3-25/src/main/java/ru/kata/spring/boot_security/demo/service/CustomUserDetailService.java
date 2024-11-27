package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dao.UserRepositories;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepositories userRepositories;

    @Autowired
    public CustomUserDetailService(UserRepositories userRepositories) {
        this.userRepositories = userRepositories;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable((userRepositories.findByUsername(username)));
        return user.orElseThrow(() -> new UsernameNotFoundException(String.format("User '%u not found", username)));
    }

}
