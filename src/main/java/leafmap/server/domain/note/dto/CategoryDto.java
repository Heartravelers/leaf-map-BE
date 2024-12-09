package leafmap.server.domain.note.dto;

import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {  //categoryFilter 에 대한 dto
//    private Long id;
    //@NotNull(message = "Category name must not be null")
    private String name;
    //@NotNull(message = "Category color must not be null")

    private String color;
    //@NotNull(message = "IsDefault must not be null")

//    private Boolean isDefault;
//    private String code;
    private Integer countNote;
    //@NotNull(message = "User must not be null")

//    private User user;
//    @NotNull(message = "Challenge must not be null")


    public CategoryDto(CategoryFilter categoryFilter){ //Not null 어노테이션 잠시 삭제
        this.color = categoryFilter.getColor();
        this.countNote = categoryFilter.getCountNote();
        this.name = categoryFilter.getName();
    }
}
