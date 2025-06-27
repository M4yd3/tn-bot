package dev.m4yd3.tn_bot.service.impl;

import dev.m4yd3.tn_bot.core.TelegramClientWrapper;
import dev.m4yd3.tn_bot.db.entity.Registration;
import dev.m4yd3.tn_bot.db.entity.User;
import dev.m4yd3.tn_bot.db.repository.DepartmentRepository;
import dev.m4yd3.tn_bot.db.repository.RegistrationRepository;
import dev.m4yd3.tn_bot.db.repository.UserRepository;
import dev.m4yd3.tn_bot.service.EmployeeService;
import dev.m4yd3.tn_bot.service.RegistrationService;
import dev.m4yd3.tn_bot.service.SettingService;
import dev.m4yd3.tn_bot.util.Responses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final RegistrationRepository registrationRepository;
    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SettingService settingService;
    private final TelegramClientWrapper client;
    private final Map<Long, Registration> cache = new HashMap<>();

    private static String generateCode() {
        return String.valueOf((int) (Math.random() * 999999) + 1000000).substring(1);
    }

    @Override
    public boolean isUserRegistered(final Long telegramId) {
        return userRepository.existsByTelegramIdAndIsActiveTrue(telegramId);
    }

    @Override
    public void handleRegistrationFlow(final Message message) {
        final Long telegramId = message.getFrom().getId();
        final String text = message.getText().trim();
        final Registration registration = checkTimers(getRegistration(telegramId));

        final String responseText = switch (registration.getState()) {
            case READY -> processReadyState(registration);
            case EMAIL -> processEmailState(registration, text, message.getFrom().getUserName());
            case CODE -> processCodeState(registration, text);
            case BLOCKED -> processBlockedState(registration);
        };

        final var response = new SendMessage(String.valueOf(message.getChatId()), responseText);
        client.sendMessage(response);
    }

    private Registration checkTimers(final Registration registration) {
        final var unblockTime = getRegistrationUnblockTime(registration);
        final var resetTime = registration.getFirstAttemptAt()
                .plus(settingService.getRegistrationLimitsResetDuration());
        boolean save = false;

        if (registration.getState() == Registration.State.BLOCKED && Instant.now().isAfter(unblockTime)) {
            registration.setState(Registration.State.READY);
            save = true;
        }

        if (registration.getState() != Registration.State.BLOCKED && Instant.now().isAfter(resetTime)) {
            registration.reset();
            save = true;
        }

        if (save) saveRegistration(registration);

        return registration;
    }

    private String processReadyState(final Registration registration) {
        registration.setState(Registration.State.EMAIL);
        saveRegistration(registration);

        return Responses.ENTER_EMAIL;
    }

    private String processEmailState(final Registration registration, final String input, final String username) {
        registration.incrementEmailAttempts();
        final var isLastAttempt = registration.getEmailAttempts() >= settingService.getRegistrationEmailAttemptsLimit();

        final var pattern = settingService.getRegistrationEmailPattern().matcher(input);
        if (!pattern.matches()) {
            if (isLastAttempt) return blockEmailAttempts(registration);

            saveRegistration(registration);
            return Responses.INVALID_EMAIL_TRY_AGAIN;
        }

        final var isEmailTaken = userRepository.existsByEmailAndIsActiveTrue(input);
        if (isEmailTaken) {
            if (isLastAttempt) return blockEmailAttempts(registration);

            saveRegistration(registration);
            return Responses.USER_EXISTS;
        }

        final var employee = employeeService.getEmployeeByEmail(input);
        if (employee.isEmpty()) {
            if (isLastAttempt) return blockEmailAttempts(registration);

            saveRegistration(registration);
            return Responses.INVALID_EMAIL_TRY_AGAIN;
        }

        final var userId = userRepository.findUserIdByTelegramId(registration.getTelegramId()).map(User::getId);

        final User user = employee.get().toUser();
        userId.ifPresent(user::setId);
        user.setTelegramId(registration.getTelegramId());
        user.setUserName(username);
        departmentRepository.save(user.getDepartment());
        userRepository.save(user);

        final String code = generateCode();
        registration.setEmail(input);
        registration.setCode(code);
        registration.setState(Registration.State.CODE);
        saveRegistration(registration);

        // TODO: send email with code
        log.info(code);

        return Responses.ENTER_CODE;
    }

    private String processCodeState(final Registration registration, final String input) {
        registration.incrementCodeAttempts();
        final var isLastAttempt = registration.getCodeAttempts() >= settingService.getRegistrationCodeAttemptsLimit();

        if (!input.equals(registration.getCode())) {
            if (isLastAttempt) return blockCodeAttempts(registration);

            saveRegistration(registration);
            return Responses.INVALID_CODE_TRY_AGAIN;
        }

        final var userOptional = userRepository.findByEmailAndTelegramId(
                registration.getEmail(),
                registration.getTelegramId()
        );

        if (userOptional.isEmpty()) return Responses.ERROR;

        final var user = userOptional.get();
        user.setIsActive(true);
        user.setRegisteredAt(Instant.now());
        userRepository.save(user);
        deleteRegistration(registration);

        return Responses.SUCCESS;
    }

    private String processBlockedState(final Registration registration) {
        return Responses.blocked(getRegistrationUnblockTime(registration));
    }

    private Instant getRegistrationUnblockTime(final Registration registration) {
        if (registration.getBlockedAt() == null) return Instant.now().minusSeconds(1000);

        return registration.getBlockedAt().plus(settingService.getRegistrationBlockDuration());
    }

    private String blockEmailAttempts(final Registration registration) {
        registration.setState(Registration.State.BLOCKED);
        registration.setBlockedAt(Instant.now());
        saveRegistration(registration);
        return Responses.emailLimitExceeded(getRegistrationUnblockTime(registration));
    }

    private String blockCodeAttempts(final Registration registration) {
        registration.setState(Registration.State.BLOCKED);
        registration.setBlockedAt(Instant.now());
        saveRegistration(registration);
        return Responses.codeLimitExceeded(getRegistrationUnblockTime(registration));
    }

    private Registration getRegistration(final Long telegramId) {
        if (cache.containsKey(telegramId)) return cache.get(telegramId);

        final var registration = registrationRepository.findByTelegramId(telegramId);
        return registration.orElse(new Registration(telegramId));
    }

    private void saveRegistration(final Registration registration) {
        cache.put(registration.getTelegramId(), registration);
        registrationRepository.save(registration);
    }

    private void deleteRegistration(final Registration registration) {
        cache.remove(registration.getTelegramId());
        registrationRepository.delete(registration);
    }
}