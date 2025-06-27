package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.model.DepartmentDTO;
import dev.m4yd3.tn_bot.model.EmployeeDTO;
import dev.m4yd3.tn_bot.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeMockServiceImpl implements EmployeeService {
    public Optional<EmployeeDTO> getEmployeeByEmail(final String email) {
        if (!Objects.equals(email, "test@transneft.ru")) return Optional.empty();

        final var orgUnit = new DepartmentDTO(1L, "Test org unit", "TOU");

        final var employee = new EmployeeDTO("Ivan", "Ivanov", "Ivanovich", "test@transneft.ru", orgUnit);

        return Optional.of(employee);
    }
}
