package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR_V3_0;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;

import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarDadosCadastraisCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig de consultar dados cadastrais</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarDadosCadastraisCommand extends RequisicaoExternaCommand {

    public ConsultarDadosCadastraisCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = parametros.get(RSE_CODIGO).toString();
        String serCodigo = parametros.get(SER_CODIGO).toString();
        String orgCodigo = parametros.get(ORG_CODIGO).toString();

        ServidorDelegate serDelegate = new ServidorDelegate();
        ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        // Busca o servidor
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = serDelegate.findServidor(servidor, responsavel);
        RegistroServidorTO registroServidor = new RegistroServidorTO(rseCodigo);
        registroServidor.setSerCodigo(serCodigo);
        registroServidor = serDelegate.findRegistroServidor(registroServidor, responsavel);
        // Recupera descrição do codigo de estado civil
        String serEstCivil = serDelegate.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Recupera o órgão
        OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);
        EstabelecimentoTransferObject estabelecimento = cseDelegate.findEstabelecimento(orgao.getEstCodigo(), responsavel);

        CustomTransferObject dadosServidor = new CustomTransferObject();
        dadosServidor.setAtributos(servidor.getAtributos()); // Adiciona informações do servidor
        dadosServidor.setAtributos(registroServidor.getAtributos()); // Adiciona informações do registro servidor
        dadosServidor.setAttribute(Columns.SER_EST_CIVIL, serEstCivil); // Adiciona a descrição do estado civil
        dadosServidor.setAtributos(orgao.getAtributos()); // Adiciona Informações do órgão
        dadosServidor.setAtributos(estabelecimento.getAtributos()); // Adiciona Informações do estabelecimento
        dadosServidor.setAttribute(Columns.SER_EMAIL, servidor.getSerEmail());

        // Guarda o boleto no hash para ser consultada na geração do resultado
        if (parametros.get(OPERACAO).equals(CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0)) {
            parametros.put(DADOS_SERVIDOR_V3_0, dadosServidor);
        } else {
            parametros.put(DADOS_SERVIDOR, dadosServidor);
        }
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaServidor(parametros);
    }
}
