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
    private Long placeId;
    private Long userId;
    private String profilePicture;
    @NotNull(message = "Title must not be null")
    private String title;
    private LocalDate date;
    @NotNull(message = "Content must not be null")
    private String content;
    private Boolean isPublic;
    private List<NoteImage> noteImages;
    private String placeName;
    private String address;
    private int countHeart;
    @JsonIgnore
    private CategoryFilter categoryFilter;




    public NoteDto(@NotNull Note note){
        this.placeId = note.getPlace().getId();
        this.userId = note.getUser().getId();
        this.profilePicture = note.getUser().getProfilePicture();
        this.title = note.getTitle();
        this.date = note.getDate();
        this.noteImages = note.getNoteImages();
        this.isPublic = note.getIsPublic();
        this.placeName = note.getPlace().getName();
        this.address = note.getPlace().getAddress();
        this.countHeart = note.getCountHeart();
        this.categoryFilter = note.getCategoryFilter();
    }
}
