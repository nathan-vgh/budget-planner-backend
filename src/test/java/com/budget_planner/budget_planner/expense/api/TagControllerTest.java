package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;
import com.budget_planner.budget_planner.expense.application.tag.TagService;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@DisplayName("Tag controller unit tests")
class TagControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService service;

    private UUID userId;
    private UUID tagId;

    private CreateTagDto createRequest;
    private UpdateTagDto updateRequest;
    private TagResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.tagId = UUID.randomUUID();

        this.createRequest = new CreateTagDto("TestTag", userId);
        this.updateRequest = new UpdateTagDto("TestTagUpdated");
        this.response = new TagResponseDto(tagId, "TestTag", userId);
    }

    @Nested
    @DisplayName("Create tag tests")
    class CreateTagTests {

        @Test
        @DisplayName("Should create tag successfully")
        void shouldCreateTagSuccessfully () throws Exception {
            when(service.createTag(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/tags/" + response.id();

            mockMvc.perform(post("/api/v1/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).createTag(createRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .createTag(createRequest);

            mockMvc.perform(post("/api/v1/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createTag(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateTagDto(null, null);

            mockMvc.perform(post("/api/v1/tags")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete tag tests")
    class DeleteTagTests {

        @Test
        @DisplayName("Should delete tag successfully")
        void shouldDeleteTagSuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteTag(tagId);

            mockMvc.perform(delete("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteTag(tagId);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () throws Exception {
            doThrow(TagNotFoundException.class)
                    .when(service)
                    .deleteTag(tagId);

            mockMvc.perform(delete("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteTag(tagId);
        }
    }

    @Nested
    @DisplayName("Get tags by user id tests")
    class GetTagsByUserTests {

        @Test
        @DisplayName("Should get tags list")
        void shouldGetTagsList () throws Exception {
            when(service.getTagsByUser(userId))
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/v1/tags/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(service, times(1)).getTagsByUser(userId);
        }

        @Test
        @DisplayName("Should get empty tags list")
        void shouldGetEmptyTagsList () throws Exception {
            when(service.getTagsByUser(userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/v1/tags/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(service, times(1)).getTagsByUser(userId);
        }
    }

    @Nested
    @DisplayName("Get tag by id tests")
    class GetTagByIdTests {

        @Test
        @DisplayName("Should get tag successfully")
        void shouldGetTagSuccessfully () throws Exception {
            when(service.getTagById(tagId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).getTagById(tagId);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () throws Exception {
            doThrow(TagNotFoundException.class)
                    .when(service)
                    .getTagById(tagId);

            mockMvc.perform(get("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getTagById(tagId);
        }
    }

    @Nested
    @DisplayName("Update tag tests")
    class UpdateTagTests {

        @Test
        @DisplayName("Should update tag successfully")
        void shouldUpdateTagSuccessfully () throws Exception {
            when(service.updateTag(tagId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).updateTag(tagId, updateRequest);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () throws Exception {
            doThrow(TagNotFoundException.class)
                    .when(service)
                    .updateTag(tagId, updateRequest);

            mockMvc.perform(put("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateTag(tagId, updateRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badUpdateRequest = new UpdateTagDto(null);

            mockMvc.perform(put("/api/v1/tags/{id}", tagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badUpdateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}