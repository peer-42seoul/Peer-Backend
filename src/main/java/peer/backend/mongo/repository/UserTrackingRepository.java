package peer.backend.mongo.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import peer.backend.mongo.entity.UserTracking;

public interface UserTrackingRepository extends MongoRepository<UserTracking, ObjectId> {

    UserTracking findByUserId(Long id);

    List<UserTracking> findAllByUserIdIn(List<Long> idList);
}
