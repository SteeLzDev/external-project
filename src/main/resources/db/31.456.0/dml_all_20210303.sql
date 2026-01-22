-- DESENV-15386
-- Inclusão de função
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('505', '4', 'Relatório de Decisões Judiciais', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('1', '505');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('2', '505');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('3', '505');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '505');

-- Inclusao no item_menu
INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR) 
VALUES ('255', '2', NULL, 'Relatório de Decisões Judiciais', '1', '84', 'N');

-- Inclusao do acesso recurso do menu
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16192', '1', '505', '/v3/listarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', '255', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16193', '1', '505', '/v3/executarRelatorio', 'tipoRelatorio', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16194', '1', '505', '/v3/excluirArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16195', '1', '505', '/v3/downloadArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16196', '1', '505', '/v3/agendarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16197', '1', '505', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16198', '2', '505', '/v3/listarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', '255', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16199', '2', '505', '/v3/executarRelatorio', 'tipoRelatorio', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16200', '2', '505', '/v3/excluirArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16201', '2', '505', '/v3/downloadArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16202', '2', '505', '/v3/agendarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16203', '2', '505', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16204', '3', '505', '/v3/listarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', '255', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16205', '3', '505', '/v3/executarRelatorio', 'tipoRelatorio', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16206', '3', '505', '/v3/excluirArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16207', '3', '505', '/v3/downloadArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16208', '3', '505', '/v3/agendarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16209', '3', '505', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16210', '7', '505', '/v3/listarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', '255', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16211', '7', '505', '/v3/executarRelatorio', 'tipoRelatorio', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16212', '7', '505', '/v3/excluirArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16213', '7', '505', '/v3/downloadArquivo', 'subtipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16214', '7', '505', '/v3/agendarRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16215', '7', '505', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);


-- Acessos recurso para campo_decisao_judicial
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16216', '1', '505', '/v3/listarCidades', 'acao', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16217', '2', '505', '/v3/listarCidades', 'acao', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16218', '3', '505', '/v3/listarCidades', 'acao', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('16219', '7', '505', '/v3/listarCidades', 'acao', 'decisao_judicial', '1', 'S', 'S', NULL, 'N', 2);


-- Inclusao de tipo de filtro de relatorio
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_decisao_judicial', 'Decisão Judicial', '/relatorios/campos_relatorio/campo_decisao_judicial.jsp', 'N');


-- Inclusao do relatorio
INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('decisao_judicial', '505', NULL, 'Relatório de Decisões Judiciais', 1, 'N', 'com.zetra.report.reports.RelatorioDecisaoJudicial', 'com.zetra.processamento.ProcessaRelatorioDecisaoJudicial', '', 'DecisaoJudicial.jasper', '', '', '', 0, 'N', '');

-- Inclusao de filtro do relatorio
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_data_inclusao', '2', '2', '0', '2', '0', 2, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_csa', '1', '1', '0', '1', '0', 2, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_cor', '0', '0', '0', '1', '0', 3, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_est', '1', '1', '0', '1', '0', 4, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_org', '1', '1', '0', '1', '0', 5, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_svc', '1', '1', '0', '1', '0', 6, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_status_contrato', '1', '1', '0', '1', '0', 7, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_status_servidor', '1', '1', '0', '1', '0', 8, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_decisao_judicial', '1', '1', '0', '1', '0', 9, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_matricula', '1', '1', '0', '1', '0', 10, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_cpf', '1', '1', '0', '1', '0', 11, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_agendado', '1', '1', '0', '1', '0', 12, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_data_execucao', '1', '1', '0', '1', '0', 13, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_tipo_agendamento', '1', '1', '0', '1', '0', 14, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_periodicidade', '1', '1', '0', '1', '0', 15, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_envio_email', '1', '1', '0', '1', '0', 16, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('decisao_judicial', 'campo_formato_relatorio', '2', '2', '0', '2', '0', 17, '', '2');
