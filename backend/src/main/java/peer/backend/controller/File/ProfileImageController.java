package peer.backend.controller.File;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.exception.IllegalArgumentException;
import peer.backend.exception.MultipartException;
import peer.backend.service.file.ProfileImageService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    @ApiOperation(value = "C-MYPAGE-01", notes = "프로필 이미지 url을 리턴한다. 이미지가 없으면 기본 이미지 url을 return한다.")
    //id를 로그인 아이디로 변경 필요
    @GetMapping("profile/image/{loginId}")
    public String getProfileImage (@PathVariable("loginId") Long userId) throws IOException {
        /**
         * TO-DO : 해당 유저가 프로필을 조회할 수 있는 권한이 있는지 확인
         */
        return profileImageService.getProfileImageUrl(userId);
    }

    //imageurl null값이면 기본 이미지 url 리턴
    @ApiOperation(value = "C-MYPAGE-02", notes = "프로필 이미지를 업로드한다.")
    @PostMapping("profile/image/{loginId}")
    public ResponseEntity<Object> uploadProfileImage(
            @PathVariable("loginId") Long userId,
            @RequestParam MultipartFile uploadImage) throws IOException {
        /**
         * TO-DO : 해당 유저가 프로필을 수정할 수 있는 권한이 있는지 확인
         */
        if (uploadImage.isEmpty())
            throw new IllegalArgumentException("fail to save file : file is not exist");
        if (!uploadImage.getContentType().startsWith(("image")))
            throw new MultipartException("Fail to save file : file is not an image");
        String resultPath = profileImageService.saveProfileImage(uploadImage, userId);
        return ResponseEntity.status(HttpStatus.OK).body(resultPath);
    }

    @ApiOperation(value = "C-MYPAGE-03", notes = "프로필 이미지를 삭제한다. 이미지가 삭제된 이후에는 기본 이미지를 사용한다.")
    @DeleteMapping("profile/image/{loginId}")
    public ResponseEntity<Object> deleteProfileImage (
            @PathVariable("loginId") Long userId) throws IOException {
        /**
         * TO-DO : 해당 유저가 프로필을 수정할 수 있는 권한이 있는지 확인
         */
        profileImageService.deleteImage(userId);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Success : file deleted");
    }

    @ApiOperation(value = "C-MYPAGE-04", notes = "프로필 이미지를 다운로드 한다.")
    @GetMapping("profile/image/{loginId}/download")
    public ResponseEntity<byte[]> downloadProfileImage(
            @PathVariable("loginId") Long userId) throws IOException {
        return profileImageService.downloadImage(userId);
    }
}