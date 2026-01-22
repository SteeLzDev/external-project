package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.OCORRENCIA_DATA;
import static com.zetra.econsig.webservice.CamposAPI.OCORRENCIA_OBS;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_DESCONTO;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: AtualizarParcelaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de atualizar parcela</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AtualizarParcelaCommand extends RequisicaoExternaCommand {

    public AtualizarParcelaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaDadosAtualizacaoParcela(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        CustomTransferObject autorizacao = (CustomTransferObject) parametros.get(CONSIGNACAO);

        if (autorizacao != null) {
            try {
                ParcelaController parcelaController = ApplicationContextProvider.getApplicationContext().getBean(ParcelaController.class);

                String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
                Short prdNumero = Short.valueOf(parametros.get(PARCELA).toString());

                parcelaController.integrarParcela(adeCodigo, prdNumero, (BigDecimal) parametros.get(VALOR_DESCONTO),
                        null, parametros.get(SITUACAO_CODIGO).toString(), parametros.get(OCORRENCIA_OBS).toString(), responsavel);

                return; // Retorna para não mandar as informações de volta
            } catch (ParcelaControllerException ex) {
                throw ex;
            }
        }
    }

    private void validaDadosAtualizacaoParcela(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object adeNumero = parametros.get(ADE_NUMERO);

        if (adeNumero == null || adeNumero.toString().equals("")) {
            throw new ZetraException("mensagem.informe.ade.numero", responsavel);
        }
        if (parametros.get(PARCELA) == null || parametros.get(PARCELA).toString().equals("")) {
            throw new ZetraException("mensagem.informe.prd.numero", responsavel);
        }
        if (parametros.get(SITUACAO_CODIGO) == null || parametros.get(SITUACAO_CODIGO).toString().equals("")) {
            throw new ZetraException("mensagem.informe.prd.status.unico", responsavel);
        }
        if (parametros.get(DATA_DESCONTO) == null || parametros.get(DATA_DESCONTO).toString().equals("")) {
            throw new ZetraException("mensagem.informe.prd.data.desconto", responsavel);
        }
        if (parametros.get(VALOR_DESCONTO) == null || parametros.get(VALOR_DESCONTO).toString().equals("")) {
            throw new ZetraException("mensagem.informe.prd.valor.desconto", responsavel);
        }
        if (parametros.get(OCORRENCIA_DATA) == null || parametros.get(OCORRENCIA_DATA).toString().equals("")) {
            throw new ZetraException("mensagem.informe.oca.data", responsavel);
        }
        if (parametros.get(OCORRENCIA_OBS) == null || parametros.get(OCORRENCIA_OBS).toString().equals("")) {
            throw new ZetraException("mensagem.informe.oca.observacao", responsavel);
        }

        try {
            String dataRealizado = DateHelper.reformat(parametros.get(DATA_DESCONTO).toString(), LocaleHelper.getDatePattern(), LocaleHelper.FORMATO_DATA_INGLES);
            parametros.put(DATA_DESCONTO, dataRealizado);
        } catch (ParseException ex) {
            throw new ZetraException("mensagem.erro.data.desconto.invalido", responsavel);
        }

        Object vlrDesconto = parametros.get(VALOR_DESCONTO);
        Object valorDesconto = null;
        if (vlrDesconto instanceof Double) {
            valorDesconto =  BigDecimal.valueOf((Double) vlrDesconto);
        } else {
            valorDesconto = NumberHelper.parseDecimal(parametros.get(VALOR_DESCONTO).toString());
        }
        if (valorDesconto != null) {
            if (((BigDecimal) valorDesconto).doubleValue() < 0) {
                throw new ZetraException("mensagem.erro.valor.desconto.maior.zero", responsavel);
            }
            parametros.put(VALOR_DESCONTO, valorDesconto);
        } else {
            throw new ZetraException("mensagem.erro.valor.desconto.invalido", responsavel);
        }

        try {
            String dataObs = DateHelper.reformat(parametros.get(OCORRENCIA_DATA).toString(), LocaleHelper.getDateTimePattern(), "yyyy-MM-dd HH:mm:ss");
            parametros.put(OCORRENCIA_DATA, dataObs);
        } catch (ParseException ex) {
            throw new ZetraException("mensagem.erro.oca.data.invalida", responsavel);
        }
    }
}
