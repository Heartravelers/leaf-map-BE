package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.note.dto.NoteDto;
import leafmap.server.domain.note.dto.NoteRequestDto;
import leafmap.server.domain.note.entity.Folder;
import leafmap.server.domain.note.entity.Note;
import leafmap.server.domain.note.entity.NoteImage;
import leafmap.server.domain.note.entity.RegionFilter;
import leafmap.server.domain.note.repository.FolderRepository;
import leafmap.server.domain.note.repository.NoteImageRepository;
import leafmap.server.domain.note.repository.NoteRepository;
import leafmap.server.domain.note.repository.RegionFilterRepository;
import leafmap.server.domain.place.entity.Place;
import leafmap.server.domain.place.repository.PlaceRepository;
import leafmap.server.domain.user.entity.User;
import leafmap.server.domain.user.repository.UserRepository;
import leafmap.server.global.common.ErrorCode;
import leafmap.server.global.common.CheckService;
import leafmap.server.global.common.exception.CustomException;
import leafmap.server.global.util.S3Provider;
import leafmap.server.global.util.s3.dto.S3UploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private FolderRepository folderRepository;
    private NoteImageRepository noteImageRepository;
    private final S3Provider s3Provider;
    private CheckService checkService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, PlaceRepository placeRepository,
                           UserRepository userRepository, RegionFilterRepository regionFilterRepository,
                           RegionFilterServiceImpl regionFilterService, FolderRepository folderRepository,
                           NoteImageRepository noteImageRepository, S3Provider s3Provider,
                           CheckService checkService){
        this.noteRepository = noteRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.regionFilterRepository = regionFilterRepository;
        this.regionFilterService = regionFilterService;
        this.folderRepository = folderRepository;
        this.noteImageRepository = noteImageRepository;
        this.s3Provider = s3Provider;
        this.checkService = checkService;
    }

    @Override     //노트 상세 조회
    public NoteDto getNote(Long myUserId, Long noteId){
        Note note = checkService.checkNote(noteId);
        User user = checkService.checkUser(note.getUser().getId());
        if (!Objects.equals(myUserId, user.getId())) //본인 글이 아닐 때
            if (!note.getIsPublic()){ //isPublic False 이면
                throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
            }

        NoteDto noteDto = new NoteDto(note); //*-* noteDto responseDto 에서 관리해야하는데 그거 필드 관련 지윤이와 상의
        return noteDto;
    }


    @Override   //노트 생성
    public void postNote(Long myUserId, NoteRequestDto noteRequestDto, List<MultipartFile> imageFiles){
        User user = checkService.checkUser(myUserId);
        String regionName = checkService.checkAndGetRegionName(noteRequestDto.getAddress()); // 주소에서 regionName get
        RegionFilter regionFilter = checkService.checkRegionFilterAndMakeOrReturn(user, regionName); // 유저에 대한 해당 regionFilter 가져오기
        Place place = checkService.checkPlaceAndSaveReturn(noteRequestDto.getPlaceId(), noteRequestDto.getAddress()); // place 정보 저장하고 가져오기
        Folder folder = checkService.checkFolder(noteRequestDto.getFolderName()); //folder 정보 확인하고 가져오기

        Note note = Note.builder()
                .title(noteRequestDto.getTitle())
                .user(user)
                .place(place)
                .date(LocalDate.now())
                .content(noteRequestDto.getContent())
                .isPublic(noteRequestDto.getIsPublic())
                .countHeart(0)
                .countVisit(0)
                .regionFilter(regionFilter)
                .folder(folder).build();
        noteRepository.save(note);

        //이미지 로직 - s3 업로드
        S3UploadRequest request = S3UploadRequest.builder()
                .userId(myUserId)
                .dirName(note.getId().toString()).build();

        for (MultipartFile file : imageFiles){
            String url = s3Provider.uploadFile(file, request);
            NoteImage noteImage = NoteImage.builder()
                    .imageUrl(url)
                    .note(note).build();
            noteImageRepository.save(noteImage);
        }

         //**파일 자체가 비공개인 것도 체크하기
        regionFilterService.increaseRegionNoteCount(user, regionName);
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
                .folder(folderRepository.findByName(noteDto.getFolderName()).get()).build();

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
        Optional<Folder> optionalFolder = folderRepository.findByName(categoryName);
        if (optionalFolder.isEmpty()){
            throw new CustomException.NotFoundFolderException(ErrorCode.NOT_FOUND);
        }

        List<Note> notes = noteRepository.findByUserAndFolder(optionalUser.get(), optionalFolder.get()); //**내거인지 아닌지, isPublic, notes 엔티티가 아닌 dto return 고려해야 함

        return notes.stream()
                .map(NoteDto::new)
                .collect(Collectors.toList());

    }
}
