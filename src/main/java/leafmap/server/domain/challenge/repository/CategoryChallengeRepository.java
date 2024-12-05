package leafmap.server.domain.challenge.repository;

import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryChallengeRepository extends JpaRepository<CategoryChallenge, Long> {
    Optional<CategoryChallenge> findByUserAndCategoryFilter(User user, CategoryFilter categoryFilter);
    Optional<CategoryChallenge> findByCategoryFilter(CategoryFilter categoryFilter);
}
