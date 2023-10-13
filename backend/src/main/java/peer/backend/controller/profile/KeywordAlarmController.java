package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import peer.backend.dto.profile.KeywordRequest;
import peer.backend.exception.BadRequestException;
import peer.backend.service.profile.KeywordAlarmService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KeywordAlarmController {
    private final KeywordAlarmService keywordAlarmService;

    @ApiOperation(value = "C-MYPATE-30", notes = "알람 키워드 추가 하기")
    @PostMapping("/alarm/add")
    public ResponseEntity<Object> addKeyword(Authentication auth, @RequestBody KeywordRequest newKeyword) {
        if (newKeyword.getNewKeyword().contains("^&%")) {
            throw new BadRequestException("잘못된 문자열 입니다.");
        }
        keywordAlarmService.addKeyword(auth.getName(), newKeyword.getNewKeyword());
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
