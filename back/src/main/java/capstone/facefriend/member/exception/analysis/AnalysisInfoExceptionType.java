package capstone.facefriend.member.exception.analysis;


import capstone.facefriend.common.exception.ExceptionType;
import capstone.facefriend.common.exception.Status;

public enum AnalysisInfoExceptionType implements ExceptionType {

    FAIL_TO_ANALYSIS(Status.NOT_FOUND, 6001, "관상 분석에 실패했습니다."),
    FAIL_TO_DESERIALIZE_ANALYSIS(Status.BAD_REQUEST, 6002, "관성 분석 역직렬화에 실패했습니다."),
    FAIL_TO_EXTRACT_FACE_SHAPE_ID_NUM(Status.BAD_REQUEST, 6003, "관성 분석 역직렬화에 실패했습니다."),
    FAIL_TO_GET_BYTES(Status.SERVER_ERROR, 6004, "바이트를 추출할 수 없습니다."),
    FAIL_TO_GET_RESPONSE_BODY(Status.SERVER_ERROR, 6005, "응답 바디를 읽을 수 없습니다.");

    private final Status status;
    private final int exceptionCode;
    private final String message;

    AnalysisInfoExceptionType(Status status, int exceptionCode, String message) {
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
