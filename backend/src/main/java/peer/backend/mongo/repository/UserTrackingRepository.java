package peer.backend.mongo.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import peer.backend.mongo.entity.UserTracking;

public interface UserTrackingRepository extends MongoRepository<UserTracking, ObjectId> {

}
