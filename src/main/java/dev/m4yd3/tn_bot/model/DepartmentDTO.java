package dev.m4yd3.tn_bot.model;

import dev.m4yd3.tn_bot.db.entity.Department;

public record DepartmentDTO(Long id, String fullName, String shortName) {
    public Department toDepartment() {
        final Department department = new Department();
        department.setId(id());
        department.setFullName(fullName());
        department.setShortName(shortName());

        return department;
    }
}
