package peer.backend.service.board.recruit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import peer.backend.dto.board.recruit.HitchListResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.repository.board.recruit.RecruitRepository;

@Service
@RequiredArgsConstructor
public class HitchHikingService {

    private RecruitRepository recruitRepository;
    public Page<HitchListResponse> getHitchList(int page, int pageSize, String type, Authentication auth){
            Page<Recruit> recruitList = recruitRepository.findAllByStatus(RecruitStatus.ONGOING, PageRequest.of(page, pageSize));
            Page<HitchListResponse> result = recruitList.map();
    }
}
