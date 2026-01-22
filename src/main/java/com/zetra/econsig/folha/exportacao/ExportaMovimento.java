package com.zetra.econsig.folha.exportacao;

import java.io.Serializable;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportaMovimento</p>
 * <p>Description: Interface de definição das classes de exportação.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ExportaMovimento extends Serializable {

    public abstract boolean sobreporExportaMovimentoFinanceiro(AcessoSistema responsavel);

    public abstract String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;


    // Depois das validações básicas e correção do saldo devedor
    public abstract void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Depois de preencher a tb_parcelas_desconto_periodo
    public abstract void preProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Depois de trocar as parcelas criadas no passo anterior para "em processamento".
    // Ou seja, este e o passo anterior são praticamente a mesma coisa, pois as parcelas já são criadas "em processamento"
    public abstract void posProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Antes de gerar a tabela de exportação, após selecionar quais consignações de movimento inicial irão compor o movimento
    public abstract void preProcessaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Antes de consolidar as renegociações (que provavelmente não é usado em lugar nenhum) e gerar o arquivo
    public abstract void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Antes de extrair os dados da tabela de movimento para o arquivo
    public abstract void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Antes de excluir o histórico gera um arquivo de diferenças entre a exportação e a anterior.
    public abstract void gravaArquivoDiferencas(String nomeArqSaidaMov, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Logo após gerar o arquivo
    // Especifica para manipulações do arquivo
    public abstract String posProcessaArqLote(String nomeArqLote, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Logo após posProcessaArqLote
    public abstract void posProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Antes do método que realiza a criação das tabelas usadas no movimento financeiro
    public abstract void preCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;

    // Depois do método que realiza a criação das tabelas usadas no movimento financeiro
    public abstract void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException;
}
