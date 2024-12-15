package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.FolderRequestDto;
import leafmap.server.domain.note.dto.FolderResponseDto;
import leafmap.server.domain.note.dto.NoteDetailResponseDto;
import leafmap.server.domain.note.dto.NoteResponseDto;

import java.util.List;

public interface FolderService {
    //폴더 목록 조회
    List<FolderResponseDto> getFolder(Long myUserId, Long userId);
    //폴더 생성
    void makeFolder(Long userId, FolderRequestDto folderRequestDto);
    //폴더 수정
    void updateFolder(Long userId, Long folderId, FolderRequestDto folderRequestDto);
    //폴더 삭제
    void deleteFolder(Long userId, List<Long> folderIds);
    //지역 필터링
    List<NoteResponseDto> filterNotes(Long userId, String regionName);

}
