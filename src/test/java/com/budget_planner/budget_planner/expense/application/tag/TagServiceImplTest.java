package com.budget_planner.budget_planner.expense.application.tag;

import com.budget_planner.budget_planner.expense.api.dto.tag.CreateTagDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.UpdateTagDto;
import com.budget_planner.budget_planner.expense.domain.Tag;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.TagMapper;
import com.budget_planner.budget_planner.expense.persist.TagRepository;
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
@DisplayName("Tag service unit test")
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl service;

    private UUID userId;
    private User user;

    private UUID tagId;
    private Tag tag;

    private CreateTagDto createRequest;
    private UpdateTagDto updateRequest;
    private TagResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.tagId = UUID.randomUUID();

        this.user = new User("TestUser", "test@domain.com", "Test123.");
        this.tag = new Tag("TestTag", user);

        this.createRequest = new CreateTagDto("TestTag", userId);
        this.updateRequest = new UpdateTagDto("TestTagUpdated");
        this.response = new TagResponseDto(tagId, "TestTag", userId);
    }

    @Nested
    @DisplayName("Create tag tests")
    class CreateTagTests {

        @Test
        @DisplayName("Should create tag successfully")
        void shouldCreateTagSuccessfully () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.of(user));

            when(tagMapper.createRequestDtoToTag(createRequest, user))
                    .thenReturn(tag);

            when(tagRepository.save(any(Tag.class)))
                    .thenReturn(tag);

            when(tagMapper.tagToResponseDto(tag))
                    .thenReturn(response);

            var createdTag = service.createTag(createRequest);

            assertNotNull(createdTag);
            assertEquals(createdTag, response);

            verify(userRepository, times(1)).findById(createRequest.userId());
            verify(tagMapper, times(1)).createRequestDtoToTag(createRequest, user);
            verify(tagRepository, times(1)).save(any(Tag.class));
            verify(tagMapper, times(1)).tagToResponseDto(tag);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.createTag(createRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(createRequest.userId());
            verifyNoInteractions(tagMapper);
            verifyNoInteractions(tagRepository);
        }
    }

    @Nested
    @DisplayName("Delete tag tests")
    class DeleteTagTests {

        @Test
        @DisplayName("Should delete tag successfully")
        void shouldDeleteTagSuccessfully () {
            service.deleteTag(tagId);
            verify(tagRepository, times(1)).deleteById(tagId);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () {
            doThrow(new EmptyResultDataAccessException(1))
                    .when(tagRepository).deleteById(tagId);

            var exception = assertThrows(TagNotFoundException.class,
                    () -> service.deleteTag(tagId));

            assertNotNull(exception);
            assertEquals("Tag with id " + tagId + " was not found", exception.getMessage());

            verify(tagRepository, times(1)).deleteById(tagId);
        }
    }

    @Nested
    @DisplayName("Get tags by user tests")
    class GetTagsByUserTest {

        @Test
        @DisplayName("Should get tags list")
        void shouldGetTagsList () {
            var firstTag = new Tag("First Tag", user);
            var secondTag = new Tag("Second Tag", user);
            var thirdTag = new Tag("Third Tag", user);

            var firstResponse = new TagResponseDto(tagId, "First Response", userId);
            var secondResponse = new TagResponseDto(tagId, "Second Response", userId);
            var thirdResponse = new TagResponseDto(tagId, "Third Response", userId);

            var tagsList = List.of(firstTag, secondTag, thirdTag);
            var responseList = List.of(firstResponse, secondResponse, thirdResponse);

            when(tagRepository.findAllByUserId(userId))
                    .thenReturn(tagsList);

            when(tagMapper.tagToResponseDto(firstTag))
                    .thenReturn(firstResponse);

            when(tagMapper.tagToResponseDto(secondTag))
                    .thenReturn(secondResponse);

            when(tagMapper.tagToResponseDto(thirdTag))
                    .thenReturn(thirdResponse);

            var tags = service.getTagsByUser(userId);

            assertNotNull(tags);
            assertEquals(responseList.size(), tags.size());
            assertEquals(responseList, tags);

            verify(tagRepository, times(1)).findAllByUserId(userId);
            verify(tagMapper, times(tags.size())).tagToResponseDto(any(Tag.class));
        }

        @Test
        @DisplayName("Should get empty tags list")
        void shouldGetEmptyTagsList () {
            when(tagRepository.findAllByUserId(userId))
                    .thenReturn(List.of());

            var tags = service.getTagsByUser(userId);

            assertNotNull(tags);
            assertEquals(0, tags.size());
            assertEquals(List.of(), tags);

            verify(tagRepository, times(1)).findAllByUserId(userId);
            verifyNoInteractions(tagMapper);
        }
    }

    @Nested
    @DisplayName("Get tag by id tests")
    class GetTagByIdTests {

        @Test
        @DisplayName("Should get tag successfully")
        void shouldGetTagSuccessfully () {
            when(tagRepository.findById(tagId))
                    .thenReturn(Optional.of(tag));

            when(tagMapper.tagToResponseDto(tag))
                    .thenReturn(response);

            var gotTag = service.getTagById(tagId);

            assertNotNull(gotTag);
            assertEquals(gotTag, response);

            verify(tagRepository, times(1)).findById(tagId);
            verify(tagMapper, times(1)).tagToResponseDto(tag);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () {
            when(tagRepository.findById(tagId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(TagNotFoundException.class,
                    () -> service.getTagById(tagId));

            assertNotNull(exception);
            assertEquals("Tag with id " + tagId + " was not found", exception.getMessage());

            verify(tagRepository, times(1)).findById(tagId);
            verifyNoInteractions(tagMapper);
        }
    }

    @Nested
    @DisplayName("Update tag tests")
    class UpdateTagTests {

        @Test
        @DisplayName("Should update tag successfully")
        void shouldUpdateTagSuccessfully () {
            when(tagRepository.findById(tagId))
                    .thenReturn(Optional.of(tag));

            when(tagMapper.tagToResponseDto(tag))
                    .thenReturn(response);

            var updatedTag = service.updateTag(tagId, updateRequest);

            assertNotNull(updatedTag);
            assertEquals(updatedTag, response);

            verify(tagRepository, times(1)).findById(tagId);
            verify(tagMapper, times(1)).merge(tag, updateRequest);
            verify(tagMapper, times(1)).tagToResponseDto(tag);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () {
            when(tagRepository.findById(tagId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(TagNotFoundException.class,
                    () -> service.updateTag(tagId, updateRequest));

            assertNotNull(exception);
            assertEquals("Tag with id " + tagId + " was not found", exception.getMessage());

            verify(tagRepository, times(1)).findById(tagId);
            verifyNoInteractions(tagMapper);
        }
    }
}