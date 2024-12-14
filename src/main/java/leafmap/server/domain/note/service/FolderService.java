package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.FolderRequestDto;
import leafmap.server.domain.note.dto.FolderResponseDto;
import leafmap.server.domain.note.dto.NoteDetailResponseDto;

import java.util.List;

public interface FolderService {
    List<FolderResponseDto> getFolder(Long myUserId, Long userId);
    void makeFolder(Long userId, FolderRequestDto folderRequestDto);
    void updateFolder(Long userId, Long folderId, FolderRequestDto folderRequestDto);
    void deleteFolder(Long userId, Long folderId);
    List<NoteDetailResponseDto> filterNotes(Long userId, String regionName);

}
