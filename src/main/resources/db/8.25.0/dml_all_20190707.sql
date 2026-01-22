-- DESENV-11456
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('456', '4', 'Relatório Sintético de Consulta de Margem por Usuário', 'N', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N');

-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('1', '456');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '456');

-- Inclusão no item_menu
INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR) 
VALUES ('218', '2', NULL, 'Sintético de Consulta de Margem por Usuário', '1', '70', 'N');

-- Inclusão do acesso recurso do menu
-- listarConsultaMargemPorUsuario.jsp
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15159','1','456','/v3/listarRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', '218', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15160','7','456','/v3/listarRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', '218', 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15161','1','456','/v3/executarRelatorio', 'tipoRelatorio', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15162','7','456','/v3/executarRelatorio', 'tipoRelatorio', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15163','1','456','/v3/excluirArquivo', 'subtipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15164','7','456','/v3/excluirArquivo', 'subtipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15165','1','456','/v3/downloadArquivo', 'subtipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15166','7','456','/v3/downloadArquivo', 'subtipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15167','1','456','/v3/agendarRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15168','7','456','/v3/agendarRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15169','1','456','/v3/cancelarAgendamentoRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2); 

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15170','7','456','/v3/cancelarAgendamentoRelatorio', 'tipo', 'sint_consulta_margem_por_usuario', '1', 'S', 'S', NULL, 'N', 2);

-- Inclusão do relatório
INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('sint_consulta_margem_por_usuario', '456', NULL, 'Relatório Sintético de Consulta de Margem por Usuário', 1, 'N', 'com.zetra.report.reports.RelatorioSinteticoConsultaMargemPorUsuario', 'com.zetra.processamento.ProcessaRelatorioSinteticoConsultaMargemPorUsuario', '', 'SinteticoConsultaMargemPorUsuario.jasper', '', '', '', 0, 'N', '');

-- Inclusão de tipo de filtro de relatório
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_cor_multiplo', 'Correspondente', '/relatorios/campos_relatorio/campo_cor_multiplo.jsp', 'N');

-- Inclusão de filtro do relatório
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_data_inicio_fim', '2', '0', '0', '0', '0', 1, '', '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_csa', '1', '0', '0', '0', '0', 2, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_cor_multiplo', '1', '0', '0', '0', '0', 3, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_agendado', '1', '0', '0', '0', '0', 4, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_data_execucao', '1', '0', '0', '0', '0', 5, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_tipo_agendamento', '1', '0', '0', '0', '0', 6, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_periodicidade', '1', '0', '0', '0', '0', 7, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_envio_email', '1', '0', '0', '0', '0', 8, '', '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('sint_consulta_margem_por_usuario', 'campo_formato_relatorio', '2', '0', '0', '0', '0', 9, '', '2');
