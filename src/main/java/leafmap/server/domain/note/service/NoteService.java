package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.ListDto;
import leafmap.server.domain.note.dto.NoteRequestDto;
import leafmap.server.domain.note.dto.NoteDto;

import java.util.List;

public interface NoteService {
    //노트 상세 조회
    NoteDto getMyNote(Long noteId);
    NoteDto getUserNote(Long noteId);

    //노트 생성
    void postNote(Long userID, NoteDto noteDto);
    //노트 수정
    void updateNote(Long noteId, NoteDto noteDto);
    //노트 삭제
    void deleteNote(Long noteId);
    //폴더 내 노트목록 조회(본인, 타사용자)
    List<NoteDto> getList(Long userId, String categoryName);
}
