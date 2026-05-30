package com.xarhabia.BankApp.user.service.impl;

import com.xarhabia.BankApp.exceptions.BusinessException;
import com.xarhabia.BankApp.user.dto.request.CreateUserRequest;
import com.xarhabia.BankApp.user.dto.request.UpdateUserPassword;
import com.xarhabia.BankApp.user.dto.response.UserResponse;
import com.xarhabia.BankApp.user.entity.UserEntity;
import com.xarhabia.BankApp.user.repository.UserRepository;
import com.xarhabia.BankApp.user.service.UserService;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
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
                .hashedPassword(request.password())
                .build();

        userRepository.save(user);

        return new GeneralResponse("00", "Usuario registrado correctamente", true, toResponse(user));
    }

    @Override
    @Transactional(readOnly = true)
    public GeneralResponse getAllUsers() {
        List<UserResponse> users = userRepository.findByIsActiveTrue()
                .stream().map(this::toResponse).toList();
        return new GeneralResponse("00", "Lista de usuarios", true, users);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneralResponse getUserById(String document) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONRTADO", "El usuario no existe"));
        return new GeneralResponse("00", "Usuario encontrado", true, toResponse(user));
    }

    @Override
    public GeneralResponse updateUserPassword(String document, UpdateUserPassword request) {
        UserEntity user = userRepository.findByDocument(document)
                .orElseThrow(() -> new BusinessException("USUARIO_NO_ENCONRTADO", "El usuario no existe"));

        if (user.getHashedPassword().equals(request.newPassword())) {
            throw new BusinessException("CLAVE_REPETIDA",
                    "La nueva contraseña no puede ser igual a la anterior");
        }

        user.setHashedPassword(request.newPassword());

        userRepository.save(user);

        return new GeneralResponse("00", "Clave actualizada con exito", true, toResponse(user));
    }

    private UserResponse toResponse(UserEntity u) {
        return new UserResponse(
                u.getFullName(), u.getDocument(), u.getEmail(),
                u.getIsActive(), u.getCreatedAt()
        );
    }
}
