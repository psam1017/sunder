package psam.portfolio.sunder.english.domain.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.book.exception.NoSuchBookException;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.QBook;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordQueryRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.QStudent;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.repository.StudentQueryRepository;
import psam.portfolio.sunder.english.domain.study.exception.IllegalStatusStudyException;
import psam.portfolio.sunder.english.domain.study.exception.StudyAccessDeniedException;
import psam.portfolio.sunder.english.domain.study.exception.WordSizeNotEnoughToStudyException;
import psam.portfolio.sunder.english.domain.study.model.embeddable.StudyRange;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudy;
import psam.portfolio.sunder.english.domain.study.model.entity.QStudyWord;
import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.model.entity.StudyWord;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTAssign;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTStart;
import psam.portfolio.sunder.english.domain.study.model.request.StudyWordPATCHCorrect;
import psam.portfolio.sunder.english.domain.study.repository.StudyCommandRepository;
import psam.portfolio.sunder.english.domain.study.repository.StudyQueryRepository;
import psam.portfolio.sunder.english.domain.study.repository.StudyWordCommandRepository;
import psam.portfolio.sunder.english.domain.study.repository.StudyWordQueryRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static psam.portfolio.sunder.english.domain.study.model.request.StudyPATCHSubmit.StudyWordPATCHSubmit;

@RequiredArgsConstructor
@Transactional
@Service
public class StudyCommandService {

    private static final int MIN_STUDY_WORDS_SIZE = 5;

    private final StudyCommandRepository studyCommandRepository;
    private final StudyQueryRepository studyQueryRepository;
    private final StudyWordCommandRepository studyWordCommandRepository;
    private final StudyWordQueryRepository studyWordQueryRepository;

    private final StudentQueryRepository studentQueryRepository;
    private final BookQueryRepository bookQueryRepository;
    private final TeacherQueryRepository teacherQueryRepository;
    private final WordQueryRepository wordQueryRepository;

    /**
     * 숙제 생성 서비스
     *
     * @param teacherId 숙제를 내주는 선생님 아이디
     * @param post      생성할 숙제 정보
     * @return 생성에 성공한 숙제 아이디 배열
     */
    public List<UUID> assign(UUID teacherId, StudyPOSTAssign post) {

        Teacher getTeacher = teacherQueryRepository.getById(teacherId);

        QBook qBook = QBook.book;
        List<Book> getBooks = bookQueryRepository.findAll(
                qBook.id.in(post.getBookIds()),
                qBook.academy.id.eq(getTeacher.getAcademy().getId())
                        .or(qBook.openToPublic.isTrue())
        );

        if (ObjectUtils.isEmpty(getBooks)) {
            throw new NoSuchBookException();
        }

        // 교재를 book.name, book.chapter, book.subject 순서로 오름차순 정렬
        getBooks.sort(Comparator.comparing(Book::getName)
                .thenComparing(Book::getChapter, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Book::getSubject, Comparator.nullsFirst(Comparator.naturalOrder())));

        // Title 생성
        String title = createTitle(getBooks);

        List<Student> getStudents = studentQueryRepository.findAll(
                QStudent.student.id.in(post.getStudentIds()),
                QStudent.student.academy.id.eq(getTeacher.getAcademy().getId())
        );

        // 단어 목록부터 생성
        List<StudyRange> studyRanges = getBooks.stream().map(StudyRange::of).toList();

        // 단어 최소 개수 검사
        List<Word> shuffledWords = wordQueryRepository.findShuffledWords(getBooks, post.getNumberOfWords());
        if (shuffledWords.size() < MIN_STUDY_WORDS_SIZE) {
            throw new WordSizeNotEnoughToStudyException(MIN_STUDY_WORDS_SIZE);
        }

        List<UUID> saveStudyIds = new ArrayList<>();
        for (Student getStudent : getStudents) {
            // 만약 동일한 학습으로 생성하지 않는다면 매번 단어 순서를 섞는다.
            if (post.getShuffleEach()) {
                Collections.shuffle(shuffledWords);
            }

            // 학습 생성
            long nextSequence = studyQueryRepository.findNextSequenceOfLastStudy();
            Study saveStudy = studyCommandRepository.save(post.toEntity(title, nextSequence, getStudent, getTeacher)); // status = ASSIGNED
            saveStudy.getStudyRanges().addAll(studyRanges);

            // 학습 단어 저장
            List<StudyWord> buildStudyWords = buildStudyWords(post, saveStudy, shuffledWords);
            List<StudyWord> saveStudyWords = studyWordCommandRepository.saveAll(buildStudyWords);
            saveStudy.getStudyWords().addAll(saveStudyWords);
            saveStudyIds.add(saveStudy.getId());
        }
        return saveStudyIds;
    }

