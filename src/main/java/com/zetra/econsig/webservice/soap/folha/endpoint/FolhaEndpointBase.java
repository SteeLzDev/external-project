package com.zetra.econsig.webservice.soap.folha.endpoint;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.soap.endpoint.EndpointBase;

/**
 * <p>Title: FolhaEndpointBase</p>
 * <p>Description: Base para os Endpoints de comunicação SOAP do service Folha</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
@SuppressWarnings("java:S1118")
public abstract class FolhaEndpointBase extends EndpointBase {

    protected List<RegistroRespostaRequisicaoExterna> executaOperacao(Map<CamposAPI, Object> parametros, String versaoInterface, AcessoSistema responsavel) throws ZetraException {
        return executaOperacao(CodedValues.FUN_INTEGRA_SOAP_FOLHA, parametros, versaoInterface, responsavel);
    }
}