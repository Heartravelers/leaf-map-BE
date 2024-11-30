package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.Scrap;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    List<Scrap> findAllByUser(User user);
}
