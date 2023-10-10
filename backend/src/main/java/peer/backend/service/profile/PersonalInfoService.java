package peer.backend.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.profile.information.PersonalInfoResponse;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.UserRepository;


@Service
@RequiredArgsConstructor
public class PersonalInfoService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PersonalInfoResponse getPersonalInfo(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다.")
        );
        return PersonalInfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .local(user.getAddress())
                .authentication(user.getCompany())
                .build();
    }
}
