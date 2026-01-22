-- DESENV-22041
INSERT INTO tb_funcao (FUN_CODIGO, GRF_CODIGO, FUN_DESCRICAO, FUN_PERMITE_BLOQUEIO, FUN_EXIGE_TMO, FUN_AUDITAVEL, FUN_RESTRITA_NCA, FUN_EXIGE_SEGUNDA_SENHA_CSE, FUN_EXIGE_SEGUNDA_SENHA_SUP, FUN_EXIGE_SEGUNDA_SENHA_ORG, FUN_EXIGE_SEGUNDA_SENHA_CSA, FUN_EXIGE_SEGUNDA_SENHA_COR) 
VALUES ('559', '23', 'Cadastrar beneficiário na reserva', 'N', 'N', 'S', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('4', 'Cônjuge');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('5','Companheiro(a) com o(a) qual tenha filho ou viva há mais de 5 (cinco) anos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('6','Companheiro(a) com o(a) qual tenha filho ou viva há mais de 5 (cinco) anos ou possua declaração de união estável');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('7','Filho(a) ou enteado(a) até 21 (vinte e um) anos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('8','Filho(a) ou enteado(a)');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('9','Filho(a) ou enteado(a) universitário(a) ou cursando escola técnica de 2º grau até 24 (vinte e quatro) anos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('10', 'universitário(a) ou cursando escola técnica de 2º grau');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('11','Filho(a) ou enteado(a) em qualquer idade quando incapacitado física e/ou mentalmente para o trabalho|');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('12','Irmão(ã) neto(a) ou bisneto(a) sem arrimo dos pais do(a) qual detenha a guarda judicial até 21 (vinte e um) anos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('13','Irmão(ã) neto(a) ou bisneto(a) sem arrimo dos pais do(a) qual detenha a guarda judicial');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('14','Irmão(ã) neto(a) ou bisneto(a) sem arrimo dos pais com idade até 24 anos se ainda estiver cursando estabelecimento de nível superior ou escola técnica de 2º grau desde que tenha detido sua guarda judicial até os 21 anos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('15','Irmão(ã) neto(a) ou bisneto(a) sem arrimo dos pais universitário(a) ou cursando escola técnica de 2° graudo(a) qual detenha a guarda judicial');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('16','Irmão(ã) neto(a) ou bisneto(a) sem arrimo dos pais do(a) qual detenha a guarda judicial em qualquer idade quando incapacitado física e/ou mentalmente para o trabalho');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('17','Pais avós e bisavós');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('18','Menor pobre até 21 (vinte e um) anos que crie e eduque e do qual detenha a guarda judicial');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('19','Menor pobre do qual detenha a guarda judicial');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('20','A pessoa absolutamente incapaz da qual seja tutor ou curador');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('21','Ex-cônjuge que receba pensão de alimentos');
INSERT INTO tb_motivo_dependencia (MDE_CODIGO, MDE_DESCRICAO) VALUES ('22','Ex-cônjuge');

