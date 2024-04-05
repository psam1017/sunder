package psam.portfolio.sunder.english.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.model.request.StudentSearchCond;
import psam.portfolio.sunder.english.domain.student.model.response.StudentFullResponse;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StudentQueryService {

    public boolean checkDuplication(UUID academyId, String attendanceId) {
        return false;
    }

    public Map<String, Object> getList(UUID userId, StudentSearchCond cond) {
        return null;
    }

    public StudentFullResponse get(UUID userId, UUID studentId) {
        return null;
    }
}
