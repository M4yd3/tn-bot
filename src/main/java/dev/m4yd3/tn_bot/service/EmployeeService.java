package dev.m4yd3.tn_bot.service;

import dev.m4yd3.tn_bot.model.EmployeeDTO;

import java.util.Optional;

public interface EmployeeService {
    public Optional<EmployeeDTO> getEmployeeByEmail(String email);
}
