package com.zetra.econsig.helper.geradoradenumero;

import java.sql.Date;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AdeNumeroHelper</p>
 * <p>Description: Helper Class para geração de ade_numero.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AdeNumeroHelper {

    private static AdeNumeroHelper helper;
    private static GeradorAdeNumero gerador = null;

    static {
        helper = new AdeNumeroHelper();
    }

    private AdeNumeroHelper() {
        // Analisamos se o sistema tem algum parametro habilitado para gerador de adeNumero
        // Caso não tiver vamos usar o genrador Generico
        String geradorClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_GERADOR_ADE_NUMERO, AcessoSistema.getAcessoUsuarioSistema());
        if (!TextHelper.isNull(geradorClassName)) {
            gerador = GeradorAdeNumeroFactory.getGerador(geradorClassName);
        } else {
            gerador = new GeradorAdeNumeroGenerico();
        }
    }

    static public AdeNumeroHelper getInstance() {
        return helper;
    }

    public static Long getNext(String vcoCodigo, Date anoMesIni) {
        return gerador.getNext(vcoCodigo, anoMesIni);
    }
}