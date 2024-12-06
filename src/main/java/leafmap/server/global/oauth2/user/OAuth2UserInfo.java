package leafmap.server.global.oauth2.user;

public interface OAuth2UserInfo {

    String getProviderId();
    OAuth2Provider getProvider();

    String getEmail();

    String getName();

}