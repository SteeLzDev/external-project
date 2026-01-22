package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.AlongarConsignacao;
import com.zetra.econsig.soap.AlongarConsignacaoResponse;
import com.zetra.econsig.soap.ObjectFactory;

public class AlongarConsignacaoClient extends WebServiceGatewaySupport {

	public AlongarConsignacaoResponse getResponse(String usuario, String senha, Long adeNumero, String adeIdentificador,
			String dataNascimento, String valorParcela, String valorLiberado, String codVerba, String codServico,
			Integer prazo, int carencia, String senhaServidor, String loginServidor, String indice, String cpf,
			String codOrgao, String codEstabelecimento, String banco, String agencia, String conta) {

		ObjectFactory objectFactory = new ObjectFactory();

		AlongarConsignacao alongarConsignacao = new AlongarConsignacao();
		alongarConsignacao.setCliente(objectFactory.createAlongarConsignacaoCliente(""));
		alongarConsignacao.setConvenio(objectFactory.createAlongarConsignacaoConvenio("LOCAL"));
		alongarConsignacao.setUsuario(usuario);
		alongarConsignacao.setSenha(senha);
		alongarConsignacao.setAdeNumero(objectFactory.createAlongarConsignacaoAdeNumero(adeNumero));
		alongarConsignacao
				.setAdeIdentificador(objectFactory.createAlongarConsignacaoAdeIdentificador(adeIdentificador));
		alongarConsignacao.setNovoAdeIdentificador(objectFactory.createAlongarConsignacaoNovoAdeIdentificador(""));
		alongarConsignacao.setDataNascimento(objectFactory.createAlongarConsignacaoDataNascimento(dataNascimento));
		alongarConsignacao.setValorParcela(valorParcela);
		alongarConsignacao.setValorLiberado(objectFactory.createAlongarConsignacaoValorLiberado(valorLiberado));
		alongarConsignacao.setCodVerba(objectFactory.createAlongarConsignacaoCodVerba(codVerba));
		alongarConsignacao.setServicoCodigo(objectFactory.createAlongarConsignacaoServicoCodigo(codServico));
		alongarConsignacao.setPrazo(prazo);
		alongarConsignacao.setCarencia(objectFactory.createAlongarConsignacaoCarencia(carencia));
		alongarConsignacao.setSenhaServidor(senhaServidor);
		alongarConsignacao.setLoginServidor(loginServidor);
		alongarConsignacao.setTokenAutServidor("");
		alongarConsignacao.setCorrespondenteCodigo(objectFactory.createAlongarConsignacaoCorrespondenteCodigo(""));
//		alongarConsignacao.setValorTac(objectFactory.createAlongarConsignacaoValorTac());
		alongarConsignacao.setIndice(objectFactory.createAlongarConsignacaoIndice(indice));
//		alongarConsignacao.setValorIof(objectFactory.createAlongarConsignacaoValorIof());
//		alongarConsignacao.setValorMensVin(objectFactory.createAlongarConsignacaoValorMensVin());		
		alongarConsignacao.setMatricula(loginServidor);
		alongarConsignacao.setCpf(objectFactory.createAlongarConsignacaoCpf(cpf));
		alongarConsignacao.setOrgaoCodigo(objectFactory.createAlongarConsignacaoOrgaoCodigo(codOrgao));
		alongarConsignacao.setEstabelecimentoCodigo(
				objectFactory.createAlongarConsignacaoEstabelecimentoCodigo(codEstabelecimento));
		alongarConsignacao.setBanco(objectFactory.createAlongarConsignacaoBanco(banco));
		alongarConsignacao.setAgencia(objectFactory.createAlongarConsignacaoAgencia(agencia));
		alongarConsignacao.setConta(objectFactory.createAlongarConsignacaoConta(conta));
		alongarConsignacao.setNaturezaServicoCodigo(objectFactory.createAlongarConsignacaoNaturezaServicoCodigo(""));

		return (AlongarConsignacaoResponse) getWebServiceTemplate().marshalSendAndReceive(alongarConsignacao);
	}

}
