package com.zetra.econsig.service.mensagem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.zetra.econsig.webservice.rest.request.MensagemRestResponse;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Funcao;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.LeituraMensagemUsuario;
import com.zetra.econsig.persistence.entity.LeituraMensagemUsuarioHome;
import com.zetra.econsig.persistence.entity.LeituraMensagemUsuarioId;
import com.zetra.econsig.persistence.entity.Mensagem;
import com.zetra.econsig.persistence.entity.MensagemCsa;
import com.zetra.econsig.persistence.entity.MensagemCsaHome;
import com.zetra.econsig.persistence.entity.MensagemHome;
import com.zetra.econsig.persistence.entity.MensagemPermiteEmail;
import com.zetra.econsig.persistence.entity.MensagemPermiteEmailHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.mensagem.ListaConsignatariaMensagemQuery;
import com.zetra.econsig.persistence.query.mensagem.ListaEmailsEnvioMensagemQuery;
import com.zetra.econsig.persistence.query.mensagem.ListaMensagemCsaBloqueioQuery;
import com.zetra.econsig.persistence.query.mensagem.ListaMensagemQuery;
import com.zetra.econsig.persistence.query.mensagem.PesquisaMensagemQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MensagemControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class MensagemControllerBean implements MensagemController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MensagemControllerBean.class);

    @Override
    public String createMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final Mensagem menBean = MensagemHome.create(mensagem.getUsuCodigo(), mensagem.getFunCodigo(),
                                                         Jsoup.parse(mensagem.getMenTitulo()).text(),
                                                         mensagem.getMenTexto(),
                                                         mensagem.getMenData(),
                                                         mensagem.getMenSequencia(),
                                                         mensagem.getMenExibeCse(),
                                                         mensagem.getMenExibeOrg(),
                                                         mensagem.getMenExibeCsa(),
                                                         mensagem.getMenExibeCor(),
                                                         mensagem.getMenExibeSer(),
                                                         mensagem.getMenExibeSup(),
                                                         mensagem.getMenExigeLeitura(),
                                                         mensagem.getMenPermiteLerDepois(),
                                                         mensagem.getMenNotificarCseLeitura(),
                                                         mensagem.getMenBloqCsaSemLeitura(),
                                                         mensagem.getMenHtml(),
                                                         mensagem.getMenPublica(),
                                                         mensagem.getMenLidaIndividualmente(),
                                                         mensagem.getMenPushNotificationSer());

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setMensagem(menBean.getMenCodigo());

            final StringBuilder logObs = new StringBuilder();
            logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.permite.ler.depois.alterado.de.arg0.para.arg1", responsavel, "", mensagem.getMenPermiteLerDepois()));
            logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.notificar.cse.leitura.alterado.de.arg0.para.arg1", responsavel, "", mensagem.getMenNotificarCseLeitura()));
            logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.bloquear.csa.sem.leitura.alterado.de.arg0.para.arg1", responsavel, "", mensagem.getMenBloqCsaSemLeitura()));
            if (!TextHelper.isNull(mensagem.getFunCodigo())) {
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.funcao.alterado.de.arg0.para.arg1", responsavel, "", mensagem.getFunCodigo()));
                logDelegate.setFuncao(mensagem.getFunCodigo());
            }
            logDelegate.add(logObs.toString());

            logDelegate.write();
            return menBean.getMenCodigo();

        } catch (final LogControllerException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public void createMensagemCsa(String menCodigo, String csaCodigo, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            MensagemCsaHome.create(menCodigo, csaCodigo);

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setMensagem(menCodigo);
            logDelegate.setPapel(CodedValues.PAP_CONSIGNATARIA);
            logDelegate.setConsignataria(csaCodigo);
            logDelegate.write();

        } catch (final LogControllerException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public MensagemTO findMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException {
        return setMensagemValues(findMensagemBean(mensagem));
    }

    private MensagemTO setMensagemValues(Mensagem mensagemBean) throws MensagemControllerException {
        final MensagemTO mensagem = new MensagemTO(mensagemBean.getMenCodigo());

        mensagem.setUsuCodigo(mensagemBean.getUsuario().getUsuCodigo());
        mensagem.setMenTitulo(mensagemBean.getMenTitulo());
        mensagem.setMenTexto(mensagemBean.getMenTexto());
        mensagem.setMenData(mensagemBean.getMenData());
        mensagem.setMenSequencia(mensagemBean.getMenSequencia());
        mensagem.setMenExibeCse(mensagemBean.getMenExibeCse());
        mensagem.setMenExibeOrg(mensagemBean.getMenExibeOrg());
        mensagem.setMenExibeCsa(mensagemBean.getMenExibeCsa());
        mensagem.setMenExibeCor(mensagemBean.getMenExibeCor());
        mensagem.setMenExibeSer(mensagemBean.getMenExibeSer());
        mensagem.setMenExibeSup(mensagemBean.getMenExibeSup());
        mensagem.setMenExigeLeitura(mensagemBean.getMenExigeLeitura());
        mensagem.setMenHtml(mensagemBean.getMenHtml());
        mensagem.setMenPermiteLerDepois(mensagemBean.getMenPermiteLerDepois());
        mensagem.setMenNotificarCseLeitura(mensagemBean.getMenNotificarCseLeitura());
        mensagem.setMenBloqCsaSemLeitura(mensagemBean.getMenBloqCsaSemLeitura());
        mensagem.setMenPublica(mensagemBean.getMenPublica());
        mensagem.setMenLidaIndividualmente(mensagemBean.getMenLidaIndividualmente());
        mensagem.setMenPushNotificationSer(mensagemBean.getMenPushNotificationSer());
        try {
            mensagem.setFunCodigo(mensagemBean.getFuncao().getFunCodigo());
        } catch (final NullPointerException ex) {
        }

        return mensagem;
    }

    private Mensagem findMensagemBean(MensagemTO mensagem) throws MensagemControllerException {
        Mensagem mensagemBean = null;
        if (mensagem.getMenCodigo() != null) {
            try {
                mensagemBean = MensagemHome.findByPrimaryKey(mensagem.getMenCodigo());

                if ("S".equals(mensagemBean.getMenHtml())) {
                    mensagemBean.setMenTexto(Jsoup.parse(mensagemBean.getMenTexto()).text());
                }

            } catch (final FindException e) {
                throw new MensagemControllerException("mensagem.erro.nenhuma.mensagem.encontrada", (AcessoSistema) null);
            }
        }
        return mensagemBean;
    }

    @Override
    public void removeMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final Mensagem menBean = findMensagemBean(mensagem);
            final String menCodigo = menBean.getMenCodigo();
            AbstractEntityHome.remove(menBean);
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setMensagem(menCodigo);
            logDelegate.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erro.nao.possivel.excluir.mensagem.selecionada.ja.lida.pelos.usuarios", responsavel);
        }
    }

    @Override
    public void removeMensagemCsa(String menCodigo, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            MensagemCsa menCsaBean = null;
            final List<MensagemCsa> beans = MensagemCsaHome.findByMenCodigo(menCodigo);
            final Iterator<MensagemCsa> it = beans.iterator();
            while (it.hasNext()) {
                menCsaBean = it.next();
                AbstractEntityHome.remove(menCsaBean);
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erro.nao.possivel.remover.dependencias.desta.mensagem", responsavel, ex);
        }
    }

    @Override
    public void updateMensagem(MensagemTO mensagem, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final Mensagem mensagemBean = findMensagemBean(mensagem);
            final LogDelegate log = new LogDelegate(responsavel, Log.MENSAGEM, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setMensagem(mensagemBean.getMenCodigo());

            final StringBuilder logObs = new StringBuilder();

            mensagem.setMenTitulo(Jsoup.parse(mensagem.getMenTitulo()).text());

            if ("S".equals(mensagemBean.getMenHtml())) {
                if (!TextHelper.isNull(mensagem.getMenTexto())) {
                    mensagem.setMenTexto(Jsoup.parse(mensagem.getMenTexto()).text());
                }
                mensagem.setMenHtml("N");
            } else {
                mensagem.setMenHtml("N");
            }

            // Compara a versão do cache com a passada por parâmetro
            final MensagemTO mensagemCache = setMensagemValues(mensagemBean);
            final CustomTransferObject merge = log.getUpdatedFields(mensagem.getAtributos(), mensagemCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.MEN_USU_CODIGO)) {
                mensagemBean.setUsuario(UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.MEN_USU_CODIGO)));
                if (!TextHelper.isNull(merge.getAttribute(Columns.MEN_USU_CODIGO))) {
                    log.setUsuario((String) merge.getAttribute(Columns.MEN_USU_CODIGO));
                }
            }
            if (merge.getAtributos().containsKey(Columns.MEN_TITULO)) {
                mensagemBean.setMenTitulo((String) merge.getAttribute(Columns.MEN_TITULO));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_TEXTO)) {
                mensagemBean.setMenTexto((String) merge.getAttribute(Columns.MEN_TEXTO));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_DATA)) {
                mensagemBean.setMenData((Date) merge.getAttribute(Columns.MEN_DATA));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_SEQUENCIA)) {
                mensagemBean.setMenSequencia((Short) merge.getAttribute(Columns.MEN_SEQUENCIA));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_CSE)) {
                mensagemBean.setMenExibeCse((String) merge.getAttribute(Columns.MEN_EXIBE_CSE));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_ORG)) {
                mensagemBean.setMenExibeOrg((String) merge.getAttribute(Columns.MEN_EXIBE_ORG));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_CSA)) {
                mensagemBean.setMenExibeCsa((String) merge.getAttribute(Columns.MEN_EXIBE_CSA));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_COR)) {
                mensagemBean.setMenExibeCor((String) merge.getAttribute(Columns.MEN_EXIBE_COR));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_SER)) {
                mensagemBean.setMenExibeSer((String) merge.getAttribute(Columns.MEN_EXIBE_SER));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIBE_SUP)) {
                mensagemBean.setMenExibeSup((String) merge.getAttribute(Columns.MEN_EXIBE_SUP));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_EXIGE_LEITURA)) {
                mensagemBean.setMenExigeLeitura((String) merge.getAttribute(Columns.MEN_EXIGE_LEITURA));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_HTML)) {
                mensagemBean.setMenHtml((String) merge.getAttribute(Columns.MEN_HTML));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_PERMITE_LER_DEPOIS)) {
                final String velho = (mensagemBean.getMenPermiteLerDepois() != null) ? mensagemBean.getMenPermiteLerDepois() : "";
                final String novo = (merge.getAttribute(Columns.MEN_PERMITE_LER_DEPOIS) != null) ? (String) merge.getAttribute(Columns.MEN_PERMITE_LER_DEPOIS) : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.permite.ler.depois.alterado.de.arg0.para.arg1", responsavel, velho, novo));

                mensagemBean.setMenPermiteLerDepois((String) merge.getAttribute(Columns.MEN_PERMITE_LER_DEPOIS));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_NOTIFICAR_CSE_LEITURA)) {
                final String velho = (mensagemBean.getMenNotificarCseLeitura() != null) ? mensagemBean.getMenNotificarCseLeitura() : "";
                final String novo = (merge.getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA) != null) ? (String) merge.getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA) : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.notificar.cse.leitura.alterado.de.arg0.para.arg1", responsavel, velho, novo));

                mensagemBean.setMenNotificarCseLeitura((String) merge.getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA));
            }
            if (merge.getAtributos().containsKey(Columns.MEN_BLOQ_CSA_SEM_LEITURA)) {
                final String velho = (mensagemBean.getMenBloqCsaSemLeitura() != null) ? mensagemBean.getMenBloqCsaSemLeitura() : "";
                final String novo = (merge.getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA) != null) ? (String) merge.getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA) : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.bloquear.csa.sem.leitura.alterado.de.arg0.para.arg1", responsavel, velho, novo));

                mensagemBean.setMenBloqCsaSemLeitura((String) merge.getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA));
            }

            if (merge.getAtributos().containsKey(Columns.MEN_PUSH_NOTIFICATION_SER)) {
                final String velho = (mensagemBean.getMenPushNotificationSer() != null) ? mensagemBean.getMenPushNotificationSer() : "";
                final String novo = (merge.getAttribute(Columns.MEN_PUSH_NOTIFICATION_SER) != null) ? (String) merge.getAttribute(Columns.MEN_PUSH_NOTIFICATION_SER) : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.push.notification.ser.alterado.de.arg0.para.arg1", responsavel, velho, novo));

                mensagemBean.setMenPushNotificationSer((String) merge.getAttribute(Columns.MEN_PUSH_NOTIFICATION_SER));
            }

            final MensagemTO mensagemAux = findMensagem(mensagem, responsavel);
            if (merge.getAtributos().containsKey(Columns.MEN_FUN_CODIGO) || (!TextHelper.isNull(mensagemAux.getFunCodigo()) && TextHelper.isNull(merge.getAttribute(Columns.MEN_FUN_CODIGO)))) {
                final Funcao funcao = !TextHelper.isNull(merge.getAttribute(Columns.MEN_FUN_CODIGO)) ? FuncaoHome.findByPrimaryKey((String) merge.getAttribute(Columns.MEN_FUN_CODIGO)) : null;
                final String velho = (mensagemAux.getFunCodigo() != null) ? mensagemAux.getFunCodigo() : "";
                final String novo = ((funcao != null) && (funcao.getFunCodigo() != null)) ? funcao.getFunCodigo() : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.funcao.alterado.de.arg0.para.arg1", responsavel, velho, novo));
                log.setFuncao(novo);

                mensagemBean.setFuncao(funcao);
            }

            if (merge.getAtributos().containsKey(Columns.MEN_PUBLICA)) {
                mensagemBean.setMenPublica((String) merge.getAttribute(Columns.MEN_PUBLICA));
            }

            if (merge.getAtributos().containsKey(Columns.MEN_LIDA_INDIVIDUALMENTE)) {
                final String velho = (mensagemBean.getMenLidaIndividualmente() != null) ? mensagemBean.getMenLidaIndividualmente() : "";
                final String novo = (merge.getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE) != null) ? (String) merge.getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE) : "";
                logObs.append(ApplicationResourcesHelper.getMessage("mensagem.log.mensagem.lida.individualmente.alterado.de.arg0.para.arg1", responsavel, velho, novo));

                mensagemBean.setMenLidaIndividualmente((String) merge.getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE));
            }

            AbstractEntityHome.update(mensagemBean);

            log.add(logObs.toString());
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final FindException e) {
            throw new MensagemControllerException(e);
        } catch (final UpdateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MensagemControllerException(e);
        }
    }

    @Override
    public int countPesquisaMensagem(AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final PesquisaMensagemQuery query = new PesquisaMensagemQuery();
            query.count = true;
            query.responsavel = responsavel;
            return query.executarContador();
        } catch (final HQueryException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countMensagem(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaMensagemQuery query = new ListaMensagemQuery();

            query.count = true;
            if (criterio != null) {
                query.menCodigo = (List<String>) criterio.getAttribute(Columns.MEN_CODIGO);
                query.menExibeCor = (String) criterio.getAttribute(Columns.MEN_EXIBE_COR);
                query.menExibeCsa = (String) criterio.getAttribute(Columns.MEN_EXIBE_CSA);
                query.menExibeCse = (String) criterio.getAttribute(Columns.MEN_EXIBE_CSE);
                query.menExibeOrg = (String) criterio.getAttribute(Columns.MEN_EXIBE_ORG);
                query.menExibeSer = (String) criterio.getAttribute(Columns.MEN_EXIBE_SER);
                query.menTitulo = (String) criterio.getAttribute(Columns.MEN_TITULO);
                query.exibeMenPublica = !TextHelper.isNull(criterio.getAttribute(Columns.MEN_PUBLICA)) && (CodedValues.TPC_SIM.equals(criterio.getAttribute(Columns.MEN_PUBLICA).toString()));

                if (criterio.getAttribute(Columns.CSA_CODIGO) instanceof List<?> csaCodigoList) {
                    query.csaCodigo = (List<String>) csaCodigoList;
                } else if (criterio.getAttribute(Columns.CSA_CODIGO) instanceof String csaCodigo) {
                    query.csaCodigo = List.of(csaCodigo);
                }
            }

            return query.executarContador();

        } catch (final HQueryException ex) {
            throw new MensagemControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstMensagem(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaMensagemQuery query = new ListaMensagemQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.menCodigo = (List<String>) criterio.getAttribute(Columns.MEN_CODIGO);
                query.menExibeCor = (String) criterio.getAttribute(Columns.MEN_EXIBE_COR);
                query.menExibeCsa = (String) criterio.getAttribute(Columns.MEN_EXIBE_CSA);
                query.menExibeCse = (String) criterio.getAttribute(Columns.MEN_EXIBE_CSE);
                query.menExibeOrg = (String) criterio.getAttribute(Columns.MEN_EXIBE_ORG);
                query.menExibeSer = (String) criterio.getAttribute(Columns.MEN_EXIBE_SER);
                query.menTitulo = (String) criterio.getAttribute(Columns.MEN_TITULO);
                query.exibeMenPublica = !TextHelper.isNull(criterio.getAttribute(Columns.MEN_PUBLICA)) && (CodedValues.TPC_SIM.equals(criterio.getAttribute(Columns.MEN_PUBLICA).toString()));

                if (criterio.getAttribute(Columns.CSA_CODIGO) instanceof List<?> csaCodigoList) {
                    query.csaCodigo = (List<String>) csaCodigoList;
                } else if (criterio.getAttribute(Columns.CSA_CODIGO) instanceof String csaCodigo) {
                    query.csaCodigo = List.of(csaCodigo);
                }
            }

            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new MensagemControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaMensagem(AcessoSistema responsavel, int rows, boolean mensagemLida) throws MensagemControllerException {
        try {
            final PesquisaMensagemQuery query = new PesquisaMensagemQuery();
            query.responsavel = responsavel;
            if (rows > 0) {
                query.firstResult = 0;
                query.maxResults = rows;
            }

            query.menLida = mensagemLida;


            final Markdown4jProcessorExtended processor = new Markdown4jProcessorExtended();

            final List<TransferObject> retorno = query.executarDTO();
            for (final TransferObject obj : retorno) {
                if ("N".equals(obj.getAttribute(Columns.MEN_HTML).toString())) {
                    obj.setAttribute(Columns.MEN_TITULO, processor.process(TextHelper.forHtmlContent(obj.getAttribute(Columns.MEN_TITULO).toString())));
                    obj.setAttribute(Columns.MEN_TEXTO, processor.process(TextHelper.forHtmlContent(obj.getAttribute(Columns.MEN_TEXTO).toString())));
                }
            }

            return retorno;

        } catch (final HQueryException ex) {
            throw new MensagemControllerException(ex);
        } catch (final IOException e) {
            throw new MensagemControllerException("mensagem.erro.interpretar.mensagem", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstConsignatarias(String menCodigo, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaConsignatariaMensagemQuery query = new ListaConsignatariaMensagemQuery();
            query.menCodigo = menCodigo;

            return query.executarDTO();

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> lstEmailEnvioMensagem(String menCodigo, String papel, List<String> csaCodigos, boolean incluirBloqueadas, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaEmailsEnvioMensagemQuery query = new ListaEmailsEnvioMensagemQuery();
            query.menCodigo = menCodigo;
            query.papCodigo = papel;
            query.csaCodigos = csaCodigos;
            query.incluirBloqueadas = incluirBloqueadas;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Envia uma mensagem por email para todas consignatárias associadas a ela.
     * @param mensagem Mensagem a ser enviada
     * @param responsavel
     * @return Retorna: -1 = caso a mensagem não seja enviada para nenhuma consignatária,
     *                   0 = caso a mensagem seja enviada para algumas das consignatárias e
     *                   1 = caso seja enviado para todos os destinatários.
     * @throws MensagemControllerException
     */
    @Override
    public int enviaMensagemEmail(MensagemTO mensagem, List<String> papeis, boolean incluirBloqueadas, AcessoSistema responsavel) throws MensagemControllerException {
        int retorno = 1;
        final Mensagem mensagemBean = findMensagemBean(mensagem);

        if ((papeis == null) || papeis.isEmpty()) {
            throw new MensagemControllerException("mensagem.erro.selecione.destinatario", responsavel);
        }

        // Verifica se o responsável possui permissão para enviar mensagem para todos os destinatários selecionados
        final List<String> destinatarioPermitido = getDestinatarioMensagemPermitidaEmail(responsavel);
        if (!destinatarioPermitido.containsAll(papeis)) {
            throw new MensagemControllerException("mensagem.erro.usuario.sem.permissao.enviar", responsavel);
        }

        // Seta os papéis onde ocorreram erros ao tentar enviar email
        final List<String> papeisError = new ArrayList<>();

        // Se a mensagem é exibida para as CSAs.
        final String menCodigo = mensagem.getMenCodigo();
        if ((papeis.contains(CodedValues.PAP_CONSIGNATARIA) || papeis.contains(CodedValues.PAP_CONSIGNATARIA + CodedValues.TPC_SIM) || papeis.contains(CodedValues.PAP_CONSIGNATARIA + CodedValues.TPC_NAO)) && (mensagemBean.getMenExibeCsa() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeCsa())) {
            final List<TransferObject> csaLiberadas = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_CONSIGNATARIA, null, incluirBloqueadas, responsavel);

            final List<TransferObject> todasCsas = lstConsignatarias(menCodigo, responsavel);
            final List<String> csaAssociadoMen = new ArrayList<>();
            for (final TransferObject cto : todasCsas) {
                if ("S".equals(cto.getAttribute("SELECIONADO"))) {
                    csaAssociadoMen.add(cto.getAttribute(Columns.CSA_CODIGO).toString());
                }
            }

            if (((csaLiberadas == null) || csaLiberadas.isEmpty()) && !csaAssociadoMen.isEmpty()) {
                // Consignatárias associadas a mensagem bloqueadas ou sem e-mail cadastrado não pode ser enviado email
                retorno = -1;
            } else if (((csaLiberadas == null) || csaLiberadas.isEmpty()) && csaAssociadoMen.isEmpty()) {
                // Não possui nenhuma consignatária associada para a mensagem, envia para todas as consignatárias ativas e que possuírem e-mail
                TransferObject destinatario = null;
                for (final TransferObject cto : todasCsas) {
                    if ((cto.getAttribute(Columns.CSA_ATIVO).equals(CodedValues.STS_ATIVO) || incluirBloqueadas) && !TextHelper.isNull(cto.getAttribute(Columns.CSA_EMAIL))) {
                        destinatario = new CustomTransferObject();
                        destinatario.setAttribute("CODIGO_ENTIDADE", cto.getAttribute(Columns.CSA_CODIGO).toString());
                        destinatario.setAttribute("EMAIL", cto.getAttribute(Columns.CSA_EMAIL).toString());

                        csaLiberadas.add(destinatario);
                    }
                }

            } else if ((csaLiberadas != null) && (csaLiberadas.size() != csaAssociadoMen.size())) {
                // Se possui alguma consignatária associada a mensagem que não está apta para receber o email, informa o usuário
                retorno = 0;
            }

            try {
                // Se não há nenhuma CSA associada à mensagem, envia para todas.
                enviaMensagemEmail(mensagemBean, csaLiberadas, CodedValues.PAP_CONSIGNATARIA, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para as consignatárias.", e);
                papeisError.add(CodedValues.PAP_CONSIGNATARIA);
            }
        }

        // Se a mensagem é exibida para os consignantes
        if (papeis.contains(CodedValues.PAP_CONSIGNANTE) && (mensagemBean.getMenExibeCse() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeCse())) {
            // Recupera todos os consignantes
            final List<TransferObject> consignantes = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_CONSIGNANTE, null, false, responsavel);
            try {
                enviaMensagemEmail(mensagemBean, consignantes, CodedValues.PAP_CONSIGNANTE, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para o consignante.", e);
                papeisError.add(CodedValues.PAP_CONSIGNANTE);
            }
        }

        // Se a mensagem é exibida para os correspondentes
        if (papeis.contains(CodedValues.PAP_CORRESPONDENTE) && (mensagemBean.getMenExibeCor() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeCor())) {
            // Recupera todas as consignatárias que podem estar associadas à mensagem
            final List<TransferObject> todasConsignatarias = lstConsignatarias(mensagem.getMenCodigo(), responsavel);
            // Monta a lista com os códigos das consignatárias que estão associadas à mensagem
            final List<String> csaCodigos = new ArrayList<>();
            for (final TransferObject cto : todasConsignatarias) {
                if ("S".equals(cto.getAttribute("SELECIONADO"))) {
                    csaCodigos.add(cto.getAttribute(Columns.CSA_CODIGO).toString());
                }
            }

            final List<TransferObject> correspondentes = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_CORRESPONDENTE, csaCodigos, false, responsavel);

            try {
                enviaMensagemEmail(mensagemBean, correspondentes, CodedValues.PAP_CORRESPONDENTE, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para os correspondentes.", e);
                papeisError.add(CodedValues.PAP_CORRESPONDENTE);
            }
        }

        // Se a mensagem é exibida para os orgaos
        if (papeis.contains(CodedValues.PAP_ORGAO) && (mensagemBean.getMenExibeOrg() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeOrg())) {
            // Recupera todos os orgaos
            final List<TransferObject> orgaos = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_ORGAO, null, false, responsavel);
            try {
                enviaMensagemEmail(mensagemBean, orgaos, CodedValues.PAP_ORGAO, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para os órgãos.", e);
                papeisError.add(CodedValues.PAP_ORGAO);
            }
        }

        // Se a mensagem é exibida para os suporte
        if (papeis.contains(CodedValues.PAP_SUPORTE) && (mensagemBean.getMenExibeSup() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeSup())) {
            // Recupera todos os suporte
            final List<TransferObject> suporte = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_SUPORTE, null, false, responsavel);

            try {
                enviaMensagemEmail(mensagemBean, suporte, CodedValues.PAP_SUPORTE, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para o suporte.", e);
                papeisError.add(CodedValues.PAP_SUPORTE);
            }
        }

        // Se a mensagem é exibida para os servidores
        if (papeis.contains(CodedValues.PAP_SERVIDOR) && (mensagemBean.getMenExibeSer() != null) && CodedValues.TPC_SIM.equals(mensagemBean.getMenExibeSer())) {
            // Recupera todos os servidores
            final List<TransferObject> servidores = lstEmailEnvioMensagem(menCodigo, CodedValues.PAP_SERVIDOR, null, false, responsavel);

            try {
                enviaMensagemEmail(mensagemBean, servidores, CodedValues.PAP_SERVIDOR, responsavel);
            } catch (final MensagemControllerException e) {
                LOG.error("Não foi possível enviar e-mail para os servidores.", e);
                papeisError.add(CodedValues.PAP_SERVIDOR);
            }
        }

        if (!papeisError.isEmpty()) {
            final StringBuilder entidades = new StringBuilder();

            if (papeisError.contains(CodedValues.PAP_CONSIGNANTE)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel)).append(", ");
            }
            if (papeisError.contains(CodedValues.PAP_SUPORTE)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel)).append(", ");
            }
            if (papeisError.contains(CodedValues.PAP_ORGAO)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)).append(", ");
            }
            if (papeisError.contains(CodedValues.PAP_CONSIGNATARIA)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append(", ");
            }
            if (papeisError.contains(CodedValues.PAP_CORRESPONDENTE)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel)).append(", ");
            }
            if (papeisError.contains(CodedValues.PAP_SERVIDOR)) {
                entidades.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append(", ");
            }

            throw new MensagemControllerException("mensagem.erro.nao.possivel.enviar.email.para.seguintes.entidades", responsavel, entidades.replace(entidades.lastIndexOf(", "), entidades.lastIndexOf(", ") + 3, ".").toString());
        }

        return retorno;
    }

    /**
     * Envia uma mensagem por e-mail para uma lista de destinatários.
     * @param mensagem Mensagem a ser enviada.
     * @param destinatarios Lista de consignatárias.
     * @param responsavel
     * @throws MensagemControllerException
     */
    private void enviaMensagemEmail(Mensagem mensagem, List<TransferObject> destinatarios, String papel, AcessoSistema responsavel) throws MensagemControllerException {

        try {
            if (destinatarios == null) {
                throw new MensagemControllerException("mensagem.erro.lista.destinatarios.nao.pode.estar.vazia", responsavel);
            }

            if (TextHelper.isNull(papel)) {
                throw new MensagemControllerException("mensagem.erro.selecione.destinatario", responsavel);
            }

            // Monta a lista de e-mails que deverão receber a mensagem.
            final List<String> emails = new ArrayList<>();
            for (final TransferObject destinatario : destinatarios) {
                // Verificar se a entidade possui e-mail cadastrado.
                final String entidadeEmail = destinatario != null ? (String) destinatario.getAttribute("EMAIL") : null;
                if (TextHelper.isNull(entidadeEmail)) {
                    continue;
                }

                // Verificar se a CSA possui mais de um e-mail cadastrado se são validos.
                final StringTokenizer email = new StringTokenizer(MailHelper.verifyEmailList(entidadeEmail), ",");
                if (email.countTokens() > 1) {
                    while (email.hasMoreTokens()) {
                        final String aux = email.nextToken();
                        if (TextHelper.isEmailValid(aux)) {
                            emails.add(aux);
                        } else {
                            LOG.warn("O e-mail '" + aux + "' é invalido.");
                        }
                    }
                } else {
                    final String aux = email.nextToken();
                    if (TextHelper.isEmailValid(aux)) {
                        emails.add(aux);
                    } else {
                        LOG.warn("O e-mail '" + aux + "' é invalido.");
                    }
                }
            }

            try {
                if ((emails != null) && (emails.size() > 0)) {
                    EnviaEmailHelper.enviarEmailMensagem(mensagem, emails, responsavel);
                }
            } catch (final ViewHelperException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new MensagemControllerException(ex);
            }

            for (final TransferObject destinatario : destinatarios) {
                try {
                    final String codigoEntidade = destinatario.getAttribute("CODIGO_ENTIDADE") != null ? destinatario.getAttribute("CODIGO_ENTIDADE").toString() : null;
                    final String email = destinatario.getAttribute("EMAIL") != null ? destinatario.getAttribute("EMAIL").toString() : null;

                    final LogDelegate log = new LogDelegate(responsavel, Log.MENSAGEM, Log.SELECT, Log.LOG_INFORMACAO);
                    log.setMensagem(mensagem.getMenCodigo());
                    log.setPapel(papel);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.mensagem.enviada.email.destinatario", responsavel, email) + " ");

                    if (CodedValues.PAP_CONSIGNANTE.equalsIgnoreCase(papel)) {
                        log.setConsignante(codigoEntidade);
                    } else if (CodedValues.PAP_SUPORTE.equalsIgnoreCase(papel)) {
                        log.setUsuario(codigoEntidade);
                    } else if (CodedValues.PAP_ORGAO.equalsIgnoreCase(papel)) {
                        log.setOrgao(codigoEntidade);
                    } else if (CodedValues.PAP_CONSIGNATARIA.equalsIgnoreCase(papel)) {
                        log.setConsignataria(codigoEntidade);
                    } else if (CodedValues.PAP_CORRESPONDENTE.equalsIgnoreCase(papel)) {
                        log.setCorrespondente(codigoEntidade);
                    } else if (CodedValues.PAP_SERVIDOR.equalsIgnoreCase(papel)) {
                        log.setServidor(codigoEntidade);
                    }

                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new MensagemControllerException(ex);
                }
            }

            // Cria log geral
            final LogDelegate log = new LogDelegate(responsavel, Log.MENSAGEM, Log.SELECT, Log.LOG_INFORMACAO);
            log.setMensagem(mensagem.getMenCodigo());
            log.setPapel(papel);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.foram.enviadas.arg0.mensagens.para.destinatarios", responsavel, String.valueOf(emails.size())) + " ");

            if (CodedValues.PAP_CONSIGNANTE.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel).toUpperCase());
            } else if (CodedValues.PAP_SUPORTE.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toUpperCase());
            } else if (CodedValues.PAP_ORGAO.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toUpperCase());
            } else if (CodedValues.PAP_CONSIGNATARIA.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase());
            } else if (CodedValues.PAP_CORRESPONDENTE.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel).toUpperCase());
            } else if (CodedValues.PAP_SERVIDOR.equalsIgnoreCase(papel)) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel).toUpperCase());
            }

            log.write();

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException(ex);
        }
    }

    /**
     * Retorna quantidade de mensagens que deveriam ter confirmacao criadas apos o cadastro do usuario.
     * @param criterio Criterio de consulta - data de cadastro do usuario.
     * @param responsavel Usuario logado para o qual as mensagens devem estar direcionadas.
     * @return Quantidade de mensagens do usuario sem confirmacao de leitura.
     * @throws MensagemControllerException
     */
    @Override
    public int countMensagemUsuarioSemLeitura(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException {
        try {

            final ParamSist ps = ParamSist.getInstance();
            final Object qtdeMesExpirarMsgObj = ps.getParam(CodedValues.TPC_QTD_MESES_EXPIRAR_MENSAGENS, responsavel);
            final Integer qtdeMesExpirarMsg = !TextHelper.isNull(qtdeMesExpirarMsgObj) ? Integer.parseInt(qtdeMesExpirarMsgObj.toString()) : null;

            final PesquisaMensagemQuery query = new PesquisaMensagemQuery();
            query.menExigeLeitura = CodedValues.TPC_SIM;
            query.responsavel = responsavel;
            query.menDataMinima = (Date) criterio.getAttribute(Columns.USU_DATA_CAD);
            query.naoConfirmadas = true;
            query.count = true;
            query.qtdeMesExpirarMsg = qtdeMesExpirarMsg;

            return query.executarContador();

        } catch (final HQueryException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna mensagens que deveriam ter confirmacao criadas apos o cadastro do usuario.
     * @param criterio Criterio de consulta - data de cadastro do usuario.
     * @param responsavel Usuario logado para o qual as mensagens devem estar direcionadas.
     * @return Mensagens do usuario sem confirmacao de leitura.
     * @throws MensagemControllerException
     */
    @Override
    public List<MensagemTO> lstMensagemUsuarioSemLeitura(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException {
        try {

            final ParamSist ps = ParamSist.getInstance();
            final Object qtdeMesExpirarMsgObj = ps.getParam(CodedValues.TPC_QTD_MESES_EXPIRAR_MENSAGENS, responsavel);
            final Integer qtdeMesExpirarMsg = !TextHelper.isNull(qtdeMesExpirarMsgObj) ? Integer.parseInt(qtdeMesExpirarMsgObj.toString()) : null;

            final PesquisaMensagemQuery query = new PesquisaMensagemQuery();
            query.menExigeLeitura = CodedValues.TPC_SIM;
            query.responsavel = responsavel;
            query.menDataMinima = (Date) criterio.getAttribute(Columns.USU_DATA_CAD);
            query.naoConfirmadas = true;
            query.qtdeMesExpirarMsg = qtdeMesExpirarMsg;

            return query.executarDTO(MensagemTO.class);

        } catch (final HQueryException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Insere valores da entidade LeituraMensagemUsuario na tabela.
     * @param criterio Valores a serem armazenados.
     * @param responsavel
     * @throws MensagemControllerException
     */
    @Override
    public void createLeituraMensagemUsuario(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final String menCodigo = (String) criterio.getAttribute(Columns.LMU_MEN_CODIGO);
            final String usuCodigo = (String) criterio.getAttribute(Columns.LMU_USU_CODIGO);
            final Date lmuData = (criterio.getAttribute(Columns.LMU_DATA) != null) ? (Date) criterio.getAttribute(Columns.LMU_DATA) : new Date();

            try {
                // Verifica se já existe registro de confirmação de leitura, pois caso ocorra um "race condition" não deverá
                // tentar criar novamente o registro, evitando erro de chave duplicada
                LeituraMensagemUsuarioHome.findByPrimaryKey(new LeituraMensagemUsuarioId(menCodigo, usuCodigo));
            } catch (final FindException ex) {
                final LeituraMensagemUsuario lmu = LeituraMensagemUsuarioHome.create(menCodigo, usuCodigo, lmuData);

                // Enviar email para gestor caso a mensagem exija notificação para o gestor
                MensagemTO msgTO = new MensagemTO(menCodigo);
                msgTO = findMensagem(msgTO, responsavel);

                if (!TextHelper.isNull(msgTO.getMenNotificarCseLeitura()) && CodedValues.TPC_SIM.equalsIgnoreCase(msgTO.getMenNotificarCseLeitura().toString())) {
                    EnviaEmailHelper.enviarEmailConfLeituraMensagem(lmu, responsavel);
                }
            }
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (final ViewHelperException e) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<String> getDestinatarioMensagemPermitidaEmail(AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final List<String> retorno = new ArrayList<>();
            final List<MensagemPermiteEmail> listMpe = MensagemPermiteEmailHome.findByPapCodigoRemetente(responsavel.getPapCodigo());

            if ((listMpe == null) || listMpe.isEmpty()) {
                throw new MensagemControllerException("mensagem.erro.nao.possivel.encontrar.permissoes.para.envio.mensagem", responsavel);
            }

            for (final MensagemPermiteEmail mpe : listMpe) {
                retorno.add(mpe.getPapelDestinatario().getPapCodigo());
            }

            return retorno;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new MensagemControllerException("mensagem.erro.nao.possivel.encontrar.permissoes.para.envio.mensagem", responsavel, ex);
        }
    }

    /**
     * Lista as mensagens das consignatárias que exigem confirmação de leitura e estão pendentes de leitura.
     *
     * @param csaCodigo Código da consignatária das mensagens
     * @param responsavel
     * @return
     * @throws MensagemControllerException
     */
    @Override
    public List<TransferObject> lstMensagemCsaBloqueio(String csaCodigo, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaMensagemCsaBloqueioQuery query = new ListaMensagemCsaBloqueioQuery(responsavel);
            query.csaCodigo = csaCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new MensagemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstMensagemBoasVindas(CustomTransferObject criterio, AcessoSistema responsavel) throws MensagemControllerException {
        try {
            final ListaMensagemQuery query = new ListaMensagemQuery();

            if (criterio != null) {
                query.exibeMenPublica = !TextHelper.isNull(criterio.getAttribute(Columns.MEN_PUBLICA)) && (CodedValues.TPC_SIM.equals(criterio.getAttribute(Columns.MEN_PUBLICA).toString()));
            }

            final Markdown4jProcessorExtended processo = new Markdown4jProcessorExtended();

            final List<TransferObject> retorno = query.executarDTO();
            for (final TransferObject obj : retorno) {
                if ("N".equals(obj.getAttribute(Columns.MEN_HTML).toString())) {
                    obj.setAttribute(Columns.MEN_TITULO, processo.process(TextHelper.forHtmlContent(obj.getAttribute(Columns.MEN_TITULO).toString())));
                    obj.setAttribute(Columns.MEN_TEXTO, processo.process(TextHelper.forHtmlContent(obj.getAttribute(Columns.MEN_TEXTO).toString())));
                }
            }

            return retorno;

        } catch (final HQueryException ex) {
            throw new MensagemControllerException(ex);
        } catch (final IOException e) {
            throw new MensagemControllerException("mensagem.erro.interpretar.mensagem", responsavel);
        }
    }

    @Override
    public List<MensagemRestResponse> parseToResponse(List<TransferObject> result){
        List<MensagemRestResponse> response = new ArrayList<MensagemRestResponse>();

        if(result != null) {
            for (final TransferObject item : result){
                MensagemRestResponse mensagem = new MensagemRestResponse();

                mensagem.setMenCodigo((String) item.getAttribute(Columns.MEN_CODIGO));
                mensagem.setFunCodigo((String) item.getAttribute(Columns.MEN_FUN_CODIGO));
                mensagem.setMenTitulo((String) item.getAttribute(Columns.MEN_TITULO));
                mensagem.setMenTexto((String) item.getAttribute(Columns.MEN_TEXTO));
                mensagem.setUsuCodigo((String) item.getAttribute(Columns.MEN_USU_CODIGO));
                mensagem.setMenData((String) item.getAttribute(Columns.MEN_DATA).toString());
                mensagem.setMenSequencia((Short) item.getAttribute(Columns.MEN_SEQUENCIA));
                mensagem.setMenExibeCse(item.getAttribute(Columns.MEN_EXIBE_CSE) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_CSE).toString()));
                mensagem.setMenExibeOrg(item.getAttribute(Columns.MEN_EXIBE_ORG) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_ORG).toString()));
                mensagem.setMenExibeCsa(item.getAttribute(Columns.MEN_EXIBE_CSA) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_CSA).toString()));
                mensagem.setMenExibeCor(item.getAttribute(Columns.MEN_EXIBE_COR) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_COR).toString()));
                mensagem.setMenExibeSer(item.getAttribute(Columns.MEN_EXIBE_SER) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_SER).toString()));
                mensagem.setMenExibeSup(item.getAttribute(Columns.MEN_EXIBE_SUP) != null && "S".equals(item.getAttribute(Columns.MEN_EXIBE_SUP).toString()));
                mensagem.setMenExigeLeitura(item.getAttribute(Columns.MEN_EXIGE_LEITURA) != null && "S".equals(item.getAttribute(Columns.MEN_EXIGE_LEITURA).toString()));
                mensagem.setMenHtml(item.getAttribute(Columns.MEN_HTML) != null && "S".equals(item.getAttribute(Columns.MEN_HTML).toString()));
                mensagem.setMenPermiteLerDepois(item.getAttribute(Columns.MEN_PERMITE_LER_DEPOIS) != null && "S".equals(item.getAttribute(Columns.MEN_PERMITE_LER_DEPOIS).toString()));
                mensagem.setMenNotificarCseLeitura(item.getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA) != null && "S".equals(item.getAttribute(Columns.MEN_NOTIFICAR_CSE_LEITURA).toString()));
                mensagem.setMenBloqCsaSemLeitura(item.getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA) != null && "S".equals(item.getAttribute(Columns.MEN_BLOQ_CSA_SEM_LEITURA).toString()));
                mensagem.setMenPublica(item.getAttribute(Columns.MEN_PUBLICA) != null && "S".equals(item.getAttribute(Columns.MEN_PUBLICA).toString()));
                mensagem.setMenLidaIndividualmente(item.getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE) != null && "S".equals(item.getAttribute(Columns.MEN_LIDA_INDIVIDUALMENTE).toString()));
                mensagem.setMenLida(item.getAttribute("lida") != null && "S".equals(item.getAttribute("lida").toString()));

                response.add(mensagem);
            }
        }

        return response;
    }

}
