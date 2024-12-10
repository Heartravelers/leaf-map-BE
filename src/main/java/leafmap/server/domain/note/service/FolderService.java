package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.FolderDto;
import leafmap.server.domain.note.dto.NoteDto;

import java.util.List;

public interface FolderService {
    List<FolderDto> getFolder(Long userId);
    void makeFolder(Long userId, FolderDto folderDto);
    void updateFolder(Long userId, Long folderId, FolderDto folderDto);
    void deleteFolder(Long userId, Long folderId);
    List<NoteDto> filterNotes(Long userId, String regionName);

}
