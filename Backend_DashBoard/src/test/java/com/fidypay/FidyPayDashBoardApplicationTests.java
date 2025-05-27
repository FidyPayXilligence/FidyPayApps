package com.fidypay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
class FidyPayDashBoardApplicationTests {

	@Test
	void contextLoads() {
	}

    /**
     * This method runs before each test method.
     * In @SpringBootTest, the context is loaded once, but @BeforeEach still runs before each test.
     * We don't need to manually create 'new Calculator()' here because Spring injects it.
     */
    @BeforeEach
    void setUp() {
        // The 'calculator' instance is now managed and injected by Spring.
        // Optionally add setup logic that depends on the injected.
        System.out.println("-> Spring Boot context loaded and FidyPay Dashboard application.");
    }

}
