package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.OBS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: SolicitarRecalculoSaldoDevedorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de solicitar recálculo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitarRecalculoSaldoDevedorCommand extends RequisicaoExternaCommand {

    public SolicitarRecalculoSaldoDevedorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();
        ArrayList<TransferObject> autList = (ArrayList<TransferObject>) parametros.get(CONSIGNACAO);
        Long adeNumero = (Long) parametros.get(ADE_NUMERO);

        TransferObject autdes = null;
        if (autList != null && autList.size() == 1) { // Otimização caso o contrato venha pela validação
            autdes = autList.get(0);
        } else {
            autdes = adeDelegate.findAutDescontoByAdeNumero(adeNumero, responsavel);
        }

        String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);

        // a combinação de parâmetros deve retornar apenas um registro
        List<String> listStc = new ArrayList<>();
        listStc.add(CodedValues.STC_AGUARD_APRV_SDV.toString());
        listStc.add(CodedValues.STC_AGUARD_PG_SDV.toString());
        List<TransferObject> relacionamentos = adeDelegate.pesquisarConsignacaoRelacionamento(adeCodigo, null, null, responsavel.getCsaCodigo(), CodedValues.TNT_CONTROLE_COMPRA, listStc, responsavel);

        if (relacionamentos == null || relacionamentos.isEmpty()) {
            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        } else if (relacionamentos.size() > 1) {
            throw new ZetraException("mensagem.erro.mais.de.um.relacionamento.compra.contrato", responsavel);
        }

        String obs = (String) parametros.get(OBS);

        sdvDelegate.solicitarRecalculoSaldoDevedor(adeCodigo, obs, responsavel);

        ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
        // Verifica se a consignatária pode ser desbloqueada automaticamente
        if (responsavel.isCsaCor()) {
            if (csaDelegate.verificarDesbloqueioAutomaticoConsignataria((String) parametros.get(CSA_CODIGO), responsavel)) {
                parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
            }
        }
    }
}
