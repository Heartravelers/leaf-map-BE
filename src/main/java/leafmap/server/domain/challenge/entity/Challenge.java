package leafmap.server.domain.challenge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Challenge")
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id", nullable = false)
    private Long id;

    @Column(name = "count_stamp")
    private Integer countStamp;

    @Column(name = "received_heart")
    private Boolean receivedHeart;

    @Column(name = "have_follower")
    private Boolean haveFollower;

    @Column(name = "have_visited")
    private Boolean haveVisited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
}
