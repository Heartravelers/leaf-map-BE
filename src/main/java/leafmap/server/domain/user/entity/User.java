package leafmap.server.domain.user.entity;

import jakarta.persistence.*;
import leafmap.server.domain.challenge.entity.Challenge;
import leafmap.server.domain.note.entity.*;
import leafmap.server.domain.qna.entity.Inquiry;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Setter
    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Setter
    @Column(name = "profile_picture")
    private String profilePicture;

    @Setter
    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Setter
    @Column(name = "is_public")
    private boolean isPublic;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inquiry> inquiries = new ArrayList<>();

    //@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Challenge> challenges = new ArrayList<>();
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegionFilter> regionFilters = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryFilter> categoryFilters = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();
}
