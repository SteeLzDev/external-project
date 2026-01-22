package com.zetra.econsig.enomina.soap.client;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.zetra.econsig.soap.ListarParcelas;
import com.zetra.econsig.soap.ListarParcelasResponse;
import com.zetra.econsig.soap.ObjectFactory;
import com.zetra.econsig.soap.SituacaoParcela;

public class ListarParcelasClient extends WebServiceGatewaySupport {

    public ListarParcelasResponse getResponse(String usuario, String senha, String dataDesconto) {

        final ObjectFactory objectFactory = new ObjectFactory();

        final ListarParcelas listarParcelas = new ListarParcelas();
        listarParcelas.setCliente(objectFactory.createListarParcelasCliente(""));
        listarParcelas.setConvenio(objectFactory.createListarParcelasConvenio("LOCAL"));
        listarParcelas.setUsuario(usuario);
        listarParcelas.setSenha(senha);
        listarParcelas.setDataDesconto(dataDesconto);
        return (ListarParcelasResponse) getWebServiceTemplate().marshalSendAndReceive(listarParcelas);
    }

    public ListarParcelasResponse getResponse(String usuario, String senha, String dataDesconto, SituacaoParcela situacaoParcela, Long adeNumero,
                                              String adeIdentificador, Integer pagina, String estabelecimentoCodigo,
                                              String orgaoCodigo, String servicoCodigo, String codigoVerba, String cpf, String matricula) {

        final ObjectFactory objectFactory = new ObjectFactory();

        final ListarParcelas listarParcelas = new ListarParcelas();
        listarParcelas.setCliente(objectFactory.createListarParcelasCliente(""));
        listarParcelas.setConvenio(objectFactory.createListarParcelasConvenio("LOCAL"));
        listarParcelas.setUsuario(usuario);
        listarParcelas.setSenha(senha);
        listarParcelas.setDataDesconto(dataDesconto);
        listarParcelas.setPagina(objectFactory.createListarParcelasPagina(pagina));
        listarParcelas.setAdeNumero(objectFactory.createListarParcelasAdeNumero(adeNumero));
        listarParcelas.setAdeIdentificador(objectFactory.createListarParcelasAdeIdentificador(adeIdentificador));
        listarParcelas.setEstabelecimentoCodigo(objectFactory.createListarParcelasEstabelecimentoCodigo(estabelecimentoCodigo));
        listarParcelas.setOrgaoCodigo(objectFactory.createListarParcelasOrgaoCodigo(orgaoCodigo));
        listarParcelas.setServicoCodigo(objectFactory.createListarParcelasServicoCodigo(servicoCodigo));
        listarParcelas.setCodigoVerba(objectFactory.createListarParcelasCodigoVerba(codigoVerba));
        listarParcelas.setCpf(objectFactory.createListarParcelasCpf(cpf));
        listarParcelas.setMatricula(objectFactory.createListarParcelasMatricula(matricula));

        return (ListarParcelasResponse) getWebServiceTemplate().marshalSendAndReceive(listarParcelas);
    }

}
