-- DESENV-6040

-- INSERE FUNCAO RELATORIO GERENCIAL
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('430', '4', 'Relatório Gerencial Geral de Consignatária', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

-- ATRIBUI AO PAPEL DE GESTOR A FUNCAO RELATORIO GERENCIAL
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('1', '430');
-- INSERT INTO tb_papel_funcao (PAP_CODIGO, FUN_CODIGO) VALUES ('7', '430');

INSERT INTO tb_item_menu (ITM_CODIGO, MNU_CODIGO, ITM_CODIGO_PAI, ITM_DESCRICAO, ITM_ATIVO, ITM_SEQUENCIA, ITM_SEPARADOR)
VALUES ('207', '2', NULL, 'Gerencial Geral de Consignatária', 1, 69, 'N');

-- HABILITA ACESSO AO RECURSO RELATORIO DE OCORRENCIA DE CONSIGNATARIAS
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14915', '1', '430', '/v3/listarRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', '207', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14916', '1', '430', '/v3/agendarRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14917', '1', '430', '/v3/downloadArquivo', 'subtipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14918', '1', '430', '/v3/excluirArquivo', 'subtipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14919', '1', '430', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');


INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14920', '7', '430', '/v3/listarRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', '207', 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14921', '7', '430', '/v3/agendarRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14922', '7', '430', '/v3/downloadArquivo', 'subtipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14923', '7', '430', '/v3/excluirArquivo', 'subtipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('14924', '7', '430', '/v3/cancelarAgendamentoRelatorio', 'tipo', 'gerencial_csa', '1', 'S', 'S', NULL, 'N', '2');


-- RELATORIO
INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO)
VALUES ('gerencial_csa', '430', '3', 'Relatório Gerencial Geral de Consignatária', 1, 'S', 
'com.zetra.report.reports.RelatorioGerencialGeralCsa', 
'com.zetra.processamento.ProcessaRelatorioGerencialGeralCsa', 
'com.zetra.timer.econsig.job.RelatorioGerencialGeralCsaJob', 
'GerencialConsignataria.jasper', NULL, 
'GerencialConsignatariaCapa.jasper, GerencialConsignatariaSumario.jasper, GerencialConsignatariaObjetivo.jasper, GerencialConsignatariaZetraSoft.jasper, GerencialConsignatariaeConsig.jasper, GerencialConsignatariaeConsig1.jasper, GerencialConsignatariaOrgaoPorServidor.jasper, GerencialConsignatariaMargem.jasper, GerencialConsignatariaMargem1.jasper, GerencialConsignatariaFaixaMargem1.jasper, GerencialConsignatariaComprometimentoMargem1.jasper, GerencialConsignatariaMargem2.jasper, GerencialConsignatariaFaixaMargem2.jasper, GerencialConsignatariaComprometimentoMargem2.jasper, GerencialConsignatariaMargem3.jasper, GerencialConsignatariaFaixaMargem3.jasper, GerencialConsignatariaComprometimentoMargem3.jasper, GerencialConsignatariaCsa.jasper, GerencialConsignatariaContratosCategoria.jasper, GerencialConsignatariaContratosServico.jasper, GerencialConsignatariaInadimplencia.jasper, GerencialConsignatariaConsideracoes.jasper', 
NULL, 30, 'N', NULL);


INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_csa_multiplo', 'Consignatária', '/relatorios/campos_relatorio/campo_csa_multiplo.jsp', 'N');

-- FILTROS
INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_data_execucao', '2', '0', '0', '0', '0', 1, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_csa_multiplo', '2', '0', '0', '0', '0', 2, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_tipo_agendamento', '2', '0', '0', '0', '0', 3, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_periodicidade', '2', '0', '0', '0', '0', 4, NULL, '2');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_envio_email', '1', '0', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('gerencial_csa', 'campo_formato_relatorio_pdf', '2', '0', '0', '0', '0', 6, NULL, '2');
