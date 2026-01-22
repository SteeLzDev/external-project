package com.zetra.econsig.folha.retorno;

import com.zetra.econsig.exception.ImportaRetornoException;

/**
 * <p>Title: ImportaRetornoFactory</p>
 * <p>Description: Factory para criaçao de classe de importação de retorno específica para cada Gestor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public class ImportaRetornoFactory {
    /**
     * Obtém o nome da classe importadora de retorno.
     * @param classe Classe de importação
     * @return
     */
    public static String getClassNameImportadorRetorno(Class<? extends ImportaRetorno> classe) {
        return classe.getName();
    }

    /**
     * Obtém uma instância da classe importadora de retorno de acordo com seu nome.
     * @param className Nome da classe.
     * @param tipoImportacaoRetorno Tipo de retorno (normal, atrasado, crítica, etc)
     * @param orgCodigo Código do órgão
     * @param estCodigo Código do estabelecimento
     * @return
     * @throws ImportaRetornoException
     */
    public static ImportaRetorno getImportadorRetorno(String className, int tipoImportacaoRetorno, String orgCodigo, String estCodigo) throws ImportaRetornoException {
        try {
            Object importadorRetorno = Class.forName(className).getConstructor(int.class, String.class, String.class).newInstance(tipoImportacaoRetorno, orgCodigo, estCodigo);
            return (ImportaRetorno) importadorRetorno;
        } catch (Exception ex) {
            throw new ImportaRetornoException(ex);
        }
    }
}
