package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.AcademyQueryRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherService {

    private final TeacherCommandRepository teacherCommandRepository;
    private final TeacherQueryRepository teacherQueryRepository;
    private final AcademyCommandRepository academyCommandRepository;
    private final AcademyQueryRepository academyQueryRepository;

    // TODO: 2024-01-23 EmailUtils
    // TODO: 2024-01-23 PasswordUtils

    // TODO: 2024-01-23 academy 의 중복 체크하는 서비스
    // TODO: 2024-01-23 email 중복 체크하는 서비스. 단, PENDING 과 TRIAL 은 중복체크에서 제외

    // TODO: 2024-01-23 academy 와 teacher(director 인 원장)을 생성하는 서비스 -> 원장의 email 로 인증토큰을 보내는 서비스

    // TODO: 2024-01-23 teacher 인 선생님이 가입하는 서비스. teacher 는 academy uuid 를 요청값으로 보내야 함. -> email 로 인증토큰 전송
    // TODO: 2024-01-23 email 로 보낸 인증토큰이 포함된 링크를 클릭하면, 인증토큰을 받고, 인증하는 서비스 -> 인증이 완료되면 teacher 를 활성화. director 라면 academy 도 같이 활성화

    // TODO: 2024-01-23 teacher list 를 조회하는 서비스 ?param=academyUuid
    // TODO: 2024-01-23 teacher 상태를 변경하는 서비스(승인, 탈퇴, 거절)

    // TODO: 2024-01-23 academy 에서 teacher 를 승인하는 서비스. 즉, teacher 는 email 인증을 거치고 director 가 승인을 해야 PENDING 에서 ACTIVE 로 변경된다.

    // TODO: 2024-01-23 academy 에서 teacher 를 바로 등록하는 서비스

    // TODO: 2024-01-23 teacher 가 본인의 정보를 수정하는 서비스
    // TODO: 2024-01-23 teacher 의 비밀번호를 변경하는 서비스
    // TODO: 2024-01-23 teacher 가 탈퇴하는 서비스
    // TODO: 2024-01-23 teacher 가 자신의 비밀번호를 잊었을 때 임시 비밀번호를 발급받는 서비스

    // TODO: 2024-01-23 academy 의 정보를 수정하는 서비스

    // TODO: 2024-01-23 teacher 1명의 detail 을 조회하는 서비스. 추가 조회를 위한 select 에는 lessons 가 있다.
    // TODO: 2024-01-23 academy 1개의 detail 을 조회하는 서비스. 추가 조회를 위한 select 에는 teachers, students, lessons 가 있다.

    // TODO: 2024-01-23 teacher 의 목록을 조회하는 서비스. 이때 조회 기준은 academy, status, lesson 요일, lesson 시간대, 수업에 참여하는 학생의 이름이 있고, 정렬 기준은 status, lesson 의 요일 or grade(학년) or 시간대, 선생님의 이름이다.
}
