package com.zetra.econsig.webservice;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;
import com.zetra.econsig.webservice.soap.factory.RequisicaoExternaCommandFactory;
import com.zetra.econsig.webservice.soap.factory.RespostaRequisicaoExternaFactory;

/**
 * <p>Title: RequisicaoExternaAppController</p>
 * <p>Description: Application controller para chamada às operações do eConsig feita por sistemas externos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RequisicaoExternaAppController {

    public static RespostaRequisicaoExternaCommand createRespostaRequisicaoExterna(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return RespostaRequisicaoExternaFactory.createRespostaRequisicaoExterna(parametros, responsavel);
    }

    public static RequisicaoExternaCommand createRequisicaoExternaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        return RequisicaoExternaCommandFactory.createRequisicaoExternaCommand(parametros, responsavel);
    }
}
