package com.budget_planner.budget_planner.expense.mapping;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;
import com.budget_planner.budget_planner.expense.domain.Tag;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag createRequestDtoToTag (CreateTagDto dto, User user) { return new Tag(dto.name(), user); }

    public TagResponseDto tagToResponseDto (Tag tag) {
        return new TagResponseDto(tag.getId(), tag.getName(), tag.getUser().getId());
    }

    public void merge (Tag tag, UpdateTagDto request) {
        if (request.name() != null && !request.name().isBlank())
            tag.setName(request.name());
    }
}
