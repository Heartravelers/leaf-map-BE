package leafmap.server.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class GoogleApiRequestDto {

    private List<String> includedTypes;

    private int maxResultCount;

    private LocationRestriction locationRestriction;


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

    public GoogleApiRequestDto(List<String> includedTypes, int maxResultCount, double radius, double latitude, double longitude) {
        this. includedTypes = includedTypes;
        this.maxResultCount = maxResultCount;
        this.locationRestriction = new LocationRestriction(new Circle(new Coordinates(latitude, longitude), radius));
    }

}
