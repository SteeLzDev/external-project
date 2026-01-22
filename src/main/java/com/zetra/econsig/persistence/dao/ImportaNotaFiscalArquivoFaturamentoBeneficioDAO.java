package com.zetra.econsig.persistence.dao;

import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;

/**
 * <p>Title: ImportaNotaFiscalArquivoFaturamentoBeneficioDAO</p>
 * <p>Description: Interface DAO para o importação de notas ficais para arquivos de faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaNotaFiscalArquivoFaturamentoBeneficioDAO {
    
    void deletarNotasFiscaisAnteriores(String fatCodigo) throws FaturamentoBeneficioControllerException;

    void criarTabelaTemporariaLancamentosCredito(String fatCodigo) throws FaturamentoBeneficioControllerException;

    void criarTabelaTemporariaLancamentosDebito(String fatCodigo) throws FaturamentoBeneficioControllerException;

    void deletaTabelasTemporarias() throws FaturamentoBeneficioControllerException;

    void gerarNotasFiscaisArquivoFaturamentoBeneficioSubsidio(String fatCodigo) throws FaturamentoBeneficioControllerException;

    void gerarNotasFiscaisArquivoFaturamentoBeneficioMcMnc(String fatCodigo) throws FaturamentoBeneficioControllerException;

    void gerarNotasFiscaisArquivoFaturamentoBeneficioCopart(String fatCodigo) throws FaturamentoBeneficioControllerException;

    
}
