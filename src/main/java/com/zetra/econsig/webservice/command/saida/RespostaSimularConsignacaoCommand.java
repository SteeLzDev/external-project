package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RANKING;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaSimularConsignacaoCommand</p>
 * <p>Description: classe command que gera info de simulação em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaSimularConsignacaoCommand extends RespostaRequisicaoExternaCommand {

    public RespostaSimularConsignacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        List<TransferObject> simulacao = (List<TransferObject>) parametros.get(SIMULACAO);
        if (simulacao != null) {
            // Adiciona vários registros com o resultado da simulação
            for (TransferObject coeficiente : simulacao) {
                RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
                reg.setNome(SIMULACAO);

                String csa_nome = (String) coeficiente.getAttribute(Columns.CSA_NOME_ABREV);
                if (csa_nome == null || csa_nome.equals("")) {
                    csa_nome = (String) coeficiente.getAttribute(Columns.CSA_NOME);
                }

                reg.addAtributo(CSA_IDENTIFICADOR, coeficiente.getAttribute(Columns.CSA_IDENTIFICADOR));
                reg.addAtributo(CONSIGNATARIA, csa_nome.toUpperCase());

                String valor = null;
                try {
                    valor = NumberHelper.reformat(coeficiente.getAttribute("VLR_LIBERADO").toString(), "en", NumberHelper.getLang(), true);
                } catch (ParseException ex) {
                    valor = coeficiente.getAttribute("VLR_LIBERADO").toString();
                }
                reg.addAtributo(VALOR_LIBERADO, valor);

                if (((BigDecimal) coeficiente.getAttribute("VLR_PARCELA")).equals(new BigDecimal(Double.MAX_VALUE))) {
                    valor = "0,00";
                } else {
                    try {
                        valor = NumberHelper.reformat(coeficiente.getAttribute("VLR_PARCELA").toString(), "en", NumberHelper.getLang(), true);
                    } catch (ParseException ex) {
                        valor = coeficiente.getAttribute("VLR_PARCELA").toString();
                    }
                }
                reg.addAtributo(VALOR_PARCELA, valor);

                try {
                    valor = NumberHelper.reformat(coeficiente.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), true);
                } catch (ParseException ex) {
                    valor = coeficiente.getAttribute(Columns.CFT_VLR).toString();
                }
                reg.addAtributo(TAXA_JUROS, valor);

                reg.addAtributo(RANKING, coeficiente.getAttribute("RANKING"));
                reg.addAtributo(SERVICO, coeficiente.getAttribute(Columns.SVC_DESCRICAO));
                reg.addAtributo(SVC_IDENTIFICADOR, coeficiente.getAttribute(Columns.SVC_IDENTIFICADOR));
                respostas.add(reg);
            }
        }

        return respostas;
    }
}
