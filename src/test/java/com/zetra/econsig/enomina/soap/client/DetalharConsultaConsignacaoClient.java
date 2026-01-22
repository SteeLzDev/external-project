package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.DetalharConsultaConsignacao;
import com.zetra.econsig.soap.DetalharConsultaConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.SituacaoContrato;
import com.zetra.econsig.soap.SituacaoServidor;

public class DetalharConsultaConsignacaoClient extends WebServiceGatewaySupport {

	public DetalharConsultaConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero,
			String adeIdentificador, String matricula, String cpf, String orgaoCodigo, String estabelecimentoCodigo,
			String servicoCodigo, String codigoVerba, SituacaoContrato situacaoContrato,
			SituacaoServidor situacaoServidor) {

		ObjectFactory objectFactory = new ObjectFactory();

		DetalharConsultaConsignacao detalharConsultaConsignacao = new DetalharConsultaConsignacao();
		detalharConsultaConsignacao.setCliente(objectFactory.createDetalharConsultaConsignacaoCliente(""));
		detalharConsultaConsignacao.setConvenio(objectFactory.createDetalharConsultaConsignacaoConvenio("LOCAL"));
		detalharConsultaConsignacao.setUsuario(usuario);
		detalharConsultaConsignacao.setSenha(senha);
		detalharConsultaConsignacao.setAdeNumero(objectFactory.createDetalharConsultaConsignacaoAdeNumero(adeNumero));
		detalharConsultaConsignacao
				.setAdeIdentificador(objectFactory.createDetalharConsultaConsignacaoAdeIdentificador(adeIdentificador));
		detalharConsultaConsignacao.setMatricula(matricula);
		detalharConsultaConsignacao.setCpf(objectFactory.createDetalharConsultaConsignacaoCpf(cpf));
		detalharConsultaConsignacao
				.setOrgaoCodigo(objectFactory.createDetalharConsultaConsignacaoOrgaoCodigo(orgaoCodigo));
		detalharConsultaConsignacao.setEstabelecimentoCodigo(
				objectFactory.createDetalharConsultaConsignacaoEstabelecimentoCodigo(estabelecimentoCodigo));
		detalharConsultaConsignacao
				.setCorrespondenteCodigo(objectFactory.createDetalharConsultaConsignacaoCorrespondenteCodigo(""));
		detalharConsultaConsignacao
				.setServicoCodigo(objectFactory.createDetalharConsultaConsignacaoServicoCodigo(servicoCodigo));
		detalharConsultaConsignacao
				.setCodigoVerba(objectFactory.createDetalharConsultaConsignacaoCodigoVerba(codigoVerba));
		detalharConsultaConsignacao
				.setSdvSolicitado(objectFactory.createDetalharConsultaConsignacaoSdvSolicitado(false));
		detalharConsultaConsignacao.setSdvSolicitadoNaoCadastrado(
				objectFactory.createDetalharConsultaConsignacaoSdvSolicitadoNaoCadastrado(false));
		detalharConsultaConsignacao.setSdvSolicitadoCadastrado(
				objectFactory.createDetalharConsultaConsignacaoSdvSolicitadoCadastrado(false));
		detalharConsultaConsignacao
				.setSdvNaoSolicitado(objectFactory.createDetalharConsultaConsignacaoSdvNaoSolicitado(false));
		detalharConsultaConsignacao.setPeriodo(objectFactory.createDetalharConsultaConsignacaoPeriodo(""));
		detalharConsultaConsignacao
				.setDataInclusaoInicio(objectFactory.createDetalharConsultaConsignacaoDataInclusaoInicio(""));
		detalharConsultaConsignacao
				.setDataInclusaoFim(objectFactory.createDetalharConsultaConsignacaoDataInclusaoFim(""));
		detalharConsultaConsignacao.setIntegraFolha(objectFactory.createDetalharConsultaConsignacaoIntegraFolha(""));
		detalharConsultaConsignacao.setCodigoMargem(objectFactory.createDetalharConsultaConsignacaoCodigoMargem(""));
		detalharConsultaConsignacao.setIndice(objectFactory.createDetalharConsultaConsignacaoIndice(""));
		detalharConsultaConsignacao
				.setSituacaoContrato(objectFactory.createDetalharConsultaConsignacaoSituacaoContrato(situacaoContrato));
		detalharConsultaConsignacao
				.setSituacaoServidor(objectFactory.createDetalharConsultaConsignacaoSituacaoServidor(situacaoServidor));

		return (DetalharConsultaConsignacaoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(detalharConsultaConsignacao);
	}

	public DetalharConsultaConsignacaoResponse getResponse(String usuario, String senha, String matricula,
			String correspondenteCodigo, boolean sdvSolicitado, boolean sdvSolicitadoNaoCadastrado,
			boolean sdvSolicitadoCadastrado, boolean sdvNaoSolicitado, String periodo, String dataInclusao,
			String dataInclusaoFim, String integraFolha, String codigoMargem, String indice) {

		ObjectFactory objectFactory = new ObjectFactory();

		DetalharConsultaConsignacao detalharConsultaConsignacao = new DetalharConsultaConsignacao();
		detalharConsultaConsignacao.setCliente(objectFactory.createDetalharConsultaConsignacaoCliente(""));
		detalharConsultaConsignacao.setConvenio(objectFactory.createDetalharConsultaConsignacaoConvenio("LOCAL"));
		detalharConsultaConsignacao.setUsuario(usuario);
		detalharConsultaConsignacao.setSenha(senha);
		detalharConsultaConsignacao
				.setAdeNumero(objectFactory.createDetalharConsultaConsignacaoAdeNumero(Long.valueOf(0)));
		detalharConsultaConsignacao
				.setAdeIdentificador(objectFactory.createDetalharConsultaConsignacaoAdeIdentificador(""));
		detalharConsultaConsignacao.setMatricula(matricula);
		detalharConsultaConsignacao.setCpf(objectFactory.createDetalharConsultaConsignacaoCpf(""));
		detalharConsultaConsignacao.setOrgaoCodigo(objectFactory.createDetalharConsultaConsignacaoOrgaoCodigo(""));
		detalharConsultaConsignacao
				.setEstabelecimentoCodigo(objectFactory.createDetalharConsultaConsignacaoEstabelecimentoCodigo(""));
		detalharConsultaConsignacao.setCorrespondenteCodigo(
				objectFactory.createDetalharConsultaConsignacaoCorrespondenteCodigo(correspondenteCodigo));

		detalharConsultaConsignacao
				.setSdvSolicitado(objectFactory.createDetalharConsultaConsignacaoSdvSolicitado(sdvSolicitado));
		detalharConsultaConsignacao.setSdvSolicitadoNaoCadastrado(
				objectFactory.createDetalharConsultaConsignacaoSdvSolicitadoNaoCadastrado(sdvSolicitadoNaoCadastrado));
		detalharConsultaConsignacao.setSdvSolicitadoCadastrado(
				objectFactory.createDetalharConsultaConsignacaoSdvSolicitadoCadastrado(sdvSolicitadoCadastrado));
		detalharConsultaConsignacao
				.setSdvNaoSolicitado(objectFactory.createDetalharConsultaConsignacaoSdvNaoSolicitado(sdvNaoSolicitado));
		detalharConsultaConsignacao.setPeriodo(objectFactory.createDetalharConsultaConsignacaoPeriodo(periodo));
		detalharConsultaConsignacao
				.setDataInclusaoInicio(objectFactory.createDetalharConsultaConsignacaoDataInclusaoInicio(dataInclusao));
		detalharConsultaConsignacao
				.setDataInclusaoFim(objectFactory.createDetalharConsultaConsignacaoDataInclusaoFim(dataInclusaoFim));
		detalharConsultaConsignacao
				.setIntegraFolha(objectFactory.createDetalharConsultaConsignacaoIntegraFolha(integraFolha));
		detalharConsultaConsignacao
				.setCodigoMargem(objectFactory.createDetalharConsultaConsignacaoCodigoMargem(codigoMargem));
		detalharConsultaConsignacao.setIndice(objectFactory.createDetalharConsultaConsignacaoIndice(indice));

		return (DetalharConsultaConsignacaoResponse) getWebServiceTemplate()
				.marshalSendAndReceive(detalharConsultaConsignacao);
	}
}
