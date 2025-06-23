package bagagem.gui;

import bagagem.model.DanificacaoBagagem;
import bagagem.model.ExtravioBagagem;
import bagagem.model.ItemEsquecidoAviao;
import bagagem.model.Processo;
import bagagem.model.ProcessoRepository;
import bagagem.model.Recibo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ListagemProcessosPanel é um JPanel para exibir e gerenciar a lista de processos de bagagem.
 * Permite listar todos os processos e buscar por um processo específico, além de editar e excluir.
 */
public class ListagemProcessosPanel extends JPanel {

    private JTable tabelaProcessos;
    private DefaultTableModel tableModel;
    private JTextField txtBuscaBase;
    private JTextField txtBuscaNumeroProcesso;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private MainFrame parentFrame; // Referência ao MainFrame para troca de painéis

    // Construtor modificado para receber o MainFrame
    public ListagemProcessosPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame; // Armazena a referência
        setOpaque(true);
        setLayout(new BorderLayout()); // Layout principal do painel

        // --- Painel Superior para Título e Busca/Ações ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4; // Ocupa 4 colunas
        JLabel titleLabel = new JLabel("Lista e Consulta de Processos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, gbc);

        // Campos de Busca (RF16)
        gbc.gridwidth = 1; // Volta para 1 coluna
        gbc.gridy++;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Base:"), gbc);
        gbc.gridx = 1;
        txtBuscaBase = new JTextField(8);
        topPanel.add(txtBuscaBase, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Número Processo:"), gbc);
        gbc.gridx = 3;
        txtBuscaNumeroProcesso = new JTextField(12);
        topPanel.add(txtBuscaNumeroProcesso, gbc);

        // Botões de Ação na Busca
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton btnBuscar = new JButton("Buscar Processo");
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProcesso();
            }
        });
        topPanel.add(btnBuscar, gbc);

        // Botão para Listar Todos (RF15)
        gbc.gridx = 1; // Ao lado do botão Buscar
        JButton btnListarTodos = new JButton("Listar Todos Processos");
        btnListarTodos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarProcessosNaTabela();
            }
        });
        topPanel.add(btnListarTodos, gbc);

        // Botões de Ação (Editar e Excluir)
        gbc.gridx = 2; // Ao lado do Listar Todos
        JButton btnEditar = new JButton("Editar Processo");
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProcessoSelecionado();
            }
        });
        topPanel.add(btnEditar, gbc);

        gbc.gridx = 3; // Ao lado do Editar
        JButton btnExcluir = new JButton("Excluir Processo");
        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirProcessoSelecionado();
            }
        });
        topPanel.add(btnExcluir, gbc);

        add(topPanel, BorderLayout.NORTH); // Adiciona o painel superior na parte de cima

        // --- Tabela de Processos ---
        // ADICIONE "ID" AQUI:
        String[] colunas = {"ID", "Base", "Número", "Tipo Processo", "Data Abertura", "Doc. Associado", "Tamanho Doc"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna todas as células não editáveis
            }
        };
        tabelaProcessos = new JTable(tableModel);
        tabelaProcessos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        tabelaProcessos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Duplo clique
                    editarProcessoSelecionado();
                }
            }
        });
        add(new JScrollPane(tabelaProcessos), BorderLayout.CENTER); // Adiciona a tabela com barra de rolagem

        carregarProcessosNaTabela();
    }

    /**
     * Carrega todos os processos do repositório na JTable (RF15).
     */
    private void carregarProcessosNaTabela() {
        tableModel.setRowCount(0); // Limpa as linhas existentes da tabela
        List<Processo> processos = ProcessoRepository.listarTodosProcessos(); // Pega os processos do repositório

        if (processos.isEmpty()) {
            return;
        }

        for (Processo p : processos) {
            String tipoProcesso = p.getClass().getSimpleName();
            String dataAberturaFormatada = dateFormat.format(p.getDataAbertura());
            String docAssociado = p.getCaminhoDocumento() != null ? p.getCaminhoDocumento() : "N/A";
            String tamanhoDoc = p.getTamanhoArquivoDocumento() > 0 ? (p.getTamanhoArquivoDocumento() / 1024) + " KB" : "N/A";

            // ADICIONE p.getId() AQUI NA PRIMEIRA POSIÇÃO:
            tableModel.addRow(new Object[]{p.getId(), p.getBase(), p.getNumeroProcesso(), tipoProcesso, dataAberturaFormatada, docAssociado, tamanhoDoc});
        }
    }

    /**
     * Busca um processo específico e exibe seus detalhes (RF16).
     */
    private void buscarProcesso() {
        String base = txtBuscaBase.getText().trim().toUpperCase();
        String numeroProcesso = txtBuscaNumeroProcesso.getText().trim();

        if (base.isEmpty() || numeroProcesso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe a Base e o Número do Processo para buscar.", "Campos Obrigatórios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Processo processoEncontrado = ProcessoRepository.buscarProcessoPorBaseNumero(base, numeroProcesso);

        if (processoEncontrado != null) {
            StringBuilder detalhes = new StringBuilder();
            detalhes.append("Detalhes do Processo:\n");
            detalhes.append("  ID: ").append(processoEncontrado.getId()).append("\n"); // ADICIONADO AQUI
            detalhes.append("Tipo: ").append(processoEncontrado.getClass().getSimpleName()).append("\n");
            detalhes.append("Base: ").append(processoEncontrado.getBase()).append("\n");
            detalhes.append("Número: ").append(processoEncontrado.getNumeroProcesso()).append("\n");
            detalhes.append("Data Abertura: ").append(dateFormat.format(processoEncontrado.getDataAbertura())).append("\n");

            // Atributos específicos
            if (processoEncontrado instanceof DanificacaoBagagem) {
                detalhes.append("Etiqueta Danificada: ").append(((DanificacaoBagagem) processoEncontrado).getEtiquetaBagagemDanificada()).append("\n");
            } else if (processoEncontrado instanceof ExtravioBagagem) {
                detalhes.append("Etiqueta Extraviada: ").append(((ExtravioBagagem) processoEncontrado).getEtiquetaBagagemExtraviada()).append("\n");
            } else if (processoEncontrado instanceof ItemEsquecidoAviao) {
                detalhes.append("Número do Voo: ").append(((ItemEsquecidoAviao) processoEncontrado).getNumeroVoo()).append("\n");
            }

            // Metadados do documento
            if (processoEncontrado.getCaminhoDocumento() != null) {
                detalhes.append("Documento: ").append(processoEncontrado.getCaminhoDocumento()).append("\n");
                detalhes.append("  Tipo Doc: ").append(processoEncontrado.getTipoArquivoDocumento()).append("\n");
                detalhes.append("  Tamanho Doc: ").append(processoEncontrado.getTamanhoArquivoDocumento() / 1024).append(" KB\n");
            }

            // Recibos associados (RF13)
            List<Recibo> recibosAssociados = ProcessoRepository.listarRecibosPorProcesso(processoEncontrado);
            if (!recibosAssociados.isEmpty()) {
                detalhes.append("\nRecibos Associados:\n");
                for (Recibo r : recibosAssociados) {
                    detalhes.append(r.toString()).append("\n---\n");
                }
            } else {
                detalhes.append("\nNenhum recibo associado.");
            }

            JOptionPane.showMessageDialog(this, detalhes.toString(), "Processo Encontrado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Processo não encontrado.", "Busca", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Obtém o processo selecionado na tabela e o envia para edição.
     * O MainFrame será instruído a exibir o painel de cadastro/edição com os dados pré-preenchidos.
     * ATENÇÃO: Se a tabela for reordenada, buscar pelo ID é mais seguro do que por Base/Número.
     */
    private void editarProcessoSelecionado() {
        int selectedRow = tabelaProcessos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um processo para editar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // AGORA É MAIS SEGURO PEGAR PELO ID, QUE É A PRIMEIRA COLUNA (índice 0)
        long idProcesso = (long) tableModel.getValueAt(selectedRow, 0); 
        
        // Você precisaria de um método no ProcessoRepository para buscar por ID:
        Processo processoParaEditar = ProcessoRepository.buscarProcessoPorId(idProcesso);

        if (processoParaEditar != null) {
            if (parentFrame != null) {
                parentFrame.showPanel(new CadastroProcessoPanel(processoParaEditar));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Processo selecionado não encontrado no repositório.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Obtém o processo selecionado na tabela e o remove.
     * ATENÇÃO: Se a tabela for reordenada, buscar pelo ID é mais seguro do que por Base/Número.
     */
    private void excluirProcessoSelecionado() {
        int selectedRow = tabelaProcessos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um processo para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // PEGUE O ID DA PRIMEIRA COLUNA:
        long idProcesso = (long) tableModel.getValueAt(selectedRow, 0); 
        
        // Busque o processo pelo ID para pegar Base e Número para a mensagem de confirmação
        Processo processoParaRemover = ProcessoRepository.buscarProcessoPorId(idProcesso);
        
        if (processoParaRemover == null) {
            JOptionPane.showMessageDialog(this, "Processo selecionado não encontrado no repositório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o processo " + processoParaRemover.getBase() + "-" + processoParaRemover.getNumeroProcesso() + " (ID: " + idProcesso + ")?\n" +
                "Isso removerá também todos os recibos associados.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Chame o método de remoção por ID se for implementado, ou continue por base/numero
            boolean removido = ProcessoRepository.removerProcesso(processoParaRemover.getBase(), processoParaRemover.getNumeroProcesso()); // ou removerProcesso(idProcesso)
            if (removido) {
                JOptionPane.showMessageDialog(this, "Processo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarProcessosNaTabela(); // Recarrega a tabela para refletir a exclusão
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir processo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}