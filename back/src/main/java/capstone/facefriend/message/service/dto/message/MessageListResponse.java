package capstone.facefriend.message.service.dto.message;

import capstone.facefriend.message.domain.Message;
import java.time.LocalDateTime;

public record MessageListResponse(
        Long senderId,
        String senderNickname,
        String senderOriginS3Url,
        String senderGeneratedS3Url,
        String content,
        LocalDateTime sendTime
) {

    public static MessageListResponse of(Message message) {
        return new MessageListResponse(
                message.getSender().getId(),
                message.getSender().getBasicInfo().getNickname(),
                message.getSender().getFaceInfo().getOriginS3url(),
                message.getSender().getFaceInfo().getGeneratedS3url(),
                message.getContent(),
                message.getSendTime()
        );
    }
}