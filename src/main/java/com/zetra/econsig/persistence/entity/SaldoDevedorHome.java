package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: SaldoDevedorHome</p>
 * <p>Description: Classe Home para a entidade SaldoDevedor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaldoDevedorHome extends AbstractEntityHome {

    public static SaldoDevedor findByPrimaryKey(String adeCodigo) throws FindException {
        SaldoDevedor saldoDevedor = new SaldoDevedor();
        saldoDevedor.setAdeCodigo(adeCodigo);
        return find(saldoDevedor, adeCodigo);
    }

    public static SaldoDevedor findArquivadoByPrimaryKey(String adeCodigo) throws FindException {
        HtSaldoDevedor saldoDevedor = new HtSaldoDevedor();
        saldoDevedor.setAdeCodigo(adeCodigo);
        return new SaldoDevedor(find(saldoDevedor, adeCodigo));
    }

    public static SaldoDevedor create(String adeCodigo, Short bcoCodigo, String usuCodigo, BigDecimal sdvValor, BigDecimal sdvValorComDesconto, String sdvAgencia, String sdvConta, Timestamp sdvDataMod, String sdvNomeFavorecido, String sdvCnpj, String sdvNumeroContrato, String sdvLinkBoletoQuitacao) throws CreateException {

        Session session = SessionUtil.getSession();
        SaldoDevedor bean = new SaldoDevedor();
        try {
            Banco banco = null;
            if (bcoCodigo != null) {
                banco = session.getReference(Banco.class, bcoCodigo);
            }

            bean.setAdeCodigo(adeCodigo);
            bean.setBanco(banco);
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setSdvValor(sdvValor);
            bean.setSdvValorComDesconto(sdvValorComDesconto);
            bean.setSdvAgencia(sdvAgencia);
            bean.setSdvConta(sdvConta);
            bean.setSdvDataMod(sdvDataMod);
            bean.setSdvNomeFavorecido(sdvNomeFavorecido);
            bean.setSdvCnpj(sdvCnpj);
            bean.setSdvNumeroContrato(sdvNumeroContrato);
            bean.setSdvLinkBoletoQuitacao(sdvLinkBoletoQuitacao);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
