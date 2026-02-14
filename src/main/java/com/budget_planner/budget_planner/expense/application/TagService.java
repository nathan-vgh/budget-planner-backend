package com.budget_planner.budget_planner.expense.application;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;

import java.util.List;
import java.util.UUID;

public interface TagService {
    TagResponseDto createTag(CreateTagDto request);
    void deleteTag(UUID id);
    List<TagResponseDto> getTagsByUser(UUID userId);
    TagResponseDto getTagById(UUID id);
    TagResponseDto updateTag(UUID id, UpdateTagDto request);
}
