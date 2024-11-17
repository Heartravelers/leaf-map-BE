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

    //response dto 생성자
    public NoteDto(Long placeId, Long userId, String profilePicture,
                   String title, LocalDate date, String content,
                   List<NoteImage> noteImages, String placeName,
                   String address, Integer countHeart, CategoryFilter categoryFilter){

        this.placeId = placeId;
        this.userId = userId;
        this.profilePicture = profilePicture;
        this.title = title;
        this.date = date;
        this.content = content;
        this.noteImages = noteImages;
        this.placeName = placeName;
        this.address = address;
        this.countHeart = countHeart;
    }

    //request dto 생성자
    public NoteDto(String title, LocalDate date, String content, Boolean isPublic,
                          List<NoteImage> noteImages, CategoryFilter categoryFilter,
                          String placeName, String address){
        this.title = title;
        this.date = date;
        this.content = content;
        this.isPublic = isPublic;
        this.noteImages = noteImages;
        this.categoryFilter = categoryFilter;
        this.placeName = placeName;
        this.address = address;
    }

}
