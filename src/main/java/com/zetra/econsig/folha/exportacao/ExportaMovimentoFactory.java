package com.zetra.econsig.folha.exportacao;

import com.zetra.econsig.exception.ExportaMovimentoException;

/**
 * <p>Title: ExportaMovimentoFactory</p>
 * <p>Description: Factory para criaçao de classe específica para cada Gestor.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportaMovimentoFactory {

    public static String getClassNameExportador(Class<? extends ExportaMovimento> classe) {
        return classe.getName();
    }

    public static ExportaMovimento getExportador(String className) throws ExportaMovimentoException {
        try {
            Object exportador = Class.forName(className).getDeclaredConstructor().newInstance();
            return (ExportaMovimento) exportador;
        } catch (Exception ex) {
            throw new ExportaMovimentoException(ex);
        }
    }
}
