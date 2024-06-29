package psam.portfolio.sunder.english.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTStart;
import psam.portfolio.sunder.english.domain.study.model.request.StudyWordPATCHCorrect;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class StudyCommandService {
    public UUID start(UUID studentId, StudyPOSTStart post) {
        return null;
    }

    public UUID submit(UUID studentId, UUID studyId, StudyPATCHSubmit patch) {
        return null;
    }

    public Long correctStudyWord(UUID teacherId, UUID studyId, Long studyWordId, StudyWordPATCHCorrect patch) {
        return null;
    }

    public UUID delete(UUID teacherId, UUID studyId) {
        return null;
    }
}
