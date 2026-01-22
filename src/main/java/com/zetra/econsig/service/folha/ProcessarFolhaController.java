package com.zetra.econsig.service.folha;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.HistoricoProcessamento;

/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Controlador para processamento do resultado da folha: margem, retorno, etc.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ProcessarFolhaController {

    public void prepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException;

    public List<HistoricoProcessamento> obterProcessamentosNaoFinalizados(AcessoSistema responsavel) throws ZetraException;

    public List<Integer> obterBlocosAguardProcessamento(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException;

    public void processarBloco(Integer bprCodigo, CacheDependenciasServidor cacheEntidades, HistoricoProcessamento processamento, AcessoSistema responsavel) throws ZetraException;

    public void finalizarProcessamento(String tipoEntidade, String codigoEntidade, HistoricoProcessamento processamento, AcessoSistema responsavel) throws ZetraException;

    public void interromperProcessamento(Date bprPeriodo, String observacao, AcessoSistema responsavel) throws ZetraException;

    public List<Integer> obterBlocosProcessados(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException;

    public void desfazerProcessamentoBloco(Integer bprCodigo, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listarHistoricoProcessamento(Date hprPeriodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ZetraException;

    public List<TransferObject> listarHistoricoProcessamento(Date hprPeriodo, List<String> orgCodigos, List<String> estCodigos, int offset, int count, boolean orderDesc, AcessoSistema responsavel) throws ZetraException;

}
