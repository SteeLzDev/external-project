package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.compra.ComprarContrato;
import com.zetra.econsig.soap.compra.ComprarContratoResponse;
import com.zetra.econsig.soap.compra.ObjectFactory;

public class ComprarContratoClient extends WebServiceGatewaySupport {

	public ComprarContratoResponse getResponse(String usuario, String senha, String matricula, String senhaServidor,
			String codVerba, String codServico, Long adeNumeros, String cpf,
			int prazo, String valorParcela, String codOrgao, String codEstabelecimento, Integer carencia) {

		ObjectFactory objectFactory = new ObjectFactory();

		ComprarContrato comprarContrato = new ComprarContrato();
		comprarContrato.setCliente(objectFactory.createComprarContratoCliente(""));
		comprarContrato.setConvenio(objectFactory.createComprarContratoConvenio("LOCAL"));
		comprarContrato.setUsuario(usuario);
		comprarContrato.setSenha(senha);
		comprarContrato.setAdeNumeros(adeNumeros);
		comprarContrato.setAdeIdentificador("");
		comprarContrato.setNovoAdeIdentificador(objectFactory.createComprarContratoNovoAdeIdentificador(""));
		comprarContrato.setDataNascimento(objectFactory.createComprarContratoDataNascimento(""));
		comprarContrato.setValorParcela(valorParcela);
		comprarContrato.setValorLiberado(objectFactory.createComprarContratoValorLiberado(Double.valueOf(3000)));
		comprarContrato.setCodVerba(objectFactory.createComprarContratoCodVerba(codVerba));
		comprarContrato.setServicoCodigo(objectFactory.createComprarContratoServicoCodigo(codServico));
		comprarContrato.setPrazo(prazo);
		comprarContrato.setCarencia(objectFactory.createComprarContratoCarencia(carencia));
		comprarContrato.setSenhaServidor(senhaServidor);
		comprarContrato.setLoginServidor(matricula);
		comprarContrato.setTokenAutServidor("");
		comprarContrato.setCorrespondenteCodigo(objectFactory.createComprarContratoCorrespondenteCodigo(""));
		comprarContrato.setIndice(objectFactory.createComprarContratoIndice(""));
		comprarContrato.setValorIof(objectFactory.createComprarContratoValorIof(Double.valueOf(0)));
		comprarContrato.setValorMensVin(objectFactory.createComprarContratoValorMensVin(Double.valueOf(0)));
		comprarContrato.setMatricula(matricula);
		comprarContrato.setCpf(objectFactory.createComprarContratoCpf(cpf));
		comprarContrato.setOrgaoCodigo(objectFactory.createComprarContratoOrgaoCodigo(codOrgao));
		comprarContrato
				.setEstabelecimentoCodigo(objectFactory.createComprarContratoEstabelecimentoCodigo(codEstabelecimento));
		comprarContrato.setBanco(objectFactory.createComprarContratoBanco(""));
		comprarContrato.setAgencia(objectFactory.createComprarContratoAgencia(""));
		comprarContrato.setConta(objectFactory.createComprarContratoConta(""));
		comprarContrato.setTaxaJuros(objectFactory.createComprarContratoTaxaJuros(Double.valueOf(2)));
		comprarContrato.setNaturezaServicoCodigo(objectFactory.createComprarContratoNaturezaServicoCodigo(""));

		return (ComprarContratoResponse) getWebServiceTemplate().marshalSendAndReceive(comprarContrato);
	}
}
