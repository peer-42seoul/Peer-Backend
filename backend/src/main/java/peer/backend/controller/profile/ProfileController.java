package peer.backend.controller.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.profile.YourProfileRequest;
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
}
