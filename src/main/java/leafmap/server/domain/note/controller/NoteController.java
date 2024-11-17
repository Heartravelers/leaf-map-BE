package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.service.NoteServiceImpl;
import leafmap.server.global.common.ApiResponse;
import leafmap.server.global.common.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class NoteController {
    private NoteServiceImpl noteService;

    @Autowired
    private NoteController(NoteServiceImpl noteService){
        this.noteService = noteService;
    }

    //모든 crud 에 대해 본인 글인지를 체크해야 함(jwt 토큰 체크)
    //반환형식 컨벤션 맞추기(그냥 200으로 할지 NOTE200으로 할지 등도/일단 메세지는 없애자)

    @Operation(summary = "노트 상세 조회")
    @GetMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> getNote(@PathVariable Long noteId){
        try{
            //본인 글이면 getMyNote() 다른사용자면 getUserNote()
            //본인에 대한 권한 없음->return 오류
            if (//본인 글){
                NoteDto noteDto = noteService.getMyNote(noteId);
                return ResponseEntity.ok(ApiResponse.onSuccess(noteDto));
            }
            else{
                NoteDto noteDto = noteService.getUserNote(noteId);
                return ResponseEntity.ok(ApiResponse.onSuccess(noteDto));
            }
        }
        catch(Exception e){
            //유저 없음
        }
        catch(Exception e){
            //note 존재하지 않음
        }
        catch(Exception e){
            //노트가 공개인지
        }
    }

    @Operation(summary = "노트 생성")
    @PostMapping("/note")
    public ResponseEntity<ApiResponse<?>> postNote(@Valid @RequestBody NoteDto noteDto){
        try {
            noteService.postNote(noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
        }
        catch(Exception e){
            //권한 없음
        }
        catch(Exception e){
            //nullable 불가가 null 값
        }

    }

    @Operation(summary = "노트 수정")
    @PutMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> updateNote(@PathVariable Long noteId,
                                                     @Valid @RequestBody NoteDto noteDto){
        try {
            //노트 존재 여부 확인
            noteService.updateNote(noteId, noteDto);
            return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.OK));
            //반환형식 컨벤션 맞추기(그냥 200으로 할지 NOTE200으로 할지 등도/일단 메세지는 없애자)
        }
        catch(Exception e){
            //권한 없음
        }
        catch(Exception e){
            //note 존재하지 않음
        }
        catch(Exception e){
            //노트가 공개인지
        }
        catch(Exception e){
            //nullable 불가가 null 값
        }

    }

    @Operation(summary = "노트 삭제")
    @DeleteMapping("/note/{noteId}")
    public ResponseEntity<ApiResponse<?>> deleteNote(@PathVariable Long noteId){
        try{
            noteService.deleteNote(noteId);
            //if와 예외처리와 return
        }
        catch(Exception e){
            //권한 없음
        }
        catch (Exception e){
            //note 존재하지 않음
        }

    }



}
