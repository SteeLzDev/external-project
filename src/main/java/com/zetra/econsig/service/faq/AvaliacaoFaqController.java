package com.zetra.econsig.service.faq;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.AvaliacaoFaqTO;
import com.zetra.econsig.exception.AvaliacaoFaqControllerException;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
/**
 * <p>Title: AvaliacaoFaqController</p>
 * <p>Description: Interface para o session bean AvaliacaoFaq</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */

public interface AvaliacaoFaqController {

    public String createAvaliacaoFaq(AvaliacaoFaqTO avaliacaoFaq, AcessoSistema responsavel) throws AvaliacaoFaqControllerException;
    public AvaliacaoFaqTO findAvaliacaoFaq(AvaliacaoFaqTO avaliacaoFaq, AcessoSistema responsavel) throws AvaliacaoFaqControllerException;
    public void updateAvaliacaoFaq(AvaliacaoFaqTO avaliacaoFaq, AcessoSistema responsavel) throws AvaliacaoFaqControllerException;
    public List<TransferObject> pesquisaAvaliacaoFaq(String pesquisa, AcessoSistema responsavel, int rows) throws AvaliacaoFaqControllerException, FaqControllerException;
}
