package community.independe.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "회원이 존재하지 않습니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 access token 입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 refresh token 입니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "잘못된 refresh token 요청 입니다."),
    REFRESH_IP_NOT_MATCH(HttpStatus.BAD_REQUEST, "저장된 토큰의 IP와 일치하지 않습니다."),
    FAVORITE_POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "즐겨찾기 정보가 존재하지 않습니다."),
    RECOMMEND_POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "게시글 추천 정보가 존재하지 않습니다."),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "댓글이 존재하지 않습니다"),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "게시글이 존재하지 않습니다."),
    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다."),
    REGION_NOT_AUTHENTICATE(HttpStatus.FORBIDDEN, "위치 인증이 완료되지 않았습니다."),
    USERNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 ID 입니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 닉네임 입니다.");



    private final HttpStatus status;
    private final String errorMessage;
}
