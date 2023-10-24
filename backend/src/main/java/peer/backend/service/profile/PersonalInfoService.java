package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.request.PasswordRequest;
import peer.backend.dto.profile.response.PersonalInfoResponse;
import peer.backend.entity.user.User;
import peer.backend.exception.ForbiddenException;
import peer.backend.exception.NotFoundException;
import peer.backend.oauth.PrincipalDetails;
import peer.backend.repository.user.UserRepository;


@Service
@RequiredArgsConstructor
public class PersonalInfoService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PersonalInfoResponse getPersonalInfo(PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        return PersonalInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .local(user.getAddress())
                .authentication(user.getCompany())
                .build();
    }

    @Transactional
    public void changePassword(String name, PasswordRequest passwords) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("사용자가 존재하지 않습니다.")
        );
        if (!encoder.matches(passwords.getPresentPassword(), user.getPassword())) {
            throw new ForbiddenException("현재 비밀번호가 올바르지 않습니다..");
        }
        if (!passwords.getNewPassword().equals(passwords.getConfirmPassword())) {
            throw new ForbiddenException("변경할 비밀번호와 일치하지 않습니다.");
        }
        user.setPassword(encoder.encode(passwords.getNewPassword()));
        userRepository.save(user);
    }
}
