package com.ox.crm.core.controller.rest;

import com.ox.crm.core.dto.ContactDto;
import com.ox.crm.core.dto.LoginDto;
import com.ox.crm.core.dto.LoginResponse;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.mapper.ContactMapper;
import com.ox.crm.core.service.AuthenticationService;
import com.ox.crm.core.service.JwtService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication resource")
public class AuthController {

  private final JwtService jwtService;
  private final ContactMapper contactMapper;
  private final AuthenticationService authenticationService;

  @PostMapping("/signup")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public ContactDto register(@RequestBody @Valid ContactCreateParam contactCreateParam) {
    var registeredContact = authenticationService.signup(contactCreateParam);

    return contactMapper.mapToContactDto(registeredContact);
  }

  @PostMapping("/login")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public LoginResponse authenticate(@RequestBody @Valid LoginDto loginDto) {
    String jwtToken = authenticationService.authenticate(loginDto);

    return LoginResponse.builder()
        .accessToken(jwtToken)
        .expiresIn(jwtService.getExpirationTime())
        .build();
  }
}
