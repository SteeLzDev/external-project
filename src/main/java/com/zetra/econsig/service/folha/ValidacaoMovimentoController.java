package com.zetra.econsig.service.folha;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ValidacaoMovimentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidacaoMovimentoController</p>
 * <p>Description: Intercafe EJB de controller da Validacao Movimento.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidacaoMovimentoController {

    // Validação de arquivo de movimento financeiro
    public void validarArquivoMovimento(String fileName, List<String> estCodigos, List<String> orgCodigos, AcessoSistema responsavel) throws ConsignanteControllerException;

    // ResultadoValidacaoMov
    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimento(ResultadoValidacaoMovimentoTO resultadoValidacaoMovimento, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException;

    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimento(String rvaCodigo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException;

    public ResultadoValidacaoMovimentoTO findResultadoValidacaoMovimentoByNomeArquivo(String rvaNomeArquivo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException;

    public void updateResultadoValidacaoMovimento(ResultadoValidacaoMovimentoTO resultadoValidacaoMovimento, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException;

    // ResultadoRegraValidMov
    public List<TransferObject> selectResultadoRegras(String rvaCodigo, AcessoSistema responsavel) throws ValidacaoMovimentoControllerException;
}