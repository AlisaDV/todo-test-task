package ru.fateev.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fateev.dto.IdDto;
import ru.fateev.dto.TodoDto;
import ru.fateev.model.Todo;
import ru.fateev.service.TodoService;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Получить задачу по id
     * @param id - id нужной задачи
     * @return - Todo todo
     */
    @RequestMapping(method = {RequestMethod.GET}, value = "/todos/{id}")
    public Todo getTodo(@PathVariable Long id) {
        return todoService.getTodo(id);
    }

    /**
     * Получить задачи пользователя
     * @param principal - текущий пользователь
     * @param pageable - пагинация
     * @return - Page<Todo> size=4, sort by id desc
     */
    @RequestMapping(method = {RequestMethod.GET}, value = "/todos")
    public Page<Todo> getTodosByUser(Principal principal,
                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC, size = 4)
                              Pageable pageable
                              ) {
        return todoService.getTodosByUser(principal.getName(), pageable);
    }


    /**
     * Создать новую задачу
     * @param todoDto - данный из запроса
     * @param principal - текущий пользователь
     * @return - Todo todo
     */
    @RequestMapping(method = {RequestMethod.POST}, value = "/todos")
    public ResponseEntity<?> addTodo(@RequestBody TodoDto todoDto, Principal principal) {
       return todoService.addTodo(todoDto, principal.getName());
    }

    /**
     * Обновить задачу через PUT
     * @param id
     * @param todo
     * @return
     */
    @RequestMapping(method = {RequestMethod.PUT}, value = "/todos/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id, @RequestBody Todo todo){
       return todoService.updateTodo(id, todo);
    }


    /**
     * 400 Bad request
     * @param id
     * @return
     */
//    @RequestMapping(method = {RequestMethod.PATCH}, value = "/todos/{id}")
//    public ResponseEntity<?> updateTodoWithPatch(@PathVariable Long id, @RequestBody JsonPatch jsonPatch)
//            throws JsonPatchException, JsonProcessingException {
//        return ResponseEntity.ok(todoService.patch(id, jsonPatch));
//    }




    @RequestMapping(method = {RequestMethod.DELETE}, value = "/todos/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id){
       return todoService.deleteTodo(id);
    }


    /**
     * Передача задачи другому пользователю
     * @param idDto - id пользователя и id задачи
     * @return - Todo todo переданная другому пользователю
     */
    @RequestMapping(method = {RequestMethod.POST}, value = "/transit")
    public ResponseEntity<?> transitTodoToAnotherUser(@RequestBody IdDto idDto){

        return todoService.transitTodoToAnotherUser(idDto.getTodo_id(), idDto.getUser_id());
    }



}

