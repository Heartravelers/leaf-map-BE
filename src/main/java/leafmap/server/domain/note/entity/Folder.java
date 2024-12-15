package leafmap.server.domain.note.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.note.dto.FolderRequestDto;
import leafmap.server.domain.user.entity.User;
import leafmap.server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Folder")
public class Folder extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "code")
    private String code;

    @Column(name = "count_note")
    private Integer countNote;

    @Column(name = "isPublic") //파일 비공개 여부 추가
    private Boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @OneToOne(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private CategoryChallenge categoryChallenge;

    public void update(FolderRequestDto folderRequestDto){
        this.name = folderRequestDto.getName();
        this.color = folderRequestDto.getColor();
        this.isPublic = folderRequestDto.getIsPublic();
    }

    public void syncCategoryChallenge(CategoryChallenge categoryChallenge){
        this.categoryChallenge = categoryChallenge;
        if (categoryChallenge != null) {
            categoryChallenge.update(this); // CategoryChallenge 와 동기화
        }
    }
}
