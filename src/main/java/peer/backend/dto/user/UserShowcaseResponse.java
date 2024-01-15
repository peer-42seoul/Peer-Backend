package peer.backend.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserShowcaseResponse {
    private String image;
    private String nickname;
    private List<String> role;
}
