package com.th.eventmanagmentsystem.usermanagement.e2e;

import com.th.eventmanagmentsystem.usermanagement.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class RegisterUserUseCaseE2ETest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;


}
