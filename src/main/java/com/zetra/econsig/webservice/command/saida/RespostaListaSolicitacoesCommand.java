package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SOLICITACAO;
import static com.zetra.econsig.webservice.CamposAPI.SOLICITACOES;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaListaSolicitacoesCommand</p>
 * <p>Description: classe command que gera uma lista de solicitações em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaListaSolicitacoesCommand extends RespostaRequisicaoExternaCommand {

    public RespostaListaSolicitacoesCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        Object ade_data = null, ade_vlr = null, adeVlrLiquido;

        // Cria registros de lista de solicitações
        List<TransferObject> solicitacoes = (List<TransferObject>) parametros.get(SOLICITACOES);
        if (solicitacoes != null) {
            for (TransferObject solicitacao : solicitacoes) {
                RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
                reg.setNome(SOLICITACAO);

                try {
                    ade_data = DateHelper.reformat(solicitacao.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                } catch (ParseException ex) {
                    ade_data = solicitacao.getAttribute(Columns.ADE_DATA);
                }
                reg.addAtributo(DATA_RESERVA, ade_data);
                reg.addAtributo(ADE_NUMERO, solicitacao.getAttribute(Columns.ADE_NUMERO));
                reg.addAtributo(SERVIDOR, solicitacao.getAttribute(Columns.SER_NOME));
                reg.addAtributo(SER_TEL, solicitacao.getAttribute(Columns.SER_TEL));
                reg.addAtributo(SER_CPF, solicitacao.getAttribute(Columns.SER_CPF));
                reg.addAtributo(RSE_MATRICULA, solicitacao.getAttribute(Columns.RSE_MATRICULA));

                //busca detalhes do órgão e estabelecimento da solicitação
                if (!TextHelper.isNull(solicitacao.getAttribute(Columns.RSE_ORG_CODIGO))) {
                    CustomTransferObject orgFiltro = new CustomTransferObject();
                    orgFiltro.setAttribute(Columns.ORG_CODIGO, solicitacao.getAttribute(Columns.RSE_ORG_CODIGO));
                    ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

                    List<TransferObject> orgList = cseDelegate.lstOrgaos(orgFiltro, responsavel);
                    if (orgList != null && !orgList.isEmpty()) {
                        TransferObject orgaoTO = orgList.get(0);
                        reg.addAtributo(ORG_IDENTIFICADOR, orgaoTO.getAttribute(Columns.ORG_IDENTIFICADOR));
                        reg.addAtributo(ORG_NOME, orgaoTO.getAttribute(Columns.ORG_NOME));
                        reg.addAtributo(EST_IDENTIFICADOR, orgaoTO.getAttribute(Columns.EST_IDENTIFICADOR));
                        reg.addAtributo(EST_NOME, orgaoTO.getAttribute(Columns.EST_NOME));
                    }
                }

                try {
                    ade_vlr = NumberHelper.reformat(solicitacao.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang(), true);
                } catch (ParseException ex) {
                    ade_vlr = solicitacao.getAttribute(Columns.ADE_VLR);
                }

                adeVlrLiquido = ((solicitacao.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? NumberHelper.format((new BigDecimal(solicitacao.getAttribute(Columns.ADE_VLR_LIQUIDO).toString())).doubleValue(), NumberHelper.getLang()) : "");
                reg.addAtributo(VALOR_LIBERADO, adeVlrLiquido);

                reg.addAtributo(VALOR_PARCELA, ade_vlr);

                reg.addAtributo(PRAZO, solicitacao.getAttribute(Columns.ADE_PRAZO) != null ?
                        solicitacao.getAttribute(Columns.ADE_PRAZO) : "0");
                reg.addAtributo(SERVICO, solicitacao.getAttribute(Columns.SVC_DESCRICAO));
                reg.addAtributo(SVC_IDENTIFICADOR, solicitacao.getAttribute(Columns.SVC_IDENTIFICADOR));
                reg.addAtributo(CNV_COD_VERBA, solicitacao.getAttribute(Columns.CNV_COD_VERBA));

                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);
                ParametroDelegate parDelegate = new ParametroDelegate();
                ParamSvcTO parSvcCse = parDelegate.selectParamSvcCse(solicitacao.getAttribute(Columns.SVC_CODIGO).toString(), tpsCodigos, responsavel);

                String vlrLiqTaxaJuros = ((solicitacao.getAttribute(Columns.ADE_TAXA_JUROS) != null) ? NumberHelper.format((new BigDecimal(solicitacao.getAttribute(Columns.ADE_TAXA_JUROS).toString())).doubleValue(), NumberHelper.getLang()) : "");
                String vlrCoeficiente = ((solicitacao.getAttribute(Columns.CFT_VLR) != null) ? NumberHelper.format((new BigDecimal(solicitacao.getAttribute(Columns.CFT_VLR).toString())).doubleValue(), NumberHelper.getLang()) : "");
                String taxaMensal = parSvcCse.isTpsVlrLiqTaxaJuros() ? vlrLiqTaxaJuros : vlrCoeficiente;
                reg.addAtributo(TAXA_JUROS, taxaMensal);

                respostas.add(reg);
            }
        }

        return respostas;
    }

}
