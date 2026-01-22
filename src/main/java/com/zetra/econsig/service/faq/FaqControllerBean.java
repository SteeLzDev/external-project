package com.zetra.econsig.service.faq;


import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FaqTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.CategoriaFaq;
import com.zetra.econsig.persistence.entity.CategoriaFaqHome;
import com.zetra.econsig.persistence.entity.Faq;
import com.zetra.econsig.persistence.entity.FaqHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.faq.ListaFaqQuery;
import com.zetra.econsig.persistence.query.faq.PesquisaFaqQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FaqControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class FaqControllerBean implements FaqController {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FaqControllerBean.class);

    @Override
    public String createFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException {
        try {
            Short faqSequencia = faq.getFaqSequencia() != null ? faq.getFaqSequencia().shortValue() : null;

            Faq faqBean = FaqHome.create(faq.getUsuCodigo(), Jsoup.parse(faq.getFaqTitulo1()).text(), Jsoup.parse(faq.getFaqTitulo2()).text(), faq.getFaqTexto(),
                    faq.getFaqData(), faqSequencia, faq.getFaqExibeCse(), faq.getFaqExibeOrg(),
                    faq.getFaqExibeCsa(), faq.getFaqExibeCor(), faq.getFaqExibeSer(), faq.getFaqExibeSup(), faq.getFaqExibeMobile(), faq.getCafCodigo());

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setFaq(faqBean.getFaqCodigo());
            logDelegate.write();
            return faqBean.getFaqCodigo();

        } catch (CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaqControllerException("mensagem.erroInternoSistema", responsavel);
        }  catch (LogControllerException ex) {
            throw new FaqControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public FaqTO findFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException {
        return setFaqValues(findFaqBean(faq));
    }

    @Override
    public void updateFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException {
        try {
            Faq faqBean = findFaqBean(faq);
            LogDelegate log = new LogDelegate(responsavel, Log.MENSAGEM, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setFaq(faqBean.getFaqCodigo());
            if (!TextHelper.isNull(faq.getUsuCodigo())) {
                log.setUsuario(faq.getUsuCodigo());
            }

            faq.setFaqTitulo1(Jsoup.parse(faq.getFaqTitulo1()).text());
            faq.setFaqTitulo2(Jsoup.parse(faq.getFaqTitulo2()).text());

            if(faqBean.getFaqHtml().equals("S")){
                if(!TextHelper.isNull(faq.getFaqTexto())){
                    faq.setFaqTexto(Jsoup.parse(faq.getFaqTexto()).text());
                }
                faq.setFaqHtml("N");
            } else {
                faq.setFaqHtml("N");
            }

            /* Compara a versão do cache com a passada por parâmetro */
            FaqTO faqCache = setFaqValues(faqBean);
            CustomTransferObject merge = log.getUpdatedFields(faq.getAtributos(), faqCache.getAtributos());


            if (merge.getAtributos().containsKey(Columns.FAQ_USU_CODIGO)) {
                try {
                    faqBean.setUsuario(UsuarioHome.findByPrimaryKey((String) merge.getAttribute(Columns.FAQ_USU_CODIGO)));
                } catch (FindException e) {
                    faqBean.setUsuario(null);
                }
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_TITULO_1)) {
                faqBean.setFaqTitulo1((String) merge.getAttribute(Columns.FAQ_TITULO_1));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_TITULO_2)) {
                faqBean.setFaqTitulo2((String) merge.getAttribute(Columns.FAQ_TITULO_2));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_TEXTO)) {
                faqBean.setFaqTexto((String) merge.getAttribute(Columns.FAQ_TEXTO));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_DATA)) {
                faqBean.setFaqData((Date) merge.getAttribute(Columns.FAQ_DATA));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_SEQUENCIA)) {
                Short faqSequencia = null;
                if (merge.getAttribute(Columns.FAQ_SEQUENCIA) != null) {
                    faqSequencia = ((Integer)merge.getAttribute(Columns.FAQ_SEQUENCIA)).shortValue();
                }
                faqBean.setFaqSequencia(faqSequencia);
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_CSE)) {
                faqBean.setFaqExibeCse((String) merge.getAttribute(Columns.FAQ_EXIBE_CSE));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_ORG)) {
                faqBean.setFaqExibeOrg((String) merge.getAttribute(Columns.FAQ_EXIBE_ORG));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_CSA)) {
                faqBean.setFaqExibeCsa((String) merge.getAttribute(Columns.FAQ_EXIBE_CSA));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_COR)) {
                faqBean.setFaqExibeCor((String) merge.getAttribute(Columns.FAQ_EXIBE_COR));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_SER)) {
                faqBean.setFaqExibeSer((String) merge.getAttribute(Columns.FAQ_EXIBE_SER));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_SUP)) {
                faqBean.setFaqExibeSup((String) merge.getAttribute(Columns.FAQ_EXIBE_SUP));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_HTML)) {
                faqBean.setFaqHtml((String) merge.getAttribute(Columns.FAQ_HTML));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_EXIBE_MOBILE)) {
                faqBean.setFaqExibeMobile((String) merge.getAttribute(Columns.FAQ_EXIBE_MOBILE));
            }
            if (merge.getAtributos().containsKey(Columns.FAQ_CAF_CODIGO)) {
                faqBean.setCafCodigo((String) merge.getAttribute(Columns.FAQ_CAF_CODIGO));
            }
            FaqHome.update(faqBean);

            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new FaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException {
        try {
            Faq faqBean = findFaqBean(faq);
            String faqCodigo = faqBean.getFaqCodigo();
            FaqHome.remove(faqBean);
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setFaq(faqCodigo);
            logDelegate.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new FaqControllerException("mensagem.erro.nao.possivel.excluir.registro", responsavel);
        }
    }

    @Override
    public int countFaq(CustomTransferObject criterio, AcessoSistema responsavel)throws FaqControllerException {
        try {
            ListaFaqQuery query = new ListaFaqQuery();

            query.count = true;
            if (criterio != null) {
                query.faqExibeCor = (String) criterio.getAttribute(Columns.FAQ_EXIBE_COR);
                query.faqExibeCsa = (String) criterio.getAttribute(Columns.FAQ_EXIBE_CSA);
                query.faqExibeCse = (String) criterio.getAttribute(Columns.FAQ_EXIBE_CSE);
                query.faqExibeOrg = (String) criterio.getAttribute(Columns.FAQ_EXIBE_ORG);
                query.faqExibeSer = (String) criterio.getAttribute(Columns.FAQ_EXIBE_SER);
                query.faqExibeSup = (String) criterio.getAttribute(Columns.FAQ_EXIBE_SUP);
                query.faqTitulo1  = (String) criterio.getAttribute(Columns.FAQ_TITULO_1);
                query.faqTitulo2  = (String) criterio.getAttribute(Columns.FAQ_TITULO_2);
                query.faqExibeMobile  = (String) criterio.getAttribute(Columns.FAQ_EXIBE_MOBILE);
            }

            return query.executarContador();

        } catch (HQueryException ex) {
            throw new FaqControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstFaq(CustomTransferObject criterio, int offset, int rows, AcessoSistema responsavel)throws FaqControllerException {
        try {
            ListaFaqQuery query = new ListaFaqQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (rows != -1) {
                query.maxResults = rows;
            }

            if (criterio != null) {
                query.faqExibeCor = (String) criterio.getAttribute(Columns.FAQ_EXIBE_COR);
                query.faqExibeCsa = (String) criterio.getAttribute(Columns.FAQ_EXIBE_CSA);
                query.faqExibeCse = (String) criterio.getAttribute(Columns.FAQ_EXIBE_CSE);
                query.faqExibeOrg = (String) criterio.getAttribute(Columns.FAQ_EXIBE_ORG);
                query.faqExibeSer = (String) criterio.getAttribute(Columns.FAQ_EXIBE_SER);
                query.faqExibeSup = (String) criterio.getAttribute(Columns.FAQ_EXIBE_SUP);
                query.faqTitulo1  = (String) criterio.getAttribute(Columns.FAQ_TITULO_1);
                query.faqTitulo2  = (String) criterio.getAttribute(Columns.FAQ_TITULO_2);
                query.faqExibeMobile  = (String) criterio.getAttribute(Columns.FAQ_EXIBE_MOBILE);
            }

            return query.executarDTO();

        } catch (HQueryException ex) {
            throw new FaqControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> pesquisaFaq(String usuCodigo, AcessoSistema responsavel, int rows)throws FaqControllerException{
        try {
            PesquisaFaqQuery query = new PesquisaFaqQuery();
            query.responsavel = responsavel;
            query.usuCodigo = usuCodigo;
            if (rows > 0) {
                query.firstResult = 0;
                query.maxResults = rows;
            }

            Markdown4jProcessorExtended processor = new Markdown4jProcessorExtended();

            List<TransferObject> retorno = query.executarDTO();
            for (TransferObject obj : retorno) {
                if (obj.getAttribute(Columns.FAQ_HTML).toString().equals("N")) {
                    obj.setAttribute(Columns.FAQ_TEXTO, processor.process(TextHelper.forHtmlContent(obj.getAttribute(Columns.FAQ_TEXTO).toString())));
                }
            }

            return retorno;

        } catch (HQueryException ex) {
            throw new FaqControllerException(ex);
        } catch (IOException e) {
            throw new FaqControllerException("mensagem.erro.interpretar.texto.faq", responsavel);

        }
    }

    private Faq findFaqBean(FaqTO faq) throws FaqControllerException {
        Faq faqBean = null;
        if (faq.getFaqCodigo() != null) {
            try {
                faqBean = FaqHome.findByPrimaryKey(faq.getFaqCodigo());

                if(faqBean.getFaqHtml().equals("S")){
                    faqBean.setFaqTexto(Jsoup.parse(faqBean.getFaqTexto()).text());
                }

            } catch (FindException ex) {
                throw new FaqControllerException("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null);
            }
        }
        return faqBean;
    }

    private FaqTO setFaqValues(Faq faqBean) {
        FaqTO faq = new FaqTO(faqBean.getFaqCodigo());

        faq.setUsuCodigo(faqBean.getUsuario() != null ? faqBean.getUsuario().getUsuCodigo() : null);
        faq.setFaqTitulo1(faqBean.getFaqTitulo1());
        faq.setFaqTitulo2(faqBean.getFaqTitulo2());
        faq.setFaqTexto(faqBean.getFaqTexto());
        faq.setFaqData(faqBean.getFaqData());
        faq.setFaqSequencia(faqBean.getFaqSequencia() != null ? faqBean.getFaqSequencia().intValue() : null);
        faq.setFaqExibeCse(faqBean.getFaqExibeCse());
        faq.setFaqExibeOrg(faqBean.getFaqExibeOrg());
        faq.setFaqExibeCsa(faqBean.getFaqExibeCsa());
        faq.setFaqExibeCor(faqBean.getFaqExibeCor());
        faq.setFaqExibeSer(faqBean.getFaqExibeSer());
        faq.setFaqExibeSup(faqBean.getFaqExibeSup());
        faq.setFaqHtml(faqBean.getFaqHtml());
        faq.setFaqExibeMobile(faqBean.getFaqExibeMobile());
        faq.setCafCodigo(faqBean.getCafCodigo());
        return faq;
    }

    @Override
    public List<CategoriaFaq> lstCategoriaFaq(AcessoSistema responsavel) throws FaqControllerException{
        try {
            return CategoriaFaqHome.lstCategoriaFaq();
        } catch (FindException e) {
            throw new FaqControllerException("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null);
        }
    }
}
