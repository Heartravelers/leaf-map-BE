package leafmap.server.domain.challenge.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.user.entity.User;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "CategoryChallenge")
public class CategoryChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "count_stamp")
    private Integer countStamp;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    @JsonBackReference
    private Folder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}