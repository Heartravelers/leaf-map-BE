package leafmap.server.domain.place.dto;

import leafmap.server.domain.note.dto.NoteResponseDto;
import leafmap.server.domain.note.entity.Note;
import lombok.Getter;

import java.util.List;

@Getter
public class PlaceDetailResponseDto {

    private String name;

    private String category;

    private String address;

    private String image;

    private List<NoteResponseDto> notes;

    public PlaceDetailResponseDto(GooglePlace googlePlace, List<Note> notes) {
        this.name = googlePlace.getDisplayName().getText();
        this.category = googlePlace.getPrimaryType();
        this.address = googlePlace.getFormattedAddress();
        if(googlePlace.getPhotos() != null && !googlePlace.getPhotos().isEmpty()) {
            this.image = googlePlace.getPhotos().get(0).getGoogleMapsUri();
        } else {
            this.image = null;
        }
        this.notes = notes.stream().map(NoteResponseDto::new).toList();
    }

}
