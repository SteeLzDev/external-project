package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.DATA_CARGA;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaContrachequeCommand</p>
 * <p>Description: classe command que gera info de um contracheque em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaContrachequeCommand extends RespostaRequisicaoExternaCommand {

    public RespostaContrachequeCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        // Exibe os atributos da autorização
        TransferObject contracheque = (TransferObject) parametros.get(CONTRACHEQUE);

        if (contracheque != null) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(CONTRACHEQUE);

            reg.addAtributo(DATA_CARGA, DateHelper.toDateTimeString((java.util.Date) contracheque.getAttribute(Columns.CCQ_DATA_CARGA)));
            reg.addAtributo(PERIODO, DateHelper.toPeriodString((java.util.Date) contracheque.getAttribute(Columns.CCQ_PERIODO)));
            reg.addAtributo(TEXTO, contracheque.getAttribute(Columns.CCQ_TEXTO));

            respostas.add(reg);
        }

        return respostas;
    }
}
