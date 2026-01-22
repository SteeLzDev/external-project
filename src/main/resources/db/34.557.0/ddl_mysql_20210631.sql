/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/05/2021 09:30:41                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_banner_publicidade                                 */
/*==============================================================*/
create table tb_banner_publicidade
(
   BPU_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   NSE_CODIGO           varchar(32),
   BPU_DESCRICAO        varchar(100) not null,
   BPU_URL_SAIDA        varchar(255) not null,
   BPU_ORDEM            smallint not null,
   primary key (BPU_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_natureza_servico
   add NSE_DESCRICAO_PORTAL varchar(40);

alter table tb_natureza_servico
   add NSE_TITULO_DETALHE_TOPO varchar(255);

alter table tb_natureza_servico
   add NSE_TEXTO_DETALHE_TOPO text;

alter table tb_natureza_servico
   add NSE_TITULO_DETALHE_RODAPE varchar(255);

alter table tb_natureza_servico
   add NSE_TEXTO_DETALHE_RODAPE text;

alter table tb_natureza_servico
   add NSE_TITULO_CAROUSEL_PROVEDOR varchar(255);

alter table tb_banner_publicidade add constraint FK_R_832 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

alter table tb_banner_publicidade add constraint FK_R_833 foreign key (NSE_CODIGO)
      references tb_natureza_servico (NSE_CODIGO) on delete restrict on update restrict;

