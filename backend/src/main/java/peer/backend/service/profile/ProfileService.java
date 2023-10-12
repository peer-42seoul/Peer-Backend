package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.request.UserLinkDTO;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MyProfileResponse getProfile(String name)
    {
        User user = userRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        List<UserLinkDTO> links = new ArrayList<>();
        for (UserLink link : user.getUserLinks()) {
            UserLinkDTO userLink = UserLinkDTO.builder()
                    .linkName(link.getLinkName())
                    .linkUrl(link.getLinkUrl())
                    .build();
            links.add(userLink);
        }
        return MyProfileResponse.builder()
                .profileImageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .company(user.getCompany())
                .introduction(user.getIntroduce())
                .linkList(links)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isExistNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public void editLinks(String name, List<UserLinkDTO> links) {
        User user = userRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        user.getUserLinks().clear();
        for (UserLinkDTO link : links) {
            UserLink userLink = UserLink.builder()
                    .user(user)
                    .linkName(link.getLinkName())
                    .linkName(link.getLinkName())
                    .build();
            user.getUserLinks().add(userLink);
        };
        userRepository.save(user);
    }
}
