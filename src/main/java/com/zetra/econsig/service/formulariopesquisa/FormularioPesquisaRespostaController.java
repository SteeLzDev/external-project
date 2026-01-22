package com.zetra.econsig.service.formulariopesquisa;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.persistence.entity.FormularioPesquisaResposta;

/**
 * <p>Title: FormularioPesquisaRespostaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface FormularioPesquisaRespostaController  {
    public FormularioPesquisaResposta findByPrimaryKey(String fpeCodigo) throws FindException;

    public FormularioPesquisaResposta createFormularioPesquisaResposta(FormularioPesquisaResposta formularioPesquisaResposta) throws CreateException;

    public List<TransferObject> countFormularioPesquisaRespostaByFpeCodigo(String fpeCodigo) throws FormularioPesquisaRespostaControllerException;
}