package com.study.domain.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface
MemberMapper {

    /**
     * 회원 정보 저장 (회원가입)
     * @param params - 회원 정보
     */
    void save(MemberRequest params);

    /**
     * 회원 상세정보 조회
     * @param loginId - UK
     * @return 회원 상세정보
     */
    MemberResponse findByLoginId(String loginId);

    /**
     * 회원 정보 수정
     * @param params - 회원 정보
     */
    void update(MemberRequest params);

    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param id - PK
     */
    void deleteById(Long id);

    /**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param loginId - UK
     * @return 회원 수
     */
    int countByLoginId(String loginId);

    /**
     * 회원 PK 조회
     * @param id - PK
     * @return 회원 상세정보
     */
    MemberResponse findById(Long id);

    /**
     * 비밀번호 변경
     * @param id - PK
     * @param password - 인코딩된 새 비밀번호
     */
    void changePassword(@org.apache.ibatis.annotations.Param("id") Long id,
                        @org.apache.ibatis.annotations.Param("password") String password);

    /**
     * 개인정보 변경 (이름, 성별, 생년월일)
     * @param id - PK
     * @param params - 변경할 정보
     */
    void updateProfile(@org.apache.ibatis.annotations.Param("id") Long id,
                       @org.apache.ibatis.annotations.Param("name") String name,
                       @org.apache.ibatis.annotations.Param("gender") Gender gender,
                       @org.apache.ibatis.annotations.Param("birthday") java.time.LocalDate birthday);

    /**
     * 프로필 이미지 변경
     * @param id - PK
     * @param profileImage - 저장된 파일명
     */
    void updateProfileImage(@org.apache.ibatis.annotations.Param("id") Long id,
                            @org.apache.ibatis.annotations.Param("profileImage") String profileImage);

    /**
     * 알림 설정 변경
     * @param id - PK
     * @param commentNotiYn - 댓글 알림
     * @param replyNotiYn - 답글 알림
     */
    void updateNotification(@org.apache.ibatis.annotations.Param("id") Long id,
                            @org.apache.ibatis.annotations.Param("commentNotiYn") boolean commentNotiYn,
                            @org.apache.ibatis.annotations.Param("replyNotiYn") boolean replyNotiYn);

}
