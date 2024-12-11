package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.NoteDetailResponseDto;
import leafmap.server.domain.note.dto.NoteRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoteService {
    //노트 상세 조회
    NoteDetailResponseDto getNote(Long myUserId, Long noteId);
    //노트 생성
    void postNote(Long myUserId, NoteRequestDto noteRequestDto, List<MultipartFile> imageFile);
    //노트 수정
    void updateNote(Long myUserId, Long noteId, NoteRequestDto noteRequestDto, List<MultipartFile> imageFiles);
    //노트 삭제
    void deleteNote(Long myUserId, Long noteId);
    //폴더 내 노트목록 조회(본인, 타사용자)
    List<NoteDetailResponseDto> getList(Long userId, String categoryName);
}
