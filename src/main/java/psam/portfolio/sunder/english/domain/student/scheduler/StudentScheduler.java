package psam.portfolio.sunder.english.domain.student.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.student.repository.StudentCommandRepository;

@RequiredArgsConstructor
@Transactional
@Component
public class StudentScheduler {


    private final StudentCommandRepository studentCommandRepository;

    /**
     * 매년 1월 1일 0시 0분에 학생들의 학년을 1씩 증가시킴
     */
    @Scheduled(cron = "0 0 0 1 1 *")
    public void increaseGrade() {
        studentCommandRepository.increaseGradeLessThen(
                12
        );
    }

}
