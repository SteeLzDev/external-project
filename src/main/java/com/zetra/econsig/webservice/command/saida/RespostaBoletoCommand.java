package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CEP;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.ORG_COMPLEMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_ENDERECO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_TELEFONE;
import static com.zetra.econsig.webservice.CamposAPI.ORG_UF;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRD_PAGAS;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RANKING;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.math.BigDecimal;
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
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaBoletoCommand</p>
 * <p>Description: classe command que gera um boleto em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaBoletoCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaBoletoCommand.class);

    public RespostaBoletoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = new ArrayList<>();

        // Exibe os atributos do boleto
        CustomTransferObject boleto = (CustomTransferObject) parametros.get(BOLETO);

        if (boleto != null) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(BOLETO);


            Object ser_sexo = null;
            try {
                ser_sexo = boleto.getAttribute(Columns.SER_SEXO).toString().equalsIgnoreCase("M") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel).toUpperCase() : boleto.getAttribute(Columns.SER_SEXO).toString().equalsIgnoreCase("F") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel).toUpperCase() : "";
            } catch (Exception ex) {
                ser_sexo = boleto.getAttribute(Columns.SER_SEXO);
            }

            Object rse_data_admissao = boleto.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ?
                    DateHelper.toDateString((java.util.Date) boleto.getAttribute(Columns.RSE_DATA_ADMISSAO)) : null;

            Object ade_ano_mes_ini = DateHelper.toPeriodString((java.util.Date) boleto.getAttribute(Columns.ADE_ANO_MES_INI));
            Object ade_ano_mes_fim = boleto.getAttribute(Columns.ADE_ANO_MES_FIM) != null ?
                    DateHelper.toPeriodString((java.util.Date) boleto.getAttribute(Columns.ADE_ANO_MES_FIM)) : "12/2999";

            Object ade_data = boleto.getAttribute(Columns.ADE_DATA) != null ?
                    DateHelper.toDateTimeString((java.util.Date) boleto.getAttribute(Columns.ADE_DATA)) : "";

            Object ade_vlr = null;
            Object vlr_liberado = (boleto.getAttribute("VALOR_LIBERADO") != null) ? boleto.getAttribute("VALOR_LIBERADO") : boleto.getAttribute(Columns.ADE_VLR_LIQUIDO);
            try {
                ade_vlr = NumberHelper.reformat(boleto.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang(), true);
                if (vlr_liberado != null && vlr_liberado instanceof BigDecimal) {
                    vlr_liberado = NumberHelper.reformat(vlr_liberado.toString(), "en", NumberHelper.getLang(), true);
                }
            } catch (Exception ex) {
                ade_vlr = boleto.getAttribute(Columns.ADE_VLR);
            }

            Object taxaMensal = null;
            String svcCodigo = (String) boleto.getAttribute(Columns.SVC_CODIGO);
            if (!TextHelper.isNull(svcCodigo)) {
                ParametroDelegate parDelegate = new ParametroDelegate();
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);
                ParamSvcTO parSvcCse = parDelegate.selectParamSvcCse(svcCodigo.toString(), tpsCodigos, responsavel);
                try {
                    String vlrCoeficiente = (boleto.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.reformat(boleto.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), true) : null;
                    String taxaJuros = boleto.getAttribute(Columns.ADE_TAXA_JUROS) != null ? NumberHelper.reformat(boleto.getAttribute(Columns.ADE_TAXA_JUROS).toString(), "en", NumberHelper.getLang(), true) : null;
                    taxaMensal = parSvcCse.isTpsVlrLiqTaxaJuros() ? taxaJuros : vlrCoeficiente;
                } catch (ParseException ex) {
                    taxaMensal = parSvcCse.isTpsVlrLiqTaxaJuros() ? boleto.getAttribute(Columns.ADE_TAXA_JUROS) : boleto.getAttribute(Columns.CFT_VLR);
                }
            }

            try {
                ParametroDelegate parDelegate = new ParametroDelegate();
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    Object ser_data_nasc = boleto.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.toDateString((java.util.Date) boleto.getAttribute(Columns.SER_DATA_NASC)) : null;
                    reg.addAtributo(DATA_NASCIMENTO, ser_data_nasc);
                }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            reg.addAtributo(SERVIDOR, boleto.getAttribute(Columns.SER_NOME));
            reg.addAtributo(SER_CPF, boleto.getAttribute(Columns.SER_CPF));
            reg.addAtributo(SER_SEXO, ser_sexo);
            reg.addAtributo(SER_EST_CIVIL, boleto.getAttribute(Columns.SER_EST_CIVIL));
            reg.addAtributo(SER_NRO_IDT, boleto.getAttribute(Columns.SER_NRO_IDT));
            reg.addAtributo(SER_NOME_PAI, boleto.getAttribute(Columns.SER_NOME_PAI));
            reg.addAtributo(SER_NOME_MAE, boleto.getAttribute(Columns.SER_NOME_MAE));
            reg.addAtributo(SER_END, boleto.getAttribute(Columns.SER_END));
            reg.addAtributo(SER_NRO, boleto.getAttribute(Columns.SER_NRO));
            reg.addAtributo(SER_COMPL, boleto.getAttribute(Columns.SER_COMPL));
            reg.addAtributo(SER_BAIRRO, boleto.getAttribute(Columns.SER_BAIRRO));
            reg.addAtributo(SER_CIDADE, boleto.getAttribute(Columns.SER_CIDADE));
            reg.addAtributo(SER_UF, boleto.getAttribute(Columns.SER_UF));
            reg.addAtributo(SER_CEP, boleto.getAttribute(Columns.SER_CEP));
            reg.addAtributo(SER_TEL, boleto.getAttribute(Columns.SER_TEL));
            reg.addAtributo(RSE_MATRICULA, boleto.getAttribute(Columns.RSE_MATRICULA));
            reg.addAtributo(RSE_DATA_ADMISSAO, rse_data_admissao);
            reg.addAtributo(RSE_PRAZO, boleto.getAttribute(Columns.RSE_PRAZO));
            reg.addAtributo(RSE_TIPO, boleto.getAttribute(Columns.RSE_TIPO));
            reg.addAtributo(ESTABELECIMENTO, boleto.getAttribute(Columns.EST_NOME));
            reg.addAtributo(ORGAO, boleto.getAttribute(Columns.ORG_NOME));
            reg.addAtributo(ORG_IDENTIFICADOR, boleto.getAttribute(Columns.ORG_IDENTIFICADOR));
            reg.addAtributo(EST_IDENTIFICADOR, boleto.getAttribute(Columns.EST_IDENTIFICADOR));
            reg.addAtributo(ORG_ENDERECO, boleto.getAttribute(Columns.ORG_LOGRADOURO));
            reg.addAtributo(ORG_NUMERO, boleto.getAttribute(Columns.ORG_NRO));
            reg.addAtributo(ORG_COMPLEMENTO, boleto.getAttribute(Columns.ORG_COMPL));
            reg.addAtributo(ORG_BAIRRO, boleto.getAttribute(Columns.ORG_BAIRRO));
            reg.addAtributo(ORG_CIDADE, boleto.getAttribute(Columns.ORG_CIDADE));
            reg.addAtributo(ORG_UF, boleto.getAttribute(Columns.ORG_UF));
            reg.addAtributo(ORG_CEP, boleto.getAttribute(Columns.ORG_CEP));
            reg.addAtributo(ORG_TELEFONE, boleto.getAttribute(Columns.ORG_TEL));
            reg.addAtributo(CONSIGNATARIA, boleto.getAttribute(Columns.CSA_NOME));
            reg.addAtributo(CSA_IDENTIFICADOR, boleto.getAttribute(Columns.CSA_IDENTIFICADOR));
            reg.addAtributo(CNV_COD_VERBA, boleto.getAttribute(Columns.CNV_COD_VERBA));
            reg.addAtributo(RANKING, boleto.getAttribute("RANKING"));
            reg.addAtributo(SERVICO, boleto.getAttribute(Columns.SVC_DESCRICAO));
            reg.addAtributo(SVC_IDENTIFICADOR, boleto.getAttribute(Columns.SVC_IDENTIFICADOR));
            reg.addAtributo(VALOR_LIBERADO, vlr_liberado);
            reg.addAtributo(TAXA_JUROS, taxaMensal);
            reg.addAtributo(DATA_RESERVA, ade_data);
            reg.addAtributo(DATA_INICIAL, ade_ano_mes_ini);
            reg.addAtributo(DATA_FINAL, ade_ano_mes_fim);
            reg.addAtributo(VALOR_PARCELA, ade_vlr);
            reg.addAtributo(ADE_NUMERO, boleto.getAttribute(Columns.ADE_NUMERO));
            reg.addAtributo(ADE_IDENTIFICADOR, boleto.getAttribute(Columns.ADE_IDENTIFICADOR));
            reg.addAtributo(ADE_INDICE, boleto.getAttribute(Columns.ADE_INDICE));
            reg.addAtributo(PRAZO, boleto.getAttribute(Columns.ADE_PRAZO) != null ? boleto.getAttribute(Columns.ADE_PRAZO) : "0");
            reg.addAtributo(ADE_PRD_PAGAS, boleto.getAttribute(Columns.ADE_PRD_PAGAS) != null && !boleto.getAttribute(Columns.ADE_PRD_PAGAS).toString().equals("") ? boleto.getAttribute(Columns.ADE_PRD_PAGAS) : "0");
            reg.addAtributo(SITUACAO, boleto.getAttribute(Columns.SAD_DESCRICAO));
            reg.addAtributo(SAD_CODIGO, boleto.getAttribute(Columns.ADE_SAD_CODIGO) != null ? (String) boleto.getAttribute(Columns.ADE_SAD_CODIGO) : (String) boleto.getAttribute(Columns.SAD_CODIGO));
            reg.addAtributo(RESPONSAVEL, (boleto.getAttribute(Columns.USU_LOGIN) != null ?
                    (boleto.getAttribute(Columns.USU_CODIGO) != null ?
                            (boleto.getAttribute(Columns.USU_CODIGO).toString().equalsIgnoreCase(boleto.getAttribute(Columns.USU_LOGIN).toString()) ?
                                    (boleto.getAttribute(Columns.USU_TIPO_BLOQ) != null ? boleto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : "") :
                                        boleto.getAttribute(Columns.USU_LOGIN).toString()) : boleto.getAttribute(Columns.USU_LOGIN).toString()) : ""));

            respostas.add(reg);
        }
        return respostas;
    }
}
