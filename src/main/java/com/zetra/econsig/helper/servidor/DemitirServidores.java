package com.zetra.econsig.helper.servidor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DemitirServidores</p>
 * <p>Description: Classe para importação de servidores para o Programa de Demissão Voluntária. </p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DemitirServidores implements RotinaExternaViaProxy {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DemitirServidores.class);
    private static final String NOME_CLASSE = DemitirServidores.class.getName();

	@Override
    public int executar(String[] args) {
		final String textoAjuda = "USE: java " + NOME_CLASSE + " ARQUIVO USU_CODIGO [\"Justificativa\"]\nOBS: O USU_CODIGO deve ser de um usuário com permissão de executar a operação.";
		String obs = "";
		try {
			if (args.length < 2) {
				LOG.error(textoAjuda);
	            return -1;

			} else {
				String nomeArquivo = args[0];
				String responsavel = args[1];
				Path arquivo = Paths.get(nomeArquivo);
				if (!Files.exists(Paths.get(nomeArquivo))) {
					LOG.error("Arquivo " + nomeArquivo + " não encontrado.");
					return -1;
				}
				if (!Files.isReadable(arquivo)) {
					LOG.error("Arquivo " + nomeArquivo + " sem permissão de leitura.");
					return -1;
				}
				if (args.length > 2) {
					obs = args[2];
				}

				String nomeArquivoCritica = DateHelper.format(DateHelper.getSystemDatetime(), "yMMddHHmmss") + "_"
						+ arquivo.getFileName().toString() + ".result";

				Path currentRelativePath = Paths.get("");
				String s = currentRelativePath.toAbsolutePath().toString();
				System.out.println("Current relative path is: " + s);
				Path arquivoCritica = Paths.get(nomeArquivoCritica);
				if (Files.exists(arquivoCritica)) {
					LOG.error("Arquivo de crítica já existente. Se necessário, espere um pouco e tente novamente.");
					return -1;
				}

				LOG.info("INÍCIO - DEMISSÃO DE SERVIDORES: " + DateHelper.getSystemDatetime());
				executeWeaponOfMassDemission(arquivo, arquivoCritica, responsavel , obs);
				LOG.info("FIM - : DEMISSÃO DE SERVIDORES: " + DateHelper.getSystemDatetime());
			}
            return 0;
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
			return -1;
		}
	}

	private void executeWeaponOfMassDemission(Path arquivo, Path arquivoCritica, String usuCodigoResponsavel, String obs) throws IOException {

		SaldoDevedorDelegate saldoDelegate;
		ServidorDelegate serDelegate;
		try {
			saldoDelegate = new SaldoDevedorDelegate();
			serDelegate = new ServidorDelegate();
		} catch (SaldoDevedorControllerException e) {
			LOG.error("Erro de conexão com o sistema eConsig", e);
			return;
		}

		AcessoSistema responsavel;
		try {
			responsavel = AcessoSistema.recuperaAcessoSistema(usuCodigoResponsavel, "127.0.0.1", null);
		} catch (ZetraException e2) {
			LOG.error("Não foi possível recuperar o usuário " + usuCodigoResponsavel);
			return;
		}

		List<String> lines = Files.readAllLines(arquivo, Charset.defaultCharset());

		Files.write(arquivoCritica, "".getBytes(Charset.defaultCharset()),StandardOpenOption.CREATE_NEW);
		for (String linha : lines) {
			String rseCodigo = linha.trim();
			String res;
			try {
				// Bloqueando registro servidor
				RegistroServidorTO servidor = serDelegate.findRegistroServidor(rseCodigo, responsavel);
				if (!CodedValues.SRS_BLOQUEADOS.contains(servidor.getAttribute(Columns.SRS_CODIGO).toString())) {
					servidor.setAttribute(Columns.SRS_CODIGO, CodedValues.SRS_BLOQUEADO);
					serDelegate.updateRegistroServidorSemHistoricoMargem(servidor, responsavel);
				}
				try {
					saldoDelegate.solicitarSaldoDevedorExclusaoServidor(rseCodigo, obs, responsavel);
					res = rseCodigo + " : " + "OK" + "\n";
					LOG.debug(res);
					Files.write(arquivoCritica, res.getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
				} catch (SaldoDevedorControllerException e) {
					res = rseCodigo + " : " + e.getMessage() + "\n";
					LOG.debug(res);
					Files.write(arquivoCritica, res.getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
				}
			} catch (ServidorControllerException e1) {
				res = rseCodigo + " : " + e1.getMessage() + "\n";
				LOG.debug(res);
				Files.write(arquivoCritica, res.getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
			}
		}

	}

}
