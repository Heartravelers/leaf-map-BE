package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.NoteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoteService {
    //노트 상세 조회
    NoteDto getNote(Long userId, Long noteId);
    //노트 생성
    void postNote(Long userId, NoteDto noteDto, List<MultipartFile> imageFile);
    //노트 수정
    void updateNote(Long userId, Long noteId, NoteDto noteDto, List<MultipartFile> imageFiles, List<Long> imageIdToDelete);
    //노트 삭제
    void deleteNote(Long userId, Long noteId);
    //폴더 내 노트목록 조회(본인, 타사용자)
    List<NoteDto> getList(Long userId, String categoryName);
}
