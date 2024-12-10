package leafmap.server.domain.note.service;

public interface ScrapService {
    void makeScrap(Long userId, Long noteId);
    void deleteScrap(Long userId, Long noteId);
}
