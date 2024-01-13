package portfolio.sunder.domain.student.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Student { // TODO: 2024-01-13 extends TimeEntity

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String uid;

    @Column(nullable = false, length = 20)
    private String upw;

    @Column(nullable = false)
    private String name;

    // TODO: 2024-01-13 @Enumerated(EnumType.STRING)
    private String status;

    private int grade;

    private int classroom;

    // TODO: 2024-01-13 @ManyToOne School
    private String school;

    @Builder
    public Student(String uid, String upw, String name, String status, int grade, int classroom, String school) {
        this.uid = uid;
        this.upw = upw;
        this.name = name;
        this.status = status;
        this.grade = grade;
        this.classroom = classroom;
        this.school = school;
    }
}
