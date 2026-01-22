/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     30/03/2022 15:22:22                          */
/*==============================================================*/


drop table tb_arquivo_mensagem cascade constraints;


/*==============================================================*/
/* Table: tb_arquivo_mensagem                                   */
/*==============================================================*/
create table tb_arquivo_mensagem  (
   men_codigo           varchar2(32)                    not null,
   arq_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   amn_data_criacao     date                            not null,
   amn_nome             varchar2(255)                   not null,
   amn_ip_acesso        varchar2(45)                    not null,
   constraint pk_tb_arquivo_mensagem primary key (men_codigo, arq_codigo)
);

/*==============================================================*/
/* Index: r_857_fk                                              */
/*==============================================================*/
create index r_857_fk on tb_arquivo_mensagem (
   men_codigo asc
);

/*==============================================================*/
/* Index: r_858_fk                                              */
/*==============================================================*/
create index r_858_fk on tb_arquivo_mensagem (
   arq_codigo asc
);

/*==============================================================*/
/* Index: r_865_fk                                              */
/*==============================================================*/
create index r_865_fk on tb_arquivo_mensagem (
   usu_codigo asc
);

alter table tb_arquivo_mensagem
   add constraint fk_tb_arqui_r_857_tb_mensa foreign key (men_codigo)
      references tb_mensagem (men_codigo);

alter table tb_arquivo_mensagem
   add constraint fk_tb_arqui_r_858_tb_arqui foreign key (arq_codigo)
      references tb_arquivo (arq_codigo);

alter table tb_arquivo_mensagem
   add constraint fk_tb_arqui_r_865_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);
