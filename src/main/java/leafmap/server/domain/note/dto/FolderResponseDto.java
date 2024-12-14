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
public class FolderResponseDto {
    //@NotNull(message = "Category name must not be null")
    private String name;
    //@NotNull(message = "Category color must not be null")
    private String color;
    //@NotNull(message = "IsDefault must not be null")
    private Boolean isPublic;


    public FolderResponseDto(Folder folder){ //Not null 어노테이션 잠시 삭제
        this.color = folder.getColor();
        this.isPublic = folder.getIsPublic();
        this.name = folder.getName();
    }
}
