package org.gouenji.financeapp.controller.common;

import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.service.UserService;
import org.gouenji.financeapp.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class PublicAuthorizationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private PublicAuthorizationController publicAuthorizationController;

    private MockMvc mockMvc;

    private User userTest;
    private UserDetails userTestDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(publicAuthorizationController)
                .build();

        userTest = new User(
                "Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userTest.setId(1);

        userTestDetails = new org.springframework.security.core.userdetails.User(
                userTest.getEmail(),
                userTest.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // === Тесты для GET /login ===

    @Test
    void getLoginPage_ShouldReturnLoginPageView_WithoutError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/authorization/login-page"))
                .andExpect(model().attributeDoesNotExist("isAuthenticationFailed"));
    }

    @Test
    void getLoginPage_ShouldReturnLoginPageView_WithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/authorization/login-page"))
                .andExpect(model().attributeExists("isAuthenticationFailed"))
                .andExpect(model().attribute("isAuthenticationFailed", true));
    }

    // === Тесты для GET /registration ===

    @Test
    void getRegistrationPage_ShouldReturnRegistrationPageView() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("public/authorization/registration-page"));
    }

    // === Тесты для POST /login ===

    @Test
    void login_ShouldAuthenticateUserAndRedirectToAccount_WhenCredentialsAreValid() throws Exception {
        String email = userTest.getEmail();
        String password = userTest.getPassword();
        String jwtToken = "generated.jwt.token.here";

        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        when(userService.loadUserByUsername(email)).thenReturn(userTestDetails);
        when(jwtTokenUtil.generateToken(userTestDetails)).thenReturn(jwtToken);
        when(jwtTokenUtil.getLifetime()).thenReturn(Duration.ofHours(24));

        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
                .param("password", password));

        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"))
                .andExpect(cookie().exists("jwt_token"))
                .andExpect(cookie().value("jwt_token", jwtToken))
                .andExpect(cookie().httpOnly("jwt_token", true))
                .andExpect(cookie().secure("jwt_token", false))
                .andExpect(cookie().path("jwt_token", "/"))
                .andExpect(cookie().maxAge("jwt_token", (int) Duration.ofHours(24).getSeconds()));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).loadUserByUsername(email);
        verify(jwtTokenUtil, times(1)).generateToken(userTestDetails);
        verify(jwtTokenUtil, times(1)).getLifetime();
    }

    @Test
    void login_ShouldRedirectToLoginPageWithError_WhenCredentialsAreInvalid() throws Exception {

        String email = userTest.getEmail();
        String password = "wrongpassword";


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));


        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
                .param("password", password));


        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).loadUserByUsername(anyString());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    // === Тесты для POST /registration ===

    @Test
    void createUserAccount_ShouldEncodePasswordAndSaveUser_ThenRedirectToLogin() throws Exception {
        String name = userTest.getName();
        String email = userTest.getEmail();
        String rawPassword = userTest.getPassword();
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);


        ResultActions result = mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", name)
                .param("email", email)
                .param("password", rawPassword));


        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userService, times(1)).save(argThat(user ->
                user.getName().equals(name) &&
                        user.getEmail().equals(email) &&
                        user.getPassword().equals(encodedPassword) &&
                        user.getUserRole() == UserRole.USER
        ));
    }

    // === Тесты для POST /logout ===

    @Test
    void logout_ShouldClearSecurityContextAndRemoveCookie_ThenRedirectToHome() throws Exception {

        ResultActions result = mockMvc.perform(post("/logout"));


        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        result.andExpect(cookie().exists("jwt_token"))
                .andExpect(cookie().maxAge("jwt_token", 0))
                .andExpect(cookie().httpOnly("jwt_token", true))
                .andExpect(cookie().secure("jwt_token", false))
                .andExpect(cookie().path("jwt_token", "/"));
    }
}