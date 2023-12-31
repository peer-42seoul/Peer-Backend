package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.profile.request.EditProfileRequest;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.response.OtherProfileResponse;
import peer.backend.dto.profile.response.UserLinkResponse;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;
import peer.backend.service.file.ObjectService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserLinkRepository userLinkRepository;
    private final ObjectService objectService;

    private boolean isFileNotEmpty(MultipartFile imageFile) {
        return imageFile != null && !imageFile.isEmpty();
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getProfile(Authentication auth) {
        User user = User.authenticationToUser(auth);
        List<UserLink> userLinks = userLinkRepository.findAllByUserId(user.getId());
        List<UserLinkResponse> links = new ArrayList<>();
        for (UserLink link : userLinks) {
            UserLinkResponse userLink = UserLinkResponse.builder()
                    .id(link.getId())
                    .linkUrl(link.getLinkUrl())
                    .linkName(link.getLinkName())
                    .build();
            links.add(userLink);
        }
        return MyProfileResponse.builder()
                .profileImageUrl(user.getImageUrl())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .association(user.getCompany())
                .introduction(user.getIntroduce() == null ? "" : user.getIntroduce())
                .linkList(links)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isExistNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    @Transactional
    public void editLinks(Authentication auth, List<UserLinkRequest> links) {
        User user = User.authenticationToUser(auth);
        user.setUserLinks(userLinkRepository.findAllByUserId(user.getId()));
        user.getUserLinks().clear();
        for (UserLinkRequest userLinkRequest : links) {
            if (userLinkRequest.getLinkName().isEmpty() || userLinkRequest.getLinkName().isBlank() ||
            userLinkRequest.getLinkUrl().isEmpty() || userLinkRequest.getLinkUrl().isBlank()) {
                continue;
            }
            UserLink newLink = UserLink.builder()
                    .user(user)
                    .linkName(userLinkRequest.getLinkName())
                    .linkUrl(userLinkRequest.getLinkUrl())
                    .build();
            user.getUserLinks().add(newLink);
        }
        userRepository.save(user);
    }

    @Transactional
    public OtherProfileResponse getOtherProfile(Long userId, List<String> infoList) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        OtherProfileResponse profile = new OtherProfileResponse();
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

    @Transactional
    public void editProfile(Authentication auth, EditProfileRequest profile, boolean isChange) throws IOException {
        User user = User.authenticationToUser(auth);
        // 기존 이미지가 있는 경우
        //     요청한 이미지가 있는 경우 -> 변경
        //     요청한 이미지가 없고, 변경을 원하는 경우 -> 삭제
        // 기존 이미지가 없고, 요청한 이미지가 있는 경우 -> 저장
        if (user.getImageUrl() != null) {
            if (isFileNotEmpty(profile.getProfileImage())) {
                byte[] binary = Base64.encodeBase64(profile.getProfileImage().getBytes());
                objectService.deleteObject(user.getImageUrl());
                String newImage = objectService.uploadObject("profile/image", new String(binary), "image");
                user.setImageUrl(newImage);
            }
            else if (isChange) {
                objectService.deleteObject(user.getImageUrl());
                user.setImageUrl(null);
            }
        }
        else if (isFileNotEmpty(profile.getProfileImage())){
            byte[] binary = Base64.encodeBase64(profile.getProfileImage().getBytes());
            String newImage = objectService.uploadObject("profile/image", new String(binary), "image");
            user.setImageUrl(newImage);
        }
        user.setNickname(profile.getNickname());
        user.setIntroduce(profile.getIntroduction());
        userRepository.save(user);
    }
}
