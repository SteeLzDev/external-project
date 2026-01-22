package com.zetra.econsig.service.folha;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: SincronizadorControllerBean</p>
 * <p>Description: Session Façade para Rotina de Sincronização da Folha com o eConsig</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class SincronizadorControllerBean implements SincronizadorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SincronizadorControllerBean.class);

    /*
    -- ADICIONAR AO MODELO DE DADOS

    ALTER TABLE tb_aut_desconto
    ADD COLUMN ade_data_ult_conciliacao DATETIME NULL;

    CREATE INDEX IX_DATA_CONCILIACAO on tb_aut_desconto (ade_data_ult_conciliacao);

    UPDATE tb_aut_desconto SET ade_data_ult_conciliacao = NULL;

    -- ANTES DE EXECUTAR, RODAR A INCONSISTÊNCIA "09 - Transferência Servidor.txt"

    -- QUERY PARA CONFERÊNCIA
    select cod_erro, count(*) from tb_tmp_arquivo_conciliacao group by cod_erro;
    */

    // CÓDIGOS DE ERRO
    private static final int CODIGO_ERRO_LINHA_NAO_PROCESSADA                  = -1;
    private static final int CODIGO_OK                                         =  0;
    private static final int CODIGO_ERRO_INF_OBRIGATORIA_AUSENTE               =  1;
    private static final int CODIGO_ERRO_COVENIO_NAO_ENCONTRADO                =  2;
    private static final int CODIGO_ERRO_SERVIDOR_NAO_ENCONTRADO               =  3;
    private static final int CODIGO_ERRO_CONSIGNACAO_NAO_ENCONTRADA            =  4;
    private static final int CODIGO_ERRO_ENCONTRADO_DATA_INCONSISTENTE         =  5;
    private static final int CODIGO_ERRO_ENCONTRADO_VALOR_INCONSISTENTE        =  6;
    private static final int CODIGO_ERRO_ENCONTRADO_DATA_VALOR_INCONSISTENTE   =  7;
    private static final int CODIGO_ERRO_ENCONTRADO_DUPLICADO_DATA_VALOR_IGUAL =  8;
    private static final int CODIGO_ERRO_ENCONTRADO_DUPLICADO_VALOR_IGUAL      =  9;
    private static final int CODIGO_ERRO_ENCONTRADO_DUPLICADO_DATA_IGUAL       = 10;
    private static final int CODIGO_ERRO_ENCONTRADO_DUPLICADO_INCONSISTENTE    = 11;

    private static final String XML_ENTRADA  = "folha_entrada.xml";
    private static final String XML_TRADUTOR = "folha_tradutor.xml";

    private static final String QUERY_REMOVE_TABELA_CARGA = "DROP TABLE IF EXISTS tb_tmp_arquivo_conciliacao";

    private static final String QUERY_CRIA_TABELA_CARGA = "CREATE TABLE tb_tmp_arquivo_conciliacao ( "
        + "nome_arquivo varchar(100) NOT NULL, "
        + "id_linha int(11) NOT NULL, "
        + "linha varchar(500) NOT NULL, "
        + "cod_erro smallint NOT NULL default '0', "
        + "rse_matricula varchar(20), "
        + "ser_cpf varchar(19), "
        + "est_identificador varchar(40), "
        + "org_identificador varchar(40), "
        + "csa_identificador varchar(40), "
        + "svc_identificador varchar(40), "
        + "cnv_cod_verba varchar(32), "
//      + "ade_indice varchar(32), "
//      + "ade_cod_reg char(1), "
        + "ade_ano_mes_ini date, "
        + "ade_ano_mes_fim date, "
        + "ade_prazo int(11), "
//      + "ade_prd_pagas int(11), "
        + "ade_vlr decimal(13,2), "
