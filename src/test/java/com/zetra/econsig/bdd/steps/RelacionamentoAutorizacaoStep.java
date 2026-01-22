package com.zetra.econsig.bdd.steps;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.core.ConditionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.service.RelacionamentoAutorizacaoService;

import io.cucumber.java.pt.E;

public class RelacionamentoAutorizacaoStep {

	private final ConditionFactory await = await().atMost(50, TimeUnit.SECONDS);

	@Autowired
	public RelacionamentoAutorizacaoService relacionamentoAutorizacaoService;

	@E("contrato {string} destino de relacionamento de contrato {string} do tipo {string}")
	public void confere_status_contrato_adenumero(String adeNumeroDestino, String adeNumeroOrigem, String tntCodigo) throws InterruptedException {
		relacionamentoAutorizacaoService.definirRelacionamentoAdes(Integer.parseInt(adeNumeroOrigem), Integer.parseInt(adeNumeroDestino), tntCodigo);
		await.atLeast(Duration.ofMillis(500));
	}

}
