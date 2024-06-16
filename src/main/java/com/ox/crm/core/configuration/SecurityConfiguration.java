package com.ox.crm.core.configuration;

import static com.ox.crm.core.model.enums.Role.ROLE_ADMIN;
import static com.ox.crm.core.model.enums.Role.ROLE_USER;

import com.ox.crm.core.model.enums.Role;
import com.ox.crm.core.properties.ApplicationProperties;
import com.ox.crm.core.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final ApplicationProperties properties;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsService userDetailsService;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public SecurityConfiguration(
      ApplicationProperties properties,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
      BCryptPasswordEncoder passwordEncoder) {
    this.properties = properties;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var adminAccess = AuthorityAuthorizationManager.<RequestAuthorizationContext>hasRole(ROLE_ADMIN.toString());
    adminAccess.setRoleHierarchy(roleHierarchy());
    var userAccess = AuthorityAuthorizationManager.<RequestAuthorizationContext>hasRole(ROLE_USER.toString());
    userAccess.setRoleHierarchy(roleHierarchy());

    http.csrf()
        .disable()
        .authorizeHttpRequests(registry -> registry
            .requestMatchers("/auth/**")
            .permitAll()
            .requestMatchers(HttpMethod.DELETE, "/v1/**")
            .access(adminAccess)
            .requestMatchers("/v1/**")
            .access(userAccess)
        )
        .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var configuration = new CorsConfiguration();
    var cors = properties.getCors();

    configuration.setAllowedOrigins(cors.getAllowedOrigins());
    configuration.setAllowedMethods(cors.getAllowedMethods());
    configuration.setAllowedHeaders(cors.getAllowedHeaders());

    var source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**",configuration);

    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    var authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);

    return authProvider;
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy(Role.getRolesHierarchy());
  }
}
