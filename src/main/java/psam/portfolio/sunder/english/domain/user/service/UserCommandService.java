package psam.portfolio.sunder.english.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.user.repository.UserCommandRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class UserCommandService {

    private final UserCommandRepository userCommandRepository;
    private final UserQueryRepository userQueryRepository;

}
