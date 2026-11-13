package com.pedroonietoo.pymt_srv_stream_payments;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PymtSrvStreamPaymentsApplicationTest {

	@Test
	void shouldConstructAndRunMain() {
		PymtSrvStreamPaymentsApplication application = new PymtSrvStreamPaymentsApplication();
		assertNotNull(application);

		try (MockedStatic<SpringApplication> mockedSpringApp = Mockito.mockStatic(SpringApplication.class)) {
			PymtSrvStreamPaymentsApplication.main(new String[]{"--test-arg=true"});
			mockedSpringApp.verify(() -> SpringApplication.run(PymtSrvStreamPaymentsApplication.class, new String[]{"--test-arg=true"}));
		}
	}
}