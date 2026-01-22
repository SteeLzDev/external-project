package com.zetra.econsig.service.folha;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportaMovimentoController</p>
 * <p>Description: Session Façade para Rotina de Exportação de Movimento</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ExportaMovimentoController {

    public void criarTabelasExportacaoMovFin(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void validarExportacaoMovimento(List<String> orgCodigos, List<String> estCodigos, boolean tipoRetornoIntegracao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, List<String> adeNumeros, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> listaOrgaosExpMovFin(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> selectResumoExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, boolean exportar, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void enviarEmailDownloadNaoRealizadoMovFin(AcessoSistema responsavel) throws ConsignanteControllerException;

    public String compactarAnexosAdePeriodo(List<String> orgCodigos, List<String> estCodigos, List<String> codVerbas, String zipFileNameOutPut, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> consultarMovimentoFinanceiro(String periodo, String rseMatricula, String serCpf, String orgIdentificador, String estIdentificador, String csaIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void exportaMovimentoFinanceiroAutomaticoOrgao(AcessoSistema responsavel) throws ConsignanteControllerException;
}