    /**
     * 학습 시작 서비스
     *
     * @param studentId 학생 아이디
     * @param post      시작할 학습 정보
     * @return 시작에 성공한 학습 아이디
     */
    public UUID start(UUID studentId, StudyPOSTStart post) {

        Student getStudent = studentQueryRepository.getById(studentId);

        QBook qBook = QBook.book;
        List<Book> getBooks = bookQueryRepository.findAll(
                qBook.id.in(post.getBookIds()),
                qBook.academy.id.eq(getStudent.getAcademy().getId())
                        .or(qBook.openToPublic.isTrue())
        );

        if (ObjectUtils.isEmpty(getBooks)) {
            throw new NoSuchBookException();
        }

        // 교재를 book.name, book.chapter, book.subject 순서로 오름차순 정렬
        getBooks.sort(Comparator.comparing(Book::getName)
                .thenComparing(Book::getChapter, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Book::getSubject, Comparator.nullsFirst(Comparator.naturalOrder())));

        // Title 생성
        String title = createTitle(getBooks);

        // 학습 생성
        long nextSequence = studyQueryRepository.findNextSequenceOfLastStudy();
        Study saveStudy = studyCommandRepository.save(post.toEntity(nextSequence, getStudent, title)); // status = STARTED

        // 학습 범위 저장
        getBooks.forEach(b -> saveStudy.getStudyRanges().add(StudyRange.of(b)));

        // 단어 최소 개수 검사
        List<Word> shuffledWords = wordQueryRepository.findShuffledWords(getBooks, post.getNumberOfWords());
        if (shuffledWords.size() < MIN_STUDY_WORDS_SIZE) {
            throw new WordSizeNotEnoughToStudyException(MIN_STUDY_WORDS_SIZE);
        }

        // 학습 단어 저장
        List<StudyWord> buildStudyWords = buildStudyWords(post, saveStudy, shuffledWords);
        List<StudyWord> saveStudyWords = studyWordCommandRepository.saveAll(buildStudyWords);
        saveStudy.getStudyWords().addAll(saveStudyWords);

        return saveStudy.getId();
    }

    private String createTitle(List<Book> books) {
        Book firstBook = books.get(0);
        String title = buildTitleSegment(firstBook);

        if (books.size() > 1) {
            Book lastBook = books.get(books.size() - 1);
            title += " ~ " + buildTitleSegment(lastBook);
        }
        return title;
    }

    private String buildTitleSegment(Book book) {
        StringBuilder titleSegment = new StringBuilder();

        if (StringUtils.hasText(book.getPublisher())) {
            titleSegment.append(book.getPublisher()).append(" ");
        }
        titleSegment.append(book.getName());

        boolean chapterExist = StringUtils.hasText(book.getChapter());
        boolean subjectExist = StringUtils.hasText(book.getSubject());

        if (chapterExist && subjectExist) {
            titleSegment.append("(").append(book.getChapter()).append(" ").append(book.getSubject()).append(")");
        } else if (chapterExist) {
            titleSegment.append("(").append(book.getChapter()).append(")");
        } else if (subjectExist) {
            titleSegment.append("(").append(book.getSubject()).append(")");
        }
        return titleSegment.toString();
    }

    private List<StudyWord> buildStudyWords(StudyPOSTStart post, Study saveStudy, List<Word> words) {
        return saveStudy.getType() == StudyType.SELECT
                ? buildSelectStudyWords(post, saveStudy, words)
                : buildNonSelectStudyWords(post, saveStudy, words);
    }

    private List<StudyWord> buildSelectStudyWords(StudyPOSTStart post, Study saveStudy, List<Word> words) {
        boolean isStudyTargetKorean = saveStudy.getTarget() == StudyTarget.KOREAN;
        int bound = Math.min(words.size(), post.getNumberOfWords());
        List<StudyWord> studyWords = new ArrayList<>();
        for (int i = 0; i < bound; i++) {
            Word w = words.get(i);
            StudyWord studyWord = isStudyTargetKorean
                    ? buildKoreanStudyWord(saveStudy, w)
                    : buildEnglishStudyWord(saveStudy, w);

            List<String> choices = new ArrayList<>();
            choices.add(isStudyTargetKorean ? w.getKorean() : w.getEnglish());

            while (choices.size() < 4) {
                Word randomWord = words.get(ThreadLocalRandom.current().nextInt(words.size()));
                String choice = isStudyTargetKorean ? randomWord.getKorean() : randomWord.getEnglish();
                if (!choices.contains(choice)) {
                    choices.add(choice);
                }
            }
            Collections.shuffle(choices);
            studyWord.getChoices().addAll(choices);
            studyWords.add(studyWord);
        }
        return studyWords;
    }

