package com.zetra.econsig.service.folha;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BlocoProcessamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BlocoProcessamentoController</p>
 * <p>Description: Interface para o session bean BlocoProcessamento</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface BlocoProcessamentoController {

    public List<TransferObject> listarBlocosProcessamentoDashboard(String bprPeriodo, List<String> sbpCodigos, List<String> tbpCodigos, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException;
    
    public int countParcelasRejeitadasPeriodoAtual(Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException;

    public List<TransferObject> listarHistoricoMediaMargem(Date periodoIni, Date periodoFim, String tipoEntidade, String codigoEntidade, Short marCodigo, AcessoSistema responsavel) throws BlocoProcessamentoControllerException;
    
    public Date obterInicioProcessamento(List<String> sbpCodigos, AcessoSistema responsavel) throws BlocoProcessamentoControllerException;

    public List<TransferObject> obterMediaMargemProcessada(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws BlocoProcessamentoControllerException;

}
