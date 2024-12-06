package leafmap.server.domain.place.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import leafmap.server.domain.place.dto.*;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaceServiceImpl implements PlaceService {

    private static final double LATITUDE_MIN = 33.0;
    private static final double LATITUDE_MAX = 38.6278;
    private static final double LONGITUDE_MIN = 125.5666;
    private static final double LONGITUDE_MAX = 131.8696;

    private static final int MAX_RESULT_COUNT = 5;
    private static final double RADIUS = 500;
    private static final String FIELD_MASK = "places.id,places.displayName,places.primaryType,places.formattedAddress,places.photos,places.location";
    private static final String DETAIL_FIELD_MASK = "displayName,formattedAddress,primaryType,photos";

    @Value("${google.api-key}")
    private String apiKey;

    @Autowired
    PlaceRepository placeRepository;
    
    //키워드 있으면 위치 기반 키워드 검색하고 카테고리는 무시
    //키워드 없으면 위치 기반 검색하고 카테고리 필터링
    @Override
    public List<PlaceResponseDto> findAll(double latitude, double longitude, String category, String keyword) {
        
        if(latitude < LATITUDE_MIN || latitude > LATITUDE_MAX || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String url = "https://places.googleapis.com/v1/places:searchNearby";
        Object requestBody;
        if(keyword != null) {
            url = "https://places.googleapis.com/v1/places:searchText";
            requestBody = new GoogleApiKeywordRequestDto(keyword, MAX_RESULT_COUNT, RADIUS, latitude, longitude);
        } else {
            List<String> includedTypes = new ArrayList<>();
            if(category != null) {
                switch (category) {
                    case "식당" ->
                            includedTypes = List.of("acai_shop", "afghani_restaurant", "african_restaurant", "american_restaurant", "asian_restaurant", "bar", "bar_and_grill", "barbecue_restaurant", "brazilian_restaurant", "breakfast_restaurant", "brunch_restaurant", "buffet_restaurant", "chinese_restaurant", "diner", "fast_food_restaurant", "fine_dining_restaurant", "food_court", "french_restaurant", "greek_restaurant", "hamburger_restaurant", "indian_restaurant", "indonesian_restaurant", "italian_restaurant", "japanese_restaurant", "korean_restaurant", "lebanese_restaurant", "meal_delivery", "meal_takeaway", "mediterranean_restaurant", "mexican_restaurant", "middle_eastern_restaurant", "pizza_restaurant", "pub", "ramen_restaurant", "restaurant", "sandwich_shop", "seafood_restaurant", "spanish_restaurant", "steak_house", "sushi_restaurant", "thai_restaurant", "turkish_restaurant", "vegan_restaurant", "vegetarian_restaurant", "vietnamese_restaurant", "wine_bar");
                    case "카페" ->
                            includedTypes = List.of("bagel_shop", "bakery", "candy_store", "cat_cafe", "chocolate_factory", "chocolate_shop", "coffee_shop", "confectionery", "dessert_restaurant", "dessert_shop", "dog_cafe", "donut_shop", "ice_cream_shop", "juice_shop", "tea_house");
                    case "관광명소" ->
                            includedTypes = List.of("cultural_landmark", "historical_place", "monument", "historical_landmark", "observation_deck", "beach");
                    case "여가문화" ->
                            includedTypes = List.of("art_gallery","art_studio","auditorium","museum","performing_arts_theater","sculpture","library","adventure_sports_center","amphitheatre","amusement_center","amusement_park","aquarium","banquet_hall",
                                    "barbecue_area","botanical_garden","bowling_alley","comedy_club","community_center","concert_hall","convention_center","cultural_center","cycling_park","dance_hall","dog_park","ferris_wheel","garden",
                                    "hiking_area","internet_cafe","karaoke","marina","movie_theater","national_park","opera_house","park","philharmonic_hall","picnic_ground","planetarium","plaza","roller_coaster","skateboard_park","state_park",
                                    "tourist_attraction","video_arcade","visitor_center","water_park","wildlife_park","zoo","arena","athletic_field","ice_skating_rink");
                    default -> throw new CustomException(ErrorCode.BAD_REQUEST);
                }
            }
            requestBody = new GoogleApiRequestDto(includedTypes, MAX_RESULT_COUNT, RADIUS, latitude, longitude);
        }

        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleApiResponseDto response = null;
        try {
            response = objectMapper.readValue(data, GoogleApiResponseDto.class);
            return response.getPlaces().stream().map(PlaceResponseDto::new).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    @Override
    public List<PlaceResponseDto> findAll(double latitude, double longitude, String category) {

        if(latitude < LATITUDE_MIN || latitude > LATITUDE_MAX || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        List<String> includedTypes = new ArrayList<>();
        if(category != null) {
            switch (category) {
                case "식당" ->
                        includedTypes = List.of("acai_shop", "afghani_restaurant", "african_restaurant", "american_restaurant", "asian_restaurant", "bar", "bar_and_grill", "barbecue_restaurant", "brazilian_restaurant", "breakfast_restaurant", "brunch_restaurant", "buffet_restaurant", "chinese_restaurant", "diner", "fast_food_restaurant", "fine_dining_restaurant", "food_court", "french_restaurant", "greek_restaurant", "hamburger_restaurant", "indian_restaurant", "indonesian_restaurant", "italian_restaurant", "japanese_restaurant", "korean_restaurant", "lebanese_restaurant", "meal_delivery", "meal_takeaway", "mediterranean_restaurant", "mexican_restaurant", "middle_eastern_restaurant", "pizza_restaurant", "pub", "ramen_restaurant", "restaurant", "sandwich_shop", "seafood_restaurant", "spanish_restaurant", "steak_house", "sushi_restaurant", "thai_restaurant", "turkish_restaurant", "vegan_restaurant", "vegetarian_restaurant", "vietnamese_restaurant", "wine_bar");
                case "카페" ->
                        includedTypes = List.of("bagel_shop", "bakery", "candy_store", "cat_cafe", "chocolate_factory", "chocolate_shop", "coffee_shop", "confectionery", "dessert_restaurant", "dessert_shop", "dog_cafe", "donut_shop", "ice_cream_shop", "juice_shop", "tea_house");
                case "관광명소" ->
                        includedTypes = List.of("cultural_landmark", "historical_place", "monument", "historical_landmark", "observation_deck", "beach");
                case "여가문화" ->
                        includedTypes = List.of("art_gallery","art_studio","auditorium","museum","performing_arts_theater","sculpture","library","adventure_sports_center","amphitheatre","amusement_center","amusement_park","aquarium","banquet_hall",
                                "barbecue_area","botanical_garden","bowling_alley","comedy_club","community_center","concert_hall","convention_center","cultural_center","cycling_park","dance_hall","dog_park","ferris_wheel","garden",
                                "hiking_area","internet_cafe","karaoke","marina","movie_theater","national_park","opera_house","park","philharmonic_hall","picnic_ground","planetarium","plaza","roller_coaster","skateboard_park","state_park",
                                "tourist_attraction","video_arcade","visitor_center","water_park","wildlife_park","zoo","arena","athletic_field","ice_skating_rink");
                default -> throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }

        String url = "https://places.googleapis.com/v1/places:searchNearby";

        GoogleApiRequestDto requestBody = new GoogleApiRequestDto(includedTypes, MAX_RESULT_COUNT, RADIUS, latitude, longitude);

        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleApiResponseDto response = null;
        try {
            response = objectMapper.readValue(data, GoogleApiResponseDto.class);
            return response.getPlaces().stream().map(PlaceResponseDto::new).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<PlaceResponseDto> findAllByKeyword(double latitude, double longitude, String keyword) {

        if(latitude < LATITUDE_MIN || latitude > LATITUDE_MAX || longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX || keyword == null || keyword.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String url = "https://places.googleapis.com/v1/places:searchText";

        GoogleApiKeywordRequestDto requestBody = new GoogleApiKeywordRequestDto(keyword, MAX_RESULT_COUNT, RADIUS, latitude, longitude);

        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleApiResponseDto response = null;
        try {
            response = objectMapper.readValue(data, GoogleApiResponseDto.class);
            return response.getPlaces().stream().map(PlaceResponseDto::new).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    */

    @Override
    public PlaceDetailResponseDto findById(String id) {
        String url = "https://places.googleapis.com/v1/places/" + id + "?languageCode=ko";

        WebClient webClient = WebClient.create();
        String data = webClient
                .get()
                .uri(url)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", DETAIL_FIELD_MASK)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        GooglePlace response = null;
        try {
            response = objectMapper.readValue(data, GooglePlace.class);
            Optional<Place> placeOptional = placeRepository.findById(id);
            if(placeOptional.isPresent()) {
                return new PlaceDetailResponseDto(response, placeOptional.get().getNotes());
            }
            throw new CustomException(ErrorCode.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
