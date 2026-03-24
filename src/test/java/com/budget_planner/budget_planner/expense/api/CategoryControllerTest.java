package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;
import com.budget_planner.budget_planner.expense.application.category.CategoryService;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
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

@WebMvcTest(CategoryController.class)
@DisplayName("Category controller unit tests")
class CategoryControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    private UUID userId;
    private UUID categoryId;

    private CreateCategoryDto createRequest;
    private UpdateCategoryDto updateRequest;
    private CategoryResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.categoryId = UUID.randomUUID();

        this.createRequest = new CreateCategoryDto("TestCategory", Color.BLUE, userId);
        this.updateRequest = new UpdateCategoryDto("TestCategoryUpdated", null);
        this.response = new CategoryResponseDto(categoryId, "TestCategory", Color.BLUE.getHex(), userId);
    }

    @Nested
    @DisplayName("Create category tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully () throws Exception {
            when(service.createCategory(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/categories/" + response.id();

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.color").value(response.color()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).createCategory(createRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .createCategory(createRequest);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createCategory(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateCategoryDto(null, null, null);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete category tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteCategory(categoryId);

            mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteCategory(categoryId);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () throws Exception {
            doThrow(CategoryNotFoundException.class)
                    .when(service)
                    .deleteCategory(categoryId);

            mockMvc.perform(delete("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteCategory(categoryId);
        }
    }

    @Nested
    @DisplayName("Get categories by user id tests")
    class GetCategoriesByUserTests {

        @Test
        @DisplayName("Should get categories list")
        void shouldGetCategoryList () throws Exception {
            when(service.getCategoriesByUser(userId))
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/v1/categories/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(service, times(1)).getCategoriesByUser(userId);
        }

        @Test
        @DisplayName("Should get empty categories list")
        void shouldGetEmptyCategoriesList () throws Exception {
            when(service.getCategoriesByUser(userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/v1/categories/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(service, times(1)).getCategoriesByUser(userId);
        }
    }

    @Nested
    @DisplayName("Get category by id tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should get category successfully")
        void shouldGetCategorySuccessfully () throws Exception {
            when(service.getCategoryById(categoryId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.color").value(response.color()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).getCategoryById(categoryId);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () throws Exception {
            doThrow(CategoryNotFoundException.class)
                    .when(service)
                    .getCategoryById(categoryId);

            mockMvc.perform(get("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getCategoryById(categoryId);
        }
    }

    @Nested
    @DisplayName("Update category tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully () throws Exception {
            when(service.updateCategory(categoryId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.color").value(response.color()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).updateCategory(categoryId, updateRequest);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () throws Exception {
            doThrow(CategoryNotFoundException.class)
                    .when(service)
                    .updateCategory(categoryId, updateRequest);

            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateCategory(categoryId, updateRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badUpdateRequest = new UpdateCategoryDto(null, null);

            mockMvc.perform(put("/api/v1/categories/{id}", categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badUpdateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}