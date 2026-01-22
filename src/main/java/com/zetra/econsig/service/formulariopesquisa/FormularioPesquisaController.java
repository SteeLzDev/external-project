package com.zetra.econsig.service.formulariopesquisa;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FormularioPesquisaTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.FormularioPesquisaControllerException;
import com.zetra.econsig.exception.FormularioPesquisaRespostaControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.FormularioPesquisa;

/**
 * <p>Title: FormularioPesquisaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface FormularioPesquisaController  {
    public FormularioPesquisaTO findByPrimaryKey(String fpeCodigo) throws FindException;

    public FormularioPesquisaTO createFormularioPesquisa(FormularioPesquisa formularioPesquisa) throws CreateException;

    public void updateFormularioPesquisa(FormularioPesquisa updated, AcessoSistema responsavel) throws UpdateException,
            FindException, FormularioPesquisaRespostaControllerException, FormularioPesquisaControllerException;

    public List<TransferObject> listFormularioPesquisa(TransferObject criterio, int offset, int count, 
         AcessoSistema responsavel) throws FormularioPesquisaControllerException;

    public void deleteFormularioPesquisa(String fpeCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException;

    public void publicar(String fpeCodigo) throws FindException, UpdateException;

    public void despublicar(String fpeCodigo, AcessoSistema responsavel) throws FindException, UpdateException,
            FormularioPesquisaRespostaControllerException, FormularioPesquisaControllerException;

    public TransferObject findFormularioPesquisaMaisAntigoSemResposta(String usuCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException;

    public String verificaFormularioParaResponder(String usuCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException;

    public List<TransferObject> listFormularioPesquisaRespostaDash(String fpeCodigo, AcessoSistema responsavel) throws FormularioPesquisaControllerException;

}