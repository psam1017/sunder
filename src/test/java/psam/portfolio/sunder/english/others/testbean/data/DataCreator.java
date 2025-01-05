package psam.portfolio.sunder.english.others.testbean.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.academy.model.entity.AcademyShare;
import psam.portfolio.sunder.english.domain.academy.model.entity.QAcademyShare;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyCommandRepository;
import psam.portfolio.sunder.english.domain.academy.repository.AcademyShareQueryRepository;
import psam.portfolio.sunder.english.domain.academy.service.AcademyShareCommandService;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.book.repository.BookCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordCommandRepository;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.student.repository.StudentCommandRepository;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyClassification;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyTarget;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyType;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTAssign;
import psam.portfolio.sunder.english.domain.study.model.request.StudyPOSTStart;
import psam.portfolio.sunder.english.domain.study.service.StudyCommandService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherCommandRepository;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.QRole;
import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.domain.user.repository.RoleCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.RoleQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserRoleCommandRepository;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.infrastructure.password.PasswordUtils;
import psam.portfolio.sunder.english.others.testbean.container.InfoContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataCreator {

    private final InfoContainer uic;
    private final PasswordUtils passwordUtils;

    private final RoleQueryRepository roleQueryRepository;
    private final RoleCommandRepository roleCommandRepository;
    private final UserRoleCommandRepository userRoleCommandRepository;
    private final StudentCommandRepository studentCommandRepository;
    private final TeacherCommandRepository teacherCommandRepository;
    private final AcademyCommandRepository academyCommandRepository;
    private final AcademyShareCommandService academyShareCommandService;
    private final AcademyShareQueryRepository academyShareQueryRepository;
    private final BookCommandRepository bookCommandRepository;
    private final WordCommandRepository wordCommandRepository;
    private final StudyCommandService studyCommandService;

    public void createAllRoles() {
        RoleName[] roles = RoleName.values();
        for (RoleName rn : roles) {
            Role saveRole = Role.builder()
                    .name(rn)
                    .build();
            roleCommandRepository.save(saveRole);
        }
    }

    public Academy registerAcademy(AcademyStatus status) {
        Academy academy = Academy.builder()
                .name(uic.getUniqueAcademyName())
                .address(uic.getAnyAddress())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .openToPublic(true)
                .status(status)
                .build();
        return academyCommandRepository.save(academy);
    }

    public Academy registerAcademy(boolean openToPublic, AcademyStatus status) {
        Academy academy = Academy.builder()
                .name(uic.getUniqueAcademyName())
                .address(uic.getAnyAddress())
                .phone(uic.getUniquePhoneNumber())
                .email(uic.getUniqueEmail())
                .openToPublic(openToPublic)
                .status(status)
                .build();
        return academyCommandRepository.save(academy);
    }

    public Teacher registerTeacher(UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Teacher teacher = Teacher.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getAnyRawPassword()))
                .name("사용자" + uniqueId.substring(0, 3))
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .address(uic.getAnyAddress())
                .status(status)
                .academy(academy)
                .build();
        Teacher saveTeacher = teacherCommandRepository.save(teacher);
        academy.getTeachers().add(saveTeacher);
        return saveTeacher;
    }

    public Teacher registerTeacher(String name, UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Teacher teacher = Teacher.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getAnyRawPassword()))
                .name(name)
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .address(uic.getAnyAddress())
                .status(status)
                .academy(academy)
                .build();
        Teacher saveTeacher = teacherCommandRepository.save(teacher);
        academy.getTeachers().add(saveTeacher);
        return saveTeacher;
    }

    public Student registerStudent(UserStatus status, Academy academy) {
        String uniqueId = uic.getUniqueLoginId();
        Student student = Student.builder()
                .loginId(uniqueId)
                .loginPw(passwordUtils.encode(uic.getAnyRawPassword()))
                .name("사용자" + uniqueId.substring(0, 3))
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .attendanceId(uic.getUniqueAttendanceId())
                .note("note about student")
                .address(uic.getAnyAddress())
                .school(uic.getAnySchool())
                .parent(uic.getAnyParent())
                .status(status)
                .academy(academy)
                .build();
        Student saveStudent = studentCommandRepository.save(student);
        academy.getStudents().add(saveStudent);
        return saveStudent;
    }

    public Student registerStudent(String name, String attendanceId, Address address, School school, Parent parent, UserStatus status, Academy academy) {
        Student student = Student.builder()
                .loginId(uic.getUniqueLoginId())
                .loginPw(passwordUtils.encode(uic.getAnyRawPassword()))
                .name(name)
                .email(uic.getUniqueEmail())
                .phone(uic.getUniquePhoneNumber())
                .attendanceId(attendanceId)
                .note("note about student")
                .address(address)
                .school(school)
                .parent(parent)
                .status(status)
                .academy(academy)
                .build();
        Student saveStudent = studentCommandRepository.save(student);
        academy.getStudents().add(saveStudent);
        return saveStudent;
    }

    public void createUserRoles(User user, RoleName... roleNames) {
        List<Role> roles = roleQueryRepository.findAll(
                QRole.role.name.in(roleNames)
        );

        List<UserRole> buildUserRoles = new ArrayList<>();
        for (Role r : roles) {
            UserRole buildUserRole = UserRole.builder().user(user).role(r).build();
            buildUserRoles.add(buildUserRole);
        }

        List<UserRole> saveUserRoles = userRoleCommandRepository.saveAll(buildUserRoles);
        user.getRoles().addAll(saveUserRoles);
    }

    public Book registerAnyBook(Academy academy) {
        return registerBook(false, "publisher", "name", "chapter", "subject", academy);
    }

    public Book registerBook(boolean openToPublic, String publisher, String name, String chapter, String subject, Academy academy) {
        Book book = Book.builder()
                .openToPublic(openToPublic)
                .publisher(publisher)
                .name(name)
                .chapter(chapter)
                .subject(subject)
                .academy(academy)
                .build();
        Book saveBook = bookCommandRepository.save(book);
        if (academy != null) {
            academy.getBooks().add(saveBook);
        }
        return saveBook;
    }

    public void registerWord(String english, String korean, Book book) {
        Word word = Word.builder()
                .english(english)
                .korean(korean)
                .book(book)
                .build();
        Word saveWord = wordCommandRepository.save(word);
        book.getWords().add(saveWord);
    }

    public List<UUID> assignAnyStudy(UUID teacherId, List<UUID> bookIds, List<UUID> studentIds) {
        return studyCommandService.assign(teacherId, new StudyPOSTAssign(bookIds, true, 20, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN, studentIds, true));
    }

    public UUID startAnyStudy(UUID studentId, List<UUID> bookIds) {
        return studyCommandService.start(studentId, new StudyPOSTStart(bookIds, true, 10, StudyType.WRITING, StudyClassification.EXAM, StudyTarget.KOREAN));
    }

    public AcademyShare registerAcademyShare(Academy sharingAcademy, Academy sharedAcademy) {
        academyShareCommandService.share(sharingAcademy.getId(), sharedAcademy.getId());
        return academyShareQueryRepository.getOne(
                QAcademyShare.academyShare.sharingAcademy.id.eq(sharingAcademy.getId()),
                QAcademyShare.academyShare.sharedAcademy.id.eq(sharedAcademy.getId())
        );
    }
}
