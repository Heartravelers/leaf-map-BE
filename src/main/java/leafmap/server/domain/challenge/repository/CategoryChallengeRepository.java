package leafmap.server.domain.challenge.repository;

import leafmap.server.domain.challenge.entity.CategoryChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryChallengeRepository extends JpaRepository<CategoryChallenge, Long> {
    Optional<CategoryChallenge> findByUserAndCategory(Long userId, Long categoryId);
    Optional<CategoryChallenge> findByCategoryId(Long categoryId);
}
