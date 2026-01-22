package com.zetra.econsig.helper.email.modelo;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.BoletoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ModeloEmail;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ModeloEmailInterpolator</p>
 * <p>Description: Interpolador de um modelo de e-mail com os dados informados.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModeloEmailInterpolator {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ModeloEmailInterpolator.class);

    private final ModeloEmail modeloEmail;

    private final AcessoSistema responsavel;

    private final CustomTransferObject dados = new CustomTransferObject();

    public ModeloEmailInterpolator(ModeloEmail modeloEmail, AcessoSistema responsavel) {
         this.modeloEmail = modeloEmail;
         this.responsavel = responsavel;
    }

    public ModeloEmailInterpolator clearDados() {
        dados.removeAll(dados);
        return this;
    }

    public ModeloEmailInterpolator setDados(CustomTransferObject dados) {
        this.dados.setAtributos(dados.getAtributos());
        return this;
    }

    public String interpolateTexto() throws ViewHelperException {
        return interpolate(modeloEmail.getMemTexto());
    }

    public String interpolateTitulo() throws ViewHelperException {
        return interpolate(modeloEmail.getMemTitulo());
    }

    private String interpolate(String modelo) throws ViewHelperException {
        prepararDados();
        if (modelo == null) {
            modelo = "";
        } else if (modelo.indexOf("<@show_informacoes_no_log>") != -1) {
            LOG.info("ModeloEmail = " + modeloEmail);
            LOG.info("Dados = " + dados.toString());
        }
        return ApplicationResourcesHelper.interpolate(BoletoHelper.substituirPadroes(modelo, dados), responsavel);
    }

    private void prepararDados() throws ViewHelperException {
        if (dados.getAttribute("agora") == null) {
            dados.setAttribute("agora", DateHelper.toDateTimeString(DateHelper.getSystemDatetime()));
        }
        if (dados.getAttribute("link_acesso_sistema") == null) {
            dados.setAttribute("link_acesso_sistema", ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel));
        }
        if (dados.getAttribute("nome_sistema") == null) {
            dados.setAttribute("nome_sistema", JspHelper.getNomeSistema(responsavel));
        }
        if (dados.getAttribute("nome_consignante") == null) {
            try {
                dados.setAttribute("nome_consignante", new ConsignanteDelegate().getCseNome(responsavel));
            } catch (ConsignanteControllerException e) {
                throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, e);
            }
        }
        if (responsavel != null) {
            if (dados.getAttribute("responsavel") == null) {
                dados.setAttribute("responsavel", responsavel.getUsuLogin());
            }
            if (dados.getAttribute("responsavel_codigo_entidade") == null) {
                dados.setAttribute("responsavel_codigo_entidade", responsavel.getCodigoEntidade());
            }
            if (dados.getAttribute("responsavel_nome_entidade") == null) {
                dados.setAttribute("responsavel_nome_entidade", responsavel.getNomeEntidade());
            }
        }
    }
}
