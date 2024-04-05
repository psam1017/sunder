package psam.portfolio.sunder.english.domain.student.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHInfo;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPATCHStatus;
import psam.portfolio.sunder.english.domain.student.model.request.StudentPOST;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class StudentCommandService {

    public UUID register(UUID studentId, StudentPOST post) {
        return null;
    }

    public UUID updateInfo(UUID userId, UUID studentId, StudentPATCHInfo patch) {
        return null;
    }

    public UserStatus changeStatus(UUID directorId, UUID studentId, StudentPATCHStatus patch) {
        return null;
    }
}
