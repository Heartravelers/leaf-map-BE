package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryFilter, Long> {
    Optional<CategoryFilter> findByName(String categoryName);
    List<CategoryDto> findByUserId(Long userId);

}
