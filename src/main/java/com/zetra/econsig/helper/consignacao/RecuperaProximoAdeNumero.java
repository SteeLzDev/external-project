package com.zetra.econsig.helper.consignacao;

import java.text.ParseException;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;


/**
 * <p>Title: RecuperaProximoAdeNumero</p>
 * <p>Description: a ser chamado externamente ao eConsig para recuperar próximo adeNumero de forma sincronizada.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecuperaProximoAdeNumero implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecuperaProximoAdeNumero.class);
    private static final String NOME_CLASSE = RecuperaProximoAdeNumero.class.getName();

    public Long getNexAdeNumero (String vcoCodigo, java.sql.Date adeAnoMesIni) throws AutorizacaoControllerException {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        return adeDelegate.getNextAdeNumero(vcoCodigo, adeAnoMesIni, responsavel);
    }

    @Override
    public int executar(String[] args) {
        if (args.length != 2) {
            LOG.debug("USE: " + NOME_CLASSE + " [VCO_CODIGO] ADE_ANO_MES_INI" +
                    "\nVCO_CODIGO: Código da verba convênio" +
                    "\nADE_ANO_MES_INI: Data inicial do contrato (formato: "+LocaleHelper.getDatePattern()+")" +
                    "\n***** Usar [] para indicar branco nos campos opcionais");
            return -1;
        }

        String vcoCodigo = args[0].substring(1, args[0].length() - 1);
        java.sql.Date anoMesIni = null;
        try {
            anoMesIni = new java.sql.Date(DateHelper.parse(args[1], LocaleHelper.getDatePattern()).getTime());
        } catch (ParseException e) {
            LOG.debug("ADE_ANO_MES_INI: Data inicial do contrato inválido. Formato correto: "+LocaleHelper.getDatePattern()+")");
            return -1;
        }

        long adeNumeroNovo = 0;
        try {
            adeNumeroNovo = getNexAdeNumero(vcoCodigo, anoMesIni);
            LOG.debug("NOVO ADE_NUMERO GERADO: " + adeNumeroNovo);
            return 0;
        } catch (AutorizacaoControllerException e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }
}
