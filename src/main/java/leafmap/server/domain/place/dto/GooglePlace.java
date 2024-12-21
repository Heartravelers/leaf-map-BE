package leafmap.server.domain.place.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class GooglePlace {

    private String id;

    private String formattedAddress;

    private location location;

    private displayName displayName;

    //private String primaryType;

    @Setter
    private List<String> types;

    private List<GooglePhoto> photos;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class location {
        private double latitude;
        private double longitude;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class displayName {
        private String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class GooglePhoto {
        private String googleMapsUri;
    }
}
