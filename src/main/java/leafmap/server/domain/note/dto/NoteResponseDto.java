package leafmap.server.domain.note.dto;

import leafmap.server.domain.note.entity.Note;
import lombok.Getter;

@Getter
public class NoteResponseDto {

    private Long id;

    private String title;

    private String image;

    private String userProfileImage;

    public NoteResponseDto(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        if(note.getNoteImages() != null && !note.getNoteImages().isEmpty()) {
            this.image = note.getNoteImages().get(0).getImageUrl();
        } else {
            this.image = null;
        }
        if(note.getUser().getProfilePicture() != null) {
            this.userProfileImage = note.getUser().getProfilePicture();
        }
    }

}
