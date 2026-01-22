-- DESENV-18083
INSERT INTO tb_tipo_ocorrencia (toc_codigo,toc_descricao) VALUES ('205','Aceitação do Termo de Uso do fluxo de cadastro de senha');

INSERT INTO tb_tipo_ocorrencia (toc_codigo,toc_descricao) VALUES ('206','Aceitação da Política de Privacidade do fluxo de cadastro de senha');

DELETE FROM tb_texto_sistema WHERE tex_chave = 'mensagem.termo.de.consentimento.cadastro.usuario.servidor.texto';

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR)
VALUES ('534', '4', 'Gerar Política de Privacidade em PDF', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_relatorio (REL_CODIGO, FUN_CODIGO, TAG_CODIGO, REL_TITULO, REL_ATIVO, REL_AGENDADO, REL_CLASSE_RELATORIO, REL_CLASSE_PROCESSO, REL_CLASSE_AGENDAMENTO, REL_TEMPLATE_JASPER, REL_TEMPLATE_DINAMICO, REL_TEMPLATE_SUBRELATORIO, REL_TEMPLATE_SQL, REL_QTD_DIAS_LIMPEZA, REL_CUSTOMIZADO, REL_AGRUPAMENTO) 
VALUES('politica_privacidade', '534', NULL, 'Política de Privacidade', 1, 'N', 'com.zetra.econsig.report.reports.PoliticaPrivacidade', 'com.zetra.processamento.ProcessaPoliticaPrivacidade', NULL, 'PoliticaPrivacidade.jasper', NULL, NULL, NULL, 30, 'N', NULL);