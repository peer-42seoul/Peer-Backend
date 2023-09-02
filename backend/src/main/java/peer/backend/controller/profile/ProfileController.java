package peer.backend.controller.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.profile.EditProfileDTO;
import peer.backend.entity.user.UserLink;
import peer.backend.service.profile.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController{
    private final ProfileService profileService;

    @GetMapping("/profile/{userId}")
    public ResponseEntity showOtherProfile(@PathVariable("userId") Long userId) throws Exception {
        return new ResponseEntity<>(profileService.showOtherProfile(userId), HttpStatus.OK);
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity showMyProfile(@PathVariable("userId") Long userId) throws Exception {
        // Todo: Principle로 리팩토링하여 내가 맞는지 여부 확인
        return new ResponseEntity<>(profileService.showMyProfile(userId), HttpStatus.OK);
    }

    @PutMapping("/me/{userId}")
    public ResponseEntity editMyProfile(@PathVariable("userId") Long userId,
        @RequestBody EditProfileDTO editProfileDTO) throws Exception {
        // Todo: Principle로 리팩토링하여 내가 맞는지 여부 확인 및 유저아이디 패스배리어블 삭제
        return new ResponseEntity<>(profileService.editMyProfile(editProfileDTO, userId), HttpStatus.OK);
    }
}
