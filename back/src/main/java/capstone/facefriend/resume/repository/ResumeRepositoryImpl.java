package capstone.facefriend.resume.repository;

import static capstone.facefriend.member.domain.analysisInfo.QAnalysisInfo.*;
import static capstone.facefriend.member.domain.faceInfo.QFaceInfo.faceInfo;
import static capstone.facefriend.member.domain.member.QMember.member;
import static capstone.facefriend.member.exception.member.MemberExceptionType.NOT_FOUND;
import static capstone.facefriend.resume.domain.QResume.resume;

import capstone.facefriend.member.domain.analysisInfo.QAnalysisInfo;
import capstone.facefriend.member.domain.member.Member;
import capstone.facefriend.member.exception.member.MemberException;
import capstone.facefriend.member.repository.MemberRepository;
import capstone.facefriend.resume.domain.Resume;
import capstone.facefriend.resume.dto.QResumePreviewResponse;
import capstone.facefriend.resume.dto.ResumePreviewResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;

    private static final List<Integer> GOOD_COMBI_IN_CASE_0 = List.of(2, 4); // 화
    private static final List<Integer> GOOD_COMBI_IN_CASE_1 = List.of(2, 4); // 수
    private static final List<Integer> GOOD_COMBI_IN_CASE_2 = List.of(0, 1); // 목
    private static final List<Integer> GOOD_COMBI_IN_CASE_3 = List.of(1, 4); // 금
    private static final List<Integer> GOOD_COMBI_IN_CASE_4 = List.of(0, 3); // 토

    public Page<ResumePreviewResponse> getResumesByGoodCombi(Long memberId, Pageable pageable) {
        Member me = findMemberById(memberId);
        Integer faceShapeId = me.getAnalysisInfo().getFaceShapeIdNum();

        BooleanBuilder hasGoodCombi = new BooleanBuilder();
        switch (faceShapeId) {
            case 0: // 화
                hasGoodCombi.and(analysisInfo.faceShapeIdNum.in(GOOD_COMBI_IN_CASE_0));
                break;
            case 1: // 수
                hasGoodCombi.and(analysisInfo.faceShapeIdNum.in(GOOD_COMBI_IN_CASE_1));
                break;
            case 2: // 목
                hasGoodCombi.and(analysisInfo.faceShapeIdNum.in(GOOD_COMBI_IN_CASE_2));
                break;
            case 3: // 금
                hasGoodCombi.and(analysisInfo.faceShapeIdNum.in(GOOD_COMBI_IN_CASE_3));
                break;
            case 4: // 토
                hasGoodCombi.and(analysisInfo.faceShapeIdNum.in(GOOD_COMBI_IN_CASE_4));
                break;
        }

        List<ResumePreviewResponse> content = queryFactory
                .select(new QResumePreviewResponse(
                        resume.id,
                        faceInfo.generatedS3url))
                .from(resume)
                .innerJoin(resume.member, member)
                .innerJoin(member.faceInfo, faceInfo)
                .innerJoin(member.analysisInfo, analysisInfo)
                .where(hasGoodCombi)
                .where(resume.member.ne(me))
                .orderBy(resume.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(resume.count())
                .from(resume)
                .innerJoin(resume.member, member)
                .innerJoin(member.analysisInfo, analysisInfo)
                .where(hasGoodCombi)
                .where(resume.member.ne(me))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<ResumePreviewResponse> getResumesByCategory(Long memberId, String category, Pageable pageable) {
        Member me = findMemberById(memberId);

        List<ResumePreviewResponse> content = queryFactory
                .select(new QResumePreviewResponse(
                        resume.id,
                        faceInfo.generatedS3url))
                .from(resume)
                .innerJoin(resume.member, member)
                .innerJoin(member.faceInfo, faceInfo)
                .where(resume.categories.contains(Resume.Category.valueOf(category)))
                .where(resume.member.ne(me))
                .orderBy(resume.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(resume.count())
                .from(resume)
                .innerJoin(resume.member)
                .innerJoin(resume.categories)
                .where(resume.categories.contains(Resume.Category.valueOf(category)))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND));
    }
}
