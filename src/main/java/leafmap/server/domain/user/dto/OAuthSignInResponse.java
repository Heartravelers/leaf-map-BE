package leafmap.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthSignInResponse {
    private String grantType;
    private String accessToken;
    private Long accessTokenExpirationTime;
    private String refreshToken;
    private Long refreshTokenExpirationTime;

    public static OAuthSignInResponse from(
            String grantType,
            String accessToken,
            Long accessTokenExpirationTime,
            String refreshToken,
            Long refreshTokenExpirationTime) {
        return OAuthSignInResponse.builder()
                .grantType(grantType)
                .accessToken(accessToken)
                .accessTokenExpirationTime(accessTokenExpirationTime)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshTokenExpirationTime)
                .build();
    }
}