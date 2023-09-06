package peer.backend.service.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.EditProfileDTO;
import peer.backend.dto.profile.MyProfileResponse;
import peer.backend.dto.profile.UserLinkDTO;
import peer.backend.dto.profile.YourProfileResponse;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserLinkRepository userLinkRepository;

    @Transactional(readOnly = true)
    public YourProfileResponse showOtherProfile(Long userId) throws Exception{
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("유저가 존재하지 않습니다");
        });

        YourProfileResponse dto = YourProfileResponse.builder()
            .id(user.getId())
            .profileImageUrl(user.getImageUrl())
            .introduction(user.getIntroduce())
            .representAchievement(user.getRepresentAchievement())
            .linkList(user.getUserLinks())
            .build();
        return dto;
    }

    // Todo: Principle 통해 해당 유저가 나인지 판단할것
    @Transactional(readOnly = true)
    public MyProfileResponse showMyProfile(Long userId) throws Exception
    {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("유저가 존재하지 않습니다");
        });

        MyProfileResponse dto = MyProfileResponse.builder()
            .id(user.getId())
            .profileImageUrl(user.getImageUrl())
            .introduction(user.getIntroduce())
            .linkList(user.getUserLinks())
            .phone(user.getPhone())
            .representAchievement(user.getRepresentAchievement())
            .achievements(user.getUserAchievements())
            .build();
        return dto;
    }

    // Todo: 프로필 수정
    //       내가 맞는지 판단할 것
    @Transactional
    public EditProfileDTO editMyProfile(EditProfileDTO profile, Long userId) throws Exception
    {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("유저가 존재하지 않습니다");
        });

        Optional.ofNullable(profile.getProfileImageUrl())
            .ifPresent(user::setImageUrl);
        Optional.ofNullable(profile.getIntroduction())
            .ifPresent(user::setIntroduce);
        Optional.ofNullable(profile.getPhone())
            .ifPresent(user::setPhone);
        Optional.ofNullable(profile.getAchievement())
            .ifPresent(user::setRepresentAchievement);

        if (profile.getLinkList() != null) {
            List<UserLink> userLinks = user.getUserLinks();
            List<UserLink> newLinks = new ArrayList<>(); // 새로운 링크 엔터티를 저장할 리스트

            for (UserLink newLink : profile.getLinkList()) {
                // 이미 존재하는 링크와 중복되는지 확인
                boolean isDuplicate = false;
                for (UserLink existingLink : userLinks) {
                    if (existingLink.getLinkName().equals(newLink.getLinkName())) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (!isDuplicate) {
                    UserLink userLink = new UserLink();
                    userLink.setUser(user);
                    userLink.setLinkName(newLink.getLinkName());
                    userLink.setLinkUrl(newLink.getLinkUrl());
                    userLink.setFaviconPath(newLink.getFaviconPath());

                    newLinks.add(userLink); // 중복되지 않는 경우에만 새로운 링크 엔터티 추가
                }
            }

            userLinks.addAll(newLinks); // 중복되지 않는 새로운 링크 목록을 기존 목록에 추가
        }
        userRepository.save(user);

        return profile;
    }
}
