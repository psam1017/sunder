package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchStudyException;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudyWord;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static psam.portfolio.sunder.english.domain.study.model.entity.QStudy.study;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudyQueryRepository {

    private final JPAQueryFactory query;
    private final EntityManager em;

    public Study getById(UUID uuid) {
        Study entity = em.find(Study.class, uuid);
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Study getOne(BooleanExpression... expressions) {
        Study entity = query
                .select(study)
                .from(study)
                .where(expressions)
                .fetchOne();
        if (entity == null) {
            throw new NoSuchStudyException();
        }
        return entity;
    }

    public Optional<Study> findOne(BooleanExpression... expressions) {
        return Optional.ofNullable(
                query.select(study)
                        .from(study)
                        .where(expressions)
                        .fetchOne()
        );
    }

    public Optional<Study> findById(UUID uuid) {
        return Optional.ofNullable(em.find(Study.class, uuid));
    }

    public List<Study> findAll(BooleanExpression... expressions) {
        return query.select(study)
                .from(study)
                .where(expressions)
                .fetch();
    }

    public long findNextSequenceOfLastStudy() {
        Long lastSequence = query.select(study.sequence.max())
                .from(study)
                .fetchOne();
        return lastSequence == null ? 1 : lastSequence + 1;
    }

    public List<StudySlicingResponse> findAllBySlicingSearchCond(Student student, StudySlicingSearchCond cond) {
        return findAllBySlicingSearchCondQuery(student, null, cond);
    }

    public List<StudySlicingResponse> findAllBySlicingSearchCond(Teacher teacher, StudySlicingSearchCond cond) {
        return findAllBySlicingSearchCondQuery(null, teacher, cond);
    }

    // lastSequence 의 비교 방향과 정렬 방향이 일치해야 한다.
    private List<StudySlicingResponse> findAllBySlicingSearchCondQuery(Student student, Teacher teacher, StudySlicingSearchCond cond) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        return query.select(Projections.constructor(StudySlicingResponse.class,
                        study.id,
                        study.sequence,
                        study.title,
                        study.status,
                        study.type,
                        study.classification,
                        study.target,
                        study.submitDateTime,
                        study.student.id.as("studentId"),
                        study.student.attendanceId.as("attendanceId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        new CaseBuilder()
                                .when(qStudyWord.correct.isTrue())
                                .then(1)
                                .otherwise(0)
                                .sum()
                                .as("correctCount"),
                        qStudyWord.count().castToNum(Integer.class).as("totalCount")
                ))
                .distinct()
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        sequenceLt(cond.getLastSequence()),
                        studentIdEq(student),
                        academyIdEqByTeacher(teacher),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime()),
                        titleContains(cond.getSplitStudyWord()),
                        studentNameContains(cond.getStudentName()),
                        studentSchoolGradeEq(cond.getSchoolGrade())
                )
                .groupBy(study.id)
                .orderBy(study.sequence.desc())
                .limit(cond.getLimit())
                .fetch();
    }

    private static BooleanExpression sequenceLt(Long sequence) {
        return sequence == null ? null : study.sequence.lt(sequence);
    }

    private static BooleanExpression studentIdEq(Student student) {
        return student == null ? null : study.student.id.eq(student.getId());
    }

    private BooleanExpression academyIdEqByTeacher(Teacher teacher) {
        return teacher == null ? null : study.student.academy.id.eq(teacher.getAcademy().getId());
    }

    private static BooleanExpression createdDateTimeGoe(LocalDateTime createdDateTime) {
        return createdDateTime == null ? null : study.createdDateTime.goe(createdDateTime);
    }

    private static BooleanExpression createdDateTimeLoe(LocalDateTime createdDateTime) {
        return createdDateTime == null ? null : study.createdDateTime.loe(createdDateTime);
    }

    private static BooleanExpression titleContains(String[] titles) {
        if (titles == null || titles.length == 0) {
            return null;
        }
        return Arrays.stream(titles)
                .map(t -> study.title.toLowerCase().contains(t))
                .reduce(BooleanExpression::or)
                .orElse(null);
    }

    private static BooleanExpression studentNameContains(String studentName) {
        if (StringUtils.hasText(studentName)) {
            return Expressions
                    .stringTemplate("replace({0}, ' ', '')", study.student.name.toLowerCase())
                    .contains(studentName.replaceAll(" ", "").toLowerCase());
        }
        return null;
    }

    private static BooleanExpression studentSchoolGradeEq(Integer schoolGrade) {
        return schoolGrade == null ? null : study.student.school.grade.eq(schoolGrade);
    }

    public long countAll() {
        Long count = query.select(study.id.count())
                .from(study)
                .fetchOne();
        return count == null ? 0 : count;
    }
}
