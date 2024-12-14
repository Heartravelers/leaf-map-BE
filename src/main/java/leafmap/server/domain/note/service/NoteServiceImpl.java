package leafmap.server.domain.note.service;

import jakarta.transaction.Transactional;
import leafmap.server.domain.note.dto.NoteDetailResponseDto;
import leafmap.server.domain.note.dto.NoteRequestDto;
import leafmap.server.domain.note.dto.NoteResponseDto;
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
    public NoteDetailResponseDto getNote(Long myUserId, Long noteId){
        Note note = checkService.checkNote(noteId);
        User user = checkService.checkUser(note.getUser().getId());
        if (!Objects.equals(myUserId, user.getId())) //본인 글이 아닐 때
            if (!note.getIsPublic()){ //isPublic False 이면
                throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
            }

        return new NoteDetailResponseDto(note);
    }


    @Override   //노트 생성
    public void postNote(Long myUserId, NoteRequestDto noteRequestDto, List<MultipartFile> imageFiles){
        User user = checkService.checkUser(myUserId);
        String regionName = checkService.checkAndGetRegionName(noteRequestDto.getAddress()); // 주소에서 regionName get
        RegionFilter regionFilter = checkService.checkRegionFilterAndMakeOrReturn(user, regionName); // 유저에 대한 해당 regionFilter 가져오기
        Place place = checkService.checkPlaceAndSaveReturn(noteRequestDto.getPlaceId(), noteRequestDto.getAddress()); // place 정보 저장하고 가져오기
        Folder folder = checkService.checkUserFolder(user, noteRequestDto.getFolderName()); //folder 정보 확인하고 가져오기

        //dto 데이터 통해 노트 생성
        Note note = Note.builder()
                .title(noteRequestDto.getTitle())
                .user(user)
                .place(place)
                .date(LocalDate.now())
                .content(noteRequestDto.getContent())
                .isPublic(noteRequestDto.getIsPublic())
                .countHeart(0)
                .regionFilter(regionFilter)
                .folder(folder).build();
        noteRepository.save(note);

        //이미지 관련 로직 - s3 업로드하고 db에 정보 저장 (note 와 image 연결)
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

         //**폴더 자체가 비공개인 것도 체크하기
        regionFilterService.increaseRegionNoteCount(user, regionName);
    }

    @Override   //노트 수정
    public void updateNote(Long myUserId, Long noteId, NoteRequestDto noteRequestDto, List<MultipartFile> imageFiles){
        User user = checkService.checkUser(myUserId);
        Note note = checkService.checkNote(noteId);

        if (!Objects.equals(user, note.getUser())){ //본인이 쓴 글이 아니면
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        RegionFilter regionFilter = checkService.checkAndGetRegionName()
        Place place = checkService.checkPlaceAndSaveReturn();

        //**플레이스 변경도 가능한지?-가능 -> 폴더, 플레이스 객체 dto정보 통해 생성해서 update로 넘겨주기

        note.update(noteRequestDto);
        noteRepository.save(note);

        //이미지 로직 - s3 수정
        //**이미지 파일 자체로 그냥 받고 해당 파일의 NoteImage 로컬 db 객체를 불러와서
        //생성된 url 이 해당 db에 있는지를 확인하고
        //비교해보고 있으면 제외, 없으면 s3올리고 저장, 언급된 url 이 없다면 삭제를 하도록?

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

//        if (imageFiles != null && !imageFiles.isEmpty()) {
//            S3UploadRequest request = S3UploadRequest.builder()
//                    .userId(userId)
//                    .dirName(newNote.getId().toString()).build();
//
//            for (MultipartFile file : imageFiles) {
//                String url = s3Provider.uploadFile(file, request);
//                NoteImage noteImage = NoteImage.builder()
//                        .imageUrl(url)
//                        .note(newNote).build();
//                noteImageRepository.save(noteImage);
//            }
//        }

    }

    @Override   //노트 삭제
    public void deleteNote(Long myUserId, Long noteId){
        User user = checkService.checkUser(myUserId);
        Note note = checkService.checkNote(noteId);
        String regionName = checkService.checkAndGetRegionName(note.getPlace().getAddress());

        if (!Objects.equals(user, note.getUser())){ //본인이 쓴 글이 아니면
            throw new CustomException.ForbiddenException(ErrorCode.FORBIDDEN);
        }

        noteRepository.deleteById(noteId);
        regionFilterService.decreaseRegionNoteCount(user, regionName);
    }

    @Override    //폴더 내 노트목록 조회
    public List<NoteResponseDto> getList(Long myUserId, Long userId, String folderName){
        User user = checkService.checkUser(userId);
        Folder folder = checkService.checkUserFolder(user, folderName);

        List<Note> notes = noteRepository.findByUserAndFolder(user, folder);

        if (!Objects.equals(myUserId, user.getId())) //본인 글이 아닐 때
            for (Note note : notes) {
                if (!note.getIsPublic()) { //다른 유저 글이 isPublic False 이면 해당 객체 제외
                    notes.remove(note);
                }
            }

        return notes.stream()
                .map(NoteResponseDto::new)
                .collect(Collectors.toList());
    }
}
