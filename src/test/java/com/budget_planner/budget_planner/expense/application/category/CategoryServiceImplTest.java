package com.budget_planner.budget_planner.expense.application.category;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.CategoryMapper;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.user.domain.User;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category service unit test")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl service;

    private UUID userId;
    private User user;

    private UUID categoryId;
    private Category category;

    private CreateCategoryDto createRequest;
    private UpdateCategoryDto updateRequest;
    private CategoryResponseDto response;

    @BeforeEach
    void setup () {
        this.userId = UUID.randomUUID();
        this.categoryId = UUID.randomUUID();

        this.user = new User("TestUser", "test@domain.com", "Test123.");
        this.category = new Category("TestCategory", Color.BLUE, user);

        this.createRequest = new CreateCategoryDto("TestCategory", Color.BLUE, userId);
        this.updateRequest = new UpdateCategoryDto("TestCategoryUpdated", null);
        this.response = new CategoryResponseDto(categoryId, "TestCategory", Color.BLUE.getHex(), userId);
    }

    @Nested
    @DisplayName("Create category tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.of(user));

            when(categoryMapper.createCategoryDtoToEntity(createRequest, user))
                    .thenReturn(category);

            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(category);

            when(categoryMapper.categoryToResponseDto(category))
                    .thenReturn(response);

            var createdCategory = service.createCategory(createRequest);

            assertNotNull(createdCategory);
            assertEquals(createdCategory, response);

            verify(userRepository, times(1)).findById(createRequest.userId());
            verify(categoryMapper, times(1)).createCategoryDtoToEntity(createRequest, user);
            verify(categoryRepository, times(1)).save(any(Category.class));
            verify(categoryMapper, times(1)).categoryToResponseDto(category);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.createCategory(createRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(createRequest.userId());
            verifyNoInteractions(categoryMapper);
            verifyNoInteractions(categoryRepository);
        }
    }

    @Nested
    @DisplayName("Delete category tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully () {
            service.deleteCategory(categoryId);
            verify(categoryRepository, times(1)).deleteById(categoryId);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () {
            doThrow(new EmptyResultDataAccessException(1))
                    .when(categoryRepository).deleteById(categoryId);

            var exception = assertThrows(CategoryNotFoundException.class,
                    () -> service.deleteCategory(categoryId));

            assertNotNull(exception);
            assertEquals("Category with id " + categoryId + " was not found", exception.getMessage());

            verify(categoryRepository, times(1)).deleteById(categoryId);
        }
    }

    @Nested
    @DisplayName("Get categories by user tests")
    class GetCategoriesByUserTests {

        @Test
        @DisplayName("Should get categories list")
        void shouldGetCategoriesList () {
            var firstCategory = new Category("First Category", Color.RED, user);
            var secondCategory = new Category("Second Category", Color.GREEN, user);
            var thirdCategory = new Category("Third Category", Color.BLACK, user);

            var firstResponse = new CategoryResponseDto(categoryId, "First Response", Color.RED.getHex(), userId);
            var secondResponse = new CategoryResponseDto(categoryId, "Second Response", Color.GREEN.getHex(), userId);
            var thirdResponse = new CategoryResponseDto(categoryId, "Third Response", Color.BLACK.getHex(), userId);

            var categoryList = List.of(firstCategory, secondCategory, thirdCategory);
            var responseList = List.of(firstResponse, secondResponse, thirdResponse);

            when(categoryRepository.findAllByUserId(userId))
                    .thenReturn(categoryList);

            when(categoryMapper.categoryToResponseDto(firstCategory))
                    .thenReturn(firstResponse);

            when(categoryMapper.categoryToResponseDto(secondCategory))
                    .thenReturn(secondResponse);

            when(categoryMapper.categoryToResponseDto(thirdCategory))
                    .thenReturn(thirdResponse);

            var categories = service.getCategoriesByUser(userId);

            assertNotNull(categories);
            assertEquals(responseList.size(), categories.size());
            assertEquals(responseList, categories);

            verify(categoryRepository, times(1)).findAllByUserId(userId);
            verify(categoryMapper, times(categories.size())).categoryToResponseDto(any(Category.class));
        }

        @Test
        @DisplayName("Should get empty categories list")
        void shouldGetEmptyCategoriesList () {
            when(categoryRepository.findAllByUserId(userId))
                    .thenReturn(List.of());

            var categories = service.getCategoriesByUser(userId);

            assertNotNull(categories);
            assertEquals(0, categories.size());
            assertEquals(List.of(), categories);

            verify(categoryRepository, times(1)).findAllByUserId(userId);
            verifyNoInteractions(categoryMapper);
        }
    }

    @Nested
    @DisplayName("Get category by id tests")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should get category successfully")
        void shouldGetCategorySuccessfully () {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(categoryMapper.categoryToResponseDto(category))
                    .thenReturn(response);

            var gotCategory = service.getCategoryById(categoryId);

            assertNotNull(gotCategory);
            assertEquals(gotCategory, response);

            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryMapper, times(1)).categoryToResponseDto(category);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(CategoryNotFoundException.class,
                    () -> service.getCategoryById(categoryId));

            assertNotNull(exception);
            assertEquals("Category with id " + categoryId + " was not found", exception.getMessage());

            verify(categoryRepository, times(1)).findById(categoryId);
            verifyNoInteractions(categoryMapper);
        }
    }

    @Nested
    @DisplayName("Update category tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully () {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(categoryMapper.categoryToResponseDto(category))
                    .thenReturn(response);

            var updatedCategory = service.updateCategory(categoryId, updateRequest);

            assertNotNull(updatedCategory);
            assertEquals(updatedCategory, response);

            verify(categoryRepository, times(1)).findById(categoryId);
            verify(categoryMapper, times(1)).merge(category, updateRequest);
            verify(categoryMapper, times(1)).categoryToResponseDto(category);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () {
            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(CategoryNotFoundException.class,
                    () -> service.updateCategory(categoryId, updateRequest));

            assertNotNull(exception);
            assertEquals("Category with id " + categoryId + " was not found", exception.getMessage());

            verify(categoryRepository, times(1)).findById(categoryId);
            verifyNoInteractions(categoryMapper);
        }
    }
}