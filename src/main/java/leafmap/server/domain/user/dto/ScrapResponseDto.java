package leafmap.server.domain.user.dto;

import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.NoteImage;
import leafmap.server.domain.note.entity.Scrap;

import java.util.List;

public class ScrapResponseDto {

    private Long noteId;

    private String title;

    private String image;

    public ScrapResponseDto(Scrap scrap) {
        Note note = scrap.getNote();
        this.noteId = note.getId();
        this.title = note.getTitle();
        List<NoteImage> noteImages = note.getNoteImages();
        if(noteImages == null || noteImages.isEmpty()) {
            this.image = null;
        } else {
            this.image = note.getNoteImages().get(0).getImageUrl();
        }
    }

}
