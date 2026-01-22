-- DESENV-11063
INSERT INTO tb_status_contrato_beneficio (SCB_CODIGO, SCB_DESCRICAO) 
VALUES ('7', 'Cancelamento Solicitado pelo Beneficiário');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('711', 'Permite cancelamento de benefício sem a aprovação do suporte', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) 
VALUES ('711', '1', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('173', 'Solicitação de Cancelamento de Contrato Benefício');

INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('471', '23', 'Permite a solicitação de cancelamento de contratos de benefícios', 'N', 'N', 'S', 'N', 'N', 'P', 'N', 'N', 'N');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15586', '6', '471', '/v3/aprovarSolicitacao', 'acao', 'solicitarCancelamento', 1, 'S', 'N', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15587', '6', '424', '/v3/aprovarSolicitacao', 'acao', 'salvar', 1, 'S', 'N', NULL, 'S', 2);

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP) 
VALUES ('15588', '7', '424', '/v3/aprovarSolicitacao', 'acao', 'desfazerCancelamento', 1, 'S', 'N', NULL, 'S', 2);

INSERT INTO tb_agendamento (AGD_CODIGO, TAG_CODIGO, SAG_CODIGO, USU_CODIGO, AGD_DESCRICAO, AGD_JAVA_CLASS_NAME, AGD_DATA_CADASTRO, AGD_DATA_PREVISTA, REL_CODIGO) 
VALUES ('39', '1', '1', '1', 'Desfazer Solicitacao de Cancelamento de Beneficio Realizado pelo Servidor', 'com.zetra.timer.econsig.job.DesfazerCancelamentoSolicitacaoBeneficiarioJob', CURDATE(), CURDATE(), NULL);

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO) 
VALUES ('6', 'Cancelamento de beneficios pelo servidor');

UPDATE tb_tipo_motivo_operacao SET ACA_CODIGO = '6' WHERE TEN_CODIGO = '104' AND TMO_IDENTIFICADOR IN ('PS02', 'PS03', 'PS04', 'PS11', 'PS12', 'PS13', 'PS14', 'PS15', 'PS16', 'PS17', 'PS18', 'PS19', 'PS20', 'PS21');
