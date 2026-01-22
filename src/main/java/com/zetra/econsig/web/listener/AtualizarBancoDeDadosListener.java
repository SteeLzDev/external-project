package com.zetra.econsig.web.listener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaAtualizacaoBaseDeDados;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AtualizarBancoDeDadosListener</p>
 * <p>Description: Listener que realiza atualizações SQL no banco de dados do sistema</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Component
public class AtualizarBancoDeDadosListener {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizarBancoDeDadosListener.class);

    private static final String DIRETORIO_RESOURCES_DB = "/db/";
    private static final String CLASSPATH_RESOURCES_DB = "classpath:db/";

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private ResourceLoader resourceLoader;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Atualização do Banco de Dados do eConsig");

        // Expressão regular para busca dos arquivos por nome
        final Pattern sqlFilePattern;
        if (DAOFactory.isMysql()) {
            sqlFilePattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)/(ddl|dml)_(mysql|all)_([0-9]{8})(_DESENV-[0-9]+)?.sql");
        } else {
            sqlFilePattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.([0-9]+)/(ddl|dml)_(oracle|all)_([0-9]{8})(_DESENV-[0-9]+)?.sql");
        }

        try {
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

            final List<Resource> dbResources = new ArrayList<>();
            final Resource rootDbSql = resourceLoader.getResource(CLASSPATH_RESOURCES_DB);
            if (rootDbSql.getFile().canRead()) {
                final List<String> fileNames = FileHelper.getFilesInDir(rootDbSql.getFile().getAbsolutePath(), file -> file.isDirectory() || file.getName().toLowerCase().endsWith(".sql"));
                for (String name : fileNames) {
                    dbResources.add(resourceLoader.getResource(CLASSPATH_RESOURCES_DB + name));
                }
            }

            if (dbResources.isEmpty()) {
                throw new RuntimeException("Os arquivos SQL de banco de dados não foram encontrados.");
            }

            final Map<String, String> sqlFiles = new HashMap<>();
            for (Resource dbResource : dbResources) {
                String name = dbResource.getURI().toASCIIString();
                String fileName = name.substring(name.indexOf(DIRETORIO_RESOURCES_DB) + DIRETORIO_RESOURCES_DB.length());

                if (sqlFilePattern.matcher(fileName).matches()) {
                    // Verificar os arquivos que já foram executados:
                    if (!sistemaController.temDbOcorrencia(dbResource.getFilename(), responsavel)) {
                        // Se não foi executado, obtém o conteúdo e adiciona na fila de execução
                        String content = FileHelper.readAll(dbResource.getInputStream(), StandardCharsets.ISO_8859_1.name());
                        sqlFiles.put(fileName, content);
                    }
                }
            }

            // Ordena por versão e data do arquivo
            List<String> fileNames = new ArrayList<>(sqlFiles.keySet());
            if (!fileNames.isEmpty()) {
                Collections.sort(fileNames, (n1, n2) -> {
                    int major1 = 0, major2 = 0;
                    int minor1 = 0, minor2 = 0;
                    int patch1 = 0, patch2 = 0;
                    int datef1 = 0, datef2 = 0;
                    int typef1 = 0, typef2 = 0;

                    Matcher matcher = sqlFilePattern.matcher(n1);
                    if (matcher.matches()) {
                        major1 = Integer.valueOf(matcher.group(1));
                        minor1 = Integer.valueOf(matcher.group(2));
                        patch1 = Integer.valueOf(matcher.group(3));
                        datef1 = Integer.valueOf(matcher.group(6));
                        typef1 = matcher.group(4).equalsIgnoreCase("ddl") ? 0 : 1;
                    }

                    matcher = sqlFilePattern.matcher(n2);
                    if (matcher.matches()) {
                        major2 = Integer.valueOf(matcher.group(1));
                        minor2 = Integer.valueOf(matcher.group(2));
                        patch2 = Integer.valueOf(matcher.group(3));
                        datef2 = Integer.valueOf(matcher.group(6));
                        typef2 = matcher.group(4).equalsIgnoreCase("ddl") ? 0 : 1;
                    }
                    
                    if (major1 == 0 && major2 > 0) {
                        return 1;
                    } else if (major2 == 0 && major1 > 0) {
                        return -1;
                    } else if (major2 > major1) {
                        return -1;
                    } else if (major2 < major1) {
                        return 1;
                    } else if (minor2 > minor1) {
                        return -1;
                    } else if (minor2 < minor1) {
                        return 1;
                    } else if (patch2 > patch1) {
                        return -1;
                    } else if (patch2 < patch1) {
                        return 1;
                    } else if (typef2 > typef1) {
                        return -1;
                    } else if (typef2 < typef1) {
                        return 1;
                    } else if (datef2 > datef1) {
                        return -1;
                    } else if (datef2 < datef1) {
                        return 1;
                    } else {
                        return 0;
                    }
                });

                for (String fileName : fileNames) {
                    LOG.info("Novo arquivo SQL encontrado: " + fileName);
                }

                // Só bloqueia se estiver ativo
                Short statusSistema = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                boolean podeBloquearSistema = statusSistema.equals(CodedValues.STS_ATIVO);

                if (podeBloquearSistema) {
                    // Bloquear o sistema, informando que está sendo atualizado
                    cseDelegate.alteraStatusSistema(CodedValues.CSE_CODIGO_SISTEMA, CodedValues.STS_INDISP, ApplicationResourcesHelper.getMessage("mensagem.sistema.indisponivel.para.atualizacao", responsavel), true, responsavel);
                }

                // Criar processo para atualização da base de dados, liberando a finalização da carga do servidor de aplicação
                ProcessaAtualizacaoBaseDeDados processo = new ProcessaAtualizacaoBaseDeDados(fileNames, sqlFiles, podeBloquearSistema, responsavel);
                processo.start();
                ControladorProcessos.getInstance().incluir(responsavel.getUsuCodigo(), processo);
            }
        } catch (IOException ex) {
            LOG.error("Ocorreram erros ao obter os arquivos SQL para atualização.", ex);

        } catch (ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
