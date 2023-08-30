package peer.backend.service.profile;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.EditProfileDTO;
import peer.backend.dto.profile.MyProfileRequest;
import peer.backend.dto.profile.YourProfileRequest;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public YourProfileRequest showOtherProfile(Long userId) throws Exception{
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("유저가 존재하지 않습니다");
        });

        YourProfileRequest dto = YourProfileRequest.builder()
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
    public MyProfileRequest showMyProfile(Long userId) throws Exception
    {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("유저가 존재하지 않습니다");
        });

        MyProfileRequest dto = MyProfileRequest.builder()
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

        List<UserLink> existingLinks = user.getUserLinks();
        existingLinks.clear(); // Remove existing links

        for (UserLink updatedLink : profile.getLinkList()) {
            UserLink newLink = UserLink.builder()
                .user(user)
                .linkName(updatedLink.getLinkName())
                .linkUrl(updatedLink.getLinkUrl())
                .faviconPath(updatedLink.getFaviconPath())
                .build();
            existingLinks.add(newLink);
        }

        user.setUserLinks(existingLinks);
        userRepository.save(user);

        return profile;
    }
}
