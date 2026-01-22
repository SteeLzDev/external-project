package com.zetra.econsig.service.totem;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.EventosTotemControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: EventosTotemControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 30365 $
 * $Date: 2020-09-16 10:25:15 -0300 (Qua, 16 set 2020) $
 */
@Service
@Transactional
public class EventosTotemControllerBean extends TotemAbstractControllerBean implements EventosTotemController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EventosTotemControllerBean.class);

    @Override
    public List<TransferObject> listarEventosTotem(TransferObject criterio, AcessoSistema responsavel) throws EventosTotemControllerException {
        return eventosTotem(false, criterio, responsavel);
    }

    @Override
    public int countEventosTotem(TransferObject criterio, AcessoSistema responsavel) throws EventosTotemControllerException {
        List<TransferObject> count = eventosTotem(true, criterio, responsavel);
        return (!count.isEmpty() && !TextHelper.isNull(count)) ? Integer.valueOf(count.get(0).getAttribute("total").toString()) : 0;
    }

    @Override
    public CustomTransferObject buscaDetalheEvento(String evnCodigo, String evnCodigoBiometria, AcessoSistema responsavel) throws EventosTotemControllerException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement= null;

        try {
            conn = conectar(responsavel);

            StringBuilder query = new StringBuilder();
            query.append(" SELECT env.evn_matricula as MATRICULA, env.evn_cpf as CPF, env.evn_data DATA, ");
            query.append(" env.evn_ip_acesso IP, ten.tvn_descricao DESCRICAO, env.evn_foto FOTO, env.evn_codigo_biometria as EVN_CODIGO_BIOMETRIA ");
            query.append(" FROM evento env INNER JOIN tipo_evento ten using (tvn_codigo) ");
            query.append(" WHERE 1=1 ");
            query.append(" and env.evn_codigo in (?,?) ");
            		
            LOG.info(query.toString());
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1, evnCodigo);
            preparedStatement.setString(2, evnCodigoBiometria);
            rs = preparedStatement.executeQuery();

            CustomTransferObject evento = new CustomTransferObject();
            int i = 1;
            while(rs.next()) {
            	if(i == 1) {
            		if (rs.getBlob("FOTO") != null) {
            			Blob blob = rs.getBlob("FOTO");
            			byte[] foto = blob.getBytes(1l, (int) blob.length());
            			evento.setAttribute("FOTOBIOMETRIA", foto);
            		}
            	} else {
            		if (rs.getBlob("FOTO") != null) {
            			Blob blob = rs.getBlob("FOTO");
            			byte[] foto = blob.getBytes(1l, (int) blob.length());
            			evento.setAttribute("FOTO", foto);
            		}
            	}
            	i++;
            	
            	evento.setAttribute("MATRICULA", rs.getString("MATRICULA"));
        		evento.setAttribute("EVN_CODIGO_BIOMETRIA", rs.getString("EVN_CODIGO_BIOMETRIA"));
        		evento.setAttribute("CPF", !TextHelper.isNull(rs.getString("CPF")) ? rs.getString("CPF") : "");
        		evento.setAttribute("DATA", !TextHelper.isNull(rs.getString("DATA")) ? DateHelper.toDateTimeString(DateHelper.parse(rs.getString("DATA"), "yyyy-MM-dd hh:mm:ss")) : null);
        		evento.setAttribute("IP", !TextHelper.isNull(rs.getString("IP")) ? rs.getString("IP") : "");
        		evento.setAttribute("DESCRICAO", rs.getString("DESCRICAO"));
            }

            return evento;

        } catch (SQLException | ParseException | ClassNotFoundException ex) {
            throw new EventosTotemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }

    private List<TransferObject> eventosTotem(boolean count, TransferObject criterio, AcessoSistema responsavel) throws EventosTotemControllerException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = conectar(responsavel);

            String matricula = TextHelper.isNull(criterio.getAttribute("matricula")) ? null : criterio.getAttribute("matricula").toString();
            String matricula2 = TextHelper.isNull(criterio.getAttribute("evn_codigo_biometria")) ? null : criterio.getAttribute("evn_codigo_biometria").toString();
            String cpf = TextHelper.isNull(criterio.getAttribute("cpf")) ? null : criterio.getAttribute("cpf").toString();
            String periodoIni = TextHelper.isNull(criterio.getAttribute("periodoIni")) ? null : criterio.getAttribute("periodoIni").toString();
            String periodoFim = TextHelper.isNull(criterio.getAttribute("periodoFim")) ? null : criterio.getAttribute("periodoFim").toString();
            String vlrPossuiFotoPesquisa = TextHelper.isNull(criterio.getAttribute("vlrPossuiFotoPesquisa")) ? null : criterio.getAttribute("vlrPossuiFotoPesquisa").toString();
            Integer size = TextHelper.isNull(criterio.getAttribute("size")) ? null : (int) criterio.getAttribute("size");
            Integer offset = TextHelper.isNull(criterio.getAttribute("offset")) ? null : (int) criterio.getAttribute("offset");

            StringBuilder query = new StringBuilder();

            if (count) {

                query.append(" select count(*) as total ");
                query.append(" from evento env ");
                query.append(" where 1=1 ");

                if (!TextHelper.isNull(matricula)) {
                    query.append(" and env.evn_matricula = ? ");
                }

                if (!TextHelper.isNull(cpf)) {
                    query.append(" and env.evn_cpf = ? ");
                }

                if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
                    query.append(" and env.evn_data between ? and ? ");
                }

                if (!TextHelper.isNull((vlrPossuiFotoPesquisa))) {
                    if (vlrPossuiFotoPesquisa.equals("1")) {
                        query.append(" and env.evn_foto is not null ");
                    } else {
                        query.append(" and env.evn_foto is null ");
                    }
                }

            } else {

                query.append(" select env.evn_codigo, env.evn_matricula as MATRICULA, env.evn_cpf as CPF, env.evn_data DATA, ");
                query.append(" env.evn_ip_acesso IP, ten.tvn_descricao DESCRICAO, env.evn_foto FOTO, env.evn_codigo_biometria as EVN_CODIGO_BIOMETRIA");
                query.append(" from evento env inner join tipo_evento ten using (tvn_codigo)");
                query.append(" where 1=1 and ten.tvn_codigo NOT IN (17) ");

                if (!TextHelper.isNull(matricula)) {
                    query.append(" and env.evn_matricula = ? ");
                }
                
                if (!TextHelper.isNull(matricula2)) {
                    query.append(" and env.evn_codigo_biometria = ? ");
                }

                if (!TextHelper.isNull(cpf)) {
                    query.append(" and env.evn_cpf = ? ");
                }

                if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
                    query.append(" and env.evn_data between ? and ? ");
                }

                if (!TextHelper.isNull((vlrPossuiFotoPesquisa))) {
                    if (vlrPossuiFotoPesquisa.equals("1")) {
                        query.append(" and env.evn_foto is not null ");
                    } else {
                        query.append(" and env.evn_foto is null ");
                    }
                }

                query.append(" ORDER BY env.evn_data DESC ");

                if (!TextHelper.isNull(size) && !TextHelper.isNull(offset)) {
                    query.append(" LIMIT ? OFFSET ? ");
                }
            }

            LOG.info(query.toString());
            preparedStatement = conn.prepareStatement(query.toString());

            int i = 1;
            if (!TextHelper.isNull(matricula)) {
                preparedStatement.setString(i++, matricula);
            }

            if (!TextHelper.isNull(cpf)) {
                preparedStatement.setString(i++, cpf);
            }

            if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
                preparedStatement.setString(i++, periodoIni);
                preparedStatement.setString(i++, periodoFim);
            }

            if (!count && !TextHelper.isNull(size) && !TextHelper.isNull(offset)) {
                preparedStatement.setInt(i++, size);
                preparedStatement.setInt(i++, offset);
            }

            rs = preparedStatement.executeQuery();

            List<TransferObject> eventos = new ArrayList<TransferObject>();

            if (!count) {

                while (rs.next()) {
                    CustomTransferObject evento = new CustomTransferObject();
                    evento.setAttribute("evn_codigo", rs.getString("evn_codigo"));
                    evento.setAttribute("EVN_CODIGO_BIOMETRIA", rs.getString("EVN_CODIGO_BIOMETRIA"));
                    evento.setAttribute("MATRICULA", rs.getString("MATRICULA"));
                    evento.setAttribute("CPF", !TextHelper.isNull(rs.getString("CPF")) ? rs.getString("CPF") : "");
                    evento.setAttribute("DATA", !TextHelper.isNull(rs.getString("DATA")) ? DateHelper.toDateTimeString(DateHelper.parse(rs.getString("DATA"), "yyyy-MM-dd hh:mm:ss")) : null);
                    evento.setAttribute("IP", !TextHelper.isNull(rs.getString("IP")) ? rs.getString("IP") : "");
                    evento.setAttribute("DESCRICAO", rs.getString("DESCRICAO"));
                    evento.setAttribute("FOTO", rs.getString("FOTO"));

                    eventos.add(evento);
                }

            } else {

                CustomTransferObject evento = new CustomTransferObject();

                if (rs.next()) {
                    evento.setAttribute("total", rs.getInt("total"));
                } else {
                    evento.setAttribute("total", 0);
                }

                eventos.add(evento);
            }

            return eventos;

        } catch (SQLException | ParseException | ClassNotFoundException ex) {
            throw new EventosTotemControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }
}