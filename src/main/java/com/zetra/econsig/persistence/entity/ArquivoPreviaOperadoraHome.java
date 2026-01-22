package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ArquivoPreviaOperadoraHome</p>
 * <p>Description: Classe home para entity ArquivoPreviaOperadora.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoPreviaOperadoraHome extends AbstractEntityHome {

    public static ArquivoPreviaOperadora findByPrimaryKey(Integer apoCodigo) throws FindException {
        ArquivoPreviaOperadora arquivoPreviaOperadora = new ArquivoPreviaOperadora();
        arquivoPreviaOperadora.setApoCodigo(apoCodigo);

        return find(arquivoPreviaOperadora, apoCodigo);
    }

    public static ArquivoPreviaOperadora create(String csaCodigo, String apoNomeArquivo, String apoOperacao,
            Date apoPeriodoFaturamento, Date apoDataInclusao, Date apoDataExclusao, String cbeNumero, String benCodigoRegistro,
            String rseMatricula, String benCodigoContrato, BigDecimal apoValorDebito, String apoTipoLancamento,
            String apoReajusteFaixaEtaria, String apoReajusteAnual, String apoNumeroLote, String apoItemLote,
            BigDecimal apoValorSubsidio, BigDecimal apoValorRealizado, BigDecimal apoValorNaoRealizado,
            BigDecimal apoValorTotal, Date apoPeriodoCobranca) throws CreateException {
        Session session = SessionUtil.getSession();
        ArquivoPreviaOperadora arquivoPreviaOperadora = new ArquivoPreviaOperadora();

        arquivoPreviaOperadora.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
        arquivoPreviaOperadora.setApoNomeArquivo(apoNomeArquivo);
        arquivoPreviaOperadora.setApoOperacao(apoOperacao);
        arquivoPreviaOperadora.setApoPeriodoFaturamento(apoPeriodoFaturamento);
        arquivoPreviaOperadora.setApoDataInclusao(apoDataInclusao);
        arquivoPreviaOperadora.setApoDataExclusao(apoDataExclusao);
        arquivoPreviaOperadora.setCbeNumero(cbeNumero);
        arquivoPreviaOperadora.setBenCodigoRegistro(benCodigoRegistro);
        arquivoPreviaOperadora.setRseMatricula(rseMatricula);
        arquivoPreviaOperadora.setBenCodigoContrato(benCodigoContrato);
        arquivoPreviaOperadora.setApoValorDebito(apoValorDebito);
        arquivoPreviaOperadora.setApoTipoLancamento(apoTipoLancamento);
        arquivoPreviaOperadora.setApoReajusteFaixaEtaria(apoReajusteFaixaEtaria);
        arquivoPreviaOperadora.setApoReajusteAnual(apoReajusteAnual);
        arquivoPreviaOperadora.setApoNumeroLote(apoNumeroLote);
        arquivoPreviaOperadora.setApoItemLote(apoItemLote);
        arquivoPreviaOperadora.setApoValorSubsidio(apoValorSubsidio);
        arquivoPreviaOperadora.setApoValorRealizado(apoValorRealizado);
        arquivoPreviaOperadora.setApoValorNaoRealizado(apoValorNaoRealizado);
        arquivoPreviaOperadora.setApoValorTotal(apoValorTotal);
        arquivoPreviaOperadora.setApoPeriodoCobranca(apoPeriodoCobranca);

        SessionUtil.closeSession(session);

        return create(arquivoPreviaOperadora);
    }
}
