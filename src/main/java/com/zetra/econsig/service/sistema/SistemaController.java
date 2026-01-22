package com.zetra.econsig.service.sistema;


import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.ModeloEmail;
import com.zetra.econsig.persistence.entity.OperacaoNaoConfirmada;
import com.zetra.econsig.persistence.entity.RecursoSistema;

/**
 * <p>Title: SistemaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SistemaController {

    public boolean isSistemaBloqueado(AcessoSistema responsavel);

    public Short verificaBloqueioSistema(String cseCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void alteraStatusSistema(String cseCodigo, Short status, String msg, boolean alteraMsgSistema, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void alteraStatusSistema(String cseCodigo, Short status, String msg, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstTipoOcorrencia(List<String> tocCodigos, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> pesquisarBancos(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstCampoSistema(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstCampoSistema(String casChave, boolean somenteCamposEditaveis, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void carregarCampoSistema(Map<String, String> recursos, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<RecursoSistema> lstRecursoSistema(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstParamSenhaExterna(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstUf(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstTipoJustica(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void arquivarConsignacoesFinalizadas(AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstCidadeUf(String ufCod, String termo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstCidadeCodigoIBGE(String cidCodigoIbge, AcessoSistema responsavel) throws ConsignanteControllerException;

    public boolean temDbOcorrencia(String dboArquivo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void executarBatchScript(String nomeArquivo, String conteudo, AcessoSistema responsavel) throws ConsignanteControllerException;

    // Modelo de Email
    public ModeloEmail findModeloEmail(String memCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public byte[] obtemChaveCriptografiaArquivos(String papCodigo, String tarCodigo, String caaCodigoEnt, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> findCep(String cepCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    // Texto Sistema
    public List<TransferObject> lstTextoSistema(AcessoSistema responsavel) throws ConsignanteControllerException;

    public void carregarTextoSistema(Map<String, String> recursos, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void gravarPaginaOperacaoSensivel(String conteudo, String ipAcesso, Map<String, Map<String, String[]>> parametros, UploadHelper uploadHelper, String hashDir, AcessoRecurso acessoRecurso, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void gravarAnexosFilaAutorizacao(String oncCodigo, String arqNome, Long tamArq, String conteudoArq, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> listarFilaOperacao(AcessoSistema responsavel)  throws ConsignanteControllerException;

    public int countOperacoesFilaAutorizacao(AcessoSistema responsavel)  throws ConsignanteControllerException;

    public OperacaoNaoConfirmada findOperacaoNaoConfirmada (String oncCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void descartarOpFilaAutorizacao (String oncCodigoDescartar, String obsDescarte, Map<HttpHelper.SessionKeysEnum, String> sessionConfig, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void confirmarOperacaoFila (String oncConfirmar, Map<HttpHelper.SessionKeysEnum, String> sessionConfig, AcessoSistema responsavel) throws ConsignanteControllerException;

    public com.zetra.econsig.persistence.entity.AcessoRecurso findAcessoRecurso (String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<String> listarPapeisEnvioEmailOperacoes(String funCodigo, String papCodigoOperador, AcessoSistema responsavel);
}
