package com.zetra.econsig.helper.margem;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ExibeMargem</p>
 * <p>Description: Gerencia como é a exibição da margem disponível.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExibeMargem implements java.io.Serializable {
    private boolean isExibe = false;
    private boolean isSemRestricao = false;

    // TIPOS DISPONÍVEIS DE EXIBIÇÃO DE MARGEM
    public static final Character NAO_EXIBE                  = '0';
    public static final Character EXIBE_ZERO_QUANDO_NEGATIVA = '1';
    public static final Character EXIBE_SEM_RESTRICAO        = '2';

    public ExibeMargem(MargemTO margem, AcessoSistema responsavel) {
        if ((margem != null) && (responsavel != null)) {
            final boolean podeConsultarMargem = responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM) ||
                    (responsavel.isCseSupOrg() && CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo()) && responsavel.temPermissao(CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL));
            if (responsavel.isCse() && podeConsultarMargem) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeCse());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCse());
            } else if (responsavel.isOrg() && podeConsultarMargem) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeOrg());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeOrg());
            } else if (responsavel.isSer()) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeSer());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeSer());
            } else if (responsavel.isCsa() && podeConsultarMargem) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeCsa());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCsa());
            } else  if (responsavel.isCor() && podeConsultarMargem) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeCor());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeCor());
            } else if (responsavel.isSup()) {
                isExibe = !NAO_EXIBE.equals(margem.getMarExibeSup());
                isSemRestricao = EXIBE_SEM_RESTRICAO.equals(margem.getMarExibeSup());
            }
        }
    }

    public boolean isExibeValor() {
        return isExibe;
    }

    public boolean isSemRestricao() {
        return isSemRestricao;
    }
}
