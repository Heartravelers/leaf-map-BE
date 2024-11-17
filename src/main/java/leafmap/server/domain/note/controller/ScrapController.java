package leafmap.server.domain.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import leafmap.server.domain.note.service.NoteServiceImpl;
import leafmap.server.global.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScarpController {
    private NoteServiceImpl noteService;

    @Autowired
    private ScarpController(NoteServiceImpl noteService){
        this.noteService = noteService;
    }

    @Operation(summary = "하트 추가 및 스크랩")
    @PostMapping("/note/{noteId}/scrap")
    public ResponseEntity<ApiResponse<?>> makeScarp(@RequestParam Long noteId){
        try{
            //본인 여부 확인
            //노트 존재여부 확인
            //하트 status 확인

        }
        catch (){

        }
    }

    @Operation(summary = "하트 삭제 및 스크랩 취소")
    @DeleteMapping("/note/{noteId}/scrap")
    public ResponseEntity<ApiResponse<?>> deleteScrap(@RequestParam Long noteId){
        //본인 여부 확인
        //노트 존재 여부 확인
        //하트 status 확인
    }
}
