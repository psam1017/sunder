package psam.portfolio.sunder.english.domain.academy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareCommandRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareQueryRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class AcademyShareCommandService {

    private final AcademyQueryRepository academyQueryRepository;
    private final AcademyShareCommandRepository academyShareCommandRepository;
    private final AcademyShareQueryRepository academyShareQueryRepository;

    public void share(UUID sharingAcademyId, UUID sharedAcademyId) {
        Academy sharingAcademy = academyQueryRepository.getById(sharingAcademyId);
        Academy sharedAcademy = academyQueryRepository.getById(sharedAcademyId);
        AcademyShare academyShare = buildAcademyShare(sharingAcademy, sharedAcademy);
        academyShareCommandRepository.save(academyShare);
    }

    public void cancelShare(UUID sharingAcademyId, UUID sharedAcademyId) {
        AcademyShare academyShare = academyShareQueryRepository.getOne(
                QAcademyShare.academyShare.sharingAcademy.id.eq(sharingAcademyId),
                QAcademyShare.academyShare.sharedAcademy.id.eq(sharedAcademyId)
        );
        academyShareCommandRepository.delete(academyShare);
    }

    private static AcademyShare buildAcademyShare(Academy sharingAcademy, Academy sharedAcademy) {
        return AcademyShare.builder()
                .sharingAcademy(sharingAcademy)
                .sharedAcademy(sharedAcademy)
                .build();
    }
}
