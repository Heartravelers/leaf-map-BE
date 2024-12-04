package leafmap.server.domain.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class GoogleApiResponseDto {

    private List<GooglePlace> places;

    /*
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    private static class place {
        private String id;

        private String formattedAddress;

        private location location;

        private displayName displayName;

        private String primaryType;

        private List<GooglePhoto> photos;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    private static class location {
        private double latitude;
        private double longitude;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    private static class displayName {
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    private static class GooglePhoto {
        private String googleMapsUri;
    }

    public void printAll() {
        for(place p : places) {
            System.out.println("id: " + p.getId() +
                    "\nformattedAddress: " + p.getFormattedAddress() +
                    "\nlatitude: " + p.getLocation().getLatitude() +
                    "\nlongitude: " + p.getLocation().getLongitude() +
                    "\ndisplayName: " + p.getDisplayName().getText() +
                    "\nprimaryType: " + p.getPrimaryType() +
                    "\nphotos: " + p.getPhotos());
        }
    }
    */

}
