package com.zetra.econsig.delegate;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: CompraContratoDelegate</p>
 * <p>Description: Delegate para manipulacao do Período.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PeriodoDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PeriodoDelegate.class);

    private PeriodoController controller = null;

    public PeriodoDelegate() throws PeriodoException {
        try {
            controller = ApplicationContextProvider.getApplicationContext().getBean(PeriodoController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PeriodoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, boolean ultimoPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoExpMovimento(orgCodigos, estCodigos, gravarPeriodo, ultimoPeriodo, responsavel);
    }

    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoExpMovimento(orgCodigos, estCodigos, gravarPeriodo, responsavel);
    }

    public List<TransferObject> obtemPeriodoImpRetorno(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoImpRetorno(orgCodigos, estCodigos, gravarPeriodo, responsavel);
    }

    public List<TransferObject> obtemPeriodoCalculoMargem(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoCalculoMargem(orgCodigos, estCodigos, gravarPeriodo, responsavel);
    }

    public Date obtemUltimoPeriodoExportado(List<String> orgCodigos, List<String> estCodigos, boolean temRetorno, Date periodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemUltimoPeriodoExportado(orgCodigos, estCodigos, temRetorno, periodo, responsavel);
    }

    public List<TransferObject> obtemPeriodoAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoAtual(orgCodigos, estCodigos, responsavel);
    }

    public Set<Date> listarPeriodosPermitidos(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException {
        return controller.listarPeriodosPermitidos(orgCodigo, dataLimite, responsavel);
    }

    public Date obtemPeriodoAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoAposPrazo(orgCodigo, qtdPeriodos, periodoInicial, ignoraPeriodosAgrupados, responsavel);
    }

    public Integer obtemPrazoEntrePeriodos(String orgCodigo, Date periodoInicial, Date periodoFinal, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPrazoEntrePeriodos(orgCodigo, periodoInicial, periodoFinal, responsavel);
    }

    public TransferObject obtemPeriodoExportacaoDistinto(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
    }

    public boolean periodoPermiteApenasReducoes(Date periodo, String orgCodigo, AcessoSistema responsavel) throws PeriodoException {
        return controller.periodoPermiteApenasReducoes(periodo, orgCodigo, responsavel);
    }

    /** Parte responsavel pelo periodo do modulo de Beneficio **/

    public List<TransferObject> obtemPeriodoBeneficio(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, Date periodo, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoBeneficio(orgCodigos, estCodigos, gravarPeriodo, periodo, responsavel);
    }

    public List<TransferObject> obtemPeriodoBeneficioAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoBeneficioAtual(orgCodigos, estCodigos, responsavel);
    }

    public Date obtemPeriodoBeneficioAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException {
        return controller.obtemPeriodoBeneficioAposPrazo(orgCodigo, qtdPeriodos, periodoInicial, ignoraPeriodosAgrupados, responsavel);
    }

}
