# Funcionalidade de Seleção de Mês - Piggy App

## Visão Geral

Foi implementada uma funcionalidade que permite ao usuário selecionar o mês que deseja visualizar
gastos e receitas, funcionando tanto no Android quanto no iOS. A funcionalidade inclui restrições
para não permitir seleção de meses futuros.

## Componentes Implementados

### 1. MonthSelector Component (`utils/ui/MonthSelector.kt`)

- **MonthYear**: Data class para representar mês-ano com comparação de datas
- **MonthSelector**: Componente principal compacto com navegação por setas
- **CompactMonthSelector**: Versão compacta com dropdown
- **HorizontalMonthSelector**: Versão horizontal scrollable

### 2. Integração nas Telas

#### HomeScreen

- Seletor de mês compacto posicionado entre a saudação do usuário e os cards de receita/gastos
- Altura reduzida (40dp) para otimizar espaço
- Filtragem automática de dados financeiros baseada no mês selecionado

#### ReportsScreen

- Seletor de mês no cabeçalho da tela de relatórios
- Filtragem de todas as métricas (receitas, gastos, transações) por mês
- Título dinâmico mostrando o mês selecionado

#### ViewIncomeScreen (Ver Receitas)

- Seletor de mês integrado no cabeçalho
- Filtragem de receitas por mês selecionado
- Reset automático do filtro de categoria ao mudar mês
- Visível apenas quando não está em modo seleção

#### ViewExpensesScreen (Ver Gastos)

- Seletor de mês integrado no cabeçalho
- Filtragem de gastos por mês selecionado
- Reset automático do filtro de categoria ao mudar mês
- Visível apenas quando não está em modo seleção

## Funcionalidades

### Navegação por Mês com Restrições
- Botões de navegação para avançar/retroceder meses
- **Limitação**: Não permite navegar para meses futuros
- Botão "próximo mês" desabilitado quando o mês atual está selecionado
- Suporte a navegação entre anos (respeitando a limitação)
- Interface intuitiva com nomes dos meses em português

### Filtragem de Dados
- Receitas mensais filtradas por mês selecionado
- Gastos mensais filtrados por mês selecionado
- Gastos por categoria específicos do mês
- Transações recentes limitadas ao mês escolhido
- Reset automático de filtros de categoria ao trocar mês

### Design Compacto

- Tamanho reduzido da barra de seleção (40dp altura)
- Ícones menores (20dp) e botões compactos (32dp)
- Padding reduzido para otimizar espaço na tela
- Elevação sutil (2dp) para integração visual harmoniosa

### Compatibilidade Multiplataforma
- Funciona nativamente no Android
- Funciona nativamente no iOS
- Usa Jetpack Compose Multiplatform
- Biblioteca kotlinx-datetime para manipulação de datas

## Arquitetura Implementada

### Estados Atualizados

- `HomeUiState`: Campo `selectedMonth` com valor padrão atual
- `ReportsUiState`: Campo `selectedMonth` com valor padrão atual
- `TransactionListUiState`: Campo `selectedMonth` para telas de transações

### ViewModels Atualizados
- `HomeViewModel`: Método `changeSelectedMonth()`
- `ReportsViewModel`: Método `changeSelectedMonth()`
- `TransactionListViewModel`: Método `changeSelectedMonth()` com filtragem

### Use Cases Modificados
- `GetFinancialSummaryUseCase`: Suporte a filtragem por `MonthYear`

## Como Usar

1. **Tela Inicial**: Seletor compacto logo após a saudação, valores atualizados automaticamente
2. **Ver Receitas**: Seletor no cabeçalho, lista filtrada por mês, filtro de categoria resetado
3. **Ver Gastos**: Seletor no cabeçalho, lista filtrada por mês, filtro de categoria resetado
4. **Relatórios**: Seletor no cabeçalho, todos os gráficos e listas filtrados
5. **Navegação**: Setas laterais para navegar, bloqueio automático de meses futuros

## Restrições Implementadas

### Limitação Temporal

- Não permite seleção de meses futuros
- Botão "próximo" desabilitado visualmente quando aplicável
- Comparação de datas implementada na classe `MonthYear`

### UX/UI

- Seletor compacto e otimizado
- Visível apenas quando não em modo seleção (nas telas de transação)
- Reset automático de filtros secundários

## Código Principal

Componente principal:
```kotlin
composeApp/src/commonMain/kotlin/com/miga/piggy/utils/ui/MonthSelector.kt
```

Integrado em:

- `HomeScreen.kt` (linha ~180) - Seletor compacto
- `ReportsScreen.kt` (linha ~140) - No cabeçalho
- `ViewIncomeScreen.kt` (linha ~135) - Condicionalmente visível
- `ViewExpensesScreen.kt` (linha ~135) - Condicionalmente visível

## Melhorias Futuras

- Seleção de intervalo de meses
- Comparação entre meses
- Modo de visualização anual
- Persistência da seleção de mês entre sessões
- Animações de transição entre meses
- Indicador visual de meses com/sem transações