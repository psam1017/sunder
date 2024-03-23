package psam.portfolio.sunder.english;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.docs.RestDocsConfig;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;
import psam.portfolio.sunder.english.infrastructure.mail.MailUtils;
import psam.portfolio.sunder.english.testbean.container.InfoContainer;
import psam.portfolio.sunder.english.testbean.data.DataCreator;
import psam.portfolio.sunder.english.testconfig.TestConfig;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@ExtendWith(RestDocumentationExtension.class)
@Import({TestConfig.class, RestDocsConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class SunderApplicationTests {

	@Test
	void contextLoads() {

	}

	@BeforeEach
	void setUp() {
		dataCreator.createAllRoles();
	}

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper om;

	@Autowired
	protected EntityManager em;

	@Autowired
	protected InfoContainer infoContainer;

	@Autowired
	protected DataCreator dataCreator;

	@MockBean
	protected MailUtils mailUtils;

	protected String createJson(Object body) {
		try {
			return om.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T refreshAnd(Supplier<T> action) {
		em.flush();
		em.clear();
		System.out.println("\n#============================== Flush and Clear. Action Start. ==============================\n");
		T result = action.get();
		em.flush();
		em.clear();
		System.out.println("\n#============================== Flush and Clear. Action Finished. ==============================\n");
		return result;
	}

	public void refreshAnd(Runnable action) {
		em.flush();
		em.clear();
		System.out.println("\n#============================== Flush and Clear. Action Start. ==============================\n");
		action.run();
		em.flush();
		em.clear();
		System.out.println("\n#============================== Flush and Clear. Action Finished. ==============================\n");
	}

	protected Academy registerAcademy(AcademyStatus status) {
		return dataCreator.registerAcademy(status);
	}

	protected Academy registerAcademy(boolean openToPublic, AcademyStatus status) {
		return dataCreator.registerAcademy(openToPublic, status);
	}

	protected Teacher registerTeacher(UserStatus status, Academy academy) {
		return dataCreator.registerTeacher(status, academy);
	}

	protected Teacher registerTeacher(String name, UserStatus status, Academy academy) {
		return dataCreator.registerTeacher(name, status, academy);
	}

	protected Student registerStudent(UserStatus status, Academy academy) {
		return dataCreator.registerStudent(status, academy);
	}

	protected List<UserRole> createUserRoles(User user, RoleName... roleNames) {
		return dataCreator.createUserRoles(user, roleNames);
	}
}
