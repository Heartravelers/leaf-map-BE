package leafmap.server.domain.user.repository;

import leafmap.server.domain.user.entity.Follow;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

}
