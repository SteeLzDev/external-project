package com.zetra.econsig.persistence.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: GenericServidorDAO</p>
 * <p>Description: Implementacao Genérica do DAO de servidor. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericServidorDAO implements ServidorDAO {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericServidorDAO.class);

    /** TODO Pode também ser feito via Hibernate */
    @Override
    public String buscaImgServidor(String serCpf, String rseCodigo) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;
        ResultSet rs = null;
        try {
            String imagem = "";
            StringBuilder query = new StringBuilder();
            query.append("SELECT ims_nome_arquivo ");
            query.append("FROM tb_imagem_servidor, tb_servidor, tb_registro_servidor ");
            query.append("WHERE tb_imagem_servidor.ims_cpf = tb_servidor.ser_cpf AND ");
            query.append("tb_registro_servidor.ser_codigo = tb_servidor.ser_codigo AND ");
            query.append("tb_servidor.ser_cpf = ? AND ");
            query.append("tb_registro_servidor.rse_codigo = ? ");

            conn = DBHelper.makeConnection();

            preStat = conn.prepareStatement(query.toString());
            preStat.setString(1, serCpf);
            preStat.setString(2, rseCodigo);
            rs = preStat.executeQuery();
            if (rs.next()) {
                imagem = rs.getString(1);
            }
            return imagem;
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Baseado no parâmetro de sistema TPC_PRESERVA_EST_MATR_TRANSFER (86) gera os demais filtros
     * a serem adicionados à query de transferência. O parêmetro pode conter os seguintes valores:
     * - MATRICULA / MATRICULA(int)
     * - ESTABELECIMENTO / ESTABELECIMENTO(list)
     * - ORGAO / ORGAO(list)
     * - CPF;E;DATA_ADMISSAO
     * - CPF (* Deve sempre vir sozinho, nunca em combinação com os demais)
     * - Qualquer combinação entre os valores acima, concatenados com ponto-virgula e juntamente com o operador
     *
     * Exemplos:
     * - ESTABELECIMENTO;E;MATRICULA ou MATRICULA;E;ESTABELECIMENTO = EST_MATRICULA (Não é mais utilizado)
     * - ESTABELECIMENTO;E;MATRICULA(5) = Preserva estabelecimento e matrícula apenas nos 5 primeiros caracteres
     * - ORGAO;E;MATRICULA(-2) = Preserva órgão e matrícula ignorando os dois últimos caracteres
     * - MATRICULA;OU;ORGAO('001','002') = Preserva matrí­cula ou não considera a matrí­cula desde que o novo órgão seja 001 ou 002
     *
     * ATENÇÃO: O método tanto para o MySql quanto para o Oracle é o mesmo, portanto cuidado ao alterá-lo.
     * @param preservaEstMatrTrans
     * @param queryParams
     * @return
     * @throws DAOException
     */
    protected String adicionaFiltroTransferencia(String preservaEstMatrTrans, MapSqlParameterSource queryParams) throws DAOException {
        String query = " and (";
        String[] param = preservaEstMatrTrans.split(";");
        for (String element : param) {
            if (element.equalsIgnoreCase("MATRICULA")) {
                // Se preserva matrícula
                query += "exc.rse_matricula = rse.rse_matricula";
            } else if (element.toUpperCase().startsWith("MATRICULA(")) {
                // Se preserva parte da matrícula, ex:
                // MATRICULA(-2) -- IGNORA OS DOIS ULTIMOS CARACTERES
                // MATRICULA(5)  -- COMPARA APENAS OS CINCO PRIMEIROS CARACTERES
                try {
                    int indice = Integer.parseInt(element.substring("MATRICULA(".length(), element.length() - 1));
                    if (indice <= 0) {
                        indice = (indice * -1) + 1;
                        query += "substring(reverse(exc.rse_matricula), :indice, 20) = substring(reverse(rse.rse_matricula), :indice, 20)";
                    } else {
                        query += "substring(exc.rse_matricula, 1, :indice) = substring(rse.rse_matricula, 1, :indice)";
                    }
                    queryParams.addValue("indice", indice);
                } catch (NumberFormatException ex) {
                    throw new DAOException("mensagem.erro.parametro.sistema.preserva.matricula.est.transferencia", (AcessoSistema) null, ex);
                }
            } else if (element.equalsIgnoreCase("ESTABELECIMENTO")) {
                // Se preserva estabelecimento
                query += "orgE.est_codigo = org.est_codigo";
            } else if (element.toUpperCase().startsWith("ESTABELECIMENTO(")) {
                // Se preserva apenas os estabelecimentos informados por parâmetro
                String identificadores = element.substring("ESTABELECIMENTO(".length(), element.length() - 1);
                query += "org.est_codigo in (select est_codigo from tb_estabelecimento where est_identificador in (:identificadores))";
                queryParams.addValue("identificadores", obtemIdentificadores(identificadores));
            } else if (element.equalsIgnoreCase("ORGAO")) {
                // Se preserva órgão
                query += "orgE.org_codigo = org.org_codigo";
            } else if (element.toUpperCase().startsWith("ORGAO(")) {
                // Se preserva apenas os órgãos informados por parâmetro
                String identificadores = element.substring("ORGAO(".length(), element.length() - 1);
                query += "org.org_identificador in (:identificadores)";
                queryParams.addValue("identificadores", obtemIdentificadores(identificadores));
            } else if (element.equalsIgnoreCase("CPF")) {
                // Não preserva nem matrícula, nem estabelecimento e nem órgão, gera transferências
                // apenas pelo cpf do servidor. Neste caso o cpf deve ser unico no sistema.
                // ATENÇÃO: Adiciona a cláusula mesmo sendo redundante para evitar mal configuração do sistema
                // e posteriormente transferências erradas.
                query += "serE.ser_cpf = serT.ser_cpf";
            } else if (element.equalsIgnoreCase("DATA_ADMISSAO")) {
                // Se data de admissao
                query += "exc.rse_data_admissao = rse.rse_data_admissao";
            } else if (element.equalsIgnoreCase("OU")) {
                query += " or ";
            } else if (element.equalsIgnoreCase("E")) {
                query += " and ";
            } else {
                throw new DAOException("mensagem.erro.parametro.sistema.preserva.matricula.est.transferencia.codigo", (AcessoSistema) null);
            }
        }
        query += ")";

        return query;
    }

    /**
     * Remove da tabela de servidores transferidos, com nome "tabelaSerTransferidos", os registros duplicados
     * contidos na "tabelaSerTransferidosDup". Os duplicados representam a mesma matrícula de origem (excluída)
     * que possui mais de uma matrícula de destino (ativos ou bloqueados). As cláusulas para definição da ordenação
     * são:
     * - MATRICULA / MATRICULA(int)
     * - ESTABELECIMENTO / ESTABELECIMENTO(list)
     * - ORGAO / ORGAO(list)
     * - STATUS
     * - CONTRATOS
     * - MARGEM1 / MARGEM2 / MARGEM3
     *
     * Exemplos:
     * - STATUS;CONTRATOS = Dá prioridade a matrículas ativas sobre as bloqueadas, e depois para aqueles com a menor quantidade de contratos
     * - MARGEM1 = Dá prioridade a matrículas com a maior margem 1 restante disponível
     * - ORGAO;MATRICULA(-2) = Dá prioridade á matrícula do mesmo órgão do servidor excluído, e que a matrícula ignorando os dois últimos caracteres seja igual
     * - ORGAO('001','002');MARGEM1 = Dá prioridade á matrícula que seja do órgão '001' ou '002', e depois aquela com maior margem 1
     *
     * ATENÇÃO: O método tanto para o MySql quanto para o Oracle é o mesmo, portanto cuidado ao alterá-lo.
     *
     * @param ordemTrans
     * @param tabelaSerTransferidos
     * @param tabelaSerTransferidosDup
     * @throws DAOException
     */
    protected void removerDuplicadosTransferencia(String ordemTrans, String tabelaSerTransferidos, String tabelaSerTransferidosDup) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        String query = "select rseE.rse_codigo as RSE_EXCLUIDO, rseA.rse_codigo as RSE_ATIVO, rseE.rse_matricula, rseA.rse_matricula,"
                     + " rseA.srs_codigo, rseA.rse_margem, rseA.rse_margem_2, rseA.rse_margem_3,"
                     + " rseA.rse_margem_rest, rseA.rse_margem_rest_2, rseA.rse_margem_rest_3,"
                     + " estE.est_codigo, estA.est_codigo, estE.est_identificador, estA.est_identificador,"
                     + " orgE.org_codigo, orgA.org_codigo, orgE.org_identificador, orgA.org_identificador"
                     + " from " + tabelaSerTransferidos + " str"
                     + " inner join tb_registro_servidor rseE on (rseE.rse_codigo = str.rse_codigo_excluido)"
                     + " inner join tb_orgao orgE on (rseE.org_codigo = orgE.org_codigo)"
                     + " inner join tb_estabelecimento estE on (orgE.est_codigo = estE.est_codigo)"
                     + " inner join tb_registro_servidor rseA on (rseA.rse_codigo = str.rse_codigo_ativo)"
                     + " inner join tb_orgao orgA on (rseA.org_codigo = orgA.org_codigo)"
                     + " inner join tb_estabelecimento estA on (orgA.est_codigo = estA.est_codigo)"
                     + " left outer join tb_aut_desconto ade on (rseA.rse_codigo = ade.rse_codigo and ade.sad_codigo not in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','") + "'))"
                     + " where rseE.rse_codigo in (select t.rse_codigo_excluido from " + tabelaSerTransferidosDup + " t)"
                     + " group by rseE.rse_codigo, rseA.rse_codigo, rseE.rse_matricula, rseA.rse_matricula,"
                     + " rseA.srs_codigo, rseA.rse_margem, rseA.rse_margem_2, rseA.rse_margem_3,"
                     + " rseA.rse_margem_rest, rseA.rse_margem_rest_2, rseA.rse_margem_rest_3,"
                     + " estE.est_codigo, estA.est_codigo, estE.est_identificador, estA.est_identificador,"
                     + " orgE.org_codigo, orgA.org_codigo, orgE.org_identificador, orgA.org_identificador"
                     + " order by rseE.rse_codigo"
                     ;

        String[] param = ordemTrans.split(";");
        for (String element : param) {
            if (element.equalsIgnoreCase("MATRICULA")) {
                // Se dá preferência quando a matrícula for a mesma
                query += ", case when rseE.rse_matricula = rseA.rse_matricula then 1 else 0 end DESC";
            } else if (element.toUpperCase().startsWith("MATRICULA(")) {
                // Se dá preferência quando parte da matrícula for a mesma, ex:
                // MATRICULA(-2) -- IGNORA OS DOIS ULTIMOS CARACTERES
                // MATRICULA(5)  -- COMPARA APENAS OS CINCO PRIMEIROS CARACTERES
                try {
                    int indice = Integer.parseInt(element.substring("MATRICULA(".length(), element.length() - 1));
                    if (indice <= 0) {
                        indice = (indice * -1) + 1;
                        query += ", case when substring(reverse(rseE.rse_matricula), :indice, 20) = substring(reverse(rseA.rse_matricula), :indice, 20) then 1 else 0 end DESC";
                    } else {
                        query += ", case when substring(rseE.rse_matricula, 1, :indice) = substring(rseA.rse_matricula, 1, :indice) then 1 else 0 end DESC";
                    }
                    queryParams.addValue("indice", indice);
                } catch (NumberFormatException ex) {
                    throw new DAOException("mensagem.erro.parametro.sistema.ordem.prioridade.transferidos", (AcessoSistema) null, ex);
                }

            } else if (element.equalsIgnoreCase("ESTABELECIMENTO")) {
                // Se dá preferência quando o estabelecimento for o mesmo
                query += ", case when estE.est_codigo = estA.est_codigo then 1 else 0 end DESC";
            } else if (element.toUpperCase().startsWith("ESTABELECIMENTO(")) {
                // Se dá preferência quando o estabelecimento for o informado por parâmetro
                String identificadores = element.substring("ESTABELECIMENTO(".length(), element.length() - 1);
                query += ", case when estA.est_identificador in (:identificadores) then 1 else 0 end DESC";
                queryParams.addValue("identificadores", obtemIdentificadores(identificadores));

            } else if (element.equalsIgnoreCase("ORGAO")) {
                // Se dá preferência quando o órgão for o mesmo
                query += ", case when orgE.org_codigo = orgA.org_codigo then 1 else 0 end DESC";
            } else if (element.toUpperCase().startsWith("ORGAO(")) {
                // Se dá preferência quando o órgão for o informado por parâmetro
                String identificadores = element.substring("ORGAO(".length(), element.length() - 1);
                query += ", case when orgA.org_identificador in (:identificadores) then 1 else 0 end DESC";
                queryParams.addValue("identificadores", obtemIdentificadores(identificadores));

            } else if (element.equalsIgnoreCase("STATUS")) {
                // Se dá preferência para servidores ativos (1=Ativo, 2=Bloqueado)
                query += ", rseA.srs_codigo ASC";

            } else if (element.equalsIgnoreCase("CONTRATOS")) {
                // Se dá preferência para servidores com menor quantidade de contratos ativos
                query += ", count(ade.ade_codigo) ASC";

            } else if (element.equalsIgnoreCase("MARGEM1")) {
                // Se dá preferência para servidores com maior margem 1 restante
                query += ", coalesce(nullif(rseA.rse_margem_rest, 0), rseA.rse_margem) DESC";

            } else if (element.equalsIgnoreCase("MARGEM2")) {
                // Se dá preferência para servidores com maior margem 2 restante
                query += ", coalesce(nullif(rseA.rse_margem_rest_2, 0), rseA.rse_margem_2) DESC";

            } else if (element.equalsIgnoreCase("MARGEM3")) {
                // Se dá preferência para servidores com maior margem 3 restante
                query += ", coalesce(nullif(rseA.rse_margem_rest_3, 0), rseA.rse_margem_3) DESC";

            } else {
                throw new DAOException("mensagem.erro.parametro.sistema.ordem.prioridade.transferidos.codigo", (AcessoSistema) null);
            }
        }

        // Itera sobre os elementos duplicados, já ordenados de acordo com a configuração do sistema
        // excluindo os registros em duplicidade da tabela de transferências
        Connection conn = null;
        PreparedStatement preStat = null;
        try {
            conn = DBHelper.makeConnection();
            preStat = conn.prepareStatement("delete from " + tabelaSerTransferidos + " where rse_codigo_excluido = ? and rse_codigo_ativo = ?");

            String ultimoRseExc = null;

            LOG.trace(query);
            List<TransferObject> resultado = MySqlGenericDAO.getFieldsValuesList(queryParams, query, "RSE_EXCLUIDO,RSE_ATIVO", ",");
            Iterator<TransferObject> it = resultado.iterator();
            while (it.hasNext()) {
                TransferObject registro = it.next();
                String rseExc = registro.getAttribute("RSE_EXCLUIDO").toString();
                String rseAtv = registro.getAttribute("RSE_ATIVO").toString();

                if (ultimoRseExc != null && ultimoRseExc.equals(rseExc)) {
                    // Exclui a partir do segundo elemento
                    preStat.setString(1, rseExc);
                    preStat.setString(2, rseAtv);
                    preStat.executeUpdate();
                }

                ultimoRseExc = rseExc;
            }

        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);
        }
    }

    // Recebe a string de identificadores no formato '001','002' e retorna uma lista com os elementos
    private static final List<String> obtemIdentificadores(String identificadores) {
        return Arrays.stream(identificadores.split(",")).map(id -> id.trim().substring(1, id.trim().length() - 1)).toList();
    }

    @Override
    public List<TransferObject> obtemServidoresTransferidos(String rseCodigo, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);

        // Verifica se o sistema preserva a matrícula e/ou estabelecimento do servidor na transferência (Default: MATRICULA)
        String preservaEstMatrTrans = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PRESERVA_EST_MATR_TRANSFER, responsavel);
        if (preservaEstMatrTrans == null || preservaEstMatrTrans.equals("")) {
            preservaEstMatrTrans = "MATRICULA";
        }
        LOG.debug("PRESERVA MATRICULA E/OU ESTABELECIMENTO: " + preservaEstMatrTrans);

        // Texto a ser incluído na linha indicando a transferência do servidor
        final String textoTransferido = ApplicationResourcesHelper.getMessage("rotulo.servidor.rse.tipo.transferido", responsavel);
        queryParams.addValue("textoTransferido", textoTransferido);

        final StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" :textoTransferido AS ACAO, ");
        query.append(" estE.est_identificador AS EST_IDENTIFICADOR, ");
        query.append(" orgE.org_identificador AS ORG_IDENTIFICADOR, ");
        query.append(" exc.rse_matricula AS RSE_MATRICULA, ");
        query.append(" serT.ser_nome AS SER_NOME, ");
        query.append(" serT.ser_cpf AS SER_CPF, ");
        query.append(" est.est_codigo AS NOVO_CODIGO_ESTABELECIMENTO, ");
        query.append(" est.est_identificador AS NOVO_ESTABELECIMENTO, ");
        query.append(" org.org_codigo AS NOVO_CODIGO_ORGAO, ");
        query.append(" org.org_identificador AS NOVO_ORGAO, ");
        query.append(" rse.rse_matricula AS NOVA_MATRICULA, ");
        query.append(" NOW() AS DATA_MUDANCA ");

        query.append("FROM tb_registro_servidor rse ");
        query.append("INNER JOIN tb_servidor serT ON (serT.ser_codigo = rse.ser_codigo) ");
        query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");

        query.append("INNER JOIN tb_servidor serE ON (serE.ser_cpf = serT.ser_cpf) ");
        query.append("INNER JOIN tb_registro_servidor exc ON (serE.ser_codigo = exc.ser_codigo) ");
        query.append("INNER JOIN tb_orgao orgE ON (exc.org_codigo = orgE.org_codigo) ");
        query.append("INNER JOIN tb_estabelecimento estE ON (orgE.est_codigo = estE.est_codigo) ");

        query.append("WHERE rse.rse_codigo = :rseCodigo ");
        query.append("  AND rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("  AND exc.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("  AND exc.rse_data_carga IS NULL ");
        query.append(adicionaFiltroTransferencia(preservaEstMatrTrans, queryParams));

        String fieldsNames = "ACAO,EST_IDENTIFICADOR,ORG_IDENTIFICADOR,RSE_MATRICULA,SER_NOME,SER_CPF,NOVO_CODIGO_ESTABELECIMENTO,NOVO_ESTABELECIMENTO,NOVO_CODIGO_ORGAO,NOVO_ORGAO,NOVA_MATRICULA,DATA_MUDANCA";

        LOG.trace(query.toString());
        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, ",");
    }

    /**
     * Utilizado para recuperar a margem do servidor logo após recálculo
     */
    @Override
    public List<TransferObject> buscarMargemServidor(String rseCodigo, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);

        final String fieldsNames = "RSE_CODIGO,RSE_MARGEM,RSE_MARGEM_REST,RSE_MARGEM_USADA,RSE_MARGEM_2,RSE_MARGEM_REST_2,RSE_MARGEM_USADA_2,RSE_MARGEM_3,RSE_MARGEM_REST_3,RSE_MARGEM_USADA_3";
        final StringBuilder query = new StringBuilder();
        query.append("SELECT " + fieldsNames + " ");
        query.append("FROM tb_registro_servidor ");
        query.append("WHERE tb_registro_servidor.rse_codigo = :rseCodigo ");

        LOG.trace(query.toString());
        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, ",");
    }

    /**
     * Utilizado ao criar/atualizar convênios, para configurar o parâmetro de quantidade de contratos
     * por convênio em registros servidores que possuem o campo RSE_PARAM_QTD_ADE_DEFAULT definido
     */
    @Override
    public void setRseQtdAdeDefault(List<String> cnvCodigo) throws DAOException {
        Connection conn = null;
        PreparedStatement preStat = null;
        PreparedStatement preStatUpd = null;

        try {
            if (cnvCodigo == null || cnvCodigo.isEmpty()) {
                return;
            }

            conn = DBHelper.makeConnection();
            String cnvElements = cnvCodigo.stream().map(cnv -> "?").collect(Collectors.joining(", "));
            java.sql.Timestamp sysdate = new java.sql.Timestamp(DateHelper.getSystemDatetime().getTime());

            // Quem cria convênio são os papéis de CSE/ORG e SUP, portanto ao definir o PCR_VLR
            // também deve definir o PCR_VLR_CSE
            StringBuilder query = new StringBuilder();
            query.append("INSERT INTO tb_param_convenio_registro_ser (TPS_CODIGO, CNV_CODIGO, RSE_CODIGO, PCR_VLR, PCR_VLR_CSE, PCR_VLR_CSA, PCR_VLR_SER, PCR_DATA_CADASTRO) ");
            query.append("SELECT ?, cnv.CNV_CODIGO, rse.RSE_CODIGO, rse.RSE_PARAM_QTD_ADE_DEFAULT, rse.RSE_PARAM_QTD_ADE_DEFAULT, NULL, NULL, ? ");
            query.append("FROM tb_registro_servidor rse ");
            query.append("INNER JOIN tb_convenio cnv ON (rse.ORG_CODIGO = cnv.ORG_CODIGO) ");
            query.append("WHERE NULLIF(TRIM(rse.RSE_PARAM_QTD_ADE_DEFAULT), '') IS NOT NULL ");
            query.append("AND cnv.CNV_CODIGO IN (").append(cnvElements).append(") ");
            query.append("AND NOT EXISTS (");
            query.append(" SELECT 1 FROM tb_param_convenio_registro_ser pcr ");
            query.append(" WHERE pcr.RSE_CODIGO = rse.RSE_CODIGO ");
            query.append("   AND pcr.CNV_CODIGO = cnv.CNV_CODIGO ");
            query.append("   AND pcr.TPS_CODIGO = ?");
            query.append(")");

            int i = 1;
            LOG.trace(query.toString());
            preStat = conn.prepareStatement(query.toString());
            preStat.setString(i++, CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            preStat.setTimestamp(i++, sysdate);
            for (String cnv : cnvCodigo) {
                preStat.setString(i++, cnv);
            }
            preStat.setString(i++, CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            preStat.executeUpdate();

            query.setLength(0);
            query.append("UPDATE tb_param_convenio_registro_ser pcr ");
            query.append("SET PCR_DATA_CADASTRO = ?, ");
            query.append("PCR_VLR_SER = NULL, ");
            query.append("PCR_VLR_CSA = NULL, ");
            query.append("PCR_VLR_CSE = (SELECT rse.RSE_PARAM_QTD_ADE_DEFAULT FROM tb_registro_servidor rse WHERE pcr.RSE_CODIGO = rse.RSE_CODIGO), ");
            query.append("PCR_VLR     = (SELECT rse.RSE_PARAM_QTD_ADE_DEFAULT FROM tb_registro_servidor rse WHERE pcr.RSE_CODIGO = rse.RSE_CODIGO) ");
            query.append("WHERE pcr.TPS_CODIGO = ? ");
            query.append("AND EXISTS (");
            query.append(" SELECT 1 FROM tb_registro_servidor rse ");
            query.append(" INNER JOIN tb_convenio cnv ON (rse.ORG_CODIGO = cnv.ORG_CODIGO) ");
            query.append(" WHERE pcr.RSE_CODIGO = rse.RSE_CODIGO ");
            query.append("   AND pcr.CNV_CODIGO = cnv.CNV_CODIGO ");
            query.append("   AND NULLIF(TRIM(rse.RSE_PARAM_QTD_ADE_DEFAULT), '') IS NOT NULL ");
            query.append("   AND cnv.CNV_CODIGO IN (").append(cnvElements).append(") ");
            query.append(")");

            i = 1;
            LOG.trace(query.toString());
            preStatUpd = conn.prepareStatement(query.toString());
            preStatUpd.setTimestamp(i++, sysdate);
            preStatUpd.setString(i++, CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO);
            for (String cnv : cnvCodigo) {
                preStatUpd.setString(i++, cnv);
            }
            preStatUpd.executeUpdate();

        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.closeStatement(preStatUpd);
            DBHelper.releaseConnection(conn);
        }
    }
}
