package org.gouenji.financeapp.service;


import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User userTest;

    @BeforeEach
    void setUp() {
        userTest = new User(
                "Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userTest.setId(1);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmailIgnoreCase(this.userTest.getEmail()))
                .thenReturn(Optional.of(userTest));

        Optional<User> userFound = userService.findByEmail(this.userTest.getEmail());

        assertThat(userFound).isPresent();
        assertThat(userFound.get().getEmail()).isEqualTo(this.userTest.getEmail());
        verify(userRepository).findByEmailIgnoreCase(this.userTest.getEmail());
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.findByEmailIgnoreCase("another@gmail.com"))
                .thenReturn(Optional.empty());

        Optional<User> userFound = userService.findByEmail("another@gmail.com");

        assertThat(userFound).isEmpty();
        verify(userRepository).findByEmailIgnoreCase("another@gmail.com");
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByEmailIgnoreCase(this.userTest.getEmail()))
                .thenReturn(Optional.of(userTest));

        UserDetails userDetails = userService.loadUserByUsername(this.userTest.getEmail());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(this.userTest.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(this.userTest.getPassword());
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
        verify(userRepository).findByEmailIgnoreCase(this.userTest.getEmail());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmailIgnoreCase("another@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("another@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: another@gmail.com");
        verify(userRepository).findByEmailIgnoreCase("another@gmail.com");
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenUserExists() {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(this.userTest.getEmail(), null)
        );

        when(userRepository.findByEmailIgnoreCase(this.userTest.getEmail()))
                .thenReturn(Optional.of(this.userTest));

        User currentUser = userService.getCurrentUser();

        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getEmail()).isEqualTo(this.userTest.getEmail());
        verify(userRepository).findByEmailIgnoreCase(this.userTest.getEmail());
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenUserDoesNotExist() {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("another@gmail.com", null)
        );

        when(userRepository.findByEmailIgnoreCase("another@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: another@gmail.com");
        verify(userRepository).findByEmailIgnoreCase("another@gmail.com");
    }

    @Test
    void getCurrentUserId_ShouldReturnId_WhenUserExists() {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(this.userTest.getEmail(), null)
        );

        when(userRepository.findByEmailIgnoreCase(this.userTest.getEmail()))
                .thenReturn(Optional.of(this.userTest));

        int id = userService.getCurrentUserId();

        assertThat(id).isEqualTo(1);
        verify(userRepository).findByEmailIgnoreCase(this.userTest.getEmail());
    }

    @Test
    void getCurrentUserId_ShouldThrowException_WhenUserDoesNotExist() {

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("another@gmail.com", null)
        );

        when(userRepository.findByEmailIgnoreCase("another@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: another@gmail.com");
        verify(userRepository).findByEmailIgnoreCase("another@gmail.com");
    }

    @Test
    void save_ShouldCallRepositorySave(){
        userService.save(userTest);

        verify(userRepository).save(userTest);
    }

}
