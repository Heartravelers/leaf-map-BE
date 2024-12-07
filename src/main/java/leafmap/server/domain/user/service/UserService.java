package leafmap.server.domain.user.service;

public interface UserService {

    void subscribeUser(Long followerId, Long followingId);

}
