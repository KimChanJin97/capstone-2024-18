package capstone.facefriend.member.service;


import static capstone.facefriend.member.domain.basicInfo.BasicInfo.AgeDegree;
import static capstone.facefriend.member.domain.basicInfo.BasicInfo.AgeGroup;
import static capstone.facefriend.member.domain.basicInfo.BasicInfo.Gender;
import static capstone.facefriend.member.domain.basicInfo.BasicInfo.HeightGroup;
import static capstone.facefriend.member.domain.basicInfo.BasicInfo.Region;
import static capstone.facefriend.member.exception.member.MemberExceptionType.NOT_FOUND;

import capstone.facefriend.member.domain.basicInfo.BasicInfo;
import capstone.facefriend.member.domain.member.Member;
import capstone.facefriend.member.dto.basicInfo.BasicInfoRequest;
import capstone.facefriend.member.dto.basicInfo.BasicInfoResponse;
import capstone.facefriend.member.exception.member.MemberException;
import capstone.facefriend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BasicInfoService {

    private final MemberRepository memberRepository;

    public BasicInfoResponse getBasicInfo(Long memberId) {
        Member member = findMemberById(memberId);
        BasicInfo basicInfo = member.getBasicInfo();

        return BasicInfoResponse.of(basicInfo);
    }

    @Transactional
    public BasicInfoResponse putBasicInfo(Long memberId, BasicInfoRequest request) {
        Member member = findMemberById(memberId);
        BasicInfo oldBasicInfo = member.getBasicInfo();

        oldBasicInfo.setNickname(request.nickname());
        oldBasicInfo.setGender(Gender.valueOf(request.gender()));
        oldBasicInfo.setAgeGroup(AgeGroup.valueOf(request.ageGroup()));
        oldBasicInfo.setAgeDegree(AgeDegree.valueOf(request.ageDegree()));
        oldBasicInfo.setHeightGroup(HeightGroup.valueOf(request.heightGroup()));
        oldBasicInfo.setRegion(Region.valueOf(request.region()));

        member.setBasicInfo(oldBasicInfo);

        return BasicInfoResponse.of(oldBasicInfo);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND));
    }
}
