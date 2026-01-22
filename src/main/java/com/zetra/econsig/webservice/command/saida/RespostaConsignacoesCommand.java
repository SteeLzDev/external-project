package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACOES;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRD_PAGAS;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RESUMO;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsignacoesCommand</p>
 * <p>Description: classe command que gera info de múltipas consignações em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsignacoesCommand extends RespostaRequisicaoExternaCommand {

    public RespostaConsignacoesCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        // Adiciona vários registros com as consignações encontradas na pesquisa,
        // para que o usuário possa escolher uma
        List<TransferObject> consignacoes = (parametros.get(CONSIGNACOES) != null) ? (List<TransferObject>) parametros.get(CONSIGNACOES) : (List<TransferObject>) parametros.get(CONSIGNACAO);
        for (TransferObject consignacao : consignacoes) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(RESUMO);

            reg.addAtributo( ADE_NUMERO, consignacao.getAttribute(Columns.ADE_NUMERO));
            reg.addAtributo( ADE_IDENTIFICADOR, consignacao.getAttribute(Columns.ADE_IDENTIFICADOR));
            reg.addAtributo( ADE_INDICE, consignacao.getAttribute(Columns.ADE_INDICE));
            reg.addAtributo( RESPONSAVEL, (consignacao.getAttribute(Columns.USU_LOGIN) != null ?
                    (consignacao.getAttribute(Columns.USU_CODIGO) != null ?
                    (consignacao.getAttribute(Columns.USU_CODIGO).toString().equalsIgnoreCase(consignacao.getAttribute(Columns.USU_LOGIN).toString()) ?
                    (consignacao.getAttribute(Columns.USU_TIPO_BLOQ) != null ? consignacao.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : "") :
                    consignacao.getAttribute(Columns.USU_LOGIN).toString()) : consignacao.getAttribute(Columns.USU_LOGIN).toString()) : ""));
            reg.addAtributo( SERVICO, consignacao.getAttribute(Columns.SVC_DESCRICAO).toString());
            reg.addAtributo( CNV_COD_VERBA, consignacao.getAttribute(Columns.CNV_COD_VERBA));
            Object ade_data = null;
            try {
                ade_data = DateHelper.reformat(consignacao.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            } catch (ParseException ex) {
                ade_data = consignacao.getAttribute(Columns.ADE_DATA);
            }

            reg.addAtributo( DATA_RESERVA, ade_data);

            Object ade_vlr = null;
            try {
                ade_vlr = NumberHelper.reformat(consignacao.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang(), true);
            } catch (ParseException ex) {
                ade_vlr = consignacao.getAttribute(Columns.ADE_VLR);
            }
            reg.addAtributo( VALOR_PARCELA, ade_vlr);

            reg.addAtributo( PRAZO, consignacao.getAttribute(Columns.ADE_PRAZO) != null ?
                    consignacao.getAttribute(Columns.ADE_PRAZO) : "0");
            reg.addAtributo( ADE_PRD_PAGAS, consignacao.getAttribute(Columns.ADE_PRD_PAGAS) != null ?
                    (consignacao.getAttribute(Columns.ADE_PRD_PAGAS).toString().equals("") ?
                    "0" : consignacao.getAttribute(Columns.ADE_PRD_PAGAS)) : "0");
            reg.addAtributo( SITUACAO, consignacao.getAttribute(Columns.SAD_DESCRICAO));
            reg.addAtributo(SAD_CODIGO, consignacao.getAttribute(Columns.SAD_CODIGO));
            reg.addAtributo(SVC_IDENTIFICADOR, consignacao.getAttribute(Columns.SVC_IDENTIFICADOR));
            reg.addAtributo(CSA_NOME, consignacao.getAttribute(Columns.CSA_NOME));
            reg.addAtributo(CSA_IDENTIFICADOR, consignacao.getAttribute(Columns.CSA_IDENTIFICADOR));

            respostas.add(reg);
        }

        return respostas;
    }

}
