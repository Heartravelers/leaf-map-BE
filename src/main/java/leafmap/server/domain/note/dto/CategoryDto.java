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
    private Long id;
    @NotNull(message = "Category name must not be null")
    private String name;
    @NotNull(message = "Category color must not be null")

    private String color;
    @NotNull(message = "IsDefault must not be null")

    private Boolean isDefault;
    private String code;
    private Integer countNote;
    @NotNull(message = "User must not be null")

    private User user;
    @NotNull(message = "Challenge must not be null")

    private CategoryChallenge categoryChallenge;

    public CategoryDto(@NotNull CategoryFilter categoryFilter){
        this.id = categoryFilter.getId();
        this.color = categoryFilter.getColor();
        this.categoryChallenge = categoryFilter.getCategoryChallenge();
        this.code = categoryFilter.getCode();
        this.countNote = categoryFilter.getCountNote();
        this.name = categoryFilter.getName();
        this.user = categoryFilter.getUser();
    }
}
