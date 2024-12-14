package leafmap.server.domain.note.dto;

import leafmap.server.domain.note.entity.Folder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FolderRequestDto {
    //@NotNull(message = "Category name must not be null")
    private String name;
    //@NotNull(message = "Category color must not be null")
    private String color;
    //@NotNull(message = "IsDefault must not be null")
    private Boolean isPublic;
}
