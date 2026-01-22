package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SIMULACAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.VLR_LIBERADO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: SimularConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de simular consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SimularConsignacaoCommand extends RequisicaoExternaCommand {

    public SimularConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validaValorAutorizacao(parametros);
        validaAdePrazo(parametros);
        validaCodigoVerba(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        boolean permiteSimularSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

        String svcCodigo = (String) parametros.get(SVC_CODIGO);
        String orgCodigo = (String) parametros.get(ORG_CODIGO);
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object adePrazo = parametros.get(ADE_PRAZO);
        Object adeVlr = parametros.get(ADE_VLR);

        List<TransferObject> retorno = new ArrayList<>();
        SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);

        // Realiza a Simulação
        try {
            List<TransferObject> simulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, (BigDecimal) adeVlr,
                    (BigDecimal) parametros.get(VLR_LIBERADO),
                    Short.parseShort(adePrazo != null ? adePrazo.toString() : "0"), null, true, CodedValues.PERIODICIDADE_FOLHA_MENSAL, responsavel);

            // Verifica se pode mostrar margem
            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            int qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
            Short incMargem  = paramSvcCse.getTpsIncideMargem();
            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, responsavel);
            BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

            if ((BigDecimal) adeVlr != null && BigDecimal.ZERO.compareTo((BigDecimal) adeVlr) < 0) {
                  if (!((rseMargemRest.compareTo((BigDecimal) adeVlr) >= 0) || permiteSimularSemMargem)) {
                      throw new ZetraException("mensagem.erro.margem.valor.prestacao.maior.margem.disponivel", responsavel);
                  }
              }


            simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);

            for (TransferObject coeficiente : simulacao) {
                if ((Boolean) coeficiente.getAttribute("OK")) {
                    retorno.add(coeficiente);
                }
            }
        } catch (Exception ex) {
            throw new ZetraException(ex);
        }

        if (retorno.size() == 0) {
            throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada.prazo", responsavel);
        } else {
            parametros.put(SIMULACAO, retorno);
        }
    }
}
