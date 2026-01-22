package com.zetra.econsig.service.folha;

import java.util.Map;

import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImpCadMargemController</p>
 * <p>Description: Session Bean para a rotina de cadastro de margens.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImpCadMargemController {

    public String[] obtemArquivosConfiguracao(String nomeArquivoEntrada, String estCodigo, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public String importaCadastroMargens(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, boolean margemTotal, boolean geraTransferidos, AcessoSistema responsavel) throws ServidorControllerException;

    public String importaServidoresTransferidos(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean qtdLinhasArqTransferidosAcimaPermitido(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException;

    public String processarLinhaMargem(Map<String, Object> entrada, CacheDependenciasServidor cacheEntidades, String periodo, AcessoSistema responsavel) throws ServidorControllerException;

    public String processarLinhaTransferidos(Map<String, Object> entrada, AcessoSistema responsavel) throws ZetraException;
}
