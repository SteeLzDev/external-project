package com.zetra.econsig.service.faq;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.FaqTO;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.CategoriaFaq;
/**
 * <p>Title: FaqController</p>
 * <p>Description: Interface para o session bean Faq</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface FaqController {

    public String createFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException;

    public FaqTO findFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException;

    public void updateFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException;

    public void removeFaq(FaqTO faq, AcessoSistema responsavel) throws FaqControllerException;

    public int countFaq(CustomTransferObject criterio, AcessoSistema responsavel)throws FaqControllerException;

    public List<TransferObject> lstFaq(CustomTransferObject criterio, int offset, int rows, AcessoSistema responsavel)throws FaqControllerException;

    public List<TransferObject> pesquisaFaq(String usuCodigo, AcessoSistema responsavel, int rows)throws FaqControllerException;

    public List<CategoriaFaq> lstCategoriaFaq(AcessoSistema responsavel) throws FaqControllerException;
}
