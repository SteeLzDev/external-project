package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.DATA;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.TIPO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDescontoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaHistoricoCommand</p>
 * <p>Description: classe command que gera um histórico em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaHistoricoCommand extends RespostaRequisicaoExternaCommand {

    public RespostaHistoricoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        //List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);
        List<RegistroRespostaRequisicaoExterna> respostas = new ArrayList<>();

        // Mostra os históricos da consignação
        if (parametros.get(HISTORICO) != null) {
            List<TransferObject> historico = (List<TransferObject>) parametros.get(HISTORICO);

            for (TransferObject cto : historico) {
                RegistroRespostaRequisicaoExterna reg1 = new RegistroRespostaRequisicaoExterna();
                reg1.setNome(HISTORICO);

                Object oca_data = null;
                try {
                    oca_data = DateHelper.reformat(cto.getAttribute(Columns.OCA_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                } catch (Exception ex) {
                    oca_data = cto.getAttribute(Columns.OCA_DATA);
                }

                reg1.addAtributo(DATA, oca_data);
                reg1.addAtributo(RESPONSAVEL, (cto.getAttribute(Columns.USU_LOGIN) != null ?
                                                (cto.getAttribute(Columns.USU_CODIGO) != null ?
                                                (cto.getAttribute(Columns.USU_CODIGO).toString().equalsIgnoreCase(cto.getAttribute(Columns.USU_LOGIN).toString()) ?
                                                (cto.getAttribute(Columns.USU_TIPO_BLOQ) != null ? cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : "") :
                                                 cto.getAttribute(Columns.USU_LOGIN).toString()) : cto.getAttribute(Columns.USU_LOGIN).toString()) : ""));
                reg1.addAtributo(TIPO, cto.getAttribute(Columns.TOC_DESCRICAO).toString());

                String tmoDescricao = (String) cto.getAttribute(Columns.TMO_DESCRICAO);
                String oca_obs = StatusAutorizacaoDescontoHelper.formataOcaObs(cto.getAttribute(Columns.OCA_OBS).toString(), tmoDescricao, responsavel);

                Object periodo = cto.getAttribute(Columns.OCA_PERIODO);
                if (periodo != null) {
                    String ocaPeriodoRef = DateHelper.toPeriodString((Date) periodo);
                    oca_obs = new StringBuilder(oca_obs).append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.oca.info.referencia", responsavel, ocaPeriodoRef)).toString();
                }

                // Remove javascript de Relacionamento de Controle de compra/Renegociação
                if (oca_obs.indexOf("<a href=") != -1) {
                    oca_obs = oca_obs.substring(0, oca_obs.indexOf("<a href=")) + oca_obs.substring(oca_obs.indexOf("</a>") + 4);
                }

                reg1.addAtributo(DESCRICAO, oca_obs);

                respostas.add(reg1);
            }
        }

        return respostas;
    }
}
