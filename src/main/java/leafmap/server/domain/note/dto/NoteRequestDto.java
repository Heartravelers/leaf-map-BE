package leafmap.server.domain.note.dto;

import leafmap.server.domain.note.entity.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequestDto {
    //@NotNull(message = "Place info must not be null")
    private String placeId;
    //    @NotNull(message = "Title must not be null")
    private String title;
    //    @NotNull(message = "Content must not be null")
    private String content;
    private Boolean isPublic;
    private String address;
    private String folderName;

    public NoteRequestDto(Note note){ //** not null 어노테이션 삭제
        this.placeId = note.getPlace().getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.isPublic = note.getIsPublic();
        this.address = note.getPlace().getAddress();
        this.folderName = note.getFolder().getName();
    }
}
