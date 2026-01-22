-- DESENV-9415

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA)
VALUES ('606', NULL, 'Contratos do beneficiário são cancelados caso não atenda os requisitos para ser dependente', 'SN', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('606', '1', 'N');

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO) 
VALUES ('3', 'Cancelamento de benefícios por motivo de perda da condição de dependente');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_CSA_ALTERA, TPA_SUP_ALTERA)
VALUES ('52', 'Data referência para considerar a idade do beneficiário ao calcular o benefício', CONCAT('ESCOLHA[C=Atual', 0x3b, 'M=Mês Anterior', 0x3b, 'P=Período Anterior]'), 'N', 'N', 'N');
