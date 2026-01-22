package com.zetra.econsig.service.parcela;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;

/**
 * <p>Title: ParcelaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParcelaController {

    public ParcelaDescontoTO findParcelaByAdeCodigoPrdCodigo(String adeCodigo, Integer prdCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public void liquidarTodasParcelas(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException;

    public void rejeitarTodasParcelas(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException;

    public void integrarParcela(String spdCodigo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ParcelaControllerException;

    public void integrarParcela(String adeCodigo, Short prdNumero, BigDecimal prdVlrRealizado, Date prdDataDesconto, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;
    public void integrarParcela(String adeCodigo, Integer prdCodigo, BigDecimal prdVlrRealizado, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;
    public void integrarParcela(String adeCodigo, Map<String, BigDecimal> vlrRealizadoPorParcela, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;

    public Integer desfazIntegracao(String adeCodigo, Integer prdCodigo, String tocCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> getParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, int offset, int count, List<String> papCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> getParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, int offset, int count, List<String> papCodigos, TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> getParcelasOrdenaDataDescontoDesc(String adeCodigo, List<String> spdCodigos, Date prdDataDesconto, Short prdNumero, AcessoSistema responsavel) throws ParcelaControllerException;

    public int countParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, List<String> papCodigos, TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException;

    public int countParcelas(String tipo, String adeNumero, String rseMatricula, String serCpf, String orgCodigo, String csaCodigo, List<String> spdCodigos, List<String> papCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<ParcelaDescontoTO> findParcelas(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> getHistoricoParcelas(Date prdDataDesconto, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> getHistoricoParcelas(String adeCodigo, List<String> spdCodigos, List<String> tocCodigos, boolean arquivado, int offset, int count, boolean exibeParcelaEmAberto, AcessoSistema responsavel) throws ParcelaControllerException;

    public Map<String, String> selectStatusParcela(AcessoSistema responsavel) throws ParcelaControllerException;

    public List<ParcelaDescontoPeriodo> findByAutDescontoStatus(String adeCodigo, String spdCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public TransferObject findParcelaByAdePeriodo(String adeCodigo, Date prdDataDesconto, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> lstOcorrenciasParcela(Integer prdCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> lstResumoParcelasPerido(Date periodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public void criaParcelaDesconto(ParcelaDescontoTO parcela, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;

    public TransferObject findStatusParcelaDesconto(String spdCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<ParcelaDescontoTO> findParcelasLiquidarParcial(String adeCodigo, boolean ordenar, Date prdDataDesconto, Short prdNumero, AcessoSistema responsavel) throws ParcelaControllerException;

    public void reimplentarParcela(String adeCodigo, Map<String, BigDecimal> vlrPrevistoPorParcela, String spdCodigo, String ocpMotivo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<ParcelaDescontoTO> findParcelasReimplantarManual(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> findParcelasPeriodo(String adeCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> adesParcelasFuturasByPeriodo(TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException;

    public Date dataLimiteOcorrencia(String adeCodigo, String tocCodigo)  throws FindException;

    public boolean consignacaoAptaCarenciaConclusao(String adeCodigo, AcessoSistema responsavel) throws ParcelaControllerException;

    public List<TransferObject> listarParcelasPorCsa(TransferObject criterio, AcessoSistema responsavel) throws ParcelaControllerException;

}