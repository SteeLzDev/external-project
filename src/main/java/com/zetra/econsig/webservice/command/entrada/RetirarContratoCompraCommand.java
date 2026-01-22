package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.CompraContratoDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RetirarContratoCompraCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de retirar compra de contrato</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RetirarContratoCompraCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RetirarContratoCompraCommand.class);

    public RetirarContratoCompraCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        exigeMotivoOperacao(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        ArrayList<TransferObject> autList = (ArrayList<TransferObject>) parametros.get(CONSIGNACAO);
        Long adeNumero = (Long) parametros.get(ADE_NUMERO);

        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        TransferObject autdes = null;
        if (autList != null && autList.size() == 1) { // Otimização caso o contrato venha pela validação
            autdes = autList.get(0);
        } else {
            autdes = adeDelegate.findAutDescontoByAdeNumero(adeNumero, responsavel);
        }

        String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        List<String> listStc = new ArrayList<>();
        listStc.add(CodedValues.NOT_EQUAL_KEY);
        listStc.add(CodedValues.STC_CANCELADO.toString());
        listStc.add(CodedValues.STC_FINALIZADO.toString());
        List<TransferObject> relacionamentos = adeDelegate.pesquisarConsignacaoRelacionamento(adeCodigo, null, null, responsavel.getCsaCodigo(), CodedValues.TNT_CONTROLE_COMPRA, listStc, responsavel);

        if (relacionamentos == null || relacionamentos.isEmpty()) {
            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        } else if (relacionamentos.size() > 1) {
            throw new ZetraException("mensagem.erro.mais.de.um.relacionamento.compra.contrato", responsavel);
        }

        String obs = (String) parametros.get(OBS);
        String tmoObs = (String) parametros.get(TMO_OBS);
        String tmoIdentificador = (String) parametros.get(TMO_IDENTIFICADOR);

        CustomTransferObject tmoTO = null;
        if (!TextHelper.isNull(tmoIdentificador)) {
            try {
                TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);

                tmoTO = new CustomTransferObject();
                tmoTO.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                tmoTO.setAttribute(Columns.TMO_CODIGO, tmo.getTmoCodigo());
                tmoTO.setAttribute(Columns.OCA_OBS, tmoObs);
            } catch (TipoMotivoOperacaoControllerException tex) {
                LOG.error(tex.getMessage(), tex);
                throw new ZetraException("mensagem.erro.tipo.motivo.nao.encontrado", responsavel);
            }
        }

        CompraContratoDelegate compraDelegate = new CompraContratoDelegate();
        compraDelegate.retirarContratoCompra(adeCodigo, obs, tmoTO, responsavel);
    }
}
