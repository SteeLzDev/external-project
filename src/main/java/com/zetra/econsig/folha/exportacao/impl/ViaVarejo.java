package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ViaVarejo</p>
 * <p>Description: Implementações específicas para o sistema de Via Varejo.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ViaVarejo extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ViaVarejo.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        Connection conn = null;
        Statement stat = null;
        PreparedStatement preStat = null;
        try {
            SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();

            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            preStat = conn.prepareStatement("update tb_tmp_exportacao set saldo_devedor = ? where ade_codigo = ?");

            String query = "select ade.ade_codigo "
                         + "from tb_tmp_exportacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                         + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                         + "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) "
                         + "where ade.ade_prazo is not null "
                         + "and ade.ade_vlr_liquido is not null "
                         + "and ade.ade_taxa_juros is not null "
                         + "and svc.nse_codigo = '" + CodedValues.NSE_EMPRESTIMO + "'";

            int linhasAfetadas = 0;

            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String adeCodigo = rs.getString("ade_codigo");

                try {
                    // Calcula o saldo devedor dos contratos e atualiza a tabela de exportação
                    BigDecimal saldoDevedor = sdvDelegate.calcularSaldoDevedor(adeCodigo, true, AcessoSistema.getAcessoUsuarioSistema());

                    preStat.setBigDecimal(1, saldoDevedor);
                    preStat.setString(2, adeCodigo);
                    linhasAfetadas += preStat.executeUpdate();

                    if (linhasAfetadas % 1000 == 0) {
                        LOG.debug("Linhas Afetadas: " + linhasAfetadas);
                    }
                } catch (SaldoDevedorControllerException ex) {
                    LOG.error(ex.getMessage());
                }
            }

            LOG.debug("Total linhas Afetadas: " + linhasAfetadas);

        } catch (SQLException | SaldoDevedorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }
}
