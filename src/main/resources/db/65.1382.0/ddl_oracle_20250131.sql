/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     29/01/2025 18:24:29                          */
/*==============================================================*/


alter table tb_dados_fato_parcela
   drop constraint fk_tb_dados_r_976_tb_fato_;

drop index r_976_fk;

CALL dropTableIfExists('tmp_tb_dados_fato_parcela');

rename tb_dados_fato_parcela to tmp_tb_dados_fato_parcela;

/*==============================================================*/
/* Table: tb_dados_fato_parcela                                 */
/*==============================================================*/
create table tb_dados_fato_parcela  (
   fap_codigo           integer                         not null,
   dfp_nome             varchar2(255)                   not null,
   dfp_cpf              char(19)                        not null,
   dfp_matricula        varchar2(20)                    not null,
   dfp_numero           integer                         not null,
   dfp_identificador    varchar2(40)                    not null,
   dfp_indice           varchar2(32),
   dfp_valor            number(13,2)                    not null,
   dfp_valor_liberado   number(13,2),
   dfp_prazo            integer,
   dfp_pagas            integer,
   dfp_numero_parcela   smallint,
   dfp_periodo          date                            not null,
   dfp_valor_previsto   number(13,2)                    not null,
   dfp_valor_realizado  number(13,2),
   dfp_data_realizado   date,
   dfp_status_parcela   varchar2(40)                    not null
);

insert into tb_dados_fato_parcela (fap_codigo, dfp_nome, dfp_cpf, dfp_matricula, dfp_numero, dfp_identificador, dfp_indice, dfp_valor, dfp_valor_liberado, dfp_prazo, dfp_pagas, dfp_numero_parcela, dfp_periodo, dfp_valor_previsto, dfp_valor_realizado, dfp_status_parcela)
select fap_codigo, dfp_nome, dfp_cpf, dfp_matricula, dfp_numero, dfp_identificador, dfp_indice, dfp_valor, dfp_valor_liberado, dfp_prazo, dfp_pagas, dfp_numero_parcela, dfp_periodo, dfp_valor_previsto, dfp_valor_realizado, dfp_status_parcela
from tmp_tb_dados_fato_parcela;

CALL dropTableIfExists('tmp_tb_dados_fato_parcela');

/*==============================================================*/
/* Index: r_976_fk                                              */
/*==============================================================*/
create index r_976_fk on tb_dados_fato_parcela (
   fap_codigo asc
);

alter table tb_dados_fato_parcela
   add constraint fk_tb_dados_r_976_tb_fato_ foreign key (fap_codigo)
      references tb_fato_parcela (fap_codigo);

