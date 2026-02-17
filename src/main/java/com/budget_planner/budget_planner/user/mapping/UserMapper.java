package com.budget_planner.budget_planner.user.mapping;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.api.dto.user_settings.UserSettingsResponseDto;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

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
}
