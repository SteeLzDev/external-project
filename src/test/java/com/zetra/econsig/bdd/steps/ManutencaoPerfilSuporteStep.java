package com.zetra.econsig.bdd.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.service.ManutencaoPerfilService;

import io.cucumber.java.pt.Entao;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ManutencaoPerfilSuporteStep {

    @Autowired
    private ManutencaoPerfilService manutencaoPerfilService;

    @Entao("o perfil suporte {string} e bloqueado")
    public void verificarPerfilBloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil suporte {} é bloqueado", perfilDescricao);

        assertEquals(0, manutencaoPerfilService.getStatusPerfilSup(perfilDescricao));
    }

    @Entao("o perfil suporte {string} e desbloqueado")
    public void verificarPerfilDesbloqueadoBanco(String perfilDescricao) {
        log.info("Entao o perfil suporte {} é desbloqueado", perfilDescricao);

        assertEquals(1, manutencaoPerfilService.getStatusPerfilSup(perfilDescricao));
    }
}
