package peer.backend.mongo.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import peer.backend.mongo.entity.TeamDnD;

public interface PeerLogDnDRepository extends MongoRepository<TeamDnD, ObjectId> {
}
