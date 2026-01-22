package com.zetra.econsig.webservice.command.entrada.v3;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_PREVISTO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_REALIZADO;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: ConsultarParcelaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar parcela</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarParcelaCommand extends RequisicaoExternaCommand {

    public ConsultarParcelaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        consultaParcela(parametros, false);

        parametros.remove(CONSIGNACAO);
    }

    protected void consultaParcela(Map<CamposAPI, Object> parametros, Boolean retornaParcelaNaoEncontrada) throws ParcelaControllerException, ZetraException {
        final TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            final String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();

            final Date prdDataDesconto = getDataDesconto(parametros);
            Short prdNumero = TextHelper.isNum(parametros.get(PRD_NUMERO)) ? Short.valueOf(parametros.get(PRD_NUMERO).toString()) : null;

            final ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
            final List<TransferObject> lstParcelas = parcelaController.getParcelasOrdenaDataDescontoDesc(adeCodigo, null, prdDataDesconto, prdNumero, responsavel);

            final boolean permiteLiquidarParcelaParcial = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL, CodedValues.TPC_SIM, responsavel);

            if (permiteLiquidarParcelaParcial) {
                final List<ParcelaDescontoTO> parcelasLiquidarParciais = parcelaController.findParcelasLiquidarParcial(adeCodigo, true, prdDataDesconto, prdNumero, responsavel);
                if (parcelasLiquidarParciais != null && !parcelasLiquidarParciais.isEmpty()) {
                    for (final ParcelaDescontoTO parcelasParciais : parcelasLiquidarParciais) {
                        lstParcelas.add(parcelasParciais);
                    }
                }
            }

            if (!retornaParcelaNaoEncontrada && (lstParcelas == null || lstParcelas.isEmpty())) {
                throw new ZetraException("mensagem.informacao.parcela.nao.encontrada", responsavel);
            }

            if (lstParcelas != null && !lstParcelas.isEmpty()) {
                // Sempre recupera a primeira da lista, mesmo que retorne mais de uma, pois deve-se retornar sempre a parcela mais recente
                // do contrato. E a lista já é ordenada por datas.
                final TransferObject parcela = lstParcelas.get(0);

                if (prdNumero == null) {
                    prdNumero = (Short) parcela.getAttribute(Columns.PRD_NUMERO);
                }

                final Integer prdCodigo = (Integer) parcela.getAttribute(Columns.PRD_CODIGO);
                final BigDecimal vlrPrevisto = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_PREVISTO);
                final BigDecimal vlrRealizado = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_REALIZADO);
                final String spdCodigo = (String) parcela.getAttribute(Columns.PRD_SPD_CODIGO);
                String spdDescricao = (String) parcela.getAttribute(Columns.SPD_DESCRICAO);

                // Concatena rótulo de (Parcial) à frente do status da parcela caso o valor realizado seja maior que Zero e menor que o Previsto
                if (!TextHelper.isNull(vlrRealizado) && vlrRealizado.signum() > 0 && vlrRealizado.compareTo(vlrPrevisto) < 0) {
                    spdDescricao += " (" + ApplicationResourcesHelper.getMessage("rotulo.parcial", responsavel) + ")";
                }

                List<TransferObject> lstOcp = parcelaController.lstOcorrenciasParcela(prdCodigo, responsavel);
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    lstOcp = TextHelper.groupConcat(lstOcp, new String[]{Columns.OCP_DATA}, new String[]{Columns.OCP_OBS}, ", ", false, false);
                }

                if (lstOcp != null) {
                	Collections.sort(lstOcp, (o1, o2) -> ((Date) o2.getAttribute(Columns.OCP_DATA)).compareTo((Date) o1.getAttribute(Columns.OCP_DATA)));
                }

                parametros.put(PRD_NUMERO, prdNumero);
                parametros.put(DATA_DESCONTO, parcela.getAttribute(Columns.PRD_DATA_DESCONTO));
                parametros.put(DATA_REALIZADO, parcela.getAttribute(Columns.PRD_DATA_REALIZADO));
                parametros.put(VLR_PREVISTO, vlrPrevisto);
                parametros.put(VLR_REALIZADO, vlrRealizado);
                parametros.put(SPD_CODIGO, spdCodigo);
                parametros.put(SPD_DESCRICAO, spdDescricao);
                parametros.put(OBSERVACAO, lstOcp != null && !lstOcp.isEmpty() ? lstOcp.get(0).getAttribute(Columns.OCP_OBS) : "");
            }
        }
    }

    protected Date getDataDesconto(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object prdDataDescontoStr = parametros.get(DATA_DESCONTO);

        if (prdDataDescontoStr instanceof Date) {
            return (Date) prdDataDescontoStr;
        }

        Date prdDataDesconto = null;
        if (!TextHelper.isNull(prdDataDescontoStr)) {
            try {
                if (prdDataDescontoStr.toString().matches("([0-9]{2})/([0-9]{4})")) {
                    prdDataDesconto = DateHelper.parsePeriodString(prdDataDescontoStr.toString());
                } else {
                    throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                }
            } catch (final ParseException e) {
                throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
            }
        }
        return prdDataDesconto;
    }

}
