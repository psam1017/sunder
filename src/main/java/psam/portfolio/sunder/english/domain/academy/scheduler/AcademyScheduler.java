package psam.portfolio.sunder.english.domain.academy.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademy;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.BookCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordCommandRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.QStudent;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.study.repository.StudyCommandRepository;
import psam.portfolio.sunder.english.domain.study.repository.StudyWordCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.QTeacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Component
public class AcademyScheduler {

    private final AcademyQueryRepository academyQueryRepository;
    private final StudentQueryRepository studentQueryRepository;
    private final TeacherQueryRepository teacherQueryRepository;

    private final StudyWordCommandRepository studyWordCommandRepository;
    private final StudyCommandRepository studyCommandRepository;
    private final WordCommandRepository wordCommandRepository;
    private final BookCommandRepository bookCommandRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final UserCommandRepository userCommandRepository;
    private final AcademyShareCommandRepository academyShareCommandRepository;
    private final AcademyCommandRepository academyCommandRepository;

    // TODO: 2025-01-29 테스트
    /**
     * 매일 0시 1분에 탈퇴 요청 후 14일 이상 지난 학원은 삭제
     * 사실 소수를 제외하고는 외래키가 없기 때문에 참조되지 않는 테이블부터 삭제하지는 않아도 되지만, 혹시 모르니 순서를 조정.
     * 그리고 소수의 외래키도 @CollectionTable 로 연결되어 있는, 즉 생명주기가 같은 것이기 때문에 삭제할 때 문제가 생기지 않고 같이 삭제됨.
     * -> terms(역색인), 학습단어, 학습, 교재단어, 교재, 사용자 권한, 사용자, 학원 공유 정보, 학원
     */
    @Scheduled(cron = "0 1 0 * * *")
    public void deleteWithdrawnAcademy() {
        QAcademy qAcademy = QAcademy.academy;
        List<UUID> academyIds = academyQueryRepository.findAllIds(
                qAcademy.status.eq(AcademyStatus.WITHDRAWN),
                qAcademy.withdrawalAt.before(LocalDateTime.now())
        );

        if (ObjectUtils.isEmpty(academyIds)) {
            return;
        }

        studyWordCommandRepository.deleteAllByAcademyIdIn(academyIds);
        studyCommandRepository.deleteAllByAcademyIdIn(academyIds);
        wordCommandRepository.deleteAllByAcademyIdIn(academyIds);
        bookCommandRepository.deleteAllByAcademyIdIn(academyIds);

        QStudent qStudent = QStudent.student;
        List<UUID> studentIds = studentQueryRepository.findAllIds(
                qStudent.academy.id.in(academyIds)
        );
        if (!ObjectUtils.isEmpty(studentIds)) {
            userRoleCommandRepository.deleteAllByUserIdIn(studentIds);
            userCommandRepository.deleteAllById(studentIds);
        }

        QTeacher qTeacher = QTeacher.teacher;
        List<UUID> teacherIds = teacherQueryRepository.findAllIds(
                qTeacher.academy.id.in(academyIds)
        );
        if (!ObjectUtils.isEmpty(teacherIds)) {
            userRoleCommandRepository.deleteAllByUserIdIn(teacherIds);
            userCommandRepository.deleteAllById(teacherIds);
        }

        academyShareCommandRepository.deleteAllByAcademyIdIn(academyIds);
        academyCommandRepository.deleteAllById(academyIds);
    }
}
