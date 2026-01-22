package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoParamValidacaoArqEnum</p>
 * <p>Description: Enumeração do tipo de parâmetro de validação de arquivo.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoParamValidacaoArqEnum {

    MARGEM_EXTENSOES("1", ".extensoes"),
    MARGEM_DIAS_IDADE_MAXIMA_ARQUIVO("2", ".diasIdadeMaximaArquivo"),
    MARGEM_PADRAO_NOME_ARQUIVO_BUSCA("3", ".padraoNomeArquivoBusca"),
    MARGEM_PADRAO_NOME_ARQUIVO_FINAL("4", ".padraoNomeArquivoFinal"),
    MARGEM_QTD_MININA_ARQUIVOS("5", ".qtdMinimaArquivos"),
    MARGEM_QTD_MAXIMA_ARQUIVOS("6", ".qtdMaximaArquivos"),
    MARGEM_QTD_MINIMA_LINHAS("7", ".qtdMinimaLinhas"),
    MARGEM_PERCENTUAL_VAR_POS_QTD_LINHAS("8", ".percentualVarPosQtdLinhas"),
    MARGEM_PERCENTUAL_VAR_NEG_QTD_LINHAS("9", ".percentualVarNegQtdLinhas"),
    MARGEM_QTD_MAXIMA_LINHAS_TAMANHO_INVALIDO("10", ".qtdMaximaLinhasTamanhoInvalido"),
    MARGEM_TAMANHO_MINIMO_LINHA("11", ".tamanhoMinimoLinha"),
    MARGEM_TAMANHO_MAXIMO_LINHA("12", ".tamanhoMaximoLinha"),
    MARGEM_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("49", ".padraoNomeArquivoBuscaExemplo"),
    MARGEM_PADRAO_NOME_ARQUIVO_BUSCA2("53", ".padraoNomeArquivoBusca2"),

    RETORNO_EXTENSOES("13", ".extensoes"),
    RETORNO_DIAS_IDADE_MAXIMA_ARQUIVO("14", ".diasIdadeMaximaArquivo"),
    RETORNO_PADRAO_NOME_ARQUIVO_BUSCA("15", ".padraoNomeArquivoBusca"),
    RETORNO_PADRAO_NOME_ARQUIVO_FINAL("16", ".padraoNomeArquivoFinal"),
    RETORNO_QTD_MININA_ARQUIVOS("17", ".qtdMinimaArquivos"),
    RETORNO_QTD_MAXIMA_ARQUIVOS("18", ".qtdMaximaArquivos"),
    RETORNO_QTD_MINIMA_LINHAS("19", ".qtdMinimaLinhas"),
    RETORNO_PERCENTUAL_VAR_POS_QTD_LINHAS("20", ".percentualVarPosQtdLinhas"),
    RETORNO_PERCENTUAL_VAR_NEG_QTD_LINHAS("21", ".percentualVarNegQtdLinhas"),
    RETORNO_QTD_MAXIMA_LINHAS_TAMANHO_INVALIDO("22", ".qtdMaximaLinhasTamanhoInvalido"),
    RETORNO_TAMANHO_MINIMO_LINHA("23", ".tamanhoMinimoLinha"),
    RETORNO_TAMANHO_MAXIMO_LINHA("24", ".tamanhoMaximoLinha"),
    RETORNO_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("50", ".padraoNomeArquivoBuscaExemplo"),
    RETORNO_PADRAO_NOME_ARQUIVO_BUSCA2("54", ".padraoNomeArquivoBusca2"),

    CRITICA_EXTENSOES("25", ".extensoes"),
    CRITICA_DIAS_IDADE_MAXIMA_ARQUIVO("26", ".diasIdadeMaximaArquivo"),
    CRITICA_PADRAO_NOME_ARQUIVO_BUSCA("27", ".padraoNomeArquivoBusca"),
    CRITICA_PADRAO_NOME_ARQUIVO_FINAL("28", ".padraoNomeArquivoFinal"),
    CRITICA_QTD_MININA_ARQUIVOS("29", ".qtdMinimaArquivos"),
    CRITICA_QTD_MAXIMA_ARQUIVOS("30", ".qtdMaximaArquivos"),
    CRITICA_QTD_MINIMA_LINHAS("31", ".qtdMinimaLinhas"),
    CRITICA_PERCENTUAL_VAR_POS_QTD_LINHAS("32", ".percentualVarPosQtdLinhas"),
    CRITICA_PERCENTUAL_VAR_NEG_QTD_LINHAS("33", ".percentualVarNegQtdLinhas"),
    CRITICA_QTD_MAXIMA_LINHAS_TAMANHO_INVALIDO("34", ".qtdMaximaLinhasTamanhoInvalido"),
    CRITICA_TAMANHO_MINIMO_LINHA("35", ".tamanhoMinimoLinha"),
    CRITICA_TAMANHO_MAXIMO_LINHA("36", ".tamanhoMaximoLinha"),
    CRITICA_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("51", ".padraoNomeArquivoBuscaExemplo"),
    CRITICA_PADRAO_NOME_ARQUIVO_BUSCA2("55", ".padraoNomeArquivoBusca2"),

    TRANSFERIDOS_EXTENSOES("37", ".extensoes"),
    TRANSFERIDOS_DIAS_IDADE_MAXIMA_ARQUIVO("38", ".diasIdadeMaximaArquivo"),
    TRANSFERIDOS_PADRAO_NOME_ARQUIVO_BUSCA("39", ".padraoNomeArquivoBusca"),
    TRANSFERIDOS_PADRAO_NOME_ARQUIVO_FINAL("40", ".padraoNomeArquivoFinal"),
    TRANSFERIDOS_QTD_MININA_ARQUIVOS("41", ".qtdMinimaArquivos"),
    TRANSFERIDOS_QTD_MAXIMA_ARQUIVOS("42", ".qtdMaximaArquivos"),
    TRANSFERIDOS_QTD_MINIMA_LINHAS("43", ".qtdMinimaLinhas"),
    TRANSFERIDOS_PERCENTUAL_VAR_POS_QTD_LINHAS("44", ".percentualVarPosQtdLinhas"),
    TRANSFERIDOS_PERCENTUAL_VAR_NEG_QTD_LINHAS("45", ".percentualVarNegQtdLinhas"),
    TRANSFERIDOS_QTD_MAXIMA_LINHAS_TAMANHO_INVALIDO("46", ".qtdMaximaLinhasTamanhoInvalido"),
    TRANSFERIDOS_TAMANHO_MINIMO_LINHA("47", ".tamanhoMinimoLinha"),
    TRANSFERIDOS_TAMANHO_MAXIMO_LINHA("48", ".tamanhoMaximoLinha"),
    TRANSFERIDOS_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("52", ".padraoNomeArquivoBuscaExemplo"),
    TRANSFERIDOS_PADRAO_NOME_ARQUIVO_BUSCA2("56", ".padraoNomeArquivoBusca2"),
    TRANSFERIDOS_PERCENTUAL_QTD_RSE_ATIVOS_QTD_LINHAS("57", ".percentualRseAtivosQtdLinhas"),

    CONTRACHEQUE_PADRAO_NOME_ARQUIVO_BUSCA("58", ".padraoNomeArquivoBusca"),
    CONTRACHEQUE_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("59", ".padraoNomeArquivoBuscaExemplo"),
    CONTRACHEQUE_EXTENSOES("60", ".extensoes"),

    MARGEMCOMPLEMENTAR_PADRAO_NOME_ARQUIVO_BUSCA("61", ".padraoNomeArquivoBusca"),
    MARGEMCOMPLEMENTAR_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("62", ".padraoNomeArquivoBuscaExemplo"),
    MARGEMCOMPLEMENTAR_EXTENSOES("63", ".extensoes"),

    RETORNOATRASADO_PADRAO_NOME_ARQUIVO_BUSCA("64", ".padraoNomeArquivoBusca"),
    RETORNOATRASADO_PADRAO_NOME_ARQUIVO_BUSCA_EXEMPLO("65", ".padraoNomeArquivoBuscaExemplo"),
    RETORNOATRASADO_EXTENSOES("66", ".extensoes"),

    DESLIGADO_EXTENSOES("67", ".extensoes"),
    BLOQUEIO_SER_EXTENSOES("68", ".extensoes"),
    LOTE_EXTENSOES("69", ".extensoes"),
    HISTORICO_EXTENSOES("70", ".extensoes"),
    PREVIA_FATURAMENTO_BENEFICIOS_EXTENSOES("71", ".extensoes"),

    ;

    private String codigo;
    private String chave;

    private TipoParamValidacaoArqEnum(String codigo, String chave) {
        this.codigo = codigo;
        this.chave = chave;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getChave() {
        return chave;
    }

    /**
     * Recupera um tipo de parâmetro de validação de arquivo de acordo com o código passado.
     *
     * @param codigo Código do tipo de parâmetro de validação de arquivo que deve ser recuperado.
     * @return Retorna um tipo de parâmetro de validação de arquivo
     *
     * @throws IllegalArgumentException Caso o código do tipo de parâmetro de validação de arquivo informádo seja inválido
     */
    public static TipoParamValidacaoArqEnum recuperaTipoArquivo(String codigo) {
        TipoParamValidacaoArqEnum tipoArquivo = null;

        for (TipoParamValidacaoArqEnum tipo : TipoParamValidacaoArqEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoArquivo = tipo;
                break;
            }
        }

        if (tipoArquivo == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.parametro.validacao.invalido", (AcessoSistema) null));
        }

        return tipoArquivo;
    }

}
