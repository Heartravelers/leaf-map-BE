package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserAndFolder(User user, Folder folder);
    List<Note> findByUserAndRegionFilter(User user, RegionFilter regionFilter);
}
