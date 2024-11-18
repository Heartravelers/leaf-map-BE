package leafmap.server.domain.note.dto;

import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.NoteImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
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
    private Integer countHeart;
    private CategoryFilter categoryFilter;
}
