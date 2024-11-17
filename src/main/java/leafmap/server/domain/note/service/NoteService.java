package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.NoteRequestDto;
import leafmap.server.domain.note.dto.NoteDto;

public interface NoteService {
    //노트 상세 조회
    NoteDto getMyNote(Long noteId);
    NoteDto getUserNote(Long noteId);

    //노트 생성
    void postNote(NoteDto noteDto);
    //노트 수정
    void updateNote(Long noteId, NoteDto noteDto);
    //노트 삭제
    void deleteNote(Long noteId);
}
