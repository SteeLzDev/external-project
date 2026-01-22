-- DESENV-15001
UPDATE tb_texto_sistema SET TEX_TEXTO = 'Você mora ou já morou no endereço' WHERE TEX_CHAVE = 'rotulo.questionario.servidor.endereco';

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_nome_mae', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_nome_pai', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_nome_conjuge', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_dia_nasc', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_mes_nasc', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_ano_nasc', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_cidade_nasc', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_dia_idt', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_mes_idt', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_ano_idt', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_num_identidade', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_num_cart_trabalho', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_num_pis', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_endereco', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_num_endereco', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_bairro', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_cidade', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_cep', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_telefone', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_celular', 'N');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_categoria', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_dia_admissao', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_mes_admissao', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_ano_admissao', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_agencia_salario', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_conta_salario', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('cadServidorAvancado_municipio_lotacao', 'N');
