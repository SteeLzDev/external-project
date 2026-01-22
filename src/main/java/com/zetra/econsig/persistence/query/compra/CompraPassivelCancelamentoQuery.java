package com.zetra.econsig.persistence.query.compra;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: CompraPassivelCancelamentoQuery</p>
 * <p>Description: Verifica se uma compra de contratos é passivel de cancelamento.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CompraPassivelCancelamentoQuery extends HQuery {

    public String adeCodigo;
    public boolean isSer;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT COUNT(*)");
        corpoBuilder.append(" FROM RelacionamentoAutorizacao rad");
        corpoBuilder.append(" WHERE rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        // Status não permitidos para a compra ser cancelada
        List<String> stcCodigos = new ArrayList<String>();
        stcCodigos.add(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo());
        stcCodigos.add(StatusCompraEnum.LIQUIDADO.getCodigo());
        stcCodigos.add(StatusCompraEnum.FINALIZADO.getCodigo());

        // Data de pagamento de saldo ou liquidação não podem estar preenchidas
        String datas = "rad.radDataPgtSaldo IS NOT NULL OR rad.radDataLiquidacao IS NOT NULL";

        if (isSer) {
            boolean permiteCancelCompraAposSaldo = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CANCEL_COMPRA_SER_APOS_INF_SALDO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean habilitaEtapaAprSaldoCompra  = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            if (!habilitaEtapaAprSaldoCompra && !permiteCancelCompraAposSaldo) {
                // Se não pode cancelar após o saldo e não tem etapa de aprovação de saldo,
                // então não deixa cancelar na situação AGUARDANDO_PAG_SALDO (Situação normal do sistema)
                stcCodigos.add(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo());
                datas += " OR rad.radDataInfSaldo IS NOT NULL";
            } else if (!habilitaEtapaAprSaldoCompra && permiteCancelCompraAposSaldo) {
                // OK, pode cancelar compra até a situação AGUARDANDO_PAG_SALDO
                // já que não tem etapa de aprovação do saldo. Funciona como os
                // demais usuários do sistema.

            } else if (habilitaEtapaAprSaldoCompra && permiteCancelCompraAposSaldo) {
                // Se tem etapa de aprovação de saldo e permite cancelamento após o saldo
                // não pode permitir o status AGUARDANDO_PAG_SALDO pois a aprovação já foi realizada (AER)
                stcCodigos.add(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo());
                datas += " OR rad.radDataAprSaldo IS NOT NULL";
             
            } else if (habilitaEtapaAprSaldoCompra && !permiteCancelCompraAposSaldo) {
                // Se tem etapa de aprovação e não pode cancelar após o saldo
                // então não deixa cancelar nas etapas AGUARDANDO_APR_SALDO e AGUARDANDO_PAG_SALDO
                stcCodigos.add(StatusCompraEnum.AGUARDANDO_APR_SALDO.getCodigo());                
                stcCodigos.add(StatusCompraEnum.AGUARDANDO_PAG_SALDO.getCodigo());
                datas += " OR rad.radDataInfSaldo IS NOT NULL OR rad.radDataAprSaldo IS NOT NULL";
            }
        } else {
            // Demais usuários a regra é padrão: não pode cancelar compra com
            // pagamento de saldo ou liquidação de contrato
        }

        // Monta a query final: datas não nulas e a situação nos status esperados
        corpoBuilder.append(" AND (").append(datas).append(" OR rad.statusCompra.stcCodigo in ('").append(TextHelper.join(stcCodigos, "','")).append("'))");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        return query;
    }

    /**
     * Gera a cláusula para ser incluída nas demais querys que listam processos de compra
     * para cancelamento pelo não cumprimento dos prazos de cada etapa. 
     * OBS: a query externa onde será adicionada as cláusulas deve ter: adeOrigem, adeDestino e rad
     * @return
     */
    public static String gerarClausulaPendenciaCancelCompra() {
        StringBuilder corpoBuilder = new StringBuilder();
        
        // Não tem pagamento de saldo ou contrato liquidado no processo de compra
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append(" SELECT _rad.adeCodigoDestino");
        corpoBuilder.append(" FROM adeDestino.relacionamentoAutorizacaoByAdeCodigoDestinoSet _rad");
        corpoBuilder.append(" WHERE _rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("'");
        corpoBuilder.append(" AND (_rad.radDataPgtSaldo IS NOT NULL OR _rad.radDataLiquidacao IS NOT NULL OR _rad.statusCompra.stcCodigo in ('");
        corpoBuilder.append(StatusCompraEnum.AGUARDANDO_LIQUIDACAO.getCodigo()).append("', '");
        corpoBuilder.append(StatusCompraEnum.LIQUIDADO.getCodigo()).append("', '");
        corpoBuilder.append(StatusCompraEnum.FINALIZADO.getCodigo()).append("'))");
        corpoBuilder.append(")");

        if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CANCELAMENTO_COMPRA_COM_REJ_PGT, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // E não existe uma rejeição de pagamento de saldo devedor
            corpoBuilder.append(" AND NOT EXISTS (");
            corpoBuilder.append(" SELECT _oca.ocaCodigo");
            corpoBuilder.append(" FROM adeOrigem.ocorrenciaAutorizacaoSet _oca");
            corpoBuilder.append(" WHERE _oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR).append("'");
            corpoBuilder.append(" AND _oca.ocaData > rad.radData");
            corpoBuilder.append(")");
        }

        return corpoBuilder.toString();
    }
}
