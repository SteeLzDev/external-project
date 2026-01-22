#language:pt

Funcionalidade: Reservar Margem com Carência

	Contexto: Configuração e pesquisa do servidor
    Dado Sistema permite CPF opcional na pesquisaServidor e senhaAutorizacaoServidor opcional
    E que tenha incluido o item de menu Reservar Margem no favoritos para usuario "csa"
    E UsuCsa com Convenio para Servico Emprestimo
    E Servico configurado para nao exibir campo CET
    E UsuCsa com permissao para confirmar reserva e deferimento manual
    E Parametro Registro Servidor com valores zerados
    Quando UsuCsa Logado
    E Usuario navega para Pagina PesquisarServidor via menu ReservarMargem
    E Usuario seleciona Servico Emprestimo
    E Usuario preenche matricula de servidor
    E Usuario clica no botao Pesquisar 
    
    
  Esquema do Cenário: Reservar Margem para servidor com carência dentro da faixa    
    Quando Usuario preenche campo valor parcela "<valorPrestacao>"
    E Usuario preenche campo numero prestacoes "<prazo>"
    E Usuario preenche valor liquido "<valorLiquido>"
    E Usuario preenche valor carencia "<valorCarencia>"
    E Usuario clica no botao Confirmar para prosseguir
    E verifica se dados estao corretos "<valorPrestacao>", "<prazo>", "<valorLiquido>" e "<valorCarencia>"
    E Usuario clica em Concluir  
    Então Sistema mostra tela de conclusao com as informacoes "<valorPrestacao>", "<prazo>", "<valorLiquido>", "<valorCarencia>" e "<situacao>"
		
    Exemplos: 
      | valorPrestacao | prazo | valorLiquido | valorCarencia |       situacao      |
      |         10     |     1 |         2000 |       15      | Aguard. Deferimento |

	
  Cenário: Reservar Margem para servidor com carência maior que o permitido
    Quando Usuario preenche campo valor parcela "10"
    E Usuario preenche valor liquido "2000"
    E Usuario preenche campo numero prestacoes "10"    
    E Usuario preenche valor carencia "27"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema lanca mensagem de erro de margem "PRAZO TOTAL DO CONTRATO (NÚMERO DE PRESTAÇÕES MAIS CARÊNCIA) DEVE SER MENOR OU IGUAL A 36."   
             

  Cenário: Reservar Margem para servidor com campo carência em branco
    Quando Usuario preenche campo valor parcela "10"
    E Usuario preenche valor liquido "2000"
    E Usuario preenche campo numero prestacoes "1"    
    E Usuario preenche valor carencia "<valorCarencia>"
    E Usuario clica no botao Confirmar para prosseguir
    Entao Sistema exibe mensagem de erro "A carência deve ser informada."   
    

                            