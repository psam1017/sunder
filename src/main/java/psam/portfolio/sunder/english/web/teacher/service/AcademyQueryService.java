package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.exception.OneParamToCheckAcademyDuplException;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;

import java.util.Optional;

import static psam.portfolio.sunder.english.web.teacher.model.entity.QAcademy.academy;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AcademyQueryService {

    private final AcademyQueryRepository academyQueryRepository;

    /**
     * 학원 등록 시 중복 체크 서비스
     * @param name 학원 이름
     * @param phone 학원 전화번호
     * @param email 학원 이메일
     * @return 중복 여부
     */
    public boolean checkDuplication(String name, String phone, String email) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasPhone = StringUtils.hasText(phone);
        boolean hasEmail = StringUtils.hasText(email);

        if (!hasOnlyOne(hasName, hasPhone, hasEmail)) {
            throw new OneParamToCheckAcademyDuplException();
        }

        Optional<Academy> optAcademy = Optional.empty();
        if (hasName) {
            optAcademy = academyQueryRepository.findOne(
                    academy.name.eq(name),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasPhone) {
            optAcademy = academyQueryRepository.findOne(
                    academy.phone.eq(phone),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        } else if (hasEmail) {
            optAcademy = academyQueryRepository.findOne(
                    academy.email.eq(email),
                    academy.status.ne(AcademyStatus.PENDING)
            );
        }
        return optAcademy.isEmpty();
    }

    private static boolean hasOnlyOne(boolean a, boolean b, boolean c) {
        return a ^ b ^ c && !(a && b && c);
    }


    /* todo
    GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students,lessons}
    학원 상세 정보 조회 서비스
     */
}
