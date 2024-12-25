package com.example.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter.model.request.ReqLoginDTO;
import com.example.jobhunter.model.response.ResLoginDTO;
import com.example.jobhunter.service.UserService;
import com.example.jobhunter.service.error.IdInvalidException;
import com.example.jobhunter.util.SecurityUtil;
import com.example.jobhunter.util.annotation.ApiMessage;

@RestController
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final SecurityUtil securityUtil;
  private final UserService userService;

  @Value("${jwt.refresh-expiration}")
  private long refreshTokenExpiration;

  public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
      UserService userService) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
  }

  @PostMapping("/auth/login")
  public ResponseEntity<ResLoginDTO> login(@RequestBody ReqLoginDTO loginDto) {
    // Nạp input gồm username/password vào Security
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(), loginDto.getPassword());

    // xác thực người dùng => cần viết hàm loadUserByUsername
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // create a token

    ResLoginDTO res = new ResLoginDTO();

    var email = loginDto.getUsername();
    var currentUser = this.userService.getUserByEmail(email);

    var userInfo = new ResLoginDTO.UserInfo(currentUser.getId(), currentUser.getEmail(), currentUser.getName());
    res.setUser(userInfo);

    String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());

    res.setAccessToken(access_token);

    // create a refresh token
    String refresh_token = this.securityUtil.createRefreshToken(email, res);

    // update user's refresh token
    this.userService.updateRefreshToken(email, refresh_token);

    // Set cookies
    var resCookies = ResponseCookie.from("refresh_token", refresh_token).httpOnly(true).secure(true).path("/")
        .maxAge(refreshTokenExpiration).build();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString(), null).body(res);
  }

  @GetMapping("/auth/account")
  @ApiMessage("Get current user's information")
  public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
    var email = SecurityUtil.getCurrentUserLogin().orElse("");
    var currentUser = this.userService.getUserByEmail(email);

    ResLoginDTO.UserInfo userInfo = new ResLoginDTO.UserInfo();
    ResLoginDTO.UserGetAccount res = new ResLoginDTO.UserGetAccount(userInfo);

    if (currentUser != null) {
      userInfo.setId(currentUser.getId());
      userInfo.setEmail(currentUser.getEmail());
      userInfo.setName(currentUser.getName());
      res.setUser(userInfo);
    }

    return ResponseEntity.ok(res);
  }

  @GetMapping("/auth/refresh")
  @ApiMessage("Refresh access token")
  public ResponseEntity<ResLoginDTO> refresh(
      @CookieValue(name = "refresh_token") String refresh_token) throws IdInvalidException {

    var decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);

    var email = decodedToken.getSubject();

    var currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);

    if (currentUser == null) {
      throw new IdInvalidException("refresh token không hợp lệ");
    }

    ResLoginDTO res = new ResLoginDTO();

    var userInfo = new ResLoginDTO.UserInfo(currentUser.getId(), currentUser.getEmail(), currentUser.getName());
    res.setUser(userInfo);

    String access_token = this.securityUtil.createAccessToken(email, res.getUser());

    res.setAccessToken(access_token);

    // create a refresh token
    String newRefreshToken = this.securityUtil.createRefreshToken(email, res);

    // update user's refresh token
    this.userService.updateRefreshToken(email, newRefreshToken);

    // Set cookies
    var resCookies = ResponseCookie.from("refresh_token", newRefreshToken).httpOnly(true).secure(true).path("/")
        .maxAge(refreshTokenExpiration).build();

    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString(), null).body(res);
  }

  @PostMapping("/auth/logout")
  @ApiMessage("Logout")
  public ResponseEntity<Void> logout() {
    var email = SecurityUtil.getCurrentUserLogin().orElse("");
    this.userService.updateRefreshToken(email, null);

    // remove cookies
    var resCookies = ResponseCookie.from("refresh_token", null).httpOnly(true).secure(true).path("/").maxAge(0).build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookies.toString())
        .build();
  }
}
