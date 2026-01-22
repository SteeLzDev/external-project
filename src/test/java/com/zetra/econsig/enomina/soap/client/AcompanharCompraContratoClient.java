package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.AcompanharCompraContrato;
import com.zetra.econsig.soap.compra.AcompanharCompraContratoResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class AcompanharCompraContratoClient extends WebServiceGatewaySupport {

	public AcompanharCompraContratoResponse getResponse(String usuario, String senha, Long adeNumero, String matricula,
			String cpf, boolean contratosCompradosPelaEntidade, boolean saldoDevedorInformado,
			Short diasUteisSemInfoSaldoDevedor, boolean saldoDevedorAprovado, Short diasUteisSemAprovacaoSaldoDevedor,
			boolean saldoDevedorPago, Short diasUteisSemPagamentoSaldoDevedor, boolean contratoLiquidado,
			Short diasUteisSemLiquidacao) {

		ObjectFactory objectFactory = new ObjectFactory();

		AcompanharCompraContrato acompanharCompraContrato = new AcompanharCompraContrato();
		acompanharCompraContrato.setCliente(objectFactory.createAcompanharCompraContratoCliente(""));
		acompanharCompraContrato.setConvenio(objectFactory.createAcompanharCompraContratoConvenio(""));
		acompanharCompraContrato.setUsuario(usuario);
		acompanharCompraContrato.setSenha(senha);
		acompanharCompraContrato.setAdeNumero(objectFactory.createAcompanharCompraContratoAdeNumero(adeNumero));
		acompanharCompraContrato.setMatricula(objectFactory.createAcompanharCompraContratoMatricula(matricula));
		acompanharCompraContrato.setCpf(objectFactory.createAcompanharCompraContratoCpf(cpf));
		acompanharCompraContrato.setTemPendenciaProcessoCompra(false);
		acompanharCompraContrato.setTemContratosBloqueadosOuABloquear(false);
		acompanharCompraContrato
				.setDiasParaBloqueio(objectFactory.createAcompanharCompraContratoDiasParaBloqueio(Short.valueOf("0")));
		acompanharCompraContrato.setContratosCompradosPelaEntidade(contratosCompradosPelaEntidade);
		acompanharCompraContrato.setSaldoDevedorInformado(
				objectFactory.createAcompanharCompraContratoSaldoDevedorInformado(saldoDevedorInformado));
		acompanharCompraContrato.setDiasUteisSemInfoSaldoDevedor(
				objectFactory.createAcompanharCompraContratoDiasUteisSemInfoSaldoDevedor(diasUteisSemInfoSaldoDevedor));
		acompanharCompraContrato.setSaldoDevedorAprovado(
				objectFactory.createAcompanharCompraContratoSaldoDevedorAprovado(saldoDevedorAprovado));
		acompanharCompraContrato.setDiasUteisSemAprovacaoSaldoDevedor(objectFactory
				.createAcompanharCompraContratoDiasUteisSemAprovacaoSaldoDevedor(diasUteisSemAprovacaoSaldoDevedor));
		acompanharCompraContrato
				.setSaldoDevedorPago(objectFactory.createAcompanharCompraContratoSaldoDevedorPago(saldoDevedorPago));
		acompanharCompraContrato.setDiasUteisSemPagamentoSaldoDevedor(objectFactory
				.createAcompanharCompraContratoDiasUteisSemPagamentoSaldoDevedor(diasUteisSemPagamentoSaldoDevedor));
		acompanharCompraContrato
				.setContratoLiquidado(objectFactory.createAcompanharCompraContratoContratoLiquidado(contratoLiquidado));
		acompanharCompraContrato.setDiasUteisSemLiquidacao(
				objectFactory.createAcompanharCompraContratoDiasUteisSemLiquidacao(diasUteisSemLiquidacao));
		acompanharCompraContrato.setDataInicioCompra("2021-05-23");
		acompanharCompraContrato.setDataFimCompra("2021-06-22");

		return (AcompanharCompraContratoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(acompanharCompraContrato);
	}

	public AcompanharCompraContratoResponse getResponse(String usuario, String senha, String matricula, String cpf,
			boolean contratosCompradosPelaEntidade, boolean saldoDevedorInformado, boolean saldoDevedorAprovado,
			boolean saldoDevedorPago, boolean contratoLiquidado) {

		ObjectFactory objectFactory = new ObjectFactory();

		AcompanharCompraContrato acompanharCompraContrato = new AcompanharCompraContrato();
		acompanharCompraContrato.setUsuario(usuario);
		acompanharCompraContrato.setSenha(senha);
		acompanharCompraContrato.setMatricula(objectFactory.createAcompanharCompraContratoMatricula(matricula));
		acompanharCompraContrato.setCpf(objectFactory.createAcompanharCompraContratoCpf(cpf));
		acompanharCompraContrato.setContratosCompradosPelaEntidade(contratosCompradosPelaEntidade);
		acompanharCompraContrato.setSaldoDevedorInformado(
				objectFactory.createAcompanharCompraContratoSaldoDevedorInformado(saldoDevedorInformado));
		acompanharCompraContrato.setSaldoDevedorAprovado(
				objectFactory.createAcompanharCompraContratoSaldoDevedorAprovado(saldoDevedorAprovado));
		acompanharCompraContrato
				.setSaldoDevedorPago(objectFactory.createAcompanharCompraContratoSaldoDevedorPago(saldoDevedorPago));
		acompanharCompraContrato
				.setContratoLiquidado(objectFactory.createAcompanharCompraContratoContratoLiquidado(contratoLiquidado));
		acompanharCompraContrato.setDataInicioCompra("2021-05-23");
		acompanharCompraContrato.setDataFimCompra("2021-06-22");

		return (AcompanharCompraContratoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(acompanharCompraContrato);
	}

	public AcompanharCompraContratoResponse getResponse(String usuario, String senha, Long adeNumero, String matricula,
			String cpf, String dataFimCompra) {

		ObjectFactory objectFactory = new ObjectFactory();

		AcompanharCompraContrato acompanharCompraContrato = new AcompanharCompraContrato();
		acompanharCompraContrato.setCliente(objectFactory.createAcompanharCompraContratoCliente(""));
		acompanharCompraContrato.setConvenio(objectFactory.createAcompanharCompraContratoConvenio(""));
		acompanharCompraContrato.setUsuario(usuario);
		acompanharCompraContrato.setSenha(senha);
		acompanharCompraContrato.setAdeNumero(objectFactory.createAcompanharCompraContratoAdeNumero(adeNumero));
		acompanharCompraContrato.setMatricula(objectFactory.createAcompanharCompraContratoMatricula(matricula));
		acompanharCompraContrato.setCpf(objectFactory.createAcompanharCompraContratoCpf(cpf));
		acompanharCompraContrato.setDataInicioCompra("2021-05-22");
		acompanharCompraContrato.setDataFimCompra(dataFimCompra);

		return (AcompanharCompraContratoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(acompanharCompraContrato);
	}

	public AcompanharCompraContratoResponse getResponse(String usuario, String senha, String matricula, String cpf,
			String dataFimCompra) {

		ObjectFactory objectFactory = new ObjectFactory();

		AcompanharCompraContrato acompanharCompraContrato = new AcompanharCompraContrato();
		acompanharCompraContrato.setCliente(objectFactory.createAcompanharCompraContratoCliente(""));
		acompanharCompraContrato.setConvenio(objectFactory.createAcompanharCompraContratoConvenio(""));
		acompanharCompraContrato.setUsuario(usuario);
		acompanharCompraContrato.setSenha(senha);
		acompanharCompraContrato.setMatricula(objectFactory.createAcompanharCompraContratoMatricula(matricula));
		acompanharCompraContrato.setCpf(objectFactory.createAcompanharCompraContratoCpf(cpf));
		acompanharCompraContrato.setDataInicioCompra("2021-05-22");
		acompanharCompraContrato.setDataFimCompra(dataFimCompra);

		return (AcompanharCompraContratoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(acompanharCompraContrato);
	}
}
