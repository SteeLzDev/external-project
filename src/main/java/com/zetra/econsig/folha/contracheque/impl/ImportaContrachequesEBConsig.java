package com.zetra.econsig.folha.contracheque.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.folha.contracheque.AbstractImportaContraCheques;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

public class ImportaContrachequesEBConsig extends AbstractImportaContraCheques {

	/** Log object for this class. */
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaContrachequesEBConsig.class);

	@Override
	public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade,
			String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException {

		BufferedReader entrada = null;
		String linha = null;
		StringBuilder conteudo = null;
		String rseCodigo = null;
		String serCpf = null;
		String rseMatricula = null;
		String cpf = null;
		int numLinha = 0;

		try {
			//Inicia o processo de leitura linha a linha do arquivo de contracheque.
			entrada = new BufferedReader(new FileReader(nomeArquivo));

			while ((linha = entrada.readLine()) != null) {
				numLinha += 1;

				if (!linha.isBlank()) {

					//Recupera do arquivo de contracheque a matrícula e o cpf do servidor.
					if (linha.trim().length() >= 91) {
						cpf = linha.substring(80, 91);
						//reinicializa as variáveis.
						numLinha = 1;
					} else if (numLinha == 6) {
						rseMatricula = linha.substring(33, 42);
					}

					//Se mudar o cpf é porque é outro contracheque
					if (!TextHelper.isNull(serCpf) && !serCpf.equalsIgnoreCase(cpf) && !TextHelper.isNull(rseCodigo)) {
						if (conteudo != null) {
							// Se já existe um contracheque, então grava o registro anterior de contracheque
							conteudo.append("\n" + "Responsabilidade dos dados acima:" + "\n" +"Exército Brasileiro CPEX");
							criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
							conteudo.setLength(0);

							//zero os dados anteriores
							rseCodigo = null;
							rseMatricula = null;

						} else {
							conteudo = new StringBuilder();
						}
					}

					if (cpf != null && !cpf.trim().equals("") && rseMatricula != null && !rseMatricula.trim().equals("")) {

						serCpf = cpf;

						if (rseCodigo == null) {
							//Executa a query de pesquisa, que realiza a busca pelo servidor usando a matrícula e o cpf.
							ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
							query.serCPF = TextHelper.format(serCpf, "###.###.###-##");
							query.rseMatricula = rseMatricula.trim();

							List<String> servidores = query.executarLista();
							if (servidores == null || servidores.isEmpty()) {
								rseCodigo = null;
								LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.registro.servidor.unico", responsavel, rseMatricula));
								continue;
							} else if (servidores.size() > 1) {
								rseCodigo = servidores.get(0);
								query.ativo = true;
								servidores = query.executarLista();

								if (servidores.size() > 1) {
									rseCodigo = null;
									LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.encontrar.registro.servidor.multiplo", responsavel, rseMatricula));
									continue;
								}

							} else {
								rseCodigo = servidores.get(0);
							}
						}
					}

					//Começa a montar o conteúdo do contracheque, a partir da segunda linha.
					if (numLinha > 1) {
						//Conteúdo do contracheque.
						if (conteudo == null) {
							conteudo = new StringBuilder();
						}

						if (linha.trim().length() >= 71) {
							conteudo.append(linha.substring(23, 71)).append("\n");
						}
					}
				}
			}

			if (!TextHelper.isNull(rseCodigo) && !TextHelper.isNull(conteudo)) {
				//Grava o registro do contracheque final
				conteudo.append("\n" + "Responsabilidade dos dados acima:" + "\n" +"Exército Brasileiro CPEX");
				criarContraChequeRegistroServidor(rseCodigo, periodo, conteudo.toString());
			}

		} catch (FileNotFoundException ex) {
			throw new ImportaContrachequesException("mensagem.erro.contracheque.arquivo.invalido", responsavel, ex);
		} catch (IOException ex) {
			throw new ImportaContrachequesException("mensagem.erro.contracheque.processar.arquivo", responsavel, ex, nomeArquivo);
		} catch (HQueryException ex) {
			throw new ImportaContrachequesException("mensagem.erro.contracheque.pesquisar.rse", responsavel, ex);
		} finally {
			try {
				if (entrada != null) {
					entrada.close();
				}
			} catch (IOException ex) {
				LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.contracheque.fechar.arquivo", responsavel, nomeArquivo), ex);
			}
		}


	}
}