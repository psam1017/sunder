package psam.portfolio.sunder.english.domain.study.model.request;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StudyStatisticSearchCond {

    private UUID studentId;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;

    public StudyStatisticSearchCond(String studentId, LocalDate startDate, LocalDate endDate) {
        this.studentId = StringUtils.hasText(studentId) ? UUID.fromString(studentId) : null;

        // default is tomorrow, and maximum date is tomorrow
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDate tomorrowDate = tomorrow.toLocalDate();
        if (endDate == null || endDate.isAfter(tomorrowDate)) {
            this.endDateTime = tomorrow;
        } else {
            this.endDateTime = endDate.atStartOfDay();
        }

        // default is 7 days ago, and maximum period is 1 month
        if (startDate == null || startDate.isAfter(tomorrowDate)) {
            this.startDateTime = tomorrow.minusDays(7);
        } else if (startDate.isBefore(tomorrowDate.minusMonths(1))) {
            this.startDateTime = tomorrow.minusMonths(1);
        } else {
            this.startDateTime = startDate.atStartOfDay();
        }
    }

    public void changeStudentId(UUID studentId) {
        this.studentId = studentId;
    }
}
