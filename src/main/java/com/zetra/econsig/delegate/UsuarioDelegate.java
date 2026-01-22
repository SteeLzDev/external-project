package com.zetra.econsig.delegate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ArquivoUsuario;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: UsuarioDelegate</p>
 * <p>Description: Delegate para manipulação de usuários</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioDelegate.class);

    private UsuarioController consig = null;

    public UsuarioDelegate() throws UsuarioControllerException {
        try {
            consig = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    // findUsuario
    private UsuarioTransferObject findUsuario(UsuarioTransferObject usuario, AcessoSistema responsavel, String tipo) throws UsuarioControllerException {
        return consig.findUsuario(usuario, tipo, responsavel);
    }

    public CustomTransferObject findTipoUsuario(String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findTipoUsuarioByLogin(usuLogin, responsavel);
    }

    public UsuarioTransferObject findUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        return findUsuario(usuario, responsavel, AcessoSistema.ENTIDADE_USU);
    }

    public UsuarioTransferObject findUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        return findUsuario(usuario, responsavel, AcessoSistema.ENTIDADE_SER);
    }

    public UsuarioTransferObject findUsuarioByLogin(String login, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject();
        usuario.setUsuLogin(login);
        return findUsuario(usuario, responsavel, AcessoSistema.ENTIDADE_USU);
    }

    public List<TransferObject> findUsuarioByEmail(String email, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findUsuarioByEmail(email, responsavel);
    }

    public String findUsuarioPerfil(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findUsuarioPerfil(usuCodigo, responsavel);
    }

    //retorna uma lista de usuários cse
    public List<TransferObject> findUsuarioCseList(AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findUsuarioCseList(responsavel);
    }

    // createUsuario
    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, funcoes, codigoEntidade, tipo, null, true, senhaAberta, responsavel);
    }

    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, funcoes, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, responsavel);
    }

    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, funcoes, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, validaForcaSenha, responsavel);
    }

    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, perCodigo, codigoEntidade, tipo, null, true, senhaAberta, true,responsavel);
    }

    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, perCodigo, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, true,responsavel);
    }

    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createUsuario(usuario, perCodigo, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, validaForcaSenha, responsavel);
    }

    // updateUsuario
    public void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.updateUsuario(usuario, ocorrenciaUsu, null, null, null, null, null, responsavel);
    }

    public void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, List<String> funcoes, String perCodigo, String tipo, String codigoEntidade, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.updateUsuario(usuario, ocorrenciaUsu, funcoes, perCodigo, tipo, codigoEntidade, tipoMotivoOperacao, responsavel);
    }

    // Executa a exclusão lógica de um usuário no sistema.
    public void removeUsuario(UsuarioTransferObject usuario, String tipo, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.removeUsuario(usuario, tipo, tipoMotivoOperacao, responsavel);
    }

    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstUsuariosSer(serCpf, rseMatricula, estIdentificador, orgIdentificador, responsavel);
    }

    // selectUsuariosSer
    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstUsuariosSer(serCpf, rseMatricula, estIdentificador, orgIdentificador, offset, count, responsavel);
    }

    // Perfil
    public Perfil findPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findPerfil(perCodigo, responsavel);
    }

    public Short getPerfilStatus(String perCodigo, String tipo, String entCodigo, AcessoSistema responsavel) throws FindException, UsuarioControllerException {
        return consig.getPerfilStatus(perCodigo, tipo, entCodigo, responsavel);
    }

    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.selectFuncoes(usuCodigo, entidade, tipo, responsavel);
    }

    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.selectFuncoes(usuCodigo, entidade, tipo, filtro, offset, count, responsavel);
    }

    public List<TransferObject> selectFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.selectFuncoes(tipo, responsavel);
    }

    public boolean usuarioTemBloqueioFuncao(String usuCodigo, String funCodigo, String svcCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.usuarioTemBloqueioFuncao(usuCodigo, funCodigo, svcCodigo, responsavel);
    }

    public List<TransferObject> lstFuncaoExigeTmo(String funExigeTmo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstFuncaoExigeTmo(funExigeTmo, responsavel);
    }

    /**
     * ATENÇÃO: Não utilizar este método, a não ser que seja necessário verificar se um terceiro
     * usuário tenha permissão para realizar uma operação. Caso seja para verificar sobre o próprio
     * usuário, utilize a função AcessoSistema.temPermissao()
     * @param usuCodigo
     * @param funCodigo
     * @param tipoEntidade
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public boolean usuarioTemPermissao(String usuCodigo, String funCodigo, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.usuarioTemPermissao(usuCodigo, funCodigo, tipoEntidade, responsavel);
    }

    public TransferObject obtemUsuarioTipo(String usuCodigo, String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.obtemUsuarioTipo(usuCodigo, usuLogin, responsavel);
    }

    /**
     * Funções para Edição de Perfil
     */
    public CustomTransferObject getFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.getFuncao(funCodigo, responsavel);
    }

    public Short getStatusPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.getStatusPerfil(tipoEntidade, codigoEntidade, perCodigo, responsavel);
    }

    public String createOcorrenciaUsuario(CustomTransferObject ocorrencia, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createOcorrenciaUsuario(ocorrencia, responsavel);
    }

    public List<TransferObject> lstOcorrenciaUsuario(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstOcorrenciaUsuario(filtro, offset, count, responsavel);
    }

    public String isOrg(String usuCodigo) throws UsuarioControllerException {
        return consig.isOrg(usuCodigo);
    }

    /**
     * Altera a senha do usuário.
     * @param usuCodigo
     * @param senhaNova
     * @param dicaSenha
     * @param expiracaoImediata
     * @param reiniciacao
     * @param senhaCriptografada
     * @param responsavel
     * @throws UsuarioControllerException
     */
    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.alterarSenha(usuCodigo, senhaNova, dicaSenha, expiracaoImediata, reiniciacao, senhaCriptografada, null, senhaAtualAberta, responsavel);
    }

    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, CustomTransferObject tipoMotivoOperacao, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.alterarSenha(usuCodigo, senhaNova, dicaSenha, expiracaoImediata, reiniciacao, senhaCriptografada, tipoMotivoOperacao, senhaAtualAberta, responsavel);
    }

    /**
     * Retorna os dados do usuário servidor juntamente com informações sobre sua senha
     * @param rseCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public TransferObject getSenhaServidor(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.getSenhaServidor(rseCodigo, responsavel);
    }

    /**
     * Gera nova senha de autorização para o usuário servidor
     * @param usuCodigo
     * @param totem
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.gerarSenhaAutorizacao(usuCodigo, totem, responsavel);
    }

    /**
     * Gera nova senha de autorização para o usuário servidor
     * @param usuCodigo
     * @param totem
     * @param responsavel
     * @param naoComunicarServidor
     * @return
     * @throws UsuarioControllerException
     */
    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, boolean naoComunicarServidor, boolean validaQtdeSenhaAutorizacao, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.gerarSenhaAutorizacao(usuCodigo, totem, naoComunicarServidor, validaQtdeSenhaAutorizacao, responsavel);
    }

    /**
     * Valida a quantidade máxima de senhas de autorização múltiplas que podem ser geradas para um servidor.
     * Validação somente é executada caso o sistema utilize senhas múltiplas de autorização.
     *
     * @param usuCodigo
     * @param responsavel
     * @throws UsuarioControllerException
     */
    public void validaQtdeSenhaAutorizacao(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.validaQtdeSenhaAutorizacao(usuCodigo, responsavel);
    }

    /**
     * Cancela a senha de autorização do usuário.
     * @param usuCodigo
     * @param senhaNaoUtilizada
     * @param responsavel
     * @throws UsuarioControllerException
     */
    public void cancelaSenhaAutorizacao(String usuCodigo, String senhaNaoUtilizada, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.cancelaSenhaAutorizacao(usuCodigo, senhaNaoUtilizada, responsavel);
    }

    /**
     * Cria o protocolo de senha de autorização do servidor.
     *
     * @param psaCodigo
     * @param usuCodigoResponsavel
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public String createProtocoloSenhaAutorizacao(String psaCodigo, String usuCodigoAfetado, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.createProtocoloSenhaAutorizacao(psaCodigo, usuCodigoAfetado, responsavel);
    }

    /**
     * Retorna protocolo de senha de autorização.
     *
     * @param psaCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public TransferObject getProtocoloSenhaAutorizacao(String psaCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.getProtocoloSenhaAutorizacao(psaCodigo, responsavel);
    }

    /**
     * Invalida as senhas dá autorizalção dos servidores de acordo com o prazo de validade
     * definido no parâmetro de sistema.
     * @param responsavel
     * @throws UsuarioControllerException
     */
    public List<TransferObject> listarSenhasExpiradasCancelamentoAut(AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.listarSenhasExpiradasCancelamentoAut(responsavel);
    }

    /**
     * Lista as senhas de autorização do servidor.
     * @param usuCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public List<TransferObject> lstSenhaAutorizacaoServidor(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstSenhaAutorizacaoServidor(usuCodigo, responsavel);
    }


    /**
     * Retorna a quantidade de senha de autorização de usuário servidor gerada no dia atual via host a host.
     *
     * @param usuCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public int qtdeSenhaAutorizacaoUsuSerDiaHostAHost(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.qtdeSenhaAutorizacaoUsuSerDiaHostAHost(usuCodigo, responsavel);
    }

    /**
     * Recupera os dados da senha de autorização do servidor.
     * @param usuCodigo
     * @param sasSenha
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public TransferObject obtemSenhaAutorizacaoServidor(String usuCodigo, String sasSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.obtemSenhaAutorizacaoServidor(usuCodigo, sasSenha, responsavel);
    }

    public void alteraDataUltimoAcessoSistema(AcessoSistema responsavel) throws UsuarioControllerException {
        consig.alteraDataUltimoAcessoSistema(responsavel);
    }

    public List<TransferObject> lstFuncoesPermitidasPerfil(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstFuncoesPermitidasPerfil(tipo, codigoEntidade, responsavel);
    }

    public void bloquearUsuarioMotivoSeguranca(String usuCodigo, String entidadeOperada, String operacao, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.bloquearUsuarioMotivoSeguranca(usuCodigo, entidadeOperada, operacao, responsavel);
    }

    public void aprovarCadastroUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.aprovarCadastroUsuarioSer(usuCodigo, responsavel);
    }

    public Map<String, List<TransferObject>> lstUsuarioAuditorEntidade(AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstUsuarioAuditorEntidade(responsavel);
    }

    public String gerarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.gerarSenhasUsuServidores(responsavel);
    }

    public void ativarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException {
        consig.ativarSenhasUsuServidores(responsavel);
    }

    public List<TransferObject> findFuncaoAuditavelPorEntidade(String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findFuncaoAuditavelPorEntidade(codigoEntidade, tipoEntidade, responsavel);
    }

    public void bloqueiaUsuariosFimVigencia(AcessoSistema responsavel) throws UsuarioControllerException {
        consig.bloqueiaUsuariosFimVigencia(responsavel);
    }

    /**
     * Gera token de sessão para usuário
     * @param usuCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public String gerarChaveSessaoUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.gerarChaveSessaoUsuario(usuCodigo, responsavel);
    }

    /**
     * Cadastra ou atualiza a chave de dispositivo do usuário
     * @param usuCodigo
     * @param tdiCodigo
     * @param deviceToken
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public void cadastroDeviceToken(String usuCodigo, String tdiCodigo, String deviceToken, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.cadastroDeviceToken(usuCodigo, tdiCodigo, deviceToken, responsavel);
    }

    /**
     * pesquisa por chave de dispositivo gerado pelos servidores de mensagem de terceiros que o usuário tenha acessado por último
     * @param usuCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public String findDeviceToken(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findDeviceToken(usuCodigo, responsavel);
    }

    /**
     * Recupera um token de acesso
     * @param token
     * @return UsuarioChaveSessao
     * @throws FindException se não for encontrado token
     */
    public UsuarioChaveSessao findUsuarioChaveSessao(String token) throws FindException {
        UsuarioChaveSessao usuChaveSessao = null;
        usuChaveSessao = consig.findUsuarioChaveSessao(token);
        return usuChaveSessao;
    }

    /**
     * Remove um token de acesso
     * @param usuCodigo
     * @throws UsuarioControllerException
     */
    public void deleteUsuarioChaveSessao(String usuCodigo) throws UsuarioControllerException {
        consig.deleteUsuarioChaveSessao(usuCodigo);
    }

    public UsuarioChaveSessao validateToken(String token) throws UsuarioControllerException {
        return consig.validateToken(token);
    }

    public void recuperarSenha(String usuCodigo, String tipoEntidade, String chaveRecuperarSenha, String senhaNova, String dicaSenha, boolean autoDesbloqueio, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.recuperarSenha(usuCodigo, tipoEntidade, chaveRecuperarSenha, senhaNova, dicaSenha, autoDesbloqueio, responsavel);
    }

    public void recuperarSenha(String cpf, String login, List<String> orgCodigos, String otp, String senhaNova, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException {
        consig.recuperarSenha(cpf, login, orgCodigos, otp, senhaNova, senhaApp, responsavel);
    }

    public Collection<ArquivoUsuario> findArquivoUsuario(String usuCodigo, String tarCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findArquivoUsuario(usuCodigo, tarCodigo, responsavel);
    }

    public List<TransferObject> enviaNotificacaoUsuariosPorTempoInatividade(AcessoSistema responsavel) throws UsuarioControllerException, HQueryException {
        return consig.enviaNotificacaoUsuariosPorTempoInatividade(responsavel);
    }

    public void enviarCodigoAutorizacaoSms(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException, UsuarioControllerException {
        consig.enviarCodigoAutorizacaoSms(rseCodigo, responsavel);
    }

    public List<TransferObject> lstPerfil(String tipoEntidade, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.lstPerfil(tipoEntidade, codigoEntidade, filtro, responsavel);
    }

    public Perfil findPerfilByUsuCodigo(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return consig.findPerfilByUsuCodigo(usuCodigo, responsavel);
    }
}
