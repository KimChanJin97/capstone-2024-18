package capstone.facefriend.member.controller;

import static capstone.facefriend.member.exception.analysis.AnalysisInfoExceptionType.FAIL_TO_ANALYSIS;
import static capstone.facefriend.member.service.AnalysisInfoService.AnalysisInfoTotal;

import capstone.facefriend.auth.support.AuthMember;
import capstone.facefriend.member.dto.analysisInfo.AnalysisInfoFullResponse;
import capstone.facefriend.member.dto.analysisInfo.AnalysisInfoFullShortResponse;
import capstone.facefriend.member.dto.analysisInfo.AnalysisInfoShortResponse;
import capstone.facefriend.member.exception.analysis.AnalysisInfoException;
import capstone.facefriend.member.service.AnalysisInfoRequestor;
import capstone.facefriend.member.service.AnalysisInfoService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
public class AnalysisInfoController {

    private final AnalysisInfoService analysisInfoService;
    private final AnalysisInfoRequestor analysisInfoRequestor;

    @PutMapping("/analysis-info")
    public ResponseEntity<AnalysisInfoFullShortResponse> postAnalysisInfo(
            @RequestPart("origin") MultipartFile origin,
            @AuthMember Long memberId
    ) {
        CompletableFuture<AnalysisInfoTotal> futureAnalysisInfoTotal = CompletableFuture
                .supplyAsync(() -> analysisInfoRequestor.analyze(origin, memberId));

        AnalysisInfoTotal total = fetchAnalysisInfoTotal(futureAnalysisInfoTotal);

        return ResponseEntity.ok(analysisInfoService.bindAnalysisInfoTotal(total, memberId));
    }

    @GetMapping("/analysis-info/full-short")
    public ResponseEntity<AnalysisInfoFullShortResponse> getAnalysisInfoFullShort(
            @AuthMember Long memberId
    ) {
        return ResponseEntity.ok(analysisInfoService.getAnalysisInfoFullShort(memberId));
    }

    @GetMapping("/analysis-info/full")
    public ResponseEntity<AnalysisInfoFullResponse> getAnalysisInfoFull(
            @AuthMember Long memberId
    ) {
        return ResponseEntity.ok(analysisInfoService.getAnalysisInfoFull(memberId));
    }

    @GetMapping("/analysis-info/short")
    public ResponseEntity<AnalysisInfoShortResponse> getAnalysisInfoShort(
            @AuthMember Long memberId
    ) {
        return ResponseEntity.ok(analysisInfoService.getAnalysisInfoShort(memberId));
    }

    private AnalysisInfoTotal fetchAnalysisInfoTotal(CompletableFuture<AnalysisInfoTotal> futureAnalysisInfoTotal) {
        AnalysisInfoTotal total;

        try {
            total = futureAnalysisInfoTotal.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AnalysisInfoException(FAIL_TO_ANALYSIS);
        }

        return total;
    }
}