//      + "ade_numero bigint(20), "
        + "ade_codigo varchar(32), "
        + "PRIMARY KEY (id_linha, nome_arquivo), "
        + "KEY idx_cod_erro (cod_erro), "
        + "KEY idx_org_matricula (org_identificador, rse_matricula), "
        + "KEY idx_matricula (rse_matricula), "
        + "KEY idx_cpf (ser_cpf), "
        + "KEY idx_verba (cnv_cod_verba), "
        + "KEY idx_ade (ade_codigo) "
        + ")";

    private static final String QUERY_INSERE_DADOS_TABELA = "INSERT INTO tb_tmp_arquivo_conciliacao (nome_arquivo, id_linha, linha, cod_erro, rse_matricula, ser_cpf, " +
        "est_identificador, org_identificador, csa_identificador, svc_identificador, cnv_cod_verba, ade_ano_mes_ini, ade_ano_mes_fim, ade_prazo, ade_vlr) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";



    /**
     * Realiza a rotina de sincronização do arquivo enviado pela folha com o eConsig.
     * @param nomeArqEntrada : arquivo contendo a relação de contratos existentes na folha
     * @param ultimoPeriodoRetorno : último período de retorno no formato YYYY-MM-DD
     * @throws ConsignanteControllerException
     */
    @Override
    public void sincronizarFolhaEConsig(String nomeArqEntrada, String ultimoPeriodoRetorno) throws ConsignanteControllerException {
        String dataHoraInicio = DateHelper.format(DateHelper.getSystemDatetime(), "yyyy-MM-dd HH:mm:ss");
        SincronizadorConf conf = new SincronizadorConf();

        // Objetos para conexão com banco de dados
        Connection conn = null;
        Statement stat = null;
        PreparedStatement prep = null;

        try {
            // Verifica se os arquivos de entrada e configuração existem
            File arqEntrada = new File(nomeArqEntrada);
            if (!arqEntrada.exists() || !arqEntrada.canRead()) {
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.arquivo.entrada.nao.encontrado.arg0", (AcessoSistema) null, nomeArqEntrada);
            }
            String nomeXmlEntrada = arqEntrada.getParent() + File.separatorChar + XML_ENTRADA;
            File xmlEntrada = new File(nomeXmlEntrada);
            if (!xmlEntrada.exists() || !xmlEntrada.canRead()) {
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.arquivo.xml.entrada.nao.encontrado.arg0", (AcessoSistema) null, nomeXmlEntrada);
            }
            String nomeXmlTradutor = arqEntrada.getParent() + File.separatorChar + XML_TRADUTOR;
            File xmlTradutor = new File(nomeXmlTradutor);
            if (!xmlTradutor.exists() || !xmlTradutor.canRead()) {
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.arquivo.xml.traducao.nao.encontrado.arg0", (AcessoSistema) null, nomeXmlTradutor);
            }

            int linhasAfetadas = 0;
            int linhasAfetadasTotal = 0;

            // Abre a conexão com a base de dados
            try {
                conn = DBHelper.makeConnection();
                stat = conn.createStatement();
            } catch (SQLException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.fazer.conexao.base.dados", (AcessoSistema) null,  ex);
            }

            // Cria tabela para carga do arquivo de entrada
            try {
                LOG.info(QUERY_REMOVE_TABELA_CARGA);
                stat.executeUpdate(QUERY_REMOVE_TABELA_CARGA);

                LOG.info(QUERY_CRIA_TABELA_CARGA);
                stat.executeUpdate(QUERY_CRIA_TABELA_CARGA);
            } catch (SQLException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.criar.tabela.carga.arquivo.entrada", (AcessoSistema) null,  ex);
            }

            // Prepara statment para inserção dos dados na tabela
            try {
                LOG.info(QUERY_INSERE_DADOS_TABELA);
                prep = conn.prepareStatement(QUERY_INSERE_DADOS_TABELA);
            } catch (SQLException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erro.sincronizador.criar.procedimento.carga.arquivo.entrada", (AcessoSistema) null, ex);
            }

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            Map<String, Object> entrada = new HashMap<>();
            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (nomeArqEntrada.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(nomeXmlEntrada, nomeArqEntrada);
            } else {
                leitor = new LeitorArquivoTexto(nomeXmlEntrada, nomeArqEntrada);
            }

            // Prepara tradução do arquivo
            Escritor escritor = new EscritorMemoria(entrada);
            Tradutor tradutor = new Tradutor(nomeXmlTradutor, leitor, escritor);

            // Inicia tradução do arquivo de entrada ...
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.carga.arquivo.entrada.inicio", (AcessoSistema) null));
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                // e insere os dados da linha na tabela
                linhasAfetadas = inserirDadosTabela(entrada, leitor.getNomeArquivo(), leitor.getLinha(), leitor.getNumeroLinha(), conf, prep, ultimoPeriodoRetorno);
                linhasAfetadasTotal += linhasAfetadas;
                if (linhasAfetadasTotal % 1000 == 0) {
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.carregadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));
                }
            }
            tradutor.encerraTraducao();
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.carga.arquivo.entrada.fim", (AcessoSistema) null));
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.carregadas.total.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));

            // Verifica quais linhas não existe o convênio informado
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.atualiza.linhas.sem.convenios", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasSemConvenio(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            // Verifica quais linhas não existe o servidor informado
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.atualiza.linhas.sem.servidor", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasSemServidor(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            // Atualiza as linhas onde todas as chaves são iguais
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.chaves.iguais", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, true, true, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            // Atualiza as linhas ignorando cada vez mais chaves
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.ignorando.uma.chave.cada.vez", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, true, true, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, true, true, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, true, true, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ignorando.prazo.data.final", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, true, false, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, true, false, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, true, false, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, true, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ignorando.prazo.data.inicial", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, true, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, true, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, true, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, true, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ignorando.data.inicial.data.final", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, false, false, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, false, false, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, false, false, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, true, false, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ignorando.prazo.data.inicial.final", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, false, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, false, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, false, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(true, false, false, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));


            // Verifica quais linhas não existe nenhuma consignação para a verba e servidor
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.sem.consignacao", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasSemConsignacao(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            // Verifica quais linhas que existem duplicações com chaves iguais
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.com.consignacao.duplicada.chaves.iguais", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasDuplicadosChavesIguais(true, true, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));


            // Atualiza as linhas onde as chaves são iguais menos o valor
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.com.chaves.iguais.menos.valor", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(false, true, true, true, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, true, true, true, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, true, true, true, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, true, true, true, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

            // Atualiza as linhas onde as chaves não batem
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linhas.com.chaves.diferentes", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasChavesIguais(false, false, false, false, true, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, false, false, false, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, false, false, false, false, true, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasChavesIguais(false, false, false, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));


            // Verifica quais linhas que existem duplicações com apenas algumas chaves iguais
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.atualiza.linha.consignacoes.duplicadas.chaves.iguais", (AcessoSistema) null));
            linhasAfetadas = atualizarLinhasDuplicadosChavesIguais(true, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasDuplicadosChavesIguais(false, true, true, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = atualizarLinhasDuplicadosChavesIguais(false, false, false, false, conf, stat, dataHoraInicio);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));


            // Realiza ajustes finais
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.realiza.ajustes.finais.codigo.erro", (AcessoSistema) null));
            linhasAfetadas = ajustarCodigoErroContratosPercentuais(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = ajustarCodigoErroContratosPrazoInconsistente(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = ajustarCodigoErroContratosPrazoIndeterminado(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));
            linhasAfetadas = ajustarCodigoErroContratosPrazoAlterado(conf, stat);
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.sincronizador.linhas.afetadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadas)));

        } catch (ParserException ex) {
            throw new ConsignanteControllerException(ex);
        } finally {
            if (prep != null) {
                DBHelper.closeStatement(prep);
            }
            if (stat != null) {
                DBHelper.closeStatement(stat);
            }
            if (conn != null) {
                DBHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Insere os dados da linha, já formatados no mapa "entrada", na tabela de
     * carga para posterior processamento.
     * @param entrada : hash com os dados de entrada
     * @param nomeArquivo : nome do arquivo
     * @param linha   : linha a ser importada
     * @param idLinha : número da linha
     * @param prep : prepared statement para atualização dos dados
     * @param ultimoPeriodoRetorno : último período de retorno no formato YYYY-MM-DD
     * @return
     * @throws ConsignanteControllerException
     */
    private int inserirDadosTabela(Map<String, Object> entrada, String nomeArquivo, String linha, int idLinha, SincronizadorConf conf, PreparedStatement prep, String ultimoPeriodoRetorno) throws ConsignanteControllerException {
        try {
            // Código de erro
            int codErro = CODIGO_ERRO_LINHA_NAO_PROCESSADA;
            // Dados presente no arquivo para localização das consignações
            String cnvCodVerba = (String) entrada.get("CNV_COD_VERBA");
            String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
            String svcIdentificador = (String) entrada.get("SVC_IDENTIFICADOR");
            String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
            String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
            String rseMatricula = (String) entrada.get("RSE_MATRICULA");
            String serCpf = (String) entrada.get("SER_CPF");
            // Dados presente no arquivo para identificação das consignações
            BigDecimal valorFolha = entrada.get("ADE_VLR") != null ? new BigDecimal(entrada.get("ADE_VLR").toString()) : null;
            Integer prazoFolha = entrada.get("ADE_PRAZO") != null ? Integer.valueOf(entrada.get("ADE_PRAZO").toString()) : null;
            Date dataIniFolha = entrada.get("ADE_ANO_MES_INI") != null ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_INI").toString(), "yyyy-MM-dd")) : null;
            Date dataFimFolha = entrada.get("ADE_ANO_MES_FIM") != null ? DateHelper.toSQLDate(DateHelper.parse(entrada.get("ADE_ANO_MES_FIM").toString(), "yyyy-MM-dd")) : null;

            if (dataFimFolha != null && dataFimFolha.compareTo(DateHelper.parse(ultimoPeriodoRetorno, "yyyy-MM-dd")) <= 0) {
                // Se a data fim deste contrato é menor ou igual à data do último período
                // então não registra esta linha, pois não deveria ter sido enviada
                return 0;
            }

            if (prazoFolha == null && dataIniFolha != null && dataFimFolha != null) {
                // Calcula o prazo do contrato, caso não tenha sido enviado
                prazoFolha = PeriodoHelper.getInstance().calcularPrazo(null, dataIniFolha, dataFimFolha, null, AcessoSistema.getAcessoUsuarioSistema());
            }
            if (prazoFolha != null && prazoFolha.intValue() >= 100) {
                // Se maior que 100 prazo é indeterminado
                prazoFolha = null;
                dataFimFolha = null;
            }

            // Verifica as informações mínimas necessárias
            if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.inf.obrigatoria.ausente.linha.matricula.ou.cpf.arg0", (AcessoSistema) null, String.valueOf(idLinha)));
                codErro = CODIGO_ERRO_INF_OBRIGATORIA_AUSENTE;
            }
            if (TextHelper.isNull(cnvCodVerba) && (TextHelper.isNull(csaIdentificador) || TextHelper.isNull(svcIdentificador))) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.inf.obrigatoria.ausente.linha.verba.csa.ser.arg0", (AcessoSistema) null, String.valueOf(idLinha)));
                codErro = CODIGO_ERRO_INF_OBRIGATORIA_AUSENTE;
            }
            if (valorFolha == null && dataIniFolha == null) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.inf.obrigatoria.ausente.linha.valor.data.inicial.arg0", (AcessoSistema) null, String.valueOf(idLinha)));
                codErro = CODIGO_ERRO_INF_OBRIGATORIA_AUSENTE;
            }

            // Flags que indicam se algum identificador foi informado no arquivo
            // para ajudar na otimização das querys
            if (!conf.temIdSvc && !TextHelper.isNull(svcIdentificador)) {
                conf.temIdSvc = true;
            }
            if (!conf.temIdCsa && !TextHelper.isNull(csaIdentificador)) {
                conf.temIdCsa = true;
            }
            if (!conf.temIdOrg && !TextHelper.isNull(orgIdentificador)) {
                conf.temIdOrg = true;
            }
            if (!conf.temIdEst && !TextHelper.isNull(estIdentificador)) {
                conf.temIdEst = true;
            }
            if (!conf.temVerba && !TextHelper.isNull(cnvCodVerba)) {
                conf.temVerba = true;
            }
            if (!conf.temCpf && !TextHelper.isNull(serCpf)) {
                conf.temCpf = true;
            }
            if (!conf.temMatricula && !TextHelper.isNull(rseMatricula)) {
                conf.temMatricula = true;
            }

            prep.setString(1, nomeArquivo);
            prep.setInt(2, idLinha);
            prep.setString(3, linha);
            prep.setInt(4, codErro);

            prep.setString(5, rseMatricula);
            prep.setString(6, serCpf);
            prep.setString(7, estIdentificador);
            prep.setString(8, orgIdentificador);
            prep.setString(9, csaIdentificador);
            prep.setString(10, svcIdentificador);
            prep.setString(11, cnvCodVerba);
            prep.setDate(12, dataIniFolha);
            prep.setDate(13, dataFimFolha);
            prep.setObject(14, prazoFolha);
            prep.setBigDecimal(15, valorFolha);

            return prep.executeUpdate();
        } catch (ParseException ex) {
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.formato.incorreto.campo.data.presente.linha.arquivo.entrada.arg0", (AcessoSistema) null, ex, String.valueOf(idLinha));
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.gravar.dados.linha.arquivo.entrada.tabela.arg0", (AcessoSistema) null, ex, String.valueOf(idLinha));
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.gravar.dados.linha.arquivo.entrada.tabela.arg0", (AcessoSistema) null, ex, String.valueOf(idLinha));
        }
    }

    /**
     * Atualiza o código de erro para as linhas que não possuem convênio no sistema
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int atualizarLinhasSemConvenio(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "UPDATE tb_tmp_arquivo_conciliacao tmp SET tmp.cod_erro = " + CODIGO_ERRO_COVENIO_NAO_ENCONTRADO
                         + " WHERE tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA
                         + " AND NOT EXISTS ("
                         + " SELECT 1 FROM tb_convenio cnv"
                         + (conf.temIdOrg || conf.temIdEst ? " INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo)" : "")
                         + (conf.temIdEst ? " INNER JOIN tb_estabelecimento est on (org.est_codigo = est.est_codigo)" : "")
                         + (conf.temIdCsa ? " INNER JOIN tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)" : "")
                         + (conf.temIdSvc ? " INNER JOIN tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)" : "")
                         + " WHERE 1=1"
                         + (conf.temVerba ? " AND cnv.cnv_cod_verba = tmp.cnv_cod_verba" : "")
                         + (conf.temIdCsa ? " AND csa.csa_identificador = tmp.csa_identificador" : "")
                         + (conf.temIdSvc ? " AND svc.svc_identificador = tmp.svc_identificador" : "")
                         + (conf.temIdOrg ? " AND org.org_identificador = tmp.org_identificador" : "")
                         + (conf.temIdEst ? " AND est.est_identificador = tmp.est_identificador" : "")
                         + ")"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.nao.possuem.convenios", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o código de erro para as linhas que não possuem servidor no sistema
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int atualizarLinhasSemServidor(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "UPDATE tb_tmp_arquivo_conciliacao tmp SET tmp.cod_erro = " + CODIGO_ERRO_SERVIDOR_NAO_ENCONTRADO
                         + " WHERE tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA
                         + " AND NOT EXISTS ("
                         + " SELECT 1 FROM tb_registro_servidor rse"
                         + (conf.temCpf ? " INNER JOIN tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)" : "")
                         + (conf.temIdOrg || conf.temIdEst ? " INNER JOIN tb_orgao org on (rse.org_codigo = org.org_codigo)" : "")
                         + (conf.temIdEst ? " INNER JOIN tb_estabelecimento est on (org.est_codigo = est.est_codigo)" : "")
                         + " WHERE 1=1"
                         + (conf.temMatricula ? " AND rse.rse_matricula = tmp.rse_matricula" : "")
                         + (conf.temCpf ? " AND ser.ser_cpf = tmp.ser_cpf" : "")
                         + (conf.temIdOrg ? " AND org.org_identificador = tmp.org_identificador" : "")
                         + (conf.temIdEst ? " AND est.est_identificador = tmp.est_identificador" : "")
                         + ")"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.nao.possuem.servidor", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o código de erro para as linhas que não possuem servidor no sistema
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int atualizarLinhasSemConsignacao(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "UPDATE tb_tmp_arquivo_conciliacao tmp SET tmp.cod_erro = " + CODIGO_ERRO_CONSIGNACAO_NAO_ENCONTRADA
                         + " WHERE tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA
                         + " AND NOT EXISTS ("
                         + " SELECT 1 FROM tb_aut_desconto ade"
                         + " INNER JOIN tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                         + " INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                         + " INNER JOIN tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo)"
                         + (conf.temCpf ? " INNER JOIN tb_servidor ser on (rse.ser_codigo = ser.ser_codigo)" : "")
                         + (conf.temIdOrg || conf.temIdEst ? " INNER JOIN tb_orgao org on (rse.org_codigo = org.org_codigo and cnv.org_codigo = org.org_codigo)" : "")
                         + (conf.temIdEst ? " INNER JOIN tb_estabelecimento est on (org.est_codigo = est.est_codigo)" : "")
                         + (conf.temIdCsa ? " INNER JOIN tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo)" : "")
                         + (conf.temIdSvc ? " INNER JOIN tb_servico svc on (cnv.svc_codigo = svc.svc_codigo)" : "")
                         + " WHERE 1=1"
                         + (conf.temMatricula  ? " AND rse.rse_matricula = tmp.rse_matricula" : "")
                         + (conf.temCpf ? " AND ser.ser_cpf = tmp.ser_cpf" : "")
                         + (conf.temIdOrg ? " AND org.org_identificador = tmp.org_identificador" : "")
                         + (conf.temIdEst ? " AND est.est_identificador = tmp.est_identificador" : "")
                         + (conf.temIdCsa ? " AND csa.csa_identificador = tmp.csa_identificador" : "")
                         + (conf.temIdSvc ? " AND svc.svc_identificador = tmp.svc_identificador" : "")
                         + (conf.temVerba ? " AND cnv.cnv_cod_verba = tmp.cnv_cod_verba" : "")
                         + ")"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.nao.possuem.consignacoes", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o código de erro e o código da ADE para os contratos que estão
     * com todas as chaves iguais e não há duplicidade, ou seja, só existe um
     * contrato no sistema para um contrato no arquivo.
     * @param vfValor    : Verifica chave de valor do contrato
     * @param vfPrazo    : Verifica chave de prazo do contrato
     * @param vfDataIni  : Verifica chave de data inicial do contrato
     * @param vfDataFim  : Verifica chave de data final do contrato
     * @param vfStatus   : Verifica chave de status do contrato (status ativo)
     * @param vfPagas    : Verifica chave de pagas do contrato (pagas > 0, pois a folha conhece o contrato)
     * @param stat       : statement para atualização dos dados
     * @param dataHoraInicio : data do início do processamento
     * @return
     * @throws ConsignanteControllerException
     */
    private int atualizarLinhasChavesIguais(boolean vfValor, boolean vfPrazo, boolean vfDataIni, boolean vfDataFim, boolean vfStatus, boolean vfPagas, SincronizadorConf conf, Statement stat, String dataHoraInicio) throws ConsignanteControllerException {
        try {
            int linhasAfetadas = 0;
            int linhasAfetadasTotal = 0;
            int codigo = ((!vfDataIni || !vfDataFim || !vfPrazo) && !vfValor) ? CODIGO_ERRO_ENCONTRADO_DATA_VALOR_INCONSISTENTE
                       : (!vfDataIni || !vfDataFim || !vfPrazo) ? CODIGO_ERRO_ENCONTRADO_DATA_INCONSISTENTE
                       : (!vfValor) ? CODIGO_ERRO_ENCONTRADO_VALOR_INCONSISTENTE
                       : CODIGO_OK;

            String query = null;

            query = "drop temporary table if exists tmp_contratos_conciliacao_duplicados";
            LOG.info(query);
            stat.executeUpdate(query);

            query = "create temporary table tmp_contratos_conciliacao_duplicados (id_linha int, primary key (id_linha)) "
                  + "select tmp.id_linha "
                  + "from tb_tmp_arquivo_conciliacao tmp "
                  + (conf.temMatricula ? "inner join tb_registro_servidor rse on (tmp.rse_matricula = rse.rse_matricula) " : "")
                  + (conf.temCpf && conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf and rse.ser_codigo = ser.ser_codigo) " : "")
                  + (conf.temCpf && !conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf) inner join tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo) " : "")
                  + (conf.temIdOrg ? "inner join tb_orgao org on (tmp.org_identificador = org.org_identificador and rse.org_codigo = org.org_codigo) " : "")
                  + (conf.temVerba ? "inner join tb_convenio cnv on (tmp.cnv_cod_verba = cnv.cnv_cod_verba and cnv.org_codigo = rse.org_codigo) " : "inner join tb_convenio cnv on (cnv.org_codigo = rse.org_codigo) ")
                  + (conf.temIdCsa ? "inner join tb_consignataria csa on (tmp.csa_identificador = csa.csa_identificador and cnv.csa_codigo = csa.csa_codigo)" : "")
                  + (conf.temIdSvc ? "inner join tb_servico svc on (tmp.svc_identificador = svc.svc_identificador and cnv.svc_codigo = svc.svc_codigo)" : "")
                  + "inner join tb_verba_convenio vco on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo and vco.vco_codigo = ade.vco_codigo) "
                  + "where tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA + " "
                  + "and tmp.ade_codigo is null "
                  + "and (ade.ade_data_ult_conciliacao is null or ade.ade_data_ult_conciliacao < '" + dataHoraInicio + "') "
                  + (vfValor ?   "and (ade.ade_vlr = tmp.ade_vlr or ade.ade_vlr_folha = tmp.ade_vlr) " : "")
                  + (vfPrazo ?   "and coalesce(ade.ade_prazo, 99999) = coalesce(tmp.ade_prazo, 99999) " : "")
                  + (vfDataIni ? "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) = tmp.ade_ano_mes_ini " : "")
                  + (vfDataFim ? "and coalesce(ade.ade_ano_mes_fim, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01') " : "")
                  + (vfStatus ?  "and ade.sad_codigo not in ('3','7','8','9') " : "")
                  + (vfPagas ?   "and coalesce(ade.ade_prd_pagas, 0) > 0 " : "")
                  + "group by tmp.id_linha "
                  + "having count(*) > 1 "
                  ;
            LOG.info(query);
            stat.executeUpdate(query);

            query = "update tb_tmp_arquivo_conciliacao tmp "
                  + (conf.temMatricula ? "inner join tb_registro_servidor rse on (tmp.rse_matricula = rse.rse_matricula) " : "")
                  + (conf.temCpf && conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf and rse.ser_codigo = ser.ser_codigo) " : "")
                  + (conf.temCpf && !conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf) inner join tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo) " : "")
                  + (conf.temIdOrg ? "inner join tb_orgao org on (tmp.org_identificador = org.org_identificador and rse.org_codigo = org.org_codigo) " : "")
                  + (conf.temVerba ? "inner join tb_convenio cnv on (tmp.cnv_cod_verba = cnv.cnv_cod_verba and cnv.org_codigo = rse.org_codigo) " : "inner join tb_convenio cnv on (cnv.org_codigo = rse.org_codigo) ")
                  + (conf.temIdCsa ? " inner join tb_consignataria csa on (tmp.csa_identificador = csa.csa_identificador and cnv.csa_codigo = csa.csa_codigo)" : "")
                  + (conf.temIdSvc ? " inner join tb_servico svc on (tmp.svc_identificador = svc.svc_identificador and cnv.svc_codigo = svc.svc_codigo)" : "")
                  + "inner join tb_verba_convenio vco on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo and vco.vco_codigo = ade.vco_codigo) "
                  + "set tmp.ade_codigo = ade.ade_codigo, "
                  + "tmp.cod_erro = " + codigo + ", "
                  + "ade.ade_data_ult_conciliacao = now() "
                  + "where tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA + " "
                  + "and tmp.ade_codigo is null "
                  + "and (ade.ade_data_ult_conciliacao is null or ade.ade_data_ult_conciliacao < '" + dataHoraInicio + "') "
                  + (vfValor ?   "and (ade.ade_vlr = tmp.ade_vlr or ade.ade_vlr_folha = tmp.ade_vlr) " : "")
                  + (vfPrazo ?   "and coalesce(ade.ade_prazo, 99999) = coalesce(tmp.ade_prazo, 99999) " : "")
                  + (vfDataIni ? "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) = tmp.ade_ano_mes_ini " : "")
                  + (vfDataFim ? "and coalesce(ade.ade_ano_mes_fim, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01') " : "")
                  + (vfStatus ?  "and ade.sad_codigo not in ('3','7','8','9') " : "")
                  + (vfPagas ?   "and coalesce(ade.ade_prd_pagas, 0) > 0 " : "")
                  + "and not exists (select 1 from tmp_contratos_conciliacao_duplicados tmp2 where tmp.id_linha = tmp2.id_linha) "
                  ;
            LOG.info(query);
            linhasAfetadasTotal = stat.executeUpdate(query);

            query = "drop temporary table if exists tmp_contratos_conciliacao_duplicados_2";
            LOG.info(query);
            stat.executeUpdate(query);

            query = "create temporary table tmp_contratos_conciliacao_duplicados_2 (ade_codigo varchar(32), primary key (ade_codigo))"
                  + "select ade_codigo "
                  + "from tb_tmp_arquivo_conciliacao "
                  + "where ade_codigo is not null "
                  + "group by ade_codigo "
                  + "having count(*) > 1";
            LOG.info(query);
            stat.executeUpdate(query);

            query = "update tb_tmp_arquivo_conciliacao tmp "
                  + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                  + "set tmp.ade_codigo = null, "
                  + "tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA + ", "
                  + "ade.ade_data_ult_conciliacao = null "
                  + "where ade.ade_codigo in (select ade_codigo from tmp_contratos_conciliacao_duplicados_2)";
            LOG.info(query);
            linhasAfetadas = stat.executeUpdate(query);

            return linhasAfetadasTotal - linhasAfetadas;
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.todas.chaves.iguais", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza as linhas que as chaves batem, porém há uma duplicação de contratos
     * para cada linha, o que inviabiliza identificação correta do contrato.
     * OBS: A data de conciliação é alterada para evitar que estes contratos sejam
     * atualizados em outras etapas.
     * @param vfValor    : Verifica chave de valor do contrato
     * @param vfPrazo    : Verifica chave de prazo do contrato
     * @param vfDataIni  : Verifica chave de data inicial do contrato
     * @param vfDataFim  : Verifica chave de data final do contrato
     * @param stat       : statement para atualização dos dados
     * @param dataHoraInicio : data do início do processamento
     * @return
     * @throws ConsignanteControllerException
     */
    private int atualizarLinhasDuplicadosChavesIguais(boolean vfValor, boolean vfPrazo, boolean vfDataIni, boolean vfDataFim, SincronizadorConf conf, Statement stat, String dataHoraInicio) throws ConsignanteControllerException {
        try {
            int codigo = ((!vfDataIni || !vfDataFim || !vfPrazo) && !vfValor) ? CODIGO_ERRO_ENCONTRADO_DUPLICADO_INCONSISTENTE
                       : (!vfDataIni || !vfDataFim || !vfPrazo) ? CODIGO_ERRO_ENCONTRADO_DUPLICADO_VALOR_IGUAL
                       : (!vfValor) ? CODIGO_ERRO_ENCONTRADO_DUPLICADO_DATA_IGUAL
                       : CODIGO_ERRO_ENCONTRADO_DUPLICADO_DATA_VALOR_IGUAL;

            String query = "update tb_tmp_arquivo_conciliacao tmp "
                        + (conf.temMatricula ? "inner join tb_registro_servidor rse on (tmp.rse_matricula = rse.rse_matricula) " : "")
                        + (conf.temCpf && conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf and rse.ser_codigo = ser.ser_codigo) " : "")
                        + (conf.temCpf && !conf.temMatricula  ? "inner join tb_servidor ser on (tmp.ser_cpf = ser.ser_cpf) inner join tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo) " : "")
                        + (conf.temIdOrg ? "inner join tb_orgao org on (tmp.org_identificador = org.org_identificador and rse.org_codigo = org.org_codigo) " : "")
                        + (conf.temVerba ? "inner join tb_convenio cnv on (tmp.cnv_cod_verba = cnv.cnv_cod_verba and cnv.org_codigo = rse.org_codigo) " : "inner join tb_convenio cnv on (cnv.org_codigo = rse.org_codigo) ")
                        + (conf.temIdCsa ? " inner join tb_consignataria csa on (tmp.csa_identificador = csa.csa_identificador and cnv.csa_codigo = csa.csa_codigo)" : "")
                        + (conf.temIdSvc ? " inner join tb_servico svc on (tmp.svc_identificador = svc.svc_identificador and cnv.svc_codigo = svc.svc_codigo)" : "")
                        + "inner join tb_verba_convenio vco on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo and vco.vco_codigo = ade.vco_codigo) "
                        + "set tmp.cod_erro = " + codigo + ", "
                        + "ade.ade_data_ult_conciliacao = now() "
                        + "where tmp.cod_erro = " + CODIGO_ERRO_LINHA_NAO_PROCESSADA + " "
                        + "and tmp.ade_codigo is null "
                        + "and (ade.ade_data_ult_conciliacao is null or ade.ade_data_ult_conciliacao < '" + dataHoraInicio + "') "
                        + (vfValor ?   "and (ade.ade_vlr = tmp.ade_vlr or ade.ade_vlr_folha = tmp.ade_vlr) " : "")
                        + (vfPrazo ?   "and coalesce(ade.ade_prazo, 99999) = coalesce(tmp.ade_prazo, 99999) " : "")
                        + (vfDataIni ? "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) = tmp.ade_ano_mes_ini " : "")
                        + (vfDataFim ? "and coalesce(ade.ade_ano_mes_fim, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01') " : "")
                        ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.todas.chaves.iguais", (AcessoSistema) null, ex);
        }
    }

    /**
     * CASOS ONDE O VALOR ESTÁ INCONSISTENTE PORÉM SÃO DE VALOR PERCENTUAL: IGNORAR
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int ajustarCodigoErroContratosPercentuais(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "update "
                         + "tb_tmp_arquivo_conciliacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "set cod_erro = (case when cod_erro = 7 then 5 else 0 end) "
                         + "where cod_erro in (6, 7) "
                         + "and ade.ade_tipo_vlr = 'P'"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.valor.percentual", (AcessoSistema) null, ex);
        }
    }

    /**
     * CASOS ONDE A DATA INICIAL E FINAL SÃO IGUAIS, PORÉM O PRAZO É DIFERENTE: IGNORAR
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int ajustarCodigoErroContratosPrazoInconsistente(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "update "
                         + "tb_tmp_arquivo_conciliacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "set cod_erro = (case when cod_erro = 7 then 6 else 0 end) "
                         + "where cod_erro in (5, 7) "
                         + "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) = tmp.ade_ano_mes_ini "
                         + "and (ade.ade_prazo is null or coalesce(ade.ade_ano_mes_fim, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01'))"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.prazo.inconsistente", (AcessoSistema) null, ex);
        }
    }

    /**
     * CASOS ONDE A DATA FINAL É IGUAL (OU INDETERMINADA), PORÉM A DATA INICIAL OU PRAZO SÃO DIFERENTES: AJUSTAR DATA INICIAL FOLHA E IGNORAR
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int ajustarCodigoErroContratosPrazoIndeterminado(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "update "
                         + "tb_tmp_arquivo_conciliacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "set cod_erro = (case when cod_erro = 7 then 6 else 0 end), ade.ade_ano_mes_ini_folha = tmp.ade_ano_mes_ini "
                         + "where cod_erro in (5, 7) "
                         + "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) <> tmp.ade_ano_mes_ini "
                         + "and (ade.ade_prazo is null or coalesce(ade.ade_ano_mes_fim, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01'))"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.prazo.indeterminado", (AcessoSistema) null, ex);
        }
    }


    /**
     * CASOS ONDE A DATA INICIAL É IGUAL, PORÉM A DATA FINAL É DIFERENTE POIS O CONTRATO TEVE PRAZO ALTERADO: IGNORAR
     * @param stat : statement para atualização dos dados
     * @return
     * @throws ConsignanteControllerException
     */
    private int ajustarCodigoErroContratosPrazoAlterado(SincronizadorConf conf, Statement stat) throws ConsignanteControllerException {
        try {
            String query = "update "
                         + "tb_tmp_arquivo_conciliacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                         + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                         + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                         + "set cod_erro = (case when cod_erro = 7 then 6 else 0 end) "
                         + "where cod_erro in (5, 7) "
                         + "and coalesce(ade.ade_ano_mes_ini_folha, ade.ade_ano_mes_ini) = tmp.ade_ano_mes_ini "
                         + "and coalesce(ade.ade_ano_mes_fim_folha, '2099-12-01') = coalesce(tmp.ade_ano_mes_fim, '2099-12-01') "
                         + "and coalesce(ade.ade_ano_mes_fim, '2099-12-01') <> coalesce(tmp.ade_ano_mes_fim, '2099-12-01') "
                         + "and exists (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo and oca.toc_codigo = '14' and oca.oca_data > pex.pex_data_fim)"
                         ;
            LOG.info(query);
            return stat.executeUpdate(query);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.atualizar.registros.prazo.indeterminado", (AcessoSistema) null, ex);
        }
    }

    /**
     * Gera o arquivo, no leiaute especificado pelos XML's de movimento financeiro,
     * para a exclusão dos registros que foram determinados para exclusão.
     * @param caminhoSaida : caminho para gravação do arquivo de saida
     * @param ultimoPeriodoRetorno : último período de retorno no formato YYYY-MM-DD
     * @return
     * @throws ConsignanteControllerException
     */
    @Override
    public String gerarArquivoExclusoes(String caminhoSaida, String ultimoPeriodoRetorno) throws ConsignanteControllerException {
        Connection conn = null;

        try {
            String dataAtual = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
            String nomeArqSaida        = caminhoSaida + File.separatorChar + "EXCLUIR_" + dataAtual + ".TXT";
            String nomeArqConfEntrada  = caminhoSaida + File.separatorChar + "mov_fin_entrada.xml";
            String nomeArqConfTradutor = caminhoSaida + File.separatorChar + "mov_fin_tradutor.xml";
            String nomeArqConfSaida    = caminhoSaida + File.separatorChar + "mov_fin_saida.xml";

            String query = "select "
                         // Informações fixas
                         + "  'E' as situacao, '" + ultimoPeriodoRetorno + "' as pex_periodo_ant, "
                         // Dados do servidor
                         + "  est.est_identificador, org.org_identificador, rse.rse_matricula, ser.ser_cpf, ser.ser_nome, "
                         // Dados do convênio
                         + "  cnv.cnv_cod_verba, csa.csa_identificador, csa.csa_nome, svc.svc_identificador, svc.svc_descricao, "
                         // Dados da consignação
                         + "  ade.ade_numero, ade.ade_vlr, ade.ade_vlr_folha, 0.00 as valor_desconto, ade.ade_prazo, coalesce(ade.ade_prd_pagas, 0) as ade_prd_pagas, "
                         + "  nullif(ade.ade_ano_mes_ini, '0000-00-00') as ade_ano_mes_ini, nullif(tmp.ade_ano_mes_ini, '0000-00-00') as ade_ano_mes_ini_folha, "
                         + "  nullif(ade.ade_ano_mes_fim, '0000-00-00') as ade_ano_mes_fim, nullif(tmp.ade_ano_mes_fim, '0000-00-00') as ade_ano_mes_fim_folha, "
                         + "  nullif(ade.ade_ano_mes_ini, '0000-00-00') as data_ini_contrato, nullif(tmp.ade_ano_mes_fim, '0000-00-00') as data_fim_contrato, "
                         + "  ade.ade_tipo_vlr, ade.ade_inc_margem, ade.ade_int_folha "
                         + "from tb_tmp_arquivo_conciliacao tmp "
                         + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                         + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                         + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                         + "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) "
                         + "inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) "
                         + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                         + "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) "
                         + "inner join tb_orgao org on (rse.org_codigo = org.org_codigo) "
                         + "inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) "
                         + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                         + "left outer join tb_ocorrencia_autorizacao oca06 on (ade.ade_codigo = oca06.ade_codigo and oca06.toc_codigo = '6' and oca06.oca_data > pex.pex_data_fim) "
                         + "where ade.sad_codigo in ('3','7','8','9') "
                         + "and oca06.oca_codigo is null "
                         + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                         + "and ade.ade_data_ult_conciliacao >= curdate() "
                         + "order by cnv_cod_verba, ade_numero "
                         ;

            conn = DBHelper.makeConnection();

            Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntrada, query, conn);
            EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
            Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();

            return nomeArqSaida;

        } catch (ParserException ex) {
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.gerar.arquivo.exclusoes.enviadas.folha", (AcessoSistema) null, ex);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.fazer.conexao.base.dados", (AcessoSistema) null, ex);
        } finally {
            if (conn != null) {
                DBHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Inclui ocorrências de alteração nas consignações que devam ser reenviadas para a folha
     * por motivo de desincronização de valor, prazo ou ambos. Com a ocorrência de alteração
     * o próprio sistema se encarregará de enviar uma exclusão/inclusão.
     * @param ajustarInfFolha : true se deve ajustar as data ini e fim folha e o valor folha
     * @param ultimoPeriodoRetorno : último período de retorno no formato YYYY-MM-DD
     * @throws ConsignanteControllerException
     */
    @Override
    public void incluirAlteracaoFolha(boolean ajustarInfFolha, String ultimoPeriodoRetorno) throws ConsignanteControllerException {
        Connection conn = null;
        Statement stat = null;
        PreparedStatement prep = null;

        try {
            String query = null;
            int linhasAfetadasTotal = 0;

            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            if (ajustarInfFolha) {
                // Executa as alterações de data ini e fim folha para evitar problema no reimplante
                query = "update tb_tmp_arquivo_conciliacao tmp "
                      + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                      + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                      + "set ade.ade_ano_mes_ini_folha = tmp.ade_ano_mes_ini, ade.ade_ano_mes_fim_folha = tmp.ade_ano_mes_fim, ade.ade_vlr_folha = tmp.ade_vlr "
                      + "where tmp.cod_erro = 5 "
                      + "and ade.sad_codigo not in ('3','7','8','9') "
                      + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                      ;
                LOG.debug(query);
                linhasAfetadasTotal = stat.executeUpdate(query);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.consignacao.aberta.data.inconsistente.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));

                query = "update tb_tmp_arquivo_conciliacao tmp "
                      + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                      + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                      + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                      + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                      + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                      + "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data > pex.pex_data_fim) "
                      + "set ade.ade_ano_mes_ini_folha = tmp.ade_ano_mes_ini, ade.ade_ano_mes_fim_folha = tmp.ade_ano_mes_fim, ade.ade_vlr_folha = tmp.ade_vlr "
                      + "where tmp.cod_erro = 6 "
                      + "and ade.ade_tipo_vlr = 'F' "
                      + "and oca14.oca_data is null "
                      + "and ade.sad_codigo not in ('3','7','8','9') "
                      + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                      ;
                LOG.debug(query);
                linhasAfetadasTotal = stat.executeUpdate(query);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.consignacao.aberta.valor.inconsistente.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));

                query = "update tb_tmp_arquivo_conciliacao tmp "
                      + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                      + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                      + "set ade.ade_ano_mes_ini_folha = tmp.ade_ano_mes_ini, ade.ade_ano_mes_fim_folha = tmp.ade_ano_mes_fim, ade.ade_vlr_folha = tmp.ade_vlr "
                      + "where tmp.cod_erro = 7 "
                      + "and ade.sad_codigo not in ('3','7','8','9') "
                      + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                      + "and ade.ade_tipo_vlr = 'F' "
                      ;
                LOG.debug(query);
                linhasAfetadasTotal = stat.executeUpdate(query);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.consignacao.aberta.data.valor.inconsistente.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));
            }

            query = "( "
                  + "select ade.ade_codigo "
                  + "from tb_tmp_arquivo_conciliacao tmp "
                  + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + "where tmp.cod_erro = 5 "
                  + "and ade.sad_codigo not in ('3','7','8','9') "
                  + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                  + ") union ( "
                  + "select ade.ade_codigo "
                  + "from tb_tmp_arquivo_conciliacao tmp "
                  + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                  + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                  + "left outer join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo and pse.tps_codigo = '4') "
                  + "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data > pex.pex_data_fim) "
                  + "where tmp.cod_erro = 6 "
                  + "and ade.ade_tipo_vlr = 'F' "
                  + "and oca14.oca_data is null "
                  + "and ade.sad_codigo not in ('3','7','8','9') "
                  + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                  + ") union ( "
                  + "select ade.ade_codigo "
                  + "from tb_tmp_arquivo_conciliacao tmp "
                  + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + "where tmp.cod_erro = 7 "
                  + "and ade.sad_codigo not in ('3','7','8','9') "
                  + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                  + "and ade.ade_tipo_vlr = 'F' "
                  + ") "
                  ;

            prep = conn.prepareStatement("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, ade_codigo, toc_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) VALUES (?, ?, '14', '1', CURDATE(), date_add('" + ultimoPeriodoRetorno + "', interval 1 month), 'CONCILIAÇÃO COM A FOLHA DE PAGAMENTOS.')");

            LOG.debug(query);
            linhasAfetadasTotal = 0;
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String ocaCodigo = DBHelper.getNextId();
                String adeCodigo = rs.getString("ade_codigo");

                prep.setString(1, ocaCodigo);
                prep.setString(2, adeCodigo);
                int linhasAfetadas = prep.executeUpdate();

                linhasAfetadasTotal += linhasAfetadas;
                if (linhasAfetadasTotal % 100 == 0) {
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ocorrencias.inseridas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));
                }
            }
            rs.close();
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.total.ocorrencias.inseridas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));

        } catch (MissingPrimaryKeyException ex) {
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.utilizar.gerador.chaves.primarias", (AcessoSistema) null, ex);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.fazer.conexao.base.dados", (AcessoSistema) null, ex);
        } finally {
            if (prep != null) {
                DBHelper.closeStatement(prep);
            }
            if (stat != null) {
                DBHelper.closeStatement(stat);
            }
            if (conn != null) {
                DBHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Inclui ocorrências de reimplante nas consignações que devam ser reenviadas para a folha
     * por motivo de não integração pela rotina de conciliação, apesar de terem sido pagas pelo
     * último retorno. Provavelmente a consignação que está sendo paga é uma liquidada, cancelada
     * ou concluida que será excluída da folha pela rotina de conciliação. Portante este contrato
     * deve ser reimplantado para que seja pago.
     * @param ajustarInfFolha : true se deve ajustar as data ini e fim, prazo e pagas, ou false apenas para incluir a ocorrência
     * @param ultimoPeriodoRetorno : último período de retorno no formato YYYY-MM-DD
     * @throws ConsignanteControllerException
     */
    @Override
    public void incluirReimplanteFolha(boolean ajustarInfFolha, String ultimoPeriodoRetorno) throws ConsignanteControllerException {
        Connection conn = null;
        Statement stat = null;
        PreparedStatement prep = null;

        try {
            String query = null;
            int linhasAfetadasTotal = 0;

            conn = DBHelper.makeConnection();
            stat = conn.createStatement();

            // Consignações que devem ser reimplantadas: Verificar parâmetros de reimplante e preservação de parcelas
            query = "select ade.ade_codigo "
                  + "from tb_registro_servidor rse "
                  + "inner join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo) "
                  + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                  + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                  + "left outer join tb_param_svc_consignataria psc35 on (cnv.svc_codigo = psc35.svc_codigo and cnv.csa_codigo = psc35.csa_codigo and psc35.tps_codigo = '35' and coalesce(psc35.psc_ativo, '1' = '1')) "
                  + "left outer join tb_param_sist_consignante tpc66  on (tpc66.tpc_codigo  = '66') "  // TEM REIMPLANTE
                  + "left outer join tb_param_sist_consignante tpc67  on (tpc67.tpc_codigo  = '67') "  // CSA ESCOLHE REIMPLANTE
                  + "left outer join tb_param_sist_consignante tpc134 on (tpc134.tpc_codigo = '134') " // DEFAULT SVC REIMPLANTE
                  + "where 1=1 "
                  + "and (ade.ade_data_ult_conciliacao is null or ade.ade_data_ult_conciliacao < curdate()) "
                  + "and exists (select 1 from tb_parcela_desconto prd where ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto = '" + ultimoPeriodoRetorno + "' and prd.spd_codigo = '6') "
                  + "and ade.sad_codigo = '5' "
                  + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                  + "and coalesce(tpc66.psi_vlr, 'N') = 'S' "         // Se o sistema reimplanta
                  + "    and ((coalesce(tpc67.psi_vlr, 'N') = 'N') "  // e as consignatárias não podem escolher, ou podem e escolheram para reimplantar
                  + "      or (coalesce(tpc67.psi_vlr, 'N') = 'S' and coalesce(psc35.psc_vlr, coalesce(tpc134.psi_vlr, 'N')) = 'S')) "
                  ;

            prep = conn.prepareStatement("INSERT INTO tb_ocorrencia_autorizacao (oca_codigo, ade_codigo, toc_codigo, usu_codigo, oca_data, oca_periodo, oca_obs) VALUES (?, ?, '10', '1', CURDATE(), date_add('" + ultimoPeriodoRetorno + "', interval 1 month), 'CONCILIAÇÃO COM A FOLHA DE PAGAMENTOS.')");

            linhasAfetadasTotal = 0;
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String ocaCodigo = DBHelper.getNextId();
                String adeCodigo = rs.getString("ade_codigo");

                prep.setString(1, ocaCodigo);
                prep.setString(2, adeCodigo);
                int linhasAfetadas = prep.executeUpdate();

                linhasAfetadasTotal += linhasAfetadas;
                if (linhasAfetadasTotal % 100 == 0) {
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.ocorrencias.inseridas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));
                }
            }
            rs.close();
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.total.ocorrencias.inseridas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));

            // Se deve ajustar as informações, então altera datas inicial/final, prazo, pagas
            // e a situação para Deferido para que a consignação seja reimplantada como inclusão
            if (ajustarInfFolha) {
                query = "update tb_registro_servidor rse "
                      + "inner join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo) "
                      + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                      + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                      + "left outer join tb_param_svc_consignataria psc35 on (cnv.svc_codigo = psc35.svc_codigo and cnv.csa_codigo = psc35.csa_codigo and psc35.tps_codigo = '35' and coalesce(psc35.psc_ativo, '1' = '1')) "
                      + "left outer join tb_param_sist_consignante tpc66  on (tpc66.tpc_codigo  = '66') "  // TEM REIMPLANTE
                      + "left outer join tb_param_sist_consignante tpc67  on (tpc67.tpc_codigo  = '67') "  // CSA ESCOLHE REIMPLANTE
                      + "left outer join tb_param_sist_consignante tpc134 on (tpc134.tpc_codigo = '134') " // DEFAULT SVC REIMPLANTE

                      + "set ade.sad_codigo = '4', "
                      + "    ade.ade_ano_mes_ini = date_add('" + ultimoPeriodoRetorno + "', interval 1 month), "
                      + "    ade.ade_ano_mes_fim = if(ade.ade_prazo is not null, date_add(date_add('" + ultimoPeriodoRetorno + "', interval 1 month), interval (ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0)) - 1 month), null), "
                      + "    ade.ade_prazo = if(ade.ade_prazo is not null, (ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0)), null), "
                      + "    ade.ade_prd_pagas = 0 "

                      + "where 1=1 "
                      + "and (ade.ade_data_ult_conciliacao is null or ade.ade_data_ult_conciliacao < curdate()) "
                      + "and exists (select 1 from tb_parcela_desconto prd where ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto = '" + ultimoPeriodoRetorno + "' and prd.spd_codigo = '6') "
                      + "and ade.sad_codigo = '5' "
                      + "and rse.srs_codigo" + " NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" + " "
                      + "and coalesce(tpc66.psi_vlr, 'N') = 'S' "         // Se o sistema reimplanta
                      + "    and ((coalesce(tpc67.psi_vlr, 'N') = 'N') "  // e as consignatárias não podem escolher, ou podem e escolheram para reimplantar
                      + "      or (coalesce(tpc67.psi_vlr, 'N') = 'S' and coalesce(psc35.psc_vlr, coalesce(tpc134.psi_vlr, 'N')) = 'S')) "
                      ;

                linhasAfetadasTotal = stat.executeUpdate(query);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.consignacoes.reimplantadas.arg0", (AcessoSistema) null, String.valueOf(linhasAfetadasTotal)));
            }

        } catch (MissingPrimaryKeyException ex) {
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.utilizar.gerador.chaves.primarias", (AcessoSistema) null, ex);
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.sincronizador.fazer.conexao.base.dados", (AcessoSistema) null, ex);
        } finally {
            if (prep != null) {
                DBHelper.closeStatement(prep);
            }
            if (stat != null) {
                DBHelper.closeStatement(stat);
            }
            if (conn != null) {
                DBHelper.releaseConnection(conn);
            }
        }
    }

    private static class SincronizadorConf {
        // Flags para otimização de joins
        boolean temIdSvc = false;
        boolean temIdCsa = false;
        boolean temIdOrg = false;
        boolean temIdEst = false;
        boolean temVerba = false;
        boolean temCpf   = false;
        boolean temMatricula = false;
    }
}
