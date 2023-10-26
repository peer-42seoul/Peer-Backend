package peer.backend.controller.profile;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import peer.backend.dto.profile.KeywordRequest;
import peer.backend.dto.profile.KeywordResponse;
import peer.backend.entity.user.User;
import peer.backend.exception.BadRequestException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.service.profile.KeywordAlarmService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KeywordAlarmController {
    private final KeywordAlarmService keywordAlarmService;

    @ApiOperation(value = "C-MYPATE-30", notes = "알람 키워드 추가 하기")
    @PostMapping("/alarm/add")
    public ResponseEntity<Object> addKeyword(Authentication auth, @RequestBody KeywordRequest newKeyword) {
        if (newKeyword.getNewKeyword().contains("^&%") ||
            newKeyword.getNewKeyword().isEmpty() ||
            newKeyword.getNewKeyword().isBlank()) {
            throw new BadRequestException("잘못된 문자열 입니다.");
        }
        keywordAlarmService.addKeyword(User.authenticationToUser(auth) , newKeyword.getNewKeyword());
        return new ResponseEntity<> (HttpStatus.CREATED);
    }

    @ApiOperation(value = "C-MYPAGE-30", notes = "알람 키워드 조회 하기")
    @GetMapping("/alarm")
    public ResponseEntity<Object> getKeywords(Authentication auth) {
        KeywordResponse keyword = keywordAlarmService.getKeyword(User.authenticationToUser(auth));
        return new ResponseEntity<> (keyword, HttpStatus.OK);
    }

    @ApiOperation(value = "C-MYPAGE-31", notes = "알람 키워드 삭제 하기")
    @DeleteMapping("/alarm/delete")
    public ResponseEntity<Object> deleteKeyword(Authentication auth,
                                                @RequestParam(value = "keyword", required = true)String keyword) {
        keywordAlarmService.deleteKeyword(User.authenticationToUser(auth), keyword);
        return new ResponseEntity<> (HttpStatus.CREATED);
    }

    @ApiOperation(value = "C-MYPAGE-31", notes = "알람 키워드 전체 삭제 하기")
    @DeleteMapping("/alarm/delete/all")
    public ResponseEntity<Object> deleteAll(Authentication auth) {
        keywordAlarmService.deleteAll(User.authenticationToUser(auth));
        return new ResponseEntity<> (HttpStatus.CREATED);
    }
}
