package leafmap.server.domain.note.dto;

import jakarta.validation.constraints.NotNull;
import leafmap.server.domain.challenge.CategoryChallenge;
import leafmap.server.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
}
