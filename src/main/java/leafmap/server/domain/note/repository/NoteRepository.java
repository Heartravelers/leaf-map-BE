package leafmap.server.domain.note.repository;

import io.lettuce.core.dynamic.annotation.Param;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserAndCategoryFilter(User user, CategoryFilter categoryFilter);
//    List<NoteDto> findByUserIdAndCategoryFilter(Long userId, CategoryFilter categoryFilter);
    List<NoteDto> findByUserAndRegionFilter(User user, RegionFilter regionFilter);
}
