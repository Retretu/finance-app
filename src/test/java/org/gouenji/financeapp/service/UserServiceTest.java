package org.gouenji.financeapp.service;


import org.gouenji.financeapp.entity.User;
import org.gouenji.financeapp.entity.enums.users.UserRole;
import org.gouenji.financeapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Ivan",
                "test@gmail.com",
                "1234",
                UserRole.USER
        );
        userRepository.save(user);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUSerExists() {
        Optional<User> userFound = userService.findByEmail("test@gmail.com");

        assertThat(userFound).isPresent();

        User user = userFound.get();

        assertThat(user.getName()).isEqualTo(this.user.getName());
        assertThat(user.getEmail()).isEqualTo(this.user.getEmail());
        assertThat(user.getPassword()).isEqualTo(this.user.getPassword());
        assertThat(user.getUserRole()).isEqualTo(this.user.getUserRole());
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        Optional<User> userFound = userService.findByEmail("testAnotherUser@gmail.com");

        assertThat(userFound).isEmpty();
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        UserDetails userFoundDetails = userService.loadUserByUsername("test@gmail.com");

        assertThat(userFoundDetails).isNotNull();
        assertThat(userFoundDetails.getUsername()).isEqualTo(this.user.getEmail());
        assertThat(userFoundDetails.getPassword()).isEqualTo(this.user.getPassword());
        assertThat(userFoundDetails.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        assertThatThrownBy(() -> userService.loadUserByUsername("testAnotherUser@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: testAnotherUser@gmail.com");
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getCurrentUser_ShouldReturnUser_WhenUserExists() {

        User user = userService.getCurrentUser();

        assertThat(user.getName()).isEqualTo(this.user.getName());
        assertThat(user.getEmail()).isEqualTo(this.user.getEmail());
        assertThat(user.getPassword()).isEqualTo(this.user.getPassword());
        assertThat(user.getUserRole()).isEqualTo(this.user.getUserRole());
    }

    @Test
    @WithMockUser(username = "testAnotherUser@gmail.com")
    void getCurrentUser_ShouldThrowException_WhenUserDoesNotExist() {

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: testAnotherUser@gmail.com");
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getCurrentUserId_ShouldReturnId_WhenUserExists() {

        int userId = userService.getCurrentUser().getId();

        assertThat(userId).isEqualTo(this.user.getId());

    }

    @Test
    @WithMockUser(username = "testAnotherUser@gmail.com")
    void getCurrentUserId_ShouldThrowException_WhenUserDoesNotExist() {

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: testAnotherUser@gmail.com");

    }

}
