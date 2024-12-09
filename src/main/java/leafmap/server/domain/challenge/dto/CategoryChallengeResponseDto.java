package leafmap.server.domain.challenge.dto;

import leafmap.server.domain.note.entity.Folder;
import lombok.Getter;

@Getter
public class CategoryChallengeResponseDto {

    private String categoryName;

    private int countStamp;

    public CategoryChallengeResponseDto(Folder folder) {
        this.categoryName = folder.getName();
        this.countStamp = folder.getCategoryChallenge().getCountStamp();
    }

}
