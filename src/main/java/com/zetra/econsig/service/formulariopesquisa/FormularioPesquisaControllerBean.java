package com.zetra.econsig.service.formulariopesquisa;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;
import com.zetra.econsig.persistence.entity.FormularioPesquisaHome;
import com.zetra.econsig.persistence.query.formulariopesquisa.BuscaFormularioPesquisaSemRespostaQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaDashQuery;
import com.zetra.econsig.persistence.query.formulariopesquisa.VerificaFormularioRespondidoQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>
 * Title: FormularioPesquisaControllerBean
 * </p>
 * <p>
 * Description: Session Façade para manipulação de formulário de pesquisa
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: ZetraSoft Ltda.
 * </p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class FormularioPesquisaControllerBean implements FormularioPesquisaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory
            .getLog(FormularioPesquisaControllerBean.class);

    @Autowired
    public FormularioPesquisaRespostaController formularioPesquisaRespostaController;

    @Override
    public FormularioPesquisaTO findByPrimaryKey(String fpeCodigo) throws FindException {
        return new FormularioPesquisaTO(FormularioPesquisaHome.findByPrimaryKey(fpeCodigo));
    }

    private FormularioPesquisa find(String fpeCodigo) throws FindException {
        return FormularioPesquisaHome.findByPrimaryKey(fpeCodigo);
    }

    @Override
    public FormularioPesquisaTO createFormularioPesquisa(FormularioPesquisa formularioPesquisa) throws CreateException {
        formularioPesquisa.setFpeDtCriacao(new Date());
        return new FormularioPesquisaTO(FormularioPesquisaHome.createFormularioPesquisa(formularioPesquisa));
    }

    @Override
    public void updateFormularioPesquisa(FormularioPesquisa updated, AcessoSistema responsavel) throws UpdateException,
            FindException, FormularioPesquisaRespostaControllerException, FormularioPesquisaControllerException {
        FormularioPesquisa old = find(updated.getFpeCodigo());
        verificaRespostas(old, responsavel);
        updated.setFpeDtCriacao(old.getFpeDtCriacao());
        FormularioPesquisaHome.updateFormularioPesquisa(updated);
    }

    @Override
    public List<TransferObject> listFormularioPesquisa(TransferObject criterio, int offset, int count,
            AcessoSistema responsavel) throws FormularioPesquisaControllerException {
        try {
            final ListaFormularioPesquisaQuery query = new ListaFormularioPesquisaQuery();
            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.fpeNome = (String) criterio.getAttribute(Columns.FPE_NOME);

                if (criterio.getAttribute(Columns.FPE_PUBLICADO) != null) {
                    query.somentePublicados = (Boolean) criterio.getAttribute(Columns.FPE_PUBLICADO);
                }
            }

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException(ex);
        }
    }

    @Override
    public void deleteFormularioPesquisa(String fpeCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException {
        FormularioPesquisa formularioPesquisa = null;

        try {
            formularioPesquisa = find(fpeCodigo);

            verificaRespostas(formularioPesquisa, responsavel);

            FormularioPesquisaHome.removeFormularioPesquisa(formularioPesquisa);

        } catch (final FindException | FormularioPesquisaRespostaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException("mensagem.erro.encontrar.formulario.pesquisa", responsavel);
        } catch (final RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException("mensagem.erro.exclusao.form.pesquisa", responsavel, formularioPesquisa.getFpeNome());
        }
    }

    private void verificaRespostas(FormularioPesquisa formularioPesquisa, AcessoSistema responsavel)
            throws FormularioPesquisaRespostaControllerException, FormularioPesquisaControllerException {
        if (formularioPesquisa.getFpePublicado()) {
            List<TransferObject> respostas = formularioPesquisaRespostaController
                    .countFormularioPesquisaRespostaByFpeCodigo(formularioPesquisa.getFpeCodigo());
            if (!respostas.isEmpty() && ((Long) respostas.getFirst().getAttribute("total")) > 0) {
                throw new FormularioPesquisaControllerException("mensagem.erro.form.pesquisa.contem.respostas",
                        responsavel, formularioPesquisa.getFpeNome());
            }
        }
    }

    @Override
    public void publicar(String fpeCodigo) throws FindException, UpdateException {
        FormularioPesquisa fpe = find(fpeCodigo);
        fpe.setFpePublicado(true);
        FormularioPesquisaHome.updateFormularioPesquisa(fpe);
    }

    @Override
    public void despublicar(String fpeCodigo, AcessoSistema responsavel) throws FindException, UpdateException,
            FormularioPesquisaRespostaControllerException, FormularioPesquisaControllerException {
        FormularioPesquisa fpe = find(fpeCodigo);
        verificaRespostas(fpe, responsavel);
        fpe.setFpePublicado(false);
        FormularioPesquisaHome.updateFormularioPesquisa(fpe);
    }

    @Override
    public TransferObject findFormularioPesquisaMaisAntigoSemResposta(String usuCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException {
        try {
            final BuscaFormularioPesquisaSemRespostaQuery query = new BuscaFormularioPesquisaSemRespostaQuery();
            query.usuCodigo = usuCodigo;
            List<TransferObject> resultados = query.executarDTO();

            if (resultados.isEmpty()) {
                return null;
            } else {
                return resultados.getFirst();
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException(ex);
        }
    }

    @Override
    public String verificaFormularioParaResponder(String usuCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException {
        try {
            VerificaFormularioRespondidoQuery bean = new VerificaFormularioRespondidoQuery();
            bean.usuCodigo = usuCodigo;
            List<TransferObject> result = bean.executarDTO();

            if (!result.isEmpty()) {
                for (TransferObject obj : result) {
                    boolean publicado = (boolean) obj.getAttribute(Columns.FPE_PUBLICADO);
                    String fprCodigo = obj.getAttribute(Columns.FPR_CODIGO) != null ? (String) obj.getAttribute(Columns.FPR_CODIGO) : null;
                    if (publicado && TextHelper.isNull(fprCodigo)) {
                        return (String) obj.getAttribute(Columns.FPE_CODIGO);
                    }
                }
            } else {
                return null;
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException(ex);
        }
        return null;
    }

    public List<TransferObject> listFormularioPesquisaRespostaDash(String fpeCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException {
        try {
            final ListaFormularioPesquisaRespostaDashQuery query = new ListaFormularioPesquisaRespostaDashQuery();
            query.fpeCodigo = fpeCodigo;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaControllerException(ex);
        }
    }
}
