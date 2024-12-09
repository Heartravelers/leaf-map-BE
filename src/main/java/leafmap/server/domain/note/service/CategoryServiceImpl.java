package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.challenge.repository.CategoryChallengeRepository;
import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    CategoryChallengeRepository categoryChallengeRepository;
    NoteRepository noteRepository;
    RegionFilterRepository regionFilterRepository;

    @Autowired
    public CategoryServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository,
                               CategoryChallengeRepository categoryChallengeRepository, NoteRepository noteRepository,
                               RegionFilterRepository regionFilterRepository){
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.categoryChallengeRepository = categoryChallengeRepository;
        this.noteRepository = noteRepository;
        this.regionFilterRepository = regionFilterRepository;
    }

    @Override
    public List<CategoryDto> getCategory(Long userId){ //폴더 목록 조회
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        List<CategoryFilter> categoryFilters = categoryRepository.findByUser(optionalUser.get());

        return categoryFilters.stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
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

        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByUserAndCategoryFilter(optionalUser.get(), categoryFilter);
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
    public void updateCategory(Long userId, Long folderId, CategoryDto categoryDto){ //폴더 수정
        Optional<CategoryFilter> optionalCategory = categoryRepository.findById(folderId);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        if (!Objects.equals(userId, optionalCategory.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        CategoryFilter newCategory = optionalCategory.get().toBuilder()
                .name(categoryDto.getName()) // 수정할 필드만 변경
                .color(categoryDto.getColor()) // 수정할 필드만 변경
                .build(); // 새로운 객체를 빌더로 생성

        categoryRepository.save(newCategory);
    }

    @Override
    public void deleteCategory(Long userId, Long folderId){  //폴더 삭제
        Optional<CategoryFilter> optionalCategory = categoryRepository.findById(folderId);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundCategoryException(ErrorCode.NOT_FOUND);
        }
        if (!Objects.equals(userId, optionalCategory.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        categoryRepository.deleteById(folderId); //폴더와 함께 챌린지도 함께 삭제(Cascade.All 설정)

//        Optional<CategoryChallenge> optionalChallenge = categoryChallengeRepository.findByCategoryFilter(optionalCategory.get());
//        if (optionalChallenge.isEmpty()){
//            throw new CustomException.NotFoundChallengeException(ErrorCode.NOT_FOUND);
//        }
//
//        categoryChallengeRepository.deleteById(optionalChallenge.get().getId());
    }

    @Override
    public List<NoteDto> filterNotes(Long userId, String regionName){ //지역 필터링
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        RegionFilter regionFilter = regionFilterRepository.findByRegionName(regionName);
        List<Note> notes = noteRepository.findByUserAndRegionFilter(optionalUser.get(), regionFilter);

        return notes.stream()
                .map(NoteDto::new)
                .collect(Collectors.toList());
    }

}
