package com.budget_planner.budget_planner.user.mapping;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.api.dto.user_settings.UserSettingsResponseDto;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Locale;

@Component
public class UserMapper {

    public User createRequestDtoToUser(CreateUserDto dto) {
        return new User(dto.name(), dto.email(), dto.password());
    }

    public UserResponseDto userToResponseDto (User user) {
        var settings = user.getSettings();

        var settingsDto = new UserSettingsResponseDto(
                settings.getCurrency().getCurrencyCode(),
                settings.getWeekStartDay(),
                settings.getLanguage().toLanguageTag(),
                settings.getTheme()
        );

        return new UserResponseDto(user.getId(), user.getName(), user.getEmail(), settingsDto);
    }

    public void merge (User user, UpdateUserDto request) {
        if (request.name() != null && !request.name().isBlank())
            user.setName(request.name());

        if (request.email() != null && !request.email().isBlank())
            user.setEmail(request.email());

        if (request.settings() != null) {
            var settingsDto = request.settings();

            var currency = settingsDto.currency() != null
                    ? Currency.getInstance(settingsDto.currency())
                    : null;

            var locale = settingsDto.language() != null
                    ? Locale.forLanguageTag(settingsDto.language())
                    : null;

            user.setSettings(
                    currency,
                    settingsDto.weekStartDay(),
                    locale,
                    settingsDto.theme()
            );
        }
    }
}