    private List<StudyWord> buildNonSelectStudyWords(StudyPOSTStart post, Study saveStudy, List<Word> words) {
        boolean isStudyTargetKorean = saveStudy.getTarget() == StudyTarget.KOREAN;
        int bound = Math.min(post.getNumberOfWords(), words.size());
        List<StudyWord> studyWords = new ArrayList<>();
        for (int i = 0; i < bound; i++) {
            StudyWord studyWord = isStudyTargetKorean
                    ? buildKoreanStudyWord(saveStudy, words.get(i))
                    : buildEnglishStudyWord(saveStudy, words.get(i));
            studyWords.add(studyWord);
        }
        return studyWords;
    }

    private static StudyWord buildKoreanStudyWord(Study saveStudy, Word w) {
        return StudyWord.builder()
                .question(w.getEnglish())
                .answer(w.getKorean())
                .study(saveStudy)
                .build();
    }

    private static StudyWord buildEnglishStudyWord(Study saveStudy, Word w) {
        return StudyWord.builder()
                .question(w.getKorean())
                .answer(w.getEnglish())
                .study(saveStudy)
                .build();
    }

    /**
     * 학습 제출 서비스
     * @param studentId 학생 아이디
     * @param studyId   제출할 학습 아이디
     * @param patch     제출할 학습 정보
     * @return 제출에 성공한 학습 아이디
     */
    public UUID submit(UUID studentId, UUID studyId, StudyPATCHSubmit patch) {

        Study getStudy = studyQueryRepository.getOne(
                QStudy.study.id.eq(studyId),
                QStudy.study.student.id.eq(studentId)
        );

        if (!getStudy.canSubmit()) {
            throw new IllegalStatusStudyException(getStudy.getStatus());
        }

        getStudy.submit();
        if (!ObjectUtils.isEmpty(getStudy.getStudyWords())) { // just for fetch
            for (StudyWordPATCHSubmit sw : patch.getStudyWords()) {
                StudyWord getStudyWord = studyWordQueryRepository.getById(sw.getId());
                if (!getStudy.hasStudyWord(getStudyWord)) {
                    throw new StudyAccessDeniedException();
                }
                getStudyWord.submit(sw.getSubmit());
            }
        }

        // 제출되지 않은 학습 단어는 null 로 제출한다.
        for (StudyWord sw : getStudy.getStudyWords()) {
            if (sw.getCorrect() == null) {
                sw.submit(null);
            }
        }
        return studyId;
    }

    /**
     * 학습 단어 정정 서비스
     * 선생님이 같은 학원 학생의 학습 단어를 정정할 수 있다.
     *
     * @param teacherId   성적을 정정하는 선생님 아이디
     * @param studyId     정정할 학습 아이디
     * @param studyWordId 정정할 학습 단어 아이디
     * @param patch       정정할 학습 단어 정보
     * @return 정정에 성공한 학습 단어 아이디
     */
    public Long correctStudyWord(UUID teacherId, UUID studyId, Long studyWordId, StudyWordPATCHCorrect patch) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        StudyWord getStudyWord = studyWordQueryRepository.getOne(
                QStudyWord.studyWord.id.eq(studyWordId),
                QStudyWord.studyWord.study.id.eq(studyId)
        );
        if (!Objects.equals(getTeacher.getAcademy().getId(), getStudyWord.getStudy().getStudent().getAcademy().getId())) {
            throw new StudyAccessDeniedException();
        }
        getStudyWord.getStudy().isCorrectedBy(getTeacher);
        getStudyWord.correct(patch.getCorrect(), patch.getReason());
        return studyWordId;
    }

    /**
     * 학습 삭제 서비스
     * 선생님이 같은 학원 학생의 학습을 삭제할 수 있다.
     *
     * @param teacherId 성적을 삭제하는 선생님 아이디
     * @param studyId   삭제할 학습 아이디
     * @return 삭제에 성공한 학습 아이디
     */
    public UUID delete(UUID teacherId, UUID studyId) {
        studyQueryRepository.getOne(
                QStudy.study.id.eq(studyId),
                QStudy.study.student.academy.teachers.any().id.eq(teacherId)
        ).delete();
        return studyId;
    }
}
