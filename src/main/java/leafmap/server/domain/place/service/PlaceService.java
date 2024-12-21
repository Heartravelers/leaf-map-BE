package leafmap.server.domain.place.service;

import leafmap.server.domain.place.dto.PlaceDetailResponseDto;
import leafmap.server.domain.place.dto.PlaceResponseDto;

import java.util.List;

public interface PlaceService {

    List<PlaceResponseDto> findAll(double latitude, double longitude, String category, String keyword);

    PlaceDetailResponseDto findById(String id);

}
