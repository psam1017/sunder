package psam.portfolio.sunder.english.domain.study.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.exception.NoSuchStudyException;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudyWord;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyStatisticSearchCond;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static psam.portfolio.sunder.english.domain.study.model.entity.QStudy.study;
import static psam.portfolio.sunder.english.domain.study.model.response.StudyStatisticResponse.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Repository
public class StudyQueryRepository {

    private final static int TOP_STUDENT_LIMIT = 3;
    private final static int NON_SUBMIT_STUDENT_LIMIT = 5;

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
                        qStudyWord.correct.count().castToNum(Integer.class).as("correctCount"),
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

    public StudyCountByStatus countByStatus(StudyStatisticSearchCond cond, UUID academyId) {
        List<Tuple> tuples = query.select(
                        study.status,
                        study.id.count()
                )
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.status)
                .fetch();

        StudyCountByStatus status = new StudyCountByStatus();
        for (Tuple tuple : tuples) {
            StudyStatus studyStatus = tuple.get(study.status);
            Long count = tuple.get(study.id.count());
            if (studyStatus != null) {
                switch (studyStatus) {
                    case ASSIGNED -> status.setAssignedCount(count);
                    case STARTED -> status.setStartedCount(count);
                    case SUBMITTED -> status.setSubmittedCount(count);
                }
            }
        }
        return status;
    }

    public StudyCountByType countByType(StudyStatisticSearchCond cond, UUID academyId) {
        List<Tuple> tuples = query.select(
                        study.type,
                        study.id.count()
                )
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.type)
                .fetch();

        StudyCountByType type = new StudyCountByType();
        for (Tuple tuple : tuples) {
            StudyType studyType = tuple.get(study.type);
            Long count = tuple.get(study.id.count());
            if (studyType != null) {
                switch (studyType) {
                    case TRACING -> type.setTracingCount(count);
                    case SELECT -> type.setSelectCount(count);
                    case WRITING -> type.setWritingCount(count);
                }
            }
        }
        return type;
    }

    public StudyCountByClassification countByClassification(StudyStatisticSearchCond cond, UUID academyId) {
        List<Tuple> tuples = query.select(
                        study.classification,
                        study.id.count()
                )
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.classification)
                .fetch();

        StudyCountByClassification classification = new StudyCountByClassification();
        for (Tuple tuple : tuples) {
            StudyClassification studyClassification = tuple.get(study.classification);
            Long count = tuple.get(study.id.count());
            if (studyClassification != null) {
                switch (studyClassification) {
                    case EXAM -> classification.setExamCount(count);
                    case PRACTICE -> classification.setPracticeCount(count);
                }
            }
        }
        return classification;
    }

    public StudyCountByTarget countByTarget(StudyStatisticSearchCond cond, UUID academyId) {
        List<Tuple> tuples = query.select(
                        study.target,
                        study.id.count()
                )
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.target)
                .fetch();

        StudyCountByTarget target = new StudyCountByTarget();
        for (Tuple tuple : tuples) {
            StudyTarget studyTarget = tuple.get(study.target);
            Long count = tuple.get(study.id.count());
            if (studyTarget != null) {
                switch (studyTarget) {
                    case KOREAN -> target.setKoreanCount(count);
                    case ENGLISH -> target.setEnglishCount(count);
                }
            }
        }
        return target;
    }

    public List<StudyCountByDay> countByDay(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Integer> day = study.createdDateTime.dayOfYear();
        List<Tuple> tuples = query.select(
                        day,
                        study.id.count(),
                        qStudyWord.correct.count(),
                        qStudyWord.count()
                )
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(day)
                .fetch();

        int year = LocalDate.now().getYear();
        List<StudyCountByDay> days = new ArrayList<>();
        for (Tuple tuple : tuples) {
            StudyCountByDay dayCount = new StudyCountByDay();
            Integer dayOfYear = tuple.get(day);
            if (dayOfYear == null) {
                continue;
            }
            dayCount.setStudyDate(LocalDate.ofYearDay(year, dayOfYear));
            dayCount.setStudyCount(tuple.get(study.id.count()));
            dayCount.setCorrectCount(tuple.get(qStudyWord.correct.count()));
            dayCount.setTotalCount(tuple.get(qStudyWord.count()));
            days.add(dayCount);
        }
        return days;
    }

    public List<TopStudent> findBestStudentsByAnswerRate(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Long> correctPercent = qStudyWord.correct.count().divide(qStudyWord.count().doubleValue());
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.count().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(correctPercent.desc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findWorstStudentsByAnswerRate(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Long> correctPercent = qStudyWord.correct.count().divide(qStudyWord.count().doubleValue());
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.count().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(correctPercent.asc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findBestStudentsByStudyCount(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Long> correctPercent = qStudyWord.correct.count().divide(qStudyWord.count().doubleValue());
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.count().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(studyWordCount.desc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<TopStudent> findWorstStudentsByStudyCount(StudyStatisticSearchCond cond, UUID academyId) {
        QStudyWord qStudyWord = QStudyWord.studyWord;
        NumberExpression<Long> correctPercent = qStudyWord.correct.count().divide(qStudyWord.count().doubleValue());
        NumberExpression<Long> studyWordCount = qStudyWord.id.count();

        return query.select(Projections.constructor(TopStudent.class,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade"),
                        correctPercent.multiply(100).as("correctPercent"),
                        study.id.count().as("studyCount"),
                        studyWordCount.as("studyWordCount")
                ))
                .from(study)
                .join(study.studyWords, qStudyWord)
                .where(
                        academyIdEq(academyId),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime())
                )
                .groupBy(study.student.id)
                .orderBy(studyWordCount.asc())
                .limit(TOP_STUDENT_LIMIT)
                .fetch();
    }

    public List<OldHomework> findOldHomeworks(StudyStatisticSearchCond cond, UUID academyId) {
        return query.select(Projections.constructor(OldHomework.class,
                        study.id.as("studyId"),
                        study.title,
                        study.status,
                        study.createdDateTime,
                        study.student.id.as("studentId"),
                        study.student.name.as("studentName"),
                        study.student.school.name.as("schoolName"),
                        study.student.school.grade.as("schoolGrade")
                ))
                .from(study)
                .where(
                        academyIdEq(academyId),
                        studentIdEq(cond.getStudentId()),
                        createdDateTimeGoe(cond.getStartDateTime()),
                        createdDateTimeLoe(cond.getEndDateTime()),
                        statusNe(StudyStatus.SUBMITTED)
                )
                .orderBy(study.createdDateTime.asc())
                .limit(NON_SUBMIT_STUDENT_LIMIT)
                .fetch();
    }

    private BooleanExpression academyIdEq(UUID academyId) {
        return academyId == null ? null : study.student.academy.id.eq(academyId);
    }

    private BooleanExpression studentIdEq(UUID studentId) {
        return studentId == null ? null : study.student.id.eq(studentId);
    }

    private BooleanExpression statusNe(StudyStatus status) {
        return status == null ? null : study.status.ne(status);
    }
}
