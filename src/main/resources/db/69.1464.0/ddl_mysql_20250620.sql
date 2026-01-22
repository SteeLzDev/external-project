/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/05/2025 12:15:24                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_controle_documento_margem                          */
/*==============================================================*/
create table tb_controle_documento_margem
(
   CDM_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   CDM_BASE64           varchar(244) not null,
   CDM_CODIGO_AUTH      varchar(32) not null,
   CDM_DATA             datetime not null,
   primary key (CDM_CODIGO)
) ENGINE = InnoDB;

alter table tb_controle_documento_margem add constraint FK_R_986 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

