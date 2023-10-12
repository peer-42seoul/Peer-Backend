package peer.backend.dto.profile.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkListRequest {
    private List<UserLinkDTO> linkList = new ArrayList<>();
}
