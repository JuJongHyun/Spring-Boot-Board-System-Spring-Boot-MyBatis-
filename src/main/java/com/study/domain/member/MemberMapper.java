package com.study.domain.member;

import com.study.common.dto.SearchDTO;
import com.study.domain.member.dto.MemberRequest;
import com.study.domain.member.dto.MemberResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MemberMapper {

    void save(MemberRequest params);

    MemberResponse findByLoginId(String loginId);

    MemberResponse findById(Long id);

    void update(MemberRequest params);

    void deleteById(Long id);

    int countByLoginId(String loginId);

    void changePassword(@Param("id") Long id, @Param("password") String password);

    void updateProfile(@Param("id") Long id,
                       @Param("name") String name,
                       @Param("gender") Gender gender,
                       @Param("birthday") LocalDate birthday);

    void updateProfileImage(@Param("id") Long id, @Param("profileImage") String profileImage);

    void updateNotification(@Param("id") Long id,
                            @Param("commentNotiYn") boolean commentNotiYn,
                            @Param("replyNotiYn") boolean replyNotiYn);

    void updateRole(@Param("id") Long id, @Param("role") MemberRole role);

    // ── 관리자용 ──────────────────────────────────────────

    int countActive();

    int countToday();

    int countForAdmin(SearchDTO params);

    List<MemberResponse> findAllForAdmin(SearchDTO params);

}
