package com.zetra.econsig.service.formulariopesquisa;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.entity.FormularioPesquisaResposta;
import com.zetra.econsig.persistence.entity.FormularioPesquisaRespostaHome;
import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaQuery;

/**
 * <p>Title: FormularioPesquisaRespostaControllerBean</p>
 * <p>Description: Session Façade para manipulação de formulário de pesquisa resposta</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class FormularioPesquisaRespostaControllerBean implements FormularioPesquisaRespostaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FormularioPesquisaRespostaControllerBean.class);

    public FormularioPesquisaResposta findByPrimaryKey(String fprCodigo) throws FindException {
        return FormularioPesquisaRespostaHome.findByPrimaryKey(fprCodigo);
    }

    public FormularioPesquisaResposta createFormularioPesquisaResposta(FormularioPesquisaResposta formularioPesquisaResposta) throws CreateException {
        return FormularioPesquisaRespostaHome.createFormularioPesquisaResposta(formularioPesquisaResposta);
    }

    public List<TransferObject> countFormularioPesquisaRespostaByFpeCodigo(String fpeCodigo) throws FormularioPesquisaRespostaControllerException {
        try {
            final ListaFormularioPesquisaRespostaQuery query = new ListaFormularioPesquisaRespostaQuery();
            query.fpeCodigo = fpeCodigo;
            query.count = true;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new FormularioPesquisaRespostaControllerException(ex);
        }
    }
}
