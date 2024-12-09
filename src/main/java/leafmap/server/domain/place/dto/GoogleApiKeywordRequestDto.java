package leafmap.server.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class GoogleApiKeywordRequestDto{

    private String textQuery;

    private int pageSize;

    private LocationRestriction locationBias;

    private String languageCode;


    @AllArgsConstructor
    @Getter
    private static class LocationRestriction {
        private Circle circle;
    }

    @AllArgsConstructor
    @Getter
    private static class Circle {
        private Coordinates center;
        private double radius;
    }

    @AllArgsConstructor
    @Getter
    private static class Coordinates {
        private double latitude;
        private double longitude;
    }

    public GoogleApiKeywordRequestDto(String textQuery, int pageSize, double radius, double latitude, double longitude) {
        this.textQuery = textQuery;
        this.pageSize = pageSize;
        this.locationBias = new LocationRestriction(new Circle(new Coordinates(latitude, longitude), radius));
        languageCode = "ko";
    }

}
