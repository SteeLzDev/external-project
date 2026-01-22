-- DESENV-9852
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_SUP_CONSULTA, TPC_SUP_ALTERA, TPC_CSE_CONSULTA, TPC_CSE_ALTERA) 
VALUES ('625', NULL, 'Percentual agenciamento dos contratos de benefícios', 'FLOAT', '0', 'N', 'N', 'N', 'N'); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('625', '1', '0'); 

-- funcao e item menu

INSERT INTO tb_funcao (fun_codigo, grf_codigo, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('440', '4', 'Relatório de Comissionamento e Agenciamento Analítico para Operadoras de Beneficio', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'); 

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '440');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR) 
VALUES ('215', '2', NULL, 'Comissionamento e Agenciamento Analítico para Operadoras', 1, 81, 'N'); 

-- acesso recurso

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15028', '7', '440', '/v3/excluirArquivo', 'subtipo', 'agenciamento_analitico_operadora', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15029', '7', '440', '/v3/executarRelatorio', 'tipoRelatorio', 'agenciamento_analitico_operadora', 1, 'S', 'S', NULL, 'S', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15030', '7', '440', '/v3/listarRelatorio', 'tipo', 'agenciamento_analitico_operadora', 1, 'S', 'S', '215', 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15031', '7', '440', '/v3/downloadArquivo', 'subtipo', 'agenciamento_analitico_operadora', 1, 'S', 'S', NULL, 'N', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15032', '7', '440', '/v3/agendarRelatorio', 'tipo', 'agenciamento_analitico_operadora', 1, 'S', 'S', NULL, 'S', '2'); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15033', '7', '440', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'agenciamento_analitico_operadora', 1, 'S', 'S', NULL, 'N', '2'); 

-- -- relatorio

INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO) 
VALUES ('agenciamento_analitico_operadora', '440', NULL, 'Relatório de Comissionamento e Agenciamento Analítico', 1, 'N', 'com.zetra.report.reports.RelatorioComissionamentoAgenciamentoAnalitico', 'com.zetra.processamento.ProcessaRelatorioComissionamentoAgenciamentoAnalitico', NULL, 'ComissionamentoAgenciamentoAnalitico.jasper', NULL, NULL, NULL, 30, 'N', NULL);

-- filtro relatorio

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_data_periodo', '0', '0', '0', '0', '0', 1, 90, '2'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_beneficio', '0', '0', '0', '0', '0', 4, null, '1'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_org', '0', '0', '0', '0', '0', 3, null, '1'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_tipo_agendamento', '0', '0', '0', '0', '0', 8, null, '1'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_agendado', '0', '0', '0', '0', '0', 6, null, '1'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_formato_relatorio', '0', '0', '0', '0', '0', 5, null, '2'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_periodicidade', '0', '0', '0', '0', '0', 9, null, '1'); 

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('agenciamento_analitico_operadora', 'campo_data_execucao', '0', '0', '0', '0', '0', 7, null, '1');
