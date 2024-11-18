package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.CategoryDto;

public interface CategoryService {
    void makeOwnCategory(Long userId, CategoryDto categoryDto);

}
