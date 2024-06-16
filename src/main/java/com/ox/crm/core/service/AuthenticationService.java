package com.ox.crm.core.service;

import java.util.ArrayList;

import com.ox.crm.core.dto.LoginDto;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.model.Contact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final ContactService contactService;
  private final JwtService jwtService;

  public Contact signup(ContactCreateParam input) {
    return contactService.create(input);
  }

  public String authenticate(LoginDto input) {
    var contact = contactService.findByEmail(input.getEmail());
    var roles = contact.getRoles()
        .stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
        .toList();
    var privileges = contact.getRoles()
        .stream()
        .flatMap(role -> role.getPrivileges().stream())
        .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
        .toList();
    var contactAuthorities = new ArrayList<GrantedAuthority>(roles);
    contactAuthorities.addAll(privileges);
    var principal = new User(contact.getEmail(), contact.getPassword(), contactAuthorities);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            input.getEmail(),
            input.getPassword()
        )
    );

    return jwtService.generateToken(principal);
  }
}
