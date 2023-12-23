//package peer.backend.repository.board.recruit;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import peer.backend.entity.board.recruit.RecruitApplicant;
//import peer.backend.entity.composite.TeamUserJobPK;
//
//import java.util.List;
//
//public interface RecruitApplicantRepository extends JpaRepository<RecruitApplicant, TeamUserJobPK> {
//    List<RecruitApplicant> findByUserId(Long userId);
//    List<RecruitApplicant> findByRecruitId(Long recruitId);
//    RecruitApplicant findByUserIdAndRecruitId(Long userId, Long recruitId);
//}
