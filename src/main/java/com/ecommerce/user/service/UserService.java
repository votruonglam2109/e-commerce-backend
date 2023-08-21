package com.ecommerce.user.service;

import com.ecommerce.exception.DuplicateResourceException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.user.UserRepository;
import com.ecommerce.user.model.*;
import com.ecommerce.user.model.request.UserPasswordChange;
import com.ecommerce.user.model.request.UserPasswordReset;
import com.ecommerce.user.model.request.UserRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    public UserDTO registerUser(UserRegistration userRegistration){
        if(!userRepository.existsByEmail(userRegistration.email())){
            User newUser = userMapper.toEntity(userRegistration);
            return userMapper.toDto(
                    userRepository.save(newUser)
            );
        }else
            throw new DuplicateResourceException("The user with email [%s] already exists".formatted(userRegistration.email()));
    }

    public User findUserById(Long id){
        User u = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The user with id [%s] not exists"
                        .formatted(id)));
        return u;
    }
    public User saveUser(User user){
        return userRepository.save(user);
    }
    public User findUserByEmail(String email){
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("The user with email [%s] not exists"
                        .formatted(email)));
        return u;
    }
    public UserDTO getUser(Long id){
        User u = findUserById(id);
        return userMapper.toDto(u);
    }
    public UserDTO deleteUser(Long id){
        User u = findUserById(id);
        u.setEnable(false);
        return userMapper.toDto(userRepository.save(u));
    }
    public String lockUserWithId(Long id){
        User u = findUserById(id);
        u.setNonLocked(false);
        userRepository.save(u);
        return "The user with id [%s] was locked".formatted(id);
    }
    public String unlockUserWithId(Long id){
        User u = findUserById(id);
        u.setNonLocked(true);
        userRepository.save(u);
        return "The user with id [%s] was unlocked".formatted(id);
    }

    public UserDTO changePassword(User authenticatedUser, UserPasswordChange userPasswordChange) {
        return Optional.of(authenticatedUser).stream()
                .filter(u -> passwordEncoder.matches(
                        userPasswordChange.oldPassword(),
                        u.getPassword()
                ))
                .map(u -> {
                    u.setPassword(passwordEncoder.encode(userPasswordChange.newPassword()));
                    return userRepository.save(u);
                })
                .map(userMapper::toDto)
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Old password wrong"));
    }
    public String handleForgotPassword(String email){
        User u = findUserByEmail(email);
        String otp = createResetPasswordOTP();
        u.setResetPasswordOTP(otp);
        u.setResetPasswordExpired(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
        userRepository.save(u);
        mailService.sendSimpleMessage(
                email,
                "Reset password request",
                "Your reset password code is %s".formatted(otp)
        );
        return "Please check your email";
    }
    private String createResetPasswordOTP(){
        Random rd = new Random();
        int otpCode = rd.nextInt(900000) + 100000;
        return String.format("%6d", otpCode);
    }

    public String handleResetPassword(UserPasswordReset userPasswordReset){
        User user = userRepository.findByEmail(userPasswordReset.email())
                .filter(u -> u.getResetPasswordExpired().isAfter(LocalDateTime.now()))
                .filter(u -> u.getResetPasswordOTP().equals(userPasswordReset.otp()))
                .map(u -> {
                    u.setPassword(passwordEncoder.encode(userPasswordReset.newPassword()));
                    u.setResetPasswordOTP(null);
                    u.setResetPasswordExpired(null);
                    return userRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Invalid reset password token"));

        if (Optional.ofNullable(user).isPresent()){
            return "Reset password successfully";
        }
        return "failure";
    }
}
