package com.zetra.econsig.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CreateCustomFlywayImages {

	public void create() {
		log.info("creating custom Flyway images");
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "./src/test/resources/build_flyway_images.sh");
		
		try {
			Process process = processBuilder.start();
			int exitVal = process.waitFor();
			
			if (exitVal == 0) {
				log.info("Imagens customizadas do Flyway geradas com sucesso");
			} else {
				log.error("Nao foi possivel gerar as imagens customizadas do Flyway. Verifique se o seu ambiente possui o Docker instalado e se precisa de sudo para executar.");
				throw new RuntimeException("Nao foi possivel gerar as imagens customizadas do Flyway. Verifique se o seu ambiente possui o Docker instalado e se precisa de sudo para executar.");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
