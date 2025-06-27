package dev.m4yd3.tn_bot.db.repository;

import dev.m4yd3.tn_bot.db.entity.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Long> {
}
