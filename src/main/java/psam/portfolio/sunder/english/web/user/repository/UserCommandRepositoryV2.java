package psam.portfolio.sunder.english.web.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.web.user.model.User;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = false)
@Repository
public class UserCommandRepositoryV2 {

    private final UserCommandRepository repository;

    public User save(User user) {
        return repository.save(user);
    }

    public List<User> saveAll(List<User> users) {
        return repository.saveAll(users);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public void deleteById(UUID uuid) {
        repository.deleteById(uuid);
    }

    public void deleteAll(List<User> users) {
        repository.deleteAll(users);
    }

    public void deleteAllById(List<UUID> uuids) {
        repository.deleteAllById(uuids);
    }
}
