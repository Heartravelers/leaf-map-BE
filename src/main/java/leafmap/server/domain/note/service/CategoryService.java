package leafmap.server.domain.note.service;

import leafmap.server.domain.note.dto.CategoryDto;
import leafmap.server.domain.note.dto.NoteDto;

import java.util.List;

public interface CategoryService {
    void makeCategory(Long userId, CategoryDto categoryDto);
    List<CategoryDto> getCategory(Long userId);
    void updateCategory(Long categoryId, CategoryDto categoryDto);
    void deleteCategory(Long categoryId);
    List<NoteDto> filterNotes(Long userId, String regionName);

}
