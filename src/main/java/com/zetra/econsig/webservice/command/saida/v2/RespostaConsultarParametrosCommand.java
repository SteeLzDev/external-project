package com.zetra.econsig.webservice.command.saida.v2;

import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.v2.ConsultarParametrosCommand;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

/**
 * <p>Title: RespostaConsultarParametrosCommand</p>
 * <p>Description: classe command que gera conjunto de parâmetros de resposta.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarParametrosCommand extends com.zetra.econsig.webservice.command.saida.RespostaConsultarParametrosCommand {

    public RespostaConsultarParametrosCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        CustomTransferObject paramSet = (CustomTransferObject) parametros.get(PARAMETRO_SET);

        paramSet.setAttribute(ConsultarParametrosCommand.USA_CET, paramSet.getAttribute(ConsultarParametrosCommand.USA_CET));

        return respostas;
    }
}
