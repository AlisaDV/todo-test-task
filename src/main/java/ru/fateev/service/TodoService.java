package ru.fateev.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.fateev.dto.TodoDto;
import ru.fateev.exception.AppError;
import ru.fateev.model.Todo;
import ru.fateev.model.User;
import ru.fateev.repository.TodoRepository;
import ru.fateev.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }



    public Todo getTodo(Long id) {
        return todoRepository.findById(id).orElse(null);
    }
    public ResponseEntity<?> saveTodo(Todo todo) {
         return ResponseEntity.ok(todoRepository.save(todo));
    }

    public ResponseEntity<?> addTodo(TodoDto todoDto, String username) {
        Optional<User> author = userRepository.findByUsername(username);
        if(author.isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь не существует"), HttpStatus.BAD_REQUEST);
        }
        Todo newTodo = new Todo(todoDto.getTitle(), todoDto.getDescription(), todoDto.getDeadline());
        newTodo.setUserId(author.get().getId());
        author.get().getTodos().add(newTodo);
        userRepository.save(author.get());
        todoRepository.save(newTodo);
        return ResponseEntity.ok(newTodo);
    }

    public ResponseEntity<?> updateTodo(Long id, Todo todo) {
        todo.setId(id);
        todoRepository.save(todo);
        return ResponseEntity.ok(todo);
    }

    public ResponseEntity<?> deleteTodo(Long id) {
        todoRepository.removeByUserIdNative(id);
        todoRepository.delete(getTodo(id));
        return ResponseEntity.ok("Задача удалена");
    }

    public Page<Todo> getTodosByUser(String username, Pageable pageable){
        return todoRepository.findByUserId(userRepository.findByUsername(username).get().getId(), pageable);
    }


    /**
     * Передача задачи другому пользователю
     * @param todo_id - id задачи
     * @param author_id - id пользователя
     * @return
     */
    public ResponseEntity<?> transitTodoToAnotherUser(Long todo_id, Long author_id) {
        Optional<Todo> todo = todoRepository.findById(todo_id);
        Optional<User> newAuthor = userRepository.findById(author_id);
        Optional<User> oldAuthor = userRepository.findById(todo.get().getUserId());
        if(newAuthor.isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь не существует"), HttpStatus.BAD_REQUEST);
        }
        if(oldAuthor.isEmpty()){
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь не существует"), HttpStatus.BAD_REQUEST);
        }

        oldAuthor.get().getTodos().remove(todo.get());
        todoRepository.removeByUserIdNative(oldAuthor.get().getId());
        todo.get().setUserId(newAuthor.get().getId());
        newAuthor.get().getTodos().add(todo.get());
        userRepository.save(oldAuthor.get());
        userRepository.save(newAuthor.get());
        todoRepository.save(todo.get());
        return ResponseEntity.ok(todo.get());
    }


    /**
     * Вспомогательный инструмент для работы с PATCH-методом
     * @param id - id задачи
     * @param jsonPatch - вспомогательная утилита
     * @return - обновлённый Todo todo
     * @throws JsonPatchException
     * @throws JsonProcessingException
     */
    public Todo patch(Long id, JsonPatch jsonPatch) throws JsonPatchException, JsonProcessingException {
        Todo todo = todoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(todo, JsonNode.class));

        return todoRepository.save(objectMapper.treeToValue(patched, Todo.class));
    }


}
