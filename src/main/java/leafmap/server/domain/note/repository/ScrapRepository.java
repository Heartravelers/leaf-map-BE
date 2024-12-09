package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.Scrap;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<Scrap> findByNoteIdAndUser(Long noteId, User user);
}
