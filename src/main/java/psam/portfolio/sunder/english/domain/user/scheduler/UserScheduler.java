package psam.portfolio.sunder.english.domain.user.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class UserScheduler {

    // 매일 0시 2분에 체험기간 사용이 끝난 학원의 사용자들의 상태를 변경
    // -> 체험기간 종료에 대한 정책은 아직 수행하지 않기로 함.
    // TODO: 2025-01-29 체험기간인 사용들의 체험종료 변경 여부 결정. 만약 변경 시 체험기간도 결정 필요.
}
