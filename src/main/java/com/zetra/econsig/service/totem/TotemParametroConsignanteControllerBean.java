package com.zetra.econsig.service.totem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.TotemParametroConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: TotemParametroConsignanteControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TotemParametroConsignanteControllerBean extends TotemAbstractControllerBean implements TotemParametroConsignanteController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TotemParametroConsignanteControllerBean.class);

    @Override
    public List<TransferObject> selectParametroConsignanteTotem(List<Integer> tpaCodigos, Integer cseCodigo, AcessoSistema responsavel) throws TotemParametroConsignanteControllerException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = conectar(responsavel);

            StringBuilder query = new StringBuilder();

            query.append("SELECT tpa.tpa_codigo, tpa.tpa_descricao, pce.cse_codigo, pce.rcs_codigo, pce.pce_valor ");
            query.append("FROM tipo_parametro tpa ");
            query.append("LEFT OUTER JOIN parametro_consignante pce ON (pce.tpa_codigo = tpa.tpa_codigo AND pce.cse_codigo = ?) ");
            query.append("WHERE 1=1 ");
            if (tpaCodigos != null && !tpaCodigos.isEmpty()) {
                query.append("AND tpa.tpa_codigo IN (");
                query.append(new String(new char[tpaCodigos.size()]).replace("\0", "?,"));
                query.setLength(Math.max(query.length() - 1, 0));
                query.append(") ");
            }
            query.append("ORDER BY tpa.tpa_codigo");

            preparedStatement = conn.prepareStatement(query.toString());

            preparedStatement.setInt(1, cseCodigo);
            if (tpaCodigos != null && !tpaCodigos.isEmpty()) {
                for (int i=0; i<tpaCodigos.size(); i++) {
                    preparedStatement.setInt(2+i, tpaCodigos.get(i));
                }
            }

            LOG.info(query.toString());
            rs = preparedStatement.executeQuery();

            List<TransferObject> eventos = new ArrayList<TransferObject>();
            while (rs.next()) {
                CustomTransferObject evento = new CustomTransferObject();
                evento.setAttribute("TPA_CODIGO", rs.getInt("tpa_codigo"));
                evento.setAttribute("TPA_DESCRICAO", rs.getString("tpa_descricao"));
                evento.setAttribute("CSE_CODIGO", rs.getInt("cse_codigo"));
                evento.setAttribute("RCS_CODIGO", rs.getInt("rcs_codigo"));
                evento.setAttribute("PCE_VALOR", rs.getString("pce_valor"));

                eventos.add(evento);
            }
            return eventos;

        } catch (SQLException | ClassNotFoundException ex) {
            throw new TotemParametroConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    @Override
    public void updateParametroConsignante(Integer tpaCodigo, Integer cseCodigo, Integer rcsCodigo, String pceValor, AcessoSistema responsavel) throws TotemParametroConsignanteControllerException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = conectar(responsavel);

            String query = "UPDATE parametro_consignante SET pce_valor = ?, rcs_codigo = ? WHERE cse_codigo = ? AND tpa_codigo = ? ";
            LOG.info(query.toString());
            preparedStatement = conn.prepareStatement(query.toString());

            preparedStatement.setString(1, pceValor);
            if (rcsCodigo == null) {
                preparedStatement.setNull(2, Types.INTEGER);
            } else {
                preparedStatement.setInt(2, rcsCodigo);
            }
            preparedStatement.setInt(3, cseCodigo);
            preparedStatement.setInt(4, tpaCodigo);

            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                DBHelper.closeStatement(preparedStatement);
                query = "INSERT INTO parametro_consignante (pce_valor, rcs_codigo, cse_codigo, tpa_codigo) VALUES (?, ?, ?, ?)";
                LOG.info(query.toString());
                preparedStatement = conn.prepareStatement(query.toString());

                preparedStatement.setString(1, pceValor);
                if (rcsCodigo == null) {
                    preparedStatement.setNull(2, Types.INTEGER);
                } else {
                    preparedStatement.setInt(2, rcsCodigo);
                }
                preparedStatement.setInt(3, cseCodigo);
                preparedStatement.setInt(4, tpaCodigo);

                preparedStatement.executeUpdate();
            }

        } catch (SQLException | ClassNotFoundException ex) {
            throw new TotemParametroConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }
}
