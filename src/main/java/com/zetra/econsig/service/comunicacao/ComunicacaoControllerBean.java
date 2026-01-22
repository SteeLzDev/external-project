package com.zetra.econsig.service.comunicacao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.comunicacao.ProcessaEmailAlertaNovaComunicacao;
import com.zetra.econsig.job.process.comunicacao.ProcessaEnvioEmailCmnMsgPendente;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Comunicacao;
import com.zetra.econsig.persistence.entity.ComunicacaoCsaHome;
import com.zetra.econsig.persistence.entity.ComunicacaoCseHome;
import com.zetra.econsig.persistence.entity.ComunicacaoHome;
import com.zetra.econsig.persistence.entity.ComunicacaoOrgHome;
import com.zetra.econsig.persistence.entity.ComunicacaoPermitida;
import com.zetra.econsig.persistence.entity.ComunicacaoPermitidaHome;
import com.zetra.econsig.persistence.entity.ComunicacaoSerHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.LeituraComunicacaoUsuario;
import com.zetra.econsig.persistence.entity.LeituraComunicacaoUsuarioHome;
import com.zetra.econsig.persistence.entity.LeituraComunicacaoUsuarioId;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacaoQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasCsaQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasCseQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasOrgQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListaComunicacoesNaoLidasSerQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListaUsuariosComunicacoesPendentesCsaQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListarAssuntoComunicacaoQuery;
import com.zetra.econsig.persistence.query.comunicacao.ListarComunicacoesQuery;
import com.zetra.econsig.persistence.query.comunicacao.ObtemResponsavelComunicacaoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaCmnPendenteAlemPrazoQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaConvenioNseCodigoQuery;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ComunicacaoControllerBean</p>
 * <p>Description: session façade do módulo de Comunicação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ComunicacaoControllerBean implements ComunicacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ComunicacaoControllerBean.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private EditarAnexoComunicacaoController editarAnexoComunicacaoController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public int countComunicacoes(TransferObject criterio, AcessoSistema responsavel) throws ZetraException {
        final ListarComunicacoesQuery lstComunicacoes = new ListarComunicacoesQuery();

        lstComunicacoes.count = true;
        lstComunicacoes.responsavel = responsavel;
        lstComunicacoes.setCriterios(criterio);

        try {
            return lstComunicacoes.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);

        }
    }

    @Override
    public List<TransferObject> listaAssuntoComunicacao(AcessoSistema responsavel) throws ZetraException {
        final ListarAssuntoComunicacaoQuery lstAssuntoComunicacao = new ListarAssuntoComunicacaoQuery();

        try {
            return lstAssuntoComunicacao.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    @Override
    public List<TransferObject> listComunicacoes(TransferObject criterio, AcessoSistema responsavel) throws ZetraException {
        return listComunicacoes(criterio, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> listComunicacoes(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ZetraException {
        return listComunicacoes(criterio, true, offset, count, responsavel);
    }

    @Override
    public List<TransferObject> listComunicacoes(TransferObject criterio, boolean ordenacaoDesc, int offset, int count, AcessoSistema responsavel) throws ZetraException {
        final ListarComunicacoesQuery lstComunicacoes = new ListarComunicacoesQuery();

        if (offset != -1) {
            lstComunicacoes.firstResult = offset;
        }

        if (count != -1) {
            lstComunicacoes.maxResults = count;
        }

        lstComunicacoes.setCriterios(criterio);
        lstComunicacoes.responsavel = responsavel;

        try {
            return lstComunicacoes.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    private TransferObject buscaComunicacao(String cmnCodigo, AcessoSistema responsavel) throws ZetraException {
        final ListaComunicacaoQuery query = new ListaComunicacaoQuery();
        query.cmnCodigo = cmnCodigo;
        query.responsavel = responsavel;

        try {
            final List<TransferObject> lista = query.executarDTO();
            if ((lista == null) || lista.isEmpty()) {
                throw new ZetraException("mensagem.erro.comunicacao.nao.encontrada", responsavel);
            }

            return lista.get(0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    @Override
    public String createComunicacao(TransferObject comunicacao, AcessoSistema responsavel) throws ZetraException {
        return this.createComunicacao(comunicacao, null, responsavel);
    }

    @Override
    public String createComunicacao(TransferObject comunicacao, File anexoComunicacao, AcessoSistema responsavel) throws ZetraException {
        String cmnCodigo = null;
        final List<String> cmnCodigos = new ArrayList<>();
        File dirFinal = null;
        final boolean enviaCopiaComunicacaoUsuario = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_COMUNICACAO_CRIADA_EMAIL_USUARIO, responsavel);

        try {
            // Verifica se a categoria do assunto esta presente
            final String cmnAscCodigo = comunicacao.getAttribute(Columns.CMN_ASC_CODIGO).toString();
            final String adeCodigo = (String) comunicacao.getAttribute(Columns.CMN_ADE_CODIGO);
            if (TextHelper.isNull(cmnAscCodigo)) {
                throw new ZetraException("mensagem.erro.categoria.assunto.comunicacao.nao.informado", responsavel);
            }

            if (TextHelper.isNull(comunicacao.getAttribute(Columns.PAP_CODIGO).toString())) {
                throw new ZetraException("mensagem.erro.papel.destino.comunicacao.nao.informado", responsavel);
            }

            if (TextHelper.isNull(comunicacao.getAttribute(Columns.CSE_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.ORG_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.CSA_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.SER_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.RSE_CODIGO))) {
                throw new ZetraException("mensagem.erro.entidade.destino.comunicacao.nao.informada", responsavel);
            }

            final String cmnTexto = validarTextoComunicacao(comunicacao, responsavel);
            final String papCodigoDestinatario = comunicacao.getAttribute(Columns.PAP_CODIGO).toString();

            final boolean cmnPendencia = definirComunicacaoPendente(papCodigoDestinatario, responsavel);
            final boolean enviarAlertaEmail = ((comunicacao.getAttribute(Columns.CMN_ALERTA_EMAIL) != null) && "1".equals(comunicacao.getAttribute(Columns.CMN_ALERTA_EMAIL).toString()));
            final boolean enviarCopiaEmail = ((comunicacao.getAttribute(Columns.CMN_COPIA_EMAIL_SMS) != null) && "1".equals(comunicacao.getAttribute(Columns.CMN_COPIA_EMAIL_SMS).toString()));
            final String ipAcesso = (String) comunicacao.getAttribute(Columns.CMN_IP_ACESSO);

            Comunicacao cmnBean = null;

            final boolean csaBuscaSer = comunicacao.getAttribute(CodedValues.ENVIA_CSA_CONTRATOS_SERVIDOR) != null;

            // Insere a comunicação de acordo com a entidade remetente e destino
            if(!csaBuscaSer && !CodedValues.PAP_SERVIDOR.equals(papCodigoDestinatario)) {
                cmnBean = ComunicacaoHome.create(responsavel.getUsuCodigo(), null, cmnPendencia, cmnTexto, null, ipAcesso, enviarAlertaEmail, enviarCopiaEmail, cmnAscCodigo, adeCodigo);
                cmnCodigo = cmnBean.getCmnCodigo();
                insereComunicacaoEntidade(cmnCodigo, papCodigoDestinatario, comunicacao, responsavel);
                cmnCodigos.add(cmnCodigo);
            }

            // Código da CSA de destino, ou NULL caso o destino seja outro tipo de entidade
            final String csaCodigo = (String) comunicacao.getAttribute(Columns.CSA_CODIGO);
            if (!TextHelper.isNull(csaCodigo) && !CodedValues.PAP_SERVIDOR.equals(papCodigoDestinatario)) {
                comunicaCsa(csaCodigo, comunicacao, responsavel);
            }

            final String orgCodigo = (String) comunicacao.getAttribute(Columns.ORG_CODIGO);
            final String cseCodigo = (String) comunicacao.getAttribute(Columns.CSE_CODIGO);

            if (CodedValues.PAP_SERVIDOR.equals(papCodigoDestinatario)) {
                final String verbaBuscaSer = comunicacao.getAttribute(CodedValues.ENVIA_CSA_CNV_CONTRATOS_SERVIDOR) != null ? (String) comunicacao.getAttribute(CodedValues.ENVIA_CSA_CNV_CONTRATOS_SERVIDOR) : null;
                if(responsavel.isCseSup() && csaBuscaSer) {
                    final List<TransferObject> servidores = servidorController.listaServidorPorCsaVerba(csaCodigo, verbaBuscaSer, responsavel);

                    for(final TransferObject servidor : servidores) {
                        final String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                        final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
                        final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);


                        comunicacao.setAttribute(Columns.SER_CODIGO, serCodigo);

                        comunicacao.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                        cmnBean = ComunicacaoHome.create(responsavel.getUsuCodigo(), null, cmnPendencia, cmnTexto, null, ipAcesso, enviarAlertaEmail, enviarCopiaEmail, cmnAscCodigo, adeCodigo);
                        cmnCodigo = cmnBean.getCmnCodigo();

                        cmnCodigos.add(cmnCodigo);
                        insereComunicacaoEntidade(cmnCodigo, papCodigoDestinatario, comunicacao, responsavel);

                        if (enviarCopiaEmail || enviaCopiaComunicacaoUsuario) {
                            try {
                                enviarCopiaComunicacaoEmailSMS(comunicacao, responsavel);
                            } catch (final ViewHelperException ex1) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.comunicacao.nao.criada.servidor.erro", responsavel, serNome));
                                LOG.error(ex1.getMessage(), ex1);
                            }
                        }
                    }
                } else if(!csaBuscaSer) {
                    cmnBean = ComunicacaoHome.create(responsavel.getUsuCodigo(), null, cmnPendencia, cmnTexto, null, ipAcesso, enviarAlertaEmail, enviarCopiaEmail, cmnAscCodigo, adeCodigo);
                    cmnCodigo = cmnBean.getCmnCodigo();
                    cmnCodigos.add(cmnCodigo);

                    insereComunicacaoEntidade(cmnCodigo, papCodigoDestinatario, comunicacao, responsavel);

                    if (enviarCopiaEmail || enviaCopiaComunicacaoUsuario) {
                        enviarCopiaComunicacaoEmailSMS(comunicacao, responsavel);
                    }
                }
            } else if (enviarCopiaEmail && CodedValues.PAP_ORGAO.equals(papCodigoDestinatario) && !TextHelper.isNull(orgCodigo)) {
                comunicaOrg(orgCodigo, comunicacao, responsavel);
            } else if (enviarCopiaEmail && CodedValues.PAP_CONSIGNANTE.equals(papCodigoDestinatario) && !TextHelper.isNull(cseCodigo)) {
                comunicaCse(cseCodigo, comunicacao, responsavel);
            }

            if (anexoComunicacao != null) {
                final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                final File dirComunicacao = new File(diretorioRaizArquivos + File.separatorChar + "comunicacao");

                if (dirComunicacao.exists() || dirComunicacao.mkdir()) {
                    for(final String cmnCodigoAnexo : cmnCodigos) {
                        dirFinal = new File(dirComunicacao.getCanonicalPath() + File.separatorChar + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd") + File.separatorChar + cmnCodigoAnexo);
                        if (dirFinal.exists() || ((dirFinal.getParentFile().exists() || dirFinal.getParentFile().mkdir()) && (dirFinal.exists() || dirFinal.mkdir()))) {
                            Files.copy(anexoComunicacao.toPath(), new File(dirFinal.getCanonicalPath() + File.separatorChar + anexoComunicacao.getName()).toPath());
                        }
                        editarAnexoComunicacaoController.createAnexoComunicacao(cmnCodigoAnexo, anexoComunicacao.getName(), anexoComunicacao.getName(), null, cmnCodigoAnexo, responsavel);
                    }
                }
            }

            if(csaBuscaSer) {
                final CustomTransferObject comunicacaoNova = new CustomTransferObject();
                for (final Entry<String, Object> entry : comunicacao.getAtributos().entrySet()) {
                    if(!CodedValues.ENVIA_CSA_CNV_CONTRATOS_SERVIDOR.equals(entry.getKey()) && !CodedValues.ENVIA_CSA_CONTRATOS_SERVIDOR.equals(entry.getKey())) {
                        comunicacaoNova.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
                comunicacao = comunicacaoNova;
            }
            for(final String cmnCodigoAtual : cmnCodigos) {
                final LogDelegate log = new LogDelegate(responsavel, Log.COMUNICACAO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setComunicacao(cmnCodigoAtual);
                log.setUsuario(responsavel.getUsuCodigo());
                log.getUpdatedFields(comunicacao.getAtributos(), null);
                log.write();
            }
        } catch (CreateException | IOException ex) {
            if (anexoComunicacao != null) {
                anexoComunicacao.delete();
            }

            try {
                if ((dirFinal != null) && dirFinal.exists()) {
                    FileHelper.deleteDir(dirFinal.getCanonicalPath());
                }
            } catch (final IOException ex1) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel, ex1.getMessage()));
            }

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.comunicacao.nao.criada", responsavel, ex.getMessage()));
            throw new ZetraException("mensagem.erro.comunicacao.nao.criada", responsavel, ex, ex.getMessage());
        }

        if (enviaCopiaComunicacaoUsuario) {
            enviaCopiaUsuEmail(comunicacao, responsavel);
        }
        return cmnCodigo;
    }

    @Override
    public List<String> createComunicacaoNseCodigo(TransferObject comunicacao, AcessoSistema responsavel) throws ZetraException {
        return this.createComunicacaoNseCodigo(comunicacao, null, responsavel);
    }

    @Override
    public List<String> createComunicacaoNseCodigo(TransferObject comunicacao, File anexoComunicacao, AcessoSistema responsavel) throws ZetraException {
        final List<String> cmnCodigos = new ArrayList<>();
        String cmnCodigo;
        File dirFinal = null;
        final List<File> dirFinals = new ArrayList<>();
        final boolean enviaCopiaComunicacaoUsuario = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_COMUNICACAO_CRIADA_EMAIL_USUARIO, responsavel);

        try {
            // Verifica se a categoria do assunto esta presente
            final String cmnAscCodigo = comunicacao.getAttribute(Columns.CMN_ASC_CODIGO).toString();
            final String adeCodigo = (String) comunicacao.getAttribute(Columns.CMN_ADE_CODIGO);
            if (TextHelper.isNull(cmnAscCodigo)) {
                throw new ZetraException("mensagem.erro.categoria.assunto.comunicacao.nao.informado", responsavel);
            }

            if (TextHelper.isNull(comunicacao.getAttribute(Columns.PAP_CODIGO).toString())) {
                throw new ZetraException("mensagem.erro.papel.destino.comunicacao.nao.informado", responsavel);
            }

            if (TextHelper.isNull(comunicacao.getAttribute(Columns.CSE_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.ORG_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.CSA_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.NSE_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.SER_CODIGO)) &&
                    TextHelper.isNull(comunicacao.getAttribute(Columns.RSE_CODIGO))) {
                throw new ZetraException("mensagem.erro.entidade.destino.comunicacao.nao.informada", responsavel);
            }

            final String cmnTexto = validarTextoComunicacao(comunicacao, responsavel);
            final String papCodigoDestinatario = comunicacao.getAttribute(Columns.PAP_CODIGO).toString();

            final boolean cmnPendencia = definirComunicacaoPendente(papCodigoDestinatario, responsavel);
            final boolean enviarAlertaEmail = ((comunicacao.getAttribute(Columns.CMN_ALERTA_EMAIL) != null) && "1".equals(comunicacao.getAttribute(Columns.CMN_ALERTA_EMAIL)));
            final boolean enviarCopiaEmail = ((comunicacao.getAttribute(Columns.CMN_COPIA_EMAIL_SMS) != null) && "1".equals(comunicacao.getAttribute(Columns.CMN_COPIA_EMAIL_SMS)));
            final String ipAcesso = (String) comunicacao.getAttribute(Columns.CMN_IP_ACESSO);
            final String nseCodigo = (String) comunicacao.getAttribute(Columns.NSE_CODIGO);

            final List<String> csaCodigos = listaCsaCodigos(nseCodigo, responsavel);
            for (final String codigo : csaCodigos) {

                // Cria uma comunicaçao para cada CSA_CODIGO
                final Comunicacao cmnBean = ComunicacaoHome.create(responsavel.getUsuCodigo(), null, cmnPendencia, cmnTexto, null, ipAcesso, enviarAlertaEmail, enviarCopiaEmail, cmnAscCodigo, adeCodigo);
                cmnCodigo = cmnBean.getCmnCodigo();
                cmnCodigos.add(cmnCodigo);

                //Update CSA_CODIGO na comunicacao para utilizaçao nos demais processos.
                comunicacao.setAttribute(Columns.CSA_CODIGO, codigo);

                insereComunicacaoEntidade(cmnCodigo, papCodigoDestinatario, comunicacao, responsavel);
                comunicaCsa(codigo, comunicacao, responsavel);

                if ((enviarCopiaEmail || enviaCopiaComunicacaoUsuario) && CodedValues.PAP_SERVIDOR.equals(papCodigoDestinatario)) {
                    enviarCopiaComunicacaoEmailSMS(comunicacao, responsavel);
                }

                if (anexoComunicacao != null) {
                    final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                    final File dirComunicacao = new File(diretorioRaizArquivos + File.separatorChar + "comunicacao");

                    if (dirComunicacao.exists() || dirComunicacao.mkdir()) {
                        dirFinal = new File(dirComunicacao.getCanonicalPath() + File.separatorChar + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd") + File.separatorChar + cmnCodigo);
                        if (dirFinal.exists() || ((dirFinal.getParentFile().exists() || dirFinal.getParentFile().mkdir()) && (dirFinal.exists() || dirFinal.mkdir()))) {
                            dirFinals.add(dirFinal);
                            Files.copy(anexoComunicacao.toPath(), new File(dirFinal.getCanonicalPath() + File.separatorChar + anexoComunicacao.getName()).toPath());
                        }

                        editarAnexoComunicacaoController.createAnexoComunicacao(cmnCodigo, anexoComunicacao.getName(), anexoComunicacao.getName(), null, cmnCodigo, responsavel);
                    }
                }

                final LogDelegate log = new LogDelegate(responsavel, Log.COMUNICACAO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setComunicacao(cmnCodigo);
                log.setUsuario(responsavel.getUsuCodigo());
                log.getUpdatedFields(comunicacao.getAtributos(), null);
                log.write();
            }
        } catch (CreateException | IOException ex) {
            if (anexoComunicacao != null) {
                anexoComunicacao.delete();
            }

            if (!dirFinals.isEmpty()) {
                for (final File finals: dirFinals) {
                    if ((finals != null) && finals.exists()) {
                        try {
                            FileHelper.deleteDir(finals.getCanonicalPath());
                        } catch (final IOException ex1) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel, ex1.getMessage()));
                        }
                    }
                }
            }

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.comunicacao.nao.criada", responsavel, ex.getMessage()));
            throw new ZetraException("mensagem.erro.comunicacao.nao.criada", responsavel, ex, ex.getMessage());
        }

        if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_COMUNICACAO_CRIADA_EMAIL_USUARIO, responsavel)) {
            enviaCopiaUsuEmail(comunicacao, responsavel);
        }

        return cmnCodigos;
    }

    private String validarTextoComunicacao(TransferObject comunicacao, AcessoSistema responsavel) throws ZetraException {
        String cmnTexto = Jsoup.parse((String) comunicacao.getAttribute(Columns.CMN_TEXTO)).text();

        // Verifica se a mensagem foi preenchida
        if (TextHelper.isNull(cmnTexto)) {
            throw new ZetraException("mensagem.erro.texto.comunicacao.nao.informado", responsavel);
        }
        cmnTexto = cmnTexto.trim();

        final String msgSemQuebraDeLinha = cmnTexto.replaceAll("\\r\\n|\\r|\\n", " ");

        // Verifica quantidade de caracteres informados na mensagem
        if (msgSemQuebraDeLinha.length() < 10) {
            throw new ZetraException("mensagem.erro.comunicacao.texto.minimo", responsavel);
        }

        // Verifica se existe pelo menos uma letra na mensagem
        final String regex = "^.*[a-zA-Z]+.*$";
        if (!msgSemQuebraDeLinha.matches(regex)) {
            throw new ZetraException("mensagem.erro.comunicacao.texto.invalido", responsavel);
        }

        int tamMaxMsg = 0;
        try {
            tamMaxMsg = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_COMUNICACAO, responsavel).toString());
        } catch (final Exception ex) {
            tamMaxMsg = 0;
        }
        // mensagem não pode ser maior do que o definido no parâmetro de sistema 354. Valor 0 deste significa que não há limite
        // para o tamanho da mensagem.
        if ((tamMaxMsg > 0) && (cmnTexto.length() > tamMaxMsg)) {
            throw new ZetraException("mensagem.erro.comunicacao.texto.maximo", responsavel);
        }

        return cmnTexto;
    }

    private void enviarCopiaComunicacaoEmailSMS(TransferObject comunicacao, AcessoSistema responsavel) throws ViewHelperException {
        String serEmail = null;
        String serCelular = null;

        try {
            final Servidor servidor = ServidorHome.findByPrimaryKey((String) comunicacao.getAttribute(Columns.SER_CODIGO));
            serEmail = servidor.getSerEmail();
            serCelular = servidor.getSerCelular();
        } catch (final FindException ex) {
            throw new ViewHelperException("mensagem.nenhumServidorEncontrado", responsavel, ex, ex.getMessage());
        }

        if (!TextHelper.isNull(serEmail)) {
            final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), serEmail, comunicacao, responsavel);
            emailNovaCmn.start();
        }
        if (!TextHelper.isNull(serCelular)) {
            try {
                // Credenciais para envio do SMS
                final String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                final String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                final String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                if (TextHelper.isNull(accountSid) || TextHelper.isNull(authToken) || TextHelper.isNull(fromNumber)) {
                    LOG.warn("Necessário habilitar os parâmetros de sistema " + CodedValues.TPC_SID_CONTA_SMS + ", " + CodedValues.TPC_TOKEN_AUTENTICACAO_SMS + ", " + CodedValues.TPC_NUMERO_REMETENTE_SMS + " para envio de SMS.");

                } else {
                    final String celularDestinatario = LocaleHelper.formataCelular(serCelular);
                    if (!TextHelper.isNull(celularDestinatario)) {
                        String corpo = (String) comunicacao.getAttribute(Columns.CMN_TEXTO);

                        if (corpo.length() > 100) {
                            corpo = corpo.substring(0, 50) + ApplicationResourcesHelper.getMessage("mensagem.visualizar.mensagem.completa.sms", responsavel);
                        }

                        new SMSHelper(accountSid, authToken, fromNumber).send(celularDestinatario, TextHelper.removeAccent(corpo));
                    }
                }
            } catch (final ZetraException ex) {
                throw new ViewHelperException("mensagem.erro.sms.enviar", responsavel, ex);
            }
        }
    }

    /**
     * Insere a comunicação de acordo com a entidade para o remetente e para o destino.
     *
     * @param cmnCodigo Código da comunicação.
     * @param papCodigoDestinatario Tipo do destinatario
     * @param comunicacao Dados da comunicação.
     * @param responsavel Responsável pela comunicação.
     * @throws CreateException
     */
    private void insereComunicacaoEntidade(String cmnCodigo, String papCodigoDestinatario, TransferObject comunicacao, AcessoSistema responsavel) throws CreateException {
        // Destinatário
        if (CodedValues.PAP_CONSIGNANTE.equals(papCodigoDestinatario)) {
            ComunicacaoCseHome.create(cmnCodigo, (String) comunicacao.getAttribute(Columns.CSE_CODIGO), CodedValues.TPC_SIM);
        } else if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigoDestinatario)) {
            ComunicacaoCsaHome.create(cmnCodigo, (String) comunicacao.getAttribute(Columns.CSA_CODIGO), CodedValues.TPC_SIM);
        } else if (CodedValues.PAP_ORGAO.equals(papCodigoDestinatario)) {
            ComunicacaoOrgHome.create(cmnCodigo, (String) comunicacao.getAttribute(Columns.ORG_CODIGO), CodedValues.TPC_SIM);
        } else if (CodedValues.PAP_SERVIDOR.equals(papCodigoDestinatario)) {
            ComunicacaoSerHome.create(cmnCodigo, (String) comunicacao.getAttribute(Columns.SER_CODIGO), (String) comunicacao.getAttribute(Columns.RSE_CODIGO), CodedValues.TPC_SIM);
        }

        // Remetente
        if (responsavel.isCse()) {
            ComunicacaoCseHome.create(cmnCodigo, responsavel.getCodigoEntidade(), CodedValues.TPC_NAO);
        } else if (responsavel.isCsa()) {
            ComunicacaoCsaHome.create(cmnCodigo, responsavel.getCodigoEntidade(), CodedValues.TPC_NAO);
        } else if (responsavel.isOrg()) {
            ComunicacaoOrgHome.create(cmnCodigo, responsavel.getCodigoEntidade(), CodedValues.TPC_NAO);
        } else if (responsavel.isSer()) {
            ComunicacaoSerHome.create(cmnCodigo, responsavel.getSerCodigo(), responsavel.getRseCodigo(), CodedValues.TPC_NAO);
        }
    }

    /**
     * se o sistema estiver configurado para bloquear CSA segundo número máximo de comunicações para servidores distintos pendentes
     * @param csaCodigo consignatária alvo da comunicação
     * @param responsavel
     * @throws HQueryException
     * @throws ConsignatariaControllerException
     */
    private void verificaBloqueioNumCmnPendentes(String csaCodigo, AcessoSistema responsavel) throws HQueryException, FindException, ConsignatariaControllerException {
        final Consignataria csa = ConsignatariaHome.findByPrimaryKey(csaCodigo);
        if ((csa.getCsaAtivo().shortValue() == CodedValues.STS_ATIVO.shortValue()) && temNumMaxUsuComCmnPendentes(csaCodigo, responsavel)) {
            final ConsignatariaTransferObject csaTO = new ConsignatariaTransferObject(csaCodigo);
            final List<TransferObject> csaList = new ArrayList<>();
            csaList.add(csaTO);

            consignatariaController.bloquearConsignatariasContratos(csaList, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.maximo.comunicacoes.pendentes", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, AcessoSistema.getAcessoUsuarioSistema());

            // Inclui as ocorrências específicas
            if (ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel) && (csaList.size() > 0)) {
                final List<String> csaCodigoList = new ArrayList<>();
                csaCodigoList.add(csaCodigo);
                consignatariaController.incluirOcorrenciaConsignatarias(csaCodigoList, CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.possui.comunicacoes.pendentes", responsavel), responsavel);
            }
        }
    }

    /**
     * returna verdadeiro se o número de comunicações pendentes por servidores distintos para consignatária tiver alcançado
     * o número máximo permitido, definido pelo parâmetro de sistema 355
     * @param csaCodigo consignatária alvo da comunicação
     * @param responsavel
     * @return
     * @throws HQueryException
     * @throws ConsignatariaControllerException
     */
    private boolean temNumMaxUsuComCmnPendentes(String csaCodigo, AcessoSistema responsavel) throws HQueryException, ConsignatariaControllerException {
        int numMaxCmnPendente = 0;
        try {
            numMaxCmnPendente = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_NUM_MAX_CMN_PENDENTE_POR_CSA, responsavel).toString());
        } catch (final Exception ex) {
            numMaxCmnPendente = 0;
        }

        if (numMaxCmnPendente > 0) {
            // lista de usuários distintos que tem comunicação pendente com para a CSA.
            final ListaUsuariosComunicacoesPendentesCsaQuery usuQuery = new ListaUsuariosComunicacoesPendentesCsaQuery();
            usuQuery.csaCodigo = csaCodigo;

            final List<TransferObject> usuList = usuQuery.executarDTO();

            //bloqueia a consignatária se o número de comunicações pendentes é maior que o limite fixado pelo parâmetro 355
            if (!usuList.isEmpty() && (usuList.size() >= numMaxCmnPendente)) {
                return true;
            }
        }
        return false;
    }

    /**
     * gera uma comunicação de resposta a outra enviada pelo servidor, representado pelo parâmetro comnCodigoPai
     * @param comunicacao - dados da nova comunicação
     * @param cmnCodigoPai - código da comunicação para a qual está sendo gerada a resposta
     * @param responsavel - usuário logado responsável pela resposta
     * @return
     * @throws ZetraException
     */
    @Override
    public String geraComunicacaoResposta(TransferObject comunicacao, String cmnCodigoPai, AcessoSistema responsavel) throws ZetraException {
        if (TextHelper.isNull(cmnCodigoPai)) {
            throw new ZetraException("mensagem.erro.comunicacao.pai.nao.informada", responsavel);
        }

        // Busca o destinatário da comunicação pai
        final TransferObject destinatarios = buscaComunicacao(cmnCodigoPai, responsavel);
        final String csaCodigo = (String) destinatarios.getAttribute(Columns.CSA_CODIGO);

        final String papCodigoRemetente = destinatarios.getAttribute("PAP_CODIGO_REMETENTE").toString();
        final String papCodigoDestinatario = destinatarios.getAttribute("PAP_CODIGO_DESTINATARIO").toString();
        final String papCodigoRelacionamento = responsavel.getPapCodigo().equals(papCodigoRemetente) ? papCodigoDestinatario : papCodigoRemetente;

        String cmnCodigo = null;
        final String cmnTexto = validarTextoComunicacao(comunicacao, responsavel);
        final boolean cmnPendencia = definirComunicacaoPendente(papCodigoDestinatario, responsavel);

        try {
            final Comunicacao cmnPai = ComunicacaoHome.findByPrimaryKey(cmnCodigoPai);

            // Cria comunicação de resposta pendente ou não de acordo com as configurações do sistema e as entidades envolvidas
            final Comunicacao cmnBean = ComunicacaoHome.create(responsavel.getUsuCodigo(), cmnPai.getCmnNumero(),
                    cmnPendencia, cmnTexto,
                    (String) comunicacao.getAttribute(Columns.CMN_CODIGO_PAI),
                    (String) comunicacao.getAttribute(Columns.CMN_IP_ACESSO), false, cmnPai.getCmnCopiaEmailSms(), null, (String) comunicacao.getAttribute(Columns.CMN_ADE_CODIGO));
            cmnCodigo = cmnBean.getCmnCodigo();

            // Insere a comunicação de resposta de acordo com a entidade remetente e o responsável
            insereComunicacaoEntidade(cmnCodigo, papCodigoRelacionamento, destinatarios, responsavel);

            // Se a comunicação resposta será criada como pendente, então altera a comunicação pai para pendente
            if (cmnPendencia) {
                if (!cmnPai.getCmnPendencia()) {
                    cmnPai.setCmnPendencia(true);
                    AbstractEntityHome.update(cmnPai);
                }

                // Se a comunicação é destinada uma consignatária, verifica bloqueios por número de comunicações pendentes
                if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigoDestinatario) && !TextHelper.isNull(csaCodigo)) {
                    verificaBloqueioNumCmnPendentes(csaCodigo, responsavel);

                    if (responsavel.isSer()) {
                        // Verifica se tem configuração de número máximo de mensagens pendentes para a comunicação pai.
                        // Caso sim e tenha alcançado o limite, envia e-mail de alerta para o gestor.
                        int numMaxMsgPendenteCmn = 0;
                        try {
                            numMaxMsgPendenteCmn = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_MAX_MSG_PENDENTE_POR_CMN, responsavel).toString());
                        } catch (final Exception ex) {
                            numMaxMsgPendenteCmn = 0;
                        }

                        if (numMaxMsgPendenteCmn > 0) {
                            final Collection<Comunicacao> msgPendentes = ComunicacaoHome.findComunicacaoPendenteSerRemetenteCsaDestinatario(cmnPai.getCmnCodigo());
                            if ((msgPendentes != null) && (msgPendentes.size() >= numMaxMsgPendenteCmn)) {
                                final ProcessaEnvioEmailCmnMsgPendente sendMailMsgPendenteThread = new ProcessaEnvioEmailCmnMsgPendente(cmnPai.getCmnTexto(), cmnPai.getCmnData(), cmnPai.getCmnNumero(), responsavel.getUsuCodigo(), csaCodigo, responsavel);
                                sendMailMsgPendenteThread.start();
                            }
                        }
                    }
                }

            } else {
                // Se a comunicação de resposta não reabre a pendência, verifica pelo tipo de entidade
                // se pode marcar a pendência como resolvida, e caso sim, atualiza as comunicações filhas
                if (papCodigoDestinatario.equals(responsavel.getPapCodigo())) {
                    // Se a comunicação tem como destinatário o tipo de entidade do usuário, então pode marcar como resolvida
                    cmnPai.setCmnPendencia(false);
                    AbstractEntityHome.update(cmnPai);

                    // Atualiza as comunicações filhas pendentes como resolvidas
                    final Collection<Comunicacao> comunicoesPendentes = ComunicacaoHome.findByCmnCodigoPai(cmnCodigoPai, true);
                    for (final Comunicacao cmnPendente : comunicoesPendentes) {
                        if (!cmnPendente.getCmnCodigo().equals(cmnCodigo)) {
                            cmnPendente.setCmnPendencia(false);
                            AbstractEntityHome.update(cmnPendente);
                        }
                    }

                    // Se a comunicação pai foi dada como resolvida, então caso o destinatário seja CSA
                    // verifica se precisa desbloquear automaticamente
                    if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigoDestinatario) && !TextHelper.isNull(csaCodigo)) {
                        final Consignataria csaTarget = ConsignatariaHome.findByPrimaryKey(csaCodigo);

                        // se consignatária não estiver ativa
                        if (csaTarget.getCsaAtivo().shortValue() != CodedValues.STS_ATIVO.shortValue()) {
                            int numMaxCmnPendente = 0;
                            try {
                                numMaxCmnPendente = Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_NUM_MAX_CMN_PENDENTE_POR_CSA, responsavel).toString());
                            } catch (final Exception ex) {
                                numMaxCmnPendente = 0;
                            }

                            boolean podeValidarDesbloqAut = true;
                            if (numMaxCmnPendente > 0) {
                                // lista de usuários distintos que tem comunicação pendente com para a CSA.
                                final ListaUsuariosComunicacoesPendentesCsaQuery usuQuery = new ListaUsuariosComunicacoesPendentesCsaQuery();
                                usuQuery.csaCodigo = csaCodigo;

                                // Permite o desbloqueio apenas se não houver mais comunicações pendentes para o caso do parâmetro 355 for maior que 0.
                                podeValidarDesbloqAut = usuQuery.executarDTO().isEmpty();
                            }
                            if (podeValidarDesbloqAut) {
                                consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                            }
                        }
                    }
                }

                // Como é uma resposta de um outro usuário, envia email de alerta de que a cmn original foi respondida,
                // caso o usuário que gerou a comunicação original assim tenha solicitado.
                final boolean enviaCopiaComunicacaoUsuario = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_COMUNICACAO_CRIADA_EMAIL_USUARIO, responsavel);
                if ((cmnPai.getCmnAlertaEmail() || enviaCopiaComunicacaoUsuario) && !responsavel.getUsuCodigo().equals(cmnPai.getUsuario().getUsuCodigo())) {
                    final CustomTransferObject remetente = usuarioController.findTipoUsuarioByCodigo(cmnPai.getUsuario().getUsuCodigo(), responsavel);
                    String usuEmail = (String) remetente.getAttribute(Columns.USU_EMAIL);
                    // Se for servidor, o e-mail estará cadastrado na tb_servidor.ser_email
                    if (!TextHelper.isNull(remetente.getAttribute(Columns.USE_SER_CODIGO))) {
                        final Servidor servidor = ServidorHome.findByPrimaryKey((String) remetente.getAttribute(Columns.USE_SER_CODIGO));
                        usuEmail = servidor.getSerEmail();
                    }

                    if (!TextHelper.isNull(usuEmail)) {
                        final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), usuEmail, comunicacao, responsavel);
                        emailNovaCmn.start();
                    }

                    // Se a comunicação pai está informando que deve enviar cópia por email/sms ao servidor e a comunicação
                    // de resposta não é do servidor, então envia a comunicação por e-mail/sms
                    if ((cmnPai.getCmnCopiaEmailSms() || enviaCopiaComunicacaoUsuario) && !responsavel.isSer() && !TextHelper.isNull(comunicacao.getAttribute(Columns.SER_CODIGO))) {
                        enviarCopiaComunicacaoEmailSMS(comunicacao, responsavel);
                    }
                }
            }

            // Marca comunicação pai com não lida para demais usuários diferentes do usuário atual
            final List<LeituraComunicacaoUsuario> leiturasCmn = LeituraComunicacaoUsuarioHome.findByCmnCodigo(cmnCodigoPai);
            if ((leiturasCmn != null) && !leiturasCmn.isEmpty()) {
                for (final LeituraComunicacaoUsuario lcm : leiturasCmn) {
                    if (!lcm.getUsuCodigo().equals(responsavel.getUsuCodigo())) {
                        AbstractEntityHome.remove(lcm);
                    }
                }
            }

            // Verifica se CSA destinatária deve ser alertada via e-mail da criação desta comunicação.
            if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigoDestinatario) && !TextHelper.isNull(csaCodigo) && !responsavel.isCsa()) {
                final List<TransferObject> paramCsa = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_RECEBE_EMAIL_ALERTA_COMUNICACAO, null, null, null, responsavel);
                if ((paramCsa != null) && (paramCsa.size() > 0) && (paramCsa.get(0) != null)) {
                    final String emailAlertaNovaCmn = (String) paramCsa.get(0).getAttribute(Columns.PCS_VLR);

                    // Envia e-mail para o endereço especificado para esta consignatária no parâmetro de consignatária.
                    if (!TextHelper.isNull(emailAlertaNovaCmn)) {
                        final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), emailAlertaNovaCmn, comunicacao, responsavel);
                        emailNovaCmn.start();
                    }
                }
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.COMUNICACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setComunicacao(cmnCodigo);
            log.getUpdatedFields(comunicacao.getAtributos(), null);
            log.write();
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.comunicacao.nao.criada", responsavel, ex.getMessage());
        } catch (final FindException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.comunicacao.originaria.nao.encontrada", responsavel);
        } catch (final UpdateException uex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(uex.getMessage(), uex);
            throw new ZetraException("mensagem.erro.comunicacao.nao.atualizada", responsavel);
        } catch (final RemoveException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error("Erro ao atualizar comunicação.", e);
        }
        return cmnCodigo;
    }

    @Override
    public Comunicacao findComunicacaoByPK(String cmnCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            return ComunicacaoHome.findByPrimaryKey(cmnCodigo);
        } catch (final FindException e) {
            throw new ZetraException("mensagem.erro.comunicacao.nao.encontrada", responsavel);
        }
    }

    /**
     * gera entrada de leitura de uma comunicação pelo usuário de csa e/ou gestor
     * @param cmnCodigo
     * @param responsavel
     * @throws ZetraException
     */
    @Override
    public void logLeituraComunicacao(String cmnCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            final LeituraComunicacaoUsuario leituraNoBanco = LeituraComunicacaoUsuarioHome.findLeituraComunicacaoUsuByPK(new LeituraComunicacaoUsuarioId(cmnCodigo, responsavel.getUsuCodigo()));
            if (leituraNoBanco == null) {
                LeituraComunicacaoUsuarioHome.create(cmnCodigo, responsavel.getUsuCodigo());

                final Map<String, Object> mapAtributos = new HashMap<>();
                mapAtributos.put(Columns.CMN_CODIGO, cmnCodigo);
                mapAtributos.put(Columns.USU_CODIGO, responsavel.getCodigoEntidade());
                final LogDelegate log = new LogDelegate(responsavel, Log.LEITURA_COMUNICACAO, Log.CREATE, Log.LOG_INFORMACAO);
                log.setComunicacao(cmnCodigo);
                log.setUsuario(responsavel.getUsuCodigo());
                log.getUpdatedFields(mapAtributos, null);
                log.write();
            }
        } catch (final CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException("mensagem.erro.criar.log.leitura.comunicacao", responsavel);
        } catch (final FindException e) {
            throw new ZetraException("mensagem.erro.criar.log.leitura.comunicacao", responsavel);
        }
    }

    @Override
    public Usuario findResponsavelCmn(String cmnCodigo, AcessoSistema responsavel) throws ZetraException {
        final ObtemResponsavelComunicacaoQuery ownerSQL = new ObtemResponsavelComunicacaoQuery();
        ownerSQL.cmnCodigo = cmnCodigo;
        try {
            final List<TransferObject> ownerList = ownerSQL.executarDTO();
            if (!ownerList.isEmpty()) {
                final TransferObject usuTO = ownerList.get(0);
                final Usuario usuario = new Usuario();
                usuario.setUsuCodigo((String) usuTO.getAttribute(Columns.USU_CODIGO));
                usuario.setUsuNome((String) usuTO.getAttribute(Columns.USU_NOME));
                usuario.setUsuLogin((String) usuTO.getAttribute(Columns.USU_LOGIN));

                return usuario;
            } else {
                return null;
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);

        }
    }

    @Override
    public void bloqueiaCsaPorCmnPendente(AcessoSistema responsavel) throws ZetraException {
        final int diasParaBloqueioCmnEnviadaSer = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER, responsavel);
        final int diasParaBloqueioCmnEnviadaCseOrg = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG, responsavel);

        // quantidade de dias = 0 significa que não há regra de bloqueio por comunicação pendente
        if ((diasParaBloqueioCmnEnviadaSer == 0) && (diasParaBloqueioCmnEnviadaCseOrg == 0)) {
            return;
        }

        try {
            final ListaConsignatariaCmnPendenteAlemPrazoQuery pendenciaQuery = new ListaConsignatariaCmnPendenteAlemPrazoQuery();
            pendenciaQuery.diasParaBloqueioCmnSer = diasParaBloqueioCmnEnviadaSer;
            pendenciaQuery.diasParaBloqueioCmnCseOrg = diasParaBloqueioCmnEnviadaCseOrg;

            final List<TransferObject> csaList = pendenciaQuery.executarDTO();
            if (!csaList.isEmpty()) {
                // Extrai os códigos das consignatárias da lista que devem ser bloqueadas
                final List<String> csaCodigos = new ArrayList<>();

                for (final TransferObject csaTO : csaList) {
                    final int qtdPendenciaSer = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_SER").toString());
                    final int qtdPendenciaCse = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_CSE").toString());
                    final int qtdPendenciaOrg = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_ORG").toString());

                    if (((diasParaBloqueioCmnEnviadaSer > 0) && (qtdPendenciaSer > 0)) ||
                            ((diasParaBloqueioCmnEnviadaCseOrg > 0) && ((qtdPendenciaCse > 0) || (qtdPendenciaOrg > 0)))) {
                        csaCodigos.add((String) csaTO.getAttribute(Columns.CSA_CODIGO));
                    }
                }

                if (!csaCodigos.isEmpty()) {
                    // Executa o bloqueio das consignatárias
                    consignatariaController.bloquearConsignatarias(csaCodigos, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.comunicacoes.pendentes.expiradas", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);

                    // incluí ocorrência específica de comunicação pendente caso tenha desbloqueio automático configurado no sistema.
                    if (ParamSist.paramEquals(CodedValues.TPC_DESBLOQ_AUT_CSA_CMN_SEM_RESPOSTA, CodedValues.TPC_SIM, responsavel)) {
                        consignatariaController.incluirOcorrenciaConsignatarias(csaCodigos, CodedValues.TOC_CONSIGNATARIA_COM_CMN_PENDENTE, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.possui.comunicacoes.pendentes", responsavel), responsavel);
                    }
                }
            }
        } catch (final HQueryException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean temComunicacoesParaBloqueioCsa(String csaCodigo, AcessoSistema responsavel) throws ZetraException {
        final int diasParaBloqueioCmnEnviadaSer = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER, responsavel);
        final int diasParaBloqueioCmnEnviadaCseOrg = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG, responsavel);

        // quantidade de dias = 0 significa que não há regra de bloqueio por comunicação pendente
        if ((diasParaBloqueioCmnEnviadaSer == 0) && (diasParaBloqueioCmnEnviadaCseOrg == 0)) {
            return false;
        }

        try {
            final ListaConsignatariaCmnPendenteAlemPrazoQuery pendenciaQuery = new ListaConsignatariaCmnPendenteAlemPrazoQuery();
            pendenciaQuery.csaCodigo = csaCodigo;
            pendenciaQuery.diasParaBloqueioCmnSer = diasParaBloqueioCmnEnviadaSer;
            pendenciaQuery.diasParaBloqueioCmnCseOrg = diasParaBloqueioCmnEnviadaCseOrg;

            final List<TransferObject> csaList = pendenciaQuery.executarDTO();
            if (!csaList.isEmpty()) {
                for (final TransferObject csaTO : csaList) {
                    final int qtdPendenciaSer = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_SER").toString());
                    final int qtdPendenciaCse = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_CSE").toString());
                    final int qtdPendenciaOrg = Integer.parseInt(csaTO.getAttribute("QTD_PENDENCIA_ORG").toString());

                    if (((diasParaBloqueioCmnEnviadaSer > 0) && (qtdPendenciaSer > 0)) ||
                            ((diasParaBloqueioCmnEnviadaCseOrg > 0) && ((qtdPendenciaCse > 0) || (qtdPendenciaOrg > 0)))) {
                        return true;
                    }
                }
            }

            return false;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private int getDiasParaBloqueioPorCmnPendente(String tpcCodigo, AcessoSistema responsavel) {
        int diasParaBloqueio = 0;

        try {
            final Object paramValue = ParamSist.getInstance().getParam(tpcCodigo, responsavel);
            diasParaBloqueio = TextHelper.isNum(paramValue) ? Integer.parseInt(paramValue.toString()) : 0;
        } catch (final NumberFormatException nex) {
            diasParaBloqueio = 0;
        }
        return diasParaBloqueio;
    }

    /**
     * Comunicação enviada por um usuário SER ou CSE/ORG com destino para uma consignatária será criada como pendente,
     * caso haja bloqueio de consignatária nas comunicações não respondidas no prazo
     * @param papCodigoDestinatario
     * @param responsavel
     * @return
     */
    private boolean definirComunicacaoPendente(String papCodigoDestinatario, AcessoSistema responsavel) {
        final int diasParaBloqueioCmnEnviadaSer = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER, responsavel);
        final int diasParaBloqueioCmnEnviadaCseOrg = getDiasParaBloqueioPorCmnPendente(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG, responsavel);

        if (CodedValues.PAP_CONSIGNATARIA.equals(papCodigoDestinatario) &&
                (((diasParaBloqueioCmnEnviadaSer > 0) && responsavel.isSer()) || ((diasParaBloqueioCmnEnviadaCseOrg > 0) && responsavel.isCseOrg()))) {
            return true;
        }

        return false;
    }

    @Override
    public List<ComunicacaoPermitida> listaComunicacaoPermitida(AcessoSistema responsavel) throws ZetraException {
        try {
            return ComunicacaoPermitidaHome.listaComunicacaoPermitida();
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarComunicacaoNaoLida(int diasAposEnvio, String papel, AcessoSistema responsavel) throws ZetraException {
        try {
            if (CodedValues.PAP_CONSIGNANTE.equals(papel)) {
                final ListaComunicacoesNaoLidasCseQuery query = new ListaComunicacoesNaoLidasCseQuery();
                query.diasAposEnvio = diasAposEnvio;
                return query.executarDTO();
            } else if (CodedValues.PAP_CONSIGNATARIA.equals(papel)) {
                final ListaComunicacoesNaoLidasCsaQuery query = new ListaComunicacoesNaoLidasCsaQuery();
                query.diasAposEnvio = diasAposEnvio;
                return query.executarDTO();
            } else if (CodedValues.PAP_ORGAO.equals(papel)) {
                final ListaComunicacoesNaoLidasOrgQuery query = new ListaComunicacoesNaoLidasOrgQuery();
                query.diasAposEnvio = diasAposEnvio;
                return query.executarDTO();
            } else if (CodedValues.PAP_SERVIDOR.equals(papel)) {
                final ListaComunicacoesNaoLidasSerQuery query = new ListaComunicacoesNaoLidasSerQuery();
                query.diasAposEnvio = diasAposEnvio;
                return query.executarDTO();
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<String> listaCsaCodigos(String nseCodigo, AcessoSistema responsavel) {
        try {
            final ListaConsignatariaConvenioNseCodigoQuery query = new ListaConsignatariaConvenioNseCodigoQuery();
            query.nseCodigo = nseCodigo;
            query.responsavel = responsavel;
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    private void comunicaCsa(String csaCodigo, TransferObject comunicacao, AcessoSistema responsavel) throws HQueryException, FindException, ConsignatariaControllerException, ParametroControllerException {
        verificaBloqueioNumCmnPendentes(csaCodigo, responsavel);

        // Verifica se CSA destinatária deve ser alertada via e-mail da criação desta comunicação.
        final List<TransferObject> paramCsa = parametroController.selectParamCsa(csaCodigo, CodedValues.TPA_RECEBE_EMAIL_ALERTA_COMUNICACAO, null, null, null, responsavel);
        if ((paramCsa != null) && (paramCsa.size() > 0) && (paramCsa.get(0) != null)) {
            final String emailAlertaNovaCmn = (String) paramCsa.get(0).getAttribute(Columns.PCS_VLR);

            // envia e-mail para o endereço especificado para esta consignatária no parâmetro de consignatária.
            if (!TextHelper.isNull(emailAlertaNovaCmn)) {
                final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), emailAlertaNovaCmn, comunicacao, responsavel);
                emailNovaCmn.start();
            }
        }
    }

    private void comunicaOrg(String orgCodigo, TransferObject comunicacao, AcessoSistema responsavel) throws HQueryException, FindException, ConsignatariaControllerException, ParametroControllerException {
        final Orgao orgao = OrgaoHome.findByPrimaryKey(orgCodigo);
        if (!TextHelper.isNull(orgao.getOrgEmail())) {
            final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), orgao.getOrgEmail(), comunicacao, responsavel);
            emailNovaCmn.start();
        }
    }

    private void comunicaCse(String cseCodigo, TransferObject comunicacao, AcessoSistema responsavel) throws HQueryException, FindException, ConsignatariaControllerException, ParametroControllerException {
        final Consignante cse = ConsignanteHome.findByPrimaryKey(cseCodigo);
        if (!TextHelper.isNull(cse.getCseEmail())) {
            final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), cse.getCseEmail(), comunicacao, responsavel);
            emailNovaCmn.start();
        }
    }

    private void enviaCopiaUsuEmail(TransferObject comunicacao, AcessoSistema responsavel) {
        final String email = !responsavel.isSer() ? responsavel.getUsuEmail() : responsavel.getSerEmail();
        if (!TextHelper.isNull(email)) {
            final ProcessaEmailAlertaNovaComunicacao emailNovaCmn = new ProcessaEmailAlertaNovaComunicacao(responsavel.getUsuCodigo(), email, comunicacao, responsavel);
            emailNovaCmn.start();
        }
    }
}
