package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.service.profile.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController{
    private final ProfileService profileService;

    @ApiOperation(value = "C-MYPAGE-", notes = "사용자 프로필 정보 조회하기")
    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(Authentication auth) {
        return new ResponseEntity<> (profileService.getProfile(auth.getName()), HttpStatus.OK);
    }
}
