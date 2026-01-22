package com.zetra.econsig.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.zetra.econsig.EConsigInitializer;

@ContextConfiguration(initializers = { EConsigInitializer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ContextSpringConfiguration {

}
