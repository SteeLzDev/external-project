package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: RiscoRegistroServidorEnum</p>
 * <p>Description: Enumeração dos riscos do servidor cadastrados pela CSA</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public enum RiscoRegistroServidorEnum {
    
    BAIXISSIMO("0"),
    BAIXO("1"),
    MEDIO("2"),
    ALTO("3"),
    ALTISSIMO("4");
    
    private String codigo;

    private RiscoRegistroServidorEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
    
    public static RiscoRegistroServidorEnum recuperaRisco(String codigo) {
        RiscoRegistroServidorEnum riscoServidor = null;

        for (RiscoRegistroServidorEnum risco : RiscoRegistroServidorEnum.values()) {
            if (risco.getCodigo().equals(codigo)) {
                riscoServidor = risco;
                break;
            }
        }

        if (riscoServidor == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.notificacao.invalido", (AcessoSistema) null));
        }

        return riscoServidor;
    }
    
    public static String recuperaDescricaoRisco(String arrRisco, AcessoSistema responsavel) {
        String risco;
        if(TextHelper.isNull(arrRisco)) {
            risco = "-1";
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.null", responsavel);
        } else {
            risco = arrRisco;
        }
        if(risco.equals(BAIXISSIMO.getCodigo())){
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.baixissimo", responsavel);
        } else if(risco.equals(BAIXO.getCodigo())){
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.baixo", responsavel);
        } else if(risco.equals(MEDIO.getCodigo())){
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.medio", responsavel);                    
        } else if(risco.equals(ALTO.getCodigo())){
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.alto", responsavel);
        } else if(risco.equals(ALTISSIMO.getCodigo())){
            arrRisco = ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa.altissimo", responsavel);                    
        }
        
        return arrRisco;
    }
}
