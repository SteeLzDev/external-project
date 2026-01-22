package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRD_PAGAS;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA_PROC;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RANKING;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsignacaoCommand</p>
 * <p>Description: classe command que gera info de uma consignação em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsignacaoCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaConsignacaoCommand.class);

    public RespostaConsignacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        ParametroDelegate parDelegate = new ParametroDelegate();

        // Exibe os atributos da autorização
        CustomTransferObject autorizacao = (CustomTransferObject) parametros.get(CONSIGNACAO);

        if (autorizacao != null) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(CONSIGNACAO);

            reg.addAtributo(DATA_RESERVA, DateHelper.toDateTimeString((java.util.Date) autorizacao.getAttribute(Columns.ADE_DATA)));
            reg.addAtributo(DATA_INICIAL, DateHelper.toPeriodString((java.util.Date) autorizacao.getAttribute(Columns.ADE_ANO_MES_INI)));
            reg.addAtributo(DATA_FINAL, autorizacao.getAttribute(Columns.ADE_ANO_MES_FIM) != null ? DateHelper.toPeriodString((java.util.Date) autorizacao.getAttribute(Columns.ADE_ANO_MES_FIM)) : "12/2999");
            reg.addAtributo(ADE_NUMERO, autorizacao.getAttribute(Columns.ADE_NUMERO));
            reg.addAtributo(ADE_IDENTIFICADOR, autorizacao.getAttribute(Columns.ADE_IDENTIFICADOR));
            reg.addAtributo(ADE_INDICE, autorizacao.getAttribute(Columns.ADE_INDICE));
            reg.addAtributo(ESTABELECIMENTO, autorizacao.getAttribute(Columns.EST_NOME));
            reg.addAtributo(ORGAO, autorizacao.getAttribute(Columns.ORG_NOME));
            reg.addAtributo(CONSIGNATARIA, autorizacao.getAttribute(Columns.CSA_NOME));
            reg.addAtributo(CSA_IDENTIFICADOR, autorizacao.getAttribute(Columns.CSA_IDENTIFICADOR));
            reg.addAtributo(ORG_IDENTIFICADOR, autorizacao.getAttribute(Columns.ORG_IDENTIFICADOR));
            reg.addAtributo(EST_IDENTIFICADOR, autorizacao.getAttribute(Columns.EST_IDENTIFICADOR));
            reg.addAtributo(SERVIDOR, autorizacao.getAttribute(Columns.SER_NOME));
            reg.addAtributo(SER_CPF, autorizacao.getAttribute(Columns.SER_CPF));
            reg.addAtributo(RSE_MATRICULA, autorizacao.getAttribute(Columns.RSE_MATRICULA));
            reg.addAtributo(RSE_TIPO, autorizacao.getAttribute(Columns.RSE_TIPO));

            try {
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    String ser_data_nasc = "";
                    if (autorizacao.getAttribute(Columns.SER_DATA_NASC) != null) {
                        ser_data_nasc = autorizacao.getAttribute(Columns.SER_DATA_NASC).toString();
                        if (!ser_data_nasc.equals("0000-00-00") && !ser_data_nasc.equals("0001-01-01") && !ser_data_nasc.equals("1753-01-01")) {
                            ser_data_nasc = DateHelper.reformat(ser_data_nasc, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
                        } else {
                            ser_data_nasc = "";
                        }
                    }
                    reg.addAtributo(DATA_NASCIMENTO, ser_data_nasc);
                }
            } catch (ParseException | ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }


            Object rse_data_admissao = null;
            try {
                rse_data_admissao = autorizacao.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.reformat(autorizacao.getAttribute(Columns.RSE_DATA_ADMISSAO).toString(), LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern()) : "";
            } catch (ParseException ex) {
                rse_data_admissao = autorizacao.getAttribute(Columns.RSE_DATA_ADMISSAO);
            }
            reg.addAtributo(RSE_DATA_ADMISSAO, rse_data_admissao);
            reg.addAtributo(RSE_PRAZO, autorizacao.getAttribute(Columns.RSE_PRAZO));

            if (autorizacao.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) {
                Object vlr_liberado = null;
                try {
                    vlr_liberado = NumberHelper.reformat(autorizacao.getAttribute(Columns.ADE_VLR_LIQUIDO).toString(), "en", NumberHelper.getLang(), true);
                } catch (Exception ex) {
                    vlr_liberado = autorizacao.getAttribute(Columns.ADE_VLR_LIQUIDO);
                }

                reg.addAtributo(VALOR_LIBERADO, vlr_liberado);
                reg.addAtributo(RANKING, autorizacao.getAttribute("RANKING"));
            }

            Object ade_vlr = null;
            try {
                ade_vlr = NumberHelper.reformat(autorizacao.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang(), true);
            } catch (ParseException ex) {
                ade_vlr = autorizacao.getAttribute(Columns.ADE_VLR);
            }
            reg.addAtributo(VALOR_PARCELA, ade_vlr);
            reg.addAtributo(PRAZO, autorizacao.getAttribute(Columns.ADE_PRAZO) != null ?
                                     autorizacao.getAttribute(Columns.ADE_PRAZO) : "0");
            reg.addAtributo(ADE_PRD_PAGAS, autorizacao.getAttribute(Columns.ADE_PRD_PAGAS) != null ?
                                     (autorizacao.getAttribute(Columns.ADE_PRD_PAGAS).toString().equals("") ? "0" : autorizacao.getAttribute(Columns.ADE_PRD_PAGAS)) : "0");
            reg.addAtributo(SERVICO, autorizacao.getAttribute(Columns.SVC_DESCRICAO));
            reg.addAtributo(CNV_COD_VERBA, autorizacao.getAttribute(Columns.CNV_COD_VERBA));
            reg.addAtributo(RESPONSAVEL, (autorizacao.getAttribute(Columns.USU_LOGIN) != null ?
                                           (autorizacao.getAttribute(Columns.USU_CODIGO) != null ?
                                           (autorizacao.getAttribute(Columns.USU_CODIGO).toString().equalsIgnoreCase(autorizacao.getAttribute(Columns.USU_LOGIN).toString()) ?
                                           (autorizacao.getAttribute(Columns.USU_TIPO_BLOQ) != null ? autorizacao.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : "") :
                                           autorizacao.getAttribute(Columns.USU_LOGIN).toString()) : autorizacao.getAttribute(Columns.USU_LOGIN).toString()) : ""));
            reg.addAtributo(SITUACAO, autorizacao.getAttribute(Columns.SAD_DESCRICAO));
            String sadCodigo = (autorizacao.getAttribute(Columns.ADE_SAD_CODIGO) != null) ? (String) autorizacao.getAttribute(Columns.ADE_SAD_CODIGO): (String) autorizacao.getAttribute(Columns.SAD_CODIGO);
            reg.addAtributo(SAD_CODIGO, sadCodigo);
            reg.addAtributo(SVC_IDENTIFICADOR, autorizacao.getAttribute(Columns.SVC_IDENTIFICADOR));
            reg.addAtributo(PARCELA_PROC, (autorizacao.getAttribute(Columns.PRD_ADE_CODIGO) != null ? "S" : "N"));

            Object taxaMensal = null;
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);
            ParamSvcTO parSvcCse = parDelegate.selectParamSvcCse(autorizacao.getAttribute(Columns.SVC_CODIGO).toString(), tpsCodigos, responsavel);
            try {
                String vlrCoeficiente = (autorizacao.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.reformat(autorizacao.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), true) : null;
                String taxaJuros = autorizacao.getAttribute(Columns.ADE_TAXA_JUROS) != null ? NumberHelper.reformat(autorizacao.getAttribute(Columns.ADE_TAXA_JUROS).toString(), "en", NumberHelper.getLang(), true) : null;
                taxaMensal = parSvcCse.isTpsVlrLiqTaxaJuros() ? taxaJuros : vlrCoeficiente;
            } catch (ParseException ex) {
                taxaMensal = parSvcCse.isTpsVlrLiqTaxaJuros() ? autorizacao.getAttribute(Columns.ADE_TAXA_JUROS) : autorizacao.getAttribute(Columns.CFT_VLR);
            }

            reg.addAtributo(TAXA_JUROS, taxaMensal);

            respostas.add(reg);
        } else {
            LOG.warn("REDIRECINANDO PARA COMMNAD DE BOLETO");

            RespostaBoletoCommand boletoCommand = new RespostaBoletoCommand(responsavel);
            boletoCommand.geraRegistrosResposta(parametros);

        }

        return respostas;
    }
}
