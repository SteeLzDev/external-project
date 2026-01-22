package com.zetra.econsig.service.sistema;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TextoSistemaTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.TextoSistema;
import com.zetra.econsig.persistence.entity.TextoSistemaHome;
import com.zetra.econsig.persistence.query.texto.ListaMobileTextoSistemaQuery;
import com.zetra.econsig.persistence.query.texto.ListaTextoSistemaQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.request.TextoSistemaRestRequest;

/**
 * <p>Title: TextoSistemaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TextoSistemaControllerBean implements TextoSistemaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TextoSistemaControllerBean.class);

    @Override
    public int countTextoSistema(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaTextoSistemaQuery query = new ListaTextoSistemaQuery();
            query.count = true;
            if (criterio != null) {
                query.texChave = (String) criterio.getAttribute(Columns.TEX_CHAVE);
                query.texTexto = (String) criterio.getAttribute(Columns.TEX_TEXTO);
                query.texDataAlteracao = (String) criterio.getAttribute(Columns.TEX_DATA_ALTERACAO);
            }
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTextoSistema(CustomTransferObject criterio, int offset, int rows, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaTextoSistemaQuery query = new ListaTextoSistemaQuery();
            query.count = false;
            if (offset != -1) {
                query.firstResult = offset;
            }
            if (rows != -1) {
                query.maxResults = rows;
            }
            if (criterio != null) {
                query.texChave = (String) criterio.getAttribute(Columns.TEX_CHAVE);
                query.texTexto = (String) criterio.getAttribute(Columns.TEX_TEXTO);
                query.texDataAlteracao = (String) criterio.getAttribute(Columns.TEX_DATA_ALTERACAO);
            }
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateTextoSistema(TextoSistemaTO textoSistema, AcessoSistema responsavel) throws ConsignanteControllerException {

        try {
            // Compara a versão do cache com a passada por parâmetro
            TextoSistema textoSistemaBean = findTextoSistemaBean(textoSistema);
            TextoSistemaTO textoSistemaCache = setTextoSistemaValues(textoSistemaBean);
            LogDelegate log = new LogDelegate(responsavel, Log.TEXTO_SISTEMA, Log.UPDATE, Log.LOG_INFORMACAO);
            CustomTransferObject merge = log.getUpdatedFields(textoSistema.getAtributos(), textoSistemaCache.getAtributos());

            // Só faz o update set o texto tiver sido alterado.
            if (merge.getAtributos().containsKey(Columns.TEX_TEXTO)) {
                textoSistemaBean.setTexTexto((String) merge.getAttribute(Columns.TEX_TEXTO));
                // Atualiza data de alteração
                textoSistemaBean.setTexDataAlteracao(DateHelper.getSystemDatetime());

                TextoSistemaHome.update(textoSistemaBean);

                log.add(Columns.TEX_CHAVE, textoSistemaBean.getTexChave());
                log.write();
            }
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }

    }

    @Override
    public TextoSistemaTO findTextoSistema(TextoSistemaTO textoSistema, AcessoSistema responsavel) throws ConsignanteControllerException {
        return setTextoSistemaValues(findTextoSistemaBean(textoSistema));
    }

    private TextoSistemaTO setTextoSistemaValues(TextoSistema textoSistemaBean) {
        TextoSistemaTO textoSistema = new TextoSistemaTO(textoSistemaBean.getTexChave());

        textoSistema.setTexTexto(textoSistemaBean.getTexTexto());
        textoSistema.setTexDataAlteracao(textoSistemaBean.getTexDataAlteracao());

        return textoSistema;
    }

    @Override
    public List<TransferObject> lstMobileTextoSistema(String texChave, Date dataUltimaAlteracao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaMobileTextoSistemaQuery query = new ListaMobileTextoSistemaQuery(texChave, dataUltimaAlteracao);

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private TextoSistema findTextoSistemaBean(TextoSistemaTO textoSistema) throws ConsignanteControllerException {
        TextoSistema textoSistemaBean = new TextoSistema();
        if (textoSistema.getTexChave() != null) {
            try {
                textoSistemaBean = TextoSistemaHome.findByPrimaryKey(textoSistema.getTexChave());
            } catch (FindException e) {
                throw new ConsignanteControllerException("mensagem.erro.nenhuma.mensagem.encontrada", (AcessoSistema) null);
            }
        }
        return textoSistemaBean;
    }

    @Override
    public List<TextoSistema> updateMobileTextoSistema(List<TextoSistemaRestRequest> mensagensMobile, Date texDataAlteracao, AcessoSistema responsavel) throws ConsignanteControllerException {

        List<TextoSistema> mensagensAlteradas = new ArrayList<>();

        List<TransferObject> mensagensSistemaTO = lstMobileTextoSistema(null, null, responsavel);
        List<TextoSistemaRestRequest> mensagensSistema = mensagensSistemaTO.stream().map(o -> {
            String dataAlteracao = !TextHelper.isNull(o.getAttribute(Columns.TEX_DATA_ALTERACAO)) ? DateHelper.format((java.util.Date) o.getAttribute(Columns.TEX_DATA_ALTERACAO), "yyyy-MM-dd HH:mm:ss") : null;
            return new TextoSistemaRestRequest((String) o.getAttribute(Columns.TEX_CHAVE),
                                    ApplicationResourcesHelper.getMessage((String) o.getAttribute(Columns.TEX_CHAVE), responsavel),
                                    dataAlteracao);
        }).collect(Collectors.toList());

        //verifica se alguma mensagem vinda do Mobile está diferente ou não existe no eConsig
        boolean hasMoreMsg = !mensagensMobile.stream().allMatch(mensagensSistema::contains);

        if (!hasMoreMsg) {
            return new ArrayList<>();
        } else {
            mensagensMobile.forEach(o -> {

                //verifico se a chave que chegou segue o padrão de comçecar com mobile.
                if (!o.texChave.startsWith("mobile.")) {
                    LOG.error("A CHAVE NÃO RESPEITA O PADRÃO DE COMEÇAR COM \".mobile\" : " + o.texChave);
                } else {

                    TextoSistemaTO textoSistemaTO = new TextoSistemaTO(o.texChave);

                    try {
                        textoSistemaTO = findTextoSistema(textoSistemaTO, responsavel);
                        if (!textoSistemaTO.getTexTexto().equals(o.texTexto)) {
                            mensagensAlteradas.add(new TextoSistema(textoSistemaTO.getTexChave(), textoSistemaTO.getTexTexto(), textoSistemaTO.getTexDataAlteracao()));
                        }

                    } catch (ConsignanteControllerException e) {
                        //Não encontrou, portante é uma chave nova
                        textoSistemaTO.setTexTexto(o.texTexto);
                        try {
                            LOG.info("Novo TextoSistema: " + textoSistemaTO.getTexChave() + "=" + textoSistemaTO.getTexTexto());
                            TextoSistemaHome.create(textoSistemaTO.getTexChave(), textoSistemaTO.getTexTexto(), texDataAlteracao);
                        } catch (CreateException e1) {
                            LOG.error("ERRO AO FAZER INSERÇÃO DA NOVO TEXTO SISTEMA:" + o.texChave, e1);
                        }
                    }
                }
            });

            AcessoRecursoHelper.reset();
            ApplicationResourcesHelper.getInstance().reset();
        }

        return mensagensAlteradas;
    }

}
