package leafmap.server.domain.note.dto;

import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.NoteImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDetailResponseDto {
    //@NotNull(message = "Place info must not be null")
    private String placeId; //프론트에서 해당 플레이스 id 로 검색 후 address 등 필요 요소 있다면 반환 필요
//    @NotNull(message = "Title must not be null")
    private String title;
    private LocalDate date;
//    @NotNull(message = "Content must not be null")
    private String content;
    private Boolean isPublic;
    private Integer countHeart;
    private String folderName;
    private List<NoteImage> noteImageList;

    public NoteDetailResponseDto(Note note){ //** not null 어노테이션 삭제
        this.placeId = note.getPlace().getId();
        this.title = note.getTitle();
        this.date = note.getDate();
        this.content = note.getContent();
        this.isPublic = note.getIsPublic();
        this.countHeart = note.getCountHeart();
        this.folderName = note.getFolder().getName();
        this.noteImageList = note.getNoteImages();
    }
}
