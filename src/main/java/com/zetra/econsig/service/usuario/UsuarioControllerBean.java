package com.zetra.econsig.service.usuario;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidor;
import com.zetra.econsig.helper.emailexterno.ConsultarEmailExternoServidorFactory;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.senhaexterna.SenhaExterna;
import com.zetra.econsig.helper.sms.EnviaSMSHelper;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorListTO;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.*;
import com.zetra.econsig.persistence.query.funcao.FuncoesPerfilQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesPerfilRestricaoAcessoQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesPersonalizadasQuery;
import com.zetra.econsig.persistence.query.funcao.FuncoesPersonalizadasRestricaoAcessoQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncaoPerfilTodasEntidadesQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesAuditadasQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesAuditaveisPapelQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesBloqueadasQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesBloqueaveisQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPerfilQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPermitidasNcaQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPermitidasPapelQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesRegraTaxaQuery;
import com.zetra.econsig.persistence.query.funcao.ListaFuncoesSensiveisCsaQuery;
import com.zetra.econsig.persistence.query.funcao.ListaPapeisQuery;
import com.zetra.econsig.persistence.query.funcao.ListaPerfilTodasEntidadesPossuemFuncaoQuery;
import com.zetra.econsig.persistence.query.funcao.ListarFuncaoQuery;
import com.zetra.econsig.persistence.query.funcao.ObtemFuncaoQuery;
import com.zetra.econsig.persistence.query.perfil.ListaOcorrenciaPerfilQuery;
import com.zetra.econsig.persistence.query.perfil.ListaPerfilSemBloqueioRepasseQuery;
import com.zetra.econsig.persistence.query.perfil.ListaPerfilTipoEntidadeQuery;
import com.zetra.econsig.persistence.query.senha.ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery;
import com.zetra.econsig.persistence.query.senha.ListaSenhaAutorizacaoServidorExpiradaQuery;
import com.zetra.econsig.persistence.query.senha.ListaSenhaAutorizacaoServidorQuery;
import com.zetra.econsig.persistence.query.senha.ListaSenhasAntigasUsuarioQuery;
import com.zetra.econsig.persistence.query.senha.ListaUsuarioSerSenhaAutExpiradaQuery;
import com.zetra.econsig.persistence.query.senha.ObtemProtocoloSenhaAutorizacaoQuery;
import com.zetra.econsig.persistence.query.senha.ObtemSenhaServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidoresQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemTotalServidoresPorEmailCelularQuery;
import com.zetra.econsig.persistence.query.usuario.FindEmailUsuarioRepeatQuery;
import com.zetra.econsig.persistence.query.usuario.ListaFuncoesUsuarioQuery;
import com.zetra.econsig.persistence.query.usuario.ListaOcorrenciaUsuarioQuery;
import com.zetra.econsig.persistence.query.usuario.ListaStatusLoginQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioAtivoComEmailQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioAuditorEntidadeQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioCriadoPorResponsavelQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioFimVigenciaQuery;
import com.zetra.econsig.persistence.query.funcao.*;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioInativoQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioNotificacaoInatividadeQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuarioServidorNovaSenhaQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosAuditoresQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosComNovaSenhaQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosEntidadeQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosFuncaoEspecificaQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosSerQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosSerRseQuery;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosServidorLoginQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemNomeUsuarioQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemPapelUsuarioQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemTotalUsuariosPorEmailQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCsaCorQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCseOrgQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioCseQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioServidorQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioSupQuery;
import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioTipoQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCorPodeModificarPerfilQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCorPodeModificarUsuQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsaPodeModificarPerfilQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsaPodeModificarUsuQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioCsePodeModificarUsuQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioEstPodeModificarUsuQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioOrgPodeModificarPerfilQuery;
import com.zetra.econsig.persistence.query.usuario.UsuarioOrgPodeModificarUsuQuery;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;
import com.zetra.econsig.values.ParamEmailExternoServidorEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.webclient.sso.SSOClient;

/**
 * <p>Title: UsuarioControllerBean</p>
 * <p>Description: Session Façade para manipulação de usuário</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $
 * $
 * $
 */
@Service
@Transactional
public class UsuarioControllerBean implements UsuarioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioControllerBean.class);

    // TODO Criar uma constante única para todo o código
    private static final String MENSAGEM_ERRO_INTERNO_SISTEMA = "mensagem.erroInternoSistema";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private CorrespondenteController correspondenteController;

    @Lazy(true)
    @Autowired
    private SSOClient ssoClient;

    // Usuario
    @Override
    public UsuarioTransferObject findUsuario(UsuarioTransferObject usuario, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        return setUsuarioValues(findUsuarioBean(usuario, tipo));
    }

    @Override
    public UsuarioTransferObject findUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        return findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);
    }

    @Override
    public UsuarioTransferObject findUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        return findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
    }

    @Override
    public UsuarioTransferObject findUsuarioByLogin(String login, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject();
        usuario.setUsuLogin(login);
        return findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);
    }

    @Override
    public String findUsuarioPerfil(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        // Remove o perfil do usuário
        try {
            final PerfilUsuario upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);
            return upeBean.getPerfil().getPerCodigo();
        } catch (final FindException e) {
            return null;
        }
    }

    @Override
    public CustomTransferObject findTipoUsuarioByLogin(String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException {
        CustomTransferObject usuario = null;
        try {
            final ObtemUsuarioQuery query = new ObtemUsuarioQuery();
            query.usuLogin = usuLogin;
            final List<TransferObject> list = query.executarDTO();
            if (list.size() > 0) {
                usuario = (CustomTransferObject) list.get(0);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
        return usuario;
    }

    @Override
    public CustomTransferObject findTipoUsuarioByCodigo(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        CustomTransferObject usuario = null;
        try {
            final ObtemUsuarioQuery query = new ObtemUsuarioQuery();
            query.usuCodigo = usuCodigo;
            final List<TransferObject> list = query.executarDTO();
            if (list.size() > 0) {
                usuario = (CustomTransferObject) list.get(0);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
        return usuario;
    }

    @Override
    public List<TransferObject> findUsuarioCseList(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioCseQuery query = new ObtemUsuarioCseQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public Usuario findUsuarioByEmailAndToken(String email, String token, AcessoSistema responsavel) throws UsuarioControllerException {
        List<Usuario> usuarioBean = null;

        try {
            usuarioBean = UsuarioHome.findByEmailAndToken(email, token);

        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", (AcessoSistema) null);
        }

        if (usuarioBean == null) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", (AcessoSistema) null);
        }

        if (usuarioBean.size() > 1) {
            throw new UsuarioControllerException("mensagem.erro.usuario.multiplo", (AcessoSistema) null);
        }

        return usuarioBean.get(0);
    }

    @Override
    public List<TransferObject> findUsuarioByEmail(String email, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioQuery query = new ObtemUsuarioQuery();
            query.usuEmail = email;
            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public List<UsuarioTransferObject> lstUsuariosSerByEmail(String email, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final List<UsuarioTransferObject> retorno = new ArrayList<>();

            final List<Usuario> usuarios = UsuarioHome.findByEmail(email);
            if ((usuarios != null) && !usuarios.isEmpty()) {
                for (final Usuario usuario : usuarios) {
                    try {
                        // Verifica se é usuário servidor
                        findUsuarioSer(usuario.getUsuCodigo(), responsavel);

                        if (!TextHelper.isNull(usuario.getUsuCpf())) {
                            retorno.add(setUsuarioValues(usuario));
                        }
                    } catch (final UsuarioControllerException e) {
                        // Se não é usuário servidor, não inclui na lista que será retornada
                    }
                }
            }

            return retorno;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    private Usuario findUsuarioBean(UsuarioTransferObject usuario, String tipo) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();

        if (!AcessoSistema.ENTIDADE_USU.equals(tipo) && (usuario.getUsuCodigo() == null)) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", (AcessoSistema) null);
        }

        Usuario usuarioBean = null;
        try {
            if (AcessoSistema.ENTIDADE_USU.equals(tipo)) {
                if (usuario.getUsuCodigo() != null) {
                    usuarioBean = UsuarioHome.findByPrimaryKey(usuario.getUsuCodigo());
                } else if (usuario.getUsuLogin() != null) {
                    usuarioBean = UsuarioHome.findByLogin(usuario.getUsuLogin());
                }
            } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuCse(usuario.getUsuCodigo());
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuCsa(usuario.getUsuCodigo());
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuCor(usuario.getUsuCodigo());
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuOrg(usuario.getUsuCodigo());
            } else if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuSer(usuario.getUsuCodigo());
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                usuarioBean = UsuarioHome.findUsuSup(usuario.getUsuCodigo());
            }
        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", (AcessoSistema) null, e);
        }
        if (usuarioBean == null) {
            // Se não deu FindException, significa que o método foi chamado com ENTIDADE_USU
            // e não foi passado usuCodigo nem usuLogin. Chama o dumpStack para identificarmos
            // o ponto que está chamando o método com parâmetros inválidos.
            Thread.dumpStack();
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", (AcessoSistema) null);
        }

        return usuarioBean;
    }

    private Collection<?> findFuncaoPerfilBean(String usuCodigo, String tipo) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();

        try {
            Collection<?> funcoes = null;

            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                funcoes = FuncaoPerfilCseHome.findByUsuCodigo(usuCodigo);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                funcoes = FuncaoPerfilCsaHome.findByUsuCodigo(usuCodigo);
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                funcoes = FuncaoPerfilCorHome.findByUsuCodigo(usuCodigo);
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                funcoes = FuncaoPerfilOrgHome.findByUsuCodigo(usuCodigo);
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                funcoes = FuncaoPerfilSupHome.findByUsuCodigo(usuCodigo);
            }
            return funcoes;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.perfil.usuario.nao.encontrado", (AcessoSistema) null);
        }
    }

    /**
     * Recupera arquivos de um usuário na base
     * @param usuCodigo - código do usuário
     * @param tarCodigo - tipo de arquivo de usuário que se deseja buscar (opcional)
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public Collection<ArquivoUsuario> findArquivoUsuario(String usuCodigo, String tarCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            return ArquivoUsuarioHome.findByUsuCodigoTipoArquivo(usuCodigo, tarCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.arquivo.nao.encontrado", (AcessoSistema) null);
        }
    }

    private UsuarioTransferObject setUsuarioValues(Usuario usuarioBean) {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuarioBean.getUsuCodigo());
        if (usuarioBean.getUsuDataCad() != null) {
            usuario.setUsuDataCad(DateHelper.toSQLDate(usuarioBean.getUsuDataCad()));
        }
        usuario.setStuCodigo(usuarioBean.getStatusLogin().getStuCodigo());
        usuario.setUsuLogin(usuarioBean.getUsuLogin());
        usuario.setUsuSenha(usuarioBean.getUsuSenha());
        usuario.setUsuSenha2(usuarioBean.getUsuSenha2());
        usuario.setUsuSenhaApp(usuarioBean.getUsuSenhaApp());
        usuario.setUsuNome(usuarioBean.getUsuNome());
        usuario.setUsuEmail(usuarioBean.getUsuEmail());
        usuario.setUsuTel(usuarioBean.getUsuTel());
        usuario.setUsuDicaSenha(usuarioBean.getUsuDicaSenha());
        usuario.setUsuTipoBloq(usuarioBean.getUsuTipoBloq());
        if (usuarioBean.getUsuDataExpSenha() != null) {
            usuario.setUsuDataExpSenha(DateHelper.toSQLDate(usuarioBean.getUsuDataExpSenha()));
        }
        if (usuarioBean.getUsuDataExpSenha2() != null) {
            usuario.setUsuDataExpSenha2(DateHelper.toSQLDate(usuarioBean.getUsuDataExpSenha2()));
        }
        if (usuarioBean.getUsuDataUltAcesso() != null) {
            usuario.setUsuDataUltAcesso(DateHelper.toSQLDate(usuarioBean.getUsuDataUltAcesso()));
        }
        usuario.setUsuIpAcesso(usuarioBean.getUsuIpAcesso());
        usuario.setUsuDDNSAcesso(usuarioBean.getUsuDdnsAcesso());
        usuario.setUsuCPF(usuarioBean.getUsuCpf());
        usuario.setUsuCentralizador(usuarioBean.getUsuCentralizador() != null ? usuarioBean.getUsuCentralizador().toString() : null);
        usuario.setUsuVisivel(usuarioBean.getUsuVisivel() != null ? usuarioBean.getUsuVisivel().toString() : null);
        usuario.setUsuExigeCertificado(usuarioBean.getUsuExigeCertificado() != null ? usuarioBean.getUsuExigeCertificado().toString() : null);
        usuario.setUsuMatriculaInst(usuarioBean.getUsuMatriculaInst() != null ? usuarioBean.getUsuMatriculaInst().toString() : null);
        usuario.setUsuChaveRecuperarSenha(usuarioBean.getUsuChaveRecuperarSenha() != null ? usuarioBean.getUsuChaveRecuperarSenha().toString() : null);
        usuario.setUsuNovaSenha(usuarioBean.getUsuNovaSenha() != null ? usuarioBean.getUsuNovaSenha().toString() : null);
        usuario.setUsuDataFimVig(usuarioBean.getUsuDataFimVig() != null ? new Date(usuarioBean.getUsuDataFimVig().getTime()) : null);
        usuario.setUsuDeficienteVisual(usuarioBean.getUsuDeficienteVisual() != null ? usuarioBean.getUsuDeficienteVisual().toString() : null);
        usuario.setUsuDataRecSenha(usuarioBean.getUsuDataRecSenha() != null ? new Date(usuarioBean.getUsuDataRecSenha().getTime()) : null);
        usuario.setUsuChaveValidacaoTotp(usuarioBean.getUsuChaveValidacaoTotp());
        usuario.setUsuPermiteValidacaoTotp(usuarioBean.getUsuPermiteValidacaoTotp());
        usuario.setUsuOperacoesValidacaoTotp(usuarioBean.getUsuOperacoesValidacaoTotp());
        usuario.setUsuOperacoesSenha2(usuarioBean.getUsuOperacoesSenha2());
        usuario.setUsuOtpCodigo(usuarioBean.getUsuOtpCodigo());
        usuario.setUsuOtpChaveSeguranca(usuarioBean.getUsuOtpChaveSeguranca());
        usuario.setUsuOtpDataCadastro(usuarioBean.getUsuOtpDataCadastro());
        usuario.setUsuQtdConsultasMargem(usuarioBean.getUsuQtdConsultasMargem());
        usuario.setUsuAutentiaSso(usuarioBean.getUsuAutenticaSso());
        usuario.setUsuAutorizaEmailMarketing(usuarioBean.getUsuAutorizaEmailMarketing());
        return usuario;
    }

    @Override
    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return createUsuario(usuario, funcoes, null, codigoEntidade, tipo, null, true, senhaAberta, true, responsavel);
    }

    @Override
    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return createUsuario(usuario, funcoes, null, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, true, responsavel);
    }

    @Override
    public String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        return createUsuario(usuario, funcoes, null, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, validaForcaSenha, responsavel);
    }

    @Override
    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, String senhaAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        return createUsuario(usuario, null, perCodigo, codigoEntidade, tipo, null, true, senhaAberta, true, responsavel);
    }

    @Override
    public String createUsuario(UsuarioTransferObject usuario, String perCodigo, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        return createUsuario(usuario, null, perCodigo, codigoEntidade, tipo, tipoMotivoOperacao, validaCpfEmail, senhaAberta, validaForcaSenha, responsavel);
    }

    private String createUsuario(UsuarioTransferObject usuario, List<String> funcoes, String perCodigo, String codigoEntidade, String tipo, TransferObject tipoMotivoOperacao, boolean validaCpfEmail, String senhaAberta, boolean validaForcaSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        String usuCodigo = null;
        try {
            // Verifica se não existe outro usuário com o mesmo login
            final UsuarioTransferObject teste = new UsuarioTransferObject();
            teste.setUsuLogin((String) usuario.getAttribute(Columns.USU_LOGIN));

            boolean existe = false;
            try {
                findUsuarioBean(teste, AcessoSistema.ENTIDADE_USU);
                existe = true;
            } catch (final UsuarioControllerException ex) {
                // OK, nenhum usuário encontrato com o login informado
            }

            if (existe) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.este.usuario.existe.outro.mesmo.login.cadastrado.sistema", responsavel);
            }

            boolean autenticaSSO = false;

            if (responsavel.isCsa() && responsavel.getCanal() == CanalEnum.WEB) {

                boolean campoAutenticaSsoTrue = (!TextHelper.isNull(usuario.getUsuAutenticaSso()) && usuario.getUsuAutenticaSso().equals("S"));
                // Verifica se como CSA o campo autentica via SSO é verdadeiro
                autenticaSSO = campoAutenticaSsoTrue;

            } else {
                // se o parâmetro de sistema do papel autentica no SSO
                autenticaSSO = validarDadosSSO(usuario, codigoEntidade, tipo, responsavel);
            }

            
            
            boolean enviaEmailInicializacaoSenha = false;

            // Na criação do usuário MASTER realizado durante a criação da CSA, não valido CPF ou email
            if (validaCpfEmail) {

                validaCpfUsuario(usuario.getUsuCPF(), tipo, responsavel);
                validaUnicidadeCpf(usuario.getUsuCodigo(), usuario.getUsuCPF(), tipo, codigoEntidade, responsavel);

                enviaEmailInicializacaoSenha = verificarSeEnviaEmailInicializacaoSenha(autenticaSSO, tipo, usuario, responsavel);

            }

            if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                final boolean usuTelObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_SUP, CodedValues.TPC_SIM, responsavel);
                if (usuTelObrigatorio && TextHelper.isNull(usuario.getUsuTel())) {
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.telefone.usuario.deve.ser.informado", responsavel);
                }
            }
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                final boolean usuTelObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSE, CodedValues.TPC_SIM, responsavel);
                if (usuTelObrigatorio && TextHelper.isNull(usuario.getUsuTel())) {
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.telefone.usuario.deve.ser.informado", responsavel);
                }
            }
            
            // Cria a entidade na tb_usuario
            final String usuExigeCertificado = !TextHelper.isNull(usuario.getUsuExigeCertificado()) ? usuario.getUsuExigeCertificado().substring(0, 1) : null;
            final String usuCentralizador = !TextHelper.isNull(usuario.getUsuCentralizador()) ? usuario.getUsuCentralizador().substring(0, 1) : null;
            final String usuVisivel = !TextHelper.isNull(usuario.getUsuVisivel()) ? usuario.getUsuVisivel().substring(0, 1) : null;

            if (validaForcaSenha) {
                SenhaHelper.validarForcaSenha(senhaAberta, AcessoSistema.ENTIDADE_SER.equals(tipo), responsavel);
            }

            final Usuario usuarioBean = UsuarioHome.create(usuario.getStuCodigo(), usuario.getUsuLogin(), usuario.getUsuSenha(), usuario.getUsuSenha2(), usuario.getUsuNome(), usuario.getUsuEmail(), usuario.getUsuTel(), usuario.getUsuDicaSenha(), usuario.getUsuTipoBloq(), usuario.getUsuDataExpSenha(), usuario.getUsuDataExpSenha2(), usuario.getUsuIpAcesso(), usuario.getUsuDDNSAcesso(), usuario.getUsuCPF(), usuCentralizador, usuVisivel, usuExigeCertificado, usuario.getUsuMatriculaInst(), usuario.getUsuChaveRecuperarSenha(), usuario.getUsuDataFimVig(), usuario.getUsuDeficienteVisual(),
                                                           usuario.getUsuChaveValidacaoTotp(), usuario.getUsuPermiteValidacaoTotp(), usuario.getUsuQtdConsultasMargem(), usuario.getUsuAutenticaSso());
            usuCodigo = usuarioBean.getUsuCodigo();

            // Cria a entidade na tb_usuario_xxx
            createUsuarioEntidade(usuCodigo, codigoEntidade, tipo);

            // Verifica se o responsável tem permissão para criar este usuário
            usuarioPodeModificarUsu(usuCodigo, false, true, responsavel);

            // Cria o perfil do usuário
            updateUsuarioPerfil(funcoes, perCodigo, usuCodigo, codigoEntidade, tipo, false, responsavel);

            // Recupera as funções que pertencem ao papel do novo usuário e
            // que estão bloqueadas para o usuário responsável, que serão bloqueadas também para o novo usuário
            final List<String> codSvcEntidade = retornaCodigoServicoEntidade(codigoEntidade, tipo, responsavel);
            final List<TransferObject> lista = selectFuncoesBloqueadas(responsavel.getUsuCodigo(), tipo, responsavel);
            if (!lista.isEmpty()) {
                final List<TransferObject> funcoesBloqueadas = new ArrayList<>();
                for (final TransferObject to : lista) {
                    // Se não retornou uma lista de serviços nula,
                    // é porque a entidade possui convênio com todos os serviços.
                    // Se retornou serviços, devem ser validados os serviços que pertencem ao convênio da entidade.
                    if ((codSvcEntidade == null) || ((codSvcEntidade != null) && !codSvcEntidade.isEmpty() && codSvcEntidade.contains(to.getAttribute(Columns.BUF_SVC_CODIGO).toString()))) {
                        to.setAttribute(Columns.BUF_USU_CODIGO, usuCodigo);
                        funcoesBloqueadas.add(to);
                    }
                }
                // Insere bloqueio de funções para o novo usuário
                insereBloqueiosFuncoes(usuCodigo, funcoesBloqueadas, responsavel);
            }

            // Cria ocorrência de inclusão de usuário
            final CustomTransferObject ocorrencia = new CustomTransferObject();
            ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
            ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_INCLUSAO_USUARIO);
            ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
            ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.usuario", responsavel));
            ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());
            if (tipoMotivoOperacao != null) {
                ocorrencia.setAttribute(Columns.OUS_OBS, ocorrencia.getAttribute(Columns.OUS_OBS) + (ocorrencia.getAttribute(Columns.OUS_OBS).toString().lastIndexOf(".") == (ocorrencia.getAttribute(Columns.OUS_OBS).toString().length() - 1) ? " " : ". ") + tipoMotivoOperacao.getAttribute(Columns.OUS_OBS));
                ocorrencia.setAttribute(Columns.OUS_TMO_CODIGO, tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO));
            }

            createOcorrenciaUsuario(ocorrencia, responsavel);

            if (enviaEmailInicializacaoSenha) {
                final String linkReinicializacao = usuario.getLinkRecuperarSenha();

                if (TextHelper.isNull(linkReinicializacao)) {
                    throw new UsuarioControllerException("mensagem.erro.usuario.link.senha.ausente", responsavel);
                }

                // Gera uma nova codigo de recuparação de senha
                final String cod_Senha = SynchronizerToken.generateToken();
                // Atualiza o codigo de recuperação de senha do usuário
                alteraChaveRecupSenha(usuCodigo, cod_Senha, responsavel);
                // Envia e-mail com link para recuperação de senha
                enviaLinkIniciacaoSenhaNovoUsuario(usuCodigo, usuario.getUsuEmail(), (String) usuario.getAttribute(Columns.USU_LOGIN), usuario.getUsuNome(), linkReinicializacao, cod_Senha, responsavel);
                // Limpa o atributo para não ser gravado no log
                usuario.setLinkRecuperarSenha(null);
            }

            if (autenticaSSO) {
                // Irá verificar se o sso está habilitado e cria o usuário no sso
                ssoClient.addUsuarioSSO(usuario, senhaAberta, tipo, codigoEntidade, responsavel);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setUsuario(usuCodigo);
            log.setStatusLogin(usuario.getStuCodigo());
            log.getUpdatedFields(usuario.getAtributos(), null);

            if (!TextHelper.isNull(tipo)) {
                final List<String> codigosEntidade = new ArrayList<>();
                codigosEntidade.add(codigoEntidade);

                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.tipo.arg0", responsavel, tipo + " "));
                if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    log.add(Columns.CSE_NOME, codigosEntidade, ConsignanteHome.class);
                } else if (AcessoSistema.ENTIDADE_EST.equals(tipo)) {
                    log.add(Columns.EST_NOME, codigosEntidade, EstabelecimentoHome.class);
                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    log.add(Columns.ORG_NOME, codigosEntidade, OrgaoHome.class);
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    log.add(Columns.CSA_NOME, codigosEntidade, ConsignatariaHome.class);
                } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    log.add(Columns.COR_NOME, codigosEntidade, CorrespondenteHome.class);
                } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                    log.add(Columns.CSE_NOME, codigosEntidade, ConsignanteHome.class);
                }
            }

            if ((perCodigo != null) && !"".equals(perCodigo)) {
                log.setPerfil(perCodigo);
            } else {
                log.add(Columns.FUN_CODIGO, funcoes, FuncaoHome.class);
            }
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.usuario.erro.interno.arg0", responsavel, ex.getMessage());
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(e);
        }
        return usuCodigo;
    }

    private boolean verificarSeEnviaEmailInicializacaoSenha(boolean autenticaSSO, 
            String tipo,
            UsuarioTransferObject usuario,
            AcessoSistema responsavel) throws UsuarioControllerException {
        
        //DESENV-10463: Verifica se sistema envia e-mail de inicialização de senha na criação do usuário do tipo em questão
        boolean enviaEmailInicializacaoSenha = ((AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSE_ORG, CodedValues.TPC_SIM, responsavel)) || ((AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR, CodedValues.TPC_SIM, responsavel)) ||
                                        (AcessoSistema.ENTIDADE_SUP.equals(tipo) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_SUP, CodedValues.TPC_SIM, responsavel));

        if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
            final boolean usuEmailObrigatorio = enviaEmailInicializacaoSenha || ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMAIL_OBRIGATORIO_USUARIO_CSA, responsavel);
            if (usuEmailObrigatorio && TextHelper.isNull(usuario.getUsuEmail())) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.email.usuario.deve.ser.informado", responsavel);
            }
        } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            final boolean usuEmailObrigatorio = enviaEmailInicializacaoSenha || ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMAIL_OBRIGATORIO_CSE_ORG_SUP, responsavel);
            if (usuEmailObrigatorio && TextHelper.isNull(usuario.getUsuEmail())) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.email.usuario.deve.ser.informado", responsavel);
            }
        }
        
        boolean ehCriacaoUsuarioViaAccessGateway = autenticaSSO && responsavel.getCanal() == CanalEnum.SOAP;

        if (ehCriacaoUsuarioViaAccessGateway) {
            enviaEmailInicializacaoSenha = false;
        }

        return enviaEmailInicializacaoSenha;

    }

    private boolean validarDadosSSO(UsuarioTransferObject usuario, String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException, ParametroControllerException {

        boolean realizaCadastroSSO = UsuarioHelper.usuarioAutenticaSso(usuario, tipoEntidade, responsavel);

        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            // Caso seja consignatária, valida se o parâmetro de criação de usuário autenticado no SSO foi habilitado para a CSA
            realizaCadastroSSO = false;
            final String criaUsuarioAutenticaoSSO = parametroController.getParamCsa(codigoEntidade, CodedValues.TPA_USUARIO_AUTENTICA_SSO, responsavel);

            if (!TextHelper.isNull(criaUsuarioAutenticaoSSO) && "S".equalsIgnoreCase(criaUsuarioAutenticaoSSO)) {
                realizaCadastroSSO = true;
            }
        } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            // Se for correspondente, o padrão é criar não autenticando no SSO
            realizaCadastroSSO = false;
        }

        if (realizaCadastroSSO) {
            // Validar se email cadastrado
            if (TextHelper.isNull(usuario.getUsuEmail())) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.email.usuario.deve.ser.informado", responsavel);
            }

            // Seta para autenticar no SSO
            usuario.setUsuAutentiaSso(CodedValues.TPC_SIM);
        }

        return realizaCadastroSSO;
    }

    /**
     * Retorna os códigos dos serviços disponíveis para a entidade informada.
     * Se retornou uma lista nula é porque a entidade é cse e possui convênio com todos os serviços.
     *
     * @param codigoEntidade Código da entidade informada.
     * @param tipo Tipo da entidade informada.
     * @param responsavel
     * @return Retorna uma lista com os códigos dos serviços disponíveis para a entidade informada. Se a lista nula é porque a entidade é cse e possui convênio com todos os serviços.
     * @throws UsuarioControllerException
     */
    private List<String> retornaCodigoServicoEntidade(String codigoEntidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();
        List<String> retorno = null;
        try {
            List<TransferObject> servicos = null;
            if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                servicos = servicoController.selectServicosCsa(codigoEntidade, responsavel);
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                servicos = servicoController.selectServicosCorrespondente(codigoEntidade, responsavel);
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                servicos = servicoController.selectServicosOrgao(codigoEntidade, responsavel);
            }

            if ((servicos != null) && !servicos.isEmpty()) {
                retorno = new ArrayList<>();
                for (final TransferObject servico : servicos) {
                    retorno.add(servico.getAttribute(Columns.SVC_CODIGO).toString());
                }
            }

        } catch (final ServicoControllerException ex) {
            throw new UsuarioControllerException(ex);
        }
        return retorno;
    }

    private void createUsuarioEntidade(String usuCodigo, String codigoEntidade, String tipo) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();
        try {
            final String stuCodigo = CodedValues.STU_ATIVO;

            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                UsuarioCseHome.create(codigoEntidade, usuCodigo, stuCodigo);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                UsuarioCsaHome.create(codigoEntidade, usuCodigo, stuCodigo);
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                UsuarioCorHome.create(codigoEntidade, usuCodigo, stuCodigo);
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                UsuarioOrgHome.create(codigoEntidade, usuCodigo, stuCodigo);
            } else if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                UsuarioSerHome.create(codigoEntidade, usuCodigo, stuCodigo);
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                UsuarioSupHome.create(codigoEntidade, usuCodigo, stuCodigo);
            }
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.usuario.erro.interno.arg0", (AcessoSistema) null, ex.getMessage());
        }
    }

    private void createUsuarioPerfil(List<String> funcoes, String usuCodigo, String codigoEntidade, String tipo) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();
        if (funcoes == null) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.usuario.erro.interno.arg0", (AcessoSistema) null, ApplicationResourcesHelper.getMessage("mensagem.informacao.lista.funcoes.perfil.usuario.nao.pode.ser.nula", (AcessoSistema) null));
        }

        try {
            final Iterator<String> it = funcoes.iterator();

            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                while (it.hasNext()) {
                    final String funCodigo = it.next().toString();
                    FuncaoPerfilCseHome.create(codigoEntidade, usuCodigo, funCodigo);
                }
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                while (it.hasNext()) {
                    final String funCodigo = it.next().toString();
                    FuncaoPerfilCsaHome.create(codigoEntidade, usuCodigo, funCodigo);
                }
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                while (it.hasNext()) {
                    final String funCodigo = it.next().toString();
                    FuncaoPerfilCorHome.create(codigoEntidade, usuCodigo, funCodigo);
                }
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                while (it.hasNext()) {
                    final String funCodigo = it.next().toString();
                    FuncaoPerfilOrgHome.create(codigoEntidade, usuCodigo, funCodigo);
                }
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                while (it.hasNext()) {
                    final String funCodigo = it.next().toString();
                    FuncaoPerfilSupHome.create(codigoEntidade, usuCodigo, funCodigo);
                }
            }
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.usuario.erro.interno.arg0", (AcessoSistema) null, ex.getMessage());
        }
    }

    private void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, AcessoSistema responsavel) throws UsuarioControllerException {
        alteraUsuario(usuario, ocorrenciaUsu, null, null, null, null, false, false, false, null, responsavel);
    }

    @Override
    public void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, List<String> funcoes, String perCodigo, String tipo, String codigoEntidade, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        alteraUsuario(usuario, ocorrenciaUsu, funcoes, perCodigo, tipo, codigoEntidade, true, true, true, tipoMotivoOperacao, responsavel);
    }

    private void updateUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, List<String> funcoes, String perCodigo, String tipo, String codigoEntidade, TransferObject tipoMotivoOperacao, AcessoSistema responsavel, Boolean validarCpf) throws UsuarioControllerException {
        alteraUsuario(usuario, ocorrenciaUsu, funcoes, perCodigo, tipo, codigoEntidade, validarCpf, true, true, tipoMotivoOperacao, responsavel);
    }

    /**
     * Altera os dados do usuário.
     * Se uma ocorrência for passada por parâmetro a mesma será salva, caso contrário será incluída uma ocorrência de alteração de usuário.
     * @param usuario : Dados para alteração do usuário
     * @param ocorrenciaUsu : Ocorrencia que deverá ser incluída, caso seja nula será criada uma ocorrência de alteração de usuário.
     * @param funcoes : Funções de perfil personalizado do usuário
     * @param perCodigo : Perfil não personalizado do usuário
     * @param tipo : Tipo da entidade do usuario.
     * @param codigoEntidade : Código da entidade do usuário
     * @param validaCpf : True caso seja para validar o CPF do usuário
     * @param validaEmail : True caso seja para validar o Email do usuário de CSA/COR
     * @param validaTel : True caso seja para validar o Telefone do usuário CSA/COR
     * @param tipoMotivoOperacao : Motivo da operação
     * @param responsavel : Responsável pela operação
     * @throws UsuarioControllerException
     */
    private void alteraUsuario(UsuarioTransferObject usuario, OcorrenciaUsuarioTransferObject ocorrenciaUsu, List<String> funcoes, String perCodigo, String tipo, String codigoEntidade, boolean validaCpf, boolean validaEmail, boolean validaTel, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Se é consumo de senha, ou o usuário pode modificar o usuário, realiza a operação
            if (((ocorrenciaUsu != null) && ocorrenciaUsu.isUtilizacaoSenhaAutServidor()) || usuarioPodeModificarUsu(usuario.getUsuCodigo(), true, true, responsavel)) {

                final Usuario usuarioBean = findUsuarioBean(usuario, AcessoSistema.ENTIDADE_USU);
                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setUsuario(usuarioBean.getUsuCodigo());

                /* Compara a versão do cache com a passada por parâmetro */
                final String usuLoginAntesExclusao = usuarioBean.getUsuLogin();
                final UsuarioTransferObject usuarioCache = setUsuarioValues(usuarioBean);
                final CustomTransferObject merge = log.getUpdatedFields(usuario.getAtributos(), usuarioCache.getAtributos());

                final StringBuilder msgOus = new StringBuilder();

                if (merge.getAtributos().containsKey(Columns.USU_LOGIN)) {

                    // Verifica se não existe outro usuário com o mesmo login
                    final UsuarioTransferObject teste = new UsuarioTransferObject();
                    teste.setUsuLogin((String) merge.getAttribute(Columns.USU_LOGIN));

                    boolean existe = false;
                    try {
                        findUsuarioBean(teste, AcessoSistema.ENTIDADE_USU);
                        existe = true;
                    } catch (final UsuarioControllerException ex) {
                    }
                    if (existe) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.alterar.este.usuario.existe.outro.mesmo.login.cadastrado.sistema", responsavel);
                    }
                    msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.login.alterado.de.arg0.para.arg1", responsavel, usuarioBean.getUsuLogin(), (String) merge.getAttribute(Columns.USU_LOGIN)));
                    usuarioBean.setUsuLogin((String) merge.getAttribute(Columns.USU_LOGIN));
                }

                String stu_codigo = "";
                boolean stuAtivo = CodedValues.STU_ATIVO.equals(usuarioBean.getStatusLogin().getStuCodigo());
                if (merge.getAtributos().containsKey(Columns.USU_STU_CODIGO)) {
                    final boolean isCseSup = responsavel == null ? false : responsavel.isCseSup();
                    final boolean isSup = responsavel == null ? false : responsavel.isSup();

                    stu_codigo = merge.getAttribute(Columns.USU_STU_CODIGO) != null ? merge.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;

                    // Se o usuário alterado está sendo bloqueado ...
                    if (CodedValues.STU_BLOQUEADO.equals(stu_codigo)) {

                        // Verifica se ele não esta sendo bloqueado por ele próprio
                        if ((responsavel != null) && usuarioCache.getUsuCodigo().equals(responsavel.getUsuCodigo())) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.nao.pode.bloquear.ele.proprio", responsavel);
                        }

                        // Verifica se o usuário está sendo bloqueado por um usuário da consignante
                        if (isCseSup) {
                            stu_codigo = CodedValues.STU_BLOQUEADO_POR_CSE;
                            usuarioBean.setUsuTipoBloq(ApplicationResourcesHelper.getMessage(isSup ? "mensagem.usuario.bloqueado.sup" : "mensagem.usuario.bloqueado.cse", responsavel));
                        }

                        // Se o usuário alterado está sendo desbloqueado ...
                    } else if (CodedValues.STU_ATIVO.equals(stu_codigo)) {

                        if (!isCseSup && CodedValues.STU_BLOQUEADO_POR_CSE.equals(usuarioCache.getStuCodigo())) {
                            // Verifica se um usuário que não é da consignante está tentando desbloquear um usuário bloqueado pela consignante
                            throw new UsuarioControllerException("mensagem.erro.nao.possivel.desbloquear.usuario.arg0.pois.foi.bloqueado.pelo.consignante", responsavel, usuarioCache.getUsuLogin());
                        } else if (!isSup && CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.equals(usuarioCache.getStuCodigo())) {
                            // verifica se o usuário não é sup e está tentando desbloquear um usuário bloqueado por segurança
                            throw new UsuarioControllerException("mensagem.erro.nao.possivel.desbloquear.usuario.arg0.pois.foi.bloqueado.por.seguranca", responsavel, usuarioCache.getUsuLogin());
                        } else {
                            usuarioBean.setUsuTipoBloq("");
                        }

                        // Se o usuário alterado está sendo cancelado ...
                    } else if (CodedValues.STU_EXCLUIDO.equals(stu_codigo)) {

                        // Altera o login do usuário para o seu código de modo que
                        // novos usuários possam usar o login deste usuário
                        usuarioBean.setUsuTipoBloq(usuarioBean.getUsuLogin());
                        usuarioBean.setUsuLogin(usuarioBean.getUsuCodigo());
                    }

                    stuAtivo = CodedValues.STU_ATIVO.equals(stu_codigo);

                    // Altera o status do usuário
                    final StatusLogin statusLogin = StatusLoginHome.findByPrimaryKey(stu_codigo);
                    usuarioBean.setStatusLogin(statusLogin);
                    log.setStatusLogin(stu_codigo);
                }

                if (merge.getAtributos().containsKey(Columns.USU_NOME)) {
                    final String antes = usuarioBean.getUsuNome() != null ? usuarioBean.getUsuNome() : "";
                    final String depois = merge.getAttribute(Columns.USU_NOME) != null ? merge.getAttribute(Columns.USU_NOME).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.nome.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuNome((String) merge.getAttribute(Columns.USU_NOME));
                }
                if (merge.getAtributos().containsKey(Columns.USU_CPF)) {
                    final String antes = usuarioBean.getUsuCpf() != null ? usuarioBean.getUsuCpf() : "";
                    final String depois = merge.getAttribute(Columns.USU_CPF) != null ? merge.getAttribute(Columns.USU_CPF).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.cpf.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuCpf((String) merge.getAttribute(Columns.USU_CPF));
                }
                if (merge.getAtributos().containsKey(Columns.USU_EMAIL)) {
                    final String antes = usuarioBean.getUsuEmail() != null ? usuarioBean.getUsuEmail() : "";
                    final String depois = merge.getAttribute(Columns.USU_EMAIL) != null ? merge.getAttribute(Columns.USU_EMAIL).toString() : "";

                    final boolean bloqueiaEdicaoEmail = !responsavel.isSup() && ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL, responsavel);
                    if (bloqueiaEdicaoEmail && !TextHelper.isNull(antes)) {
                        // Se não pode editar e-mail e ele já estava cadastrado, então lança exceção
                        throw new UsuarioControllerException("mensagem.erro.usuario.email.nao.pode.ser.alterado", responsavel);
                    }
                    if (existeOutroUsuarioMesmoEmail(depois, usuarioBean.getUsuCpf(), responsavel)) {
                        // Se existe outro usuário no sistema com o mesmo e-mail, envia mensagem de erro
                        throw new UsuarioControllerException("mensagem.erro.usuario.email.informado.em.uso", responsavel);
                    }
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.email.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }

                    usuarioBean.setUsuEmail(depois);
                    // força nova validação do e-mail
                    usuarioBean.setUsuDataValidacaoEmail(null);
                }
                if (merge.getAtributos().containsKey(Columns.USU_TEL)) {
                    final String antes = usuarioBean.getUsuTel() != null ? usuarioBean.getUsuTel() : "";
                    final String depois = merge.getAttribute(Columns.USU_TEL) != null ? merge.getAttribute(Columns.USU_TEL).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.telefone.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuTel((String) merge.getAttribute(Columns.USU_TEL));
                }
                if (merge.getAtributos().containsKey(Columns.USU_SENHA)) {
                    usuarioBean.setUsuSenha((String) merge.getAttribute(Columns.USU_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_SENHA_APP)) {
                    usuarioBean.setUsuSenhaApp((String) merge.getAttribute(Columns.USU_SENHA_APP));
                }
                if (merge.getAtributos().containsKey(Columns.USU_SENHA_2)) {
                    usuarioBean.setUsuSenha2((String) merge.getAttribute(Columns.USU_SENHA_2));
                }
                if (merge.getAtributos().containsKey(Columns.USU_NOVA_SENHA)) {
                    usuarioBean.setUsuNovaSenha((String) merge.getAttribute(Columns.USU_NOVA_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DICA_SENHA)) {
                    final String antes = usuarioBean.getUsuDicaSenha() != null ? usuarioBean.getUsuDicaSenha() : "";
                    final String depois = merge.getAttribute(Columns.USU_DICA_SENHA) != null ? merge.getAttribute(Columns.USU_DICA_SENHA).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.dica.senha.alterada.dearg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuDicaSenha((String) merge.getAttribute(Columns.USU_DICA_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DATA_EXP_SENHA)) {
                    usuarioBean.setUsuDataExpSenha((java.sql.Date) merge.getAttribute(Columns.USU_DATA_EXP_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DATA_EXP_SENHA_2)) {
                    usuarioBean.setUsuDataExpSenha2((java.sql.Date) merge.getAttribute(Columns.USU_DATA_EXP_SENHA_2));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DATA_EXP_SENHA_APP)) {
                    usuarioBean.setUsuDataExpSenhaApp((java.sql.Date) merge.getAttribute(Columns.USU_DATA_EXP_SENHA_APP));
                }
                if (merge.getAtributos().containsKey(Columns.USU_IP_ACESSO)) {
                    final String antes = usuarioBean.getUsuIpAcesso() != null ? usuarioBean.getUsuIpAcesso() : "";
                    final String depois = merge.getAttribute(Columns.USU_IP_ACESSO) != null ? merge.getAttribute(Columns.USU_IP_ACESSO).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.ip.acesso.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuIpAcesso((String) merge.getAttribute(Columns.USU_IP_ACESSO));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DDNS_ACESSO)) {
                    final String antes = usuarioBean.getUsuDdnsAcesso() != null ? usuarioBean.getUsuDdnsAcesso() : "";
                    final String depois = merge.getAttribute(Columns.USU_DDNS_ACESSO) != null ? merge.getAttribute(Columns.USU_DDNS_ACESSO).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.acesso.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    usuarioBean.setUsuDdnsAcesso((String) merge.getAttribute(Columns.USU_DDNS_ACESSO));
                }
                if (merge.getAtributos().containsKey(Columns.USU_CENTRALIZADOR)) {
                    final String usuCentralizador = (String) merge.getAttribute(Columns.USU_CENTRALIZADOR);
                    final String centralizadorOld = usuarioBean.getUsuCentralizador() != null ? usuarioBean.getUsuCentralizador().toString() : "";
                    final String centralizadorNew = !TextHelper.isNull(usuCentralizador) ? usuCentralizador.substring(0, 1) : "";
                    if (!centralizadorOld.equals(centralizadorNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.centralizador.alterado.de.arg0.para.arg1", responsavel, centralizadorOld, centralizadorNew));
                    }
                    usuarioBean.setUsuCentralizador(!TextHelper.isNull(usuCentralizador) ? usuCentralizador.substring(0, 1) : null);
                }
                if (merge.getAtributos().containsKey(Columns.USU_AUTENTICA_SSO)) {
                    final String usuAutenticaSso = (String) merge.getAttribute(Columns.USU_AUTENTICA_SSO);
                    final String ssoOld = usuarioBean.getUsuAutenticaSso() != null ? usuarioBean.getUsuAutenticaSso().toString() : "";
                    final String ssoNew = !TextHelper.isNull(usuAutenticaSso) ? usuAutenticaSso.substring(0, 1) : "";
                    if (!ssoOld.equals(ssoNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.sso.alterado.de.arg0.para.arg1", responsavel, ssoOld, ssoNew));
                    }
                    usuarioBean.setUsuAutenticaSso(!TextHelper.isNull(usuAutenticaSso) ? usuAutenticaSso.substring(0, 1) : null);
                }
                if (merge.getAtributos().containsKey(Columns.USU_VISIVEL)) {
                    final String usuVisivel = (String) merge.getAttribute(Columns.USU_VISIVEL);
                    final String visivelOld = usuarioBean.getUsuVisivel() != null ? usuarioBean.getUsuVisivel().toString() : "";
                    final String visivelNew = !TextHelper.isNull(usuVisivel) ? usuVisivel.substring(0, 1) : "";
                    if (!visivelOld.equals(visivelNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.visibilidade.alterada.de.arg0.para.arg1", responsavel, visivelOld, visivelNew));
                    }
                    usuarioBean.setUsuVisivel(!TextHelper.isNull(usuVisivel) ? usuVisivel.substring(0, 1) : null);
                }
                if (merge.getAtributos().containsKey(Columns.USU_EXIGE_CERTIFICADO)) {
                    final String usuExigeCertificado = (String) merge.getAttribute(Columns.USU_EXIGE_CERTIFICADO);
                    final String certificadoOld = usuarioBean.getUsuExigeCertificado() != null ? usuarioBean.getUsuExigeCertificado().toString() : "";
                    final String certificadoNew = !TextHelper.isNull(usuExigeCertificado) ? usuExigeCertificado.substring(0, 1) : "";
                    if (!certificadoOld.equals(certificadoNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.exige.certificado.alterado.de.arg0.para.arg1", responsavel, certificadoOld, certificadoNew));
                    }
                    usuarioBean.setUsuExigeCertificado(!TextHelper.isNull(usuExigeCertificado) ? usuExigeCertificado.substring(0, 1) : null);
                }
                if (merge.getAtributos().containsKey(Columns.USU_TIPO_BLOQ)) {
                    usuarioBean.setUsuTipoBloq((String) merge.getAttribute(Columns.USU_TIPO_BLOQ));
                }
                if (merge.getAtributos().containsKey(Columns.USU_MATRICULA_INST)) {
                    usuarioBean.setUsuMatriculaInst((String) merge.getAttribute(Columns.USU_MATRICULA_INST));
                }
                if (merge.getAtributos().containsKey(Columns.USU_CHAVE_RECUPERAR_SENHA)) {
                    usuarioBean.setUsuChaveRecuperarSenha((String) merge.getAttribute(Columns.USU_CHAVE_RECUPERAR_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_OPERACOES_SENHA_2)) {
                    usuarioBean.setUsuOperacoesSenha2((Short) merge.getAttribute(Columns.USU_OPERACOES_SENHA_2));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DATA_REC_SENHA)) {
                    usuarioBean.setUsuDataRecSenha((java.util.Date) merge.getAttribute(Columns.USU_DATA_REC_SENHA));
                }
                if (merge.getAtributos().containsKey(Columns.USU_QTD_CONSULTAS_MARGEM)) {
                    usuarioBean.setUsuQtdConsultasMargem((Integer) merge.getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DATA_FIM_VIG)) {
                    final String dataOld = usuarioBean.getUsuDataFimVig() != null ? DateHelper.toDateString(usuarioBean.getUsuDataFimVig()) : "";
                    final String dataNew = merge.getAttribute(Columns.USU_DATA_FIM_VIG) != null ? DateHelper.toDateString((Date) merge.getAttribute(Columns.USU_DATA_FIM_VIG)) : "";
                    if (!dataOld.equals(dataNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.fim.vigencia.alterado.de.arg0.para.arg1", responsavel, dataOld, dataNew));
                    }
                    usuarioBean.setUsuDataFimVig((Date) merge.getAttribute(Columns.USU_DATA_FIM_VIG));
                }
                if (merge.getAtributos().containsKey(Columns.USU_DEFICIENTE_VISUAL)) {
                    final String usuDeficienteVisual = (String) merge.getAttribute(Columns.USU_DEFICIENTE_VISUAL);
                    final String deficienteVisualOld = usuarioBean.getUsuDeficienteVisual() != null ? usuarioBean.getUsuDeficienteVisual().toString() : "";
                    final String deficienteVisualNew = !TextHelper.isNull(usuDeficienteVisual) ? usuDeficienteVisual.substring(0, 1) : "";
                    if (!deficienteVisualOld.equals(deficienteVisualNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.deficiente.visual.alterado.de.arg0.para.arg1", responsavel, deficienteVisualOld, deficienteVisualNew));
                    }
                    usuarioBean.setUsuDeficienteVisual(!TextHelper.isNull(usuDeficienteVisual) ? usuDeficienteVisual.substring(0, 1) : null);
                }

                if (merge.getAtributos().containsKey(Columns.USU_CHAVE_VALIDACAO_EMAIL)) {
                    usuarioBean.setUsuChaveValidacaoEmail((String) merge.getAttribute(Columns.USU_CHAVE_VALIDACAO_EMAIL));
                }

                if (merge.getAtributos().containsKey(Columns.USU_DATA_VALIDACAO_EMAIL)) {
                    usuarioBean.setUsuDataValidacaoEmail((java.util.Date) merge.getAttribute(Columns.USU_DATA_VALIDACAO_EMAIL));
                }

                if (merge.getAtributos().containsKey(Columns.USU_AUTORIZA_EMAIL_MARKETING)) {
                    usuarioBean.setUsuAutorizaEmailMarketing((String) merge.getAttribute(Columns.USU_AUTORIZA_EMAIL_MARKETING));
                }

                if (stuAtivo) {
                    if (validaCpf) {
                        // Se o usuário está ativo, ou está sendo desbloqueado, validar obrigatoriedade
                        // do CPF, bem como sua unicidade, dado os parâmetros de sistema
                        validaCpfUsuario(usuarioBean.getUsuCpf(), tipo, responsavel);
                        validaUnicidadeCpf(usuarioBean.getUsuCodigo(), usuarioBean.getUsuCpf(), tipo, codigoEntidade, responsavel);
                    }
                    if (validaEmail) {
                        if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                            final boolean usuEmailObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_EMAIL_OBRIGATORIO_USUARIO_CSA, CodedValues.TPC_SIM, responsavel);
                            if (usuEmailObrigatorio && TextHelper.isNull(usuarioBean.getUsuEmail())) {
                                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.email.usuario.deve.ser.informado", responsavel);
                            }
                        } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                            final boolean usuEmailObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_EMAIL_OBRIGATORIO_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel);
                            if (usuEmailObrigatorio && TextHelper.isNull(usuarioBean.getUsuEmail())) {
                                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.email.usuario.deve.ser.informado", responsavel);
                            }
                        }
                    }
                    if (validaTel) {
                        if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                            final boolean usuTelObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSA, CodedValues.TPC_SIM, responsavel);
                            if (usuTelObrigatorio && TextHelper.isNull(usuarioBean.getUsuTel())) {
                                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.telefone.usuario.deve.ser.informado", responsavel);
                            }
                        }
                        if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                            final boolean usuTelObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSE, CodedValues.TPC_SIM, responsavel);
                            if (usuTelObrigatorio && TextHelper.isNull(usuarioBean.getUsuTel())) {
                                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.telefone.usuario.deve.ser.informado", responsavel);
                            }
                        }
                        if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                            final boolean usuTelObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_SUP, CodedValues.TPC_SIM, responsavel);
                            if (usuTelObrigatorio && TextHelper.isNull(usuarioBean.getUsuTel())) {
                                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.telefone.usuario.deve.ser.informado", responsavel);
                            }
                        }
                    }
                }

                if (merge.getAtributos().containsKey(Columns.USU_CHAVE_VALIDACAO_TOTP)) {
                    final String usuChaveValidacaoTotp = (String) merge.getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP);
                    final String chaveTotpOld = !TextHelper.isNull(usuarioBean.getUsuChaveValidacaoTotp()) ? usuarioBean.getUsuChaveValidacaoTotp() : "";
                    final String chaveTotpNew = !TextHelper.isNull(usuChaveValidacaoTotp) ? usuChaveValidacaoTotp : "";
                    if (!chaveTotpOld.equals(chaveTotpNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.chave.validacao.totp.alterado.de.arg0.para.arg1", responsavel, chaveTotpOld, chaveTotpNew));
                    }
                    usuarioBean.setUsuChaveValidacaoTotp(!TextHelper.isNull(usuChaveValidacaoTotp) ? usuChaveValidacaoTotp : null);
                }

                if (merge.getAtributos().containsKey(Columns.USU_PERMITE_VALIDACAO_TOTP) && responsavel.isSup() && !responsavel.getUsuCodigo().equals(usuario.getUsuCodigo())) {
                    final String usuPermiteValidacaoTotp = (String) merge.getAttribute(Columns.USU_PERMITE_VALIDACAO_TOTP);
                    final String permiteTotpOld = !TextHelper.isNull(usuarioBean.getUsuPermiteValidacaoTotp()) ? usuarioBean.getUsuPermiteValidacaoTotp() : "";
                    final String permiteTotpNew = !TextHelper.isNull(usuPermiteValidacaoTotp) ? usuPermiteValidacaoTotp : "";
                    if (!permiteTotpOld.equals(permiteTotpNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.permite.validacao.totp.alterado.de.arg0.para.arg1", responsavel, permiteTotpOld, permiteTotpNew));
                    }
                    usuarioBean.setUsuPermiteValidacaoTotp(!TextHelper.isNull(usuPermiteValidacaoTotp) ? usuPermiteValidacaoTotp : null);
                }

                if (merge.getAtributos().containsKey(Columns.USU_OPERACOES_VALIDACAO_TOTP)) {
                    final String usuOperacoesValidacaoTotp = (String) merge.getAttribute(Columns.USU_OPERACOES_VALIDACAO_TOTP);
                    final String operacoesTotpOld = !TextHelper.isNull(usuarioBean.getUsuOperacoesValidacaoTotp()) ? usuarioBean.getUsuOperacoesValidacaoTotp() : OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo();
                    final String operacoesTotpNew = !TextHelper.isNull(usuOperacoesValidacaoTotp) ? usuOperacoesValidacaoTotp : OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo();
                    if (!operacoesTotpOld.equals(operacoesTotpNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.operacoes.validacao.totp.alterado.de.arg0.para.arg1", responsavel, operacoesTotpOld, operacoesTotpNew));
                    }
                    usuarioBean.setUsuOperacoesValidacaoTotp(operacoesTotpNew);
                }

                if (merge.getAtributos().containsKey(Columns.USU_OTP_CHAVE_SEGURANCA)) {
                    final String usuOtpChaveSeguranca = (String) merge.getAttribute(Columns.USU_OTP_CHAVE_SEGURANCA);
                    final String chaveOtpOld = !TextHelper.isNull(usuarioBean.getUsuOtpChaveSeguranca()) ? usuarioBean.getUsuOtpChaveSeguranca() : "";
                    final String chaveOtpNew = !TextHelper.isNull(usuOtpChaveSeguranca) ? usuOtpChaveSeguranca : "";
                    if (!chaveOtpOld.equals(chaveOtpNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.otp.chave.seguranca.alterado.de.arg0.para.arg1", responsavel, chaveOtpOld, chaveOtpNew));
                    }
                    usuarioBean.setUsuOtpChaveSeguranca(!TextHelper.isNull(usuOtpChaveSeguranca) ? usuOtpChaveSeguranca : null);

                }

                if (merge.getAtributos().containsKey(Columns.USU_OTP_CODIGO)) {
                    final String usuOtpCodigo = (String) merge.getAttribute(Columns.USU_OTP_CODIGO);
                    final String otpCodigoOld = !TextHelper.isNull(usuarioBean.getUsuOtpCodigo()) ? usuarioBean.getUsuOtpCodigo() : "";
                    final String otpCodigoNew = !TextHelper.isNull(usuOtpCodigo) ? usuOtpCodigo : "";
                    if (!otpCodigoOld.equals(otpCodigoNew)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.otp.codigo.alterado.de.arg0.para.arg1", responsavel, otpCodigoOld, otpCodigoNew));
                    }
                    usuarioBean.setUsuOtpCodigo(!TextHelper.isNull(usuOtpCodigo) ? usuOtpCodigo : null);

                }

                if (merge.getAtributos().containsKey(Columns.USU_OTP_DATA_CADASTRO)) {
                    usuarioBean.setUsuOtpDataCadastro((java.util.Date) merge.getAttribute(Columns.USU_OTP_DATA_CADASTRO));
                }

                AbstractEntityHome.update(usuarioBean);

                // Atualiza o perfil do usuário
                if (!TextHelper.isNull(perCodigo) || (funcoes != null)) {
                    updateUsuarioPerfil(funcoes, perCodigo, usuario.getUsuCodigo(), codigoEntidade, tipo, true, responsavel);
                }

                if ((ocorrenciaUsu == null) && !merge.getAtributos().isEmpty()) {
                    // Cria ocorrência de alteração de usuário
                    ocorrenciaUsu = new OcorrenciaUsuarioTransferObject();
                    ocorrenciaUsu.setUsuCodigo(usuarioBean.getUsuCodigo());
                    ocorrenciaUsu.setTocCodigo(CodedValues.TOC_ALTERACAO_USUARIO);
                    ocorrenciaUsu.setOusUsuCodigo(responsavel.getUsuCodigo());
                    ocorrenciaUsu.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.usuario", responsavel) + msgOus.toString());
                    ocorrenciaUsu.setOusIpAcesso(responsavel.getIpUsuario());
                }
                if ((ocorrenciaUsu != null) && (tipoMotivoOperacao != null)) {
                    ocorrenciaUsu.setOusObs(ocorrenciaUsu.getOusObs() + (ocorrenciaUsu.getOusObs().lastIndexOf(".") == (ocorrenciaUsu.getOusObs().length() - 1) ? " " : ". ") + tipoMotivoOperacao.getAttribute(Columns.OUS_OBS));
                    ocorrenciaUsu.setAttribute(Columns.OUS_TMO_CODIGO, tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO));
                }
                if (ocorrenciaUsu != null) {
                    createOcorrenciaUsuario(ocorrenciaUsu, responsavel);
                }

                log.write();

                // Se for exclusão de usuário csa, verifica se o parâmetro de sistema do papel autentica no SSO
                boolean autenticaSSO = false;
                if (!TextHelper.isNull(tipo) && !TextHelper.isNull(codigoEntidade) && CodedValues.STU_EXCLUIDO.equals(stu_codigo) && AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    // Seta email apenas para validação do usuário
                    usuario.setUsuEmail(usuarioBean.getUsuEmail());
                    autenticaSSO = !TextHelper.isNull(usuarioBean.getUsuAutenticaSso()) && CodedValues.TPC_SIM.equals(usuarioBean.getUsuAutenticaSso()) && validarDadosSSO(usuario, codigoEntidade, tipo, responsavel);
                }

                if (autenticaSSO) {
                    // Irá verificar se o sso está habilitado e remover a ligação com a consignatária
                    ssoClient.removeServiceProviderFromUser(usuLoginAntesExclusao, tipo, codigoEntidade);
                }
            }
        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } catch (ParametroControllerException | SSOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(ex.getMessage(), responsavel, ex);
        } catch (UpdateException | FindException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void updateEnderecoAcessoFuncao(CustomTransferObject dadosTo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String usuCodigo = (String) dadosTo.getAttribute(Columns.USU_CODIGO);
            final String codEntidade = (String) dadosTo.getAttribute("COD_ENTIDADE");
            final String tipo = (String) dadosTo.getAttribute("TIPO");

            final String funCodigo = (String) dadosTo.getAttribute(Columns.FUN_CODIGO);
            final String funDescricao = (String) dadosTo.getAttribute(Columns.FUN_DESCRICAO);
            final String eafIpAcesso = (String) dadosTo.getAttribute(Columns.EAF_IP_ACESSO);
            final String eafDdnsAcesso = (String) dadosTo.getAttribute(Columns.EAF_DDNS_ACESSO);

            // Carrega o objeto do banco (se existir)
            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FUN_CODIGO, funCodigo);
            final EnderecoFuncaoTransferObject funcoesToAtual = selectFuncoes(usuCodigo, codEntidade, tipo, criterio, -1, -1, responsavel).get(funCodigo);
            boolean create = false;
            EnderecoAcessoFuncao eafBean = null;
            try {
                eafBean = EnderecoAcessoFuncaoHome.findByPrimaryKey(usuCodigo, funCodigo);
            } catch (final FindException ex) {
                eafBean = new EnderecoAcessoFuncao(usuCodigo, funCodigo, funcoesToAtual.getEafIpAcesso(), funcoesToAtual.getEafDdnsAcesso());
                create = true;
            }

            // Objeto a ser salvo
            final EnderecoFuncaoTransferObject funcoesToNovo = new EnderecoFuncaoTransferObject();
            funcoesToNovo.setFunCodigo(funCodigo);
            funcoesToNovo.setFunDescricao(funDescricao);
            funcoesToNovo.setEafIpAcesso(eafIpAcesso);
            funcoesToNovo.setEafDdnsAcesso(eafDdnsAcesso);

            // LOG
            final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setUsuario(usuCodigo);

            final boolean podeEdtRestAcessoFun = usuarioPodeModificarUsu(usuCodigo, true, true, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO);

            // Se o usuário pode modificar o usuário, realiza a operação
            if (podeEdtRestAcessoFun) {
                // Compara as versões
                final CustomTransferObject merge = log.getUpdatedFields(funcoesToNovo.getAtributos(), funcoesToAtual.getAtributos());

                final StringBuilder msgOus = new StringBuilder();

                if (merge.getAtributos().containsKey(Columns.EAF_IP_ACESSO)) {
                    final String antes = eafBean.getEafIpAcesso() != null ? eafBean.getEafIpAcesso() : "";
                    final String depois = merge.getAttribute(Columns.EAF_IP_ACESSO) != null ? merge.getAttribute(Columns.EAF_IP_ACESSO).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.ip.acesso.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    eafBean.setEafIpAcesso((String) merge.getAttribute(Columns.EAF_IP_ACESSO));
                }
                if (merge.getAtributos().containsKey(Columns.EAF_DDNS_ACESSO)) {
                    final String antes = eafBean.getEafDdnsAcesso() != null ? eafBean.getEafDdnsAcesso() : "";
                    final String depois = merge.getAttribute(Columns.EAF_DDNS_ACESSO) != null ? merge.getAttribute(Columns.EAF_DDNS_ACESSO).toString() : "";
                    if (!antes.equals(depois)) {
                        msgOus.append(ApplicationResourcesHelper.getMessage("mensagem.informacao.endereco.acesso.alterado.de.arg0.para.arg1", responsavel, antes, depois));
                    }
                    eafBean.setEafDdnsAcesso((String) merge.getAttribute(Columns.EAF_DDNS_ACESSO));
                }

                if (!create && TextHelper.isNull(eafIpAcesso) && TextHelper.isNull(eafDdnsAcesso)) {
                    AbstractEntityHome.remove(eafBean);
                } else if (create) {
                    EnderecoAcessoFuncaoHome.create(eafBean);
                } else {
                    AbstractEntityHome.update(eafBean);
                }

                if (!merge.getAtributos().isEmpty()) {
                    // Cria ocorrência de alteração de usuário
                    OcorrenciaUsuarioTransferObject ocorrenciaUsu = new OcorrenciaUsuarioTransferObject();
                    ocorrenciaUsu = new OcorrenciaUsuarioTransferObject();
                    ocorrenciaUsu.setUsuCodigo(usuCodigo);
                    ocorrenciaUsu.setTocCodigo(CodedValues.TOC_ALTERACAO_USUARIO);
                    ocorrenciaUsu.setOusUsuCodigo(responsavel.getUsuCodigo());
                    ocorrenciaUsu.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.usuario", responsavel) + msgOus.toString());
                    ocorrenciaUsu.setOusIpAcesso(responsavel.getIpUsuario());
                    if (ocorrenciaUsu != null) {
                        createOcorrenciaUsuario(ocorrenciaUsu, responsavel);
                    }
                }
                log.write();
            }
        } catch (CreateException | UpdateException | RemoveException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

    }

    /**
     * Verifica obrigatoriedade do CPF, bem como se o valor informado é válido para um usuário.
     * @param usuCpf: CPF a ser validado
     * @param tipo : CSE, ORG, CSA, COR, SER ou SUP
     * @param responsavel: Responsável pela operação
     * @return TRUE caso o CPF seja informado e esteja correto, ou se não é obrigatório e não foi informado
     * @throws UsuarioControllerException
     */
    private boolean validaCpfUsuario(String usuCpf, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        final String[] cpfsInvalidos = {
                                         "000.000.000-00", "111.111.111-11", "222.222.222-22", "333.333.333-33", "444.444.444-44",
                                         "555.555.555-55", "666.666.666-66", "777.777.777-77", "888.888.888-88", "999.999.999-99"
        };

        // Não valida CPF de usuário de suporte em sistema que não seja no Brasil
        final boolean validarCpfSuporte = (!AcessoSistema.ENTIDADE_SUP.equals(tipo) || LocaleHelper.BRASIL.equals(LocaleHelper.getLocale()));

        if (TextHelper.isNull(usuCpf)) {
            final boolean cpfObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO, CodedValues.TPC_SIM, responsavel);
            if (cpfObrigatorio) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.cpf.usuario.deve.ser.informado", responsavel);
            }
        } else if (validarCpfSuporte && (!TextHelper.cpfOk(TextHelper.dropSeparator(usuCpf)) || (Arrays.binarySearch(cpfsInvalidos, usuCpf) >= 0))) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.cpf.usuario.invalido", responsavel);
        }
        return true;
    }

    /**
     * Verifica se o sistema impede que o usuario de csa/cor cadastrado tenha CPF igual ao de algum outro usuario csa/cor
     * de entidade diferente ou de um servidor.
     * @param usuCodigo: Codigo do usuario.
     * @param usuCpf: CPF do usuario.
     * @param tipoEntidade: Tipo da entidade do usuario.
     * @param codigoEntidade: Código da entidade do usuário
     * @param responsavel: Responsavel pela operacao.
     * @throws UsuarioControllerException Excecao padrao.
     */
    private void validaUnicidadeCpf(String usuCodigo, String usuCpf, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        if (!TextHelper.isNull(usuCpf) && !TextHelper.isNull(tipoEntidade)) {
            if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                // CPF cadastrado para um usuário de CSA/COR não pode ser igual a um CPF de servidor não excluído
                boolean exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_SER, responsavel);
                if (exigeUnicidadeCPF && existeServidor(usuCpf, responsavel)) {
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.servidor.mesmo.cpf", responsavel);
                }

                // CPF cadastrado para um usuário de CSA/COR não pode ser igual a um CPF de usuário CSE/ORG ativo
                exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_CSE, responsavel);
                if (exigeUnicidadeCPF) {
                    List<TransferObject> usuarios = lstUsuarioCseOrg(usuCodigo, usuCpf, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.gestor.mesmo.cpf", responsavel);
                    }
                    usuarios = lstUsuarioSup(usuCodigo, usuCpf, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.suporte.mesmo.cpf", responsavel);
                    }
                }

                // CPF cadastrado para um usuário de CSA/COR não pode ser igual a um CPF de usuário de outra entidade CSA/COR ativo
                exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA, responsavel);
                if (exigeUnicidadeCPF) {
                    final List<TransferObject> usuarios = lstUsuarioCsaCor(usuCodigo, usuCpf, false, tipoEntidade, codigoEntidade, false, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.usuario.outra.entidade.mesmo.cpf", responsavel);
                    }
                }

                // CPF cadastrado para um usuário de CSA/COR não pode ser igual a um CPF de usuário da mesma entidade CSA/COR ativo
                exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_MESMA_CSA, responsavel);
                if (exigeUnicidadeCPF) {
                    final List<TransferObject> usuarios = lstUsuarioCsaCor(usuCodigo, usuCpf, false, tipoEntidade, codigoEntidade, true, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.usuario.mesma.entidade.mesmo.cpf", responsavel);
                    }
                }

            } else if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                // CPF cadastrado para um usuário de CSE/ORG não pode ser igual a um CPF de outro usuário CSE/ORG ativo
                boolean exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSE, responsavel);
                if (exigeUnicidadeCPF) {
                    List<TransferObject> usuarios = lstUsuarioCseOrg(usuCodigo, usuCpf, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.gestor.mesmo.cpf", responsavel);
                    }
                    usuarios = lstUsuarioSup(usuCodigo, usuCpf, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.suporte.mesmo.cpf", responsavel);
                    }
                }

                // CPF cadastrado para um usuário de CSE/ORG não pode ser igual a um CPF de usuário CSA/COR ativo
                exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_CSE, responsavel);
                if (exigeUnicidadeCPF) {
                    final List<TransferObject> usuarios = lstUsuarioCsaCor(usuCodigo, usuCpf, false, null, null, false, responsavel);
                    if ((usuarios != null) && (usuarios.size() > 0)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.possivel.realizar.esta.operacao.pois.existe.usuario.outra.entidade.mesmo.cpf", responsavel);
                    }
                }
            }
        }
    }

    private void updateUsuarioPerfil(List<String> funcoes, String perCodigo, String usuCodigo, String codigoEntidade, String tipo, boolean isAlteracao, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // LOG Verificar log que está setando funções removidas/inseridas ou perfil alterado
            final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setUsuario(usuCodigo);

            boolean alterou = false;

            // Pega as funções que o usuário possui nas tabelas tb_funcao_perfil_???
            final List<String> funcoesOld = getUsuarioFuncoes(usuCodigo, tipo, responsavel);

            final List<String> diff = new ArrayList<>();
            final List<String> excluir = new ArrayList<>();

            // Funções permitidas para o usuário
            final List<String> funcoesPermitidas = new ArrayList<>();
            final List<TransferObject> funPermUsu = lstFuncoesPermitidasUsuario(tipo, codigoEntidade, responsavel);
            if ((funPermUsu != null) && !funPermUsu.isEmpty()) {
                for (final TransferObject transferObject : funPermUsu) {
                    funcoesPermitidas.add(transferObject.getAttribute(Columns.FUN_CODIGO).toString());
                }
            }

            if (funcoes != null) {
                for (final String funPermitida : funcoesPermitidas) {
                    if (funcoesOld.contains(funPermitida) && !funcoes.contains(funPermitida)) {
                        excluir.add(funPermitida);
                    } else if (funcoesOld.contains(funPermitida) && funcoes.contains(funPermitida)) {
                        // Usuário já possui a função, não precisa incluir
                        continue;
                    } else if (!funcoesOld.contains(funPermitida) && funcoes.contains(funPermitida)) {
                        diff.add(funPermitida);
                    }
                }

                //Caso a função não seja permitida gera log de erro
                for (final String novaFuncao : funcoes) {
                    if (!funcoesPermitidas.contains(novaFuncao)) {
                        try {
                            final LogDelegate logErro = new LogDelegate(responsavel, Log.FUNCAO_PERFIL, Log.UPDATE, Log.LOG_ERRO_SEGURANCA);
                            logErro.setPerfil(perCodigo);
                            logErro.setFuncao(novaFuncao);
                            logErro.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.incluir.esta.funcao", responsavel)));
                            logErro.write();
                            continue;
                        } catch (final LogControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }

                // confere se usuário possui email cadastrado para atrbuir a função 248 (usuário auditor)
                if (funcoes.contains(CodedValues.FUN_USUARIO_AUDITOR)) {
                    final UsuarioTransferObject filtro = new UsuarioTransferObject(usuCodigo);

                    final UsuarioTransferObject usuario = findUsuario(filtro, tipo, responsavel);
                    if (TextHelper.isNull(usuario.getUsuEmail())) {
                        throw new UsuarioControllerException("mensagem.erro.email.valido.deve.ser.cadastrado.para.atribuir.permissao.auditor.ao.usuario", responsavel);
                    }
                }
            } else {
                excluir.addAll(funcoesOld);
            }

            List<String> funcPerfil = null;
            if (!TextHelper.isNull(perCodigo)) {
                funcPerfil = getFuncaoPerfil(tipo, codigoEntidade, perCodigo, responsavel);
            }

            // confere se é o último usuário auditor para a entidade alvo.
            if (excluir.contains(CodedValues.FUN_USUARIO_AUDITOR) && !podeRemoverFuncAuditoria(usuCodigo, codigoEntidade, tipo, responsavel) && ((funcPerfil == null) || !funcPerfil.contains(CodedValues.FUN_USUARIO_AUDITOR))) {
                throw new UsuarioControllerException("mensagem.erro.permissao.auditoria.nao.pode.remover", responsavel);
            }

            // confere se usuário possui email cadastrado para atrbuir a função 248 (usuário auditor)
            // para o caso de ser atribuído a um perfil
            if (!TextHelper.isNull(perCodigo)) {
                funcPerfil = getFuncaoPerfil(tipo, codigoEntidade, perCodigo, responsavel);

                if (funcPerfil.contains(CodedValues.FUN_USUARIO_AUDITOR)) {
                    final UsuarioTransferObject filtro = new UsuarioTransferObject(usuCodigo);

                    final UsuarioTransferObject usuario = findUsuario(filtro, tipo, responsavel);
                    if (TextHelper.isNull(usuario.getUsuEmail())) {
                        throw new UsuarioControllerException("mensagem.erro.email.valido.deve.ser.cadastrado.para.atribuir.permissao.auditor.ao.usuario", responsavel);
                    }
                }
            }

            // Apagar as removidas presentes em funcoesOld
            if (excluir.size() > 0) {
                removeFuncaoPerfilUsuario(excluir, usuCodigo, tipo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.removendo.permissoes", responsavel, TextHelper.join(excluir, ",")));
                alterou = true;
            }

            // Adicionar as novas presentes em diff
            if (diff.size() > 0) {
                createUsuarioPerfil(diff, usuCodigo, codigoEntidade, tipo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.inserindo.permissoes", responsavel, TextHelper.join(diff, ",")));
                alterou = true;
            }

            // Busca o perfil do usuário
            PerfilUsuario upeBean = null;
            Perfil perfilAnterior = null;
            try {
                upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);
                perfilAnterior = PerfilHome.findByPrimaryKey(upeBean.getPerfil().getPerCodigo());
            } catch (final FindException ex) {
            }

            String obs = "";
            if (!TextHelper.isNull(perCodigo) && (upeBean != null)) {
                // Se o usuário mudou de perfil
                if (!perCodigo.equals(perfilAnterior.getPerCodigo())) {
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.trocando.de.perfil", responsavel, perfilAnterior.getPerCodigo() + "," + perCodigo));
                    final Perfil perfil = PerfilHome.findByPrimaryKey(perCodigo);
                    obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.de.arg0.para.arg1", responsavel, perfilAnterior.getPerDescricao(), perfil.getPerDescricao());
                    upeBean.setPerfil(perfil);
                    AbstractEntityHome.update(upeBean);
                    alterou = true;
                }
            } else if (!TextHelper.isNull(perCodigo)) {
                // Se o usuário agora tem um perfil
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.incluindo.perfil", responsavel, perCodigo));
                final Perfil perfil = PerfilHome.findByPrimaryKey(perCodigo);
                obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.de.arg0.para.arg1", responsavel, ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.personalizado", responsavel), perfil.getPerDescricao());
                PerfilUsuarioHome.create(usuCodigo, perCodigo);
                alterou = true;
            } else if (upeBean != null) {
                // Se o usuário não tem mais um perfil
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.excluindo.perfil", responsavel, perfilAnterior.getPerCodigo()));
                obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.de.arg0.para.arg1", responsavel, perfilAnterior.getPerDescricao(), ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.personalizado", responsavel));
                AbstractEntityHome.remove(upeBean);
                alterou = true;
            }

            log.write();

            // Se alterou o perfil de funções do usuário
            if (alterou && isAlteracao) {
                // Cria ocorrência de alteração de perfil de usuário
                final CustomTransferObject ocorrencia = new CustomTransferObject();
                ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
                ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_ALTERACAO_PERFIL_USUARIO);
                ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
                ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.perfil.usuario", responsavel) + obs);
                ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

                createOcorrenciaUsuario(ocorrencia, responsavel);
            }
        } catch (CreateException | UpdateException | RemoveException | FindException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * confere se a função de auditoria pode ser removida do usuário alvo. se ele for o último auditor
     * da entidade e houver funções auditadas para esta, a função não pode ser removida.
     * @param usuCodigo
     * @param codigoEntidade
     * @param tipo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public boolean podeRemoverFuncAuditoria(String usuCodigo, String codigoEntidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        // se estiver removendo a permissão de auditor, verificar se há funções auditadas para a entidade
        // e se este é o último usuário auditor do sistema
        final ListaUsuariosAuditoresQuery lstAuditores = new ListaUsuariosAuditoresQuery();
        final List<String> usuCodigos = new ArrayList<>();
        usuCodigos.add(CodedValues.NOT_EQUAL_KEY);
        usuCodigos.add(usuCodigo);
        lstAuditores.usuCodigo = usuCodigos;
        lstAuditores.codigoEntidade = codigoEntidade;
        lstAuditores.tipo = tipo;
        lstAuditores.count = true;

        try {
            final int auditoresQntd = lstAuditores.executarContador();
            if (auditoresQntd <= 0) {
                final ListaFuncoesAuditadasQuery funcAuditadasEnt = new ListaFuncoesAuditadasQuery();
                funcAuditadasEnt.codigoEntidade = codigoEntidade;
                funcAuditadasEnt.tipo = tipo;
                funcAuditadasEnt.count = true;

                final int numFuncAuditadas = funcAuditadasEnt.executarContador();
                if (numFuncAuditadas > 0) {
                    return false;
                }
            }
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }

        return true;
    }

    @Override
    public void removeUsuario(UsuarioTransferObject usuario, String tipo, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (usuarioPodeModificarUsu(usuario.getUsuCodigo(), true, true, responsavel)) {

                try {
                    // Verifica se o responsável pela operação ainda existe, ou seja, se o usuário não foi excluido.
                    findUsuarioBean(new UsuarioTransferObject(responsavel.getUsuCodigo()), responsavel.getTipoEntidade());
                } catch (final UsuarioControllerException ex) {
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.excluir.usuario.selecionado.pois.seu.usuario.esta.bloqueado", responsavel);
                }

                final Usuario usuarioBean = findUsuarioBean(usuario, tipo);
                final String usuCodigo = usuarioBean.getUsuCodigo();

                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.DELETE, Log.LOG_INFORMACAO);
                log.setUsuario(usuCodigo);

                // Se o usuário que está excluindo é o mesmo usuário que será excluído
                if (usuCodigo.equals(responsavel.getUsuCodigo())) {
                    throw new UsuarioControllerException("mensagem.erro.usuario.nao.pode.ser.excluido.por.ele.proprio", responsavel);
                }

                String tpcEnviaAuditoria = null;
                try {
                    if (responsavel.isCseSupOrg()) {
                        tpcEnviaAuditoria = parametroController.findParamSistCse(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG, "1", responsavel);
                    } else if (responsavel.isCsaCor()) {
                        tpcEnviaAuditoria = parametroController.findParamSistCse(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR, "1", responsavel);
                    }
                } catch (final ParametroControllerException e1) {
                    LOG.error(e1.getMessage(), e1);
                    throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e1);
                }

                if (!TextHelper.isNull(tpcEnviaAuditoria) && !CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO.equals(tpcEnviaAuditoria)) {
                    final ListaUsuariosAuditoresQuery usuAuditListQuery = new ListaUsuariosAuditoresQuery();

                    final String codEntidade = usuario.getAttribute(Columns.COR_CODIGO) != null ? (String) usuario.getAttribute(Columns.COR_CODIGO) : usuario.getAttribute(Columns.CSA_CODIGO) != null ? (String) usuario.getAttribute(Columns.CSA_CODIGO) : usuario.getAttribute(Columns.CSE_CODIGO) != null ? (String) usuario.getAttribute(Columns.CSE_CODIGO) : usuario.getAttribute(Columns.ORG_CODIGO) != null ? (String) usuario.getAttribute(Columns.ORG_CODIGO) : null;

                    if (!TextHelper.isNull(codEntidade)) {
                        usuAuditListQuery.codigoEntidade = codEntidade;
                        usuAuditListQuery.stuCodigo = CodedValues.STU_ATIVO;
                        usuAuditListQuery.tipo = tipo;

                        try {
                            final List<UsuarioTransferObject> usuAuditList = usuAuditListQuery.executarDTO(UsuarioTransferObject.class);
                            if (usuAuditList.size() == 1) {
                                final String usuCodigoAudit = usuAuditList.get(0).getUsuCodigo();

                                // se usuário é o último auditor remascente da entidade e houver configuração de auditoria para esta,
                                // a remoção do usuário não é permitida
                                if (usuCodigoAudit.equals(usuario.getUsuCodigo())) {
                                    throw new UsuarioControllerException("mensagem.erro.usuario.nao.pode.ser.removido.pois.entidade.possui.configuracoes.auditoria", responsavel);
                                }
                            }
                        } catch (final HQueryException e) {
                            LOG.error(e.getMessage(), e);
                            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
                        }
                    }
                }

                String entidade = null;
                if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    entidade = (String) usuario.getAttribute(Columns.COR_CODIGO);
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    entidade = (String) usuario.getAttribute(Columns.CSA_CODIGO);
                } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    entidade = (String) usuario.getAttribute(Columns.CSE_CODIGO);
                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    entidade = (String) usuario.getAttribute(Columns.ORG_CODIGO);
                } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                    entidade = (String) usuario.getAttribute(Columns.CSE_CODIGO);
                }

                final Map<String, EnderecoFuncaoTransferObject> funcMap = selectFuncoes(usuCodigo, entidade, tipo, responsavel);
                final Set<String> keySet = funcMap.keySet();

                if (keySet.contains(CodedValues.FUN_USUARIO_AUDITOR) && !podeRemoverFuncAuditoria(usuCodigo, entidade, tipo, responsavel)) {
                    throw new UsuarioControllerException("mensagem.erro.permissao.auditoria.nao.pode.remover", responsavel);
                }

                // Remove ligacao entre perfil e usuário (tb_perfil_usuario) para perfis especificos
                try {
                    final PerfilUsuario upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);
                    AbstractEntityHome.remove(upeBean);
                    final List<String> funcoes = getFuncaoPerfil(tipo, usuCodigo, upeBean.getPerfil().getPerCodigo(), responsavel);

                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.funcoes.removidas", responsavel, TextHelper.join(funcoes, ",")));
                } catch (final FindException e) {
                } // Se nao achou é porque usuario possui perfil personalizado

                // Exclui da tabela tb_funcao_perfil_xxx para perfis personalizados
                final List<String> listaFuncoes = removeFuncaoPerfilUsuario(null, usuCodigo, tipo);
                if ((listaFuncoes != null) && !listaFuncoes.isEmpty()) {
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.funcoes.removidas", responsavel, TextHelper.join(listaFuncoes, ",")));
                }

                // Cria ocorrência de exclusão
                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo(usuario.getUsuCodigo());
                ocorrencia.setTocCodigo(CodedValues.TOC_EXCLUSAO_USUARIO);
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.exclusao.usuario", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                if (tipoMotivoOperacao != null) {
                    ocorrencia.setOusObs(ocorrencia.getOusObs() + (ocorrencia.getOusObs().lastIndexOf(".") == (ocorrencia.getOusObs().length() - 1) ? " " : ". ") + tipoMotivoOperacao.getAttribute(Columns.OUS_OBS));
                    ocorrencia.setAttribute(Columns.OUS_TMO_CODIGO, tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO));
                }

                // Exclusão lógica de usuário
                usuario.setStuCodigo(CodedValues.STU_EXCLUIDO);
                alteraUsuario(usuario, ocorrencia, null, null, tipo, entidade, false, false, false, null, responsavel);

                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.excluir.usuario.selecionado.erro.interno.arg0", responsavel, ex.getMessage());
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(ex);
        }
    }

    private void removeVinculoFuncao(String funCodigo, List<String> papCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.FUNCAO_PERFIL, Log.DELETE, Log.LOG_INFORMACAO);
            log.setFuncao(funCodigo);

            final Funcao funcao = FuncaoHome.findByPrimaryKey(funCodigo);

            // Exclui todos os vínculos da função com qualquer perfil personalizado de qualquer usuário
            final ListaFuncaoPerfilTodasEntidadesQuery funPerTodasEntidades = new ListaFuncaoPerfilTodasEntidadesQuery();
            funPerTodasEntidades.funCodigo = funCodigo;
            final List<TransferObject> fpeEntidades = funPerTodasEntidades.executarDTO();
            if (fpeEntidades != null) {
                for (final TransferObject fpe : fpeEntidades) {
                    final String codigoEntidade = fpe.getAttribute("CODIGO_ENTIDADE").toString();
                    final String tipoEntidade = fpe.getAttribute("TIPO_ENTIDADE").toString();
                    final String papel = fpe.getAttribute("PAPEL").toString();
                    final String usuCodigo = fpe.getAttribute(Columns.USU_CODIGO).toString();

                    if ((papCodigos == null) || papCodigos.isEmpty() || !papCodigos.contains(papel)) {
                        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                            final FuncaoPerfilCseId id = new FuncaoPerfilCseId();
                            id.setCseCodigo(codigoEntidade);
                            id.setFunCodigo(funCodigo);
                            id.setUsuCodigo(usuCodigo);

                            final FuncaoPerfilCse fp = new FuncaoPerfilCse();
                            fp.setFuncao(funcao);
                            fp.setId(id);

                            AbstractEntityHome.remove(fp);
                        } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
                            final FuncaoPerfilOrgId id = new FuncaoPerfilOrgId();
                            id.setOrgCodigo(codigoEntidade);
                            id.setFunCodigo(funCodigo);
                            id.setUsuCodigo(usuCodigo);

                            final FuncaoPerfilOrg fp = new FuncaoPerfilOrg();
                            fp.setFuncao(funcao);
                            fp.setId(id);

                            AbstractEntityHome.remove(fp);
                        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                            final FuncaoPerfilCsaId id = new FuncaoPerfilCsaId();
                            id.setCsaCodigo(codigoEntidade);
                            id.setFunCodigo(funCodigo);
                            id.setUsuCodigo(usuCodigo);

                            final FuncaoPerfilCsa fp = new FuncaoPerfilCsa();
                            fp.setFuncao(funcao);
                            fp.setId(id);

                            AbstractEntityHome.remove(fp);
                        } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                            final FuncaoPerfilCorId id = new FuncaoPerfilCorId();
                            id.setCorCodigo(codigoEntidade);
                            id.setFunCodigo(funCodigo);
                            id.setUsuCodigo(usuCodigo);

                            final FuncaoPerfilCor fp = new FuncaoPerfilCor();
                            fp.setFuncao(funcao);
                            fp.setId(id);

                            AbstractEntityHome.remove(fp);
                        } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                            final FuncaoPerfilSupId id = new FuncaoPerfilSupId();
                            id.setCseCodigo(codigoEntidade);
                            id.setFunCodigo(funCodigo);
                            id.setUsuCodigo(usuCodigo);

                            final FuncaoPerfilSup fp = new FuncaoPerfilSup();
                            fp.setFuncao(funcao);
                            fp.setId(id);

                            AbstractEntityHome.remove(fp);
                        }
                        log.setUsuario(usuCodigo);
                        log.write();
                    }
                }
            }

            // Exclui todos os vínculos da função com qualquer perfil
            final ListaPerfilTodasEntidadesPossuemFuncaoQuery lstPerfilFuncaoTodasEntidades = new ListaPerfilTodasEntidadesPossuemFuncaoQuery();
            lstPerfilFuncaoTodasEntidades.funCodigo = funCodigo;
            final List<TransferObject> funcoesPerfil = lstPerfilFuncaoTodasEntidades.executarDTO();
            if (funcoesPerfil != null) {
                for (final TransferObject to : funcoesPerfil) {
                    final String perCodigo = to.getAttribute(Columns.PER_CODIGO).toString();
                    final String papel = to.getAttribute("PAPEL").toString();

                    if ((papCodigos == null) || papCodigos.isEmpty() || !papCodigos.contains(papel)) {
                        final FuncaoPerfilId id = new FuncaoPerfilId();
                        id.setFunCodigo(funCodigo);
                        id.setPerCodigo(perCodigo);

                        final FuncaoPerfil fpe = new FuncaoPerfil();
                        fpe.setFuncao(funcao);
                        fpe.setId(id);

                        AbstractEntityHome.remove(fpe);

                        log.setPerfil(perCodigo);
                        log.setFuncao(funCodigo);
                        log.write();
                    }
                }
            }
        } catch (HQueryException | FindException | RemoveException | LogControllerException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.excluir.permissoes.usuario.erro.interno.arg0", responsavel, e.getMessage());
        }
    }

    private List<String> removeFuncaoPerfilUsuario(List<String> drop, String usuCodigo, String tipo) throws UsuarioControllerException {
        tipo = tipo.toUpperCase();
        final List<String> listaFuncoes = new ArrayList<>();
        try {
            final Collection<?> funcoes = findFuncaoPerfilBean(usuCodigo, tipo);
            final Iterator<?> it = funcoes.iterator();
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                while (it.hasNext()) {
                    final FuncaoPerfilCse fp = (FuncaoPerfilCse) it.next();
                    if ((drop == null) || drop.contains(fp.getFunCodigo())) {
                        AbstractEntityHome.remove(fp);
                        listaFuncoes.add(fp.getFunCodigo());
                    }
                }
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                while (it.hasNext()) {
                    final FuncaoPerfilCsa fp = (FuncaoPerfilCsa) it.next();
                    if ((drop == null) || drop.contains(fp.getFunCodigo())) {
                        AbstractEntityHome.remove(fp);
                        listaFuncoes.add(fp.getFunCodigo());
                    }
                }
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                while (it.hasNext()) {
                    final FuncaoPerfilCor fp = (FuncaoPerfilCor) it.next();
                    if ((drop == null) || drop.contains(fp.getFunCodigo())) {
                        AbstractEntityHome.remove(fp);
                        listaFuncoes.add(fp.getFunCodigo());
                    }
                }
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                while (it.hasNext()) {
                    final FuncaoPerfilOrg fp = (FuncaoPerfilOrg) it.next();
                    if ((drop == null) || drop.contains(fp.getFunCodigo())) {
                        AbstractEntityHome.remove(fp);
                        listaFuncoes.add(fp.getFunCodigo());
                    }
                }
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                while (it.hasNext()) {
                    final FuncaoPerfilSup fp = (FuncaoPerfilSup) it.next();
                    if ((drop == null) || drop.contains(fp.getFunCodigo())) {
                        AbstractEntityHome.remove(fp);
                        listaFuncoes.add(fp.getFunCodigo());
                    }
                }
            }
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.excluir.usuario.selecionado.erro.interno.arg0", (AcessoSistema) null, ex.getMessage());
        }
        return listaFuncoes;
    }

    /** LISTAGEM DE USUÁRIOS --------------------------------------------------------------------------------- **/

    // Get (lst_usuario_papel)
    @Override
    public List<TransferObject> getUsuarios(String tipo, String codigo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstUsuarios("0", tipo, codigo, filtro, offset, count, responsavel);
    }

    // List (lst_usuario)
    @Override
    public List<TransferObject> listUsuarios(String tipo, String codigo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstUsuarios("1", tipo, codigo, filtro, offset, count, responsavel);
    }

    @Override
    public List<TransferObject> lstUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstUsuarios(opcao, tipo, codigoEntidade, filtro, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE) && !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA) && !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR) && !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG) && !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP)) {
            return new ArrayList<>();
        }

        // somente usuários de suporte podem listar usuários de papel suporte
        if (AcessoSistema.ENTIDADE_SUP.equals(tipo) && !responsavel.isSup()) {
            return new ArrayList<>();
        }

        try {
            if ("1".equals(opcao)) {
                final ListaUsuariosQuery query = new ListaUsuariosQuery();
                query.responsavel = responsavel;

                if (offset != -1) {
                    query.firstResult = offset;
                }

                if (count != -1) {
                    query.maxResults = count;
                }

                query.tipo = tipo;
                query.entCodigo = codigoEntidade;

                if (filtro != null) {
                    query.cseNome = (String) filtro.getAttribute(Columns.CSE_NOME);
                    query.usuCodigo = (String) filtro.getAttribute(Columns.USU_CODIGO);
                    query.usuLogin = (String) filtro.getAttribute(Columns.USU_LOGIN);
                    query.usuNome = (String) filtro.getAttribute(Columns.USU_NOME);
                    query.usuCpf = (String) filtro.getAttribute(Columns.USU_CPF);
                    query.stuCodigo = filtro.getAttribute(Columns.USU_STU_CODIGO);
                    query.orgNome = (String) filtro.getAttribute(Columns.ORG_NOME);
                    query.csaNome = (String) filtro.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.csaNomeAbrev = (String) filtro.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.corNome = (String) filtro.getAttribute(Columns.COR_NOME);
                    query.orgNome = (String) filtro.getAttribute(Columns.ORG_NOME);
                    query.usuCseUsuCodigo = (String) filtro.getAttribute(Columns.UCE_USU_CODIGO);
                    query.usuCsaUsuCodigo = (String) filtro.getAttribute(Columns.UCA_USU_CODIGO);
                    query.usuCorUsuCodigo = (String) filtro.getAttribute(Columns.UCO_USU_CODIGO);
                    query.usuOrgUsuCodigo = (String) filtro.getAttribute(Columns.UOR_USU_CODIGO);
                    if (filtro.getAttribute("OCULTA_USU_VISIVEL") != null) {
                        query.ocultaUsuVisivel = (Boolean) filtro.getAttribute("OCULTA_USU_VISIVEL");
                    }
                }

                return query.executarDTO();
            } else {
                final ListaUsuariosEntidadeQuery query = new ListaUsuariosEntidadeQuery();
                if (offset != -1) {
                    query.firstResult = offset;
                }

                if (count != -1) {
                    query.maxResults = count;
                }

                query.entCodigo = codigoEntidade;
                query.tipo = tipo;
                query.perCodigo = (String) filtro.getAttribute(Columns.PER_CODIGO);
                query.perDescricao = (String) filtro.getAttribute(Columns.PER_DESCRICAO);
                query.usuCodigo = (String) filtro.getAttribute(Columns.USU_CODIGO);
                query.usuLogin = (String) filtro.getAttribute(Columns.USU_LOGIN);
                query.usuNome = (String) filtro.getAttribute(Columns.USU_NOME);
                query.usuCpf = (String) filtro.getAttribute(Columns.USU_CPF);
                query.stuCodigo = filtro.getAttribute(Columns.USU_STU_CODIGO);
                query.usuEmail = (String) filtro.getAttribute(Columns.USU_EMAIL);
                if (filtro.getAttribute("OCULTA_USU_VISIVEL") != null) {
                    query.ocultaUsuVisivel = (Boolean) filtro.getAttribute("OCULTA_USU_VISIVEL");
                }

                return query.executarDTO();
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public int countUsuarios(String tipo, String codigo, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        return countUsuarios("0", tipo, codigo, filtro, responsavel);
    }

    @Override
    public int listCountUsuarios(String tipo, String codigo, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        return countUsuarios("1", tipo, codigo, filtro, responsavel);
    }

    @Override
    public int countUsuarios(String opcao, String tipo, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        if (!responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE) &&
            !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA) &&
            !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR) &&
            !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG) &&
            !responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP)) {

            return 0;
        }

        // somente usuários de suporte podem listar usuários de papel suporte
        if (AcessoSistema.ENTIDADE_SUP.equals(tipo) && !responsavel.isSup()) {
            return 0;
        }

        try {
            if ("1".equals(opcao)) {
                final ListaUsuariosQuery query = new ListaUsuariosQuery();
                query.responsavel = responsavel;

                query.count = true;
                query.tipo = tipo;
                query.entCodigo = codigoEntidade;

                if (filtro != null) {
                    query.cseNome = (String) filtro.getAttribute(Columns.CSE_NOME);
                    query.usuCodigo = (String) filtro.getAttribute(Columns.USU_CODIGO);
                    query.usuLogin = (String) filtro.getAttribute(Columns.USU_LOGIN);
                    query.usuNome = (String) filtro.getAttribute(Columns.USU_NOME);
                    query.usuCpf = (String) filtro.getAttribute(Columns.USU_CPF);
                    query.stuCodigo = filtro.getAttribute(Columns.USU_STU_CODIGO);
                    query.orgNome = (String) filtro.getAttribute(Columns.ORG_NOME);
                    query.csaNome = (String) filtro.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.csaNomeAbrev = (String) filtro.getAttribute(Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV);
                    query.corNome = (String) filtro.getAttribute(Columns.COR_NOME);
                    query.orgNome = (String) filtro.getAttribute(Columns.ORG_NOME);
                    query.usuCseUsuCodigo = (String) filtro.getAttribute(Columns.UCE_USU_CODIGO);
                    query.usuCsaUsuCodigo = (String) filtro.getAttribute(Columns.UCA_USU_CODIGO);
                    query.usuCorUsuCodigo = (String) filtro.getAttribute(Columns.UCO_USU_CODIGO);
                    query.usuOrgUsuCodigo = (String) filtro.getAttribute(Columns.UOR_USU_CODIGO);
                    if (filtro.getAttribute("OCULTA_USU_VISIVEL") != null) {
                        query.ocultaUsuVisivel = (Boolean) filtro.getAttribute("OCULTA_USU_VISIVEL");
                    }
                }

                return query.executarContador();
            } else {
                final ListaUsuariosEntidadeQuery query = new ListaUsuariosEntidadeQuery();
                query.entCodigo = codigoEntidade;
                query.tipo = tipo;
                query.perCodigo = (String) filtro.getAttribute(Columns.PER_CODIGO);
                query.perDescricao = (String) filtro.getAttribute(Columns.PER_DESCRICAO);
                query.usuCodigo = (String) filtro.getAttribute(Columns.USU_CODIGO);
                query.usuLogin = (String) filtro.getAttribute(Columns.USU_LOGIN);
                query.usuNome = (String) filtro.getAttribute(Columns.USU_NOME);
                query.usuCpf = (String) filtro.getAttribute(Columns.USU_CPF);
                query.stuCodigo = filtro.getAttribute(Columns.USU_STU_CODIGO);
                if (filtro.getAttribute("OCULTA_USU_VISIVEL") != null) {
                    query.ocultaUsuVisivel = (Boolean) filtro.getAttribute("OCULTA_USU_VISIVEL");
                }
                query.count = true;

                return query.executarContador();
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /** LISTAGEM DE USUÁRIOS SERVIDORES ---------------------------------------------------------------------- **/

    @Override
    public List<TransferObject> lstUsuariosSerByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuariosSerQuery query = new ListaUsuariosSerQuery();
            query.rseCodigo = rseCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstUsuariosSerByRseCodigos(List<String> rseCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuariosSerRseQuery query = new ListaUsuariosSerRseQuery();
            query.rseCodigos = rseCodigos;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstUsuariosSer(serCpf, rseMatricula, estIdentificador, orgIdentificador, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstUsuariosSer(String serCpf, String rseMatricula, String estIdentificador, String orgIdentificador, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuariosSerQuery query = new ListaUsuariosSerQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            query.serCpf = serCpf;
            query.rseMatricula = rseMatricula;
            query.estIdentificador = estIdentificador;
            query.orgIdentificador = orgIdentificador;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     *
     * @param usuLogin
     * @param rseMatricula
     * @param serCpf
     * @param estIdentificador
     * @param orgIdentificador
     * @param serSomenteAtivo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> lstUsuariosSerLoginComCpf(String usuLogin, String rseMatricula, String serCpf, String estIdentificador, String orgIdentificador, boolean serSomenteAtivo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuariosServidorLoginQuery listaUsuariosServidorLoginQuery = new ListaUsuariosServidorLoginQuery();
            listaUsuariosServidorLoginQuery.usuLogin = usuLogin;
            listaUsuariosServidorLoginQuery.rseMatricula = rseMatricula;
            listaUsuariosServidorLoginQuery.serCpf = serCpf;
            listaUsuariosServidorLoginQuery.estIdentificador = estIdentificador;
            listaUsuariosServidorLoginQuery.orgIdentificador = orgIdentificador;
            listaUsuariosServidorLoginQuery.serSomenteAtivo = serSomenteAtivo;

            return listaUsuariosServidorLoginQuery.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Retorna as funções associadas ao perfil personalizado do usuário, porém
     * somente as funções que podem ser repassadas do usuário responsavel para
     * o usuário usuCodigo.
     * @param usuCodigo : usuário que está sendo editado
     * @param tipo : tipo do usuário que está sendo editado
     * @param responsavel : responsável pela edição do usuário
     * @return A lista de funCodigos
     * @throws UsuarioControllerException
     */
    @Override
    public List<String> getUsuarioFuncoes(String usuCodigo, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                // DESENV-15096 : usuário servidor não tem mais perfil personalizado
                return new ArrayList<>();
            }

            final ListaFuncoesUsuarioQuery query = new ListaFuncoesUsuarioQuery();

            query.tipo = tipo;
            query.usuCodigo = usuCodigo;
            query.papCodigoDestino = UsuarioHelper.getPapCodigo(tipo);
            query.papCodigoOrigem = UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade());

            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public Funcao findFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            return FuncaoHome.findByPrimaryKey(funCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public String createFuncao(String grfCodigo, String funDescricao, String funPermiteBloqueio, String funExigeTmo, String funExigeSegundaSenhaCse, String funExigeSegundaSenhaSup, String funExigeSegundaSenhaOrg, String funExigeSegundaSenhaCsa, String funExigeSegundaSenhaCor, String funExigeSegundaSenhaSer, String funAuditavel, List<String> papCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final Funcao funcao = FuncaoHome.create(grfCodigo, funDescricao, funPermiteBloqueio, funExigeTmo, funExigeSegundaSenhaCse, funExigeSegundaSenhaSup, funExigeSegundaSenhaOrg, funExigeSegundaSenhaCsa, funExigeSegundaSenhaCor, funExigeSegundaSenhaSer, funAuditavel);
            final String funCodigo = funcao.getFunCodigo();

            createPapelFuncao(papCodigos, funCodigo, responsavel);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.FUNCAO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setFuncao(funCodigo);
            logDelegate.setGrupoFuncao(grfCodigo);
            logDelegate.write();

            return funCodigo;
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    private void createPapelFuncao(List<String> papCodigos, String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if ((papCodigos != null) && !papCodigos.isEmpty()) {
                for (final String papCodigo : papCodigos) {
                    PapelFuncaoHome.create(funCodigo, papCodigo);
                }
            }
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public void updateFuncao(TransferObject funcaoTO, List<String> papCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String funCodigo = funcaoTO.getAttribute(Columns.FUN_CODIGO).toString();

            final Funcao funcao = FuncaoHome.findByPrimaryKey(funCodigo);
            final TransferObject cache = new CustomTransferObject();
            cache.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
            cache.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
            cache.setAttribute(Columns.FUN_AUDITAVEL, funcao.getFunAuditavel());
            cache.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE, funcao.getFunExigeSegundaSenhaCse());
            cache.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP, funcao.getFunExigeSegundaSenhaSup());
            cache.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG, funcao.getFunExigeSegundaSenhaOrg());
            cache.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA, funcao.getFunExigeSegundaSenhaCsa());
            cache.setAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR, funcao.getFunExigeSegundaSenhaCor());
            cache.setAttribute(Columns.FUN_EXIGE_TMO, funcao.getFunExigeTmo());
            cache.setAttribute(Columns.FUN_GRF_CODIGO, funcao.getGrupoFuncao().getGrfCodigo());
            cache.setAttribute(Columns.FUN_PERMITE_BLOQUEIO, funcao.getFunPermiteBloqueio());

            final LogDelegate log = new LogDelegate(responsavel, Log.FUNCAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setFuncao(funCodigo);

            /* Compara a versão do cache com a passada por parâmetro */
            final CustomTransferObject merge = log.getUpdatedFields(funcaoTO.getAtributos(), cache.getAtributos());
            if (merge.getAtributos().containsKey(Columns.FUN_DESCRICAO)) {
                funcao.setFunDescricao((String) merge.getAttribute(Columns.FUN_DESCRICAO));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_AUDITAVEL)) {
                funcao.setFunAuditavel((String) merge.getAttribute(Columns.FUN_AUDITAVEL));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE)) {
                funcao.setFunExigeSegundaSenhaCse((String) merge.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSE));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP)) {
                funcao.setFunExigeSegundaSenhaSup((String) merge.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_SUP));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG)) {
                funcao.setFunExigeSegundaSenhaOrg((String) merge.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_ORG));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA)) {
                funcao.setFunExigeSegundaSenhaCsa((String) merge.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_CSA));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR)) {
                funcao.setFunExigeSegundaSenhaCor((String) merge.getAttribute(Columns.FUN_EXIGE_SEGUNDA_SENHA_COR));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_EXIGE_TMO)) {
                funcao.setFunExigeTmo((String) merge.getAttribute(Columns.FUN_EXIGE_TMO));
            }
            if (merge.getAtributos().containsKey(Columns.FUN_GRF_CODIGO)) {
                funcao.setGrupoFuncao(GrupoFuncaoHome.findByPrimaryKey((String) merge.getAttribute(Columns.FUN_GRF_CODIGO)));
                if (!TextHelper.isNull(merge.getAttribute(Columns.FUN_GRF_CODIGO))) {
                    log.setGrupoFuncao((String) merge.getAttribute(Columns.FUN_GRF_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.FUN_PERMITE_BLOQUEIO)) {
                funcao.setFunPermiteBloqueio((String) merge.getAttribute(Columns.FUN_PERMITE_BLOQUEIO));
            }
            AbstractEntityHome.update(funcao);

            if ((papCodigos != null) && !papCodigos.isEmpty()) {
                // Remove os papeis função antigos e insere novos papeis para a função
                removePapelFuncao(funCodigo, responsavel);
                createPapelFuncao(papCodigos, funCodigo, responsavel);

                removeVinculoFuncao(funCodigo, papCodigos, responsavel);
            }

        } catch (FindException | UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(e);
        } catch (final LogControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new UsuarioControllerException(e);
        }
    }

    @Override
    public void removeFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final Funcao funcao = FuncaoHome.findByPrimaryKey(funCodigo);

            removeVinculoFuncao(funCodigo, null, responsavel);

            removePapelFuncao(funCodigo, responsavel);

            AbstractEntityHome.remove(funcao);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.FUNCAO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setFuncao(funCodigo);
            logDelegate.write();

        } catch (FindException | RemoveException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(e);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    private void removePapelFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final Collection<PapelFuncao> papeis = PapelFuncaoHome.findByFunCodigo(funCodigo);
            for (final PapelFuncao papel : papeis) {
                AbstractEntityHome.remove(papel);
            }
        } catch (FindException | RemoveException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(e);
        }
    }

    @Override
    public int countFuncao(TransferObject criterio, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListarFuncaoQuery query = new ListarFuncaoQuery();
            query.count = true;

            if ((criterio != null) && !TextHelper.isNull(criterio.getAttribute(Columns.FUN_DESCRICAO))) {
                query.funDescricao = criterio.getAttribute(Columns.FUN_DESCRICAO).toString();
            }

            if ((criterio != null) && !TextHelper.isNull(criterio.getAttribute(Columns.GRF_DESCRICAO))) {
                query.grfDescricao = criterio.getAttribute(Columns.GRF_DESCRICAO).toString();
            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listFuncao(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListarFuncaoQuery query = new ListarFuncaoQuery();

            if ((criterio != null) && !TextHelper.isNull(criterio.getAttribute(Columns.FUN_DESCRICAO))) {
                query.funDescricao = criterio.getAttribute(Columns.FUN_DESCRICAO).toString();
            }

            if ((criterio != null) && !TextHelper.isNull(criterio.getAttribute(Columns.GRF_DESCRICAO))) {
                query.grfDescricao = criterio.getAttribute(Columns.GRF_DESCRICAO).toString();
            }

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (size != -1) {
                query.maxResults = size;
            }

            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Retorna um Mapa com as funções associadas ao perfil do usuário (tanto personalizado
     * quanto perfil fixo). Todas as funções são retornadas independente de bloqueio de repasse.
     * @param usuCodigo
     * @param entidade
     * @param tipo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */

    @Override
    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        return selectFuncoes(usuCodigo, entidade, tipo, null, -1, -1, responsavel);
    }

    /**
     * Retorna um Mapa com as funções associadas ao perfil do usuário (tanto personalizado
     * quanto perfil fixo). Todas as funções são retornadas independente de bloqueio de repasse.
     * @param usuCodigo
     * @param entidade
     * @param tipo
     * @param filtro
     * @param offset
     * @param count
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public Map<String, EnderecoFuncaoTransferObject> selectFuncoes(String usuCodigo, String entidade, String tipo, CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        List<EnderecoFuncaoTransferObject> listResult = null;
        final Map<String, EnderecoFuncaoTransferObject> mapResult = new LinkedHashMap<>(); // Necessário manter ordenação para exibição
        String funCodigo = null, funDescricao = null;
        if ((filtro != null) && !filtro.getAtributos().isEmpty()) {
            funCodigo = (String) filtro.getAttribute(Columns.FUN_CODIGO);
            funDescricao = (String) filtro.getAttribute(Columns.FUN_DESCRICAO);
        }

        try {
            LOG.debug("usuCodigo: " + usuCodigo);

            try {
                if (TextHelper.isNull(usuCodigo)) {
                    throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
                }
                final PerfilUsuario upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);

                // Busca as funções do perfil
                final FuncoesPerfilQuery query = new FuncoesPerfilQuery();
                query.perCodigo = upeBean.getPerfil().getPerCodigo();
                query.usuCodigo = usuCodigo;
                query.funCodigo = funCodigo;
                query.funDescricao = funDescricao;
                if (offset != -1) {
                    query.firstResult = offset;
                }

                if (count != -1) {
                    query.maxResults = count;
                }
                listResult = query.executarDTO(EnderecoFuncaoTransferObject.class);

            } catch (final FindException e) {
                if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                    // DESENV-15096 : usuário servidor não tem mais perfil personalizado
                    listResult = new ArrayList<>();

                } else {
                    // Busca as funções p/ perfil personalizado
                    final FuncoesPersonalizadasQuery query = new FuncoesPersonalizadasQuery();
                    query.usuCodigo = usuCodigo;
                    query.entidade = entidade;
                    query.tipo = tipo;
                    query.funCodigo = funCodigo;
                    query.funDescricao = funDescricao;
                    if (offset != -1) {
                        query.firstResult = offset;
                    }

                    if (count != -1) {
                        query.maxResults = count;
                    }
                    listResult = query.executarDTO(EnderecoFuncaoTransferObject.class);
                }
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        // Cria um mapa para facilitar a manter a compatibilidade com a estrutura (Map<String, String>, onde a chave era funCodigo e o valor era funDescricao)
        // e métodos anteriores reduzindo o impacto da alteração.
        for (final EnderecoFuncaoTransferObject enderecoFuncaoTo : listResult) {
            mapResult.put((String) enderecoFuncaoTo.getAttribute(Columns.FUN_CODIGO), enderecoFuncaoTo);
        }
        return mapResult;
    }

    /**
     * Retorna um Mapa com as funções associadas ao perfil do usuário para retrição de acesso (tanto personalizado
     * quanto perfil fixo). Somente funções com acesso_recurso e papel associados
     * @param usuCodigo
     * @param entidade
     * @param tipo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public Map<String, String> selectFuncoesRestricaoAcesso(String usuCodigo, String entidade, String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        Map<String, String> result = null;
        try {
            LOG.debug("usuCodigo: " + usuCodigo);

            try {
                final PerfilUsuario upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);

                // Busca as funções do perfil
                final FuncoesPerfilRestricaoAcessoQuery query = new FuncoesPerfilRestricaoAcessoQuery();
                query.perCodigo = upeBean.getPerfil().getPerCodigo();
                query.papel = responsavel.getPapCodigo();
                result = query.executarMapa();

            } catch (final FindException e) {
                if (!TextHelper.isNull(tipo) && AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                    // DESENV-15096 : usuário servidor não tem mais perfil personalizado
                    return new HashMap<>();
                }

                // Busca as funções p/ perfil personalizado
                final FuncoesPersonalizadasRestricaoAcessoQuery query = new FuncoesPersonalizadasRestricaoAcessoQuery();
                query.usuCodigo = usuCodigo;
                query.entidade = entidade;
                query.tipo = tipo;
                query.papel = responsavel.getPapCodigo();
                result = query.executarMapa();
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        return result;
    }

    @Override
    public List<TransferObject> selectFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesPermitidasPapelQuery funcoesQuery = new ListaFuncoesPermitidasPapelQuery();
            funcoesQuery.papCodigoOrigem = UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade());
            funcoesQuery.papCodigoDestino = UsuarioHelper.getPapCodigo(tipo);
            return funcoesQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    /**
     * Recupera todas funções permitidas por natureza de consignatária
     * @param ncaCodigo
     * @return
     * @throws UsuarioControllerException
     */
    private List<String> selectFuncoesPermitidasNca(String ncaCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final List<String> resultado = new ArrayList<>();
            final ListaFuncoesPermitidasNcaQuery funcoesQuery = new ListaFuncoesPermitidasNcaQuery();
            funcoesQuery.ncaCodigo = ncaCodigo;
            final List<TransferObject> funcoes = funcoesQuery.executarDTO();
            for (final TransferObject funcao : funcoes) {
                resultado.add((String) funcao.getAttribute(Columns.FPN_FUN_CODIGO));
            }
            return resultado;
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<TransferObject> selectFuncoesSensiveisCsa(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            return new ListaFuncoesSensiveisCsaQuery().executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Lista as permissões de função por natureza de consignatária.
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> selectFuncoesPermitidasNca(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesPermitidasNcaQuery funcoesQuery = new ListaFuncoesPermitidasNcaQuery();
            return funcoesQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    /**
     * Recupera todos os papeis do sistema.
     *
     * @return Retorna uma lista com todos os papeis do sistema.
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> lstPapel(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaPapeisQuery query = new ListaPapeisQuery();
            return query.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    /** BLOQUEIO DE FUNÇÕES ---------------------------------------------------------------------------------- **/

    @Override
    public List<TransferObject> lstFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesQuery query = new ListaFuncoesQuery();
            query.tipo = tipo;
            return query.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public Map<String, String> getMapFuncoes(String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesQuery query = new ListaFuncoesQuery();
            query.tipo = tipo;
            return query.executarMapa();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<TransferObject> lstFuncoesBloqueaveis(String tipo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesBloqueaveisQuery funcoesBloqQuery = new ListaFuncoesBloqueaveisQuery();
            funcoesBloqQuery.tipo = tipo;
            return funcoesBloqQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<TransferObject> findFuncoesRegraTaxa(String funCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesRegraTaxaQuery funcoesRegraTaxa = new ListaFuncoesRegraTaxaQuery();
            funcoesRegraTaxa.funCodigos = funCodigos;
            return funcoesRegraTaxa.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> selectFuncoesBloqueadas(String usuario, AcessoSistema responsavel) throws UsuarioControllerException {
        return selectFuncoesBloqueadas(usuario, null, responsavel);
    }

    @Override
    public List<TransferObject> selectFuncoesBloqueadas(String usuario, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesBloqueadasQuery funcoesBloqueadasQuery = new ListaFuncoesBloqueadasQuery();
            funcoesBloqueadasQuery.usuCodigo = usuario;
            funcoesBloqueadasQuery.tipoEntidade = tipoEntidade;
            return funcoesBloqueadasQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public void insereBloqueiosFuncoes(String usuCodigo, List<TransferObject> bloqueios, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Remove as funções antigas bloqueadas
            final List<BloqueioUsuFunSvc> blocToRemove = BloqueioUsuFunSvcHome.findByUsuCodigo(usuCodigo);
            for (final BloqueioUsuFunSvc bloqueio : blocToRemove) {
                AbstractEntityHome.remove(bloqueio);

                final LogDelegate log = new LogDelegate(responsavel, Log.BLOQUEIO_FUNCAO_USU_SVC, Log.DELETE, Log.LOG_INFORMACAO);
                log.setUsuario(usuCodigo);
                log.setServico(bloqueio.getServico().getSvcCodigo());
                log.setFuncao(bloqueio.getFuncao().getFunCodigo());
                log.write();
            }

            // Insere as novas funções bloqueadas
            if ((bloqueios != null) && !bloqueios.isEmpty()) {
                for (final TransferObject bloqueio : bloqueios) {
                    final String svcCodigo = bloqueio.getAttribute(Columns.BUF_SVC_CODIGO).toString();
                    final String funCodigo = bloqueio.getAttribute(Columns.BUF_FUN_CODIGO).toString();
                    BloqueioUsuFunSvcHome.create(funCodigo, usuCodigo, svcCodigo);

                    final LogDelegate log = new LogDelegate(responsavel, Log.BLOQUEIO_FUNCAO_USU_SVC, Log.CREATE, Log.LOG_INFORMACAO);
                    log.setUsuario(usuCodigo);
                    log.setServico(svcCodigo);
                    log.setFuncao(funCodigo);
                    log.write();
                }
            }

            if (!blocToRemove.isEmpty() || ((bloqueios != null) && !bloqueios.isEmpty())) {
                // Cria ocorrência de alteração de perfil de usuário
                final CustomTransferObject ocorrencia = new CustomTransferObject();
                ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
                ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_ALTERACAO_PERFIL_USUARIO);
                ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
                ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.perfil.usuario", responsavel));
                ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

                createOcorrenciaUsuario(ocorrencia, responsavel);
            }
        } catch (CreateException | RemoveException | FindException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public boolean usuarioTemBloqueioFuncao(String usuCodigo, String funCodigo, String svcCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final CustomTransferObject funcao = getFuncao(funCodigo, responsavel);

        if (funcao == null) {
            return true;
        }

        if (funcao.getAttribute(Columns.FUN_PERMITE_BLOQUEIO) == null) {
            return false;
        }

        if (CodedValues.TPC_SIM.equals(funcao.getAttribute(Columns.FUN_PERMITE_BLOQUEIO).toString())) {
            final List<TransferObject> bloqueiosUsuario = selectFuncoesBloqueadas(usuCodigo, responsavel);
            for (final TransferObject bloqueio : bloqueiosUsuario) {
                if (bloqueio.getAttribute(Columns.BUF_USU_CODIGO).toString().equals(usuCodigo) && bloqueio.getAttribute(Columns.BUF_FUN_CODIGO).toString().equals(funCodigo) && bloqueio.getAttribute(Columns.BUF_SVC_CODIGO).toString().equals(svcCodigo)) {
                    return true;
                }
            }
        } else {
        }

        return false;
    }

    @Override
    public List<TransferObject> lstFuncaoExigeTmo(String funExigeTmo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemFuncaoQuery funQuery = new ObtemFuncaoQuery();
            funQuery.funExigeTmo = funExigeTmo;
            return funQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    /** EDIÇÃO DE PERFIL -------------------------------------------------------------------------------------- **/

    @Override
    public Perfil findPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            return PerfilHome.findByPrimaryKey(perCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        }
    }

    @Override
    public Short getPerfilStatus(String perCodigo, String tipo, String entCodigo, AcessoSistema responsavel) throws FindException, UsuarioControllerException {
        try {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                final PerfilCse perCse = PerfilCseHome.findByPrimaryKey(new PerfilCseId(entCodigo, perCodigo));
                return perCse.getPceAtivo();
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                final PerfilCsa perCsa = PerfilCsaHome.findByPrimaryKey(new PerfilCsaId(entCodigo, perCodigo));
                return perCsa.getPcaAtivo();
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                final PerfilOrg perOrg = PerfilOrgHome.findByPrimaryKey(new PerfilOrgId(entCodigo, perCodigo));
                return perOrg.getPorAtivo();
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                final PerfilCor perCor = PerfilCorHome.findByPrimaryKey(new PerfilCorId(entCodigo, perCodigo));
                return perCor.getPcoAtivo();
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                final PerfilSup perSup = PerfilSupHome.findByPrimaryKey(new PerfilSupId(entCodigo, perCodigo));
                return perSup.getPsuAtivo();
            } else {
                return null;
            }
        } catch (final FindException fex) {
            throw fex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.recuperar.perfil", responsavel);
        }
    }

    @Override
    public String createPerfil(String tipoEntidade, String codigoEntidade, String perDescricao, String perVisivel, java.util.Date perDataExpiracao, String perEntAltera, String perOrigem, String perAutoDesbloqueio, List<String> funCodigo, String perIpAcesso, String perDdnsAcesso, AcessoSistema responsavel) throws UsuarioControllerException {
        String perCodigo = null;
        try {
            final String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);
            final Short status = CodedValues.STS_ATIVO;
            if (TextHelper.isNull(perEntAltera)) {
                perEntAltera = CodedValues.TPA_SIM;
            }

            if (TextHelper.isNull(perAutoDesbloqueio)) {
                perAutoDesbloqueio = CodedValues.TPA_NAO;
            }

            // Cria o perfil
            final Perfil perBean = PerfilHome.create(papCodigo, perDescricao, perVisivel, perDataExpiracao, perAutoDesbloqueio, perEntAltera, perIpAcesso, perDdnsAcesso );
            perCodigo = perBean.getPerCodigo();

            // Se for passado um perfil de origem, as funções do perfil de origem serão replicadas para o novo perfil
            if (!TextHelper.isNull(perOrigem)) {
                funCodigo = new ArrayList<>();
                final Collection<FuncaoPerfil> funcoesPerfil = FuncaoPerfilHome.findByPerfil(perOrigem);
                if ((funcoesPerfil != null) && !funcoesPerfil.isEmpty()) {
                    for (final FuncaoPerfil funcaoPerfil : funcoesPerfil) {
                        funCodigo.add(funcaoPerfil.getFunCodigo());
                    }
                }
            }

            // Cria as funções para o perfil
            updateFuncaoPerfil(tipoEntidade, codigoEntidade, perCodigo, funCodigo, responsavel);

            // Associa o perfil a sua entidade
            if (CodedValues.PAP_CONSIGNANTE.equals(papCodigo)) { // CSE
                PerfilCseHome.create(codigoEntidade, perCodigo, status);
            } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigo)) { // CSA
                PerfilCsaHome.create(codigoEntidade, perCodigo, status);
            } else if (CodedValues.PAP_ORGAO.equals(papCodigo)) { // ORG
                PerfilOrgHome.create(codigoEntidade, perCodigo, status);
            } else if (CodedValues.PAP_CORRESPONDENTE.equals(papCodigo)) { // COR
                PerfilCorHome.create(codigoEntidade, perCodigo, status);
            } else if (CodedValues.PAP_SUPORTE.equals(papCodigo)) { // SUP
                PerfilSupHome.create(codigoEntidade, perCodigo, status);
            } else {
                throw new UsuarioControllerException("mensagem.erro.arg0.tipo.entidade.invalido", responsavel, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
            }

            // Verifica se o responsável pode modificar este perfil
            usuarioPodeModificarPerfil(perCodigo, false, true, responsavel);

            // Gera o log de auditoria
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERFIL, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setPerfil(perCodigo);
            if (!TextHelper.isNull(perOrigem)) {
                logDelegate.setPerfilOrigem(perOrigem);
                logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.perfil.criado.a.partir.das.funcoes.associadas.perfil.origem", responsavel));
            }
            logDelegate.write();

            if ((funCodigo != null) && funCodigo.isEmpty()) {
                criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.criado", responsavel), null, responsavel);
            }

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.interno.do.sistema.ao.criar.perfil", responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            if (ex instanceof UsuarioControllerException) {
                throw (UsuarioControllerException) ex;
            } else {
                throw new UsuarioControllerException("mensagem.erro.interno.do.sistema.ao.criar.perfil", responsavel);
            }
        }

        return perCodigo;
    }

    @Override
    public void updatePerfil(String tipoEntidade, String codigoEntidade, String perCodigo, String perDescricao, String perVisivel, java.util.Date perDataExpiracao, String perEntAltera, Short status, String perAutDesbloqueio, String perIpAcesso, String perDdnsAcesso, List<String> funcoes, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (usuarioPodeModificarPerfil(perCodigo, true, true, responsavel)) {
                final String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);

                // Busca o perfil
                final Perfil perBean = PerfilHome.findByPrimaryKey(perCodigo);
                boolean alterou = false;

                // Cria log de edição do perfil
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERFIL, Log.UPDATE, Log.LOG_INFORMACAO);
                logDelegate.setPerfil(perCodigo);

                // Altera a descrição do perfil
                if ((perDescricao != null) && !perDescricao.equals(perBean.getPerDescricao())) {
                    logDelegate.addChangedField(Columns.PER_DESCRICAO, perDescricao, perBean.getPerDescricao());
                    perBean.setPerDescricao(perDescricao);
                    alterou = true;
                }
                if ((perVisivel != null) && !perVisivel.equals(perBean.getPerVisivel())) {
                    logDelegate.addChangedField(Columns.PER_VISIVEL, perVisivel, perBean.getPerVisivel());
                    perBean.setPerVisivel(perVisivel);
                    alterou = true;
                }
                if (((perDataExpiracao != null) && (perBean.getPerDataExpiracao() != null) && (perBean.getPerDataExpiracao().compareTo(perDataExpiracao) != 0)) || (TextHelper.isNull(perDataExpiracao) && (perBean.getPerDataExpiracao() != null)) || (!TextHelper.isNull(perDataExpiracao) && TextHelper.isNull(perBean.getPerDataExpiracao()))) {
                    logDelegate.addChangedField(Columns.PER_DATA_EXPIRACAO, perDataExpiracao, perBean.getPerDataExpiracao());
                    perBean.setPerDataExpiracao(perDataExpiracao);
                    alterou = true;
                }

                if ((perAutDesbloqueio != null) && (perBean.getPerAutoDesbloqueio() != null) && (perBean.getPerAutoDesbloqueio().compareTo(perAutDesbloqueio) != 0)) {
                    logDelegate.addChangedField(Columns.PER_AUTO_DESBLOQUEIO, perAutDesbloqueio, perBean.getPerAutoDesbloqueio());
                    perBean.setPerAutoDesbloqueio(perAutDesbloqueio);
                    alterou = true;
                }

                if ((perEntAltera != null) && !perEntAltera.equals(perBean.getPerEntAltera())) {
                    perBean.setPerEntAltera(perEntAltera);
                    alterou = true;
                }

                //Altera ip do perfil.
                if((perIpAcesso != null) && !perIpAcesso.equals(perBean.getPerIpAcesso())) {
                    perBean.setPerIpAcesso(perIpAcesso);
                    alterou = true;
                }

                //Altera ddns do perfil.
                if((perDdnsAcesso != null) && !perDdnsAcesso.equals(perBean.getPerDdnsAcesso())) {
                    perBean.setPerDdnsAcesso(perDdnsAcesso);
                    alterou = true;
                }

                if (alterou) {
                    AbstractEntityHome.update(perBean);
                }

                // Altera o status do perfil nas tabelas de entidades
                if (status != null) {
                    if (!TextHelper.isNull(papCodigo)) {
                        if (CodedValues.PAP_CONSIGNANTE.equals(papCodigo)) { // CSE
                            final PerfilCse pceBean = PerfilCseHome.findByPrimaryKey(new PerfilCseId(codigoEntidade, perCodigo));
                            pceBean.setPceAtivo(status);
                            AbstractEntityHome.update(pceBean);
                            logDelegate.addChangedField(Columns.PCE_ATIVO, status);
                        } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigo)) { // CSA
                            final PerfilCsa pcaBean = PerfilCsaHome.findByPrimaryKey(new PerfilCsaId(codigoEntidade, perCodigo));
                            pcaBean.setPcaAtivo(status);
                            AbstractEntityHome.update(pcaBean);
                            logDelegate.addChangedField(Columns.PCA_ATIVO, status);
                        } else if (CodedValues.PAP_ORGAO.equals(papCodigo)) { // ORG
                            final PerfilOrg porBean = PerfilOrgHome.findByPrimaryKey(new PerfilOrgId(codigoEntidade, perCodigo));
                            porBean.setPorAtivo(status);
                            AbstractEntityHome.update(porBean);
                            logDelegate.addChangedField(Columns.POR_ATIVO, status);
                        } else if (CodedValues.PAP_CORRESPONDENTE.equals(papCodigo)) { // COR
                            final PerfilCor pcoBean = PerfilCorHome.findByPrimaryKey(new PerfilCorId(codigoEntidade, perCodigo));
                            pcoBean.setPcoAtivo(status);
                            AbstractEntityHome.update(pcoBean);
                            logDelegate.addChangedField(Columns.PCO_ATIVO, status);
                        } else if (CodedValues.PAP_SUPORTE.equals(papCodigo)) { // SUP
                            final PerfilSup psuBean = PerfilSupHome.findByPrimaryKey(new PerfilSupId(codigoEntidade, perCodigo));
                            psuBean.setPsuAtivo(status);
                            AbstractEntityHome.update(psuBean);
                            logDelegate.addChangedField(Columns.PSU_ATIVO, status);
                        } else {
                            throw new UsuarioControllerException("mensagem.erro.arg0.tipo.entidade.invalido", responsavel, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                        }
                    }
                    if (status.equals(CodedValues.STS_ATIVO)) {
                        criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.desbloqueio", responsavel), null, responsavel);
                    } else {
                        criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.bloqueio", responsavel), null, responsavel);
                    }
                }

                // Atualiza as funções do perfil
                final boolean funcoesAlteradas = updateFuncaoPerfil(tipoEntidade, codigoEntidade, perCodigo, funcoes, responsavel);

                // Atualiza o log
                logDelegate.write();

                if ((funcoes != null) && funcoes.isEmpty()) {
                    criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.alterado", responsavel), null, responsavel);
                }

                if (funcoesAlteradas) {
                    // Notifica as entidades da alteração do perfil de usuário
                    EnviaEmailHelper.enviarEmailAlteracaoPerfil(perCodigo, tipoEntidade, codigoEntidade, responsavel);
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.atualizar.este.perfil.erro.interno.arg0", responsavel, ex.getMessage());
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private boolean updateFuncaoPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, List<String> funcoes, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Altera as funções deste perfil
            if (funcoes != null) {
                final List<String> funcoesNovas = new ArrayList<>();
                final List<String> funcoesRemovidas = new ArrayList<>();

                FuncaoPerfil fperBean = null;

                // Navega nas funções antigas deste perfil
                final List<String> funcoesOld = getFuncaoPerfil(tipoEntidade, codigoEntidade, perCodigo, responsavel);

                // Funções permitidas para o usuário
                final List<String> funcoesPermitidas = new ArrayList<>();
                final List<TransferObject> funPermUsu = lstFuncoesPermitidasPerfil(tipoEntidade, codigoEntidade, responsavel);
                if ((funPermUsu != null) && !funPermUsu.isEmpty()) {
                    for (final TransferObject transferObject : funPermUsu) {
                        funcoesPermitidas.add(transferObject.getAttribute(Columns.FUN_CODIGO).toString());
                    }
                }

                if (funcoes != null) {
                    for (final String funPermitida : funcoesPermitidas) {
                        if (funcoesOld.contains(funPermitida) && !funcoes.contains(funPermitida)) {
                            fperBean = FuncaoPerfilHome.findByPrimaryKey(new FuncaoPerfilId(funPermitida, perCodigo));

                            // se a função a ser removida é a de usuário auditor, verifica se os usuários do perfil
                            // são os únicos auditores do sistema. caso sim, não é permitido remover esta permissão
                            if (CodedValues.FUN_USUARIO_AUDITOR.equals(funPermitida)) {
                                final ListaUsuariosAuditoresQuery lstAuditores = new ListaUsuariosAuditoresQuery();
                                final List<String> perCodigos = new ArrayList<>();
                                perCodigos.add(CodedValues.NOT_EQUAL_KEY);
                                perCodigos.add(perCodigo);
                                lstAuditores.perCodigo = perCodigos;
                                lstAuditores.codigoEntidade = codigoEntidade;
                                lstAuditores.tipo = tipoEntidade;
                                lstAuditores.count = true;

                                try {
                                    final int auditoresQntd = lstAuditores.executarContador();
                                    if (auditoresQntd <= 0) {
                                        final ListaFuncoesAuditadasQuery funcAuditadasEnt = new ListaFuncoesAuditadasQuery();
                                        funcAuditadasEnt.codigoEntidade = codigoEntidade;
                                        funcAuditadasEnt.tipo = tipoEntidade;
                                        funcAuditadasEnt.count = true;

                                        final int numFuncAuditadas = funcAuditadasEnt.executarContador();
                                        if (numFuncAuditadas > 0) {
                                            throw new UsuarioControllerException("mensagem.erro.permissao.auditoria.nao.pode.remover", responsavel);
                                        }
                                    }
                                } catch (final HQueryException e) {
                                    throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
                                }

                                AbstractEntityHome.remove(fperBean);
                                funcoesRemovidas.add(funPermitida);
                            } else {
                                AbstractEntityHome.remove(fperBean);
                                funcoesRemovidas.add(funPermitida);
                            }
                        } else if (funcoesOld.contains(funPermitida) && funcoes.contains(funPermitida)) {
                            // Usuário já possui a função, não precisa incluir
                            continue;
                        } else if (!funcoesOld.contains(funPermitida) && funcoes.contains(funPermitida)) {
                            funcoesNovas.add(funPermitida);
                        }
                    }
                }

                // todos os usuários do perfil devem ter e-mail cadastrado ao adicionar permissão de usuário auditor
                if (funcoesNovas.contains(CodedValues.FUN_USUARIO_AUDITOR)) {
                    final CustomTransferObject filtro = new CustomTransferObject();
                    filtro.setAttribute(Columns.PER_CODIGO, perCodigo);

                    final List<TransferObject> listUsuarios = lstUsuarios("0", tipoEntidade, codigoEntidade, filtro, responsavel);
                    if (!listUsuarios.isEmpty()) {
                        for (final TransferObject usuario : listUsuarios) {
                            if (TextHelper.isNull(usuario.getAttribute(Columns.USU_EMAIL))) {
                                throw new UsuarioControllerException("mensagem.erro.todos.usuarios.perfil.devem.possuir.email.valido.cadastrado.para.atribuir.permissao.auditor", responsavel);
                            }
                        }
                    }
                }

                // Insere as novas funções
                if (funcoesNovas.size() > 0) {
                    createFuncaoPerfil(funcoesNovas, perCodigo, responsavel);
                }

                // Grava log das funções removidas
                if ((funcoesRemovidas != null) && (funcoesRemovidas.size() > 0)) {
                    final LogDelegate log = new LogDelegate(responsavel, Log.FUNCAO_PERFIL, Log.DELETE, Log.LOG_INFORMACAO);
                    log.setPerfil(perCodigo);
                    for (final String funcaoRemovida : funcoesRemovidas) {
                        log.setFuncao(funcaoRemovida);
                        log.write();
                    }
                }

                // Insere ocorrência de inclusão ou alteração para o perfil
                String funcoesInseridas = "";
                String funcoesRetiradas = "";

                if ((funcoesNovas != null) && !funcoesNovas.isEmpty()) {
                    for (final String funcao : funcoesNovas) {
                        funcoesInseridas += funcao + ", ";
                    }
                }

                if ((funcoesRemovidas != null) && !funcoesRemovidas.isEmpty()) {
                    for (final String funcao : funcoesRemovidas) {
                        funcoesRetiradas += funcao + ", ";
                    }
                }

                if ((funcoesNovas != null) && !funcoesNovas.isEmpty() && ((funcoesOld == null) || funcoesOld.isEmpty())) {
                    criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.inclusao", responsavel, funcoesInseridas.substring(0, funcoesInseridas.length() - 2)), null, responsavel);
                } else if ((funcoesNovas != null) && !funcoesNovas.isEmpty() && ((funcoesRemovidas == null) || funcoesRemovidas.isEmpty())) {
                    criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.alteracao", responsavel, funcoesInseridas.substring(0, funcoesInseridas.length() - 2)), null, responsavel);
                } else if ((funcoesNovas != null) && !funcoesNovas.isEmpty() && (funcoesRemovidas != null) && !funcoesRemovidas.isEmpty()) {
                    criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.alteracao.remocao.inclusao", responsavel, funcoesInseridas.substring(0, funcoesInseridas.length() - 2), funcoesRetiradas.substring(0, funcoesRetiradas.length() - 2)), null, responsavel);
                } else if (((funcoesNovas == null) || funcoesNovas.isEmpty()) && (funcoesRemovidas != null) && !funcoesRemovidas.isEmpty()) {
                    criaOcorrenciaPerfil(perCodigo, CodedValues.TOC_ALTERA_PERFIL, ApplicationResourcesHelper.getMessage("mensagem.perfil.ocorrencia.remocao", responsavel, funcoesRetiradas.substring(0, funcoesRetiradas.length() - 2)), null, responsavel);
                }

                return ((funcoesNovas != null) && !funcoesNovas.isEmpty()) || ((funcoesRemovidas != null) && !funcoesRemovidas.isEmpty());
            }
            return false;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.atualizar.este.perfil.erro.interno.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public void removePerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (usuarioPodeModificarPerfil(perCodigo, true, true, responsavel)) {
                final String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);

                // verifica se este perfil tem usuários ligados a ele antes de remover
                final List<PerfilUsuario> usuarios = PerfilUsuarioHome.findByPerfil(perCodigo);
                if ((usuarios != null) && (usuarios.size() > 0)) {
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.remover.perfil.pois.existem.usuarios.associados", responsavel);
                }

                // Remove as funções deste perfil
                final List<FuncaoPerfil> funcoes = FuncaoPerfilHome.findByPerfil(perCodigo);
                for (final FuncaoPerfil fperBean : funcoes) {
                    AbstractEntityHome.remove(fperBean);
                }

                // Remove a associação do perfil com a entidade criadora
                if (CodedValues.PAP_CONSIGNANTE.equals(papCodigo)) { // CSE
                    final PerfilCse pceBean = PerfilCseHome.findByPrimaryKey(new PerfilCseId(codigoEntidade, perCodigo));
                    AbstractEntityHome.remove(pceBean);
                } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigo)) { // CSA
                    final PerfilCsa pcaBean = PerfilCsaHome.findByPrimaryKey(new PerfilCsaId(codigoEntidade, perCodigo));
                    AbstractEntityHome.remove(pcaBean);
                } else if (CodedValues.PAP_ORGAO.equals(papCodigo)) { // ORG
                    final PerfilOrg porBean = PerfilOrgHome.findByPrimaryKey(new PerfilOrgId(codigoEntidade, perCodigo));
                    AbstractEntityHome.remove(porBean);
                } else if (CodedValues.PAP_CORRESPONDENTE.equals(papCodigo)) { // COR
                    final PerfilCor pcoBean = PerfilCorHome.findByPrimaryKey(new PerfilCorId(codigoEntidade, perCodigo));
                    AbstractEntityHome.remove(pcoBean);
                } else if (CodedValues.PAP_SUPORTE.equals(papCodigo)) { // SUP
                    final PerfilSup psuBean = PerfilSupHome.findByPrimaryKey(new PerfilSupId(codigoEntidade, perCodigo));
                    AbstractEntityHome.remove(psuBean);
                } else {
                    throw new UsuarioControllerException("mensagem.erro.arg0.tipo.entidade.invalido", responsavel, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }

                // Remove Ocorrências do perfil
                OcorrenciaPerfilHome.deleteTodasOcorrenciaPerfilSelecionado(perCodigo);

                // Remove o perfil
                final Perfil perBean = PerfilHome.findByPrimaryKey(perCodigo);
                AbstractEntityHome.remove(perBean);

                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERFIL, Log.DELETE, Log.LOG_INFORMACAO);
                logDelegate.setPerfil(perCodigo);
                logDelegate.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.remover.este.perfil.erro.interno.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public void copyPerfil(String tipoEntidade, String codigoEntidade, String perOrigem, List<String> perDestino, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (TextHelper.isNull(perOrigem)) {
                throw new UsuarioControllerException("mensagem.erro.selecione.um.perfil.origem.de.onde.funcoes.serao.copiadas", responsavel);
            }
            if ((perDestino == null) || perDestino.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.selecione.pelo.menos.um.perfil.destino.onde.funcoes.serao.aplicadas", responsavel);
            }

            // Recupera as funções que serão copiadas
            final List<String> funcoes = new ArrayList<>();
            final Collection<FuncaoPerfil> funcoesPerfil = FuncaoPerfilHome.findByPerfil(perOrigem);
            if ((funcoesPerfil != null) && !funcoesPerfil.isEmpty()) {
                for (final FuncaoPerfil funcaoPerfil : funcoesPerfil) {
                    funcoes.add(funcaoPerfil.getFunCodigo());
                }
            }

            for (final String perCodigoDestino : perDestino) {
                if (!perCodigoDestino.equals(perOrigem)) {
                    final Perfil perfilDestino = PerfilHome.findByPrimaryKey(perCodigoDestino);
                    Short status = null;
                    if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                        final PerfilCse perfilEntidade = PerfilCseHome.findByPrimaryKey(new PerfilCseId(codigoEntidade, perCodigoDestino));
                        status = perfilEntidade.getPceAtivo();
                    } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                        final PerfilCsa perfilEntidade = PerfilCsaHome.findByPrimaryKey(new PerfilCsaId(codigoEntidade, perCodigoDestino));
                        status = perfilEntidade.getPcaAtivo();
                    } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                        final PerfilCor perfilEntidade = PerfilCorHome.findByPrimaryKey(new PerfilCorId(codigoEntidade, perCodigoDestino));
                        status = perfilEntidade.getPcoAtivo();
                    } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
                        final PerfilOrg perfilEntidade = PerfilOrgHome.findByPrimaryKey(new PerfilOrgId(codigoEntidade, perCodigoDestino));
                        status = perfilEntidade.getPorAtivo();
                    } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                        final PerfilSup perfilEntidade = PerfilSupHome.findByPrimaryKey(new PerfilSupId(codigoEntidade, perCodigoDestino));
                        status = perfilEntidade.getPsuAtivo();
                    }
                    if (status.equals(CodedValues.STS_INDISP)) {
                        LOG.warn("Funções não foram copiadas para o perfil ['" + perCodigoDestino + "'] porque o perfil está excluído.");
                        continue;
                    }

                    // Altera as funções do perfil
                    updatePerfil(tipoEntidade, codigoEntidade, perCodigoDestino, perfilDestino.getPerDescricao(), perfilDestino.getPerVisivel(), perfilDestino.getPerDataExpiracao(), null, status, null, null, null, funcoes, responsavel);

                    // Gera o log de auditoria
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PERFIL, Log.UPDATE, Log.LOG_INFORMACAO);
                    logDelegate.setPerfil(perCodigoDestino);
                    logDelegate.setPerfilOrigem(perOrigem);
                    logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.perfil.alterado.a.partir.funcoes.associadas.perfil.origem", responsavel));
                    logDelegate.write();
                }
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.recuperar.informacoes.para.copia.funcoes.para.perfil", responsavel);
        }
    }

    private void createFuncaoPerfil(List<String> funCodigo, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if ((funCodigo != null) && (funCodigo.size() > 0)) {
                for (final String funcao : funCodigo) {
                    if (CodedValues.FUNCOES_NAO_PERMITIDAS_PERFIL.contains(funcao)) {
                        throw new UsuarioControllerException("mensagem.erro.nao.permitida.associacao.funcao.arg0.perfil", responsavel, funcao);
                    }
                    FuncaoPerfilHome.create(funcao, perCodigo);
                }

                final LogDelegate log = new LogDelegate(responsavel, Log.FUNCAO_PERFIL, Log.CREATE, Log.LOG_INFORMACAO);
                log.setPerfil(perCodigo);
                for (final String funcao : funCodigo) {
                    log.setFuncao(funcao);
                    log.write();
                }
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.associar.funcao.perfil.erro.interno.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public CustomTransferObject getFuncao(String funCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemFuncaoQuery funQuery = new ObtemFuncaoQuery();
            funQuery.funCodigo = funCodigo;

            final List<TransferObject> lstFuncao = funQuery.executarDTO();
            if (!lstFuncao.isEmpty()) {
                return (CustomTransferObject) lstFuncao.get(0);
            }
            return null;
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<String> getFuncaoPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaFuncoesPerfilQuery funPerQuery = new ListaFuncoesPerfilQuery();
            funPerQuery.perCodigo = perCodigo;
            funPerQuery.papCodigoDestino = UsuarioHelper.getPapCodigo(tipoEntidade);
            funPerQuery.papCodigoOrigem = UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade());
            return funPerQuery.executarLista();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<TransferObject> lstPerfil(String tipoEntidade, String codigoEntidade, CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaPerfilTipoEntidadeQuery lstPerTipoEntQuery = new ListaPerfilTipoEntidadeQuery();
            lstPerTipoEntQuery.tipoEntidade = tipoEntidade;
            lstPerTipoEntQuery.codigoEntidade = codigoEntidade;
            lstPerTipoEntQuery.pceAtivo = (Short) filtro.getAttribute(Columns.PCE_ATIVO);
            lstPerTipoEntQuery.pcaAtivo = (Short) filtro.getAttribute(Columns.PCA_ATIVO);
            lstPerTipoEntQuery.pcoAtivo = (Short) filtro.getAttribute(Columns.PCO_ATIVO);
            lstPerTipoEntQuery.porAtivo = (Short) filtro.getAttribute(Columns.POR_ATIVO);
            lstPerTipoEntQuery.psuAtivo = (Short) filtro.getAttribute(Columns.PSU_ATIVO);
            lstPerTipoEntQuery.perDescricao = (String) filtro.getAttribute(Columns.PER_DESCRICAO);
            lstPerTipoEntQuery.responsavel = responsavel;

            return lstPerTipoEntQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public List<TransferObject> lstPerfilSemBloqueioRepasse(String tipoEntidade, String codigoEntidade, String usuCodigoEdt, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaPerfilSemBloqueioRepasseQuery perSemBloqRepQuery = new ListaPerfilSemBloqueioRepasseQuery();
            perSemBloqRepQuery.tipoEntidade = tipoEntidade;
            perSemBloqRepQuery.papCodigoDestino = UsuarioHelper.getPapCodigo(tipoEntidade);
            perSemBloqRepQuery.papCodigoOrigem = UsuarioHelper.getPapCodigo(responsavel.getTipoEntidade());
            perSemBloqRepQuery.usuCodigoEdt = usuCodigoEdt;
            perSemBloqRepQuery.codigoEntidade = codigoEntidade;
            perSemBloqRepQuery.responsavel = responsavel;

            return perSemBloqRepQuery.executarDTO();
        } catch (final HQueryException hex) {
            LOG.error(hex.getMessage(), hex);
            throw new UsuarioControllerException(hex);
        }
    }

    @Override
    public Short getStatusPerfil(String tipoEntidade, String codigoEntidade, String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String papCodigo = UsuarioHelper.getPapCodigo(tipoEntidade);

            // Remove a associação do perfil com a entidade criadora
            if (CodedValues.PAP_CONSIGNANTE.equals(papCodigo)) { // CSE
                final PerfilCse pceBean = PerfilCseHome.findByPrimaryKey(new PerfilCseId(codigoEntidade, perCodigo));
                return pceBean.getPceAtivo();
            } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigo)) { // CSA
                final PerfilCsa pcaBean = PerfilCsaHome.findByPrimaryKey(new PerfilCsaId(codigoEntidade, perCodigo));
                return pcaBean.getPcaAtivo();
            } else if (CodedValues.PAP_ORGAO.equals(papCodigo)) { // ORG
                final PerfilOrg porBean = PerfilOrgHome.findByPrimaryKey(new PerfilOrgId(codigoEntidade, perCodigo));
                return porBean.getPorAtivo();
            } else if (CodedValues.PAP_CORRESPONDENTE.equals(papCodigo)) { // COR
                final PerfilCor pcoBean = PerfilCorHome.findByPrimaryKey(new PerfilCorId(codigoEntidade, perCodigo));
                return pcoBean.getPcoAtivo();
            } else if (CodedValues.PAP_SUPORTE.equals(papCodigo)) { // SUP
                final PerfilSup psuBean = PerfilSupHome.findByPrimaryKey(new PerfilSupId(codigoEntidade, perCodigo));
                return psuBean.getPsuAtivo();
            } else if (CodedValues.PAP_SERVIDOR.equals(papCodigo)) { // SER
                return CodedValues.STS_ATIVO; // PerfilSer não existe, portanto considera o perfil ativo
            } else {
                throw new UsuarioControllerException("mensagem.erro.arg0.tipo.entidade.invalido", responsavel, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, ex, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
        }
    }

    /**
     * Verifica se o usuário tem permissão para executar a função identificada pelo funCodigo.
     * ATENÇÃO: Não utilizar este método, a não ser que seja necessário verificar se um terceiro
     * usuário tenha permissão para realizar uma operação. Caso seja para verificar sobre o próprio
     * usuário, utilize a função AcessoSistema.temPermissao()
     * @param usuCodigo : código do usuário
     * @param funCodigo : código da função
     * @param tipoEntidade : CSE, CSA, ORG, COR ou SER, null para todos
     * @param responsavel : responsável pela verificaçãp
     * @return True se o usuário tem a permissão, falso caso contrário
     * @throws UsuarioControllerException
     */
    @Override
    public boolean usuarioTemPermissao(String usuCodigo, String funCodigo, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Grava Log de Acesso
            final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.FIND, Log.LOG_INFORMACAO);
            log.setUsuario(usuCodigo);
            log.setFuncao(funCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.verificando.se.usuario.tem.permissao.para.funcao", responsavel));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Faz a busca pelo perfil do usuário
        try {
            // busca as funções do perfil
            final PerfilUsuario upeBean = PerfilUsuarioHome.findByPrimaryKey(usuCodigo);
            final FuncaoPerfilId pk = new FuncaoPerfilId(funCodigo, upeBean.getPerfil().getPerCodigo());
            FuncaoPerfilHome.findByPrimaryKey(pk);
            LOG.debug("Usuario Com Perfil Com Permissão Para " + funCodigo + ".");
            return true;

        } catch (final FindException ex) {
            // Busca as funções p/ perfil personalizado

            if ((tipoEntidade == null) || AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipoEntidade)) {
                try {
                    final List<FuncaoPerfilCor> perfil = FuncaoPerfilCorHome.findByUsuFunCodigo(usuCodigo, funCodigo);
                    if ((perfil != null) && (perfil.size() > 0)) {
                        return true;
                    }
                } catch (final FindException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, e, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }
            }

            if ((tipoEntidade == null) || AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipoEntidade)) {
                try {
                    final List<FuncaoPerfilCsa> perfil = FuncaoPerfilCsaHome.findByUsuFunCodigo(usuCodigo, funCodigo);
                    if ((perfil != null) && (perfil.size() > 0)) {
                        return true;
                    }
                } catch (final FindException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, e, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }
            }

            if ((tipoEntidade == null) || AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) {
                try {
                    final List<FuncaoPerfilOrg> perfil = FuncaoPerfilOrgHome.findByUsuFunCodigo(usuCodigo, funCodigo);
                    if ((perfil != null) && (perfil.size() > 0)) {
                        return true;
                    }
                } catch (final FindException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, e, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }
            }

            if ((tipoEntidade == null) || AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade)) {
                try {
                    final List<FuncaoPerfilCse> perfil = FuncaoPerfilCseHome.findByUsuFunCodigo(usuCodigo, funCodigo);
                    if ((perfil != null) && (perfil.size() > 0)) {
                        return true;
                    }
                } catch (final FindException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, e, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }
            }

            if ((tipoEntidade == null) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipoEntidade)) {
                try {
                    final List<FuncaoPerfilSup> perfil = FuncaoPerfilSupHome.findByUsuFunCodigo(usuCodigo, funCodigo);
                    if ((perfil != null) && (perfil.size() > 0)) {
                        return true;
                    }
                } catch (final FindException e) {
                    LOG.error(e.getMessage(), e);
                    throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, e, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel) + " ");
                }
            }
        }

        return false;
    }

    /**
     * Verifica se um usuário tem permissão de realizar operações no usuário
     * informada pelo parâmetro "usuCodigo",  de acordo com sua entidade.
     * @param usuCodigo : usuário que está sendo afetado
     * @param gravaLog : determina se será gravado log de erro, caso não tenha permissão
     * @param lancaExcecao : determina se será lançado uma exceção, caso não tenha permissão
     * @param responsavel : responsável pela operação de modificação do usuário
     * @return boolean
     * @throws AutorizacaoControllerException
     */
    private boolean usuarioPodeModificarUsu(String usuCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws UsuarioControllerException {
        final boolean podeModificar = entidadeUsuarioPodeModificarUsu(usuCodigo, responsavel);
        if (!podeModificar) {
            if (gravaLog) {
                try {
                    // Grava log de Erro
                    final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_ERRO_SEGURANCA);
                    log.setUsuario(usuCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.para.modificar.este.usuario", responsavel)));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                }
            }

            if (lancaExcecao) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.tem.permissao.para.modificar.este.usuario", responsavel);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna TRUE se o usuário, representado pelo parâmetro "responsavel", pertence a uma
     * entidade, seja CSE/ORG/SUP/CSA/COR/SER, que tenha permissão de realizar operações
     * sobre o usuário, representada pelo parâmetro "usuCodigo".
     * @param usuCodigo String
     * @param responsavel AcessoSistema
     * @return
     * @throws UsuarioControllerException
     */
    private boolean entidadeUsuarioPodeModificarUsu(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            boolean podeModificar = false;

            // DESENV-10480: correção da DESENV-9892 para sempre retornar true se houver o parâmetro de sistema de autodesbloqueio e for a respectiva ação do tipo de usuário correspondente
            if (responsavel.getUsuCodigo().equals(usuCodigo) && (
                    (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && !TextHelper.isNull(responsavel.getFunCodigo()) && CodedValues.FUN_AUTODESBLOQUEIO_CSE_ORG.equals(responsavel.getFunCodigo())) ||
                    (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && !TextHelper.isNull(responsavel.getFunCodigo()) && CodedValues.FUN_AUTODESBLOQUEIO_CSA_COR.equals(responsavel.getFunCodigo())) ||
                    (ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && !TextHelper.isNull(responsavel.getFunCodigo()) && CodedValues.FUN_AUTODESBLOQUEIO_SUP.equals(responsavel.getFunCodigo())))) {

                return true;
            } else if (!TextHelper.isNull(responsavel.getFunCodigo()) && (
                    CodedValues.FUN_AUTODESBLOQUEIO_CSE_ORG.equals(responsavel.getFunCodigo()) ||
                    CodedValues.FUN_AUTODESBLOQUEIO_CSA_COR.equals(responsavel.getFunCodigo()) ||
                    CodedValues.FUN_AUTODESBLOQUEIO_SUP.equals(responsavel.getFunCodigo()))) {
                // é ação de desbloqueio, porém não tem o respectivo parâmetro de sistema como S.
                return false;
            }

            if (responsavel.isSistema() || responsavel.isSup()) {
                // O sistema e usuários de suporte, podem modificar quaisquer usuários
                podeModificar = true;

            } else if (responsavel.isCse()) {
                // Usuário de gestor pode modificar quaisquer usuários exceto os de suporte
                final UsuarioCsePodeModificarUsuQuery query = new UsuarioCsePodeModificarUsuQuery();
                query.usuCodigoAfetado = usuCodigo;
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                // Usuário de "estabelecimento" pode modificar apenas usuários do próprio órgão, usuários servidores
                // dos órgãos do mesmo estabelecimento e usuários de consignatárias
                final UsuarioEstPodeModificarUsuQuery query = new UsuarioEstPodeModificarUsuQuery();
                query.usuCodigoAfetado = usuCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isOrg() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                // Usuário de órgão pode modificar apenas usuários do próprio órgão, usuários servidores
                // do próprio órgão e usuários de consignatárias
                final UsuarioOrgPodeModificarUsuQuery query = new UsuarioOrgPodeModificarUsuQuery();
                query.usuCodigoAfetado = usuCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isCsa()) {
                // Usuário de consignatária pode modificar usuários da próprio consignatária e de seus correspondentes
                final UsuarioCsaPodeModificarUsuQuery query = new UsuarioCsaPodeModificarUsuQuery();
                query.usuCodigoAfetado = usuCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isCor()) {
                // Usuário de correspondente pode modificar apenas usuários do próprio correspondente
                final UsuarioCorPodeModificarUsuQuery query = new UsuarioCorPodeModificarUsuQuery();
                query.usuCodigoAfetado = usuCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isSer()) {
                if (ParamSist.paramEquals(CodedValues.TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR, CodedValues.TPC_SIM, responsavel) ||
                        (ParamSist.paramEquals(CodedValues.TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF, CodedValues.TPC_SIM, responsavel))) {
                    final Usuario usuarioSer = UsuarioHome.findByPrimaryKey(usuCodigo);
                    final Servidor responsavelServidor = ServidorHome.findByUsuCodigo(responsavel.getUsuCodigo());

                    // Usuário servidor pode modificar outros usuários servidores com o mesmo cpf
                    podeModificar = responsavelServidor.getSerCpf().equals(usuarioSer.getUsuCpf());
                } else {
                    // Usuário servidor pode modificar apenas seu próprio usuário
                    podeModificar = responsavel.getUsuCodigo().equals(usuCodigo);
                }
            }

            return podeModificar;
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Verifica se um usuário tem permissão de realizar operações no perfil
     * informada pelo parâmetro "perCodigo",  de acordo com sua entidade.
     * @param perCodigo : perfil que está sendo afetado
     * @param gravaLog : determina se será gravado log de erro, caso não tenha permissão
     * @param lancaExcecao : determina se será lançado uma exceção, caso não tenha permissão
     * @param responsavel : responsável pela operação de modificação do perfil
     * @return boolean
     * @throws AutorizacaoControllerException
     */
    private boolean usuarioPodeModificarPerfil(String perCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws UsuarioControllerException {
        final boolean podeModificar = entidadeUsuarioPodeModificarPerfil(perCodigo, responsavel);
        if (!podeModificar) {
            if (gravaLog) {
                try {
                    // Grava log de Erro
                    final LogDelegate log = new LogDelegate(responsavel, Log.PERFIL, Log.UPDATE, Log.LOG_ERRO_SEGURANCA);
                    log.setPerfil(perCodigo);
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.upper.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.tem.permissao.para.modificar.este.perfil", responsavel)));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                }
            }

            if (lancaExcecao) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.tem.permissao.para.modificar.este.perfil", responsavel);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna TRUE se o usuário, representado pelo parâmetro "responsavel", pertence a uma
     * entidade, seja CSE/ORG/SUP/CSA/COR/SER, que tenha permissão de realizar operações
     * sobre o perfil, representada pelo parâmetro "perCodigo".
     * @param usuCodigo String
     * @param responsavel AcessoSistema
     * @return
     * @throws UsuarioControllerException
     */
    private boolean entidadeUsuarioPodeModificarPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            boolean podeModificar = false;

            if (responsavel.isSistema() || responsavel.isCse() || responsavel.isSup()) {
                // O sistema, usuários de gestor ou suporte, podem modificar quaisquer usuários
                podeModificar = true;

            } else if (responsavel.isOrg()) {
                // Usuário de órgão pode modificar apenas perfil do próprio órgão e perfil de consignatárias
                final UsuarioOrgPodeModificarPerfilQuery query = new UsuarioOrgPodeModificarPerfilQuery();
                query.perCodigoAfetado = perCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isCsa()) {
                // Usuário de consignatária pode modificar usuários da próprio consignatária e de seus correspondentes
                final UsuarioCsaPodeModificarPerfilQuery query = new UsuarioCsaPodeModificarPerfilQuery();
                query.perCodigoAfetado = perCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isCor()) {
                // Usuário de correspondente pode modificar apenas usuários do próprio correspondente
                final UsuarioCorPodeModificarPerfilQuery query = new UsuarioCorPodeModificarPerfilQuery();
                query.perCodigoAfetado = perCodigo;
                query.usuCodigoResponsavel = responsavel.getUsuCodigo();
                final List<?> result = query.executarLista();
                podeModificar = (result != null) && (result.size() > 0);

            } else if (responsavel.isSer()) {
                // Usuário servidor não pode modificar nenhum perfil
                podeModificar = false;
            }

            return podeModificar;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /** SENHA DE SERVIDOR ------------------------------------------------------------------------------------- **/

    @Override
    public TransferObject getSenhaServidor(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemSenhaServidorQuery query = new ObtemSenhaServidorQuery();
            query.rseCodigo = rseCodigo;

            final List<TransferObject> usuarios = query.executarDTO();

            if (usuarios.size() == 0) {
                return null;
            }
            return usuarios.get(0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /** OCORRÊNCIA DE USUÁRIO --------------------------------------------------------------------------------- **/

    @Override
    public String createOcorrenciaUsuario(CustomTransferObject ocorrencia, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final OcorrenciaUsuario ocoUsuarioBean = OcorrenciaUsuarioHome.create(ocorrencia.getAttribute(Columns.OUS_TOC_CODIGO).toString(), (String) ocorrencia.getAttribute(Columns.OUS_USU_CODIGO), DateHelper.getSystemDatetime(), (String) ocorrencia.getAttribute(Columns.OUS_OBS), (String) ocorrencia.getAttribute(Columns.OUS_OUS_USU_CODIGO), (String) ocorrencia.getAttribute(Columns.OUS_IP_ACESSO), (String) ocorrencia.getAttribute(Columns.OUS_TMO_CODIGO));

            return ocoUsuarioBean.getOusCodigo();

        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.ocorrencia.para.este.usuario.erro.interno.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaUsuario(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaOcorrenciaUsuarioQuery query = new ListaOcorrenciaUsuarioQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            if (filtro != null) {
                query.ousUsuCodigo = (String) filtro.getAttribute(Columns.OUS_USU_CODIGO);
                query.tocCodigos = (List<String>) filtro.getAttribute("tocCodigos");
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public int countOcorrenciaUsuario(CustomTransferObject filtro, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaOcorrenciaUsuarioQuery query = new ListaOcorrenciaUsuarioQuery();
            query.count = true;
            if (filtro != null) {
                query.ousUsuCodigo = (String) filtro.getAttribute(Columns.OUS_USU_CODIGO);
                query.tocCodigos = (List<String>) filtro.getAttribute("tocCodigos");
            }
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Verifica se um usuário é usuário de órgão
     * @param usuCodigo : código do usuário
     * @return O código do órgão, caso o usuário seja de órgão, ou nulo caso contrário
     * @throws UsuarioControllerException
     */
    @Override
    public String isOrg(String usuCodigo) throws UsuarioControllerException {
        try {
            final UsuarioOrg usuBean = UsuarioOrgHome.findByUsuCodigo(usuCodigo);
            return usuBean.getOrgao().getOrgCodigo();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Cadastra chave para validação TOTP e associa ao usuário responsável
     * @param usuChaveValidacaoTotp
     * @param usuOperacoesValidacaoTotp
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public String cadastrarChaveValidacaoTotp(String usuChaveValidacaoTotp, String usuOperacoesValidacaoTotp, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final UsuarioTransferObject usuario = findUsuario(new UsuarioTransferObject(responsavel.getUsuCodigo()), AcessoSistema.ENTIDADE_USU, responsavel);
            usuario.setUsuChaveValidacaoTotp(usuChaveValidacaoTotp);
            usuario.setUsuOperacoesValidacaoTotp(usuOperacoesValidacaoTotp);
            updateUsuario(usuario, null, responsavel);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setUsuario(responsavel.getUsuCodigo());
            logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cadastro.chave.validacao.totp", responsavel));
            logDelegate.write();

            return usuChaveValidacaoTotp;
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Gera chave para validação TOTP e associa ao usuário responsável
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public void removerChaveValidacaoTotp(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final UsuarioTransferObject usuario = findUsuario(new UsuarioTransferObject(responsavel.getUsuCodigo()), AcessoSistema.ENTIDADE_USU, responsavel);
            usuario.setUsuChaveValidacaoTotp(null);
            updateUsuario(usuario, null, responsavel);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setUsuario(responsavel.getUsuCodigo());
            logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.remocao.chave.validacao.totp", responsavel));
            logDelegate.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        alterarSenha(usuCodigo, senhaNova, dicaSenha, expiracaoImediata, reiniciacao, senhaCriptografada, null, senhaAtualAberta, responsavel);
    }

    /**
     * Altera a senha do usuário.
     * @param usuCodigo
     * @param senhaNova
     * @param dicaSenha
     * @param responsavel
     * @param expiracaoImediata Indica se a senha deve expirar imediatamente,
     *        ou se o prazo de validade deve ser determinado pelo parâmetro de sistema.
     * @param reiniciacao Indica se a alteração é por motivo de reiniciação de senha.
     * @param tipoMotivoOperacao Indo o motivo da operação que será gravado junto com a ocorrência
     * @throws UsuarioControllerException
     */
    @Override
    public void alterarSenha(String usuCodigo, String senhaNova, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaCriptografada, CustomTransferObject tipoMotivoOperacao, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        this.alterarSenha(usuCodigo, senhaNova, null, dicaSenha, expiracaoImediata, reiniciacao, false, senhaCriptografada, null, tipoMotivoOperacao, false, false, senhaAtualAberta, responsavel);
    }

    /**
     * Altera a senha do usuário.
     * @param usuCodigo : Código do usuário.
     * @param senhaNova : Nova senha (O flag senhaCriptografada determina se está ou não criptografada).
     * @param senhaUtilizada : Senha utilizada e que será removida. (O flag senhaCriptografada determina se está ou não criptografada).
     * @param dicaSenha : Dica da senha, se houver.
     * @param expiracaoImediata : Indica se a senha expira imediatamente.
     * @param reiniciacao : Indica se trata-se de reiniciação.
     * @param senhaAutorizacaoServidor : Informa se a senha sendo alterada é a de autorização do servidor.
     * @param senhaCriptografada : especifica se a senha passada pelo param 'senhaNova' já está criptografada ou não.
     * @param tocCodigo : Tipo de ocorrência ser gravada.
     * @param tipoMotivoOperacao : Dados do motivo da operação.
     * @param responsavel : Responsável.
     * @throws UsuarioControllerException
     */
    private void alterarSenha(String usuCodigo, String senhaNova, String senhaUtilizada, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaAutorizacaoServidor, boolean senhaCriptografada, String tocCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws UsuarioControllerException {
        this.alterarSenha(usuCodigo, senhaNova, senhaUtilizada, dicaSenha, expiracaoImediata, reiniciacao, senhaAutorizacaoServidor, senhaCriptografada, tocCodigo, tipoMotivoOperacao, false, false, null, responsavel);
    }

    /**
     * Altera a senha do usuário, seja por uma alteração manual, reinicialização,
     * consumo ou cancelamento de senha de autorização.
     * @param usuCodigo : Código do usuário.
     * @param senhaNova : Nova senha (O flag senhaCriptografada determina se está ou não criptografada).
     * @param senhaUtilizada : Senha utilizada e que será removida. (O flag senhaCriptografada determina se está ou não criptografada).
     * @param dicaSenha : Dica da senha, se houver.
     * @param expiracaoImediata : Indica se a senha expira imediatamente.
     * @param reiniciacao : Indica se trata-se de reiniciação.
     * @param senhaAutorizacaoServidor : Informa se a senha sendo alterada é a de autorização do servidor.
     * @param senhaCriptografada : especifica se a senha passada pelo param 'senhaNova' já está criptografada ou não.
     * @param tocCodigo : Tipo de ocorrência ser gravada.
     * @param tipoMotivoOperacao : Dados do motivo da operação.
     * @param naoComunicarServidor : Não enviar email para o servidor com a nova senha
     * @param senhaAtualAberta : Senha atual do usuário aberta para ser validada antes da alteração da senha atual.
     * @param responsavel : Responsável.
     * @throws UsuarioControllerException
     */
    private void alterarSenha(String usuCodigo, String senhaNova, String senhaUtilizada, String dicaSenha, boolean expiracaoImediata, boolean reiniciacao, boolean senhaAutorizacaoServidor, boolean senhaCriptografada, String tocCodigo, CustomTransferObject tipoMotivoOperacao, boolean naoComunicarServidor, boolean senhaApp, String senhaAtualAberta, AcessoSistema responsavel) throws UsuarioControllerException {
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        boolean usuarioSer = false;

        try {
            // Tenta buscar por um usuario servidor.
            usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
            usuarioSer = true;
        } catch (final UsuarioControllerException ex) {
            if (senhaAutorizacaoServidor) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.servidor", responsavel);
            }

            // Se não foi possível localizar um usuário servidor, trata-se de outro tipo de usuário.
            usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);
            usuarioSer = false;
        }

        // Se autenticação foi realizada pelo SSO e o usuário está alterando a sua própria senha, alteração de senha deve ser no SSO também
        final boolean autenticacaoSSO = UsuarioHelper.usuarioAutenticaSso(findTipoUsuarioByCodigo(usuCodigo, responsavel), responsavel);

        // Se é usuário servidor e utiliza senha de autorização, verifica se o parâmetro de múltiplas senhas
        // está habilitado, pois neste caso as senhas estarão armazenadas na tabela tb_senha_autorizacao_servidor,
        // e não na tb_usuario.usu_senha_2.
        final boolean usaMultiplasSenhasAut = usuarioSer && senhaAutorizacaoServidor && ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        if (!senhaAutorizacaoServidor && !reiniciacao) {
            SenhaHelper.validarForcaSenha(senhaNova, usuarioSer, responsavel);
        }

        // Criptografa a nova senha para armazenamento no banco.
        String senhaNovaCrypt = null;
        if (senhaNova != null) {
            if (!senhaCriptografada) {
                senhaNovaCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), senhaNova, usuarioSer, responsavel);
            } else {
                senhaNovaCrypt = senhaNova;
            }
        }
        // Criptografa a senha utilizada para ser cancelada/consumida. Para SaltedMD5, a senha deve chegar aqui criptografada
        String senhaUtilizadaCrypt = null;
        if (usaMultiplasSenhasAut && (senhaUtilizada != null)) {
            if (!senhaCriptografada) {
                senhaUtilizadaCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), senhaUtilizada, usuarioSer, responsavel);
            } else {
                senhaUtilizadaCrypt = senhaUtilizada;
            }
        }

        if (!reiniciacao) {
            // O padrão é não permitir senhas de consulta e autorização iguais.
            if (usuarioSer && (senhaNovaCrypt != null) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_SENHAS_SERVIDOR_IGUAIS, CodedValues.TPC_SIM, responsavel)) {
                if (senhaAutorizacaoServidor) {
                    // Se está alterando a senha de autorização, testa o valor da senha de consulta.
                    if (JCrypt.verificaSenha(senhaNova, usuario.getUsuSenha())) {
                        throw new UsuarioControllerException("mensagem.erro.senha.servidor.autorizacao.deve.ser.diferente.de.senha.servidor.consulta", responsavel);
                    }
                } else // Se não está alterando a senha de autorização, verifica se o sistema trabalha com ela. Caso positivo, compara seu valor.
                if (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && JCrypt.verificaSenha(senhaNova, usuario.getUsuSenha2())) {
                    throw new UsuarioControllerException("mensagem.erro.senha.servidor.autorizacao.deve.ser.diferente.de.senha.servidor.consulta", responsavel);
                }
            }

            // Verifica a senha com relação às anteriores
            validaSenhasAnteriores(usuCodigo, senhaNova, senhaNovaCrypt, senhaAutorizacaoServidor, responsavel);
        }

        try {
            // Calcula a data de expiração da senha.
            Calendar dataExpiracaoSenhaCalc = null;
            if (senhaNova != null) {
                if (!expiracaoImediata) {
                    dataExpiracaoSenhaCalc = calculaDataExpiracaoSenha(senhaAutorizacaoServidor, usuario, usuarioSer, responsavel);
                } else {
                    dataExpiracaoSenhaCalc = Calendar.getInstance();
                }
            }
            // Recupera a data de expiração apenas com a parte da data (SQL Date)
            final java.sql.Date dataExpiracaoSenha = dataExpiracaoSenhaCalc != null ? new java.sql.Date(dataExpiracaoSenhaCalc.getTimeInMillis()) : null;

            // Salva a alteração de senha.
            if (!reiniciacao && (dicaSenha != null) && !senhaAutorizacaoServidor) {
                usuario.setUsuDicaSenha(dicaSenha);
            }

            if (!senhaAutorizacaoServidor) {
                if (senhaNovaCrypt != null) {
                    if (senhaApp) {
                        usuario.setUsuSenhaApp(senhaNovaCrypt);
                    } else {
                        usuario.setUsuSenha(senhaNovaCrypt);
                    }
                } else {
                    usuario.setUsuSenha(CodedValues.USU_SENHA_SERVIDOR_CANCELADA);
                }
                if (senhaApp) {
                    usuario.setUsuDataExpSenhaApp(dataExpiracaoSenha);
                } else {
                    usuario.setUsuDataExpSenha(dataExpiracaoSenha);
                }

            } else {
                // Se nova senha de autorização foi definida, verifica o parâmetro com a quantidade
                // de operações permitidas para a nova senha 2: Default 1 (uma operação).
                final String paramQtdOperacoes = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTD_OPERACOES_VALIDADE_SENHA_AUTORIZACAO, responsavel);
                final Short qtdOperacoes = !TextHelper.isNull(paramQtdOperacoes) ? Short.valueOf(paramQtdOperacoes) : Short.valueOf("1");

                // Se usa múltiplas senhas, grava a nova na tabela apropriada
                if (usaMultiplasSenhasAut) {
                    if (senhaNovaCrypt != null) {
                        // Gerando senha múltipla de autorização
                        try {
                            SenhaAutorizacaoServidorHome.create(usuCodigo, senhaNovaCrypt, dataExpiracaoSenha, qtdOperacoes);
                        } catch (final CreateException ex) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            throw new UsuarioControllerException("mensagem.erro.interno.criacao.nova.senha.autorizacao.usuario", responsavel, ex);
                        }
                    } else {
                        // Cancelando/Consumindo senha múltipla de autorização
                        try {
                            final SenhaAutorizacaoServidor sas = SenhaAutorizacaoServidorHome.findPrimeiroByUsuarioSenha(usuCodigo, senhaUtilizadaCrypt);
                            AbstractEntityHome.remove(sas);
                        } catch (final FindException ex) {
                            throw new UsuarioControllerException("mensagem.erro.nao.possivel.localizar.senha.autorizacao.para.executar.operacao", responsavel, ex);
                        } catch (final RemoveException ex) {
                            throw new UsuarioControllerException("mensagem.erro.interno.consumir.cancelar.senha.autorizacao.para.usuario", responsavel, ex);
                        }
                    }
                } else {
                    usuario.setUsuSenha2(senhaNovaCrypt);
                    usuario.setUsuDataExpSenha2(dataExpiracaoSenha);
                    if (senhaNovaCrypt != null) {
                        usuario.setUsuOperacoesSenha2(qtdOperacoes);
                    } else {
                        usuario.setUsuOperacoesSenha2(Short.valueOf("0"));
                    }
                }
            }

            // Determina parâmetros da ocorrência de usuário a ser gravada.
            String tocCodigoOcorrencia = null;
            String ousObs = null;
            if (!senhaAutorizacaoServidor) {
                if (tocCodigo != null) {
                    tocCodigoOcorrencia = tocCodigo;
                } else {
                    tocCodigoOcorrencia = CodedValues.TOC_ALTERACAO_SENHA_USUARIO;
                }

                if (CodedValues.TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA.equals(tocCodigoOcorrencia)) {
                    ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.cancelamento.senha.nao.utilizada", responsavel);
                } else {
                    ousObs = ApplicationResourcesHelper.getMessage(reiniciacao ? "mensagem.ocorrencia.ous.obs.reinicializacao.senha.usuario" : "mensagem.ocorrencia.ous.obs.alteracao.senha.usuario", responsavel);
                }
            } else {
                if (tocCodigo != null) {
                    tocCodigoOcorrencia = tocCodigo;
                } else {
                    tocCodigoOcorrencia = CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO;
                }

                if (CodedValues.TOC_UTILIZACAO_SENHA_AUTORIZACAO.equals(tocCodigoOcorrencia)) {
                    ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.utilizacao.senha.autorizacao", responsavel);
                } else if (CodedValues.TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA.equals(tocCodigoOcorrencia)) {
                    ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.cancelamento.senha.2.nao.utilizada", responsavel);
                } else if (CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO_TOTEM.equals(tocCodigoOcorrencia)) {
                    ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.senha.autorizacao.host.a.host", responsavel);
                } else {
                    ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.senha.autorizacao", responsavel);
                }
            }

            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(tocCodigoOcorrencia);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusObs(ousObs);
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
            if (tipoMotivoOperacao != null) {
                ocorrencia.setOusObs(ocorrencia.getOusObs() + (ocorrencia.getOusObs().lastIndexOf(".") == (ocorrencia.getOusObs().length() - 1) ? " " : ". ") + tipoMotivoOperacao.getAttribute(Columns.OUS_OBS));
                ocorrencia.setAttribute(Columns.OUS_TMO_CODIGO, tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO));
            }
            if (senhaAutorizacaoServidor && CodedValues.TOC_UTILIZACAO_SENHA_AUTORIZACAO.equals(tocCodigo)) {
                ocorrencia.setUtilizacaoSenhaAutServidor(true);
            }

            // Altera usuário com ocorrência e log
            updateUsuario(usuario, ocorrencia, responsavel);

            // Caso autenticação seja no SSO, altera no SSO também
            if (autenticacaoSSO && !atualizaSenhaSSO(usuario, senhaNova, senhaAtualAberta, responsavel)) {
                LOG.error("Erro ao atualizar senha do usuário no SSO.");
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.alterar.senha.usuario", responsavel);
            }

            if (!senhaAutorizacaoServidor && usuarioSer && ParamSist.paramEquals(CodedValues.TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                //Replica a senha para todos o logins do servidor.
                matriculasUsuariosServidores(usuCodigo, senhaNovaCrypt, true, responsavel);
            }

            // Se for um usuário servidor.
            if (usuarioSer) {
                final String login = usuario.getUsuLogin();
                final int primeiroSeparador = login.indexOf('-');
                final int segundoSeparador = login.indexOf('-', primeiroSeparador + 1);

                final String estabelecimento = login.substring(0, primeiroSeparador > 0 ? primeiroSeparador : 0);
                final String matricula = login.substring((ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel) ? segundoSeparador : primeiroSeparador) + 1, login.length());

                // Verifica parâmetros de sistema que informam se há senha externa e se deve ser atualizada.
                if (!senhaAutorizacaoServidor && ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_ATUALIZA_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel)) {
                    // Se existir, reinicia a senha externa do servidor.
                    try {
                        final String[] parametros = { senhaNovaCrypt != null ? senhaNovaCrypt : CodedValues.USU_SENHA_SERVIDOR_CANCELADA, estabelecimento, matricula };
                        SenhaExterna.getInstance().atualizarSenha(parametros);
                    } catch (final UsuarioControllerException ex) {
                        LOG.error("Erro ao atualizar senha externa do servidor", ex);
                        throw new UsuarioControllerException("mensagem.erro.atualizar.senha.externa.servidor", responsavel, ex);
                    }
                }

                // Verifica se a alteração de senha deve ser comunicada.
                if ((senhaNova != null) && !naoComunicarServidor) {
                    if ((!senhaAutorizacaoServidor && ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER, CodedValues.TPC_SIM, responsavel) && (!reiniciacao || !ParamSist.paramEquals(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, CodedValues.TPC_SIM, responsavel))) ||
                        (senhaAutorizacaoServidor && (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_SMS, responsavel) ||
                                                      ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA, responsavel)))) {
                        try {
                            comunicaAlteracaoSenhaServidor(usuario.getUsuCodigo(), matricula, senhaNova, reiniciacao, senhaAutorizacaoServidor, false, responsavel);
                        } catch (final UsuarioControllerException e) {
                            // Falha no envio da comunicação não impede a alteração da senha.
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (UsuarioControllerException | SSOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.alterar.senha.usuario", responsavel, ex);
        }
    }

    private boolean atualizaSenhaSSO(UsuarioTransferObject usuario, String senhaNova, String senhaAtualAberta,
                                     AcessoSistema responsavel) throws SSOException {
        // Se o token foi setado, é porque o usuário não estava com a senha expirada
        if (!responsavel.getUsuCodigo().equals(usuario.getUsuCodigo()) || (!TextHelper.isNull(responsavel.getSsoToken()) && !TextHelper.isNull(responsavel.getSsoToken().access_token))) {
            if (usuario.getUsuCodigo().equals(responsavel.getUsuCodigo())) {
                return ssoClient.updatePassword(responsavel.getSsoToken(), senhaNova, senhaAtualAberta);
            } else { // se o usuário responsável não é o mesmo que está tendo a senha alterada, deve-se usar a API de alteração de senha com usuário admin.
                // Logar usuário admin para inclusão de novo usuário
                return ssoClient.updatePasswordAsAdmin(usuario.getUsuEmail(), senhaNova);
            }
        } else if (!TextHelper.isNull(senhaAtualAberta)) {
            return ssoClient.updateExpiredPassword(usuario.getUsuEmail(), senhaNova, senhaAtualAberta);
        } else {
            // se senha atual vazia, significa que é uma reinicialização de senha.
            return ssoClient.updatePasswordAsAdmin(usuario.getUsuEmail(), senhaNova);
        }
    }

    /**
     * Sobrecarga do método "alterarSenhaApp()".
     * @param usuCodigo
     * @param senhaNova
     * @param senhaCriptografada
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void alterarSenhaApp(String usuCodigo, String senhaNova, boolean senhaCriptografada, AcessoSistema responsavel) throws UsuarioControllerException {
        alterarSenhaApp(usuCodigo, senhaNova, senhaCriptografada, false, responsavel);
    }

    /**
     * Altera a senha do App.
     * @param usuCodigo
     * @param senhaNova
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void alterarSenhaApp(String usuCodigo, String senhaNova, boolean senhaCriptografada, boolean chamadaMobile, AcessoSistema responsavel) throws UsuarioControllerException {
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);

        try {
            // Tenta buscar por um usuario servidor.
            usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.alterar.senha.usuario", responsavel, ex);
        }

        SenhaHelper.validarForcaSenha(senhaNova, true, responsavel);

        // Criptografa a nova senha para armazenamento no banco.
        String senhaNovaCrypt = null;
        if (senhaNova != null) {
            if (!senhaCriptografada) {
                senhaNovaCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), senhaNova, true, responsavel);
            } else {
                senhaNovaCrypt = senhaNova;
            }
        }

        try {
            final boolean habilitaSenhaApp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SENHA_APP, CodedValues.TPC_SIM, responsavel);

            if (chamadaMobile && habilitaSenhaApp) {
                usuario.setUsuSenhaApp(senhaNovaCrypt);
                usuario.setUsuDataExpSenhaApp(new Date(calculaDataExpiracaoSenha(false, usuario, true, responsavel).getTimeInMillis()));
            } else {
                usuario.setUsuSenha(senhaNovaCrypt);
                usuario.setUsuDataExpSenha(new Date(calculaDataExpiracaoSenha(false, usuario, true, responsavel).getTimeInMillis()));
            }

            // Determina parâmetros da ocorrência de usuário a ser gravada.
            final String tocCodigoOcorrencia = CodedValues.TOC_ALTERACAO_SENHA_APP;
            final String ousObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.senha.app.usuario", responsavel);

            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(tocCodigoOcorrencia);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusObs(ousObs);
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

            updateUsuario(usuario, ocorrencia, responsavel);

            final String matricula = usuario.getUsuLogin().substring(usuario.getUsuLogin().lastIndexOf('-') + 1, usuario.getUsuLogin().length());

            // Verifica se a alteração de senha deve ser comunicada.
            if ((senhaNova != null) && ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER, CodedValues.TPC_SIM, responsavel)) {
                try {
                    comunicaAlteracaoSenhaServidor(usuario.getUsuCodigo(), matricula, senhaNova, false, false, true, responsavel);
                } catch (final UsuarioControllerException e) {
                    // Falha no envio da comunicação não impede a alteração da senha.
                    LOG.error(e.getMessage(), e);
                }
            }
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.alterar.senha.usuario", responsavel, ex);
        }
    }

    /**
     * Valida a nova senha de acordo com as anteriores já salvas
     * @param usuCodigo
     * @param senhaNova
     * @param senhaNovaCrypt
     * @param senhaAutorizacaoServidor
     * @param responsavel
     * @throws UsuarioControllerException
     */
    private void validaSenhasAnteriores(String usuCodigo, String senhaNova, String senhaNovaCrypt, boolean senhaAutorizacaoServidor, AcessoSistema responsavel) throws UsuarioControllerException {
        final TransferObject usuario = obtemUsuarioTipo(usuCodigo, null, responsavel);
        final String tipo = !TextHelper.isNull(usuario.getAttribute("TIPO")) ? usuario.getAttribute("TIPO").toString() : "";
        final String senhaAntigaCrypt = (String) usuario.getAttribute(Columns.USU_SENHA);

        Integer qtdeSenhasAntigasUsuPodeRepetir = 0;
        if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            qtdeSenhasAntigasUsuPodeRepetir = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_CSE_NAO_REPETE, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_CSE_NAO_REPETE, responsavel).toString()) : 0;
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
            qtdeSenhasAntigasUsuPodeRepetir = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_CSA_NAO_REPETE, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_CSA_NAO_REPETE, responsavel).toString()) : 0;
        } else if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
            qtdeSenhasAntigasUsuPodeRepetir = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_SER_NAO_REPETE, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_SENHAS_ANT_USU_SER_NAO_REPETE, responsavel).toString()) : 0;
        } else {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
        }

        // Se for servidor e a senha a ser validada é a de autorização, não requer validação
        if (AcessoSistema.ENTIDADE_SER.equals(tipo) && senhaAutorizacaoServidor) {
            return;
        }

        if (qtdeSenhasAntigasUsuPodeRepetir.compareTo(0) > 0) {
            final List<TransferObject> senhasAntigas = lstSenhasAnterioresUsuarios(usuCodigo, responsavel);

            // Caso não possua nenhuma senha antiga cadastrada e o parâmetro for maior que 1, cadastra a senha anterior
            if (senhasAntigas.isEmpty() && (qtdeSenhasAntigasUsuPodeRepetir.compareTo(1) > 0)) {
                try {
                    SenhaAnteriorHome.create(usuCodigo, senhaAntigaCrypt, DateHelper.getSystemDatetime());
                } catch (final CreateException e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new UsuarioControllerException("mensagem.erro.nao.possivel.armazenar.senha.anterior", responsavel);
                }
            }

            while (qtdeSenhasAntigasUsuPodeRepetir.compareTo(senhasAntigas.size()) < 0) {
                // Se a quantidade senhas anteriores for maior que o parâmetro deve excluir as N mais antigas
                try {
                    final TransferObject excluir = senhasAntigas.remove(0);
                    final SenhaAnteriorId pk = new SenhaAnteriorId(usuCodigo, excluir.getAttribute(Columns.SEA_SENHA).toString());
                    final SenhaAnterior senhaAnterior = SenhaAnteriorHome.findByPrimaryKey(pk);
                    AbstractEntityHome.remove(senhaAnterior);
                } catch (final FindException e) {
                    throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.validar.senha.anterior", responsavel);
                } catch (final RemoveException e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.validar.senha.anterior", responsavel);
                }
            }

            for (final TransferObject senhaAntiga : senhasAntigas) {
                // Se a nova senha for igual a uma das senhas anteriores lança uma exceção
                if (JCrypt.verificaSenha(senhaNova, (String) senhaAntiga.getAttribute(Columns.SEA_SENHA))) {
                    throw new UsuarioControllerException("mensagem.erro.nova.senha.deve.ser.diferente.das.ultimas.arg0.senhas.definidas", responsavel, qtdeSenhasAntigasUsuPodeRepetir.toString());
                }
            }

            // Armazena a nova senha como senha anterior
            try {
                SenhaAnteriorHome.create(usuCodigo, senhaNovaCrypt, DateHelper.getSystemDatetime());
            } catch (final CreateException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.armazenar.senha.anterior", responsavel);
            }

            // Se estiver criando a X senha antiga, excluir a mais antiga
            if (qtdeSenhasAntigasUsuPodeRepetir.compareTo(senhasAntigas.size()) == 0) {
                try {
                    final TransferObject excluir = senhasAntigas.remove(0);
                    final SenhaAnteriorId pk = new SenhaAnteriorId(usuCodigo, excluir.getAttribute(Columns.SEA_SENHA).toString());
                    final SenhaAnterior senhaAnterior = SenhaAnteriorHome.findByPrimaryKey(pk);
                    AbstractEntityHome.remove(senhaAnterior);
                } catch (final FindException e) {
                    throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.validar.senha.anterior", responsavel);
                } catch (final RemoveException e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.excluir.senha.anterior", responsavel);
                }
            }
        }
    }

    /**
     * Comunica ao usuário servidor sobre a alteração de sua senha.
     * @param usuCodigo : Código do usuário
     * @param matricula : Mátricula do servidor
     * @param novaSenha : Nova senha
     * @param reiniciacao : Indica se a senha foi reiniciada ou alterada
     * @param senhaAutorizacaoServidor : Indica que é senha de autorização (senha 2)
     * @param responsavel : Responsável
     * @throws UsuarioControllerException
     */
    private void comunicaAlteracaoSenhaServidor(String usuCodigo, String matricula, String novaSenha, boolean reiniciacao, boolean senhaAutorizacaoServidor, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException {
        String emailDestinatario = null;
        String celularDestinatario = null;
        final String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;

        final ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
        query.usuCodigo = usuCodigo;
        query.rseMatricula = matricula;

        List<TransferObject> servidores = null;
        try {
            servidores = query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.servidor", responsavel, e);
        }

        if (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA, responsavel)) {
            if ((servidores != null) && (servidores.size() == 1)) {
                emailDestinatario = consultarEmailServidor(true, (String) servidores.get(0).getAttribute(Columns.SER_CPF), (String) servidores.get(0).getAttribute(Columns.SER_EMAIL), modoEntrega, responsavel);
            } else {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.servidor", responsavel);
            }

            if (!TextHelper.isNull(emailDestinatario)) {
                try {
                    EnviaEmailHelper.enviarEmailAlteracaoSenhaServidor(emailDestinatario, matricula, novaSenha, reiniciacao, senhaAutorizacaoServidor, senhaApp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.enviar.email.servidor", responsavel, e);
                }
            }

        } else if (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_SMS, responsavel)) {

            if ((servidores != null) && (servidores.size() == 1)) {
                celularDestinatario = (String) servidores.get(0).getAttribute(Columns.SER_CELULAR);
            } else {
                throw new UsuarioControllerException("", responsavel);
            }

            if (!TextHelper.isNull(celularDestinatario)) {
                try {
                    EnviaSMSHelper.enviarSMSSenhaAutorizacao(celularDestinatario, matricula, novaSenha, reiniciacao, senhaAutorizacaoServidor, senhaApp, responsavel);
                } catch (final ZetraException e) {
                    throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, e);
                }
            }

        } else if (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL, responsavel)) {
            if ((servidores != null) && (servidores.size() == 1)) {
                celularDestinatario = (String) servidores.get(0).getAttribute(Columns.SER_CELULAR);
            	emailDestinatario = consultarEmailServidor(true, (String) servidores.get(0).getAttribute(Columns.SER_CPF), (String) servidores.get(0).getAttribute(Columns.SER_EMAIL), modoEntrega, responsavel);
            } else {
                throw new UsuarioControllerException("", responsavel);
            }

            if (!TextHelper.isNull(emailDestinatario)) {
                try {
                    EnviaEmailHelper.enviarEmailAlteracaoSenhaServidor(emailDestinatario, matricula, novaSenha, reiniciacao, senhaAutorizacaoServidor, senhaApp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.enviar.email.servidor", responsavel, e);
                }
            }

            if (!TextHelper.isNull(celularDestinatario)) {
                try {
                    EnviaSMSHelper.enviarSMSSenhaAutorizacao(celularDestinatario, matricula, novaSenha, reiniciacao, senhaAutorizacaoServidor, senhaApp, responsavel);
                } catch (final ZetraException e) {
                    throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, e);
                }
            }
        }
    }

    @Override
    public String gerarSenhaAutorizacaoOtp(String rseCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        return gerarSenhaAutorizacaoOtp(rseCodigo, null, responsavel);
    }

    /**
     * Gera nova senha de autorização do servidor OTP e envia por email e/ou SMS.
     * @param usuCodigo
     * @param tocCodigo
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public String gerarSenhaAutorizacaoOtp(String rseCodigo, String modoEntrega, AcessoSistema responsavel) throws UsuarioControllerException {

        if (!ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
        }

        ServidorTransferObject servidor = null;
        try {
            servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
        } catch (final ServidorControllerException e) {
            throw new UsuarioControllerException(e);
        }

        final List<TransferObject> usuarios = lstUsuariosSerByRseCodigo(rseCodigo, responsavel);

        if ((usuarios == null) || usuarios.isEmpty() || (usuarios.size() > 1)) {
            throw new UsuarioControllerException("mensagem.erro.servidor.usuario.nao.encontrado", responsavel);
        }

        final String serNome = servidor.getSerNome();
        final String usuCodigo = usuarios.get(0).getAttribute(Columns.USU_CODIGO).toString();

        if (TextHelper.isNull(modoEntrega)) {
            modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;
        }

        boolean enviou = false;
        boolean enviaEmail = false;
        boolean enviaCelular = false;
        boolean reconhecimentoFacial = false;

        switch (modoEntrega) {
            case CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL:
                enviaEmail = true;
                break;

            case CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA:
            case CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA:
            case CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA:
                enviaEmail = true;
                break;

            case CodedValues.ALTERACAO_SENHA_AUT_SER_SMS:
                enviaCelular = true;
                break;

            case CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL:
                enviaCelular = true;
                enviaEmail = true;
                break;

            case CodedValues.ALTERACAO_SENHA_AUT_SER_RECONHECIMENTO_FACIAL:
                reconhecimentoFacial = true;
                break;

            default:
                enviaEmail = true;
                break;
        }

        final String novaSenhaPlana = gerarSenhaAutorizacao(usuCodigo, false, true, true, responsavel);

        if (reconhecimentoFacial) {
            return novaSenhaPlana;
        }

        final String email = consultarEmailServidor(enviaEmail, servidor.getSerCpf(), servidor.getSerEmail(), modoEntrega, responsavel);

        final String celular = servidor.getSerCelular();

        // Envia mensagem
        if (enviaCelular && !TextHelper.isNull(celular)) {
            try {
                EnviaSMSHelper.enviarSMSOTP(celular, novaSenhaPlana, responsavel);
                enviou = true;
            } catch (final ZetraException e) {
                // Se deu erro ao enviar, espera possível envio por email
            }
        }

        if (enviaEmail && !TextHelper.isNull(email)) {
            try {
                EnviaEmailHelper.enviarEmailOTPServidor(serNome, email, novaSenhaPlana, responsavel);
                enviou = true;
            } catch (final ViewHelperException e) {
                // Se deu erro ao enviar, mensagem de erro será setada no próximo tratamento
            }
        }

        if (!enviou) {
            if (enviaCelular && enviaEmail) {
                throw new UsuarioControllerException("mensagem.erro.sms.ou.email.enviar", responsavel);
            } else if (enviaCelular) {
                throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel);
            } else if (enviaEmail) {
                throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel);
            }
        }

        return novaSenhaPlana;
    }

    /**
     * Gera nova senha de autorização do servidor.
     * @param usuCodigo
     * @param tocCodigo
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, AcessoSistema responsavel) throws UsuarioControllerException {
        return gerarSenhaAutorizacao(usuCodigo, totem, false, true, responsavel);
    }

    /**
     * Gera nova senha de autorização do servidor.
     * @param usuCodigo
     * @param tocCodigo
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public String gerarSenhaAutorizacao(String usuCodigo, boolean totem, boolean naoComunicarServidor, boolean validaQtdeSenhaAutorizacao, AcessoSistema responsavel) throws UsuarioControllerException {
        if (validaQtdeSenhaAutorizacao) {
            // Valida a quantidade máxima de senhas de autorização múltiplas que podem ser geradas para um servidor
            validaQtdeSenhaAutorizacao(usuCodigo, responsavel);
        }

        final TransferObject servidor = obtemServidorPorUsuario(usuCodigo, responsavel);
        final String srsCodigo = servidor.getAttribute(Columns.SRS_CODIGO).toString();
        if (!CodedValues.SRS_ATIVO.equals(srsCodigo)) {
            throw new UsuarioControllerException("mensagem.senha.servidor.autorizacao.erro.servidor.bloqueado", responsavel);
        }

        // Tamanho máximo da senha de autorização
        final int tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_AUT_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_AUT_SERVIDOR, responsavel).toString()) : 8;

        // Gera nova senha plana: verifica se deve ser apenas numérica
        final boolean senhaAutNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_AUT_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
        final String novaSenhaPlana = senhaAutNumerica ? GeradorSenhaUtil.getPasswordNumber(tamMaxSenhaServidor, responsavel) : GeradorSenhaUtil.getPassword(tamMaxSenhaServidor, AcessoSistema.ENTIDADE_SER, responsavel);

        final String tocCodigo = totem ? CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO_TOTEM : CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO;
        // Altera a senha de autorização do servidor
        this.alterarSenha(usuCodigo, novaSenhaPlana, null, null, false, true, true, false, tocCodigo, null, naoComunicarServidor, false, null, responsavel);

        // Retorna a senha gerada
        return novaSenhaPlana;
    }

    /**
     * Gera nova senha de autorização do servidor (REST)
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public String gerarSenhaAutorizacaoRest(AcessoSistema responsavel) throws UsuarioControllerException {
        final String senhaPlana = gerarSenhaAutorizacao(responsavel.getUsuCodigo(), false, responsavel);

        //No mobile a senha deve ser sempre enviada por e-mail, independente do tpc 362, por isso enviamos o e-mail caso o parâmetro esteja habilitado somente para exibir na tela
        //Caso esteja as duas outras opções o método anterior já enviou o e-mail
        if (!TextHelper.isNull(responsavel.getSerEmail()) && (senhaPlana != null) && ParamSist.paramEquals(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA, responsavel)) {
            try {
                comunicaAlteracaoSenhaServidor(responsavel.getUsuCodigo(), responsavel.getRseMatricula(), senhaPlana, false, true, false, responsavel);
            } catch (final UsuarioControllerException e) {
                // Falha no envio da comunicação não impede a alteração da senha.
                LOG.error(e.getMessage(), e);
            }
        }

        return senhaPlana;
    }

    /**
     * Valida a quantidade máxima de senhas de autorização múltiplas que podem ser geradas para um servidor.
     * Validação somente é executada caso o sistema utilize senhas múltiplas de autorização.
     *
     * @param usuCodigo
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void validaQtdeSenhaAutorizacao(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        // Verifica se utiliza múltiplas senhas de autorização, e caso utilize
        // se é possível gerar nova senha de autorização
        if (ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            int qtdMaxSenhaAut = 5;
            try {
                // Obtém parâmetro com a quantidade máxima de senhas de autorização: Default = 5
                final Object qtdMaxSenhaAutParam = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MAX_MULTIPLAS_SENHAS_AUTORIZACAO, responsavel);
                qtdMaxSenhaAut = !TextHelper.isNull(qtdMaxSenhaAutParam) ? Integer.parseInt(qtdMaxSenhaAutParam.toString()) : 5;
            } catch (final NumberFormatException ex) {
                LOG.warn("Valor incorreto para o parâmetro de sistema: " + CodedValues.TPC_QTD_MAX_MULTIPLAS_SENHAS_AUTORIZACAO);
                qtdMaxSenhaAut = 5;
            }
            try {
                // Recupera as senhas de autorização do servidor
                final ListaSenhaAutorizacaoServidorQuery query = new ListaSenhaAutorizacaoServidorQuery();
                query.usuCodigo = usuCodigo;
                query.count = true;
                final int total = query.executarContador();
                // Caso já exceda o total, não permite geração de nova senha
                if (total >= qtdMaxSenhaAut) {
                    throw new UsuarioControllerException("mensagem.senha.servidor.autorizacao.erro.qtd.excedida", responsavel);
                }
            } catch (final HQueryException ex) {
                throw new UsuarioControllerException("mensagem.erro.interno.listar.senhas.autorizacao.usuario", responsavel, ex);
            }
        }
    }

    /**
     * Altera a senha de autorização do servidor.
     * @param usuCodigo
     * @param senhaNova
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void alterarSenhaAutorizacao(String usuCodigo, String senhaNova, AcessoSistema responsavel) throws UsuarioControllerException {
        this.alterarSenha(usuCodigo, senhaNova, null, null, false, false, true, false, CodedValues.TOC_ALTERACAO_SENHA_AUTORIZACAO, null, responsavel);
    }

    /**
     * Registra a utilização da senha de autorização do servidor através de sua remoção.
     * @param usuCodigo : Código do usuário servidor.
     * @param senhaUtilizada : Senha utilizada que será consumida
     * @param responsavel : Responsável pela operação
     * @throws UsuarioControllerException
     */
    @Override
    public void consomeSenhaAutorizacao(String usuCodigo, String senhaUtilizada, AcessoSistema responsavel) throws UsuarioControllerException {
        this.alterarSenha(usuCodigo, null, senhaUtilizada, null, false, true, true, true, CodedValues.TOC_UTILIZACAO_SENHA_AUTORIZACAO, null, responsavel);
    }

    /**
     * Cancela a senha de autorização do servidor.
     * @param usuCodigo : Código do usuário servidor.
     * @param senhaUtilizada : Senha não utilizada que será cancelada (criptografada)
     * @param responsavel : Responsável pela operação
     * @throws UsuarioControllerException
     */
    @Override
    public void cancelaSenhaAutorizacao(String usuCodigo, String senhaNaoUtilizada, AcessoSistema responsavel) throws UsuarioControllerException {
        this.alterarSenha(usuCodigo, null, senhaNaoUtilizada, null, false, true, true, true, CodedValues.TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA, null, responsavel);
    }

    /**
     * Cancela a senha de autorização do servidor. (REST)
     * @param dataCriacao : Data de criação da senha que será cancelada (criptografada)
     * @param responsavel : Responsável pela operação
     * @throws UsuarioControllerException
     */
    @Override
    public void cancelaSenhaAutorizacaoRest(java.util.Date dataCriacao, AcessoSistema responsavel) throws UsuarioControllerException {
        SenhaAutorizacaoServidor senha = new SenhaAutorizacaoServidor();
        try {
            senha = SenhaAutorizacaoServidorHome.findByPrimaryKey(new SenhaAutorizacaoServidorId(responsavel.getUsuCodigo(), dataCriacao));
        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.localizar.senha.autorizacao.para.executar.operacao", responsavel, e);
        }

        this.alterarSenha(responsavel.getUsuCodigo(), null, senha.getSasSenha(), null, false, true, true, true, CodedValues.TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA, null, responsavel);
    }

    /**
     * Altera a quantidade de operações disponíveis para a senha de autorização do servidor
     * @param usuCodigo
     * @param qtdOperacoes
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void alterarOperacoesSenhaAutorizacao(String usuCodigo, Short qtdOperacoes, String senhaUtilizada, AcessoSistema responsavel) throws UsuarioControllerException {
        // Verifica se utiliza múltiplas senhas de autorização
        final boolean usaMultiplasSenhasAut = ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Busca o registro do usuário servidor a ser alterado
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
        Short qtdOperacoesAtual = null;
        SenhaAutorizacaoServidor sas = null;

        if (usaMultiplasSenhasAut) {
            try {
                final List<SenhaAutorizacaoServidor> senhasAut = SenhaAutorizacaoServidorHome.findByUsuCodigo(usuCodigo);
                for (final SenhaAutorizacaoServidor senhaAut : senhasAut) {
                    if (JCrypt.verificaSenha(senhaUtilizada, senhaAut.getSasSenha())) {
                        sas = senhaAut;
                        qtdOperacoesAtual = sas.getSasQtdOperacoes();
                        break;
                    }
                }
                if (sas == null) {
                    throw new UsuarioControllerException("mensagem.senha.servidor.autorizacao.nao.encontrada", responsavel);
                }
            } catch (final FindException ex) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.localizar.senha.autorizacao.para.executar.operacao", responsavel, ex);
            }
        } else {
            qtdOperacoesAtual = usuario.getUsuOperacoesSenha2();
        }

        // Se a quantidade de operações é maior que zero e está sendo reduzida,
        // cria ocorrência de utilização da senha, pois ela ainda está válida e
        // não será "consumida" pela operação
        OcorrenciaUsuarioTransferObject ocorrencia = null;
        if ((qtdOperacoes != null) && (qtdOperacoesAtual != null) && (qtdOperacoes > 0) && (qtdOperacoesAtual > qtdOperacoes)) {
            ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(CodedValues.TOC_UTILIZACAO_SENHA_AUTORIZACAO);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.utilizacao.senha.autorizacao", responsavel));
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
            ocorrencia.setUtilizacaoSenhaAutServidor(true);
        }

        if (usaMultiplasSenhasAut) {
            try {
                sas.setSasQtdOperacoes(qtdOperacoes);
                AbstractEntityHome.update(sas);
                if (ocorrencia != null) {
                    createOcorrenciaUsuario(ocorrencia, responsavel);
                }
            } catch (final UpdateException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.atualizar.senha.autorizacao", responsavel, ex);
            }
        } else {
            // Atualiza o registro no usuário
            usuario.setUsuOperacoesSenha2(qtdOperacoes);
            updateUsuario(usuario, ocorrencia, responsavel);
        }
    }

    /**
     * Lista as senhas de autorização do servidor não expiradas e com qtd
     * de operações maior que zero.
     * @param usuCodigo : código do usuário servidor
     * @param responsavel : responsável pela operação
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> lstSenhaAutorizacaoServidor(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaSenhaAutorizacaoServidorQuery query = new ListaSenhaAutorizacaoServidorQuery();
            query.usuCodigo = usuCodigo;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.interno.listar.senhas.autorizacao.usuario", responsavel, e);
        }
    }

    /**
     * Lista as senhas de autorização do servidor não expiradas e com qtd (REST)
     * de operações maior que zero.
     * @param responsavel : responsável pela operação
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> lstSenhaAutorizacaoServidorRest(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaSenhaAutorizacaoServidorQuery query = new ListaSenhaAutorizacaoServidorQuery();
            query.usuCodigo = responsavel.getUsuCodigo();
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.interno.listar.senhas.autorizacao.usuario", responsavel, e);
        }
    }

    /**
     * Retorna a quantidade de senha de autorização de usuário servidor gerada no dia atual via host a host.
     *
     * @param usuCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public int qtdeSenhaAutorizacaoUsuSerDiaHostAHost(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery query = new ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery();
            query.usuCodigo = usuCodigo;
            return query.executarContador();
        } catch (final HQueryException e) {
            // TODO Alterar mensagem da exceção
            throw new UsuarioControllerException("mensagem.erro.interno.listar.senhas.autorizacao.usuario", responsavel, e);
        }
    }

    /**
     * Recupera os dados da senha de autorização do servidor.
     * @param usuCodigo : código do usuário servidor
     * @param sasSenha : senha criptografada
     * @param responsavel : responsável pela operação
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public TransferObject obtemSenhaAutorizacaoServidor(String usuCodigo, String senha, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaSenhaAutorizacaoServidorQuery query = new ListaSenhaAutorizacaoServidorQuery();
            query.usuCodigo = usuCodigo;
            query.sasSenha = null;
            query.senhasValidas = false;
            //query.maxResults = 1;
            final List<TransferObject> senhasAutServidor = query.executarDTO();
            if ((senhasAutServidor != null) && (senhasAutServidor.size() > 0)) {
                for (final TransferObject autorizacao : senhasAutServidor) {
                    final String sasSenha = (String) autorizacao.getAttribute(Columns.SAS_SENHA);
                    if (JCrypt.verificaSenha(senha, sasSenha)) {
                        return autorizacao;
                    }
                }
            }
            return null;
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.interno.listar.senhas.autorizacao.usuario", responsavel, e);
        }
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
    @Override
    public String createProtocoloSenhaAutorizacao(String psaCodigo, String usuCodigoAfetado, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            ProtocoloSenhaAutorizacaoHome.create(psaCodigo, usuCodigoAfetado, responsavel.getUsuCodigo());

            return psaCodigo;
        } catch (final CreateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    /**
     * Retorna protocolo de senha de autorização.
     *
     * @param psaCodigo
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public TransferObject getProtocoloSenhaAutorizacao(String psaCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemProtocoloSenhaAutorizacaoQuery query = new ObtemProtocoloSenhaAutorizacaoQuery();
            query.psaCodigo = psaCodigo;
            final List<TransferObject> lista = query.executarDTO();
            return (lista != null) && !lista.isEmpty() ? lista.get(0) : null;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    /**
     * Invalida as senhas de autorização dos servidores de acordo com o prazo de validade
     * definido no parâmetro de sistema.
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> listarSenhasExpiradasCancelamentoAut(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            List<TransferObject> senhasAutorizacaoExpiradas = null;
            if (ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                // Recupera a lista de senhas de autorização já expiradas
                final ListaSenhaAutorizacaoServidorExpiradaQuery query = new ListaSenhaAutorizacaoServidorExpiradaQuery();
                senhasAutorizacaoExpiradas = query.executarDTO();

            } else {
                // Recupera a lista de usuários com senha de autorização vencida.
                final ListaUsuarioSerSenhaAutExpiradaQuery query = new ListaUsuarioSerSenhaAutExpiradaQuery();
                senhasAutorizacaoExpiradas = query.executarDTO();
            }
            return senhasAutorizacaoExpiradas;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.recuperar.senhas.nao.utilizadas", responsavel, ex);
        }
    }

    /**
     * Altera a chave de recuperação de senha do usuário.
     * @param usuCodigo : Código do usuário
     * @param codSenha  : Código de recuperação de senha
     * @param responsavel : Usuário que está realizando a recuperação de senha
     * @throws UsuarioControllerException
     */
    @Override
    public void alteraChaveRecupSenha(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        // Busca o usuário pela chave primária
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);

        if (!CodedValues.STU_ATIVO.equals(usuario.getStuCodigo())) {
            throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
        }

        try {
            // Salva chave para recuperação de senha.
            usuario.setUsuChaveRecuperarSenha(codSenha);

            if (!TextHelper.isNull(codSenha)) {
                usuario.setUsuDataRecSenha(DateHelper.getSystemDatetime());
            } else {
                usuario.setUsuDataRecSenha(null);
            }

            // Determina parâmetros da ocorrência de usuário a ser gravada.
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
            if (!TextHelper.isNull(codSenha)) {
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.chave.recup.senha.usuario", responsavel));
            } else {
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.utilizacao.chave.recup.senha.usuario", responsavel));
            }

            updateUsuario(usuario, ocorrencia, responsavel);

        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.alterar.chave.recuperacao.senha.usuario", responsavel, ex);
        }
    }

    /**
     * Altera a chave de recuperação de senha do usuário no auto-desbloqueio
     * @param usuCodigo : Código do usuário
     * @param codSenha  : Código de recuperação de senha
     * @param responsavel : Usuário que está realizando a recuperação de senha
     * @throws UsuarioControllerException
     */
    @Override
    public void alteraChaveRecupSenhaAutoDesbloqueio(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        // Busca o usuário pela chave primária
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);

        if (!CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(usuario.getStuCodigo())) {
            throw new UsuarioControllerException("mensagem.auto.desbloqueio.servidor.nao.pode.desbloqueado", responsavel);
        }

        try {
            // Salva chave para recuperação de senha.
            usuario.setUsuChaveRecuperarSenha(codSenha);

            if (!TextHelper.isNull(codSenha)) {
                usuario.setUsuDataRecSenha(DateHelper.getSystemDatetime());
            } else {
                usuario.setUsuDataRecSenha(null);
            }

            // Determina parâmetros da ocorrência de usuário a ser gravada.
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
            if (!TextHelper.isNull(codSenha)) {
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.chave.recup.senha.usuario", responsavel));
            } else {
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.utilizacao.chave.recup.senha.usuario", responsavel));
            }

            updateUsuario(usuario, ocorrencia, responsavel);

        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.alterar.chave.recuperacao.senha.usuario", responsavel, ex);
        }
    }

    private boolean validarChaveRecupSenha(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario = findUsuario(usuario, responsavel.getTipoEntidade(), responsavel);

        if (!CodedValues.STU_ATIVO.equals(usuario.getStuCodigo())) {
            throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
        }

        // Busca chave do usuario salvo em banco de dados.
        final String usuChave = usuario.getUsuChaveRecuperarSenha() == null ? "" : usuario.getUsuChaveRecuperarSenha();
        // Verificar se os codigos de recuperação são iguais.
        if (usuChave.equals(codSenha) && !"".equals(usuChave) && !"".equals(codSenha)) {
            // verifica se chave de recuperação já passou do limite de 1 dia para ser consumida
            final java.util.Date dataCriacaoChave = usuario.getUsuDataRecSenha();
            if (DateHelper.dayDiff(DateHelper.getSystemDatetime(), dataCriacaoChave) > 1) {
                return false;
            }

            alteraChaveRecupSenha(usuCodigo, "", responsavel);
            return true;
        } else {
            return false;
        }
    }

    private boolean validarChaveRecupSenhaAutoDesbloqueio(String usuCodigo, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario = findUsuario(usuario, responsavel.getTipoEntidade(), responsavel);

        if (!CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE.equals(usuario.getStuCodigo())) {
            throw new UsuarioControllerException("mensagem.auto.desbloqueio.servidor.nao.pode.desbloqueado", responsavel);
        }

        // Busca chave do usuario salvo em banco de dados.
        final String usuChave = usuario.getUsuChaveRecuperarSenha() == null ? "" : usuario.getUsuChaveRecuperarSenha();
        // Verificar se os codigos de recuperação são iguais.
        if (usuChave.equals(codSenha) && !"".equals(usuChave) && !"".equals(codSenha)) {
            // verifica se chave de recuperação já passou do limite de 1 dia para ser consumida
            final java.util.Date dataCriacaoChave = usuario.getUsuDataRecSenha();
            if (DateHelper.dayDiff(DateHelper.getSystemDatetime(), dataCriacaoChave) > 1) {
                return false;
            }

            alteraChaveRecupSenhaAutoDesbloqueio(usuCodigo, "", responsavel);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Envia ao usuário servidor link para reinicializar a senha na recuperação de senha.
     * @param usuCodigo Código do usuário
     * @param matricula Mátricula do servidor
     * @param link Link para recuperar senha
     * @param codigo Codigo para verificação do usuario
     * @param responsavel Responsável
     * @throws UsuarioControllerException
     */
    @Override
    public void enviaLinkReinicializarSenhaSer(String usuCodigo, String matricula, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException {
        String emailDestinatario = null;
        String[] serNome = null;
        String serPrimeiroNome = null;

        final ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();

        query.usuCodigo = usuCodigo;
        query.rseMatricula = matricula;
        link = link + "&tipo=recuperar&cod_recuperar=" + codigo;

        List<TransferObject> servidores = null;
        try {
            servidores = query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.servidor", responsavel, e);
        }

        if ((servidores != null) && (servidores.size() == 1)) {
            emailDestinatario = (String) servidores.get(0).getAttribute(Columns.SER_EMAIL);
            serNome = TextHelper.split((String) servidores.get(0).getAttribute(Columns.SER_NOME), " ");
            serPrimeiroNome = serNome[0].trim().toString();
        } else {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.servidor", responsavel);
        }

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarLinkRecuperacaoSenhaServidor(emailDestinatario, serPrimeiroNome, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException("mensagem.erro.enviar.email.servidor", responsavel, e);
            }
        }
    }

    /**
     * Envia ao usuário servidor link para reinicializar a senha na recuperação de senha.
     * @param usuCodigo Código do usuário
     * @param matricula Mátricula do servidor
     * @param novaSenha Nova senha
     * @param reiniciacao Indica se a senha foi reiniciada ou alterada.
     * @param responsavel Responsável
     * @throws UsuarioControllerException
     */
    @Override
    public void enviaLinkReinicializarSenhaUsu(String usuCodigo, String login, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException {
        enviaLinkReinicializarSenhaUsuEmail(usuCodigo, login, link, codigo, null, responsavel);
    }

    private void enviaLinkReinicializarSenhaUsuEmail(String usuCodigo, String login, String link, String codigo, String emailDestinatario, AcessoSistema responsavel) throws UsuarioControllerException {

        link = link + "&tipo=recuperar&cod_recuperar=" + codigo;

        if (emailDestinatario == null) {
            final ObtemUsuarioQuery query = new ObtemUsuarioQuery();
            query.usuCodigo = usuCodigo;

            List<TransferObject> usuario = null;
            try {
                usuario = query.executarDTO();
            } catch (final HQueryException e) {
                throw new UsuarioControllerException("mensagem.erro.recuperar.email.usuario", responsavel, e);
            }

            if ((usuario != null) && (usuario.size() == 1)) {
                emailDestinatario = (String) usuario.get(0).getAttribute(Columns.USU_EMAIL);
            } else {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.usuario", responsavel);
            }
        }

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarEmailLinkRecuperarSenha(emailDestinatario, login, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException("mensagem.erro.falha.enviar.email", responsavel, e);
            }
        }
    }

    /**
     * Envia ao novo usuário cirado link para definir a senha de acessi ao sistema
     * @param usuCodigo
     * @param emailDestinatario
     * @param usuLogin - Login do novo usuáro criado
     * @param usuNome - Nome do novo usuário criado
     * @param entidadeNome - Nome da entidade na qual o usuário foi criado.
     * @param link - link a ser enviado no e-mail que direciona para a página de definição de senha.
     * @param codigo - token de fluxo de acesso da URL
     * @param responsavel
     * @throws UsuarioControllerException
     */
    private void enviaLinkIniciacaoSenhaNovoUsuario(String usuCodigo, String emailDestinatario, String usuLogin, String usuNome, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException {
        link = link + "&tipo=recuperar&cod_recuperar=" + codigo;

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarEmailLinkDefinirSenhaNovoUsuario(emailDestinatario, usuLogin, usuNome, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException("mensagem.erro.falha.enviar.email", responsavel, e);
            }
        }
    }

    /**
     * Envia ao usuário servidor link para reinicializar a senha no auto desbloqueio
     * @param usuCodigo Código do usuário
     * @param matricula Mátricula do servidor
     * @param link Link para recuperar senha
     * @param codigo Codigo para verificação do usuario
     * @param responsavel Responsável
     * @throws UsuarioControllerException
     */
    @Override
    public void enviaLinkReinicializarSenhaSerAutoDesbloqueio(String usuCodigo, String matricula, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException {
        String emailDestinatario = null;

        final ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();

        query.usuCodigo = usuCodigo;
        query.rseMatricula = matricula;
        link = link + "&tipo=recuperar&cod_recuperar=" + codigo + "&autodesbloqueio=true";

        List<TransferObject> servidores = null;
        try {
            servidores = query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.servidor", responsavel, e);
        }

        if ((servidores != null) && (servidores.size() == 1)) {
            emailDestinatario = (String) servidores.get(0).getAttribute(Columns.SER_EMAIL);
        } else {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.servidor", responsavel);
        }

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarEmailLinkRecuperarSenhaAutoDesbloqueio(emailDestinatario, matricula, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException("mensagem.erro.enviar.email.servidor", responsavel, e);
            }
        }
    }

    /**
     * Envia ao usuário de outras entidades que não servidor um link para reinicializar a senha no auto desbloqueio
     * @param usuLogin login do usuário
     * @param link Link para recuperar senha
     * @param codigo Codigo para verificação do usuario
     * @param responsavel Responsável
     * @throws UsuarioControllerException
     */
    @Override
    public void enviaLinkReinicializarSenhaUsuAutoDesbloqueio(String usuLogin, String link, String codigo, AcessoSistema responsavel) throws UsuarioControllerException {
        String emailDestinatario = null;

        final ObtemUsuarioQuery query = new ObtemUsuarioQuery();

        query.usuLogin = usuLogin;
        link = link + "&tipo=recuperar&cod_recuperar=" + codigo + "&autodesbloqueio=true";

        List<TransferObject> usuarios = null;
        try {
            usuarios = query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.usuario", responsavel, e);
        }

        if ((usuarios != null) && (usuarios.size() == 1)) {
            emailDestinatario = (String) usuarios.get(0).getAttribute(Columns.USU_EMAIL);
        } else {
            throw new UsuarioControllerException("mensagem.erro.recuperar.email.usuario", responsavel);
        }

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarEmailLinkRecuperarSenhaAutoDesbloqueio(emailDestinatario, null, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException(e);
            }
        }
    }

    @Override
    public List<TransferObject> lstStatusLogin(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaStatusLoginQuery query = new ListaStatusLoginQuery();
            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.possiveis.status.login", responsavel, e);
        }
    }

    @Override
    public TransferObject obtemUsuarioTipo(String usuCodigo, String usuLogin, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioTipoQuery query = new ObtemUsuarioTipoQuery();
            query.usuCodigo = usuCodigo;
            query.usuLogin = usuLogin;

            final List<TransferObject> lista = query.executarDTO();
            if ((lista == null) || lista.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
            }

            return lista.get(0);
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel, e);
        }
    }

    private List<TransferObject> lstSenhasAnterioresUsuarios(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaSenhasAntigasUsuarioQuery query = new ListaSenhasAntigasUsuarioQuery();
            query.usuCodigo = usuCodigo;

            return query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.possiveis.status.login", responsavel, e);
        }
    }

    @Override
    public void alteraDataUltimoAcessoSistema(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final java.util.Date dataUltAcesso = DateHelper.getSystemDatetime();
            UsuarioHome.updateUsuDataUltimoAcesso(dataUltAcesso, responsavel.getUsuCodigo());
            HistoricoLoginHome.create(responsavel.getUsuCodigo(), dataUltAcesso, responsavel.getCanal());
        } catch (final UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.alterar.data.acesso.usuario", responsavel, e);
        } catch (final CreateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    /**
     * Lista usuários cujo período de inatividade excede o permitido via configuração de sistema
     * @param usuCodigo - caso se queira pesquisar um usuário específico. Caso contrário, informar null
     * @param dataLimiteBloqueio - data a se verificar com o limite de inatividade configurado no sisteema
     * @param responsavel - responsavel pela operação
     */
    @Override
    public List<TransferObject> listarBloqueiaUsuariosInativos(String usuCodigo, java.util.Date dataLimiteBloqueio, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuarioInativoQuery query = new ListaUsuarioInativoQuery();
            query.usuCodigo = usuCodigo;
            query.dataLimiteBloqueio = dataLimiteBloqueio;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.bloquear.usuarios.inativos.sistema", responsavel, e);
        }
    }

    /**
     * Verifica se exite servidor com CPF informado e matricula nao excluida.
     * @param serCpf CPF a ser pesquisado.
     * @param responsavel Responsavel pela operaca
     * @return Verdadeiro se existir.
     * @throws UsuarioControllerException Excecao padrao.
     */
    private boolean existeServidor(String serCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaServidoresQuery query = new ListaServidoresQuery();
            query.serCpf = serCpf;
            final List<TransferObject> usuarios = query.executarDTO();
            return (usuarios != null) && (usuarios.size() > 0);
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel, e);
        }
    }

    /**
     * Obtem usuarios de CSA/COR que possuem um CPF igual ao informado pelo parâmetro "usuCpf",
     * em uma entidade diferente de (ou igual a) "tipoEntidade/codigoEntidade";
     * ou que possuem CPF igual a de algum servidor não excluído.
     * @param usuCodigo : Ignora o usuário representado por este código
     * @param usuCpf : CPF a ser pesquisado
     * @param validaServidor : Flag para validar se o servidor com mesmo CPF do usuario possui matricula não excluída.
     * @param tipoEntidade : tipo da entidade do usuário
     * @param codigoEntidade : código da entidade do usuário
     * @param mesmaEntidade :
     * @param responsavel: Responsavel pela operacao
     * @return Lista de usuarios csa/cor.
     * @throws UsuarioControllerException
     */
    private List<TransferObject> lstUsuarioCsaCor(String usuCodigo, String usuCpf, boolean validaServidor, String tipoEntidade, String codigoEntidade, boolean mesmaEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioCsaCorQuery query = new ObtemUsuarioCsaCorQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.mesmaEntidade = mesmaEntidade;
            query.usuCodigo = usuCodigo;
            query.usuCpf = usuCpf;
            query.validaServidor = validaServidor;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel, e);
        }
    }

    /**
     * Obtem usuarios de CSE/ORG que possuem um CPF igual ao informado pelo parâmetro "usuCpf".
     * @param usuCodigo: Ignora o usuário representado por este código
     * @param usuCpf: CPF a ser pesquisado
     * @param responsavel: Responsavel pela operacao
     * @return Lista de usuarios cse/org.
     * @throws UsuarioControllerException
     */
    private List<TransferObject> lstUsuarioCseOrg(String usuCodigo, String usuCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioCseOrgQuery query = new ObtemUsuarioCseOrgQuery();
            query.usuCodigo = usuCodigo;
            query.usuCpf = usuCpf;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel, e);
        }
    }

    /**
     * Obtem usuarios de SUPorte que possuem um CPF igual ao informado pelo parâmetro "usuCpf".
     * @param usuCodigo: Ignora o usuário representado por este código
     * @param usuCpf: CPF a ser pesquisado
     * @param responsavel: Responsavel pela operacao
     * @return Lista de usuarios suporte.
     * @throws UsuarioControllerException
     */
    private List<TransferObject> lstUsuarioSup(String usuCodigo, String usuCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemUsuarioSupQuery query = new ObtemUsuarioSupQuery();
            query.usuCodigo = usuCodigo;
            query.usuCpf = usuCpf;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel, e);
        }
    }

    /**
     * Se o parametro exigir que o CPF cadastrado para um servidor não seja igual a um CPF de usuário CSA/COR ativo, esse usuário deve ser bloqueado.
     * @param serCpf CPF do servidor a ser pesquisado.
     * @param responsavel Responsavel pela operacao.
     * @throws UsuarioControllerException Excecao padrao.
     */
    @Override
    public void bloqueiaUsuarioCsaComCPFServidor(String serCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        final boolean exigeUnicidadeCPF = ParamSist.getBoolParamSist(CodedValues.TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_SER, responsavel);
        if (exigeUnicidadeCPF) {
            final List<TransferObject> usuarios = lstUsuarioCsaCor(null, serCpf, true, null, null, false, responsavel);
            for (final TransferObject to : usuarios) {
                final String usuCodigo = (String) to.getAttribute(Columns.USU_CODIGO);
                final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
                usuario.setStuCodigo(CodedValues.STU_BLOQUEADO_POR_CSE);
                usuario.setUsuTipoBloq(ApplicationResourcesHelper.getMessage(responsavel.isSup() ? "mensagem.usuario.bloqueado.sup" : "mensagem.usuario.bloqueado.cse", responsavel));

                // Grava ocorrência de bloqueio do usuário pelo consignante
                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo(usuCodigo);
                ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_USUARIO_POR_CSE);
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage(responsavel.isSup() ? "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.sup" : "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.cse", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                updateUsuario(usuario, ocorrencia, responsavel);
            }
        }
    }

    /**
     * Método que encapsula verificação da obrigatoriedade de cadastro de IP na criação/atualização
     * de um usuário, assim como validação sintática do IP.
     * @param tipo
     * @param usuLogin
     * @param codigoEntidade
     * @param ipList
     * @param ddnsList
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void validaIpAcessoResponsavel(String tipo, String usuLogin, String codigoEntidade, String ipList, String ddnsList, AcessoSistema responsavel) throws UsuarioControllerException {
        // Confere se cadastro de IPs (ou DDNS) de acesso é obrigatório de acordo com o tipo da entidade
        if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            final boolean paramExigeIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CADASTRO_IP_CSE_ORG, responsavel);
            final boolean paramVerificaIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG, responsavel);
            final boolean paramCadUsuSobrepoeGeral = !ParamSist.paramEquals(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, CodedValues.TPC_NAO, responsavel);

            // Se um dos parametros 223 ou 225 for SIM, entao deve exigir o cadastro de IP
            final boolean exigeIpAcesso = paramExigeIpAcesso || paramVerificaIpAcesso;

            if (exigeIpAcesso && ((TextHelper.isNull(ipList) && TextHelper.isNull(ddnsList)) || !paramCadUsuSobrepoeGeral)) {
                try {
                    if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                        ConsignanteTransferObject cse = null;
                        try {
                            cse = consignanteController.findConsignante(codigoEntidade, responsavel);
                        } catch (final ConsignanteControllerException ex) {
                            if (!TextHelper.isNull(usuLogin)) {
                                final CustomTransferObject usuario = findUsuario(usuLogin, responsavel);
                                final String cseCodigo = !TextHelper.isNull(usuario.getAttribute(Columns.UCE_CSE_CODIGO)) ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : null;
                                if (cseCodigo != null) {
                                    cse = consignanteController.findConsignante(cseCodigo, responsavel);
                                }
                            }
                        }
                        if ((cse == null) || (TextHelper.isNull(cse.getCseIPAcesso()) && TextHelper.isNull(cse.getCseDDNSAcesso()))) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }
                    } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                        OrgaoTransferObject org = null;
                        try {
                            org = consignanteController.findOrgao(codigoEntidade, responsavel);
                        } catch (final ConsignanteControllerException ex) {
                            if (!TextHelper.isNull(usuLogin)) {
                                final CustomTransferObject usuario = findUsuario(usuLogin, responsavel);
                                final String orgCodigo = !TextHelper.isNull(usuario.getAttribute(Columns.UOR_ORG_CODIGO)) ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : null;
                                if (orgCodigo != null) {
                                    org = consignanteController.findOrgao(orgCodigo, responsavel);
                                }
                            }
                        }
                        if ((org == null) || (TextHelper.isNull(org.getOrgIPAcesso()) && TextHelper.isNull(org.getOrgDDNSAcesso()))) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }
                    }
                } catch (final ConsignanteControllerException ex) {
                    throw new UsuarioControllerException(ex);
                }
            }
        } else if ((AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) && (TextHelper.isNull(ipList) && TextHelper.isNull(ddnsList))) {
            final boolean paramExigeIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CADASTRO_IP_CSA_COR, responsavel);
            final boolean paramVerificaIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, responsavel);

            // Se um dos parametros 222 ou 224 for SIM, entao deve exigir o cadastro de IP
            boolean exigeIpAcesso = paramExigeIpAcesso || paramVerificaIpAcesso;

            try {
                // Nao da erro se tiver IP (ou DDNS) cadastrado para CSA ou COR
                if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    CorrespondenteTransferObject cor = null;
                    try {
                        cor = consignatariaController.findCorrespondente(codigoEntidade, responsavel);
                    } catch (final ConsignatariaControllerException ex) {
                        if (!TextHelper.isNull(usuLogin)) {
                            final CustomTransferObject usuario = findUsuario(usuLogin, responsavel);
                            final String corCodigo = !TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO)) ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : null;
                            if (corCodigo != null) {
                                cor = consignatariaController.findCorrespondente(corCodigo, responsavel);
                            }
                        }
                    }
                    if (cor != null) {
                        // Se o campo corExigeEnderecoAcesso estiver preenchido, ele prevalece sobre os parametros 222 e 224
                        exigeIpAcesso = cor.getCorExigeEnderecoAcesso() != null ? CodedValues.TPC_SIM.equals(cor.getCorExigeEnderecoAcesso()) : exigeIpAcesso;

                        if (exigeIpAcesso && TextHelper.isNull(cor.getCorIPAcesso()) && TextHelper.isNull(cor.getCorDDNSAcesso())) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }
                    }
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    ConsignatariaTransferObject csa = null;
                    try {
                        csa = consignatariaController.findConsignataria(codigoEntidade, responsavel);
                    } catch (final ConsignatariaControllerException ex) {
                        if (!TextHelper.isNull(usuLogin)) {
                            final CustomTransferObject usuario = findUsuario(usuLogin, responsavel);
                            final String csaCodigo = !TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO)) ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : null;
                            if (csaCodigo != null) {
                                csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
                            }
                        }
                    }
                    if (csa != null) {
                        // Se o campo csaExigeEnderecoAcesso estiver preenchido, ele prevalece sobre os parametros 222 e 224
                        exigeIpAcesso = csa.getCsaExigeEnderecoAcesso() != null ? CodedValues.TPC_SIM.equals(csa.getCsaExigeEnderecoAcesso().toString()) : exigeIpAcesso;

                        if (exigeIpAcesso && TextHelper.isNull(csa.getCsaIPAcesso()) && TextHelper.isNull(csa.getCsaDDNSAcesso())) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }
                    }
                }
            } catch (final ConsignatariaControllerException ex) {
                throw new UsuarioControllerException(ex);
            }
        }

        if (!TextHelper.isNull(ipList)) {
            final List<String> ipsAcesso = Arrays.asList(ipList.split(";"));
            if (!JspHelper.validaListaIps(ipsAcesso)) {
                throw new UsuarioControllerException("mensagem.erro.endereco.ip.invalido", responsavel);
            }
        }
    }

    @Override
    public List<TransferObject> lstFuncoesPermitidasUsuario(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstFuncoesPermitidas(tipo, false, codigoEntidade, responsavel);
    }

    @Override
    public List<TransferObject> lstFuncoesPermitidasPerfil(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        return lstFuncoesPermitidas(tipo, true, codigoEntidade, responsavel);
    }

    private List<TransferObject> lstFuncoesPermitidas(String tipo, boolean isPerfil, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        final boolean isAdministrador = responsavel.temPermissao(CodedValues.FUN_USUARIO_ADMINISTRADOR);

        final List<TransferObject> funcoesPermitidas = new ArrayList<>();

        String ncaCodigo = null;

        // recupera a natureza da consignataria para filtrar as funções que são restritas para essa natureza
        if (!TextHelper.isNull(codigoEntidade)) {
            ConsignatariaTransferObject csa = null;
            CorrespondenteTransferObject cor = null;
            if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                try {
                    csa = consignatariaController.findConsignataria(codigoEntidade, responsavel);
                    ncaCodigo = csa.getCsaNcaNatureza();
                } catch (final ConsignatariaControllerException ex) {
                    throw new UsuarioControllerException(ex);
                }
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                try {
                    cor = consignatariaController.findCorrespondente(codigoEntidade, responsavel);
                    csa = consignatariaController.findConsignataria(cor.getCsaCodigo(), responsavel);
                    ncaCodigo = csa.getCsaNcaNatureza();
                } catch (final ConsignatariaControllerException ex) {
                    throw new UsuarioControllerException(ex);
                }
            }
        }

        List<String> funcoesPermitidasNca = null;
        if (!TextHelper.isNull(ncaCodigo)) {
            funcoesPermitidasNca = selectFuncoesPermitidasNca(ncaCodigo, responsavel);
        }

        final List<TransferObject> funcoes = selectFuncoes(tipo, responsavel);
        for (final TransferObject to : funcoes) {
            final String funCodigo = (String) to.getAttribute(Columns.FUN_CODIGO);
            final String grfCodigo = (String) to.getAttribute(Columns.FUN_GRF_CODIGO);
            final String funRestritaNca = (String) to.getAttribute(Columns.FUN_RESTRITA_NCA);

            // Verifica se a função pode ser associada a um perfil
            if (isPerfil && CodedValues.FUNCOES_NAO_PERMITIDAS_PERFIL.contains(funCodigo)) {
                continue;
            }

            // Verifica se a função é de grupo administrador: só exibe para usuários administradores
            if (!TextHelper.isNull(grfCodigo) && CodedValues.GRUPO_FUNCAO_ADMINISTRADOR.equals(grfCodigo) && !isAdministrador) {
                continue;
            }

            // Verifica se as funções são restritas por natureza de consignatária quando o tipo for CSA ou COR
            // caso sejam, verifica se a CSA/COR tem permissão para utilizar a função
            if ((AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) && "S".equals(funRestritaNca)) {
                if ((funcoesPermitidasNca == null) || funcoesPermitidasNca.isEmpty() || !funcoesPermitidasNca.contains(funCodigo)) {
                    continue;
                }
            }

            // Se usuário de CSE, ou o tipo do usuário não é o mesmo, ou tem permissão para a função, então é permitida
            if (responsavel.isCseSup() || !responsavel.getTipoEntidade().equalsIgnoreCase(tipo) || responsavel.temPermissao(funCodigo)) {
                funcoesPermitidas.add(to);
            }
        }

        return funcoesPermitidas;
    }

    @Override
    public List<TransferObject> lstUsuarioCriadoRecursivoPorResponsavel(List<String> usuCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        List<TransferObject> retorno = new ArrayList<>();
        retorno = lstUsuarioCriadoPorResponsavel(usuCodigos, responsavel);

        if ((retorno != null) && !retorno.isEmpty()) {
            final List<String> codigos = new ArrayList<>();
            for (final TransferObject to : retorno) {
                codigos.add(to.getAttribute(Columns.USU_CODIGO).toString());
            }
            retorno.addAll(lstUsuarioCriadoRecursivoPorResponsavel(codigos, responsavel));
        }

        return retorno;
    }

    @Override
    public List<TransferObject> lstUsuarioCriadoPorResponsavel(List<String> usuCodigos, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuarioCriadoPorResponsavelQuery query = new ListaUsuarioCriadoPorResponsavelQuery();
            query.responsaveis = usuCodigos;
            return query.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.localizar.usuarios.criados.pelo.responsavel", responsavel, e);
        }
    }

    @Override
    public void bloquearDesbloquearUsuario(String usuCodigo, String status, String tipo, String tmoCodigo, String ousObs, AcessoSistema responsavel) throws UsuarioControllerException {
        bloquearDesbloquearUsuario(usuCodigo, status, tipo, tmoCodigo, ousObs, responsavel, true);
    }

    @Override
    public void bloquearDesbloquearUsuario(String usuCodigo, String status, String tipo, String tmoCodigo, String ousObs, AcessoSistema responsavel, Boolean validarCpf) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario.setStuCodigo(status);
        if (CodedValues.STU_ATIVO.equals(status)) {
            usuario.setUsuTipoBloq("");
        }

        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
        ocorrencia.setUsuCodigo(usuCodigo);
        ocorrencia.setTocCodigo(CodedValues.STU_ATIVO.equals(status) ? CodedValues.TOC_DESBLOQUEIO_USUARIO : responsavel.isCseSup() ? CodedValues.TOC_BLOQUEIO_USUARIO_POR_CSE : CodedValues.TOC_BLOQUEIO_USUARIO);
        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage(CodedValues.STU_ATIVO.equals(status) ? "mensagem.ocorrencia.ous.obs.desbloqueio.usuario" : responsavel.isSup() ? "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.sup" : responsavel.isCse() ? "mensagem.ocorrencia.ous.obs.bloqueio.usuario.por.cse" : "mensagem.ocorrencia.ous.obs.bloqueio.usuario", responsavel));
        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

        TransferObject tmo = null;
        // Verifica motivo da operação
        if (!TextHelper.isNull(tmoCodigo)) {
            tmo = new CustomTransferObject();
            tmo.setAttribute(Columns.USU_CODIGO, usuCodigo);
            tmo.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
            tmo.setAttribute(Columns.OUS_OBS, ousObs);
        }

        updateUsuario(usuario, ocorrencia, null, null, tipo, null, tmo, responsavel, validarCpf);
    }

    @Override
    public void bloquearDesbloquearUsuario(List<TransferObject> usuarios, String status, String tmoCodigo, String ousObs, AcessoSistema responsavel) throws UsuarioControllerException {
        for (final TransferObject usuario : usuarios) {
            bloquearDesbloquearUsuario(usuario.getAttribute(Columns.USU_CODIGO).toString(), status, usuario.getAttribute("TIPO").toString(), tmoCodigo, ousObs, responsavel);
        }
    }

    @Override
    public void bloquearUsuarioMotivoSeguranca(String usuCodigo, String entidadeOperada, String operacao, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Monta texto a ser inserido no log de segurança contendo: funcao, entidade e operacao, caso venha a ser bloqueado
            String textoDoLog = "";

            // funcao
            if (!TextHelper.isNull(responsavel.getFunCodigo())) {
                final Funcao funcao = FuncaoHome.findByPrimaryKey(responsavel.getFunCodigo());
                textoDoLog += ApplicationResourcesHelper.getMessage("rotulo.log.funcao", responsavel) + ": " + funcao.getFunDescricao() + "; ";
            }
            // entidade
            if (!TextHelper.isNull(entidadeOperada)) {
                final TipoEntidade tipoEntidade = TipoEntidadeHome.findByPrimaryKey(entidadeOperada);
                textoDoLog += ApplicationResourcesHelper.getMessage("rotulo.log.entidade", responsavel) + ": " + tipoEntidade.getTenDescricao() + "; ";
            }
            // operação
            if (!TextHelper.isNull(operacao)) {
                textoDoLog += ApplicationResourcesHelper.getMessage("rotulo.log.acao", responsavel) + ": " + operacao;
            }

            String motivoDoBloqueio = "";
            if (!TextHelper.isNull(textoDoLog)) {
                motivoDoBloqueio = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.seguranca.arg0", responsavel, textoDoLog);
            } else {
                motivoDoBloqueio = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.seguranca", responsavel);
            }

            // Se o limite foi alcançado, então bloqueia o usuário
            final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
            usuario.setStuCodigo(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
            usuario.setUsuTipoBloq(ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.automaticamente.seguranca", responsavel));

            // Grava ocorrência de bloqueio automático do usuário
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_AUTOMATICO_USUARIO);
            ocorrencia.setOusUsuCodigo(AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
            ocorrencia.setOusObs(motivoDoBloqueio);
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

            updateUsuario(usuario, ocorrencia, responsavel);
        } catch (final FindException ex) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void aprovarCadastroUsuarioSer(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        usuario.setStuCodigo(CodedValues.STU_ATIVO);
        usuario.setUsuTipoBloq("");

        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
        ocorrencia.setUsuCodigo(usuCodigo);
        ocorrencia.setTocCodigo(CodedValues.TOC_APROVACAO_CADASTRO_USUARIO_SER);
        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.aprovacao.cadastro.usuario.ser", responsavel));
        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

        updateUsuario(usuario, ocorrencia, null, null, AcessoSistema.ENTIDADE_SER, null, null, responsavel, false);
    }

    @Override
    public Map<String, List<TransferObject>> lstUsuarioAuditorEntidade(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuarioAuditorEntidadeQuery query = new ListaUsuarioAuditorEntidadeQuery();
            final List<TransferObject> lista = query.executarDTO();

            final Map<String, List<TransferObject>> retorno = new HashMap<>();
            if ((lista != null) && !lista.isEmpty()) {
                for (final TransferObject to : lista) {
                    final String codigoEntidade = to.getAttribute("CODIGO_ENTIDADE").toString();
                    final String tipoEntidade = to.getAttribute("TIPO_ENTIDADE").toString();
                    final String chave = tipoEntidade + "|" + codigoEntidade;
                    List<TransferObject> valores = retorno.get(chave);
                    if (valores == null) {
                        valores = new ArrayList<>();
                        retorno.put(chave, valores);
                    }
                    valores.add(to);
                }
            }

            return retorno;
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstFuncoesAuditaveis(String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        final ListaFuncoesAuditaveisPapelQuery lstFuncAuditaveis = new ListaFuncoesAuditaveisPapelQuery();

        lstFuncAuditaveis.tipo = tipo;
        lstFuncAuditaveis.codigoEntidade = codigoEntidade;

        final String papelResponsavel = AcessoSistema.ENTIDADE_CSE.equals(responsavel.getTipoEntidade()) ? CodedValues.PAP_CONSIGNANTE : AcessoSistema.ENTIDADE_CSA.equals(responsavel.getTipoEntidade()) ? CodedValues.PAP_CONSIGNATARIA : AcessoSistema.ENTIDADE_COR.equals(responsavel.getTipoEntidade()) ? CodedValues.PAP_CORRESPONDENTE : AcessoSistema.ENTIDADE_ORG.equals(responsavel.getTipoEntidade()) ? CodedValues.PAP_ORGAO : AcessoSistema.ENTIDADE_SUP.equals(responsavel.getTipoEntidade()) ? CodedValues.PAP_SUPORTE : null;

        lstFuncAuditaveis.papCodigoOrigem = papelResponsavel;

        final String papelEntidade = AcessoSistema.ENTIDADE_CSE.equals(tipo) ? CodedValues.PAP_CONSIGNANTE : AcessoSistema.ENTIDADE_CSA.equals(tipo) ? CodedValues.PAP_CONSIGNATARIA : AcessoSistema.ENTIDADE_COR.equals(tipo) ? CodedValues.PAP_CORRESPONDENTE : AcessoSistema.ENTIDADE_ORG.equals(tipo) ? CodedValues.PAP_ORGAO : AcessoSistema.ENTIDADE_SUP.equals(tipo) ? CodedValues.PAP_SUPORTE : null;

        lstFuncAuditaveis.papCodigoDestino = papelEntidade;
        lstFuncAuditaveis.usuCodigoResponsavel = responsavel.getUsuCodigo();

        final String perCodigoResnp = findUsuarioPerfil(responsavel.getUsuCodigo(), responsavel);

        if (!TextHelper.isNull(perCodigoResnp)) {
            lstFuncAuditaveis.perCodigoResponsavel = perCodigoResnp;
        }

        List<TransferObject> retorno = new ArrayList<>();
        try {
            retorno = lstFuncAuditaveis.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }

        return retorno;
    }

    @Override
    public List<UsuarioTransferObject> lstUsuariosAuditores(String tipo, String codigoEntidade, Object stuCodigo, int offset, int size, AcessoSistema responsavel) throws UsuarioControllerException {
        final ListaUsuariosAuditoresQuery usuAuditListQuery = new ListaUsuariosAuditoresQuery();
        usuAuditListQuery.tipo = tipo;
        usuAuditListQuery.codigoEntidade = codigoEntidade;
        usuAuditListQuery.stuCodigo = stuCodigo;

        if (offset != -1) {
            usuAuditListQuery.firstResult = offset;
        }

        if (size != -1) {
            usuAuditListQuery.maxResults = size;
        }

        try {
            return usuAuditListQuery.executarDTO(UsuarioTransferObject.class);
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    @Override
    public int countUsuariosAuditores(String tipo, String codigoEntidade, Object stuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final ListaUsuariosAuditoresQuery usuAuditListQuery = new ListaUsuariosAuditoresQuery();
        usuAuditListQuery.tipo = tipo;
        usuAuditListQuery.codigoEntidade = codigoEntidade;
        usuAuditListQuery.stuCodigo = stuCodigo;
        usuAuditListQuery.count = true;

        try {
            return usuAuditListQuery.executarContador();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, e);
        }
    }

    @Override
    public void updateFuncoesAuditaveis(List<String> funcoes, String tipo, String codigoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        // LOG Verificar log que está setando funções removidas/inseridas ou perfil alterado
        LogDelegate log = null;

        //verifica se entidade possui usuários auditores. só será permitido configuração de funções auditáveis se houver pelo menos um usuário auditor
        if (funcoes != null) {
            final ListaUsuariosAuditoresQuery usuAuditoresList = new ListaUsuariosAuditoresQuery();
            usuAuditoresList.tipo = tipo;
            usuAuditoresList.codigoEntidade = codigoEntidade;
            usuAuditoresList.stuCodigo = CodedValues.STU_ATIVO;

            try {
                final List<UsuarioTransferObject> auditoresList = usuAuditoresList.executarDTO(UsuarioTransferObject.class);

                if ((auditoresList == null) || auditoresList.isEmpty()) {
                    throw new UsuarioControllerException("mensagem.erro.entidade.deve.possuir.ao.menos.um.usuario.auditor.para.configurar.auditoria", responsavel);
                } else {
                    // confere se ao menos um usuário auditor possui e-mail cadastrado
                    boolean emailCheck = false;
                    for (final UsuarioTransferObject usuAudit : auditoresList) {
                        final UsuarioTransferObject usuTO = usuAudit;
                        if (!TextHelper.isNull(usuTO.getUsuEmail())) {
                            emailCheck = true;
                            break;
                        }
                    }

                    if (!emailCheck) {
                        throw new UsuarioControllerException("mensagem.erro.entidade.deve.possuir.ao.menos.um.usuario.auditor.com.email.configurado.para.configurar.auditoria", responsavel);
                    }
                }
            } catch (final HQueryException e) {
                throw new UsuarioControllerException("mensagem.erro.consultar.usuarios.auditores", responsavel, e);
            }
        }

        Collection<String> funcAuditOld = null;

        try {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                funcAuditOld = FuncaoAuditavelCseHome.findFuncoesByCseCodigo(codigoEntidade);
                log = new LogDelegate(responsavel, Log.CONSIGNANTE, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignante(codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                funcAuditOld = FuncaoAuditavelOrgHome.findFuncoesByOrgCodigo(codigoEntidade);
                log = new LogDelegate(responsavel, Log.ORGAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setOrgao(codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                funcAuditOld = FuncaoAuditavelCsaHome.findFuncoesByCsaCodigo(codigoEntidade);
                log = new LogDelegate(responsavel, Log.CONSIGNATARIA, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignataria(codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                funcAuditOld = FuncaoAuditavelCorHome.findFuncoesByCorCodigo(codigoEntidade);
                log = new LogDelegate(responsavel, Log.CORRESPONDENTE, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setCorrespondente(codigoEntidade);
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                funcAuditOld = FuncaoAuditavelSupHome.findFuncoesByCseCodigo(codigoEntidade);
                log = new LogDelegate(responsavel, Log.CONSIGNANTE, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignante(codigoEntidade);
            }
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new UsuarioControllerException("mensagem.erro.entidade.nao.encontrada", responsavel, e);
        }

        final List<String> diff = new ArrayList<>();
        final List<String> excluir = new ArrayList<>();

        for (final String funcaoOld : funcAuditOld) {
            if ((funcoes == null) || !funcoes.contains(funcaoOld)) {
                excluir.add(funcaoOld);
            }
        }

        if (funcoes != null) {
            for (final String funcaoCorrente : funcoes) {
                if (!funcAuditOld.contains(funcaoCorrente)) {
                    diff.add(funcaoCorrente);
                }
            }
        }

        try {
            if (!diff.isEmpty()) {
                createFuncaoAuditavel(diff, codigoEntidade, tipo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.configurando.auditoria.para.permissoes.arg0", responsavel, TextHelper.join(diff, ",")));
            }

            if (!excluir.isEmpty()) {
                removeFuncoesAuditaveis(excluir, codigoEntidade, tipo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.removendo.configuracoes.auditoria.para.permissoes.arg0", responsavel, TextHelper.join(excluir, ",")));
            }
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private void createFuncaoAuditavel(List<String> funcoes, String codigoEntidade, String tipo) throws UsuarioControllerException {
        for (final String funcao : funcoes) {
            try {
                if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    FuncaoAuditavelCseHome.create(codigoEntidade, funcao);
                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    FuncaoAuditavelOrgHome.create(codigoEntidade, funcao);
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    FuncaoAuditavelCsaHome.create(codigoEntidade, funcao);
                } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    FuncaoAuditavelCorHome.create(codigoEntidade, funcao);
                } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                    FuncaoAuditavelSupHome.create(codigoEntidade, funcao);
                }
            } catch (final CreateException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.configurar.auditoria.para.esta.entidade.erro.interno.arg0", (AcessoSistema) null, ex.getMessage());
            }
        }
    }

    private void removeFuncoesAuditaveis(List<String> funcoes, String codigoEntidade, String tipo) throws UsuarioControllerException {
        try {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                final Collection<FuncaoAuditavelCse> funcAuditaveis = FuncaoAuditavelCseHome.findByCseCodigo(codigoEntidade);
                for (final FuncaoAuditavelCse funAudit : funcAuditaveis) {
                    if (funcoes.contains(funAudit.getFunCodigo())) {
                        AbstractEntityHome.remove(funAudit);
                    }
                }
            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                final Collection<FuncaoAuditavelOrg> funcAuditaveis = FuncaoAuditavelOrgHome.findByOrgCodigo(codigoEntidade);
                for (final FuncaoAuditavelOrg funAudit : funcAuditaveis) {
                    if (funcoes.contains(funAudit.getFunCodigo())) {
                        AbstractEntityHome.remove(funAudit);
                    }
                }
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                final Collection<FuncaoAuditavelCsa> funcAuditaveis = FuncaoAuditavelCsaHome.findByCsaCodigo(codigoEntidade);
                for (final FuncaoAuditavelCsa funAudit : funcAuditaveis) {
                    if (funcoes.contains(funAudit.getFunCodigo())) {
                        AbstractEntityHome.remove(funAudit);
                    }
                }
            } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                final Collection<FuncaoAuditavelCor> funcAuditaveis = FuncaoAuditavelCorHome.findByCorCodigo(codigoEntidade);
                for (final FuncaoAuditavelCor funAudit : funcAuditaveis) {
                    if (funcoes.contains(funAudit.getFunCodigo())) {
                        AbstractEntityHome.remove(funAudit);
                    }
                }
            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                final Collection<FuncaoAuditavelSup> funcAuditaveis = FuncaoAuditavelSupHome.findByCseCodigo(codigoEntidade);
                for (final FuncaoAuditavelSup funAudit : funcAuditaveis) {
                    if (funcoes.contains(funAudit.getFunCodigo())) {
                        AbstractEntityHome.remove(funAudit);
                    }
                }
            }
        } catch (FindException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.remover.configuracoes.auditoria.para.esta.entidade.erro.interno.arg0", (AcessoSistema) null, ex.getMessage());
        }

    }

    /**
     * gera novas senhas para usuários servidores ativos ou recém criados, de acordo com param.
     * de sistema 348, e gera arquivo compactado protegido por senha com usuários alterados.
     * @throws UsuarioControllerException
     * @return a senha plana do arquivo ZIP com os dados para exportação
     */
    @Override
    public String gerarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException {
        final String dia = DateHelper.format(new java.util.Date(), "dd-MM-yyyy");

        //Diretório raiz de arquivos eConsig
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String pathConf = absolutePath + File.separatorChar + "conf";
        final String pathSaida = absolutePath + File.separatorChar + "senhaservidores";

        final File dirArqSenhas = new File(pathSaida);
        final File[] arqSenhas = dirArqSenhas.listFiles();

        // deleta qualquer arquivo de senhas geradas anteriormente no diretório
        if (arqSenhas != null) {
            for (final File arquivo : arqSenhas) {
                arquivo.delete();
            }
        }

        final Object paramValue = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel);
        final int tamanhoSenha = paramValue != null ? Integer.parseInt(paramValue.toString()) : 8;

        // Arquivos de configuração para geração de senhas de servidores
        final String nomeArqConfSaida = "senhas_servidores_saida.xml";
        final String nomeArqConfTradutor = "senhas_servidores_tradutor.xml";

        //Arquivos de configuração default
        final String nomeArqConfSaidaDefault = pathConf + File.separatorChar + nomeArqConfSaida;
        final String nomeArqConfTradutorDefault = pathConf + File.separatorChar + nomeArqConfTradutor;

        if (!new File(nomeArqConfSaidaDefault).exists() || !new File(nomeArqConfTradutorDefault).exists()) {
            throw new UsuarioControllerException("mensagem.erro.arquivos.configuracao.layout.geracao.novas.senhas.ausentes.ou.incorretos", responsavel);
        }

        final boolean todosUsuAtivos = ParamSist.getBoolParamSist(CodedValues.TPC_GERAR_SENHA_TODOS_USU_SER_ATIVOS, responsavel);

        // define se o filtro é para todos usuários ativos ou apenas para os novos usuários
        String arqPrefix = null;
        String harObs = null;
        if (todosUsuAtivos) {
            harObs = ApplicationResourcesHelper.getMessage("mensagem.erro.novas.senhas.geradas.para.todos.servidores", responsavel);
            arqPrefix = "usuarios_" + ApplicationResourcesHelper.getMessage("rotulo.servidor.plural", responsavel) + "_ativos";
        } else {
            harObs = ApplicationResourcesHelper.getMessage("mensagem.erro.novas.senhas.geradas.para.novos.usuarios.servidores", responsavel);
            arqPrefix = "novos_usuarios_" + ApplicationResourcesHelper.getMessage("rotulo.servidor.plural", responsavel);
        }

        // Substitui caracteres inválidos em nomes de arquivos:  / \ : * ? " < > |
        arqPrefix = arqPrefix.replaceAll("[/|\\\\|:|*|?|\"|<|>|\\|| ]", "_");

        final String nomeArqSaida = pathSaida + File.separator + "novas_senhas_" + arqPrefix + "_" + dia + ".txt";

        //escritor temporário que armazenará os dados da linha da tabela usuário e
        //processar a geração da nova senha
        final HashMap<String, Object> camposLinha = new HashMap<>();
        EscritorMemoria escritor = new EscritorMemoria(camposLinha);

        EscritorArquivoTexto escritorFinal = null;
        try {
            escritorFinal = new EscritorArquivoTexto(nomeArqConfSaidaDefault, nomeArqSaida);
        } catch (final Exception fex) {
            LOG.error(fex.getMessage(), fex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, fex);
        }

        Tradutor tradutor = null;
        try {
            final ListaUsuarioServidorNovaSenhaQuery query = new ListaUsuarioServidorNovaSenhaQuery();
            query.todosUsuAtivos = todosUsuAtivos;
            final List<TransferObject> usuServidoresNovaSenha = query.executarDTO();

            // Cache usado para otimizar
            final HashMap<String, TransferObject> usuariosMap = new HashMap<>();
            for (final TransferObject usuario : usuServidoresNovaSenha) {
                usuariosMap.put((String) usuario.getAttribute("USU_CODIGO"), usuario);
            }

            final Leitor leitor = new LeitorListTO(usuServidoresNovaSenha);
            tradutor = new Tradutor(nomeArqConfTradutorDefault, leitor, escritor);
            tradutor.iniciaTraducao(false);
            escritorFinal.iniciaEscrita();

            boolean temRegistro = false;
            while (tradutor.traduzProximo()) {
                temRegistro = true;
                // gera a nova senha para a entidade usuário
                final boolean senhaNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                final String novaSenha = senhaNumerica ? GeradorSenhaUtil.getPasswordNumber(tamanhoSenha, responsavel) : GeradorSenhaUtil.getPassword(tamanhoSenha, AcessoSistema.ENTIDADE_SER, responsavel);

                camposLinha.put("USU_NOVA_SENHA", novaSenha);

                if (TextHelper.isNull(camposLinha.get("USU_CODIGO"))) {
                    LOG.error("Campo USU_CODIGO obrigatório no XML tradutor da geração de arquivo de exportação de senhas de usuários " + ApplicationResourcesHelper.getMessage("rotulo.servidor.plural", responsavel) + ".");
                    throw new UsuarioControllerException("mensagem.erro.arquivo.configuracao.exportacao.novas.senhas.usuarios.servidores.incorreto", responsavel);
                }

                final String usuCodigo = (String) camposLinha.get("USU_CODIGO");
                final TransferObject usuTo = usuariosMap.get(usuCodigo);
                final String senhaNovaCrypt = SenhaHelper.criptografarSenha((String) usuTo.getAttribute(Columns.USU_LOGIN), novaSenha, true, responsavel);

                UsuarioHome.updateUsuNovaSenha(senhaNovaCrypt, usuCodigo);

                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo(usuCodigo);
                ocorrencia.setTocCodigo(CodedValues.TOC_GERACAO_NOVA_SENHA);
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage(todosUsuAtivos ? "mensagem.informacao.geracao.senhas.para.servidores.ativos" : "mensagem.informacao.geracao.senhas.para.novos.usuarios.servidores", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                createOcorrenciaUsuario(ocorrencia, responsavel);

                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.GERAR_NOVA_SENHA, Log.LOG_INFORMACAO);
                log.setUsuario(usuCodigo);
                log.write();

                escritorFinal.escreve(camposLinha);

                // limpa o escritor para os dados da próxima linha
                escritor.iniciaEscrita();
            }

            if (temRegistro) {
                escritorFinal.encerraEscrita();
                escritorFinal = null;
                escritor.encerraEscrita();
                escritor = null;
                tradutor.encerraTraducao();
                tradutor = null;

                // gera histórico do arquivo de senhas de usuário servidores
                final String harResultado = CodedValues.STS_ATIVO.toString();
                final TipoArquivoEnum tipoArquivo = TipoArquivoEnum.ARQUIVO_SENHAS_SERVIDORES;
                historicoArquivoController.createHistoricoArquivo(null, null, tipoArquivo, nomeArqSaida, harObs, null, null, harResultado, responsavel);

                // comprime e encripta arquivo gerado para exportação
                final File arqNovasSenhas = new File(nomeArqSaida);
                final File arqComprimido = new File(pathSaida + File.separator + "novas_senhas_" + arqPrefix + "_" + dia + ".zip");
                final String senhaZip = GeradorSenhaUtil.getPassword(tamanhoSenha, AcessoSistema.ENTIDADE_SER, responsavel);
                FileHelper.zipAndEncrypt(arqNovasSenhas, arqComprimido, senhaZip);
                arqNovasSenhas.delete();

                return senhaZip;
            } else {
                throw new UsuarioControllerException("mensagem.nenhum.usuario.servidor.encontrado", responsavel);
            }

        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.alterar.usuario", responsavel, ex);
        } catch (ParserException | IOException | LogControllerException | HistoricoArquivoControllerException | DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } finally {
            try {
                if (escritorFinal != null) {
                    escritorFinal.encerraEscrita();
                }
                if (escritor != null) {
                    escritor.encerraEscrita();
                }
                if (tradutor != null) {
                    tradutor.encerraTraducao();
                }
            } catch (final ParserException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }
        }
    }

    /**
     * Ativar as senhas geradas automaticamente e armazenadas no campo USU_NOVA_SENHA, tornando-as a senha oficial dos
     * respectivos usuários servidores
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void ativarSenhasUsuServidores(AcessoSistema responsavel) throws UsuarioControllerException {
        final ParamSist ps = ParamSist.getInstance();
        // Diretório raiz de arquivos eConsig
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String pathArquivos = absolutePath + File.separatorChar + "senhaservidores";

        final File dirArqSenhas = new File(pathArquivos);
        final File[] arqSenhas = dirArqSenhas.listFiles();

        if ((arqSenhas == null) || (arqSenhas.length == 0)) {
            throw new UsuarioControllerException("mensagem.erro.nenhum.arquivo.com.informacao.senhas.encontrado", responsavel);
        }

        final ListaUsuariosComNovaSenhaQuery servidoresList = new ListaUsuariosComNovaSenhaQuery();

        int diasParaExpiracaoNovaSenha = 0;
        try {
            diasParaExpiracaoNovaSenha = Integer.parseInt(ps.getParam(CodedValues.TPC_DIAS_EXPIRACAO_NOVA_SENHA_SERVIDOR, responsavel).toString());
        } catch (final Exception ex) {
            diasParaExpiracaoNovaSenha = 0;
        }

        try {
            final List<TransferObject> usuTo = servidoresList.executarDTO();

            for (final TransferObject servidorTo : usuTo) {
                final String usuCodigo = (String) servidorTo.getAttribute(Columns.USU_CODIGO);

                // configura o dia para expiração da senha, de acordo com parâmetro.
                java.util.Date usuDataExpSenha = null;
                if (diasParaExpiracaoNovaSenha > 0) {
                    usuDataExpSenha = DateHelper.addDays(DateHelper.getSystemDatetime(), diasParaExpiracaoNovaSenha);
                } else {
                    usuDataExpSenha = DateHelper.getSystemDatetime();
                }

                UsuarioHome.ativaUsuNovaSenha(usuDataExpSenha, usuCodigo);

                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo(usuCodigo);
                ocorrencia.setTocCodigo(CodedValues.TOC_ATIVAR_NOVA_SENHA);
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.informacao.ativacao.senha.para.usuario", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                createOcorrenciaUsuario(ocorrencia, responsavel);

                final LogDelegate log = new LogDelegate(responsavel, Log.USUARIO, Log.ATIVAR_NOVA_SENHA, Log.LOG_INFORMACAO);
                log.setUsuario(usuCodigo);
                log.write();
            }

            if (arqSenhas != null) {
                for (final File arquivo : arqSenhas) {
                    arquivo.delete();
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.listar.usuarios.servidores", responsavel, ex);
        } catch (final UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.interno.atualizar.usuario", responsavel, ex);
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findFuncaoAuditavelPorEntidade(String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final List<TransferObject> retorno = new ArrayList<>();
            if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                final Collection<FuncaoAuditavelCse> colecao = FuncaoAuditavelCseHome.findByCseCodigo(codigoEntidade);
                for (final FuncaoAuditavelCse funcaoAuditavel : colecao) {
                    final TransferObject to = new CustomTransferObject();
                    final Funcao funcao = FuncaoHome.findByPrimaryKey(funcaoAuditavel.getFuncao().getFunCodigo());
                    to.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
                    to.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
                    retorno.add(to);
                }

            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                final Collection<FuncaoAuditavelCsa> colecao = FuncaoAuditavelCsaHome.findByCsaCodigo(codigoEntidade);
                for (final FuncaoAuditavelCsa funcaoAuditavel : colecao) {
                    final TransferObject to = new CustomTransferObject();
                    final Funcao funcao = FuncaoHome.findByPrimaryKey(funcaoAuditavel.getFuncao().getFunCodigo());
                    to.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
                    to.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
                    retorno.add(to);
                }

            } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                final Collection<FuncaoAuditavelCor> colecao = FuncaoAuditavelCorHome.findByCorCodigo(codigoEntidade);
                for (final FuncaoAuditavelCor funcaoAuditavel : colecao) {
                    final TransferObject to = new CustomTransferObject();
                    final Funcao funcao = FuncaoHome.findByPrimaryKey(funcaoAuditavel.getFuncao().getFunCodigo());
                    to.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
                    to.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
                    retorno.add(to);
                }

            } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
                final Collection<FuncaoAuditavelOrg> colecao = FuncaoAuditavelOrgHome.findByOrgCodigo(codigoEntidade);
                for (final FuncaoAuditavelOrg funcaoAuditavel : colecao) {
                    final TransferObject to = new CustomTransferObject();
                    final Funcao funcao = FuncaoHome.findByPrimaryKey(funcaoAuditavel.getFuncao().getFunCodigo());
                    to.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
                    to.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
                    retorno.add(to);
                }

            } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                final Collection<FuncaoAuditavelSup> colecao = FuncaoAuditavelSupHome.findByCseCodigo(codigoEntidade);
                for (final FuncaoAuditavelSup funcaoAuditavel : colecao) {
                    final TransferObject to = new CustomTransferObject();
                    final Funcao funcao = FuncaoHome.findByPrimaryKey(funcaoAuditavel.getFuncao().getFunCodigo());
                    to.setAttribute(Columns.FUN_CODIGO, funcao.getFunCodigo());
                    to.setAttribute(Columns.FUN_DESCRICAO, funcao.getFunDescricao());
                    retorno.add(to);
                }

            }

            return retorno;
        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.recuperar.funcoes.auditaveis", responsavel);
        }
    }

    @Override
    public void bloqueiaUsuariosFimVigencia(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuarioFimVigenciaQuery query = new ListaUsuarioFimVigenciaQuery();
            final List<TransferObject> lista = query.executarDTO();

            for (final TransferObject to : lista) {
                final String usuCodigo = to.getAttribute(Columns.USU_CODIGO).toString();

                // Altera status do usuário para bloqueado automaticamente
                final UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
                usuario.setStuCodigo(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);
                usuario.setUsuTipoBloq(ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.fim.vigencia", responsavel));

                // Grava ocorrência de bloqueio automático do usuário por fim de vigencia
                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo(usuCodigo);
                ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_USUARIO_FIM_VIGENCIA);
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo() != null ? responsavel.getUsuCodigo() : AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.por.fim.vig", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                updateUsuario(usuario, ocorrencia, responsavel);
            }

        } catch (final HQueryException e) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.bloquear.usuarios.por.fim.vigencia", responsavel, e);
        } catch (final UsuarioControllerException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.bloquear.usuarios.por.fim.vigencia", responsavel, e);
        }
    }

    @Override
    public String gerarChaveSessaoUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final UsuarioChaveSessao usuToken = UsuarioChaveSessaoHome.findByPrimaryKey(usuCodigo);

            if (usuToken != null) {
                return UsuarioChaveSessaoHome.updateToken(usuCodigo);
            } else {
                final UsuarioChaveSessao novoToken = UsuarioChaveSessaoHome.create(usuCodigo);
                return novoToken.getUcsToken();
            }
        } catch (final FindException e) {
            UsuarioChaveSessao chaveDispositivo = null;
            try {
                chaveDispositivo = UsuarioChaveSessaoHome.create(usuCodigo);
            } catch (final CreateException e1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.token.usuario.criar", responsavel, e);
            }
            return chaveDispositivo.getUcsToken();
        } catch (CreateException | UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.token.usuario.criar", responsavel, e);
        }
    }

    @Override
    public void cadastroDeviceToken(String usuCodigo, String tdiCodigo, String deviceToken, AcessoSistema responsavel) throws UsuarioControllerException {
        UsuarioChaveDispositivo device;
        try {
            device = UsuarioChaveDispositivoHome.findByPrimaryKey(usuCodigo);
        } catch (final FindException e) {
            device = null;
        }

        try {
            if (device != null) {
                UsuarioChaveDispositivoHome.update(usuCodigo, tdiCodigo, deviceToken);
            } else {
                UsuarioChaveDispositivoHome.create(usuCodigo, tdiCodigo, deviceToken);
            }
        } catch (final UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.chave.dispositivo.atualizar", (AcessoSistema) null);
        } catch (final CreateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.chave.dispositivo.criar", (AcessoSistema) null);
        }
    }

    @Override
    public String findDeviceToken(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final UsuarioChaveDispositivo device = UsuarioChaveDispositivoHome.findByPrimaryKey(usuCodigo);

            if (device != null) {
                return device.getUcdToken();
            }

        } catch (final FindException e) {
        }

        return null;
    }

    /**
     * Recupera um token de acesso
     * @param token
     * @return
     * @throws FindException Se não achou o token
     */
    @Override
    public UsuarioChaveSessao findUsuarioChaveSessao(String token) throws FindException {
        return UsuarioChaveSessaoHome.findByChaveSessao(token);
    }

    /**
     * Invalida/Apaga um token de acesso
     *
     * @param token
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public void deleteUsuarioChaveSessao(String usuCodigo) throws UsuarioControllerException {
        try {
            UsuarioChaveSessaoHome.delete(usuCodigo);
        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.erro.token.usuario.invalidar", (AcessoSistema) null);
        } catch (final RemoveException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.token.usuario.invalidar", (AcessoSistema) null);
        }
    }

    /**
     * Recupera o token e verifica se este já está expirado ou não. Se estiver expirado, apaga o token.
     * Qualquer exceção é para negar acesso.
     * @param token
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public UsuarioChaveSessao validateToken(String token) throws UsuarioControllerException {
        UsuarioChaveSessao chave;
        try {
            chave = this.findUsuarioChaveSessao(token);
        } catch (FindException e) {
            throw new UsuarioControllerException("mensagem.erro.token.expirado",
                    AcessoSistema.getAcessoUsuarioSistema());
        }

        if (isExpiredToken(chave)) {
            throw new UsuarioControllerException("mensagem.erro.token.expirado",
                    AcessoSistema.getAcessoUsuarioSistema());
        }
        
        return chave;
    }

    private boolean isExpiredToken(UsuarioChaveSessao chave) {
        Integer tempoExpiracaoToken = 600;
        String param = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TEMPO_EXPIRACAO_TOKEN_ACESSO,
                AcessoSistema.getAcessoUsuarioSistema());
        
        if (!TextHelper.isNull(param)) {
            try {
                tempoExpiracaoToken = Integer.parseInt(param.toString());
            } catch (NumberFormatException e) {
            }
        }

        Calendar dataExpiracao = Calendar.getInstance();
        dataExpiracao.setTime(chave.getUcsDataCriacao());
        dataExpiracao.add(Calendar.SECOND, tempoExpiracaoToken);
        Calendar now = Calendar.getInstance();
        if (now.compareTo(dataExpiracao) > 0) {
            try {
                this.deleteUsuarioChaveSessao(chave.getUsuCodigo());
            } catch (UsuarioControllerException e) {
            }
            return true;
        }

        return false;
    }

    /**
     * Altera/Insere a imagem do perfil de um usuário
     * @throws UsuarioControllerException
     */
    @Override
    public void insereAlteraImagemUsuario(String usuCodigo, byte[] imagem, AcessoSistema responsavel) throws UsuarioControllerException {

        ArquivoUsuario arquivo;
        // Verifica o tamanho da imagem
        if (imagem.length > (5 * 1014 * 1024)) {
            throw new UsuarioControllerException("mensagem.erro.imagem.excede.tamanho.maximo", responsavel);
        }

        // Tenta recuperar a imagem corrente
        Collection<ArquivoUsuario> lstArqs = null;
        try {
            lstArqs = ArquivoUsuarioHome.findByUsuCodigoTipoArquivo(usuCodigo, TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo());
        } catch (final FindException e) {
        }
        if ((lstArqs != null) && (lstArqs.size() > 0)) {
            // Reutiliza o arquivo existente
            arquivo = (ArquivoUsuario) lstArqs.toArray()[0];
            arquivo.setAusConteudo(imagem);
            arquivo.setAusDataCriacao(new java.util.Date());
            try {
                AbstractEntityHome.update(arquivo);
            } catch (final UpdateException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.gravar.imagem.usuario", responsavel);
            }
        } else {
            // Cria um novo arquivo
            try {
                arquivo = ArquivoUsuarioHome.create(TipoArquivoHome.findByPrimaryKey(TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo()), usuCodigo, imagem);
            } catch (CreateException | FindException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException("mensagem.erro.gravar.imagem.usuario", responsavel);
            }
        }

        // Cria ocorrência de usuário de alteração da imagem
        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
        ocorrencia.setUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_USUARIO);
        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.imagem", responsavel));
        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
        createOcorrenciaUsuario(ocorrencia, responsavel);

    }

    @Override
    public Map<String, Object> primeiroAcesso(String id, String orgCodigo, String otp, AcessoSistema responsavel) throws UsuarioControllerException {
        // verifica se sistema valida primeiro acesso
        if (!ParamSist.getBoolParamSist(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, responsavel)) {
            throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
        }

        if (TextHelper.isNull(id)) {
            throw new UsuarioControllerException("mensagem.informe.servidor.cpf", responsavel);
        }

        final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
        final Map<String, Object> retorno = new HashMap<>();
        String cpf = null;
        String serEmail = null;
        String serCelular = null;
        try {
            List<String> lstOrgId = null;
            if (!TextHelper.isNull(orgCodigo)) {
                lstOrgId = new ArrayList<>();
                lstOrgId.add(orgCodigo);
            }

            List<TransferObject> lstRegistroServidores = null;

            if (omiteCpf) {
                lstRegistroServidores = servidorController.lstRegistroServidorPorEmail(id, lstOrgId, AcessoSistema.getAcessoUsuarioSistema());
                cpf = servidorController.findServidorByRseCodigo(lstRegistroServidores.get(0).getAttribute(Columns.RSE_CODIGO).toString(), responsavel).getSerCpf();
            } else {
                lstRegistroServidores = servidorController.lstRegistroServidorPorCpf(id, lstOrgId, AcessoSistema.getAcessoUsuarioSistema());
                cpf = id;
            }

            if ((lstRegistroServidores != null) && !lstRegistroServidores.isEmpty()) {
                // inclusão de parâmetros de definição do tamanho da senha de servidor para serem usados na tela de primeiro acesso
                int tamMinSenhaServidor = 6;
                int tamMaxSenhaServidor = 8;
                try {
                    tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
                } catch (final Exception ex) {
                    tamMinSenhaServidor = 6;
                }
                try {
                    tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
                } catch (final Exception ex) {
                    tamMaxSenhaServidor = 8;
                }
                retorno.put("tamMinSenhaServidor", tamMinSenhaServidor);
                retorno.put("tamMaxSenhaServidor", tamMaxSenhaServidor);
                if (!TextHelper.isNull(otp)) {
                    // se foi informado OTP, verifica se usuário possui permissão de cadastrar email e/ou celular
                    // para poder ser direcionado para interface de preenchimento destes dados.
                    try {
                        if (UsuarioHelper.primeiroAcessoSistema(cpf, lstRegistroServidores, null)) {
                            // verifica se usuário pode cadastrar email e/ou telefone
                            boolean usuPodeCadTelEmail = false;
                            boolean otpValido = false;
                            String usuCodigo = null;
                            outer: for (final TransferObject rse : lstRegistroServidores) {
                                final List<TransferObject> lstUsuResult = lstUsuariosSer(cpf, (String) rse.getAttribute(Columns.RSE_MATRICULA), (String) rse.getAttribute(Columns.EST_IDENTIFICADOR), (String) rse.getAttribute(Columns.ORG_IDENTIFICADOR), responsavel);
                                for (final TransferObject usuTO : lstUsuResult) {
                                    usuCodigo = (String) usuTO.getAttribute(Columns.USU_CODIGO);
                                    final String otpContraCheque = (String) usuTO.getAttribute(Columns.USU_NOVA_SENHA);
                                    if (TextHelper.isNull(otpContraCheque)) {
                                        continue;
                                    }

                                    // valida OTP do contracheque na base com o informado pelo usuário
                                    if (!JCrypt.verificaSenha(otp, otpContraCheque)) {
                                        // caso não tenha dado match, pode ser o otp de outra matrícula deste servidor. continua verificação então.
                                        continue;
                                    } else {
                                        otpValido = true;
                                        usuPodeCadTelEmail = usuarioTemPermissao(usuCodigo, CodedValues.FUN_EDT_SERVIDOR, AcessoSistema.ENTIDADE_SER, responsavel);
                                        final ServidorTransferObject serTO = servidorController.findServidor((String) rse.getAttribute(Columns.SER_CODIGO), responsavel);
                                        serEmail = serTO.getSerEmail();
                                        serCelular = serTO.getSerCelular();
                                        break outer;
                                    }
                                }

                            }

                            if (!otpValido) {
                                throw new UsuarioControllerException("mensagem.senha.servidor.otp.invalido", responsavel);
                            }

                            if (usuPodeCadTelEmail) {
                                retorno.put("usuCodigo", usuCodigo);
                                //Geração de token de fluxo de caso de uso a ser confirmado pela tela de retorno ao cliente
                                final String token = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);
                                retorno.put("token", token);

                                final UsuarioTransferObject usuTO = new UsuarioTransferObject(usuCodigo);
                                usuTO.setUsuOtpChaveSeguranca(token);
                                usuTO.setUsuCPF(cpf);
                                updateUsuario(usuTO, null, null, null, AcessoSistema.ENTIDADE_SER, null, null, responsavel);

                                retorno.put("email", serEmail);
                                retorno.put("telefone", serCelular);

                                return retorno;
                            } else {
                                throw new UsuarioControllerException("mensagem.erro.servidor.nao.autorizado", responsavel);
                            }

                        } else {
                            throw new UsuarioControllerException("mensagem.usuario.possui.cadastro.ativo", responsavel);
                        }
                    } catch (final ViewHelperException qex) {
                        LOG.error(qex.getMessage(), qex);
                        throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
                    }
                } else {
                    // se não foi informado otp, verifica se usuário possui email e/ou celular cadastrado
                    // para poder enviar um novo otp gerado.
                    boolean temCelular = false;
                    boolean temEmail = false;
                    TransferObject rseSelecionado = null;
                    for (final TransferObject rse : lstRegistroServidores) {
                        final RegistroServidorTO rseTO = servidorController.findRegistroServidor((String) rse.getAttribute(Columns.RSE_CODIGO), responsavel);
                        final ServidorTransferObject serTO = servidorController.findServidor(rseTO.getSerCodigo(), responsavel);
                        final List<TransferObject> lstUsuResult = lstUsuariosSer(cpf, (String) rse.getAttribute(Columns.RSE_MATRICULA), (String) rse.getAttribute(Columns.EST_IDENTIFICADOR), (String) rse.getAttribute(Columns.ORG_IDENTIFICADOR), responsavel);
                        if (!TextHelper.isNull(serTO.getSerCelular()) && !lstUsuResult.isEmpty()) {
                            temCelular = true;
                            rseSelecionado = rse;
                            serEmail = serTO.getSerEmail();
                            serCelular = serTO.getSerCelular();
                            break;
                        }
                        if (!TextHelper.isNull(serTO.getSerEmail()) && !lstUsuResult.isEmpty()) {
                            temEmail = true;
                            rseSelecionado = rse;
                            serEmail = serTO.getSerEmail();
                            serCelular = serTO.getSerCelular();
                            break;
                        }
                    }

                    if (!temCelular && !temEmail) {
                        throw new UsuarioControllerException("mensagem.senha.servidor.otp.erro.email.celular.invalido", responsavel);
                    }

                    try {
                        if (UsuarioHelper.primeiroAcessoSistema(cpf, lstRegistroServidores, null)) {
                            final List<TransferObject> lstUsuResult = lstUsuariosSer(cpf, (String) rseSelecionado.getAttribute(Columns.RSE_MATRICULA), (String) rseSelecionado.getAttribute(Columns.EST_IDENTIFICADOR), (String) rseSelecionado.getAttribute(Columns.ORG_IDENTIFICADOR), responsavel);
                            final String usuCodigo = (String) lstUsuResult.get(0).getAttribute(Columns.USU_CODIGO);
                            final String token = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);

                            final UsuarioTransferObject usuTO = new UsuarioTransferObject(usuCodigo);
                            usuTO.setUsuOtpChaveSeguranca(token);
                            usuTO.setUsuCPF(cpf);
                            updateUsuario(usuTO, null, null, null, AcessoSistema.ENTIDADE_SER, null, null, responsavel);

                            // Envia OTP para usuário
                            enviaOTPServidor(usuCodigo, token, null, null, AcessoSistema.getAcessoUsuarioSistema());
                            retorno.put("usuCodigo", usuCodigo);

                            retorno.put("token", token);
                            retorno.put("email", serEmail);
                            retorno.put("telefone", serCelular);

                            return retorno;
                        } else {
                            throw new UsuarioControllerException("mensagem.usuario.possui.cadastro.ativo", responsavel);
                        }
                    } catch (final ViewHelperException e) {
                        LOG.error(e.getMessage(), e);
                        throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
                    }
                }
            } else {
                throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", AcessoSistema.getAcessoUsuarioSistema());
            }
        } catch (final ServidorControllerException ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public void enviaOTPServidor(String usuCodigo, String chaveSenha, String emailOpcional, String telefoneOpcional, AcessoSistema responsavel) throws UsuarioControllerException {
        enviaOTPServidor(usuCodigo, chaveSenha, emailOpcional, telefoneOpcional, true, false, false, false, responsavel);
    }

    @Override
    public void enviaOTPServidor(String usuCodigo, String chaveSenha, String emailOpcional, String telefoneOpcional, boolean fluxoMobile, boolean fluxoAutoDesbloqueio, boolean enviaOtpEmail, boolean enviaOtpCelular, AcessoSistema responsavel) throws UsuarioControllerException {
        // Para uso do portal, não precisa validar primeiro acesso, visto que será acessado sempre usando OTP
        // ATENÇÂO: Fluxo do portal não valida alguns dados
        if (fluxoMobile) {
            final boolean primeiroAcessoOTP = ParamSist.getBoolParamSist(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, responsavel);
            if (!primeiroAcessoOTP) {
                throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
            }
        }

        // Senha numérica com 6 dígitos
        final String otp = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);

        String email = emailOpcional;
        String telefone = telefoneOpcional;
        String serNome = "";

        // Buscar usuário através do usuCodigo passado
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        final Usuario usuarioBean = findUsuarioBean(usuario, AcessoSistema.ENTIDADE_SER);

        if (usuarioBean == null) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
        }

        try {
            final UsuarioSer usuarioSer = UsuarioSerHome.findByUsuCodigo(usuCodigo);
            final String serCodigo = usuarioSer.getServidor().getSerCodigo();
            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

            if (servidor == null) {
                throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }

            serNome = servidor.getSerNome();

            // Se possuir email cadastrado, utiliza cadastrado
            // ATENÇÂO: O portal não utiliza email
            if (fluxoMobile && !TextHelper.isNull(servidor.getSerEmail())) {
                email = servidor.getSerEmail();
            }
            // Se possuir telefone cadastrado, utiliza cadastrado
            if (!TextHelper.isNull(servidor.getSerCelular())) {
                telefone = servidor.getSerCelular();
            }

        } catch (ServidorControllerException | FindException e) {
            throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
        }

        // ATENÇÂO: Para uso do portal, não precisa validar primeiro acesso, visto que será acessado sempre usando OTP
        if (fluxoMobile) {

            // Valida se é primeiro acesso do usuário
            if (!TextHelper.isNull(usuarioBean.getUsuDataUltAcesso())) {
                throw new UsuarioControllerException("mensagem.usuario.possui.cadastro.ativo", responsavel);
            }

            // Valida se o REST primeiroAcesso (DESENV-6371) foi chamado anteriormente
            if (TextHelper.isNull(chaveSenha) || !chaveSenha.equalsIgnoreCase(usuarioBean.getUsuOtpChaveSeguranca())) {
                throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
            }
        }

        // Criptografa OTP antes de salvar no usuário
        final String otpCrypt = SenhaHelper.criptografarSenha(usuarioBean.getUsuLogin(), otp, true, responsavel);

        // Salva OTP gerado no campo provisório USU_CHAVE_VALIDACAO_TOTP
        usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
        usuario.setUsuOtpCodigo(otpCrypt);
        usuario.setUsuOtpDataCadastro(DateHelper.getSystemDatetime());

        // Cria ocorrência
        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
        ocorrencia.setUsuCodigo(usuCodigo);
        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_OTP_USUARIO);
        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.otp.usuario", responsavel));
        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

        // Salva OTP no usuário
        updateUsuario(usuario, ocorrencia, responsavel);

        if (!fluxoAutoDesbloqueio) {
            if (!TextHelper.isNull(telefone)) {
                try {
                    EnviaSMSHelper.enviarSMSOTP(telefone, otp, responsavel);
                } catch (final ZetraException e) {
                    if (!TextHelper.isNull(email)) {
                        try {
                            EnviaEmailHelper.enviarEmailOTPServidor(serNome, email, otp, responsavel);
                        } catch (final ViewHelperException ex) {
                            throw new UsuarioControllerException("mensagem.erro.sms.ou.email.enviar", responsavel, ex);
                        }
                    } else {
                        throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, e);
                    }
                }
            } else if (!TextHelper.isNull(email)) {
                try {
                    EnviaEmailHelper.enviarEmailOTPServidor(serNome, email, otp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel, e);
                }
            } else {
                throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
            }
        } else {
            final boolean recuperaSenhaSMS = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_SMS, responsavel);
            final boolean recuperaSenhaSMSEmail = ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_SERVIDOR, CodedValues.AUTO_DESBLOQUEIO_SERVIDOR_EMAIL_SMS, responsavel);

            if (recuperaSenhaSMS) {
                if (!TextHelper.isNull(telefone)) {
                    try {
                        EnviaSMSHelper.enviarSMSOTP(telefone, otp, responsavel);
                    } catch (final ZetraException ex) {
                        throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, ex);
                    }
                }
            } else if (recuperaSenhaSMSEmail) {
                if (!TextHelper.isNull(telefone) && enviaOtpCelular) {
                    try {
                        EnviaSMSHelper.enviarSMSOTP(telefone, otp, responsavel);
                    } catch (final ZetraException ex) {
                        throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, ex);
                    }
                }

                if (!TextHelper.isNull(email) && enviaOtpEmail) {
                    try {
                        EnviaEmailHelper.enviarEmailOTPServidor(serNome, email, otp, responsavel);
                    } catch (final ViewHelperException e) {
                        throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel, e);
                    }
                }
            }
        }
    }

    /**
     * Realiza autenticação via OTP para o portal
     *
     * @param usuCodigo
     * @param otp
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public String validarOTPPortal(TransferObject usuario, String otp, AcessoSistema responsavel) throws UsuarioControllerException {

        validarDataCadastroOptExpirado(responsavel, usuario);

        // valida otp enviado via SMS ao usuário.
        if (TextHelper.isNull(usuario.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(otp, (String) usuario.getAttribute(Columns.USU_OTP_CODIGO))) {
            return null;
        }

        return gerarChaveSessaoUsuario((String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);

    }

    /**
     * Verifica se o OTP foi expirado
     *
     * @param responsavel
     * @param usuSer
     * @throws UsuarioControllerException
     */
    private void validarDataCadastroOptExpirado(AcessoSistema responsavel, TransferObject usuSer) throws UsuarioControllerException {

        // valida se o otp ainda é válido
        final ParamSist paramSist = ParamSist.getInstance();
        final String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
        Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);
        // default 20 minutos
        if (timeoutOtp == null) {
            timeoutOtp = 20;
        }
        final java.util.Date otpDataCadastro = usuSer.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuSer.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
        final java.util.Date dataAtual = new java.util.Date();

        // verifica se passou o limite do tempo em milisegundos
        if ((otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * (6 * Math.pow(10, 4))))) {
            throw new UsuarioControllerException("mensagem.senha.servidor.otp.expirado", responsavel);
        }

    }

    /**
     * Valida OTP de primeiro acesso de servidor
     * @param usuCodigo - código do usuário servidor
     * @param novaSenha - senha digitada pelo usuário que será sua nova senha de acesso
     * @param senhaCriptografada - informa se senha está vindo criptografada ou não
     * @param otp - otp enviado por SMS ou e-mail para validação de primeiro acesso
     * @param emailOpcional
     * @param telefoneOpcional
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public List<TransferObject> validaOTPServidor(String usuCodigo, String novaSenha, boolean senhaCriptografada, String otp, String emailOpcional, String telefoneOpcional, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException {
        final AcessoSistema respValidacao = AcessoSistema.getAcessoUsuarioSistema();

        // verifica se sistema valida primeiro acesso
        if (!ParamSist.getBoolParamSist(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, respValidacao)) {
            throw new UsuarioControllerException("mensagem.operacaoInvalida", respValidacao);
        }

        TransferObject usuSer = null;
        try {
            usuSer = pesquisarServidorController.buscaUsuarioServidor(usuCodigo, respValidacao);

            // Valida se é primeiro acesso do usuário
            if (!TextHelper.isNull(usuSer.getAttribute(Columns.USU_DATA_ULT_ACESSO))) {
                throw new UsuarioControllerException("mensagem.usuario.possui.cadastro.ativo", responsavel);
            }

            validarDataCadastroOptExpirado(respValidacao, usuSer);

            // valida otp enviado via SMS ou email ao usuário.
            if (TextHelper.isNull(usuSer.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(otp, (String) usuSer.getAttribute(Columns.USU_OTP_CODIGO))) {
                throw new UsuarioControllerException("mensagem.senha.servidor.otp.invalido", respValidacao);
            }

            if ((!TextHelper.isNull(emailOpcional) || !TextHelper.isNull(telefoneOpcional)) && !usuarioTemPermissao(usuCodigo, CodedValues.FUN_EDT_SERVIDOR, AcessoSistema.ENTIDADE_SER, respValidacao)) {
                throw new UsuarioControllerException("mensagem.erro.servidor.nao.autorizado", respValidacao);
            }

            // valida tamanho máximo e mínimo dos campos senha e confirmação de senha
            int tamMinSenhaServidor = 6;
            int tamMaxSenhaServidor = 8;
            try {
                tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
            } catch (final Exception ex) {
                tamMinSenhaServidor = 6;
            }
            try {
                tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
            } catch (final Exception ex) {
                tamMaxSenhaServidor = 8;
            }
            if (TextHelper.isNull(novaSenha) || (novaSenha.length() < tamMinSenhaServidor)) {
                throw new UsuarioControllerException("mensagem.erro.cadastrar.senha.servidor.minimo", responsavel, String.valueOf(tamMinSenhaServidor));
            } else if (novaSenha.length() > tamMaxSenhaServidor) {
                throw new UsuarioControllerException("mensagem.erro.cadastrar.senha.servidor.maximo", responsavel, String.valueOf(tamMaxSenhaServidor));
            }

            // cria ocorrência de alteração de senha
            final String tocCodigoOcorrencia = CodedValues.TOC_ALTERACAO_SENHA_USUARIO;
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(tocCodigoOcorrencia);
            ocorrencia.setOusUsuCodigo(CodedValues.USU_CODIGO_SISTEMA);
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.senha.usuario", respValidacao));

            //setar nova senha para todos usuários ligados a este CPF
            final List<TransferObject> lstTodosUsuarios = lstUsuariosSer((String) usuSer.getAttribute(Columns.SER_CPF), null, null, null, respValidacao);
            for (final TransferObject usuTO : lstTodosUsuarios) {
                alterarSenha((String) usuTO.getAttribute(Columns.USU_CODIGO), novaSenha, null, null, false, false, false, senhaCriptografada, null, null, false, senhaApp, null, responsavel);
            }

            if (!TextHelper.isNull(emailOpcional) || !TextHelper.isNull(telefoneOpcional)) {
                final ServidorTransferObject servidor = new ServidorTransferObject((String) usuSer.getAttribute(Columns.SER_CODIGO));
                servidor.setSerCpf((String) usuSer.getAttribute(Columns.SER_CPF));
                if (!TextHelper.isNull(emailOpcional) && TextHelper.isNull(servidor.getSerEmail())) {
                    servidor.setSerEmail(emailOpcional);
                }

                if (!TextHelper.isNull(telefoneOpcional) && TextHelper.isNull(servidor.getSerCelular())) {
                    servidor.setSerCelular(telefoneOpcional);
                }

                ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_USUARIO);
                servidorController.updateServidor(servidor, CodedValues.TOC_RSE_ALTERACAO_DADOS_CADASTRAIS, respValidacao);
            }

            // autentica usuário e retorna dados da autenticação para usuário
            return servidorController.lstRegistroServidorUsuarioSer((String) usuSer.getAttribute(Columns.USU_LOGIN), false, respValidacao);

        } catch (final ServidorControllerException e) {
            throw new UsuarioControllerException(e.getMessageKey(), respValidacao);
        }
    }



    /**
     * Envia OTP numérico de 6 dígitos para servidor por e-mail e/ou celular previamente cadastrados.
     * Os booleanos "enviarOtpEmail" e "enviarOtpCelular" indicam em qual meio o OTP deve ser enviado.
     * Por favor, não alterar o código e inserir parametrizações específicas de caso de uso. Este método
     * deve ser genérico para que seja utilizado por outras rotinas.
     * @param usuCodigo
     * @param enviarOtpEmail
     * @param enviarOtpCelular
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void enviarOtpServidorPorEmailOuCelular(String usuCodigo, boolean enviarOtpEmail, boolean enviarOtpCelular, AcessoSistema responsavel) throws UsuarioControllerException {
        if (!enviarOtpEmail && !enviarOtpCelular) {
            throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
        }

        final String serEmail;
        final String serCelular;
        final String serNome;

        // Buscar usuário através do usuCodigo passado
        UsuarioTransferObject usuario = new UsuarioTransferObject(usuCodigo);
        final Usuario usuarioBean = findUsuarioBean(usuario, AcessoSistema.ENTIDADE_SER);
        if (usuarioBean == null) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
        }

        try {
            final UsuarioSer usuarioSer = UsuarioSerHome.findByUsuCodigo(usuCodigo);
            final String serCodigo = usuarioSer.getServidor().getSerCodigo();
            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
            if (servidor == null) {
                throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }

            serNome = servidor.getSerNome();
            serEmail = servidor.getSerEmail();
            serCelular = servidor.getSerCelular();
        } catch (ServidorControllerException | FindException ex) {
            throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel, ex);
        }

        // Senha numérica com 6 dígitos
        final String otp = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);

        // Criptografa OTP antes de salvar no usuário
        final String otpCrypt = SenhaHelper.criptografarSenha(usuarioBean.getUsuLogin(), otp, true, responsavel);

        // Salva OTP gerado no campo provisório USU_CHAVE_VALIDACAO_TOTP
        usuario = this.findUsuario(usuario, AcessoSistema.ENTIDADE_SER, responsavel);
        usuario.setUsuOtpCodigo(otpCrypt);
        usuario.setUsuOtpDataCadastro(DateHelper.getSystemDatetime());

        // Cria ocorrência
        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
        ocorrencia.setUsuCodigo(usuCodigo);
        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
        ocorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_OTP_USUARIO);
        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.otp.usuario", responsavel));
        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

        // Salva OTP no usuário
        updateUsuario(usuario, ocorrencia, responsavel);

        if (enviarOtpEmail && enviarOtpCelular) {
            if (!TextHelper.isNull(serCelular)) {
                try {
                    EnviaSMSHelper.enviarSMSOTP(serCelular, otp, responsavel);
                } catch (final ZetraException ex) {
                    if (!TextHelper.isNull(serEmail)) {
                        try {
                            EnviaEmailHelper.enviarEmailOTPServidor(serNome, serEmail, otp, responsavel);
                        } catch (final ViewHelperException ex2) {
                            throw new UsuarioControllerException("mensagem.erro.sms.ou.email.enviar", responsavel, ex);
                        }
                    } else {
                        throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, ex);
                    }
                }
            } else if (!TextHelper.isNull(serEmail)) {
                try {
                    EnviaEmailHelper.enviarEmailOTPServidor(serNome, serEmail, otp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel, e);
                }
            } else {
                throw new UsuarioControllerException("mensagem.senha.servidor.otp.erro.email.celular.invalido", responsavel);
            }
        } else if (enviarOtpCelular) {
            if (!TextHelper.isNull(serCelular)) {
                try {
                    EnviaSMSHelper.enviarSMSOTP(serCelular, otp, responsavel);
                } catch (final ZetraException ex) {
                    throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel, ex);
                }
            } else {
                throw new UsuarioControllerException("mensagem.senha.servidor.otp.erro.celular.invalido", responsavel);
            }
        } else if (enviarOtpEmail) {
            if (!TextHelper.isNull(serEmail)) {
                try {
                    EnviaEmailHelper.enviarEmailOTPServidor(serNome, serEmail, otp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel, e);
                }
            } else {
                throw new UsuarioControllerException("mensagem.senha.servidor.otp.erro.email.invalido", responsavel);
            }
        }
    }

    @Override
    public void validarOtpServidorEnviadoPorEmailOuCelular(String usuCodigo, String otp, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final TransferObject usuSer = pesquisarServidorController.buscaUsuarioServidor(usuCodigo, responsavel);

            // Valida a data de expiração do otp
            validarDataCadastroOptExpirado(responsavel, usuSer);

            // valida otp enviado via SMS ou email ao usuário.
            if (TextHelper.isNull(usuSer.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(otp, (String) usuSer.getAttribute(Columns.USU_OTP_CODIGO))) {
                throw new UsuarioControllerException("mensagem.senha.servidor.otp.invalido", responsavel);
            }
        } catch (final ServidorControllerException ex) {
            throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel, ex);
        }
    }

    /**
     * Método para recuperar senha via Web.
     * @param usuCodigo : código do usuário
     * @param tipoEntidade : tipo de entidade do usuário, ex: AcessoSistema.ENTIDADE_SER
     * @param chaveRecuperarSenha : chave de recuperação de senha enviada no e-mail
     * @param senhaNova : nova senha informada pelo usuário
     * @param dicaSenha : dica da senha
     * @param autoDesbloqueio : TRUE se é operação de autodesbloqueio
     * @param responsavel : responsável pela operação
     * @throws UsuarioControllerException
     */
    @Override
    public void recuperarSenha(String usuCodigo, String tipoEntidade, String chaveRecuperarSenha, String senhaNova, String dicaSenha, boolean autoDesbloqueio, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            boolean isCodValido = false;

            if (autoDesbloqueio) {
                isCodValido = validarChaveRecupSenhaAutoDesbloqueio(usuCodigo, chaveRecuperarSenha, responsavel);
            } else {
                isCodValido = validarChaveRecupSenha(usuCodigo, chaveRecuperarSenha, responsavel);
            }

            // Retorno mensagem de erro se o codigo de recuperação de senha for invalido
            if (!isCodValido) {
                throw new UsuarioControllerException("mensagem.erro.servidor.recuperar.senha.codigo.nao.localizado", responsavel);
            }
            // Desbloqueia o servidor caso seja auto desbloqueio
            if (autoDesbloqueio) {
                bloquearDesbloquearUsuario(usuCodigo, CodedValues.STU_ATIVO, tipoEntidade, null, null, responsavel);
            }

            if (tipoEntidade.contentEquals(AcessoSistema.ENTIDADE_SER) && ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNum(senhaNova)) {
                throw new UsuarioControllerException("mensagem.senha.servidor.consulta.deve.ser.numerica", responsavel);
            }

            // Altera a senha do usuário
            alterarSenha(usuCodigo, senhaNova, dicaSenha, false, false, false, null, responsavel);
        } catch (final UsuarioControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    /**
     * Método para recuperar senha via Web.
     * @param usuCodigos : Lista de códigos dos usuários que irão recuperar senha
     * @param tipoEntidade : tipo de entidade do usuário, ex: AcessoSistema.ENTIDADE_SER
     * @param chaveRecuperarSenha : chave de recuperação de senha enviada no e-mail
     * @param senhaNova : nova senha informada pelo usuário
     * @param dicaSenha : dica da senha
     * @param autoDesbloqueio : TRUE se é operação de autodesbloqueio
     * @param responsavel : responsável pela operação
     * @throws UsuarioControllerException
     */
    @Override
    public void recuperarSenha(Set<String> usuCodigos, String tipoEntidade, String chaveRecuperarSenha, String senhaNova, String dicaSenha, boolean autoDesbloqueio, AcessoSistema responsavel) throws UsuarioControllerException {
        for (final String usuCod : usuCodigos) {
            try {
                responsavel = AcessoSistema.recuperaAcessoSistema(usuCod, responsavel.getIpUsuario(), responsavel.getPortaLogicaUsuario());
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }

            recuperarSenha(usuCod, tipoEntidade, chaveRecuperarSenha, senhaNova, dicaSenha, autoDesbloqueio, responsavel);
        }
    }

    @Override
    public void recuperarSenha(String id, String login, List<String> orgCodigos, String otp, String senhaNova, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException {
        this.recuperarSenha(id, login, orgCodigos, otp, senhaNova, senhaApp, true, responsavel);
    }

    /** Método para recuperar senha via web.
     * Através do CPF informado, gera OTP e envia para celular ou email previamente cadastrados.
     * Ao receber, CPF, OTP e nova senha, valida OTP enviado anteriormente e cadastra nova senha.
     * Caso exista mais de um usuário vinculado ao CPF informado, OTP será enviado para todos os usuários e a nova senha também.
     *
     * @param id
     * @param login
     * @param orgCodigos
     * @param otp
     * @param senhaNova
     * @param responsavel
     * @throws UsuarioControllerException
      */
    @Override
    public UsuarioTransferObject recuperarSenha(String id, String login, List<String> orgCodigos, String otp, String senhaNova, boolean senhaApp, boolean isAcessoMobile, AcessoSistema responsavel) throws UsuarioControllerException {

        //Valida a máscara do código otp quando for preenchido
        if (!TextHelper.isNull(otp) && !TextHelper.validaMascara(otp, "#D6")) {
            throw new UsuarioControllerException("mensagem.senha.servidor.otp.invalido", responsavel);
        }

        // Verifica se o sistem permite recuperação de senha
        final Object paramRecuperacaoSenha = isAcessoMobile ? ParamSist.getInstance().getParam(CodedValues.TPC_METODO_ENVIO_OTP_RECUPERACAO_SENHA, responsavel) : AcessoSistema.ENTIDADE_SER.equals(responsavel.getTipoEntidade()) ? ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR, responsavel)
                : ParamSist.getInstance().getParam(CodedValues.TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU, responsavel);
        final String strParamRecuperaSenha = paramRecuperacaoSenha != null ? paramRecuperacaoSenha.toString() : CodedValues.ENVIA_OTP_DESABILITADO;

        if (TextHelper.isNull(paramRecuperacaoSenha) || CodedValues.ENVIA_OTP_DESABILITADO.equals(strParamRecuperaSenha)) {
            throw new UsuarioControllerException(isAcessoMobile ? "mensagem.erro.recuperacao.mobile.nao.disponivel" : "mensagem.erro.recuperacao.nao.disponivel", responsavel);
        }
        if (!CodedValues.ENVIA_OTP_SMS.equals(strParamRecuperaSenha) && !CodedValues.ENVIA_OTP_EMAIL.equals(strParamRecuperaSenha) && !CodedValues.ENVIA_OTP_SMS_OU_EMAIL.equals(strParamRecuperaSenha)) {
            throw new UsuarioControllerException("mensagem.operacaoInvalida", responsavel);
        }

        final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);

        // Recupera tempo de expiração otp
        final ParamSist paramSist = ParamSist.getInstance();
        final String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
        Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);

        // Default 20 minutos
        if (timeoutOtp == null) {
            timeoutOtp = 20;
        }

        // Senha numérica com 6 dígitos - OTP gerado é único para todos usuários com mesmo CPF
        final String otpGerado = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);
        UsuarioTransferObject usuario = null;
        if (responsavel.isSer()) {
            List<TransferObject> lstRegistroServidores = null;
            try {
                if (omiteCpf) {
                    lstRegistroServidores = servidorController.lstRegistroServidorPorEmail(id, orgCodigos, AcessoSistema.getAcessoUsuarioSistema());
                } else {
                    lstRegistroServidores = servidorController.lstRegistroServidorPorCpf(id, orgCodigos, AcessoSistema.getAcessoUsuarioSistema());
                }

            } catch (final ServidorControllerException ex) {
                throw new UsuarioControllerException(ex);
            }

            if ((lstRegistroServidores == null) || lstRegistroServidores.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }

            int serNaoEncontrado = 0;
            int usuOtpInvalido = 0;
            int usuOtpExpirado = 0;
            int usuErroGenerico = 0;
            int erroEnvioOtpSms = 0;
            int erroEnvioOtpEmail = 0;
            int erroEnvioOtpSmsEmail = 0;
            int erroGerarOtp = 0;
            int erroSemEmailCadastrado = 0;
            int erroSemTelCadastrado = 0;
            int totalErros = 0;
            ZetraException excecaoOrigem = null;
            String chaveErro = null;
            boolean notificacaoEnviada = false;

            for (final TransferObject rseTO : lstRegistroServidores) {
                CustomTransferObject usuTO = null;
                try {
                    // Busca o usuário pela chave primária
                    usuTO = pesquisarServidorController.buscaUsuarioServidor(null, null, (String) rseTO.getAttribute(Columns.RSE_MATRICULA), (String) rseTO.getAttribute(Columns.ORG_IDENTIFICADOR), (String) rseTO.getAttribute(Columns.EST_IDENTIFICADOR), responsavel);
                } catch (final ServidorControllerException ex) {
                    serNaoEncontrado++;
                    totalErros++;
                    excecaoOrigem = ex;
                }

                if (usuTO != null) {
                    final String usuCodigo = usuTO.getAttribute(Columns.USU_CODIGO).toString();
                    final String serCodigo = usuTO.getAttribute(Columns.SER_CODIGO).toString();

                    usuario = new UsuarioTransferObject(usuCodigo);
                    usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);

                    if (CodedValues.STU_ATIVO.equals(usuario.getStuCodigo()) || CodedValues.STU_AGUARD_APROVACAO_CADASTRO.equals(usuario.getStuCodigo())) {
                        if (!TextHelper.isNull(otp)) {
                            try {
                                // Valida se o otp expirou
                                final java.util.Date otpDataCadastro = usuTO.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuTO.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
                                final java.util.Date dataAtual = new java.util.Date();

                                // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
                                if (TextHelper.isNull(usuario.getUsuOtpCodigo()) || !JCrypt.verificaSenha(otp, usuario.getUsuOtpCodigo())) {
                                    usuOtpInvalido++;
                                    totalErros++;
                                    chaveErro = "mensagem.senha.servidor.otp.invalido";
                                } else if ((otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * (6 * Math.pow(10, 4))))) {
                                    usuOtpExpirado++;
                                    totalErros++;
                                    chaveErro = "mensagem.senha.servidor.otp.expirado";
                                } else {
                                    // Altera senha
                                    if (senhaApp) {
                                        alterarSenhaApp(usuCodigo, senhaNova, false, true, responsavel);
                                    } else {
                                        alterarSenha(usuCodigo, senhaNova, "", false, false, false, null, responsavel);
                                    }

                                    /*
                                     * Necessário fazer o reload das informações do usuário pois a senha foi alterada em outro método
                                     * e a variável local não é atualizada automaticamente.
                                     */
                                    usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);
                                    // Apaga a chave para recuperação de senha.
                                    usuario.setUsuOtpCodigo(null);
                                    usuario.setUsuDataRecSenha(null);
                                    usuario.setUsuOtpDataCadastro(null);

                                    // Determina parâmetros da ocorrência de usuário a ser gravada.
                                    final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                                    ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                                    ocorrencia.setUsuCodigo(usuCodigo);
                                    ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                                    ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                                    ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.invalida.otp.usuario", responsavel));

                                    // Invalida OTP
                                    updateUsuario(usuario, ocorrencia, responsavel);
                                }
                            } catch (final UsuarioControllerException ex) {
                                usuErroGenerico++;
                                totalErros++;
                                excecaoOrigem = ex;
                            }

                        } else {
                            try {
                                ServidorTransferObject servidor = null;
                                try {
                                    servidor = servidorController.findServidor(serCodigo, responsavel);
                                } catch (final ServidorControllerException ex) {
                                    serNaoEncontrado++;
                                    totalErros++;
                                    excecaoOrigem = ex;
                                }

                                if (servidor != null) {
                                    final String serNome = servidor.getSerNome();
                                    final String serEmail = servidor.getSerEmail();
                                    final String serCelular = servidor.getSerCelular();

                                    // Criptografa OTP antes de salvar no usuário
                                    final String otpCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), otpGerado, true, responsavel);

                                    // Salva chave para recuperação de senha.
                                    final java.util.Date dataCadastro = DateHelper.getSystemDatetime();
                                    usuario.setUsuOtpCodigo(otpCrypt);
                                    usuario.setUsuDataRecSenha(dataCadastro);
                                    usuario.setUsuOtpDataCadastro(dataCadastro);

                                    // Determina parâmetros da ocorrência de usuário a ser gravada.
                                    final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                                    ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                                    ocorrencia.setUsuCodigo(usuCodigo);
                                    ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                                    ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                                    ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.otp.usuario", responsavel));

                                    updateUsuario(usuario, ocorrencia, responsavel);

                                    if (!notificacaoEnviada) {
                                        if (CodedValues.ENVIA_OTP_SMS.equals(strParamRecuperaSenha) || CodedValues.ENVIA_OTP_SMS_OU_EMAIL.equals(strParamRecuperaSenha)) {
                                            try {
                                                EnviaSMSHelper.enviarSMSOTP(serCelular, otpGerado, responsavel);
                                                notificacaoEnviada = true;
                                            } catch (final ZetraException e) {
                                                if (CodedValues.ENVIA_OTP_SMS_OU_EMAIL.equals(strParamRecuperaSenha)) {
                                                    try {
                                                        EnviaEmailHelper.enviarEmailOTPServidor(serNome, serEmail, otpGerado, responsavel);
                                                        notificacaoEnviada = true;
                                                    } catch (final ViewHelperException ex) {
                                                        erroEnvioOtpSmsEmail++;
                                                        totalErros++;
                                                        excecaoOrigem = ex;
                                                        if (TextHelper.isNull(serEmail)) {
                                                            erroSemEmailCadastrado++;
                                                        }
                                                        if (TextHelper.isNull(serCelular)) {
                                                            erroSemTelCadastrado++;
                                                        }
                                                    }
                                                } else {
                                                    erroEnvioOtpSms++;
                                                    totalErros++;
                                                    excecaoOrigem = e;
                                                }
                                            }
                                        } else if (CodedValues.ENVIA_OTP_EMAIL.equals(strParamRecuperaSenha)) {
                                            try {
                                                EnviaEmailHelper.enviarEmailOTPServidor(serNome, serEmail, otpGerado, responsavel);
                                                notificacaoEnviada = true;
                                            } catch (final ViewHelperException ex) {
                                                erroEnvioOtpEmail++;
                                                totalErros++;
                                                excecaoOrigem = ex;
                                                if (TextHelper.isNull(serEmail)) {
                                                    erroSemEmailCadastrado++;
                                                }
                                            }
                                        }
                                    }
                                }

                            } catch (final UsuarioControllerException ex) {
                                erroGerarOtp++;
                                totalErros++;
                                excecaoOrigem = ex;
                            }
                        }
                    } else {
                        totalErros++;
                        chaveErro = "mensagem.erro.status.usuario.nao.permite.operacao";
                    }
                }
            }

            // Caso apresente o mesmo erro para todas as tentativas, retorna erro gerado

            if (serNaoEncontrado == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.servidor.nao.encontrado";
            } else if (usuOtpInvalido == lstRegistroServidores.size()) {
                chaveErro = "mensagem.senha.servidor.otp.invalido";
            } else if (usuOtpExpirado == lstRegistroServidores.size()) {
                chaveErro = "mensagem.senha.servidor.otp.expirado";
            } else if (erroEnvioOtpSms == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.sms.enviar";
            } else if ((erroSemEmailCadastrado == lstRegistroServidores.size()) && (erroSemTelCadastrado == lstRegistroServidores.size())) {
                chaveErro = "mensagem.erro.tel.email.ser.nao.cadastrado";
            } else if (erroSemEmailCadastrado == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.email.ser.nao.cadastrado";
            } else if (erroSemTelCadastrado == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.celular.ser.nao.cadastrado";
            } else if (erroEnvioOtpEmail == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.email.enviar";
            } else if (erroEnvioOtpSmsEmail == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.sms.ou.email.enviar";
            } else if (usuErroGenerico == lstRegistroServidores.size()) {
                if ((excecaoOrigem != null) && !TextHelper.isNull(excecaoOrigem.getMessageKey())) {
                    chaveErro = excecaoOrigem.getMessageKey();
                } else {
                    chaveErro = "mensagem.erro.alterar.chave.recuperacao.senha.usuario";
                }
            } else if (erroGerarOtp == lstRegistroServidores.size()) {
                chaveErro = "mensagem.erro.alterar.chave.recuperacao.senha.usuario";
            } else if (totalErros == lstRegistroServidores.size()) {
                if (TextHelper.isNull(chaveErro) && (excecaoOrigem != null) && !TextHelper.isNull(excecaoOrigem.getMessageKey())) {
                    chaveErro = excecaoOrigem.getMessageKey();
                } else if (TextHelper.isNull(chaveErro)) {
                    chaveErro = "mensagem.erro.alterar.chave.recuperacao.senha.usuario";
                }
            }
            if ((totalErros == lstRegistroServidores.size()) && !TextHelper.isNull(chaveErro)) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new UsuarioControllerException(chaveErro, responsavel, excecaoOrigem, excecaoOrigem != null ? excecaoOrigem.getMessageArgs() : null);
            }

        } else {
            final CustomTransferObject usuTO = findUsuarioByLogin(login, responsavel);
            final String usuCodigo = usuTO.getAttribute(Columns.USU_CODIGO).toString();
            usuario = new UsuarioTransferObject(usuCodigo);
            usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);

            if (CodedValues.STU_ATIVO.equals(usuario.getStuCodigo()) && (usuario.getUsuCPF() != null) && (id != null) && usuario.getUsuCPF().equals(id)) {
                if (!TextHelper.isNull(otp)) {
                    try {
                        // Valida se o otp expirou
                        final java.util.Date otpDataCadastro = usuario.getUsuOtpDataCadastro();
                        final java.util.Date dataAtual = new java.util.Date();

                        // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
                        if (TextHelper.isNull(usuario.getUsuOtpCodigo()) || !JCrypt.verificaSenha(otp, usuario.getUsuOtpCodigo())) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.codigo.otp.invalido", responsavel);
                        } else if ((otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * (6 * Math.pow(10, 4))))) {
                            throw new UsuarioControllerException("mensagem.erro.usuario.codigo.otp.vencido", responsavel);
                        } else {
                            // Altera senha
                            if (senhaApp) {
                                alterarSenhaApp(usuCodigo, senhaNova, false, responsavel);
                            } else {
                                alterarSenha(usuCodigo, senhaNova, "", false, false, false, null, responsavel);
                            }

                            /*
                             * Necessário fazer o reload das informações do usuário pois a senha foi alterada em outro método
                             * e a variável local não é atualizada automaticamente.
                             */
                            usuario = findUsuario(usuario, AcessoSistema.ENTIDADE_USU, responsavel);
                            // Apaga a chave para recuperação de senha.
                            usuario.setUsuOtpCodigo(null);
                            usuario.setUsuDataRecSenha(null);
                            usuario.setUsuOtpDataCadastro(null);

                            // Determina parâmetros da ocorrência de usuário a ser gravada.
                            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                            ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                            ocorrencia.setUsuCodigo(usuCodigo);
                            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.invalida.otp.usuario", responsavel));

                            // Invalida OTP
                            updateUsuario(usuario, ocorrencia, responsavel);
                        }
                    } catch (final UsuarioControllerException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw ex;
                    }
                } else {
                    try {
                        final String nome = usuario.getUsuNome();
                        final String usuEmail = usuario.getUsuEmail();
                        final String usuCelular = usuario.getUsuTel(); // TODO: usuario.getUsuCelular();

                        // Criptografa OTP antes de salvar no usuário
                        final String otpCrypt = SenhaHelper.criptografarSenha(usuario.getUsuLogin(), otpGerado, false, responsavel);

                        // Salva chave para recuperação de senha.
                        final java.util.Date dataCadastro = DateHelper.getSystemDatetime();
                        usuario.setUsuOtpCodigo(otpCrypt);
                        usuario.setUsuDataRecSenha(dataCadastro);
                        usuario.setUsuOtpDataCadastro(dataCadastro);

                        // Determina parâmetros da ocorrência de usuário a ser gravada.
                        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                        ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                        ocorrencia.setUsuCodigo(usuCodigo);
                        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.otp.usuario", responsavel));

                        updateUsuario(usuario, ocorrencia, responsavel);

                        if (CodedValues.ENVIA_OTP_SMS.equals(strParamRecuperaSenha) || CodedValues.ENVIA_OTP_SMS_OU_EMAIL.equals(strParamRecuperaSenha)) {
                            try {
                                EnviaSMSHelper.enviarSMSOTP(usuCelular, otpGerado, responsavel);
                            } catch (final ZetraException e) {
                                if (CodedValues.ENVIA_OTP_SMS_OU_EMAIL.equals(strParamRecuperaSenha)) {
                                    try {
                                        EnviaEmailHelper.enviarEmailOTP(nome, usuEmail, otpGerado, responsavel);
                                    } catch (final ViewHelperException ex) {
                                        throw new UsuarioControllerException("mensagem.erro.sms.ou.email.enviar", responsavel);
                                    }
                                } else {
                                    throw new UsuarioControllerException("mensagem.erro.sms.enviar", responsavel);
                                }
                            }
                        } else if (CodedValues.ENVIA_OTP_EMAIL.equals(strParamRecuperaSenha)) {
                            try {
                                EnviaEmailHelper.enviarEmailOTP(nome, usuEmail, otpGerado, responsavel);
                            } catch (final ViewHelperException e) {
                                throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel);
                            }
                        }
                    } catch (final UsuarioControllerException ex) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new UsuarioControllerException("mensagem.erro.alterar.chave.recuperacao.senha.usuario", responsavel, ex);
                    }
                }
            }
        }
        return usuario;
    }

    @Override
    public TransferObject buscarUsuarioPorCodRecuperarSenha(String codRecuperar, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemPapelUsuarioQuery query = new ObtemPapelUsuarioQuery();
            query.usuChaveRecuperarSenha = codRecuperar;
            final List<TransferObject> resultado = query.executarDTO();
            if ((resultado != null) && !resultado.isEmpty()) {
                return resultado.get(0);
            }
            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void atualizarUsuarioAutorizacaoEmailMarketing(UsuarioTransferObject usuario, String usuAutoriza, AcessoSistema responsavel) throws UsuarioControllerException {
        usuario.setUsuAutorizaEmailMarketing(CodedValues.TPC_SIM.equals(usuAutoriza) ? CodedValues.TPC_SIM : CodedValues.TPC_NAO);
        updateUsuario(usuario, null, responsavel);
    }

    /** NOME DE USUÁRIO (PARA LOGIN EM DUAS ETAPAS) ---------------------------------------------------------------------- **/

    @Override
    public int countNomeUsuario(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemNomeUsuarioQuery query = new ObtemNomeUsuarioQuery();
            query.count = true;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public TransferObject obtemNomeUsuario(String login, String usuCodigo, int offset, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ObtemNomeUsuarioQuery query = new ObtemNomeUsuarioQuery();
            query.usuLogin = login;
            query.usuCodigo = usuCodigo;
            if (offset != -1) {
                query.firstResult = offset;
            }
            query.maxResults = 1;
            final List<TransferObject> lista = query.executarDTO();
            if ((lista == null) || lista.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
            }

            return lista.get(0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    /**
     * Método que retorna a lista de usuários a serem notificados por tempo de initividade
     * no sistema.
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     * @throws HQueryException
     */
    @Override
    public List<TransferObject> enviaNotificacaoUsuariosPorTempoInatividade(AcessoSistema responsavel) throws UsuarioControllerException {

        List<TransferObject> lista = null;

        try {
            final Integer diasSemAcessoCse = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel).toString()) : 0;
            final Integer diasSemAcessoCsa = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel).toString()) : 0;
            final Integer diasSemAcessoSer = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel).toString()) : 0;
            final Integer qtdDiasAntesNotificacao = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_NOTIFICACAO_BLOQUEIO_INATIVIDADE, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_NOTIFICACAO_BLOQUEIO_INATIVIDADE, responsavel).toString()) : 0;

            // DESENV-14804 Quantidade de horas que o usuário tem de prazo para logar no sistema após o desbloqueio no sistema.
            Integer qtdeHorasPrazoLoginUsuario = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_HORAS_USUARIO_LOGAR_APOS_DESBLOQUEIO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_HORAS_USUARIO_LOGAR_APOS_DESBLOQUEIO, responsavel).toString()) : 48;
            // Prazo máximo permitido será 10 (dez) dias, ou seja 240 horas.
            if (qtdeHorasPrazoLoginUsuario > 240) {
                qtdeHorasPrazoLoginUsuario = 240;
            }

            // Só executa se um dos parâmetros estiver setada a quantidade de dias sem acesso
            if (((diasSemAcessoCse.compareTo(0) > 0) || (diasSemAcessoCsa.compareTo(0) > 0) || (diasSemAcessoSer.compareTo(0) > 0)) && (qtdDiasAntesNotificacao.compareTo(0) > 0)) {

                final ListaUsuarioNotificacaoInatividadeQuery query = new ListaUsuarioNotificacaoInatividadeQuery();
                final Calendar data = Calendar.getInstance();
                data.add(Calendar.HOUR, -qtdeHorasPrazoLoginUsuario);
                query.dataLimiteBloqueio = data.getTime();
                lista = query.executarDTO();
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.nao.foi.possivel.notificar.usuario.bloqueio.inatividade", responsavel, ex);
        }
        return lista;
    }

    @Override
    public void limparDadosOTP(TransferObject usuario, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);
            final Usuario usuarioBean = UsuarioHome.findByPrimaryKey(usuCodigo);

            usuarioBean.setUsuOtpCodigo(null);
            usuarioBean.setUsuOtpChaveSeguranca(null);
            usuarioBean.setUsuChaveValidacaoTotp(null);
            usuarioBean.setUsuOtpDataCadastro(null);

            AbstractEntityHome.update(usuarioBean);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Método que gera o código de autorização a ser enviado ao Servidor via SMS.
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    private String gerarCodigoAutorizacaoSms(AcessoSistema responsavel) throws UsuarioControllerException {

        //Gera o código de autorização
        String codAut;
        try {
            codAut = GeradorSenhaUtil.getPasswordNumber(6, responsavel);
        } catch (final UsuarioControllerException e) {
            LOG.debug("Erro ao gerar o código de autorização: " + e.getMessage());
            return null;
        }

        //Salva o código na tabela de usuário
        UsuarioTransferObject usu = new UsuarioTransferObject();
        try {
            usu = findUsuario(responsavel.getUsuCodigo(), responsavel);

            final String codCriptografado = JCrypt.crypt(codAut);

            // registra dados no USU_OTP_CODIGO
            usu.setUsuOtpCodigo(codCriptografado);
            usu.setUsuOtpDataCadastro(DateHelper.getSystemDatetime());

            // Grava ocorrência de geração de OTP
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_OTP_USUARIO);
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo() != null ? responsavel.getUsuCodigo() : AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.usuario", responsavel));
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

            updateUsuario(usu, ocorrencia, null, null, AcessoSistema.ENTIDADE_SER, responsavel.getUsuCodigo(), null, responsavel);
        } catch (final UsuarioControllerException ex) {
            LOG.debug("Erro ao salvar o código de autorização: " + ex.getMessage());
            codAut = null;
            throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.gerar.codigo.autorizacao.solicitacao", responsavel, ex);
        }

        return codAut;
    }

    /**
     * Método que envia o código gerado, ao celular do Servidor, cadastrado na tb_servdor, campo "ser_celular".
     * @param rseCodigo
     * @param responsavel
     * @throws ServidorControllerException
     * @throws UsuarioControllerException
     */
    @Override
    public void enviarCodigoAutorizacaoSms(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException, UsuarioControllerException {

        if ((rseCodigo == null) || !validarCodigoAutorizacaoSmsExpirado(responsavel)) {
            return;
        }

        String celularDestinatario = null;

        // Busca mensagem a ser enviada
        String corpo = ApplicationResourcesHelper.getMessage("mensagem.sms.servidor.codigo.unico", responsavel);

        // Busca dados do servidor
        final ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);

        if (servidor != null) {
            // Formata o telefone para o padrão do país
            celularDestinatario = !TextHelper.isNull(servidor.getSerCelular()) ? LocaleHelper.formataCelular(servidor.getSerCelular()) : null;
        }

        if (!TextHelper.isNull(celularDestinatario)) {
            // Envia o SMS.
            try {
                final String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                final String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                final String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                final String codAut = gerarCodigoAutorizacaoSms(responsavel);

                if (TextHelper.isNull(codAut)) {
                    return;
                }

                corpo += codAut;
                new SMSHelper(accountSid, authToken, fromNumber).send(celularDestinatario, corpo);

            } catch (final ZetraException ex) {
                LOG.debug("Erro ao enviar o SMS: " + ex.getMessage());
                throw new UsuarioControllerException("mensagem.erro.interno.nao.possivel.gerar.codigo.autorizacao.solicitacao", responsavel, ex);
            }
        } else {
            throw new UsuarioControllerException("mensagem.erro.necessita.telefone.cadastrado", responsavel);
        }

    }

    /**
     * Método que valida o código enviado ao celular do Servidor, que foi informado na tela da interface web.
     * @param codAut
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public boolean validarCodigoAutorizacaoSms(String codAut, AcessoSistema responsavel) throws UsuarioControllerException {

        TransferObject usuario = null;

        try {
            usuario = pesquisarServidorController.buscaUsuarioServidor(responsavel.getUsuCodigo(), responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.debug("Erro ao localizar o servidor: " + ex.getMessage());
            throw new UsuarioControllerException("mensagem.erro.localizar.servidor", responsavel, ex);
        }

        final ParamSist paramSist = ParamSist.getInstance();
        final String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
        Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);

        // default 20 minutos
        if (timeoutOtp == null) {
            timeoutOtp = 20;
        }

        // Valida se o otp expirou
        final java.util.Date otpDataCadastro = usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
        final java.util.Date dataAtual = new java.util.Date();

        // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
        if (TextHelper.isNull(usuario.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(codAut, usuario.getAttribute(Columns.USU_OTP_CODIGO).toString())) {
            throw new UsuarioControllerException("mensagem.erro.codigo.autorizacao.invalido", responsavel);
        } else if ((otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * 60000))) {
            throw new UsuarioControllerException("mensagem.erro.codigo.autorizacao.expirado", responsavel);
        }

        return true;
    }

    /**
     * Método que valida o tempo de expiração do código enviado ao celular do Servido.
     * @param codAut
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    private boolean validarCodigoAutorizacaoSmsExpirado(AcessoSistema responsavel) throws UsuarioControllerException {
        TransferObject usuario = null;

        try {
            usuario = pesquisarServidorController.buscaUsuarioServidor(responsavel.getUsuCodigo(), responsavel);
        } catch (final ServidorControllerException ex) {
            LOG.debug("Erro ao localizar o servidor: " + ex.getMessage());
            throw new UsuarioControllerException("mensagem.erro.localizar.servidor", responsavel, ex);
        }

        final ParamSist paramSist = ParamSist.getInstance();
        final String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
        Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);

        // default 20 minutos
        if (timeoutOtp == null) {
            timeoutOtp = 20;
        }

        // Valida se o otp expirou
        final java.util.Date otpDataCadastro = usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
        final java.util.Date dataAtual = new java.util.Date();

        // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
        return (otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * 60000));
    }

    @Override
    public List<Papel> listarPapeis(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            return PapelHome.listarPapeis();
        } catch (final FindException e) {
            throw new UsuarioControllerException("mensagem.menu.papel.indefinido", responsavel);
        }
    }

    private Calendar calculaDataExpiracaoSenha(boolean senhaAutorizacaoServidor, UsuarioTransferObject usuario, boolean usuarioSer, AcessoSistema responsavel) throws UsuarioControllerException {
        final Calendar dataExpiracaoSenhaCalc = Calendar.getInstance();
        // Determina o prazo de expiração, de acordo com o tipo de usuário.
        String paramPrazoExpiracaoSenha;
        if (usuarioSer) {
            paramPrazoExpiracaoSenha = CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_SER;
        } else {
            final TransferObject tipoUsuario = obtemUsuarioTipo(usuario.getUsuCodigo(), usuario.getUsuLogin(), responsavel);
            final String tipo = (String) tipoUsuario.getAttribute("TIPO");
            if (tipo == null) {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.determinar.tipo.usuario", responsavel);
            }
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                paramPrazoExpiracaoSenha = CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_CSE_ORG;
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                paramPrazoExpiracaoSenha = CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_CSA_COR;
            } else if (AcessoSistema.ENTIDADE_SER.equals(tipo)) {
                paramPrazoExpiracaoSenha = CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_SER;
            } else {
                throw new UsuarioControllerException("mensagem.erro.nao.possivel.determinar.tipo.usuario", responsavel);
            }
        }

        String prazo = (String) ParamSist.getInstance().getParam(paramPrazoExpiracaoSenha, responsavel);
        if (TextHelper.isNull(prazo)) {
            throw new UsuarioControllerException("mensagem.erro.nao.ha.prazo.expiracao.senha.definido", responsavel);
        }
        if (senhaAutorizacaoServidor) {
            // Se é senha de autorização de servidor e a senha possui validade em dias
            // determina a data de expiração de acordo com este parâmetro, e não com
            // o parâmetro de expiração de senha de servidor.
            final String prazoValidade = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTD_DIAS_VALIDADE_SENHA_AUTORIZACAO, responsavel);
            if (!TextHelper.isNull(prazoValidade) && (Integer.parseInt(prazoValidade) > 0)) {
                prazo = prazoValidade;
            }
        }
        dataExpiracaoSenhaCalc.add(Calendar.DATE, Integer.parseInt(prazo));

        return dataExpiracaoSenhaCalc;
    }

    /**
     * geração e envio de OTP exclusivamente para validação de e-mail
     * @param servidor
     * @param emailValidacao
     * @param responsavel
     * @throws UsuarioControllerException
     */
    @Override
    public void gerarOtpConfirmacaoEmail(Servidor servidor, String emailValidacao, AcessoSistema responsavel) throws UsuarioControllerException {
        if (TextHelper.isNull(emailValidacao)) {
            throw new UsuarioControllerException("mensagem.informe.servidor.email", responsavel);
        }

        final List<TransferObject> usuarios = lstUsuariosSer(servidor.getSerCpf(), null, null, null, responsavel);
        if ((usuarios == null) || usuarios.isEmpty()) {
            throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
        }

        // Senha numérica com 6 dígitos
        final String otp = GeradorSenhaUtil.getPasswordNumber(CodedValues.TAM_OTP, responsavel);

        try {
            if (TextHelper.isNull(servidor.getSerEmail()) || !servidor.getSerEmail().equals(emailValidacao)) {
                final ServidorTransferObject serTO = new ServidorTransferObject(servidor.getSerCodigo());
                serTO.setSerEmail(emailValidacao);
                serTO.setSerDataValidacaoEmail(null);
                servidorController.updateServidor(serTO, responsavel);
            }

            for (final TransferObject usuario : usuarios) {
                // Criptografa OTP antes de salvar no usuário
                final String otpCrypt = SenhaHelper.criptografarSenha((String) usuario.getAttribute(Columns.USU_CODIGO), otp, true, responsavel);

                // Salva OTP gerado no campo provisório USU_CHAVE_VALIDACAO_TOTP
                UsuarioTransferObject usuTo = new UsuarioTransferObject();
                usuTo.setAtributos(usuario.getAtributos());

                usuTo = findUsuario(usuTo, AcessoSistema.ENTIDADE_SER, responsavel);
                usuTo.setUsuOtpCodigo(otpCrypt);
                usuTo.setUsuOtpDataCadastro(DateHelper.getSystemDatetime());
                if (ParamSist.paramEquals(CodedValues.TPC_VALIDAR_KYC_FACESWEB_INTEGRACAO_SALARYPAY, CodedValues.TPC_SIM, responsavel)) {
                    usuTo.setStuCodigo(CodedValues.STU_AGUARD_APROVACAO_CADASTRO);
                }

                // Cria ocorrência
                final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                ocorrencia.setUsuCodigo((String) usuario.getAttribute(Columns.USU_CODIGO));
                ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                ocorrencia.setTocCodigo(CodedValues.TOC_INCLUSAO_OTP_USUARIO);
                ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.inclusao.otp.usuario", responsavel));
                ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                // Salva OTP no usuário
                updateUsuario(usuTo, ocorrencia, responsavel);

                try {
                    EnviaEmailHelper.enviarEmailOTPServidor(servidor.getSerNome(), emailValidacao, otp, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.falha.enviar.email", responsavel, e);
                }
            }
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /** Altera a chave de validação de Email
     * @param dadosUsuario : UsuarioTransferObject
     * @param responsavel : Usuário que está realizando a validação de email
     * @throws UsuarioControllerException
     */
    @Override
    public void alteraChaveValidacaoEmail(UsuarioTransferObject dadosUsuario, String link, AcessoSistema responsavel) throws UsuarioControllerException {
        if (!CodedValues.STU_ATIVO.equals(dadosUsuario.getStuCodigo())) {
            throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
        }

        try {
            // Determina parâmetros da ocorrência de usuário a ser gravada.
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setTocCodigo(CodedValues.TOC_VALIDACAO_EMAIL_USUARIO);
            ocorrencia.setUsuCodigo(dadosUsuario.getUsuCodigo());
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.validacao.email.usuario", responsavel));

            updateUsuario(dadosUsuario, ocorrencia, responsavel);

            enviaLinkValidaEmailUsuario(dadosUsuario.getUsuEmail(), dadosUsuario.getUsuNome(), link, dadosUsuario.getUsuChaveValidacaoEmail(), responsavel);

        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    /**
     * Envia ao usuário link de verificação do email
     * @param usuCodigo
     * @param emailDestinatario
     * @param link - link a ser enviado no e-mail que direciona para a página de confirmação de email.
     * @param codigo - token de fluxo de acesso da URL
     * @param responsavel
     * @throws UsuarioControllerException
     */
    private void enviaLinkValidaEmailUsuario(String emailDestinatario, String usuNome, String link, String codSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        link = link + "&codValidacao=" + codSenha;

        if (!TextHelper.isNull(emailDestinatario)) {
            try {
                EnviaEmailHelper.enviarEmailLinkValidarEmailusuario(emailDestinatario, usuNome, link, responsavel);
            } catch (final ViewHelperException e) {
                throw new UsuarioControllerException("mensagem.erro.falha.enviar.email", responsavel, e);
            }
        }
    }

    @Override
    public TransferObject buscarUsuarioPorCodValidaEmail(String codValidacaoEmail, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            if (!TextHelper.isNull(codValidacaoEmail)) {

                final ObtemPapelUsuarioQuery query = new ObtemPapelUsuarioQuery();
                query.usuChaveValidacaoEmail = codValidacaoEmail;
                final List<TransferObject> resultado = query.executarDTO();
                if ((resultado != null) && !resultado.isEmpty()) {
                    for (final TransferObject usuario : resultado) {
                        final String usuCodigo = usuario.getAttribute(Columns.USU_CODIGO).toString();
                        final UsuarioTransferObject dadosUsuario = findUsuario(usuCodigo, responsavel);
                        responsavel.setUsuCodigo(usuCodigo);
                        dadosUsuario.setUsuDataValidacaoEmail(DateHelper.getSystemDatetime());

                        final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
                        ocorrencia.setTocCodigo(CodedValues.TOC_CONFIRMACAO_EMAIL_USUARIO);
                        ocorrencia.setUsuCodigo(dadosUsuario.getUsuCodigo());
                        ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                        ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());
                        ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.email.usuario", responsavel));

                        updateUsuario(dadosUsuario, ocorrencia, responsavel);

                    }
                    return resultado.get(0);
                }
            }
            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * valida OTP para uso exclusivo em validação de e-mail
     * @param codAut
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    @Override
    public boolean validarOtpConfirmacaoEmail(String otp, String serCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        //TODO: tratar quando retornar mais de um servidor
        try {
            final List<Servidor> servidores = ServidorHome.findByCPF(serCpf);
            final List<TransferObject> usuSerTos = this.lstUsuariosSer(servidores.get(0).getSerCpf(), null, null, null, responsavel);

            if ((usuSerTos == null) || usuSerTos.isEmpty()) {
                throw new UsuarioControllerException("mensagem.erro.usuario.nao.encontrado", responsavel);
            }

            //TODO: tratar quando retornar mais de um usuário
            final TransferObject usuario = usuSerTos.get(0);

            final ParamSist paramSist = ParamSist.getInstance();
            final String timeoutOtpString = (String) paramSist.getParam(CodedValues.TPC_TEMPO_EXPIRACAO_OTP, AcessoSistema.getAcessoUsuarioSistema());
            Integer timeoutOtp = TextHelper.isNull(timeoutOtpString) ? null : Integer.valueOf(timeoutOtpString);

            // default 20 minutos
            if (timeoutOtp == null) {
                timeoutOtp = 20;
            }

            // Valida se o otp expirou
            final java.util.Date otpDataCadastro = usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) != null ? (java.util.Date) usuario.getAttribute(Columns.USU_OTP_DATA_CADASTRO) : null;
            final java.util.Date dataAtual = new java.util.Date();

            // Verifica se otp é inválido ou se passou o limite do tempo em milisegundos
            if (TextHelper.isNull(usuario.getAttribute(Columns.USU_OTP_CODIGO)) || !JCrypt.verificaSenha(otp, usuario.getAttribute(Columns.USU_OTP_CODIGO).toString())) {
                throw new UsuarioControllerException("mensagem.erro.codigo.autorizacao.invalido", responsavel);
            } else if ((otpDataCadastro == null) || ((dataAtual.getTime() - otpDataCadastro.getTime()) > (timeoutOtp * 60000))) {
                throw new UsuarioControllerException("mensagem.erro.codigo.autorizacao.expirado", responsavel);
            } else {
                // atualiza os campos do servidor que informam que o e-mail está validado
                final Servidor servidor = servidores.get(0);
                final ServidorTransferObject serTO = new ServidorTransferObject(servidor.getSerCodigo());
                serTO.setSerDataValidacaoEmail(new Timestamp(DateHelper.getSystemDatetime().getTime()));
                servidorController.updateServidor(serTO, responsavel);
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException("mensagem.erro.localizar.servidor", responsavel, ex);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        return true;
    }

    /**
     * Retorna TRUE caso exista outro usuário não excluído no sistema com o mesmo e-mail
     * @param usuEmail
     * @param usuCpfExceto
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    private boolean existeOutroUsuarioMesmoEmail(String usuEmail, String usuCpfExceto, AcessoSistema responsavel) throws UsuarioControllerException {
        boolean existeEmailCadastrado = false;
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_IMPEDE_EMAIL_IGUAL_ENTRE_USU, CodedValues.TPC_SIM, responsavel)) {
                final ObtemTotalUsuariosPorEmailQuery query = new ObtemTotalUsuariosPorEmailQuery();
                query.usuEmail = usuEmail;
                query.usuCpfExceto = usuCpfExceto;
                existeEmailCadastrado = query.executarContador() > 0;
            }
            if (!existeEmailCadastrado && ParamSist.paramEquals(CodedValues.TPC_IMPEDE_EMAIL_IGUAL_ENTRE_USU_E_SER, CodedValues.TPC_SIM, responsavel)) {
                final ObtemTotalServidoresPorEmailCelularQuery query = new ObtemTotalServidoresPorEmailCelularQuery();
                query.serEmail = usuEmail;
                query.serCpfExceto = usuCpfExceto;
                existeEmailCadastrado = query.executarContador() > 0;
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
        return existeEmailCadastrado;
    }

    private String criaOcorrenciaPerfil(String perCodigo, String tocCodigo, String oprObs, String tmoCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : null;
            final OcorrenciaPerfil opr = OcorrenciaPerfilHome.create(perCodigo, tocCodigo, usuCodigo, oprObs, responsavel.getIpUsuario(), tmoCodigo);
            return opr.getOprCodigo();
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaOcorrenciaPerfilQuery query = new ListaOcorrenciaPerfilQuery();
            query.count = true;
            query.perCodigo = perCodigo;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.contar.ocorrencias", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaPerfil(String perCodigo, int offset, int count, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaOcorrenciaPerfilQuery query = new ListaOcorrenciaPerfilQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (count != -1) {
                query.maxResults = count;
            }
            query.perCodigo = perCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.listar.ocorrencias", responsavel, ex);
        }
    }

    private TransferObject obtemServidorPorUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final ObtemUsuarioServidorQuery query = new ObtemUsuarioServidorQuery();
        query.usuCodigo = usuCodigo;

        TransferObject servidor = null;
        try {
            final List<TransferObject> servidores = query.executarDTO();
            if ((servidores != null) && (servidores.size() == 1)) {
                servidor = servidores.get(0);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (servidor == null) {
            throw new UsuarioControllerException("mensagem.erro.nao.possivel.encontrar.servidor", responsavel);
        }

        return servidor;
    }

    @Override
    public String[] matriculasUsuariosServidores(String usuCodigo, String senhaNovaCrypt, boolean alteraSenha, AcessoSistema responsavel) throws UsuarioControllerException {

        try {
            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            final List<Usuario> usuarios = UsuarioHome.listaUsuServidores(usuCodigo);
            final String[] usuLogin = new String[usuarios.size()];
            int posicaoUsu = 0;

            for (final Usuario usu : usuarios) {
                final String[] matriculasSer = usu.getUsuLogin().split("-");
                usuLogin[posicaoUsu] = matriculasSer[matriculasSer.length - 1];

                if (!usuCodigo.equals(usu.getUsuCodigo()) && alteraSenha) {
                    final UsuarioTransferObject usuTranferObject = new UsuarioTransferObject(usu.getUsuCodigo());
                    usuTranferObject.setUsuSenha(senhaNovaCrypt);

                    ocorrencia.setUsuCodigo(usu.getUsuCodigo());
                    ocorrencia.setTocCodigo(CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                    ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
                    ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.alteracao.senhas.servidor", responsavel));
                    ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

                    updateUsuario(usuTranferObject, ocorrencia, responsavel);

                }
                posicaoUsu++;
            }
            return usuLogin;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void bloqueiaPerfilDataExpiracao(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            // Lista os perfils com data expirada
            final List<Perfil> lstPerfil = PerfilHome.findPerfilExpirado();

            if ((lstPerfil != null) && !lstPerfil.isEmpty()) {
                final Short status = CodedValues.STS_INATIVO;
                for (final Perfil perfil : lstPerfil) {
                    final String perCodigo = perfil.getPerCodigo();
                    //Iremos fazer o bloqueio para cada tipo de perfil CSE,ORG,CSA,COR,SUP,USU para que seja criado as ocorrêcia de bloqueio
                    final List<PerfilCse> lstPerfilCse = PerfilCseHome.findByPerCodigo(perCodigo);
                    final List<PerfilOrg> lstPerfilOrg = PerfilOrgHome.findByPerCodigo(perCodigo);
                    final List<PerfilCsa> lstPerfilCsa = PerfilCsaHome.findByPerCodigo(perCodigo);
                    final List<PerfilCor> lstPerfilCor = PerfilCorHome.findByPerCodigo(perCodigo);
                    final List<PerfilSup> lstPerfilSup = PerfilSupHome.findByPerCodigo(perCodigo);

                    if ((lstPerfilCse != null) && !lstPerfilCse.isEmpty()) {
                        final String tipoEntidade = AcessoSistema.ENTIDADE_CSE;
                        for (final PerfilCse perfilCse : lstPerfilCse) {
                            final String cseCodigo = perfilCse.getConsignante().getCseCodigo();
                            updatePerfil(tipoEntidade, cseCodigo, perCodigo, null, null, null, null, status, null,null, null, null, responsavel);
                        }
                    }

                    if ((lstPerfilOrg != null) && !lstPerfilOrg.isEmpty()) {
                        final String tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                        for (final PerfilOrg perfilOrg : lstPerfilOrg) {
                            final String orgCodigo = perfilOrg.getOrgao().getOrgCodigo();
                            updatePerfil(tipoEntidade, orgCodigo, perCodigo, null, null, null, null, status, null, null, null, null, responsavel);
                        }
                    }

                    if ((lstPerfilCsa != null) && !lstPerfilCsa.isEmpty()) {
                        final String tipoEntidade = AcessoSistema.ENTIDADE_CSA;
                        for (final PerfilCsa perfilCsa : lstPerfilCsa) {
                            final String csaCodigo = perfilCsa.getConsignataria().getCsaCodigo();
                            updatePerfil(tipoEntidade, csaCodigo, perCodigo, null, null, null, null, status, null, null, null, null, responsavel);
                        }
                    }

                    if ((lstPerfilCor != null) && !lstPerfilCor.isEmpty()) {
                        final String tipoEntidade = AcessoSistema.ENTIDADE_COR;
                        for (final PerfilCor perfilCor : lstPerfilCor) {
                            final String corCodigo = perfilCor.getCorrespondente().getCorCodigo();
                            updatePerfil(tipoEntidade, corCodigo, perCodigo, null, null, null, null, status, null, null, null, null, responsavel);
                        }
                    }

                    if ((lstPerfilSup != null) && !lstPerfilSup.isEmpty()) {
                        final String tipoEntidade = AcessoSistema.ENTIDADE_SUP;
                        for (final PerfilSup perfilSup : lstPerfilSup) {
                            final String cseCodigo = perfilSup.getConsignante().getCseCodigo();
                            updatePerfil(tipoEntidade, cseCodigo, perCodigo, null, null, null, null, status, null, null, null, null, responsavel);
                        }
                    }
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Remove restrição do usuário por perfil.
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */

    @Override
    public List<String> unidadesPermissaoEdtUsuario(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final List<String> unidades = new ArrayList<>();
            final List<UsuarioUnidade> usuUnidades = UsuarioUnidadeHome.listUniCodigosByUsuCodigo(usuCodigo);

            for (final UsuarioUnidade usu : usuUnidades) {
                unidades.add(usu.getUniCodigo());
            }
            return unidades;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void atribuirUnidadesUsuario(String usuCodigo, List<String> unidades, AcessoSistema responsavel) throws UsuarioControllerException {
        try {

            final List<UsuarioUnidade> usuUnidades = UsuarioUnidadeHome.listUniCodigosByUsuCodigo(usuCodigo);
            if ((usuUnidades != null) && !usuUnidades.isEmpty()) {
                UsuarioUnidadeHome.deleteByUsucodigo(usuCodigo);
            }

            for (final String unidade : unidades) {
                UsuarioUnidadeHome.create(usuCodigo, unidade);
            }
        } catch (CreateException | UpdateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void fixarCamposPesquisaAvancada(List<CampoUsuario> lstCampoUsuaio, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String usuCodigo = responsavel.getUsuCodigo();
            final List<CampoUsuario> usuario = CampoUsuarioHome.findByUsuario(usuCodigo);
            if (!usuario.isEmpty()) {
                lstCampoUsuaio.forEach(campoUsuario -> {
                    try {
                        CampoUsuarioHome.updateCauValor(usuCodigo, campoUsuario.getCauChave(), campoUsuario.getCauValor());
                    } catch (final UpdateException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                });
            } else {
                lstCampoUsuaio.forEach(campoUsuario -> {
                    try {
                        CampoUsuarioHome.create(usuCodigo, campoUsuario.getCauChave(), campoUsuario.getCauValor());
                    } catch (final CreateException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                });
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<CampoUsuario> buscarCamposPesquisaAvancada(AcessoSistema responsavel) throws UsuarioControllerException {
        List<CampoUsuario> usuario = null;
        try {
            usuario = CampoUsuarioHome.findByUsuario(responsavel.getUsuCodigo());

        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        return usuario;
    }

    @Override
    public List<TransferObject> listarUsuariosFuncaoEspecifica(String funCodigo, String tipoEntidade, String codigoEndidade, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuariosFuncaoEspecificaQuery lstUsuarios = new ListaUsuariosFuncaoEspecificaQuery();
            lstUsuarios.tipo = tipoEntidade;
            lstUsuarios.funCodigo = funCodigo;
            lstUsuarios.codigoEntidade = codigoEndidade;

            return lstUsuarios.executarDTO();
        } catch (final HQueryException e) {
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
    }

    @Override
    public boolean findEmailExistenteCsaCseOrgCor(String emailUsuario, String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            List<TransferObject> listUsuEmailsRepeat = null;
            final FindEmailUsuarioRepeatQuery usuEmailRepeat = new FindEmailUsuarioRepeatQuery();
            boolean breakOperation = false;

            usuEmailRepeat.emailUsuario = emailUsuario;
            listUsuEmailsRepeat = usuEmailRepeat.executarDTO();

            for (final TransferObject usu : listUsuEmailsRepeat) {
                if ((!TextHelper.isNull(usu.getAttribute(Columns.UCE_CSE_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO))) || (!TextHelper.isNull(usu.getAttribute(Columns.UCA_CSA_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO)))) {
                    breakOperation = true;
                    break;
                } else if ((!TextHelper.isNull(usu.getAttribute(Columns.UCO_COR_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO))) || (!TextHelper.isNull(usu.getAttribute(Columns.UOR_ORG_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO)))) {
                    breakOperation = true;
                    break;
                } else if ((!TextHelper.isNull(usu.getAttribute(Columns.USP_CSE_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO))) || (!TextHelper.isNull(usu.getAttribute(Columns.USE_SER_CODIGO)) && !usuCodigo.equals(usu.getAttribute(Columns.USU_CODIGO)))) {
                    breakOperation = false;
                }
            }

            return breakOperation;
        } catch (final HQueryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String consultarEmailServidor(boolean enviaEmail, String serCpf, String serEmail, String modoEntrega, AcessoSistema responsavel) throws UsuarioControllerException {
        String email = null;
        final String consultarEmailExternoClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_BUSCA_EMAIL_SERVIDOR_API_EXTERNA, responsavel);

        //DESENV-21344
        if (enviaEmail && !TextHelper.isNull(consultarEmailExternoClassName) && (!CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA.equals(modoEntrega))) {
            try {
                final ConsultarEmailExternoServidor consultarEmailExternoServidor = ConsultarEmailExternoServidorFactory.getClasseConsultarEmailExternoServidor(consultarEmailExternoClassName);
                final CustomTransferObject resultadoConsultaAPIExterna = consultarEmailExternoServidor.consultarEmailExternoServidor(serCpf);

                if (HttpStatus.OK.equals(resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_STATUS.getChave()))) {
                    email = (String) resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_SUCCESS_DATA.getChave());
                } else if (!TextHelper.isNull(serEmail)) {
                    email = serEmail;
                } else {
                    throw UsuarioControllerException.byMessage((String) resultadoConsultaAPIExterna.getAttribute(ParamEmailExternoServidorEnum.RESULT_ERROR_DATA.getChave()));
                }
            } catch (final UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw UsuarioControllerException.byMessage(ex.getMessage());
            } catch (final ZetraException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new UsuarioControllerException("mensagem.erro.email.enviar", responsavel);
            }
        } else {
            email = serEmail;
        }

        return email;
    }

    @Override
    public Perfil findPerfilByUsuCodigo(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final String perCodigo = findUsuarioPerfil(usuCodigo, responsavel);
            if (perCodigo != null) {
                return PerfilHome.findByPrimaryKey(perCodigo);
            } else {
                throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException("mensagem.erro.arg0.nenhum.perfil.encontrado", responsavel, "");
        }
    }

    @Override
    public void removeRestricaoUsuarioPerfil(String perCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        try {

            final List<PerfilUsuario> lstUsuariosPerfil = PerfilUsuarioHome.findByPerfil(perCodigo);

            for (final PerfilUsuario usuarioPerfil : lstUsuariosPerfil) {

                final UsuarioTransferObject usuario = findUsuario(new UsuarioTransferObject(usuarioPerfil.getUsuCodigo()), AcessoSistema.ENTIDADE_USU, responsavel);
                usuario.setUsuIpAcesso(null);
                usuario.setUsuDDNSAcesso(null);
                updateUsuario(usuario, null, responsavel);

                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.USUARIO, Log.UPDATE, Log.LOG_INFORMACAO);
                logDelegate.setUsuario(responsavel.getUsuCodigo());
                logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.remocao.restricao.usuario.perfil", responsavel));
                logDelegate.write();

            }

        } catch (LogControllerException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listUsuariosAtivosComEmail(AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ListaUsuarioAtivoComEmailQuery query = new ListaUsuarioAtivoComEmailQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }

    @Override
    public void enviarNotificacaoPrazoExpiracaoSenha(String usuNome, String usuEmail, Integer qtdeDiasExpiracaoSenha, AcessoSistema responsavel) {
        try {
            EnviaEmailHelper.notificarUsuPrazoExpiracaoSenha(usuNome, usuEmail, qtdeDiasExpiracaoSenha, responsavel);
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.enviar", responsavel));
        }
    }

    @Override
    public boolean usuarioPossuiPermissaoAutoDesbloqueio(TransferObject usuarioTO, String tipoEntidade, AcessoSistema responsavel) {

        boolean usuarioPossuiPermissao = true;

        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
            if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG, CodedValues.TPC_SIM, responsavel)) {
                usuarioPossuiPermissao = false;
            }
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            
            if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, CodedValues.TPC_SIM, responsavel)) {
                usuarioPossuiPermissao = false;
            } else {
                usuarioPossuiPermissao = verificarSePerfilCsaPermiteAutoDesbloqueio(usuarioTO, responsavel);
            }
            
        } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            if (!ParamSist.paramEquals(CodedValues.TPC_AUTO_DESBLOQUEIO_USUARIO_SUP, CodedValues.TPC_SIM, responsavel)) {
                usuarioPossuiPermissao = false;
            }
        }

        return usuarioPossuiPermissao;
        
    }

    private boolean verificarSePerfilCsaPermiteAutoDesbloqueio(TransferObject usuarioTO, AcessoSistema responsavel) {
        
        try {

            boolean perfilPossuiPermissao = true;

            String tpaAutoDesbloqueio = buscarValorParametroConsignatariaAutoDesbloqueio(usuarioTO, responsavel);

            if (!TextHelper.isNull(tpaAutoDesbloqueio) && CodedValues.TPA_SIM.equals(tpaAutoDesbloqueio)) {

                String perCodigo = (String) usuarioTO.getAttribute(Columns.UPE_PER_CODIGO);

                if (perCodigo != null) {

                    Perfil perfil = findPerfil(perCodigo, responsavel);

                    if (perfil != null) {
                        
                        if (!TextHelper.isNull(perfil.getPerAutoDesbloqueio())) {
                            perfilPossuiPermissao = CodedValues.TPA_SIM.equals(perfil.getPerAutoDesbloqueio());
                        } else {
                            perfilPossuiPermissao = false;
                        }

                    }

                } 

            }

            return perfilPossuiPermissao;

        } catch (UsuarioControllerException | ParametroControllerException | CorrespondenteControllerException e) {
            LOG.error(e.getMessage(), e);
            return true;
        }

    }

    private String buscarValorParametroConsignatariaAutoDesbloqueio(TransferObject usuarioTO, AcessoSistema responsavel)
            throws CorrespondenteControllerException, ParametroControllerException {
        
        String csaCodigo = null;

        if (!TextHelper.isNull(usuarioTO.getAttribute(Columns.UCA_CSA_CODIGO))) {
            csaCodigo = (String) usuarioTO.getAttribute(Columns.UCA_CSA_CODIGO);
        } else if (!TextHelper.isNull(usuarioTO.getAttribute(Columns.UCO_COR_CODIGO))) {
            Correspondente cor = correspondenteController.findCorrespondenteByPrimaryKey((String) usuarioTO.getAttribute(Columns.UCO_COR_CODIGO), responsavel);
            csaCodigo = cor.getCsaCodigo();
        }
        
        String tpaAutoDesbloqueio = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, responsavel);

        return tpaAutoDesbloqueio;
        
    }
}
