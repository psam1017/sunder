package psam.portfolio.sunder.english.domain.study.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Component
public class StudyScheduler {

    // TODO: 2024-06-29 시작한지 하루가 지날 동안 제출하지 않은 Study 는 자동 삭제
}
