package com.xarhabia.BankApp.user.service.impl;

import com.xarhabia.BankApp.audit.Auditable;
import com.xarhabia.BankApp.exceptions.BusinessException;
import com.xarhabia.BankApp.user.dto.request.AuthLoginRequest;
import com.xarhabia.BankApp.user.dto.request.CreateUserRequest;
import com.xarhabia.BankApp.user.dto.request.UpdateUserPassword;
import com.xarhabia.BankApp.user.dto.response.UserResponse;
import com.xarhabia.BankApp.user.entity.UserEntity;
import com.xarhabia.BankApp.user.repository.UserRepository;
import com.xarhabia.BankApp.user.service.UserService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import com.xarhabia.BankApp.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    // ==========================================================
    // Spring Security: Carga el usuario por documento (username)
    // ==========================================================
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByDocument(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userEntity.getRole()));

        return new User(
                userEntity.getDocument(),
                userEntity.getHashedPassword(),
                userEntity.getIsActive(),
                true,
                true,
                true,
                authorities
        );
    }

    // ===========================
    // Auth: login
    // ===========================
    public GeneralResponse loginUser(AuthLoginRequest request) {
        Authentication authentication = authenticate(request.document(), request.password());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.createToken(authentication);
        return new GeneralResponse("00", "Login exitoso", true, token);
    }


    @Override
    @Auditable(action = "CREATE_USER")
    public GeneralResponse createNewUser(CreateUserRequest request) {

        if (userRepository.findByDocument(request.document()).isPresent()) {
            throw new BusinessException("DOCUMENTO_DUPLICADO",
                    "Ya existe un usuario con el documento: " + request.document());
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("EMAIL_DUPLICADO",
                    "Ya existe un usuario con el email: " + request.email());
        }

        UserEntity user = UserEntity.builder()
                .fullName(request.fullName())
                .document(request.document())
                .email(request.email())
                .hashedPassword(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        return new GeneralResponse("00", "Usuario registrado correctamente", true, toResponse(user));
    }

    @Override
    @Transactional(readOnly = true)
    @Auditable(action = "FIND_ALL_USERS")
    public GeneralResponse getAllUsers() {
        List<UserResponse> users = userRepository.findByIsActiveTrue()
                .stream().map(this::toResponse).toList();
        return new GeneralResponse("00", "Lista de usuarios", true, users);
    }

    @Override
    @Transactional(readOnly = true)
    @Auditable(action = "FIND_USER")
    public GeneralResponse getUserById(String document) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONRTADO", "El usuario no existe"));
        return new GeneralResponse("00", "Usuario encontrado", true, toResponse(user));
    }

    @Override
    @Auditable(action = "UPDATE_PASSWORD")
    public GeneralResponse updateUserPassword(String document, UpdateUserPassword request) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONRTADO", "El usuario no existe"));

        if (user.getHashedPassword().equals(request.newPassword())) {
            throw new BusinessException("CLAVE_REPETIDA",
                    "La nueva contraseña no puede ser igual a la anterior");
        }

        user.setHashedPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        return new GeneralResponse("00", "Clave actualizada con exito", true, toResponse(user));
    }

    // =========================================================
    // Auth: validación manual de credenciales
    // =========================================================
    @Auditable(action = "LOGIN")
    private Authentication authenticate(String document, String password) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        // 1. Verificar si la cuenta está bloqueada
        if (isAccountLocked(user)) {
            throw new BusinessException("CUENTA_BLOQUEADA",
                    "Cuenta bloqueada temporalmente por múltiples intentos fallidos. Intente más tarde");
        }

        // 2. Validar contraseña
        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            registerFailedAttempt(user);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        // 3. Login exitoso: resetear contador
        resetFailedAttempts(user);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));

        return new UsernamePasswordAuthenticationToken(
                user.getDocument(),
                user.getHashedPassword(),
                authorities
        );
    }

    private boolean isAccountLocked(UserEntity user) {
        if (user.getLockedUntil() == null) {
            return false;
        }
        if (OffsetDateTime.now().isAfter(user.getLockedUntil())) {
            // Ya paso el tiempo de bloqueo: lo liberamos automaticamente
            user.setLockedUntil(null);
            user.setFailedAttempts(0);
            userRepository.save(user);
            return false;
        }
        return true;
    }

    private void registerFailedAttempt(UserEntity user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(UserEntity user) {
        if (user.getFailedAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }
    }

    private UserResponse toResponse(UserEntity u) {
        return new UserResponse(
                u.getFullName(), u.getDocument(), u.getEmail(),
                u.getIsActive(), u.getCreatedAt()
        );
    }


}
