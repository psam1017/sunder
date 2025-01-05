package psam.portfolio.sunder.english.domain.academy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.response.AcademyShareSummary;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareQueryRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyShareQueryService {

    private final AcademyShareQueryRepository academyShareQueryRepository;

    public List<AcademyShareSummary> getShares(UUID academyId) {
        return academyShareQueryRepository.findAll(
                        QAcademyShare.academyShare.sharingAcademy.id.eq(academyId)
                ).stream()
                .map(AcademyShareSummary::from)
                .toList();
    }
}
