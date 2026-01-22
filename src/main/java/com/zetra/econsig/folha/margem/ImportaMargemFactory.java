package com.zetra.econsig.folha.margem;

import com.zetra.econsig.exception.ImportaMargemException;

/**
 * <p>Title: ImportaMargemFactory</p>
 * <p>Description: Factory para criaçao de classe de importação de margem específica para cada Gestor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 */
public class ImportaMargemFactory {
    /**
     * Obtém o nome da classe importadora de margem.
     * @param classe Classe de importação
     * @return
     */
    public static String getClassNameImportadorMargem(Class<? extends ImportaMargem> classe) {
        return classe.getName();
    }

    /**
     * Obtém uma instância da classe importadora de margem de acordo com seu nome.
     * @param className Nome da classe.
     * @return
     * @throws ImportaMargemException
     */
    public static ImportaMargem getImportadorMargem(String className) throws ImportaMargemException {
        try {
            Object importadorMargem = Class.forName(className).getDeclaredConstructor().newInstance();
            return (ImportaMargem) importadorMargem;
        } catch (Exception ex) {
            throw new ImportaMargemException(ex);
        }
    }
}
