package dev.m4yd3.tn_bot.model;

import dev.m4yd3.tn_bot.db.entity.User;

public record EmployeeDTO(String firstName, String lastName, String middleName, String email, DepartmentDTO orgUnit) {
    public User toUser() {
        final User user = new User();
        user.setDepartment(orgUnit.toDepartment());
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);

        return user;
    }
}
