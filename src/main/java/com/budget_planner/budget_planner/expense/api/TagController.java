package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;
import com.budget_planner.budget_planner.expense.application.TagService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService service;

    TagController (TagService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TagResponseDto> createTag (@Valid @RequestBody CreateTagDto request) {
        var createdTag = service.createTag(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTag.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag (@PathVariable UUID id) {
        service.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TagResponseDto>> getTagsByUser (@PathVariable UUID id) {
        var tags = service.getTagsByUser(id);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById (@PathVariable UUID id) {
        var tag = service.getTagById(id);
        return ResponseEntity.ok(tag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag (@PathVariable UUID id, @Valid @RequestBody UpdateTagDto request) {
        var updatedTag = service.updateTag(id, request);
        return ResponseEntity.ok(updatedTag);
    }
}
