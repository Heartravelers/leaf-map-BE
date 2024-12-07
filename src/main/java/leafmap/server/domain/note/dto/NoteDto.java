package leafmap.server.domain.note.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.NoteImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoteDto {
    @NotNull(message = "Place info must not be null")
    private String placeId;
    @NotNull(message = "Title must not be null")
    private String title;
    @NotNull(message = "Content must not be null")
    private String content;
    private Boolean isPublic;
    private String address;
    private String categoryName;

    public NoteDto(@NotNull Note note){
        this.placeId = note.getPlace().getId();
        this.title = note.getTitle();
        this.content = note.getContent();
        this.isPublic = note.getIsPublic();
        this.address = note.getPlace().getAddress();
        this.categoryName = note.getCategoryFilter().getName();
    }
}
