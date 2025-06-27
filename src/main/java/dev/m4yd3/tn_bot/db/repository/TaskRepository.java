package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Optional<Task> findByValueAndType(String value, Task.Type type);

    @Query("select t from Task t where t.type = 0 or t.type = 1")
    List<Task> findAllRegistrationTasks();

    @Query("select t from Task t where t.type = 2")
    List<Task> findAllFiringTasks();
}
