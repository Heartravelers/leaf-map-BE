package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.NoteImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteImageRepository extends JpaRepository<NoteImage, Long> {
}
