package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<User> findByNoteId(Long noteId);
    List<NoteDto> findByUserIdAndCategory(Long userId, String categoryName);
    List<NoteDto> findByUserIdAndRegionName(Long userId, String regionName);
}
