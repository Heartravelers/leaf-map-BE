package leafmap.server.domain.note.repository;

import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByUserAndId(User user, Long folderId);
    Optional<Folder> findByUserAndName(User user, String folderName);
    List<Folder> findByUser(User user);

}
