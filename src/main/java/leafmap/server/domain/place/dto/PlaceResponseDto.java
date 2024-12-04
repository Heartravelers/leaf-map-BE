package leafmap.server.domain.place.dto;

import lombok.Getter;

@Getter
public class PlaceResponseDto {

    private String id;

    private String name;

    private String category;

    private String address;

    private String image;

    public PlaceResponseDto(GooglePlace googlePlace) {
        this.id = googlePlace.getId();
        this.name = googlePlace.getDisplayName().getText();
        this.category = googlePlace.getPrimaryType();
        this.address = googlePlace.getFormattedAddress();
        if(googlePlace.getPhotos() != null && !googlePlace.getPhotos().isEmpty()) {
            this.image = googlePlace.getPhotos().get(1).getGoogleMapsUri();
        } else {
            this.image = null;
        }
    }
}
