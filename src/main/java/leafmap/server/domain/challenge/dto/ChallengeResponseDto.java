package leafmap.server.domain.challenge.dto;

import leafmap.server.domain.challenge.entity.Challenge;
import leafmap.server.domain.note.entity.Folder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChallengeResponseDto {

    private int countStamp; // 총 스탬프 수

    private List<CategoryChallengeResponseDto> categories;

    private boolean receivedHeart;

    private boolean haveFollower;

    private boolean haveVisited;

    public ChallengeResponseDto(Challenge challenge, List<Folder> folders) {
        this.countStamp = challenge.getCountStamp();
        this.receivedHeart = challenge.getReceivedHeart();
        this.haveFollower = challenge.getHaveFollower();
        this.haveVisited = challenge.getHaveVisited();

        List<CategoryChallengeResponseDto> categories = new ArrayList<>();
        for(Folder folder : folders) {
            categories.add(new CategoryChallengeResponseDto(folder));
        }
        this.categories = categories;
    }

}
