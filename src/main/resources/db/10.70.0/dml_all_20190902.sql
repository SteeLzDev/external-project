-- DESENV-12207
-- novo parâmetro de sistema para definir a data do termo de uso (CSE)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('664', 'Data de habilitação do termo de uso do SalaryPay (CSE)', 'DATE', 'N', 'N', NULL, 'S', 'S');

-- novo parâmetro de sistema para definir a data do termo de uso (ORG)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('665', 'Data de habilitação do termo de uso do SalaryPay (ORG)', 'DATE', 'N', 'N', NULL, 'S', 'S');

-- novo parâmetro de sistema para definir a data do termo de uso (SER)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('666', 'Data de habilitação do termo de uso do SalaryPay (SER)', 'DATE', 'N', 'N', NULL, 'S', 'S');

-- novo parâmetro de sistema para definir a data do termo de uso (CSA)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('667', 'Data de habilitação do termo de uso do SalaryPay (CSA)', 'DATE', 'N', 'N', NULL, 'S', 'S');

-- novo parâmetro de sistema para definir a data do termo de uso (COR)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('668', 'Data de habilitação do termo de uso do SalaryPay (COR)', 'DATE', 'N', 'N', NULL, 'S', 'S');

-- novo parâmetro de sistema para definir a data do termo de uso (SUP)
INSERT INTO tb_tipo_param_sist_consignante (tpc_codigo, tpc_descricao, tpc_dominio, tpc_cse_altera, tpc_cse_consulta, tpc_vlr_default, tpc_sup_altera, tpc_sup_consulta)
VALUES ('669', 'Data de habilitação do termo de uso do SalaryPay (SUP)', 'DATE', 'N', 'N', NULL, 'S', 'S');

INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('664', '1', '2019-08-20');
INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('665', '1', '2019-08-20');
INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('666', '1', '2019-08-20');
INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('667', '1', '2019-08-20');
INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('668', '1', '2019-08-20');
INSERT INTO tb_param_sist_consignante (tpc_codigo, cse_codigo, psi_vlr) VALUES ('669', '1', '2019-08-20');

-- tipo de ocorrencia
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('162', 'Aceitação do Termo de Uso do SalaryPay');
