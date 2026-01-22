package com.zetra.econsig.service.consignacao;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.anexo.ListaAnexoContratoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: DownloadAnexoContratoControllerBean</p>
 * <p>Description: Session Façade para Rotina de compactação dos anexos de contrato</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
@Service
@Transactional
public class DownloadAnexoContratoControllerBean implements DownloadAnexoContratoController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadAnexoContratoControllerBean.class);

	@Override
	public String geraNomeAnexosPeriodo(HashMap<String, String> campos, String extensao, Integer sufixo, boolean validarDocumentos, AcessoSistema responsavel) throws ParametrosException {
		String nomePattern = ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_NOME_ANEXO_PERIODO, responsavel).toString();
		String nomeFinal = "";

		//verifica se existe algum prefixo para o nome do arquivo além dos campos dinâmicos
		boolean temPrefixo = nomePattern.indexOf("<") > 0;
		String[] nomePartes = nomePattern.split("<");

		//itera sob cada bloco de parâmetros
		for (String parte : nomePartes) {
			//caso tenha prefixo, adiciona ao nome do arquivo e passa para o próximo bloco
			if (temPrefixo) {
				nomeFinal += parte;
				temPrefixo = false;
				continue;
			}

			//recupera os parâmetros
			String[] parametros = parte.split(":");

			//verifica se cada um dos valores dinâmicos possuem todos os campos necessários
			if (parametros.length < 4) {
				throw new ParametrosException("mensagem.erro.anexo.perido.parametros.ausentes", responsavel);
			}

			//verifica se depois do fim do parâmetro existe algum texto fixo
			boolean temSufixoIn = parametros[3].indexOf(">") < (parametros[3].length() - 1);

			String chave = parametros[0];
			String complemento = parametros[1];
			String direcaoComplemento = parametros[2];
			int comprimento = Integer.valueOf(parametros[3].split(">")[0]).intValue();

			String valorCampo = campos.get(chave);

			//verifica se o campo usado é um dos previstos:
			//rse_matricula, ser_cpf, ser_nome, ade_numero, cnv_cod_verba, ade_identificador, ade_indice
			if (valorCampo == null) {
				throw new ParametrosException(ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.perido.campo.sem.suporte", responsavel, parametros[0]), responsavel);
			}

			//substituo caracteres acentuados por versões sem acento
			valorCampo = Normalizer.normalize(valorCampo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			//retiro caracteres especiais
			valorCampo = valorCampo.replaceAll("[^a-zA-Z0-9]+", "");

			//Se o campo setado não estiver setado no sistema preenche com X (1 se não houver comprimento definido ou tantos
			// X quanto o comprimento
			if (!valorCampo.equals("")) {
				if (comprimento != -1) {
					int comprimentoDado = valorCampo.length();
					String complementoDado = "";

					//Controe o complemento a ser adicionado ao valor do campo
					if (comprimentoDado < comprimento) {
						for (int i = 0; i < comprimento - comprimentoDado; i++) {
							complementoDado += complemento;
						}
					}

					if (!complementoDado.equals("")) {
						//Concatena no lado certo do dado
						if (direcaoComplemento.equalsIgnoreCase("E")) {
							nomeFinal += complementoDado + valorCampo;
						} else {
							nomeFinal += valorCampo + complementoDado;
						}
					} else {
						//nesse caso o valor do campo é cortado para o tamanho do comprimento fornecido
						nomeFinal += valorCampo.substring(0, comprimento);
					}
				} else {
					nomeFinal += valorCampo;
				}

			} else {
				if (comprimento != -1) {
					int comprimentoDado = 0;
					String complementoDado = "";

					//Controe o complemento a ser adicionado ao valor do campo
					if (comprimentoDado < comprimento) {
						for (int i = 0; i < comprimento - comprimentoDado; i++) {
							complementoDado += "X";
						}
					}

					if (!complementoDado.equals("")) {
						nomeFinal += complementoDado;
					} else {
						//nesse caso o valor do campo é cortado para o tamanho do comprimento fornecido
						nomeFinal += valorCampo.substring(0, comprimento);
					}
				} else {
					nomeFinal += "X";
				}
			}

			//adiciona qualquer parte fixa do pattern de nome ao nome do arquivo final
			if (temSufixoIn) {
				nomeFinal += parametros[3].split(">")[1];
			}
		}
		nomeFinal += !validarDocumentos ? "_aaa_" : "" + (sufixo != null ? sufixo : "") + extensao;

		return nomeFinal;
	}


	/**
	 * cria uma hierarquia de diretórios contendo os anexos dos contratos.
	 * A hierquia de diretórios segue o padrão : COD_VERBA/NOME_ARQUIVO. Onde NOME-ARQUIVO
	 * será CPF-ADE_NUMERO
	 * @param orgCodigos - órgãos cujos contratos serão pesquisados
	 * @param estCodigos - estabelecimentos cujos contratos serão pesquisados
	 * @param zipFileNameOutPut - nome do arquivo zip a ser criado
	 * @param responsavel
	 * @throws ConsignanteControllerException
	 */
	@Override
    public String compactarAnexosAdePeriodo(List<String> csaCodigos, List<String> svcCodigos, List<String> sadCodigos, Date aadDataIni, Date aadDataFim, String zipFileNameOutPut, AcessoSistema responsavel) throws ConsignanteControllerException {

		if (TextHelper.isNull(zipFileNameOutPut)) {
			throw new ConsignanteControllerException("mensagem.erro.nome.arquivo.nao.informado", responsavel);
		}

		ListaAnexoContratoQuery lstAnexosContrato = new ListaAnexoContratoQuery();
		lstAnexosContrato.csaCodigos = csaCodigos;
		lstAnexosContrato.svcCodigos = svcCodigos;
		lstAnexosContrato.sadCodigos = sadCodigos;
		lstAnexosContrato.aadDataIni = aadDataIni;
		lstAnexosContrato.aadDataFim = aadDataFim;
		int sufixo = 0;


		List<TransferObject> infoAnexosList = null;
		try {
			infoAnexosList = lstAnexosContrato.executarDTO();
		} catch (HQueryException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
		}

		BigInteger adeNumeroAnt = null;
		String caminhoRaiz = null;

		Path pathRaiz = null;
		Path pathCodVerba = null;

		String rootDirPath = ParamSist.getDiretorioRaizArquivos();

		if (infoAnexosList != null && !infoAnexosList.isEmpty()) {
			//define pasta raíz onde ficará diretório temporário
			caminhoRaiz = rootDirPath + File.separatorChar + "temp";
			if (!Files.exists(FileSystems.getDefault().getPath(caminhoRaiz), LinkOption.NOFOLLOW_LINKS)) {
				try {
					Files.createDirectory(FileSystems.getDefault().getPath(caminhoRaiz));
				} catch (IOException e) {
					try {
						FileHelper.deleteDir(caminhoRaiz.toString());
					} catch (IOException ex) {
						LOG.error(ex.getMessage(), ex);
						throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
					}
					throw new ConsignanteControllerException("mensagem.anexo.contrato.erro.criacao.diretorio", responsavel, caminhoRaiz);
				}
			}
		} else {
			LOG.info(ApplicationResourcesHelper.getMessage("mensagem.aviso.nenhum.anexo.encontrado", responsavel));
			return null;
		}

		//cria raíz de anexos temporário
		caminhoRaiz += File.separatorChar + ApplicationResourcesHelper.getMessage("rotulo.nome.subpasta.anexo.ade.contrato", responsavel);
		try {
			Path caminhoRaizPath = FileSystems.getDefault().getPath(caminhoRaiz);
			if(Files.exists(caminhoRaizPath)) {
				FileHelper.deleteDir(caminhoRaiz);
			}
			pathRaiz = Files.createDirectory(caminhoRaizPath);
		} catch (IOException e1) {
			try {
				FileHelper.deleteDir(caminhoRaiz);
			} catch (IOException ex) {
				LOG.error(ex.getMessage(), ex);
				throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
			}
			throw new ConsignanteControllerException("mensagem.anexo.contrato.erro.criacao.diretorio", responsavel, caminhoRaiz);
		}

		for (TransferObject infoAnexo: infoAnexosList) {

			String codVerba = (String) infoAnexo.getAttribute(Columns.CNV_COD_VERBA);
			BigInteger adeNumero = (BigInteger) infoAnexo.getAttribute(Columns.ADE_NUMERO);
			String aadNome = (String) infoAnexo.getAttribute(Columns.AAD_NOME);
			String serCpf = (String) infoAnexo.getAttribute(Columns.SER_CPF);
			String adeCodigo = (String) infoAnexo.getAttribute(Columns.ADE_CODIGO);
			String rseMatricula = (String) infoAnexo.getAttribute(Columns.RSE_MATRICULA);
			String serNome = (String) infoAnexo.getAttribute(Columns.SER_NOME);
			String adeIdentificador = (String) infoAnexo.getAttribute(Columns.ADE_IDENTIFICADOR);
			String adeIndice = (String) infoAnexo.getAttribute(Columns.ADE_INDICE);

			if(adeNumeroAnt != null && !adeNumeroAnt.equals(adeNumero)) {
				sufixo = 0;
			} else {
				sufixo++;
			}

			if (!Files.exists(FileSystems.getDefault().getPath(caminhoRaiz + File.separatorChar + codVerba), LinkOption.NOFOLLOW_LINKS)) {
                try {
                    pathCodVerba = Files.createDirectory(FileSystems.getDefault().getPath(caminhoRaiz + File.separatorChar + codVerba));
                } catch (IOException e) {
                    try {
                        FileHelper.deleteDir(pathRaiz.toString());
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                    throw new ConsignanteControllerException("mensagem.anexo.contrato.erro.criacao.diretorio", responsavel, codVerba);
                }
            }

			//como é o primeiro registro deste adeNumero, então é o anexo mais recente deste contrato no período
			//buscar o anexo do diretório padrão eCosing

			//Pega extensão do arquivo, se houver
			String extensao = (aadNome.lastIndexOf(".") != -1) ? aadNome.substring(aadNome.lastIndexOf(".")) : "";

			Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos()
					+ File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date) infoAnexo.getAttribute(Columns.ADE_DATA), "yyyyMMdd")
					+ File.separatorChar + adeCodigo + File.separatorChar + aadNome);
			String strippedCpf = serCpf.replaceAll("\\.", "").replaceAll("-", "");

			Path novoArquivo = null;
			try {
				//campos que podem ser usados para montar o padrão de nome de arquivo
				HashMap<String, String> campos = new HashMap<>();
				campos.put("rse_matricula", rseMatricula);
				campos.put("ser_cpf", serCpf);
				campos.put("ser_nome", serNome);
				campos.put("ade_numero", adeNumero.toString());
				campos.put("cnv_cod_verba", codVerba);
				campos.put("ade_identificador", adeIdentificador);
				campos.put("ade_indice", adeIndice);

				if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_NOME_ANEXO_PERIODO, responsavel))) {
					String newName;
					try {
						newName = geraNomeAnexosPeriodo(campos, extensao, sufixo > 0 ? sufixo : null, false, responsavel);
					} catch (ParametrosException ex) {
						LOG.error(ApplicationResourcesHelper.getMessage("rotulo.prefixo.zip.anexo.ade", responsavel, aadNome) + ex.getMessage(), ex);
						continue;
					}
					novoArquivo = FileSystems.getDefault().getPath(pathCodVerba.toString() + File.separatorChar + newName);
				} else {
					novoArquivo = FileSystems.getDefault().getPath(pathCodVerba.toString() + File.separatorChar + strippedCpf + "-" + adeNumero + extensao);
				}

				// copia arquivo do diretório anexo padrão eConsig para a hierarquia de diretórios de anexos do período
				LOG.debug("Tentando copiar \"" + anexoACopiarPath + "\" para \"" + novoArquivo + "\".");
				if (Files.exists(anexoACopiarPath, LinkOption.NOFOLLOW_LINKS)) {
					Files.copy(anexoACopiarPath, novoArquivo, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
				} else {
					LOG.warn("Arquivo \"" + anexoACopiarPath + "\" não existe e não foi copiado.");
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				try {
					FileHelper.deleteDir(pathRaiz.toString());
				} catch (IOException ex) {
					LOG.error(ex.getMessage(), ex);
				}

				throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, e, (novoArquivo != null ? novoArquivo.toString() : "-"));
			}

			adeNumeroAnt = adeNumero;
		}

		//Compacta todo o diretório
		StringBuilder pathZipAnexos = null;
		try {

			pathZipAnexos = new StringBuilder(rootDirPath + File.separatorChar + "anexos_contrato");

			if (!Files.exists(FileSystems.getDefault().getPath(pathZipAnexos.toString()))) {
				Files.createDirectory(FileSystems.getDefault().getPath(pathZipAnexos.toString()));
			}

			// Define o path onde será gravado o arquivo final de anexos
			pathZipAnexos = new StringBuilder(rootDirPath + File.separatorChar + "anexos_contrato" + File.separatorChar + "cse");

			// Cria diretório caso não exista
			if (!Files.exists(FileSystems.getDefault().getPath(pathZipAnexos.toString()))) {
				Files.createDirectory(FileSystems.getDefault().getPath(pathZipAnexos.toString()));
			}

			//caminho para o arquivo final
			Date dataAtual = new Date();
			String sufixoFile = DateHelper.format(dataAtual, "dd_M_yyyy_hh_mm_ss");
			pathZipAnexos.append(File.separatorChar).append(ApplicationResourcesHelper.getMessage("rotulo.prefixo.zip.anexo.ade.contrato", responsavel)).append(sufixoFile).append(zipFileNameOutPut);

			if (Files.exists(FileSystems.getDefault().getPath(pathZipAnexos.toString()))) {
				Files.delete(FileSystems.getDefault().getPath(pathZipAnexos.toString()));
			}
			FileHelper.zipFolder(pathRaiz.toString(), pathZipAnexos.toString());
			FileHelper.deleteDir(pathRaiz.toString());

			LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.anexo.mov.arquivo.criado", responsavel, pathZipAnexos.toString()));

			return pathZipAnexos.toString();

		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, pathZipAnexos.toString());
		}
	}

}
