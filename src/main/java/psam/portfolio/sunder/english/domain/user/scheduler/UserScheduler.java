package psam.portfolio.sunder.english.domain.user.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class UserScheduler {

    // TODO 매일 0시 2분에 체험기간 사용이 끝난 학원의 사용자들의 상태를 변경
}
