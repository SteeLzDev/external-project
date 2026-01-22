package com.zetra.econsig.webservice.rest.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.DadosServidorRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: DadosServidorService</p>
 * <p>Description: Serviço REST dados do servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: andrea.giorgini $
 * $Revision: 26618 $
 * $Date: 2019-04-29 $
 */
@Path("/dadosServidor")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DadosServidorService extends RestService {
    /** Log object for this class. */
    private static final Log LOG = LogFactory.getLog(DadosServidorService.class);

    @Context
    SecurityContext securityContext;

    @GET
    @Path("/testeSimples")
    public Response atualizaDadosServidor() throws ServidorControllerException, FindException {
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/atualizaDadosServidor")
    public Response atualizaDadosServidor(DadosServidorRestRequest dados) throws ServidorControllerException, FindException {

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_EDT_SERVIDOR), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        // Verifica se veio algum dado preenchido, senão retorna com erro.
        if (dados == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        final ServidorDelegate serDelegate = new ServidorDelegate();
        //busca serCodigo
        final String serCodigo = responsavel.getSerCodigo().toString();

        try {
            // atualiza dados do servidor
            final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
            if (dados.telefone != null) {
                servidorUpd.setSerTel(dados.telefone);
            }
            if (dados.email != null) {
                servidorUpd.setSerEmail(dados.email);
            }
            if (dados.celular != null) {
                servidorUpd.setSerCelular(dados.celular);
            }
            serDelegate.updateServidor(servidorUpd, responsavel);

        } catch ( final ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/atualizaDadosServidorByCPF")
    public Response atualizaDadosServidorByCPF(List<DadosServidorRestRequest> dadosServidores) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_EDT_SERVIDOR), List.of(AcessoSistema.ENTIDADE_CSE, AcessoSistema.ENTIDADE_SUP, AcessoSistema.ENTIDADE_ORG));
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }
        responsavel.setFunCodigo(CodedValues.FUN_EDT_SERVIDOR);

		if (dadosServidores == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		}

		LOG.debug("QUANTIDADE DE SERVIDORES A SEREM ATUALIZADOS: " + dadosServidores.size());
		LOG.debug("INICIO ATUALIZACAO DE DADOS DOS SERVIDORES: " + DateHelper.getSystemDatetime());
        final String delimitador = ";";
        final List<String> critica = new ArrayList<>();
		for (final DadosServidorRestRequest dados : dadosServidores) {
			try {
				// Verifica se veio algum dado preenchido, senão retorna com erro.
				String cpf = dados.cpf;
				if (TextHelper.isNull(cpf)) {
					critica.add(dados.toString() + delimitador + TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.informado.invalido", responsavel)));
					continue;
				} else if (TextHelper.dropSeparator(cpf).length() != 11) {
					critica.add(dados.toString() + delimitador + TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("mensagem.erro.tradutor.cpf.formato.incorreto", responsavel, cpf)));
					continue;
				} else if (!TextHelper.cpfOk(TextHelper.dropSeparator(cpf))) {
					critica.add(dados.toString() + delimitador + TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.numero.invalido", responsavel, cpf)));
					continue;
				}
				// Formata o CPF
				if (cpf.length() != 14) {
					cpf = TextHelper.format(cpf, "###.###.###-##");
				}
				LOG.debug("SERVIDOR: " + TextHelper.escondeCpf(cpf));

				final ServidorDelegate serDelegate = new ServidorDelegate();
				//busca serCodigo
				List<TransferObject> servidores = null;
				try {
					servidores = serDelegate.lstRegistroServidorPorCpf(cpf, null, responsavel);
				} catch (final ServidorControllerException e) {
					critica.add(dados.toString() + delimitador + TextHelper.removeAccent(e.getMessage()));
					continue;
				}

				if (servidores == null || servidores.isEmpty()) {
					critica.add(dados.toString() + delimitador + TextHelper.removeAccent(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel)));
					continue;
				}

				for (final TransferObject servidor : servidores) {
					try {
						boolean atualiza = false;
						// atualiza dados do servidor
						final ServidorTransferObject servidorUpd = new ServidorTransferObject(servidor.getAttribute(Columns.SER_CODIGO).toString());
						if (!TextHelper.isNull(dados.telefone)) {
							servidorUpd.setSerTel(dados.telefone);
							atualiza = true;
						}
						if (!TextHelper.isNull(dados.email)) {
							servidorUpd.setSerEmail(dados.email);
							atualiza = true;
						}
						if (!TextHelper.isNull(dados.celular)) {
							servidorUpd.setSerCelular(dados.celular);
							atualiza = true;
						}
						if (atualiza) {
							serDelegate.updateServidor(servidorUpd, responsavel);
						}

					} catch ( final ServidorControllerException e) {
						critica.add(dados.toString() + delimitador + TextHelper.removeAccent(e.getMessage()));
					}
				}
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
				critica.add(dados.toString() + delimitador + TextHelper.removeAccent(e.getMessage()));
			}
		}
		LOG.debug("FIM ATUALIZACAO DE DADOS DOS SERVIDORES: " + DateHelper.getSystemDatetime());

        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathSaida = absolutePath
                         + File.separatorChar + "dadosServidor"
                         + File.separatorChar + responsavel.getTipoEntidade().toLowerCase()
                         + File.separatorChar;

        final File dir = new File(pathSaida);
        if (!dir.exists() && !dir.mkdirs()) {
            return Response.status(Response.Status.CONFLICT).entity(ResponseRestRequest.builder().mensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.criacao.diretorio", responsavel, dir.getAbsolutePath())).build()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        String nomeArqSaidaZip = null;
        if (!critica.isEmpty()) {
            // Grava de crítica com o resultado dos comandos que retornaram algum erro
            LOG.debug("ARQUIVOS CRITICA - DADOS SERVIDOR: " + DateHelper.getSystemDatetime());

            String nomeArqSaida = null;
            nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
            nomeArqSaida += ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.dados.servidor", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
            final String nomeArqSaidaTxt = nomeArqSaida + ".txt";
            PrintWriter arqSaida = null;

            try {
                arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
            } catch (final IOException ex) {
                return Response.status(Response.Status.CONFLICT).entity(ResponseRestRequest.builder().mensagem(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel)).build()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

            // Imprime as linhas de critica no arquivo
            arqSaida.println(TextHelper.join(critica, System.lineSeparator()));

            arqSaida.close();
            LOG.debug("FIM ARQUIVO CRITICA - DADOS SERVIDOR: " + DateHelper.getSystemDatetime());

            try {
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos dados servidor: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("nomeArqSaidaZip: " + nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos dados servidor: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);
            } catch (final IOException ex) {
                // Ignora erro de compactação ou remoção do arquivo, já que o lote foi
                // processado corretamente, evitando demais problemas
                LOG.error(ex.getMessage(), ex);
            }
        }

        final ResponseRestRequest responseError = new ResponseRestRequest();
        if (!critica.isEmpty()) {
        	responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.alteracao.dados.servidor.critica", responsavel, nomeArqSaidaZip);
        } else {
        	responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.alteracao.dados.servidor.sucesso", responsavel);
        }

        return Response.status(Response.Status.OK).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/pesquisaMatriculasServidor")
    public Response pesquisaMatriculasServidor(UsuarioRestRequest usr) {
        // Valida permissão para usuario gestor
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONSULTAR_SERVIDOR), List.of(AcessoSistema.ENTIDADE_CSE, AcessoSistema.ENTIDADE_SUP, AcessoSistema.ENTIDADE_ORG));
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        final ServidorDelegate pesquisarServidorController = new ServidorDelegate();
        List<TransferObject> lstServidor = new ArrayList<>();

        // Retorna erro, caso não exista algum dado
        if (TextHelper.isNull(usr.rseCodigo) && TextHelper.isNull(usr.id) && (TextHelper.isNull(usr.matricula) && TextHelper.isNull(responsavel.getRseMatricula()))) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else {
            try {
                final List<String> filter = getFilter(responsavel);
                if (!TextHelper.isNull(usr.rseCodigo)) {
                    TransferObject result = pesquisarServidorController.buscaServidor(usr.rseCodigo, null, false, true, responsavel);
                    lstServidor.add(result);
                } else {
                    lstServidor = pesquisarServidorController.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null,
                            responsavel.isCseSupOrg() ? usr.matricula : responsavel.getRseMatricula(), usr.id, -1, -1, responsavel, true, null,false,null, null,null, true);
                }
                return Response.status(Response.Status.OK)
                        .entity(transformTOs(lstServidor, filter))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                        .build();
            } catch (final ZetraException e) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                LOG.error(e.getMessage(), e);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        }
    }

    @POST
    @Secured
    @Path("/recuperaDadosServidor")
    public Response recuperaDadosServidor(UsuarioRestRequest usr) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONSULTAR_SERVIDOR), List.of(AcessoSistema.ENTIDADE_SER));
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        final ServidorDelegate pesquisarServidorController = new ServidorDelegate();
        List<TransferObject> lstServidor = new ArrayList<TransferObject>();
        // Retorna erro, caso não exista algum dado
        if (TextHelper.isNull(usr)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        try {
            lstServidor = pesquisarServidorController.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, responsavel.getRseMatricula(), usr.id, responsavel);

            final List<String> filter = Arrays.asList("ser_nome", "ser_cpf", "rse_matricula", "ser_data_nasc", "rse_salario", "ser_sexo", "ser_est_civil",  "ser_end", "ser_bairro", "ser_cidade", "ser_compl", "ser_nro", "ser_cep", "ser_uf", "ser_qtd_filhos", "ser_nro_idt", "ser_emissor_idt", "ser_data_idt", "ser_celular", "ser_tel", "rse_codigo");
            return Response.status(Response.Status.OK).entity(transformTOs(lstServidor, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        } catch (final ZetraException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/listaUsuarioServidor")
    public Response listaUsuarioServidor(UsuarioRestRequest usr) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
            Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_CONS_USU_SERVIDORES), List.of(AcessoSistema.ENTIDADE_CSE));
            if (resultadoValidacao != null) {
                return resultadoValidacao;
            }

            if (!TextHelper.isNull(usr) && !TextHelper.isNull(usr.rseCodigo)) {
                final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                List<TransferObject> result = usuarioController.lstUsuariosSerByRseCodigo(usr.rseCodigo, responsavel);
                final List<String> filter = Arrays.asList("ser_nome", "usu_login", "rse_matricula", "ser_cpf", "org_identificador", "org_nome", "est_identificador");

                return Response.status(Response.Status.OK).entity(transformTOs(result, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (UsuarioControllerException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();        }
    }

    private static List<String> getFilter(AcessoSistema responsavel) {
        List<String> filter;
        if (!responsavel.isCseSupOrg()) {
            filter = Arrays.asList(
                    "ser_codigo", "ser_cpf", "ser_matricula", "ser_nome",
                    "rse_matricula", "rse_codigo", "org_codigo", "org_nome",
                    "est_codigo", "est_identificador", "est_nome"
            );
        } else {
            filter = Arrays.asList(
                    "ser_codigo", "ser_cpf", "ser_matricula", "ser_nome",
                    "rse_matricula", "rse_codigo", "org_codigo", "org_nome",
                    "est_codigo", "est_identificador", "est_nome", "srs_descricao",
                    "ser_data_nasc", "rse_salario", "rse_proventos", "rse_margem",
                    "rse_margem_usada", "rse_margem_rest", "rse_margem_2",
                    "rse_margem_usada_2", "rse_margem_rest_2", "rse_margem_3",
                    "rse_margem_usada_3", "rse_margem_rest_3", "rse_tipo", "pos_codigo",
                    "pos_descricao", "ser_email", "usu_login", "org_identificador"
            );
        }
        return filter;
    }
}


