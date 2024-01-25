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

    // TODO: 2024-01-23 academy 의 이름을 중복 체크하는 서비스 GET /api/academy/check-dupl?name={name}
    // TODO: 2024-01-23 email 중복 체크하는 서비스. 단, PENDING 과 TRIAL 은 중복체크에서 제외 GET /api/user/check-dupl?email={email}
    // TODO: 2024-01-23 연락처를 중복 체크하는 서비스 GET /api/user/check-dupl?phone={phone}

    // TODO: 2024-01-23 academy 와 teacher(director 인 원장)을 생성하는 서비스 -> 원장의 email 로 인증토큰을 보내는 서비스 POST /api/academy/new
    // TODO: 2024-01-23 email 로 보낸 인증토큰이 포함된 링크를 클릭하여, 인증하는 서비스 -> 인증이 완료되면 teacher 를 활성화. director 라면 academy 도 같이 활성화 POST /api/teacher/verify?token={token}

    // TODO: 2024-01-23 teacher 인 선생님이 가입하는 서비스. teacher 는 academy uuid 를 요청값으로 보내야 함. -> email 로 인증토큰 전송 POST /api/teacher/new
    // -> academy uuid 를 알고 있으므로 이미 director 로부터 academy 정보를 전달 받은 인증된 사용자라는 것으로 간주한다.

    // TODO: 2024-01-23 teacher 상태를 변경하는 서비스(FORBIDDEN, WITHDRAWN) PUT /api/teacher/status

    // TODO: 2024-01-23 academy 에서 teacher 를 바로 등록하는 서비스 @Secured("ROLE_DIRECTOR") POST /api/academy/teacher/new

    // TODO: 2024-01-23 teacher 가 본인의 정보를 수정하는 서비스 PUT /api/teacher/info
    // TODO: 2024-01-23 teacher 의 비밀번호를 변경하는 서비스 PUT /api/teacher/password
    // TODO: 2024-01-23 teacher 가 탈퇴하는 서비스 PUT /api/teacher/withdraw
    // TODO: 2024-01-23 teacher 가 자신의 비밀번호를 잊었을 때 임시 비밀번호를 발급받는 서비스 POST /api/teacher/password/temporary

    // TODO: 2024-01-23 academy 의 정보를 수정하는 서비스 @Secured("ROLE_DIRECTOR") PUT /api/academy/info

    // TODO: 2024-01-23 teacher 1명의 detail 을 조회하는 서비스. 추가 조회를 위한 select 에는 lessons 가 있다. GET /api/teacher/detail?teacherUuid={teacherUuid}&select={lesson
    // TODO: 2024-01-23 academy 1개의 detail 을 조회하는 서비스. 추가 조회를 위한 select 에는 teachers, students, lessons 가 있다. GET /api/academy/detail?academyUuid={academyUuid}&select={teachers,students,lessons}

    // TODO: 2024-01-23 teacher 의 목록을 조회하는 서비스. 이때 조회 기준은 academy, status, lesson 요일, lesson 시간대, 수업에 참여하는 학생의 이름이 있고, 정렬 기준은 status, lesson 의 요일 or grade(학년) or 시간대, 선생님의 이름이다.
    // GET /api/teacher/list?academyUuid={academyUuid}&status={status}&lessonDay={lessonDay}&lessonTime={lessonTime}&studentName={studentName}&sort={status|lessonDay|lessonTime|grade|teacherName}
}
