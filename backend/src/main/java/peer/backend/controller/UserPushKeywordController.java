package peer.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import peer.backend.dto.KeywordDto;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.service.UserPushKeywordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile/keyword")
public class UserPushKeywordController {

    private final UserPushKeywordService keywordService;

    @GetMapping("/{userId}")
    public List<KeywordDto> getKeywordList(@PathVariable Long userId) {
        List<UserPushKeyword> userPushKeywordList = this.keywordService.getKeywordList(userId);

        List<KeywordDto> keywordDtoList = userPushKeywordList.stream()
            .map(m -> new KeywordDto(m.getKeyword())).collect(Collectors.toList());

        return keywordDtoList;
    }

    @PostMapping("/{userId}")
    public void postKeyword(@PathVariable Long userId, @RequestBody @Valid KeywordDto keywordDto) {
        this.keywordService.postKeyword(userId, keywordDto.getKeyword());
    }

    @DeleteMapping("/{userId}")
    public void deleteKeyword(@PathVariable Long userId,
        @RequestBody @Valid KeywordDto keywordDto) {
        this.keywordService.deleteKeyword(userId, keywordDto.getKeyword());
    }
}
