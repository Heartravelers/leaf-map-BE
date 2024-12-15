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
    private String name;
    private String color;
    private Boolean isPublic;
}
