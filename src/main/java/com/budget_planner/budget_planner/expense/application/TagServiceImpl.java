package com.budget_planner.budget_planner.expense.application;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.TagMapper;
import com.budget_planner.budget_planner.expense.persist.TagRepository;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TagMapper tagMapper;

    TagServiceImpl (TagRepository tagRepository, UserRepository userRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponseDto createTag(CreateTagDto request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        var createdTag = tagRepository.save(tagMapper.createRequestDtoToTag(request, user));
        return tagMapper.tagToResponseDto(createdTag);
    }

    @Override
    public void deleteTag(UUID id) {
        try {
            tagRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException exception) {
            throw new TagNotFoundException(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDto> getTagsByUser(UUID userId) {
        return tagRepository.findAllByUserId(userId)
                .stream()
                .map(tagMapper::tagToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TagResponseDto getTagById(UUID id) {
        var tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        return tagMapper.tagToResponseDto(tag);
    }

    @Override
    public TagResponseDto updateTag(UUID id, UpdateTagDto request) {
        var tagToBeUpdated = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));

        if (request.name() != null && !request.name().isBlank())
            tagToBeUpdated.setName(request.name());

        return tagMapper.tagToResponseDto(tagToBeUpdated);
    }
}
