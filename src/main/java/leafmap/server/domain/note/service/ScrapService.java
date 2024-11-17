package leafmap.server.domain.note.service;

public interface ScrapService {
    void makeScrap(Long noteId);
    void deleteScrap(Long noteId);
}
