package leafmap.server.domain.user.service;

import leafmap.server.domain.user.entity.Follow;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.FollowRepository;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Override
    public void subscribeUser(Long followerId, Long followingId) {
        Optional<User> followerOptional = userRepository.findById(followerId);
        Optional<User> followingOptional = userRepository.findById(followingId);

        if(followerOptional.isPresent() && followingOptional.isPresent()) {
            User follower = followerOptional.get();
            User following = followingOptional.get();

            Optional<Follow> followOptional = followRepository.findByFollowerAndFollowing(follower, following);
            if(followOptional.isPresent()) {
                followRepository.delete(followOptional.get());
            } else {
                followRepository.save(new Follow(follower, following));
            }
//            List<Follow> followList = follower.getFollowings();
//            for(Follow follow : followList) {
//                if(follow.getFollowing().equals(following)) {
//                    followRepository.delete(follow);
//                    return;
//                }
//            }
            return;
        } throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

}
