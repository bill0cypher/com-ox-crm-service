package com.ox.crm.core.service;

import static com.ox.crm.core.model.enums.Role.ROLE_GUEST;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ox.crm.core.model.Contact;
import com.ox.crm.core.model.Privilege;
import com.ox.crm.core.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Qualifier("userDetailsServiceImpl")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final ContactService contactService;
  private final RoleService roleService;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    Contact user = contactService.findByEmail(email);

    if (user == null) {
      Role role = roleService.findByName(ROLE_GUEST);
      log.info("User not found. Proceed with guest privileges");

      return new User(EMPTY, EMPTY, getAuthorities(List.of(role)));
    }

    return new User(user.getEmail(), user.getPassword(), getAuthorities(user.getRoles()));
  }

  private Collection<? extends GrantedAuthority> getAuthorities(
      Collection<Role> roles) {

    return getGrantedAuthorities(getPrivileges(roles));
  }

  private List<String> getPrivileges(Collection<Role> roles) {

    List<String> privileges = new ArrayList<>();
    List<Privilege> collection = new ArrayList<>();

    for (Role role : roles) {
      privileges.add(role.getName().name());
      collection.addAll(role.getPrivileges());
    }

    for (Privilege item : collection) {
      privileges.add(item.getName());
    }

    return privileges;
  }

  private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (String privilege : privileges) {
      authorities.add(new SimpleGrantedAuthority(privilege));
    }

    log.debug("Requested privileged authentication");
    return authorities;
  }
}
