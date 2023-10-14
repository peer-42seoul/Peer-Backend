package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.request.UserLinkDTO;
import peer.backend.dto.profile.response.OtherProfileDto;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserLinkRepository userLinkRepository;

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
        userLinkRepository.deleteAll(user.getUserLinks());
        List<UserLink> newLink = user.getUserLinks();
        newLink.clear();
        for (UserLinkDTO link : links) {
            UserLink userLink = UserLink.builder()
                    .user(user)
                    .linkName(link.getLinkName())
                    .linkUrl(link.getLinkUrl())
                    .build();
            newLink.add(userLink);
        }
        for (int index = newLink.size() - 1; index >= 0; index--) {
            userLinkRepository.save(newLink.get(index));
        }
        user.setUserLinks(newLink);
        userRepository.save(user);
    }

    @Transactional
    public OtherProfileDto getOtherProfile(Long userId, List<String> infoList) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        OtherProfileDto profile = new OtherProfileDto();
        for (String info : infoList) {
            switch (info) {
                case "nickname":
                    profile.setNickname(user.getNickname());
                    break;
                case "profileImageUrl":
                    profile.setProfileImageUrl(user.getImageUrl());
                    break;
                default:
                    throw new BadRequestException("잘못된 요청입니다.");
            }
        }
        return (profile);
    }
}
