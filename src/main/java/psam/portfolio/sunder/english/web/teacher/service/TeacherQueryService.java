package psam.portfolio.sunder.english.web.teacher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.teacher.repository.TeacherQueryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class TeacherQueryService {

    private final TeacherQueryRepository teacherQueryRepository;

    /*
    todo
    GET /api/teacher/detail?teacherUuid={teacherUuid}&select={lesson}
    선생님 상세 정보 조회 서비스

    GET /api/teacher/list?academyUuid={academyUuid}&status={status}&lessonDay={lessonDay}&lessonTime={lessonTime}&studentName={studentName}&sort={status|lessonDay|lessonTime|grade|teacherName}
    선생님 목록 조회 서비스
     */
}
