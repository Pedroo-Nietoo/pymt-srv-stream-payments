package com.pedroonietoo.pymt_srv_stream_payments;

import org.springframework.boot.SpringApplication;

public class TestPymtSrvStreamPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.from(PymtSrvStreamPaymentsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
