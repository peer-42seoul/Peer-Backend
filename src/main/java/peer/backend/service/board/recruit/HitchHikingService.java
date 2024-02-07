package peer.backend.service.board.recruit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import peer.backend.dto.board.recruit.HitchListResponse;
import peer.backend.dto.board.recruit.HitchResponse;
import peer.backend.entity.board.recruit.Recruit;
import peer.backend.entity.board.recruit.enums.RecruitStatus;
import peer.backend.entity.team.enums.TeamType;
import peer.backend.entity.user.User;
import peer.backend.exception.NotFoundException;
import peer.backend.exception.OutOfRangeException;
import peer.backend.repository.board.recruit.RecruitRepository;
import peer.backend.service.TagService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HitchHikingService {

    private final RecruitRepository recruitRepository;
    private final TagService tagService;

    private String excludeImageUrlFromContent(String origin){
        String noTag = origin.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
        return noTag;

    }

    @Transactional
    public Page<HitchListResponse> getHitchList(int page, int pageSize, String type, Long userId){
            if (page < 1)
                throw new OutOfRangeException("page 번호는 1부터 시작합니다.");
            Page<Recruit> recruitList = recruitRepository.findAllByStatusAndTeamTypeAndFavorite(
                    RecruitStatus.ONGOING,
                    TeamType.from(type),
                    userId,
                    PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending()));
            return recruitList.map(recruit ->
                    HitchListResponse.builder()
                            .authorId(recruit.getWriterId())
                            .authorImage((recruit.getWriter() == null)? null : recruit.getWriter().getImageUrl())
                            .image(recruit.getThumbnailUrl())
                            .title(recruit.getTitle())
                            .teamName(recruit.getTeam().getName())
                            .recruitId(recruit.getId())
                            .tagList(tagService.recruitTagListToTagResponseList(recruit.getRecruitTags()))
                            .build());
    }

    @Transactional
    public HitchResponse getHitch(Long hitchId){
        Recruit recruit = recruitRepository.findById(hitchId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 모집글입니다."));
        return HitchResponse.builder()
                .content(excludeImageUrlFromContent(recruit.getContent()))
                .memberImage(recruit.getTeam().getTeamUsers().stream().map(
                        teamUser -> teamUser.getUser().getImageUrl()).collect(Collectors.toList()))
                .recruitmentQuota(recruit.getTeam().getMaxMember())
                .build();
    }
}
