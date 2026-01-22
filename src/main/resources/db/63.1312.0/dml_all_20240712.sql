-- DESENV-21575
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('558', '4', 'Relatório Sintético De Autorização de Margem pelo Servidor', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (FUN_CODIGO, PAP_CODIGO) VALUES ('558', '1');
-- INSERT INTO tb_papel_funcao (FUN_CODIGO, PAP_CODIGO) VALUES ('558', '2');
-- INSERT INTO tb_papel_funcao (FUN_CODIGO, PAP_CODIGO) VALUES ('558', '7');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA)
VALUES ('288', '2', NULL, 'Sintético De Autorização de Margem pelo Servidor', 1, 999);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16975', '1', '558', '/v3/listarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', '288', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16976', '1', '558', '/v3/executarRelatorio', 'tipoRelatorio', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16977', '1', '558', '/v3/excluirArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16978', '1', '558', '/v3/downloadArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16979', '1', '558', '/v3/agendarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16980', '1', '558', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16981', '2', '558', '/v3/listarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', '288', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16982', '2', '558', '/v3/executarRelatorio', 'tipoRelatorio', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16983', '2', '558', '/v3/excluirArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16984', '2', '558', '/v3/downloadArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16985', '2', '558', '/v3/agendarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16986', '2', '558', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16987', '7', '558', '/v3/listarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', '288', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16988', '7', '558', '/v3/executarRelatorio', 'tipoRelatorio', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16989', '7', '558', '/v3/excluirArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16990', '7', '558', '/v3/downloadArquivo', 'subtipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16991', '7', '558', '/v3/agendarRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'S', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16992', '7', '558', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'sintetico_autorizacao_margem_ser', 1, 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('sintetico_autorizacao_margem_ser', '558', NULL, 'Relatório Sintético De Autorização de Margem pelo Servidor', 1, 'N', 'com.zetra.econsig.report.reports.RelatorioSinteticoAutorizacaoMargemSer', 'com.zetra.econsig.job.process.ProcessaRelatorioSinteticoAutorizacaoMargemSer', NULL, 'SinteticoAutorizacaoMargemSer.jasper', NULL, NULL, NULL, 30, 'N', NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_csa_multiplo', '1', '1', '0', '1', '0', '0', 1, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_agendado', '1', '1', '0', '1', '0', '0', 3, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_data_execucao', '1', '1', '0', '1', '0', '0', 4, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_envio_email', '1', '1', '0', '1', '0', '0', 5, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_periodicidade', '1', '1', '0', '1', '0', '0', 6, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_tipo_agendamento', '1', '1', '0', '1', '0', '0', 7, NULL);

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_ORG, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('sintetico_autorizacao_margem_ser', 'campo_formato_relatorio', '2', '2', '0', '2', '0', '0', 8, NULL);

