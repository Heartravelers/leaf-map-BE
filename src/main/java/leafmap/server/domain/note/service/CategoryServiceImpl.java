package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.entity.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    UserRepository userRepository;
    CategoryRepository categoryRepository;

    public CategoryServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository){
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public void makeOwnCategory(Long userId, CategoryDto categoryDto){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }

        CategoryFilter categoryFilter = CategoryFilter.builder()
                .name(categoryDto.getName())
                .color(categoryDto.getColor())
                .user(optionalUser.get())
                .isDefault(Boolean.FALSE).build();

        //**해당 카테고리 생기면서 같은 카테고리 이름의 categoryChallenge 도 생겨야 할지

        categoryRepository.save(categoryFilter);
    }

}
