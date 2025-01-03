package capstone.facefriend.message.exception;

import capstone.facefriend.common.exception.ExceptionType;
import capstone.facefriend.common.exception.Status;

public enum MessageExceptionType implements ExceptionType {

    NOT_FOUND_ROOM(Status.NOT_FOUND, 5001, "일치하는 채팅방이 없습니다."),
    NOT_FOUND_ROOM_MEMBER(Status.NOT_FOUND, 5002, "채팅방에 채팅방 멤버가 존재하지 않습니다."),
    FAIL_TO_SOCKET_INFO(Status.BAD_REQUEST, 5003, "소켓 연결 실패했습니다!"),
    INVALIDED_ROOM(Status.BAD_REQUEST, 5004, "유효한 채팅방이 아닙니다"),
    INVALID_ACCESS(Status.FORBIDDEN, 5005, "본인의 계정이 아닙니다."),
    UNAUTHORIZED(Status.UNAUTHORIZED, 5006, "접근 정보가 잘못되었습니다."),
    ALREADY_ROOM(Status.BAD_REQUEST, 5007, "이미 존재하는 채팅방입니다."),
    ;

    private final Status status;
    private final int exceptionCode;
    private final String message;

    MessageExceptionType(Status status, int exceptionCode, String message) {
        this.status = status;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public int exceptionCode() {
        return exceptionCode;
    }

    @Override
    public String message() {
        return message;
    }
}