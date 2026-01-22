package com.zetra.econsig.service.sdp;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DespesaComumControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DespesaComumController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface DespesaComumController {

    public String createDespesaComum(TransferObject despesaComum, String carencia, AcessoSistema responsavel) throws DespesaComumControllerException;

    public List<TransferObject> findDespesasComuns(TransferObject criterios, AcessoSistema responsavel) throws DespesaComumControllerException;

    public int countDespesasComuns(TransferObject criterios, AcessoSistema responsavel) throws DespesaComumControllerException;

    public TransferObject findDespesaComum(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException;

    public void cancelarDespesaComum(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException;

    public List<TransferObject> findOcorrencias(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException;
}
