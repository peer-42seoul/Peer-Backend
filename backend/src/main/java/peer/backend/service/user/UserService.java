package peer.backend.service.user;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.user.UserLinkDTO;
import peer.backend.dto.user.UserPasswordRequest;
import peer.backend.dto.user.UserProfileRequest;
import peer.backend.dto.user.UserProfileResponse;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserLink;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });
        UserProfileResponse profile = UserProfileResponse.builder()
            .name(user.getName())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .birthday(user.getBirthday())
            .phone(user.getPhone())
            .build();
        return profile;
    }

    @Transactional
    public UserProfileResponse editUserProfile(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });

        if (user != userProfileRequest.getUser()) {
            // TODO: 유저 exception 추가
            throw new IllegalIdentifierException("유저가 일치하지 않습니다");
        }

        return new UserProfileResponse();
    }

    @Transactional
    public String writeSelfIntroduce(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });

        if (user != userProfileRequest.getUser()) {
            // TODO: 유저 exception 추가
            throw new IllegalIdentifierException("유저가 일치하지 않습니다");
        }

        // TODO: DTO에 Introduce 추가
        user.setIntroduce(userProfileRequest.getUser().getIntroduce());
        return user.getIntroduce();
    }

    @Transactional
    public Map<String, String> editPassword(Long userId, UserPasswordRequest userPasswordRequest) {
        Map<String, String> map = new HashMap<>();

        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });

        if (!user.getPassword().equals(userPasswordRequest.getOriginalPassword()))
        {
            map.put("fail", "비밀번호가 일치하지 않습니다");
            return map;
        }

        // TODO: 비밀번호 정책에 맞게 리팩토링
        user.setPassword(userPasswordRequest.getEditPassword());
        userRepository.save(user);
        map.put("success", "비밀번호가 수정되었습니다");
        return map;
    }

    public Map<String, String> editPhone(Long userId, String phone) {
        Map<String, String> map = new HashMap<>();

        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });

        // TODO: 전화번호 유효성 검사 및 정책에 맞게 리팩토링
        user.setPhone(phone);
        userRepository.save(user);
        map.put("success", "전화번호가 수정되었습니다");
        return map;
    }

    public UserLinkDTO writeMyLink(Long userId, UserLinkDTO userLinkDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("유저를 찾을 수 없습니다");
        });

        if (user.getUserLinks().size() >= 2)
        {
            throw new IllegalIdentifierException("링크 갯수는 2개 이상 되면 안됩니다");
        }
        UserLink userLink = UserLink.builder()
            .linkName(userLinkDTO.getLinkName())
            .linkUrl(userLinkDTO.getLinkUrl())
            .faviconPath(userLinkDTO.getFaviconPath())
            .build();

        user.getUserLinks().add(userLink);

        return UserLinkDTO.toDTO(userLink);
    }
}
