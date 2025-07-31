package vn.baymax.fjob.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.baymax.fjob.domain.User;
import vn.baymax.fjob.dto.request.ReqLoginDTO;
import vn.baymax.fjob.dto.response.ResCreateUserDTO;
import vn.baymax.fjob.dto.response.ResLoginDTO;
import vn.baymax.fjob.service.UserService;
import vn.baymax.fjob.util.SecurityUtil;
import vn.baymax.fjob.util.annotation.ApiMessage;
import vn.baymax.fjob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${baymax.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("auth/register")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExsit(user.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("email is already");
        }

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleSaveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.mappingUserToResCreateUserDTO(newUser));

    }

    @PostMapping("auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // input username and password into security

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // validate user => write and use loadUserByUsername and use it
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // create a token
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUser = this.userService.handleGetUserByName(loginDTO.getUsername());
        if (currentUser != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                    currentUser.getName(), currentUser.getRole());
            resLoginDTO.setUser(userLogin);
        }

        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // update token user
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("get account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByName(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUser != null) {
            userLogin.setId(currentUser.getId());
            userLogin.setEmail(currentUser.getEmail());
            userLogin.setName(currentUser.getName());
            userLogin.setRole(currentUser.getRole());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok(userGetAccount);

    }

    @GetMapping("/auth/refresh")
    @ApiMessage("get fresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refreshToken") String refresh_token)
            throws IdInvalidException {
        Jwt decoded = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decoded.getSubject();
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("token is not valid");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByName(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                    currentUser.getName(), currentUser.getRole());
            resLoginDTO.setUser(userLogin);
        }

        // create access token
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);
        // create refresh token
        String newRefreshToken = this.securityUtil.createRefreshToken(email, resLoginDTO);

        // update token user
        this.userService.updateUserToken(newRefreshToken, email);

        // set cookies
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new IdInvalidException("access token is not valid");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refrehs token cookie
        ResponseCookie deleteTokenInCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .secure(true)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteTokenInCookie.toString())
                .body(null);
    }
}
