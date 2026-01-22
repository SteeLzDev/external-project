-- ORACLE

CREATE TABLE tb_dashboard_flex (
    dfl_codigo VARCHAR2(32) NOT NULL,
    pap_codigo VARCHAR2(32) NOT NULL,
    dfl_nome VARCHAR2(40) NOT NULL,
    dfl_compartilhamento CHAR(1) NOT NULL,
    dfl_ativo CHAR(1) NOT NULL,
    CONSTRAINT pk_dashboard_flex PRIMARY KEY (dfl_codigo)
);

CREATE TABLE tb_dashboard_flex_consulta (
    dfo_codigo VARCHAR2(32) NOT NULL,
    dfl_codigo VARCHAR2(32) NOT NULL,
    dfo_titulo VARCHAR2(100) NOT NULL,
    dfo_index VARCHAR2(100) NOT NULL,
    dfo_tipo_index CHAR(1) NOT NULL,
    dfo_usa_toolbar CHAR(1) NOT NULL,
    dfo_ativo CHAR(1) NOT NULL,
    dfo_slice CLOB,
    CONSTRAINT pk_dashboard_flex_consulta PRIMARY KEY (dfo_codigo),
    CONSTRAINT fk_dfo_dfl_1 FOREIGN KEY (dfl_codigo) REFERENCES tb_dashboard_flex(dfl_codigo)
);

CREATE INDEX idx_dfo_dfl_1 ON tb_dashboard_flex_consulta(dfl_codigo);

CREATE TABLE tb_dashboard_flex_toolbar (
    dft_codigo VARCHAR2(32) NOT NULL,
    dfo_codigo VARCHAR2(32) NOT NULL,
    dft_item VARCHAR2(40) NOT NULL,
    CONSTRAINT pk_dashboard_flex_toolbar PRIMARY KEY (dft_codigo),
    CONSTRAINT fk_dft_dfo_1 FOREIGN KEY (dfo_codigo) REFERENCES tb_dashboard_flex_consulta(dfo_codigo)
);

CREATE INDEX idx_dft_dfo_1 ON tb_dashboard_flex_toolbar(dfo_codigo);