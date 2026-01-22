package com.zetra.econsig.service.usuario;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ArquivoUsuario;
import com.zetra.econsig.persistence.entity.CampoUsuario;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.Papel;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;

/**
 * <p>Title: UsuarioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
* $Revision$
 * $Date$
 */
public interface UsuarioController  {
    // Usuario
    public UsuarioTransferObject findUsuario(UsuarioTransferObject usuario, String tipo, AcessoSistema responsavel) throws UsuarioControllerException;
    public UsuarioTransferObject findUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;
    public UsuarioTransferObject findUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;
    public UsuarioTransferObject findUsuarioByLogin(String login, AcessoSistema responsavel) throws UsuarioControllerException;

    public CustomTransferObject findTipoUsuarioByLogin(String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException;
    public CustomTransferObject findTipoUsuarioByCodigo(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> findUsuarioByEmail(String email, AcessoSistema responsavel) throws UsuarioControllerException;

    public Usuario findUsuarioByEmailAndToken(String email, String token, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<UsuarioTransferObject> lstUsuariosSerByEmail(String email, AcessoSistema responsavel) throws UsuarioControllerException;

    public String findUsuarioPerfil(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<String> getUsuarioFuncoes(String usuCodigo, String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException;
    
    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    public void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, List<String> funcoes, String perCodigo, String tipo, String codigoEntidade, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public void removeUsuario(UsuarioTransferObject usuario, String tipo, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject obtemUsuarioTipo(String usuCodigo, String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> getUsuarios(String tipo, String codigo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listUsuarios(String tipo, String codigo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listUsuariosAtivosComEmail(AcessoSistema responsavel) throws UsuarioControllerException;
    
    public int countUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    public int countUsuarios(String tipo, String codigo, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    public int listCountUsuarios(String tipo, String codigo, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    // Usuario Servidor
    public List<TransferObject> lstUsuariosSerByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuariosSerByRseCodigos(List<String> rseCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuariosSerLoginComCpf(String usuLogin, String rseMatricula, String serCpf, String estIdentificador, String orgIdentificador, boolean serSomenteAtivo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void aprovarCadastroUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    // Perfil
    public Perfil findPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> selectFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public CustomTransferObject getFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, CustomTransferObject filtro, int offset, int size, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, String> selectFuncoesRestricaoAcesso(String usuCodigo, String entidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public boolean usuarioTemPermissao(String usuCodigo, String funCodigo, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createPerfil(String tipoEntidade, String codigoEntidade, String perDescricao, String perVisivel, Date perDataExpiracoa, String perEntAltera, String perOrigem, String perAutoDesbloqueio, List<String> funCodigo, String perIpAcesso, String perDdnsAcesso, AcessoSistema responsavel) throws UsuarioControllerException;

    public void updatePerfil(String tipoEntidade, String codigoEntidade, String perCodigo, String perDescricao, String perVisivel, Date perDataExpiracao, String perEntAltera, Short status, String perAutoDesbloqueio, String perIpAcesso, String perDdnsAcesso, List<String> funcoes, AcessoSistema responsavel) throws UsuarioControllerException;

    public void removePerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void copyPerfil(String tipoEntidade, String codigoEntidade, String perOrigem, List<String> perDestino, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstPerfil(String tipoEntidade, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstPerfilSemBloqueioRepasse(String tipoEntidade, String codigoEntidade, String usuCodigoEdt, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<String> getFuncaoPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public Short getStatusPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstFuncoesPermitidasUsuario(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstFuncoesPermitidasPerfil(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    // Bloqueio de funções por usuário e serviço
    public List<TransferObject> lstFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, String> getMapFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstFuncoesBloqueaveis(String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> selectFuncoesBloqueadas(String usuario, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> selectFuncoesBloqueadas(String usuario, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public void insereBloqueiosFuncoes(String usuCodigo, List<TransferObject> bloqueios, AcessoSistema responsavel) throws UsuarioControllerException;

    public boolean usuarioTemBloqueioFuncao(String usuCodigo, String funCodigo, String svcCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstFuncaoExigeTmo(String funExigeTmo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> selectFuncoesPermitidasNca(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> selectFuncoesSensiveisCsa(AcessoSistema responsavel) throws UsuarioControllerException;

    // Senha Servidor
    public TransferObject getSenhaServidor(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhaAutorizacaoOtp(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhaAutorizacaoOtp(String rseCodigo, String modoEntrega, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, boolean naoComunicarServidor, boolean validaQtdeSenhaAutorizacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhaAutorizacaoRest(AcessoSistema responsavel) throws UsuarioControllerException;

    public void validaQtdeSenhaAutorizacao(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alterarSenhaAutorizacao(String usuCodigo, String senhaNova, AcessoSistema responsavel) throws UsuarioControllerException;

    public void consomeSenhaAutorizacao(String usuCodigo, String senhaUtilizada, AcessoSistema responsavel) throws UsuarioControllerException;

    public void cancelaSenhaAutorizacao(String usuCodigo, String senhaNaoUtilizada, AcessoSistema responsavel) throws UsuarioControllerException;

    public void cancelaSenhaAutorizacaoRest(Date dataCriacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alterarOperacoesSenhaAutorizacao(String usuCodigo, Short qtdOperacoes, String senhaUtilizada, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstSenhaAutorizacaoServidor(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstSenhaAutorizacaoServidorRest(AcessoSistema responsavel) throws UsuarioControllerException;

    public int qtdeSenhaAutorizacaoUsuSerDiaHostAHost(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject obtemSenhaAutorizacaoServidor(String usuCodigo, String sasSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    // Ocorrência usuário
    public String createOcorrenciaUsuario(CustomTransferObject ocorrencia, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstOcorrenciaUsuario(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public int countOcorrenciaUsuario(CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException;

    public String isOrg(String usuCodigo) throws UsuarioControllerException;

    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, CustomTransferObject tipoMotivoOperacao, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alterarSenhaApp(String usuCodigo, String senhaNova, boolean senhaCriptografada, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alterarSenhaApp(String usuCodigo, String senhaNova, boolean senhaCriptografada, boolean chamaMobile, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alteraChaveRecupSenha(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alteraChaveRecupSenhaAutoDesbloqueio(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaLinkReinicializarSenhaSer(String usuCodigo, String matricula, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaLinkReinicializarSenhaSerAutoDesbloqueio(String usuCodigo, String matricula, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaLinkReinicializarSenhaUsuAutoDesbloqueio(String usuLogin, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaLinkReinicializarSenhaUsu(String usuCodigo, String login, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createProtocoloSenhaAutorizacao(String psaCodigo, String usuCodigoAfetado, AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject getProtocoloSenhaAutorizacao(String psaCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listarSenhasExpiradasCancelamentoAut(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstStatusLogin(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstPapel(AcessoSistema responsavel) throws UsuarioControllerException;

    public void alteraDataUltimoAcessoSistema(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listarBloqueiaUsuariosInativos(String usuCodigo, java.util.Date dataLimiteBloqueio, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloqueiaUsuarioCsaComCPFServidor(String serCpf, AcessoSistema responsavel) throws UsuarioControllerException;

    public void validaIpAcessoResponsavel(String tipo, String usuLogin, String codigoEntidade, String ipList, String ddnsList, AcessoSistema responsavel) throws UsuarioControllerException;

    public Short getPerfilStatus(String perCodigo, String tipo, String entCodigo, AcessoSistema responsavel) throws FindException, UsuarioControllerException;

    public List<TransferObject> lstUsuarioCriadoRecursivoPorResponsavel(List<String> usuCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstUsuarioCriadoPorResponsavel(List<String> usuCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloquearDesbloquearUsuario(String usu_codigo, String status, String tipo, String tmoCodigo, String ousObs, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloquearDesbloquearUsuario(List<TransferObject> usuarios, String status, String tmoCodigo, String ousObs, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloquearDesbloquearUsuario(String usu_codigo, String status, String tipo, String tmoCodigo, String ousObs, AcessoSistema responsavel, Boolean validarCpf) throws UsuarioControllerException;

    public void bloquearUsuarioMotivoSeguranca(String usuCodigo, String entidadeOperada, String operacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public Funcao findFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String createFuncao(String grfCodigo, String funDescricao, String funPermiteBloqueio, String funExigeTmo, String funExigeSegundaSenhaCse, String funExigeSegundaSenhaSup, String funExigeSegundaSenhaOrg, String funExigeSegundaSenhaCsa, String funExigeSegundaSenhaCor, String funExigeSegundaSenhaSer, String funAuditavel, List<String> papCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public void updateFuncao(TransferObject funcaoTO, List<String> papCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public void removeFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public int countFuncao(TransferObject criterio, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listFuncao(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstFuncoesAuditaveis(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public void updateFuncoesAuditaveis(List<String> funcoes, String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, List<TransferObject>> lstUsuarioAuditorEntidade(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<UsuarioTransferObject> lstUsuariosAuditores(String tipo, String codigoEntidade, Object stuCodigo, int offset, int size, AcessoSistema responsavel) throws UsuarioControllerException;

    public int countUsuariosAuditores(String tipo, String codigoEntidade, Object stuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public boolean podeRemoverFuncAuditoria(String usuCodigo, String codigoEntidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException;

    public void ativarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> findFuncaoAuditavelPorEntidade(String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloqueiaUsuariosFimVigencia(AcessoSistema responsavel) throws UsuarioControllerException;

    public String cadastrarChaveValidacaoTotp(String usuChaveValidacaoTotp, String usuOperacoesValidacaoTotp, AcessoSistema responsavel) throws UsuarioControllerException;

    public void removerChaveValidacaoTotp(AcessoSistema responsavel) throws UsuarioControllerException;

    public void updateEnderecoAcessoFuncao(CustomTransferObject dadosTo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> findUsuarioCseList(AcessoSistema responsavel) throws UsuarioControllerException;

    public String gerarChaveSessaoUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void cadastroDeviceToken(String usuCodigo, String tdiCodigo, String deviceToken, AcessoSistema responsavel) throws UsuarioControllerException;

    public String findDeviceToken(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public UsuarioChaveSessao findUsuarioChaveSessao(String token) throws FindException;

    public void deleteUsuarioChaveSessao(String usuCodigo) throws UsuarioControllerException;

    public UsuarioChaveSessao validateToken(String token) throws UsuarioControllerException;

    public void insereAlteraImagemUsuario(String usuCodigo, byte[] imagem, AcessoSistema responsavel) throws UsuarioControllerException;

    public Map<String, Object> primeiroAcesso(String cpf, String orgCodigo, String otp, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaOTPServidor(String usuCodigo, String chaveSenha, String emailOpcional, String telefoneOpcional, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviaOTPServidor(String usuCodigo, String chaveSenha, String emailOpcional, String telefoneOpcional, boolean validarChaveSenha, boolean fluxoAutoDesbloqueio, boolean enviaOtpEmail, boolean enviaOtpCelular, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> validaOTPServidor(String usuCodigo, String chaveSenha, boolean senhaCriptografada, String otp, String emailOpcional, String telefoneOpcional, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException;

    public String validarOTPPortal(TransferObject usuario, String otp, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviarOtpServidorPorEmailOuCelular(String usuCodigo, boolean enviarOtpEmail, boolean enviarOtpCelular, AcessoSistema responsavel) throws UsuarioControllerException;
    public void validarOtpServidorEnviadoPorEmailOuCelular(String usuCodigo, String otp, AcessoSistema responsavel) throws UsuarioControllerException;

    public void recuperarSenha(String usuCodigo, String tipoEntidade, String chaveRecuperarSenha, String senhaNova, String dicaSenha, boolean autoDesbloqueio, AcessoSistema responsavel) throws UsuarioControllerException;

    public void recuperarSenha(Set<String> usuCodigos, String tipoEntidade, String chaveRecuperarSenha, String senhaNova, String dicaSenha, boolean autoDesbloqueio, AcessoSistema responsavel) throws UsuarioControllerException;

    public void recuperarSenha(String cpf, String login, List<String> orgCodigos, String otp, String senhaNova, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException;

    public UsuarioTransferObject recuperarSenha(String id, String login, List<String> orgCodigos, String otp, String senhaNova, boolean senhaApp, boolean isAcessoMobile, AcessoSistema responsavel) throws UsuarioControllerException;

    public Collection<ArquivoUsuario> findArquivoUsuario(String usuCodigo, String tarCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject buscarUsuarioPorCodRecuperarSenha(String codRecuperar, AcessoSistema responsavel) throws UsuarioControllerException;

    public void atualizarUsuarioAutorizacaoEmailMarketing(UsuarioTransferObject usuario, String usuAutoriza, AcessoSistema responsavel) throws UsuarioControllerException;

    // NOME DE USUÁRIO (PARA LOGIN EM DUAS ETAPAS)
    public int countNomeUsuario(AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject obtemNomeUsuario(String login, String usuCodigo, int offset, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> enviaNotificacaoUsuariosPorTempoInatividade(AcessoSistema responsavel) throws UsuarioControllerException;

    public void limparDadosOTP(TransferObject usuario, AcessoSistema responsavel) throws UsuarioControllerException;

    public void enviarCodigoAutorizacaoSms(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException, UsuarioControllerException;

    public boolean validarCodigoAutorizacaoSms(String codAut, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<Papel> listarPapeis(AcessoSistema responsavel) throws UsuarioControllerException;

    public void gerarOtpConfirmacaoEmail(Servidor servidor, String emailValidacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public boolean validarOtpConfirmacaoEmail(String otp, String serCpf, AcessoSistema responsavel) throws UsuarioControllerException;

    public void alteraChaveValidacaoEmail(UsuarioTransferObject dadosUsuario, String link, AcessoSistema responsavel) throws UsuarioControllerException;

    public TransferObject buscarUsuarioPorCodValidaEmail(String codValidacao, AcessoSistema responsavel) throws UsuarioControllerException;

    public int countOcorrenciaPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> lstOcorrenciaPerfil(String perCodigo, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException;

    public String[] matriculasUsuariosServidores(String usuCodigo, String senhaNovaCrypt, boolean alteraSenha, AcessoSistema responsavel) throws UsuarioControllerException;

    public void bloqueiaPerfilDataExpiracao(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<String> unidadesPermissaoEdtUsuario (String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public void atribuirUnidadesUsuario(String usuCodigo, List<String> unidades, AcessoSistema responsavel) throws UsuarioControllerException;

    public void fixarCamposPesquisaAvancada(List<CampoUsuario> lstCampoUsuario, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<CampoUsuario> buscarCamposPesquisaAvancada(AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> listarUsuariosFuncaoEspecifica(String funCodigo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException;

    public List<TransferObject> findFuncoesRegraTaxa(String funCodigos, AcessoSistema responsavel) throws UsuarioControllerException;

    public boolean findEmailExistenteCsaCseOrgCor(String emailUsuario, String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public String consultarEmailServidor(boolean enviaEmail, String serCpf, String serEmail, String modoEntrega, AcessoSistema responsavel) throws UsuarioControllerException;

    public void removeRestricaoUsuarioPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException;

    public Perfil findPerfilByUsuCodigo(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException;
    
    public void enviarNotificacaoPrazoExpiracaoSenha(String usuNome, String usuEmail, Integer qtdeDiasExpiracaoSenha, AcessoSistema responsavel);

    public boolean usuarioPossuiPermissaoAutoDesbloqueio(TransferObject usuarioTO, String tipoEntidade, AcessoSistema responsavel);
}