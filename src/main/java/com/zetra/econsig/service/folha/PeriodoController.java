package com.zetra.econsig.service.folha;


import java.util.Date;
import java.util.List;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PeriodoControllerBean</p>
 * <p>Description: Session Bean para a rotina de manipulação do período.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PeriodoController  {

    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, boolean ultimoPeriodo, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoExpMovimento(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoImpRetorno(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoImpRetornoAtrasado(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, Date periodoRetAtrasado, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoCalculoMargem(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, AcessoSistema responsavel) throws PeriodoException;
    public Date obtemUltimoPeriodoExportado(List<String> orgCodigos, List<String> estCodigos, boolean temRetorno, Date periodo, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException;
    public Set<Date> listarPeriodosPermitidos(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException;
    public Date obtemPeriodoAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException;
    public TransferObject obtemPeriodoExportacaoDistinto(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException;
    public Integer obtemPrazoEntrePeriodos(String orgCodigo, Date periodoInicial, Date periodoFinal, AcessoSistema responsavel) throws PeriodoException;
    public boolean periodoPermiteApenasReducoes(Date periodo, String orgCodigo, AcessoSistema responsavel) throws PeriodoException;
    public List<Date> obtemPeriodoAgrupado(String orgCodigo, Date dataLimite, AcessoSistema responsavel) throws PeriodoException;
    public TransferObject obtemPeriodoPorData(List<String> orgCodigos, List<String> estCodigos, Date data, AcessoSistema responsavel) throws PeriodoException;


    /** Parte responsavel pelo periodo do modulo de Beneficio **/

    public List<TransferObject> obtemPeriodoBeneficio(List<String> orgCodigos, List<String> estCodigos, boolean gravarPeriodo, Date periodo, AcessoSistema responsavel) throws PeriodoException;
    public List<TransferObject> obtemPeriodoBeneficioAtual(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws PeriodoException;
    public Date obtemPeriodoBeneficioAposPrazo(String orgCodigo, Integer qtdPeriodos, Date periodoInicial, boolean ignoraPeriodosAgrupados, AcessoSistema responsavel) throws PeriodoException;
    public Integer obtemQtdPeriodoAgrupado(String orgCodigo, Date periodoInicio, Date periodoFim, AcessoSistema responsavel) throws PeriodoException;
}
