package leafmap.server.domain.place.service;

import leafmap.server.domain.place.dto.GoogleApiRequestDto;
import leafmap.server.domain.place.dto.PlaceResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlaceServiceImpl implements PlaceService {

    private static final int MAX_RESULT_COUNT = 20;

    private static final double RADIUS = 500;

    @Value("${google.api-key}")
    private String apiKey;

    @Override
    public List<PlaceResponseDto> findAll(double latitude, double longitude) {

        String url = "https://places.googleapis.com/v1/places:searchNearby";

        List<String> includedTypes = new ArrayList<>();
        includedTypes.add("restaurant");

        GoogleApiRequestDto requestBody = new GoogleApiRequestDto(includedTypes, MAX_RESULT_COUNT, RADIUS, latitude, longitude);

        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "places.displayName")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(data);

        return null;
    }
}
