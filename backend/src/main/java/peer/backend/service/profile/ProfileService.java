package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import peer.backend.dto.profile.request.EditProfileRequest;
import peer.backend.dto.profile.response.MyProfileResponse;
import peer.backend.dto.profile.request.UserLinkRequest;
import peer.backend.dto.profile.response.OtherProfileResponse;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.BadRequestException;
import peer.backend.exception.NotFoundException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserLinkRepository;
import peer.backend.repository.user.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final UserLinkRepository userLinkRepository;
    private final Tika tika;

    @Value("${custom.filePath}")
    private String filepath;

    private boolean isFileEmpty(MultipartFile imageFile) {
        return imageFile == null || imageFile.isEmpty();
    }

    private void deleteUserImage(User user) throws IOException {
        String imagePath = user.getImageUrl();
        if (imagePath == null) {
            return;
        }
        imagePath = imagePath.substring(7);
        File file = new File(imagePath);
        if (!file.exists()) {
            user.setImageUrl(null);
            return;
        }
        else if (!file.delete()) {
            throw new IOException("파일 삭제에 실패했습니다.");
        }
        user.setImageUrl(null);
    }

    private Path saveImageFilePath(User user, MultipartFile file) throws IOException {
        String fileType = tika.detect(file.getInputStream());
        if (!fileType.startsWith("image")) {
            throw new IllegalArgumentException("image 타입이 아닙니다.");
        }
        StringBuilder builder = new StringBuilder();
        String folderPath = builder
                .append(filepath)
                .append(File.separator)
                .append("upload")
                .append(File.separator)
                .append("profiles")
                .append(File.separator)
                .append(user.getId().toString())
                .toString();
        File folder = new File(folderPath);
        System.out.println(folder.getPath());
        if (folder.mkdirs()) {
            if (!folder.exists()) {
                throw new IOException("폴더 생성에 실패했습니다.");
            }
        }
        String originalName = file.getOriginalFilename();
        assert originalName != null;
        String filePath = builder
                .append(File.separator)
                .append("profile")
                .append(originalName.substring(originalName.lastIndexOf(".")))
                .toString();
        Path path = Paths.get(filePath);
        file.transferTo(path.toFile());
        return path;
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getProfile(User user) {
        List<UserLink> userLinks = userLinkRepository.findAllByUserId(user.getId());
        List<UserLinkRequest> links = new ArrayList<>();
        for (UserLink link : userLinks) {
            UserLinkRequest userLink = UserLinkRequest.builder()
                    .id(link.getId())
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
    public void editLinks(User user, List<UserLinkRequest> links) {
        if (user.getUserLinks() != null) {
            userLinkRepository.deleteAll(user.getUserLinks());
        }
        List<UserLink> newLink = user.getUserLinks();
        newLink.clear();
        for (UserLinkRequest link : links) {
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
    public void editProfile(User user, EditProfileRequest profile) throws IOException {
        if (isFileEmpty(profile.getProfileImage()) && profile.isImageChange()) {
            deleteUserImage(user);
        }
        else if (!isFileEmpty(profile.getProfileImage())) {
            deleteUserImage(user);
            user.setImageUrl(
                    saveImageFilePath(user, profile.getProfileImage()).toUri().toString()
            );
        }
        user.setNickname(profile.getNickname());
        user.setIntroduce(profile.getIntroduction());
        userRepository.save(user);
    }
}
