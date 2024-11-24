package leafmap.server.domain.place.dto;

import lombok.Getter;

@Getter
public class PlaceResponseDto {

    private Long id;

    private String name;

    private String category;

    private String address;

    private String image;

}
