package com.study.domain.member;

import com.study.common.exception.BusinessException;
import com.study.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long saveMember(final MemberRequest params) {
        params.encodingPassword(passwordEncoder);
        memberMapper.save(params);
        return params.getId();
    }

    public MemberResponse findMemberByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    public MemberResponse findMemberById(Long id) {
        return memberMapper.findById(id);
    }

    @Transactional
    public Long updateMember(final MemberRequest params) {
        params.encodingPassword(passwordEncoder);
        memberMapper.update(params);
        return params.getId();
    }

    @Transactional
    public Long deleteMemberById(final Long id) {
        memberMapper.deleteById(id);
        return id;
    }

    public int countMemberByLoginId(final String loginId) {
        return memberMapper.countByLoginId(loginId);
    }

    public MemberResponse login(final String loginId, final String password) {
        MemberResponse member = findMemberByLoginId(loginId);
        String encodedPassword = (member == null) ? "" : member.getPassword();
        if (member == null || !passwordEncoder.matches(password, encodedPassword)) {
            return null;
        }
        member.clearPassword();
        return member;
    }

    /**
     * 개인정보 변경 (이름, 성별, 생년월일)
     */
    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateRequest params) {
        if (memberMapper.findById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.updateProfile(memberId, params.getName(), params.getGender(), params.getBirthday());
    }

    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImage(Long memberId, String profileImage) {
        if (memberMapper.findById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.updateProfileImage(memberId, profileImage);
    }

    /**
     * 비밀번호 변경: 현재 비밀번호 검증 후 새 비밀번호로 교체
     */
    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest params) {
        MemberResponse member = memberMapper.findById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(params.getCurrentPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }
        if (!params.getNewPassword().equals(params.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
        memberMapper.changePassword(memberId, passwordEncoder.encode(params.getNewPassword()));
    }

    /**
     * 알림 설정 변경
     */
    @Transactional
    public void updateNotification(Long memberId, NotificationUpdateRequest params) {
        if (memberMapper.findById(memberId) == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        memberMapper.updateNotification(memberId, params.isCommentNotiYn(), params.isReplyNotiYn());
    }

    /**
     * 회원탈퇴: 비밀번호 확인 후 소프트 삭제
     */
    @Transactional
    public void withdraw(Long memberId, String password) {
        MemberResponse member = memberMapper.findById(memberId);
        if (member == null) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException(ErrorCode.WRONG_PASSWORD);
        }
        memberMapper.deleteById(memberId);
    }
}
