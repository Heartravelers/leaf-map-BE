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
    private String name;
    private String color;
    private Boolean isPublic;


    public FolderResponseDto(Folder folder){ //Not null 어노테이션 잠시 삭제
        this.color = folder.getColor();
        this.isPublic = folder.getIsPublic();
        this.name = folder.getName();
    }
}
