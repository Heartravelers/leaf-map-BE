package leafmap.server.domain.challenge.service;

import leafmap.server.domain.challenge.dto.ChallengeResponseDto;

public interface ChallengeService {

    ChallengeResponseDto getChallenge(Long userId);

}
