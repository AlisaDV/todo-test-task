package ru.fateev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.fateev.model.Todo;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUserId(Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM users_todos WHERE todos_id = :todosId")
    void removeByUserIdNative(@Param("todosId") Long todosId);
}
