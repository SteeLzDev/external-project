-- DESENV-16871
UPDATE tb_tipo_entidade SET TEN_CAMPO_ENT_00 = 'PRD_CODIGO', TEN_CAMPO_ENT_02 = 'ADE_CODIGO' WHERE TEN_CODIGO = '20';

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('868', 'Permite a consignatária escolher a forma da numeração das parcelas por serviço', 'SN', 'N', 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('868', '1', 'N');


INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('869', 'Padrão para a forma de numeração das parcelas', CONCAT('ESCOLHA[1=Sequencial', 0x3b, '2=Mantém número ao rejeitar]'), '1', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('869', '1', '1');


INSERT INTO tb_tipo_param_svc (TPS_CODIGO, TPS_DESCRICAO, TPS_CSE_ALTERA, TPS_CSA_ALTERA, TPS_SUP_ALTERA, TPS_PODE_SOBREPOR_RSE)
VALUES ('311', 'Forma de numeração das parcelas', 'N', 'N', 'N', NULL);

