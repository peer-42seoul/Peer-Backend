package peer.backend.controller.User;

import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.KeywordDTO;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.service.user.UserPushKeywordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/keyword")
public class UserPushKeywordController {

    private final UserPushKeywordService keywordService;

    @ApiOperation(value = "C-MYPAGE-29", notes = "알림 키워드 목록을 가져옵니다.")
    @GetMapping("/{userId}")
    public List<KeywordDTO> getKeywordList(@PathVariable Long userId) {
        List<UserPushKeyword> userPushKeywordList = this.keywordService.getKeywordList(userId);

        return userPushKeywordList.stream()
            .map(m -> new KeywordDTO(m.getKeyword())).collect(Collectors.toList());
    }

    @ApiOperation(value = "C-MYPAGE-30", notes = "알림 키워드를 등록합니다.")
    @PostMapping("/{userId}")
    public void postKeyword(@PathVariable Long userId, @RequestBody @Valid KeywordDTO keywordDto) {
        this.keywordService.postKeyword(userId, keywordDto.getKeyword());
    }

    @ApiOperation(value = "C-MYPAGE-31", notes = "알림 키워드를 삭제합니다.")
    @DeleteMapping("/{userId}")
    public void deleteKeyword(@PathVariable Long userId,
        @RequestBody @Valid KeywordDTO keywordDto) {
        this.keywordService.deleteKeyword(userId, keywordDto.getKeyword());
    }
}
