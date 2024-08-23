package ru.fateev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.fateev.dto.UserDto;
import ru.fateev.model.User;
import ru.fateev.repository.UserRepository;
import ru.fateev.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }




    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{user_id}")
    public User getUser(@PathVariable Long user_id){
        return userService.getUser(user_id);
    }




    /**
     * Обновление своих данных
     * @param user - Обновлённые данные

     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{user_id}")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable Long user_id){
       return userService.updateUser(user_id, user);
    }


    /**
     * Удаление своего аккаунта
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.DELETE, value = "/users/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long user_id){

        return userService.deleteUser(user_id);
    }
}
