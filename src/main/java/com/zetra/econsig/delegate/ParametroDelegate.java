package com.zetra.econsig.delegate;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSistCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ParametroAgendamento;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ParametroDelegate</p>
 * <p>Description: Delegate de acesso aos façades de Parametro.</p>
 * <p>Copyright: Copyright (c) 2002-2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroDelegate extends AbstractDelegate {
    private ParametroController parametroController = null;

    public ParametroDelegate() {
    }

    private ParametroController getParametroController() throws ParametroControllerException {
        try {
            if (parametroController == null) {
                parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            }
            return parametroController;
        } catch (Exception ex) {
            throw new ParametroControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    // ParamSvc
    public ParamSvcCseTO findParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().findParamSvcCse(paramSvcCse, responsavel);
    }

    public List<TransferObject> selectParamSvcCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCse(svcCodigo, responsavel);
    }

    public List<TransferObject> selectParamSvcCse(String svcCodigo, String responsavelAltera, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCse(svcCodigo, responsavelAltera, responsavel);
    }

    public ParamSvcTO selectParamSvcCse(String svcCodigo, List<String> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().getParamSvcCseTO(svcCodigo, responsavel);
    }

    public List<TransferObject> recuperaIncidenciasMargem(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().recuperaIncidenciasMargem(responsavel);
    }

    public CustomTransferObject getParamSvcCse(String svcCodigo, String tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().getParamSvcCse(svcCodigo, tpsCodigo, responsavel);
    }

    // ParamSistCse
    public List<TransferObject> selectParamSistCse(String tpcCseAltera, String tpcCseConsulta, String tpcSupAltera, String tpcSupConsulta, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSistCse(tpcCseAltera, tpcCseConsulta, tpcSupAltera, tpcSupConsulta, responsavel);
    }

    public void updateParamSistCse(String psiVlr, String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        ParamSistCseTO paramSistCse = new ParamSistCseTO(tpcCodigo);
        paramSistCse.setCseCodigo(cseCodigo);
        paramSistCse.setPsiVlr(psiVlr);
        getParametroController().updateParamSistCse(paramSistCse, responsavel);
    }

    public String findParamSistCse(String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        ParamSistCseTO criterio = new ParamSistCseTO(tpcCodigo);
        criterio.setCseCodigo(cseCodigo);
        criterio = getParametroController().findParamSistCse(criterio, responsavel);
        return criterio.getPsiVlr();
    }

    public List<TransferObject> selectParamSvcCsa(List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCsa(tpsCodigos, responsavel);
    }

    public List<TransferObject> selectParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, ativo, responsavel);
    }

    public List<TransferObject> selectParamSvcCsa(List<String> svcCodigos, List<String> csaCodigos, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCsa(svcCodigos, csaCodigos, tpsCodigos, ativo, responsavel);
    }

    public List<TransferObject> selectParamSvcCsa(String csaIdentificadorInterno, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().selectParamSvcCsa(csaIdentificadorInterno, tpsCodigos, ativo, responsavel);
    }

    public boolean permiteContratoValorNegativo(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel);
    }

    public boolean senhaServidorObrigatoriaReserva(String rseCodigo, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);
    }

    public boolean requerMatriculaCpf(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().requerMatriculaCpf(false, responsavel);
    }

    public boolean requerMatriculaCpf(boolean lote, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().requerMatriculaCpf(lote, responsavel);
    }

    public Short getSvcIncMargem(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().getSvcIncMargem(svcCodigo, responsavel);
    }

    public String getParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().getParamCsa(csaCodigo, tpaCodigo, responsavel);
    }

    public boolean hasValidacaoDataNasc(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().hasValidacaoDataNasc(responsavel);
    }

    public List<TransferObject> lstTipoNatureza(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().lstTipoNatureza(responsavel);
    }

    public List<TransferObject> lstFuncoesAcessoRecurso(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().lstFuncoesAcessoRecurso(responsavel);
    }

    public List<TransferObject> lstTodasRestricoesAcesso(AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().lstTodasRestricoesAcesso(responsavel);
    }

    public String createRestricaoAcesso(TransferObject restricaoAcessoTO, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().createRestricaoAcesso(restricaoAcessoTO, responsavel);
    }

    // ParametroAgendamento
    public ParametroAgendamento findParamAgendamento(String agdCodigo, String pagNome, String pagValor, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().findParamAgendamento(agdCodigo, pagNome, pagValor, responsavel);
    }

    public List<ParametroAgendamento> findParamAgendamento(String agdCodigo, String pagNome, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().findParamAgendamento(agdCodigo, pagNome, responsavel);
    }

    public void atualizaParamAgendamento(String agdCodigo, String nome, String valor, AcessoSistema responsavel) throws ParametroControllerException {
        getParametroController().atualizaParamAgendamento(agdCodigo, nome, valor, responsavel);
    }

    public void excluirRegraRestricaoAcesso(String rraCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        getParametroController().excluirRegraRestricaoAcesso(rraCodigo, responsavel);
    }

    public List<TransferObject> listaLimitesMaxMinParamSvcCseNse(List<String> tpsCodigos, String nseCodigo, boolean limiteMinimo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().listaLimitesMaxMinParamSvcCseNse(tpsCodigos, nseCodigo, limiteMinimo, responsavel);
    }

    public int calcularAdeCarenciaDiaCorteCsa(int adeCarencia, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        return getParametroController().calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel);
    }
}
