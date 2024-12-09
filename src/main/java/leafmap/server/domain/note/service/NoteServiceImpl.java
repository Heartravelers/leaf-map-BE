package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.challenge.entity.CategoryChallenge;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.entity.CategoryFilter;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.NoteImage;
import leafmap.server.domain.note.repository.CategoryRepository;
import leafmap.server.domain.note.repository.NoteImageRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.exception.CustomException;
import leafmap.server.global.util.S3Provider;
import leafmap.server.global.util.s3.dto.S3UploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteServiceImpl implements NoteService{
    private NoteRepository noteRepository;
    private PlaceRepository placeRepository;
    private UserRepository userRepository;
    private RegionFilterRepository regionFilterRepository;
    private RegionFilterServiceImpl regionFilterService;
    private CategoryRepository categoryRepository;
    private NoteImageRepository noteImageRepository;
    private final S3Provider s3Provider;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PlaceRepository placeRepository,
                           UserRepository userRepository, RegionFilterRepository regionFilterRepository,
                           RegionFilterServiceImpl regionFilterService, CategoryRepository categoryRepository,
                           NoteImageRepository noteImageRepository, S3Provider s3Provider){
        this.noteRepository = noteRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.regionFilterRepository = regionFilterRepository;
        this.regionFilterService = regionFilterService;
        this.categoryRepository = categoryRepository;
        this.noteImageRepository = noteImageRepository;
        this.s3Provider = s3Provider;
    }

    @Override     //노트 상세 조회
    public NoteDto getNote(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()) {
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }
        Note note = optionalNote.get();
        Optional<User> optionalUser = userRepository.findById(note.getUser().getId());
        if (optionalUser.isEmpty()) {
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        if (!Objects.equals(userId, optionalUser.get().getId()))
            if (!note.getIsPublic()){
                throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
            }

        NoteDto noteDto = new NoteDto(note);
        return noteDto;
    }


    @Override   //노트 생성
    public void postNote(Long userId, NoteDto noteDto, List<MultipartFile> imageFiles){
        System.out.println("Received userId: " + userId);  // userId 로그 출력(**테스트)

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            System.out.println("User not found for userId: " + userId); // 로그로 확인(**테스트)

            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        System.out.println("Found user: " + optionalUser.get().getUsername());  // 로그로 확인(**테스트)

        String regionName = regionFilterService.getRegion(noteDto.getAddress());
        if (Objects.equals(regionName, "")){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }

        Optional<Place> optionalPlace = placeRepository.findById(noteDto.getPlaceId());
        if (optionalPlace.isEmpty()){
            Place place = Place.builder()
                    .id(noteDto.getPlaceId())
                    .regionName(regionName).build();
            placeRepository.save(place);
        }

        Note note = Note.builder()
                .title(noteDto.getTitle())
                .date(LocalDate.now())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .countHeart(0)
                .countVisit(0)
                .regionFilter(regionFilterRepository.findByUserAndRegionName(optionalUser.get(), regionName))
                .categoryFilter(categoryRepository.findByName(noteDto.getCategoryName()).get())
                .place(optionalPlace.get())
                .user(optionalUser.get()).build();
        noteRepository.save(note);

        //이미지 로직 - s3 업로드
        S3UploadRequest request = S3UploadRequest.builder()
                .userId(userId)
                .dirName(note.getId().toString()).build();

        for (MultipartFile file : imageFiles){
            String url = s3Provider.uploadFile(file, request);
            NoteImage noteImage = NoteImage.builder()
                    .imageUrl(url)
                    .note(note).build();
            noteImageRepository.save(noteImage);
        }

        regionFilterService.increaseRegionNoteCount(optionalUser.get(), regionName);
    }

    @Override   //노트 수정
    public void updateNote(Long userId, Long noteId, NoteDto noteDto, List<MultipartFile> imageFiles, List<Long> imageIdsToDelete){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        if (!Objects.equals(userId, optionalNote.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        Note newNote = optionalNote.get().toBuilder()
                .title(noteDto.getTitle())
                .content(noteDto.getContent())
                .isPublic(noteDto.getIsPublic())
                .categoryFilter(categoryRepository.findByName(noteDto.getCategoryName()).get()).build();

        noteRepository.save(newNote);

        //이미지 로직 - s3 수정

        //**기존 이미지 삭제
//        if (imageIdsToDelete!=null && !imageIdsToDelete.isEmpty()){
//            for (Long imageId : imageIdsToDelete) {
//                Optional<NoteImage> existingNoteImage = noteImageRepository.findById(imageId);
//                existingNoteImage.ifPresent(noteImage -> {
//                    noteImage.getImageUrl().forEach(s3Provider::deleteFile); //**s3Provider 에서 deleteFile 구현해야함
//                    noteImageRepository.delete(noteImage);
//                });
//            }
//        }

        if (imageFiles != null && !imageFiles.isEmpty()) {
            S3UploadRequest request = S3UploadRequest.builder()
                    .userId(userId)
                    .dirName(newNote.getId().toString()).build();

            for (MultipartFile file : imageFiles) {
                String url = s3Provider.uploadFile(file, request);
                NoteImage noteImage = NoteImage.builder()
                        .imageUrl(url)
                        .note(newNote).build();
                noteImageRepository.save(noteImage);
            }
        }

    }

    @Override   //노트 삭제
    public void deleteNote(Long userId, Long noteId){
        Optional<Note> optionalNote = noteRepository.findById(noteId);
        if (optionalNote.isEmpty()){
            throw new CustomException.NotFoundNoteException(ErrorCode.NOT_FOUND);
        }

        String region = regionFilterService.getRegion(optionalNote.get().getPlace().getAddress());
        if (Objects.equals(region, "")){
            throw new CustomException.BadRequestException(ErrorCode.BAD_REQUEST);
        }

        if (!Objects.equals(userId, optionalNote.get().getUser().getId())){
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        noteRepository.deleteById(noteId);
        regionFilterService.decreaseRegionNoteCount(optionalNote.get().getUser(), region);
    }

    @Override    //폴더 내 노트목록 조회
    public List<NoteDto> getList(Long userId, String categoryName){
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new CustomException.NotFoundUserException(ErrorCode.USER_NOT_FOUND);
        }
        Optional<CategoryFilter> optionalCategory = categoryRepository.findByName(categoryName);
        if (optionalCategory.isEmpty()){
            throw new CustomException.NotFoundCategoryException(ErrorCode.NOT_FOUND);
        }

        List<Note> notes = noteRepository.findByUserAndCategoryFilter(optionalUser.get(), optionalCategory.get()); //**내거인지 아닌지, isPublic, notes 엔티티가 아닌 dto return 고려해야 함

        return notes.stream()
                .map(NoteDto::new)
                .collect(Collectors.toList());

    }
}
