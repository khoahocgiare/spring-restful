package com.example.jobhunter.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.jobhunter.service.UserService;

import lombok.AllArgsConstructor;

@Component("userDetailsService")
@AllArgsConstructor
public class UserDetailCustom implements UserDetailsService {
  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    com.example.jobhunter.model.User user = this.userService.getUserByEmail(username);

    return new User(
        user.getEmail(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

  }
}
