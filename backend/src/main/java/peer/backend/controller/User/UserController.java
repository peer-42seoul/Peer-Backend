package peer.backend.controller.User;

import io.swagger.annotations.ApiOperation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.user.UserLinkDTO;
import peer.backend.dto.user.UserPasswordRequest;
import peer.backend.dto.user.UserProfileRequest;
import peer.backend.dto.user.UserProfileResponse;
import peer.backend.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "C-MYPAGE-09", notes = "유저의 프로필 정보를 가져옵니다")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @PathVariable("userId") Long userId) {

        /**
         * TODO: userId를 통하여 유저를 찾고 해당 유저가 존재하지 않으면 예외처리하기
         */
        return new ResponseEntity<>(userService.getUserProfile(userId), HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-10", notes = "개인정보 수정하기")
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> editUserProfile(
        @PathVariable("userId") Long userId,
        @ModelAttribute UserProfileRequest userProfileRequest) {

        /**
         * TODO: 해당 유저가 프로필을 수정할 수 있는 권한이 있는지 확인후 프로필 수정 (Principle 적용)
         */
        return new ResponseEntity<>(userService.editUserProfile(userId, userProfileRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-11", notes = "비밀번호 수정하기")
    @PutMapping("/profile/password/{userId}")
    public Map<String, String> editPassword(
        @PathVariable("userId") Long userId,
        @RequestParam UserPasswordRequest userPasswordRequest) {

        /**
         * TODO: 해당 비밀번호가 현재 비밀번호와 일치하는지 확인하고 일치한다면 바꾸기
         *       나중에 User가 아닌 Principle 적용하여 개발
         */
        return userService.editPassword(userId, userPasswordRequest);
    }

    @ApiOperation(value = "C-MYPAGE-12", notes = "전화번호 수정하기")
    @PutMapping("/profile/phone/{userId}")
    public Map<String, String> editPhone(
        @PathVariable("userId") Long userId,
        @RequestParam String phone) {

        return userService.editPhone(userId, phone);
    }

    @ApiOperation(value = "C-MYPAGE-19", notes = "프로필 자기 소개하기")
    @PostMapping("/profile/introduce/{userId}")
    public ResponseEntity<String> writeProfileIntroduce(
        @PathVariable("userId") Long userId,
        @ModelAttribute UserProfileRequest userProfileRequest) {

        /**
         * TODO: 해당 유저가 프로필을 수정할 수 있는 권한이 있는지 확인후 프로필 수정 (Principle 적용)
         *       String introduce만 가져올 수 있는 방법이 있을까?
         */

        return new ResponseEntity<>(userService.writeSelfIntroduce(userId, userProfileRequest), HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-20", notes = "프로필에 넣을 개인 링크 (gitHub, velog) 등록")
    @PostMapping("/profile/link/{userId}")
    public ResponseEntity<UserLinkDTO> writeMyLinkInProfile(
        @PathVariable("userId") Long userId,
        @RequestParam UserLinkDTO userLinkDTO) {

        /**
         * TODO: 올바른 링크인지 확인?
         */

        return new ResponseEntity<>(userService.writeMyLink(userId, userLinkDTO), HttpStatus.OK);
    }

}
