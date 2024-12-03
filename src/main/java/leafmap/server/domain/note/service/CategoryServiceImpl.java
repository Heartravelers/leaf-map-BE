package leafmap.server.domain.note.service;

import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.challenge.repository.CategoryChallengeRepository;
import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    CategoryChallengeRepository categoryChallengeRepository;
    NoteRepository noteRepository;

    public CategoryServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository,
                               CategoryChallengeRepository categoryChallengeRepository, NoteRepository noteRepository){
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.categoryChallengeRepository = categoryChallengeRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public List<CategoryDto> getCategory(Long userId){ //폴더 목록 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        return categoryRepository.findByUserId(userId);
    }

    @Override
    public void makeCategory(Long userId, CategoryDto categoryDto){ //폴더 생성
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        CategoryFilter categoryFilter = CategoryFilter.builder()
                .name(categoryDto.getName())
                .color(categoryDto.getColor())
                .user(optionalUser.get()).build();

        categoryRepository.save(categoryFilter);

        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByUserAndCategory(userId, categoryFilter.getId());
        if (optionalChallenge.isPresent()){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST); //이미 존재
        }

        CategoryChallenge categoryChallenge = CategoryChallenge.builder()
                .user(optionalUser.get())
                .categoryFilter(categoryFilter)
                .countStamp(0).build();

        categoryChallengeRepository.save(categoryChallenge);
    }

    @Override
    public void updateCategory(Long folderId, CategoryDto categoryDto){ //폴더 수정
        Optional<CategoryFilter> optionalCategory = categoryRepository.findById(folderId);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        CategoryFilter category = CategoryFilter.builder()
                .name(categoryDto.getName())
                .color(categoryDto.getColor()).build();
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long folderId){
        Optional<CategoryFilter> optionalCategory = categoryRepository.findById(folderId);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundCategoryException(ErrorCode.NOT_FOUND);
        }
        categoryRepository.deleteById(folderId);

        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByCategoryId(folderId);
        if (optionalChallenge.isEmpty()){
            throw new CustomException.NotFoundChallengeException(ErrorCode.NOT_FOUND);
        }
        categoryChallengeRepository.deleteById(optionalChallenge.get().getId());
    }

    @Override
    public List<NoteDto> filterNotes(Long userId, String regionName){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        return noteRepository.findByUserIdAndRegionName(userId, regionName);
    }

}
