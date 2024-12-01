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

    private static final int MAX_RESULT_COUNT = 10;

    private static final double RADIUS = 500;

    @Value("${google.api-key}")
    private String apiKey;

    @Override
    public List<PlaceResponseDto> findAll(double latitude, double longitude, String category) {

        String url = "https://places.googleapis.com/v1/places:searchNearby";

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
            }
        }

        GoogleApiRequestDto requestBody = new GoogleApiRequestDto(includedTypes, MAX_RESULT_COUNT, RADIUS, latitude, longitude);

        WebClient webClient = WebClient.create();
        String data = webClient
                .post()
                .uri(url)
                .bodyValue(requestBody)
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "places.id,places.displayName,places.types,places.formattedAddress,places.photos,places.location")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(data);

        return null;
    }
}