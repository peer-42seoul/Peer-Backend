package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import peer.backend.entity.user.Admin;
import peer.backend.exception.NotFoundException;
import peer.backend.repository.user.AdminRepository;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public Admin getAdminByAdminId(String id) {
        return this.adminRepository.findByAdminId(id)
            .orElseThrow(() -> new NotFoundException("존재하지 않은 어드민입니다."));
    }
}
