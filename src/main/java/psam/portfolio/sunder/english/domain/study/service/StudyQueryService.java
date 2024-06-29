package psam.portfolio.sunder.english.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudyWordFullResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudyQueryService {

    // TODO: 2024-06-29 SliceInfo ?

    public Map<String, Object> getStudyList(UUID userId, StudySlicingSearchCond cond) {
        return null;
    }

    public StudyFullResponse getDetail(UUID userId, UUID studyId, String select) {
        return null;
    }
}
