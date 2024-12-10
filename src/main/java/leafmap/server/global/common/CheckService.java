package leafmap.server.global.common;

import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.user.entity.User;

public interface CheckService {
    User checkUser(Long userId);
    Note checkNote(Long noteId);
    Folder checkFolder(String folderName);
    String checkAndGetRegionName(String address);
    Place checkPlaceAndSaveReturn(String placeId, String RegionName);
    RegionFilter checkRegionFilterAndMakeOrReturn(User user, String regionName);
}
