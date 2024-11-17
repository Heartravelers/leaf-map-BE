package leafmap.server.domain.place.repository;

import leafmap.server.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Place getOneByNameAndAddress(String name, String address);
}
