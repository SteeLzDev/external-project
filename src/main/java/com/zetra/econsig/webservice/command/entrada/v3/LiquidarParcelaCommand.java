package com.zetra.econsig.webservice.command.entrada.v3;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SPD_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_PREVISTO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_REALIZADO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: LiquidarParcelaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig para liquidar parcela</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LiquidarParcelaCommand extends ConsultarParcelaCommand {

    public LiquidarParcelaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        TransferObject autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);

        if (autorizacao != null) {
            // Valores informados pelo usuário
            String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
            String ocpMotivo = (!TextHelper.isNull(parametros.get(OBSERVACAO))) ? parametros.get(OBSERVACAO).toString().trim() : null;

            /**
             * A operação deverá pesquisar somente parcelas rejeitadas do ADE número informado e
             * informar também número da parcela ou data de desconto da parcela.
             */
            if (TextHelper.isNull(parametros.get(DATA_DESCONTO)) && TextHelper.isNull(parametros.get(PRD_NUMERO))) {
                throw new ZetraException("mensagem.informe.prd.numero.ou.prd.data.desconto", responsavel);
            }

            Boolean permiteLiquidarParcelaFutura = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA, responsavel);
            consultaParcela(parametros, permiteLiquidarParcelaFutura);

            Short prdNumero = null;
            Object objPrdNumero = parametros.get(PRD_NUMERO);
            if (!TextHelper.isNull(objPrdNumero)) {
                if (objPrdNumero instanceof Short) {
                    prdNumero = (Short) objPrdNumero;
                } else if (objPrdNumero instanceof String) {
                    prdNumero = Short.valueOf(objPrdNumero.toString());
                }
            }

            String spdCodigo = (String) parametros.get(SPD_CODIGO);
            Date prdDataDesconto = getDataDesconto(parametros);

            // Se não encontrou parcela e permite liquidar parcela futura, exige que a data de desconto da parcela seja informada
            if (permiteLiquidarParcelaFutura && TextHelper.isNull(spdCodigo) && prdDataDesconto == null) {
                throw new ZetraException("mensagem.informe.prd.data.desconto", responsavel);
            }

            boolean permiteLiquidarParcelaParcial = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean spdCodigosLiquidaParcial = (spdCodigo != null && (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL)));

            if (!TextHelper.isNull(spdCodigo) && ((permiteLiquidarParcelaParcial && !spdCodigosLiquidaParcial && !spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA))
                    || (!permiteLiquidarParcelaParcial && !spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)))) {
                throw new ZetraException("mensagem.erro.parcela.nao.disponivel.liquidacao.manual", responsavel);
            }

            Object vlrRealizadoAux = parametros.get(VALOR_REALIZADO);
            BigDecimal vlrRealizado = (vlrRealizadoAux instanceof Double) ? ((vlrRealizadoAux.equals(Double.NaN)) ? null : BigDecimal.valueOf((Double) vlrRealizadoAux)) : (BigDecimal) vlrRealizadoAux;

            if (vlrRealizado == null || vlrRealizado.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ZetraException("mensagem.erro.valor.desconto.maior.zero", responsavel);
            }

            ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);
            parcelaController.integrarParcela(adeCodigo, prdNumero, vlrRealizado, prdDataDesconto, CodedValues.SPD_LIQUIDADAMANUAL, ocpMotivo, responsavel);

            /**
             * Caso permita liquidar parcela futura, ignora o número da parcela informado e
             * recupera o maior número de parcela que corresponde a parcela criada.
             */
            if (permiteLiquidarParcelaFutura) {
                TransferObject parcela = parcelaController.findParcelaByAdePeriodo(adeCodigo, prdDataDesconto, responsavel);
                prdNumero = Short.parseShort(parcela.getAttribute(Columns.PRD_NUMERO).toString());
            }

            // Remove parâmetro e realiza nova consulta da parcela atualizada
            parametros.put(PRD_NUMERO, prdNumero);
            parametros.put(DATA_DESCONTO, null);
            parametros.put(DATA_REALIZADO, null);
            parametros.put(VLR_PREVISTO, null);
            parametros.put(VLR_REALIZADO, null);
            parametros.put(SPD_CODIGO, null);
            parametros.put(SPD_DESCRICAO, null);
            parametros.put(OBSERVACAO, null);

            consultaParcela(parametros, false);

            parametros.remove(CONSIGNACAO);
        }

    }

}
