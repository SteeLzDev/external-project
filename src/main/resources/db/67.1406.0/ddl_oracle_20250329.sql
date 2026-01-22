/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     05/03/2025 15:39:44                          */
/*==============================================================*/


alter table tb_anexo_comunicacao
   drop constraint fk_tb_anexo_r_424_tb_comun;

alter table tb_comunicacao
   drop constraint fk_tb_comun_r_279_tb_usuar;

alter table tb_comunicacao
   drop constraint fk_tb_comun_r_282_tb_comun;

alter table tb_comunicacao
   drop constraint fk_tb_comun_r_462_tb_assun;

alter table tb_comunicacao_csa
   drop constraint fk_tb_comun_r_280_tb_comun;

alter table tb_comunicacao_cse
   drop constraint fk_tb_comun_r_387_tb_comun;

alter table tb_comunicacao_org
   drop constraint fk_tb_comun_r_778_tb_comun;

alter table tb_comunicacao_ser
   drop constraint fk_tb_comun_r_389_tb_comun;

alter table tb_leitura_comunicacao_usuario
   drop constraint fk_tb_leitu_r_283_tb_comun;

drop index cmn_numero_idx;

drop index idx_pendencia;

drop index r_462_fk;

drop index r_282_fk;

drop index r_279_fk;

alter table tb_comunicacao
   drop primary key cascade;

CALL dropTableIfExists('tmp_tb_comunicacao');

rename tb_comunicacao to tmp_tb_comunicacao;

alter table tb_assunto_comunicacao add asc_consignacao smallint default 0 not null;

/*==============================================================*/
/* Table:  tb_comunicacao                                       */
/*==============================================================*/
create table tb_comunicacao  (
   cmn_codigo           varchar2(32)                    not null,
   ade_codigo           varchar2(32),
   cmn_codigo_pai       varchar2(32),
   usu_codigo           varchar2(32)                    not null,
   cmn_pendencia        smallint                       default 0 not null,
   cmn_data             date                            not null,
   cmn_texto            clob                            not null,
   cmn_ip_acesso        varchar2(45)                    not null,
   cmn_alerta_email     smallint                       default 0 not null,
   cmn_numero           integer                        default 0 not null,
   cmn_copia_email_sms  smallint                       default 0 not null,
   asc_codigo           varchar2(32),
   constraint pk_tb_comunicacao primary key (cmn_codigo)
);

insert into tb_comunicacao (cmn_codigo, cmn_codigo_pai, usu_codigo, cmn_pendencia, cmn_data, cmn_texto, cmn_ip_acesso, cmn_alerta_email, cmn_numero, cmn_copia_email_sms, asc_codigo)
select cmn_codigo, cmn_codigo_pai, usu_codigo, cmn_pendencia, cmn_data, cmn_texto, cmn_ip_acesso, cmn_alerta_email, cmn_numero, cmn_copia_email_sms, asc_codigo
from tmp_tb_comunicacao;

CALL dropTableIfExists('tmp_tb_comunicacao');

/*==============================================================*/
/* Index: r_279_fk                                              */
/*==============================================================*/
create index r_279_fk on tb_comunicacao (
   usu_codigo asc
);

/*==============================================================*/
/* Index: r_282_fk                                              */
/*==============================================================*/
create index r_282_fk on tb_comunicacao (
   cmn_codigo_pai asc
);

/*==============================================================*/
/* Index: r_462_fk                                              */
/*==============================================================*/
create index r_462_fk on tb_comunicacao (
   asc_codigo asc
);

/*==============================================================*/
/* Index: r_978_fk                                              */
/*==============================================================*/
create index r_978_fk on tb_comunicacao (
   ade_codigo asc
);

/*==============================================================*/
/* Index: idx_pendencia                                         */
/*==============================================================*/
create index idx_pendencia on tb_comunicacao (
   cmn_pendencia asc
);

/*==============================================================*/
/* Index: cmn_numero_idx                                        */
/*==============================================================*/
create index cmn_numero_idx on tb_comunicacao (
   cmn_numero asc
);

alter table tb_anexo_comunicacao
   add constraint fk_tb_anexo_r_424_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

alter table tb_comunicacao
   add constraint fk_tb_comun_r_279_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

alter table tb_comunicacao
   add constraint fk_tb_comun_r_282_tb_comun foreign key (cmn_codigo_pai)
      references tb_comunicacao (cmn_codigo);

alter table tb_comunicacao
   add constraint fk_tb_comun_r_462_tb_assun foreign key (asc_codigo)
      references tb_assunto_comunicacao (asc_codigo);

alter table tb_comunicacao
   add constraint fk_tb_comun_r_978_tb_aut_d foreign key (ade_codigo)
      references tb_aut_desconto (ade_codigo);

alter table tb_comunicacao_csa
   add constraint fk_tb_comun_r_280_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

alter table tb_comunicacao_cse
   add constraint fk_tb_comun_r_387_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

alter table tb_comunicacao_org
   add constraint fk_tb_comun_r_778_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

alter table tb_comunicacao_ser
   add constraint fk_tb_comun_r_389_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

alter table tb_leitura_comunicacao_usuario
   add constraint fk_tb_leitu_r_283_tb_comun foreign key (cmn_codigo)
      references tb_comunicacao (cmn_codigo);

