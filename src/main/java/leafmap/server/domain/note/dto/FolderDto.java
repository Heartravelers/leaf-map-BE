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
public class FolderDto {  //categoryFilter 에 대한 dto
    //@NotNull(message = "Category name must not be null")
    private String name;
    //@NotNull(message = "Category color must not be null")

    private String color;
    //@NotNull(message = "IsDefault must not be null")
    private Integer countNote;
    //@NotNull(message = "User must not be null")

//    private User user;
//    @NotNull(message = "Challenge must not be null")


    public FolderDto(Folder folder){ //Not null 어노테이션 잠시 삭제
        this.color = folder.getColor();
        this.countNote = folder.getCountNote();
        this.name = folder.getName();
    }
}
