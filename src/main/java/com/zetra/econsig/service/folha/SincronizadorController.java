package com.zetra.econsig.service.folha;

import com.zetra.econsig.exception.ConsignanteControllerException;

/**
 * <p>Title: SincronizadorController</p>
 * <p>Description: Interface Remote para o Session Façade para Rotina de Sincronização da Folha com o eConsig</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SincronizadorController {

    public void sincronizarFolhaEConsig(String nomeArqEntrada, String ultimoPeriodoRetorno) throws ConsignanteControllerException;

    public String gerarArquivoExclusoes(String caminhoSaida, String ultimoPeriodoRetorno) throws ConsignanteControllerException;

    public void incluirAlteracaoFolha(boolean ajustarInfFolha, String ultimoPeriodoRetorno) throws ConsignanteControllerException;

    public void incluirReimplanteFolha(boolean ajustarInfFolha, String ultimoPeriodoRetorno) throws ConsignanteControllerException;
}
