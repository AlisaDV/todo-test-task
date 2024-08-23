package ru.fateev.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.fateev.dto.UserDto;
import ru.fateev.exception.AppError;
import ru.fateev.model.User;
import ru.fateev.repository.UserRepository;
import ru.fateev.dto.RegistrationUserDto;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Набор CRUD методов
     */

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUser(Long id){
        Optional<User> res = userRepository.findById(id);
        return res.orElse(null);
    }

    public ResponseEntity<?> updateUser(Long id, User user){
        if (userRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователя с указанным id уже существует"), HttpStatus.BAD_REQUEST);
        }
        user.setId(id);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<?> deleteUser(Long id){
        if (userRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователя с указанным id уже существует"), HttpStatus.BAD_REQUEST);
        }
        userRepository.delete(getUser(id));
        return ResponseEntity.ok("Пользователь успешно удалён");
    }





    /**
     * Поиск по email
     * @param email - Электронная почта
     * @return Optional<User> - Пользователь в обёртке Optional
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * Авторизация
     * @param email - Электронная почта
     * @return new User() - Новый пользователь
     * @throws UsernameNotFoundException - Ошибка ненахождения пользователя по параметру авторизации
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", email)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }


    /**
     * Регистрация
     * @param  registrationUserDto - Данные с регистрации
     * @return User user - Созданный пользователь
     */
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(List.of(roleService.getUserRole()));


        return userRepository.save(user);
    }
}
