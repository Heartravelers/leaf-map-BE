package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
}
