package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.ImportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaArquivoRetornoOperadoraDAO</p>
 * <p>Description: Interface DAO para o importação de arquivos retorno da operadora</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaArquivoRetornoOperadoraDAO {

    public static final String SQL_INSERT_TEMP_ARQUIVO_OPERADORA 
                       = "INSERT INTO tb_tmp_arquivo_operadora (nome_arquivo, id_linha, operacao, csa_codigo, ben_codigo_contrato, cbe_numero, cbe_data_inicio_vigencia, cbe_data_fim_vigencia, bfc_cpf, mapeada, processada, linha) " 
                       + "VALUES (:nomeArquivo, :numeroLinha, :operacao, :csaCodigo, :benCodigoContrato, :cbeNumero, :cbeDataInicioVigencia, :cbeDataFimVigencia, :bfcCpf, 'N', 'N', :linha) ";

    public void criarTabelaTemporariaArquivoRetorno() throws ImportaArquivosBeneficioControllerException;

    public void deletaTabelaTemporariaArquivoRetorno() throws ImportaArquivosBeneficioControllerException;

    public void realizarInsertTabelaTemporariaArquivoRetorno(String nomeArquivo, int numeroLinha, String operacao, String csaCodigo, String benCodigoContrato, String cbeNumero, String cbeDataInicioVigencia, String cbeDataFimVigencia, String bfcCpf, String linha) throws ImportaArquivosBeneficioControllerException;

    // Metodos responsavel pelo o fluxo de inclusão.
    public void realizarMapeamentoContratosBeneficioOperacaoInclusao() throws ImportaArquivosBeneficioControllerException;

    public List<String> realizarAlteracaoContratosBeneficioOperacaoInclusao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException;

    // Metodos responsavel pelo o fluxo de inclusão com migração.
    public List<String> realizarMapeamentoContratosBeneficioOperacaoInclusaoMigracao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException;

    // Metodos responsavel pelo o fluxo de exclusão.
    public void realizarMapeamentoContratosBeneficioOperacaoExclusao() throws ImportaArquivosBeneficioControllerException;

    public List<String> realizarAlteracaoContratosBeneficioOperacaoExclusao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException;

    // Metodo para buscar as linhas não mapeadas.

    public List<String> geraLinhasNaoMapedasParaCritica(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException;
}
