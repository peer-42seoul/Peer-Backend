package peer.backend.comparator;

import peer.backend.entity.message.MessagePiece;

import java.time.LocalDateTime;
import java.util.*;

public class MessagePieceComparator implements Comparator<MessagePiece> {

    @Override
    public int compare(MessagePiece o1, MessagePiece o2) {
        LocalDateTime createdAtA = o1.getCreatedAt();
        LocalDateTime createdAtB = o2.getCreatedAt();
        return createdAtA.compareTo(createdAtB);
    }
}
