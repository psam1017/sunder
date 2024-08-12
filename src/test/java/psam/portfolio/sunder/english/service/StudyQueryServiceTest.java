package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTStart;
import psam.portfolio.sunder.english.domain.study.model.request.StudySlicingSearchCond;
import psam.portfolio.sunder.english.domain.study.model.request.StudyWordPATCHCorrect;
import psam.portfolio.sunder.english.domain.study.model.response.StudyFullResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudySlicingResponse;
import psam.portfolio.sunder.english.domain.study.model.response.StudyWordFullResponse;
import psam.portfolio.sunder.english.domain.study.repository.StudyQueryRepository;
import psam.portfolio.sunder.english.domain.study.service.StudyCommandService;
import psam.portfolio.sunder.english.domain.study.service.StudyQueryService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.slicing.SlicingInfo;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit.StudyWordPATCHSubmit;

@SuppressWarnings("unchecked")
class StudyQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    StudyQueryService sut; // system under test

    @Autowired
    StudyCommandService studyCommandService;

    @Autowired
    StudyQueryRepository studyQueryRepository;

    @DisplayName("학습이 연습이라면 정답을 볼 수 있다.")
    @Test
    public void getDetailWithClassificationPractice() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "1과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.TRACING, StudyClassification.PRACTICE, StudyTarget.KOREAN);
        UUID studyId = studyCommandService.start(student.getId(), post);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getDetail(student.getId(), studyId));

        // then
        assertThat(responseMap).containsKeys("study", "studyWords", "student");
        assertThat(responseMap).doesNotContainKey("teacher");

        Study getStudy = studyQueryRepository.getById(studyId);
        StudyFullResponse responseStudy = (StudyFullResponse) responseMap.get("study");
        List<StudyWord> getStudyWords = getStudy.getStudyWords();
        List<StudyWordFullResponse> responseStudyWords = (List<StudyWordFullResponse>) responseMap.get("studyWords");

        assertThat(responseStudy.getId()).isEqualTo(studyId);
        assertThat(responseStudy.getSequence()).isEqualTo(getStudy.getSequence());
        assertThat(responseStudy.isIgnoreCase()).isEqualTo(getStudy.isIgnoreCase());
        assertThat(responseStudy.getTitle()).isEqualTo(getStudy.getTitle());
        assertThat(responseStudy.getStatus()).isEqualTo(getStudy.getStatus());
        assertThat(responseStudy.getType()).isEqualTo(getStudy.getType());
        assertThat(responseStudy.getClassification()).isEqualTo(getStudy.getClassification());
        assertThat(responseStudy.getTarget()).isEqualTo(getStudy.getTarget());
        assertThat(responseStudy.getSubmitDateTime()).isNull();
        assertThat(responseStudy.getStudyRanges()).hasSize(1)
                .extracting("bookPublisher", "bookName", "bookChapter", "bookSubject")
                .containsExactly(
                        tuple(book.getPublisher(), book.getName(), book.getChapter(), book.getSubject())
                );
        assertThat(responseStudyWords).hasSize(10)
                .extracting("id", "question", "submit", "answer", "correct", "reason", "choices")
                .containsExactlyElementsOf(
                        getStudyWords.stream().map(sw -> tuple(sw.getId(), sw.getQuestion(), null, sw.getAnswer(), sw.getCorrect(), sw.getReason(), new ArrayList<>())).toList()
                );
        assertThat(responseStudyWords)
                .extracting("answer")
                .doesNotContainNull();
    }

    @DisplayName("학습이 제출되었다면 정답을 볼 수 있다.")
    @Test
    public void getDetailWithStatusSubmitted() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID studyId = studyCommandService.start(student.getId(), post);

        Study startStudy = studyQueryRepository.getById(studyId);
        List<StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
        for (StudyWord sw : startStudy.getStudyWords()) {
            submitStudyWords.add(new StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
        }
        StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);
        studyCommandService.submit(student.getId(), studyId, patchSubmit);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getDetail(student.getId(), studyId));

        // then
        assertThat(responseMap).containsKeys("study", "studyWords", "student");
        assertThat(responseMap).doesNotContainKey("teacher");

        Study getStudy = studyQueryRepository.getById(studyId);
        StudyFullResponse responseStudy = (StudyFullResponse) responseMap.get("study");
        List<StudyWord> getStudyWords = getStudy.getStudyWords();
        List<StudyWordFullResponse> responseStudyWords = (List<StudyWordFullResponse>) responseMap.get("studyWords");

        assertThat(responseStudy.getId()).isEqualTo(studyId);
        assertThat(responseStudy.getSequence()).isEqualTo(getStudy.getSequence());
        assertThat(responseStudy.isIgnoreCase()).isEqualTo(getStudy.isIgnoreCase());
        assertThat(responseStudy.getTitle()).isEqualTo(getStudy.getTitle());
        assertThat(responseStudy.getStatus()).isEqualTo(getStudy.getStatus());
        assertThat(responseStudy.getType()).isEqualTo(getStudy.getType());
        assertThat(responseStudy.getClassification()).isEqualTo(getStudy.getClassification());
        assertThat(responseStudy.getTarget()).isEqualTo(getStudy.getTarget());
        assertThat(responseStudy.getSubmitDateTime()).isNotNull();
        assertThat(responseStudy.getStudyRanges()).hasSize(1)
                .extracting("bookPublisher", "bookName", "bookChapter", "bookSubject")
                .containsExactly(
                        tuple(book.getPublisher(), book.getName(), book.getChapter(), book.getSubject())
                );
        assertThat(responseStudyWords).hasSize(10)
                .extracting("id", "question", "submit", "answer", "correct", "reason", "choices")
                .containsExactlyElementsOf(
                        getStudyWords.stream().map(sw -> tuple(sw.getId(), sw.getQuestion(), sw.getSubmit(), sw.getAnswer(), sw.getCorrect(), sw.getReason(), new ArrayList<>())).toList()
                );
        assertThat(responseStudyWords)
                .extracting("submit")
                .doesNotContainNull();
        assertThat(responseStudyWords)
                .extracting("answer")
                .doesNotContainNull();

    }

    @DisplayName("선생님은 학습의 정답을 볼 수 있다.")
    @Test
    public void getDetailByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "1과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.TRACING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID studyId = studyCommandService.start(student.getId(), post);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getDetail(teacher.getId(), studyId));

        // then
        assertThat(responseMap).containsKeys("study", "studyWords", "student");
        assertThat(responseMap).doesNotContainKey("teacher");

        Study getStudy = studyQueryRepository.getById(studyId);
        StudyFullResponse responseStudy = (StudyFullResponse) responseMap.get("study");
        List<StudyWord> getStudyWords = getStudy.getStudyWords();
        List<StudyWordFullResponse> responseStudyWords = (List<StudyWordFullResponse>) responseMap.get("studyWords");

        assertThat(responseStudy.getId()).isEqualTo(studyId);
        assertThat(responseStudy.getSequence()).isEqualTo(getStudy.getSequence());
        assertThat(responseStudy.isIgnoreCase()).isEqualTo(getStudy.isIgnoreCase());
        assertThat(responseStudy.getTitle()).isEqualTo(getStudy.getTitle());
        assertThat(responseStudy.getStatus()).isEqualTo(getStudy.getStatus());
        assertThat(responseStudy.getType()).isEqualTo(getStudy.getType());
        assertThat(responseStudy.getClassification()).isEqualTo(getStudy.getClassification());
        assertThat(responseStudy.getTarget()).isEqualTo(getStudy.getTarget());
        assertThat(responseStudy.getSubmitDateTime()).isNull();
        assertThat(responseStudy.getStudyRanges()).hasSize(1)
                .extracting("bookPublisher", "bookName", "bookChapter", "bookSubject")
                .containsExactly(
                        tuple(book.getPublisher(), book.getName(), book.getChapter(), book.getSubject())
                );
        assertThat(responseStudyWords).hasSize(10)
                .extracting("id", "question", "submit", "answer", "correct", "reason", "choices")
                .containsExactlyElementsOf(
                        getStudyWords.stream().map(sw -> tuple(sw.getId(), sw.getQuestion(), null, sw.getAnswer(), sw.getCorrect(), sw.getReason(), new ArrayList<>())).toList()
                );
        assertThat(responseStudyWords)
                .extracting("answer")
                .doesNotContainNull();
    }

    @DisplayName("학생은 시험 중인 학습의 정답을 볼 수 없다.")
    @Test
    public void getDetailWithoutAnswer() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "1과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.TRACING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID studyId = studyCommandService.start(student.getId(), post);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getDetail(student.getId(), studyId));

        // then
        assertThat(responseMap).containsKeys("study", "studyWords", "student");
        assertThat(responseMap).doesNotContainKey("teacher");

        Study getStudy = studyQueryRepository.getById(studyId);
        StudyFullResponse responseStudy = (StudyFullResponse) responseMap.get("study");
        List<StudyWord> getStudyWords = getStudy.getStudyWords();
        List<StudyWordFullResponse> responseStudyWords = (List<StudyWordFullResponse>) responseMap.get("studyWords");

        assertThat(responseStudy.getId()).isEqualTo(studyId);
        assertThat(responseStudy.getSequence()).isEqualTo(getStudy.getSequence());
        assertThat(responseStudy.isIgnoreCase()).isEqualTo(getStudy.isIgnoreCase());
        assertThat(responseStudy.getTitle()).isEqualTo(getStudy.getTitle());
        assertThat(responseStudy.getStatus()).isEqualTo(getStudy.getStatus());
        assertThat(responseStudy.getType()).isEqualTo(getStudy.getType());
        assertThat(responseStudy.getClassification()).isEqualTo(getStudy.getClassification());
        assertThat(responseStudy.getTarget()).isEqualTo(getStudy.getTarget());
        assertThat(responseStudy.getSubmitDateTime()).isNull();
        assertThat(responseStudy.getStudyRanges()).hasSize(1)
                .extracting("bookPublisher", "bookName", "bookChapter", "bookSubject")
                .containsExactly(
                        tuple(book.getPublisher(), book.getName(), book.getChapter(), book.getSubject())
                );
        assertThat(responseStudyWords).hasSize(10)
                .extracting("id", "question", "submit", "answer", "correct", "reason", "choices")
                .containsExactlyElementsOf(
                        getStudyWords.stream().map(sw -> tuple(sw.getId(), sw.getQuestion(), null, null, sw.getCorrect(), sw.getReason(), new ArrayList<>())).toList()
                );
    }

    @DisplayName("성적을 정정한 선생님이 있다면 선생님 정보를 볼 수 있다.")
    @Test
    public void getDetailWithTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID startStudyId = studyCommandService.start(student.getId(), post);

        Study startStudy = studyQueryRepository.getById(startStudyId);
        List<StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
        for (StudyWord sw : startStudy.getStudyWords()) {
            submitStudyWords.add(new StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
        }
        StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);
        studyCommandService.submit(student.getId(), startStudyId, patchSubmit);

        StudyWordPATCHCorrect patchCorrect = new StudyWordPATCHCorrect(false, "REASON");
        Long studyWordId = startStudy.getStudyWords().get(0).getId();

        studyCommandService.correctStudyWord(teacher.getId(), startStudyId, studyWordId, patchCorrect);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getDetail(teacher.getId(), startStudyId));

        // then
        assertThat(responseMap).containsKeys("study", "studyWords", "student", "teacher");
    }

    @DisplayName("선생님이 학원의 학습 목록을 슬라이싱으로 가져올 수 있다.")
    @Test
    public void getStudyListByTeacher() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        Book book = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book);
        }

        // 1 번째 학생은 제출하지 않은 상태로 만든다.
        Student firstStudent = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(firstStudent, RoleName.ROLE_STUDENT);

        StudyPOSTStart firstPost = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        studyCommandService.start(firstStudent.getId(), firstPost);

        long lastSequence = 0;
        // 10 명의 학생이 시험을 보고 제출한 상태로 만든다.
        for (int i = 2; i <= 11; i++) {
            Student student = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
            dataCreator.createUserRoles(student, RoleName.ROLE_STUDENT);

            StudyPOSTStart post = new StudyPOSTStart(List.of(book.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
            UUID startStudyId = studyCommandService.start(student.getId(), post);

            Study startStudy = studyQueryRepository.getById(startStudyId);
            List<StudyWordPATCHSubmit> submitStudyWords = new ArrayList<>();
            for (StudyWord sw : startStudy.getStudyWords()) {
                if (new Random().nextInt(2) % 2 == 0) {
                    submitStudyWords.add(new StudyWordPATCHSubmit(sw.getId(), sw.getAnswer() + "_WRONG"));
                } else {
                    submitStudyWords.add(new StudyWordPATCHSubmit(sw.getId(), sw.getAnswer()));
                }
            }
            StudyPATCHSubmit patchSubmit = new StudyPATCHSubmit(submitStudyWords);
            studyCommandService.submit(student.getId(), startStudyId, patchSubmit);
            if (i == 11) {
                lastSequence = startStudy.getSequence();
            }
        }

        StudySlicingSearchCond cond = new StudySlicingSearchCond(10, lastSequence, null, null, null, null, null);

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getStudyList(teacher.getId(), cond));

        // then
        assertThat(responseMap).containsKeys("slicingInfo", "studies");

        SlicingInfo slicingInfo = (SlicingInfo) responseMap.get("slicingInfo");
        List<StudySlicingResponse> studies = (List<StudySlicingResponse>) responseMap.get("studies");

        assertThat(slicingInfo.getSize()).isEqualTo(10);
        assertThat(slicingInfo.getLastSequence()).isEqualTo(1L);
        assertThat(slicingInfo.hasNext()).isFalse();
        assertThat(studies).hasSize(10);
        for (int i = 0; i < 9; i++) {
            StudySlicingResponse s = studies.get(i);
            assertThat(s.getId()).isNotNull();
            assertThat(s.getSequence()).isLessThan(lastSequence);
            assertThat(s.getTitle()).isNotNull();
            assertThat(s.getStatus()).isEqualTo(StudyStatus.SUBMITTED);
            assertThat(s.getType()).isEqualTo(StudyType.WRITING);
            assertThat(s.getClassification()).isEqualTo(StudyClassification.EXAM);
            assertThat(s.getTarget()).isEqualTo(StudyTarget.KOREAN);
            assertThat(s.getSubmitDateTime()).isNotNull();
            assertThat(s.getStudentId()).isNotNull();
            assertThat(s.getAttendanceId()).isNotNull();
            assertThat(s.getStudentName()).isNotNull();
            assertThat(s.getSchoolName()).isNotNull();
            assertThat(s.getSchoolGrade()).isNotNull();
            assertThat(s.getCorrectCount()).isNotNull();
            assertThat(s.getTotalCount()).isNotNull();
        }
        StudySlicingResponse lastStudy = studies.get(9);
        assertThat(lastStudy.getId()).isNotNull();
        assertThat(lastStudy.getSequence()).isEqualTo(1L);
        assertThat(lastStudy.getTitle()).isNotNull();
        assertThat(lastStudy.getStatus()).isEqualTo(StudyStatus.STARTED);
        assertThat(lastStudy.getType()).isEqualTo(StudyType.WRITING);
        assertThat(lastStudy.getClassification()).isEqualTo(StudyClassification.EXAM);
        assertThat(lastStudy.getTarget()).isEqualTo(StudyTarget.KOREAN);
        assertThat(lastStudy.getSubmitDateTime()).isNull();
        assertThat(lastStudy.getStudentId()).isNotNull();
        assertThat(lastStudy.getAttendanceId()).isNotNull();
        assertThat(lastStudy.getStudentName()).isNotNull();
        assertThat(lastStudy.getSchoolName()).isNotNull();
        assertThat(lastStudy.getSchoolGrade()).isNotNull();
        assertThat(lastStudy.getCorrectCount()).isEqualTo(0);
        assertThat(lastStudy.getTotalCount()).isEqualTo(10);
    }

    @DisplayName("학생이 자기 학습 목록을 슬라이싱으로 가져올 수 있다.")
    @Test
    public void getStudyListByStudent() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        Book book1 = dataCreator.registerBook(false, "능률(김성곤)", "중3-1학기", "2과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book1);
        }

        Book book2 = dataCreator.registerBook(false, "미래(최연희)", "중3-1학기", "1과", "본문", academy);
        for (int i = 1; i <= 10; i++) {
            dataCreator.registerWord("apple" + i, "사과" + i, book2);
        }

        Student student1 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student1, RoleName.ROLE_STUDENT);
        Student student2 = dataCreator.registerStudent(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(student2, RoleName.ROLE_STUDENT);

        StudyPOSTStart post1 = new StudyPOSTStart(List.of(book1.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        UUID studyId1 = studyCommandService.start(student1.getId(), post1);
        StudyPOSTStart post2 = new StudyPOSTStart(List.of(book1.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        studyCommandService.start(student2.getId(), post2);
        StudyPOSTStart post3 = new StudyPOSTStart(List.of(book2.getId()), true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN);
        studyCommandService.start(student1.getId(), post3);

        StudySlicingSearchCond cond = new StudySlicingSearchCond(10, null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), "능률", student1.getName(), student1.getSchool().getGrade());

        // when
        Map<String, Object> responseMap = refreshAnd(() -> sut.getStudyList(student1.getId(), cond));

        // then
        assertThat(responseMap).containsKeys("slicingInfo", "studies");

        SlicingInfo slicingInfo = (SlicingInfo) responseMap.get("slicingInfo");
        List<StudySlicingResponse> studies = (List<StudySlicingResponse>) responseMap.get("studies");

        Study getStudy1 = studyQueryRepository.getById(studyId1);

        assertThat(slicingInfo.getSize()).isEqualTo(10);
        assertThat(slicingInfo.getLastSequence()).isEqualTo(getStudy1.getSequence());
        assertThat(slicingInfo.hasNext()).isFalse();
        assertThat(studies).hasSize(1);
    }
}
