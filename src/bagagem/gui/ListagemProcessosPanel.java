package bagagem.gui;

import bagagem.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Painel da interface gráfica para listar, consultar, filtrar e gerenciar os processos existentes.
 * Exibe os processos em uma tabela e oferece botões para visualizar detalhes, editar e excluir.
 */
public class ListagemProcessosPanel extends JPanel {

    private JTable tabelaProcessos;
    private DefaultTableModel tableModel;
    private JTextField txtFiltroBase;
    private JComboBox<String> cmbFiltroTipo;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private MainFrame parentFrame;

    /**
     * Construtor do painel de listagem de processos.
     * @param parentFrame A referência do MainFrame, usada para navegação entre painéis.
     */
    public ListagemProcessosPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setOpaque(true);
        setLayout(new BorderLayout());

        // --- Painel Superior para Título e Filtros ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5; 
        JLabel titleLabel = new JLabel("Lista e Consulta de Processos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, gbc);

        // Campos de Filtro
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Filtrar por Base:"), gbc);
        gbc.gridx = 1;
        txtFiltroBase = new JTextField(8);
        topPanel.add(txtFiltroBase, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Filtrar por Tipo:"), gbc);
        gbc.gridx = 3;
        String[] tipos = {"Todos", "DanificacaoBagagem", "ExtravioBagagem", "ItemEsquecidoAviao"};
        cmbFiltroTipo = new JComboBox<>(tipos);
        topPanel.add(cmbFiltroTipo, gbc);

        gbc.gridx = 4;
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        topPanel.add(btnFiltrar, gbc);


        JPanel acoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnBuscarEspecifico = new JButton("Buscar por ID");
        btnBuscarEspecifico.addActionListener(e -> buscarProcessoEspecifico());
        acoesPanel.add(btnBuscarEspecifico);

        JButton btnVisualizar = new JButton("Visualizar Processo");
        btnVisualizar.addActionListener(e -> visualizarProcessoSelecionado());
        acoesPanel.add(btnVisualizar);

        JButton btnEditar = new JButton("Editar Processo");
        btnEditar.addActionListener(e -> editarProcessoSelecionado());
        acoesPanel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir Processo");
        btnExcluir.addActionListener(e -> excluirProcessoSelecionado());
        acoesPanel.add(btnExcluir);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(createTabela()), BorderLayout.CENTER);
        add(acoesPanel, BorderLayout.SOUTH);

        carregarProcessosNaTabela(ProcessoRepository.listarTodosProcessos());
    }

    /**
     * Cria e configura a JTable para exibir os processos.
     * Define o modelo da tabela, as colunas e adiciona um ouvinte de mouse para
     * detectar cliques duplos nas linhas.
     * @return A instância da JTable configurada.
     */
    private JTable createTabela() {
        String[] colunas = {"ID", "Base", "Número", "Tipo Processo", "Data Abertura", "Doc. Associado", "Tamanho Doc"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProcessos = new JTable(tableModel);
        tabelaProcessos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProcessos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    visualizarProcessoSelecionado();
                }
            }
        });
        return tabelaProcessos;
    }
    
    /**
     * Obtém o processo selecionado na tabela e navega para a tela de edição (CadastroProcessoPanel).
     * Se nenhum processo estiver selecionado, exibe uma mensagem de aviso.
     */
    private void editarProcessoSelecionado() { 
        int selectedRow = tabelaProcessos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um processo para editar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idProcesso = (long) tableModel.getValueAt(selectedRow, 0); 
        Processo processoParaEditar = ProcessoRepository.buscarProcessoPorId(idProcesso);

        if (processoParaEditar != null) {
            if (parentFrame != null) {
                parentFrame.showPanel(new CadastroProcessoPanel(parentFrame, processoParaEditar));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Processo selecionado não encontrado no repositório.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Limpa a tabela e a preenche com uma lista de processos fornecida.
     * @param processos A lista de processos a ser exibida na tabela.
     */
    private void carregarProcessosNaTabela(List<Processo> processos) {
        tableModel.setRowCount(0);
        if (processos == null || processos.isEmpty()) {
            return;
        }
        for (Processo p : processos) {
            String tipoProcesso = p.getClass().getSimpleName();
            String dataAberturaFormatada = dateFormat.format(p.getDataAbertura());
            String docAssociado = p.getCaminhoDocumento() != null ? p.getCaminhoDocumento() : "N/A";
            String tamanhoDoc = p.getTamanhoArquivoDocumento() > 0 ? (p.getTamanhoArquivoDocumento() / 1024) + " KB" : "N/A";
            tableModel.addRow(new Object[]{p.getId(), p.getBase(), p.getNumeroProcesso(), tipoProcesso, dataAberturaFormatada, docAssociado, tamanhoDoc});
        }
    }

    /**
     * Aplica os filtros de base e tipo selecionados pelo usuário,
     * busca os processos correspondentes no repositório e atualiza a tabela.
     */
    private void aplicarFiltros() {
        String base = txtFiltroBase.getText().trim();
        String tipo = (String) cmbFiltroTipo.getSelectedItem();

        List<Processo> processosFiltrados = ProcessoRepository.listarProcessosFiltrados(base, tipo);
        carregarProcessosNaTabela(processosFiltrados);
    }

    /**
     * Exibe os detalhes completos do processo selecionado em uma janela de diálogo (JOptionPane).
     * Inclui uma miniatura da imagem do documento, se houver.
     */
    private void visualizarProcessoSelecionado() { 
        int selectedRow = tabelaProcessos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um processo para visualizar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idProcesso = (long) tableModel.getValueAt(selectedRow, 0);
        Processo processo = ProcessoRepository.buscarProcessoPorId(idProcesso);

        if (processo == null) {
            JOptionPane.showMessageDialog(this, "Processo não encontrado no repositório.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder detalhesHtml = new StringBuilder("<html><body>");
        detalhesHtml.append("<h2>Detalhes do Processo</h2>");
        detalhesHtml.append("<b>ID:</b> ").append(processo.getId()).append("<br>");
        detalhesHtml.append("<b>Tipo:</b> ").append(processo.getClass().getSimpleName()).append("<br>");
        detalhesHtml.append("<b>Base:</b> ").append(processo.getBase()).append("<br>");
        detalhesHtml.append("<b>Número:</b> ").append(processo.getNumeroProcesso()).append("<br>");
        detalhesHtml.append("<b>Data Abertura:</b> ").append(dateFormat.format(processo.getDataAbertura())).append("<br><hr>");

        if (processo instanceof DanificacaoBagagem) {
            detalhesHtml.append("<b>Etiqueta Danificada:</b> ").append(((DanificacaoBagagem) processo).getEtiquetaBagagemDanificada()).append("<br>");
        } else if (processo instanceof ExtravioBagagem) {
            detalhesHtml.append("<b>Etiqueta Extraviada:</b> ").append(((ExtravioBagagem) processo).getEtiquetaBagagemExtraviada()).append("<br>");
        } else if (processo instanceof ItemEsquecidoAviao) {
            detalhesHtml.append("<b>Número do Voo:</b> ").append(((ItemEsquecidoAviao) processo).getNumeroVoo()).append("<br>");
        }

        detalhesHtml.append("</body></html>");

        ImageIcon iconeFinal = null;
        String caminhoDocumento = processo.getCaminhoDocumento();
        if (caminhoDocumento != null && !caminhoDocumento.isEmpty()) {
            ImageIcon iconeOriginal = new ImageIcon(caminhoDocumento);
            Image imagem = iconeOriginal.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
            iconeFinal = new ImageIcon(imagem);
        }

        JOptionPane.showMessageDialog(
            this,
            detalhesHtml.toString(),
            "Visualização do Processo",
            JOptionPane.INFORMATION_MESSAGE,
            iconeFinal
        );
    }

    /**
     * Exclui o processo selecionado na tabela, após uma confirmação do usuário.
     * A remoção também exclui quaisquer recibos associados.
     */
    private void excluirProcessoSelecionado() {
        int selectedRow = tabelaProcessos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um processo para excluir.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idProcesso = (long) tableModel.getValueAt(selectedRow, 0); 
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
            boolean removido = ProcessoRepository.removerProcesso(processoParaRemover.getBase(), processoParaRemover.getNumeroProcesso());
            if (removido) {
                JOptionPane.showMessageDialog(this, "Processo excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                aplicarFiltros(); 
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir processo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Permite ao usuário buscar um processo específico por Base e Número através de janelas de diálogo.
     * Se encontrado, o processo é selecionado e destacado na tabela.
     */
    private void buscarProcessoEspecifico() {
        String base = JOptionPane.showInputDialog(this, "Digite a Base do processo (Ex: GYN):", "Buscar Processo Específico", JOptionPane.QUESTION_MESSAGE);
        if (base == null || base.trim().isEmpty()) {
            return; 
        }

        String numero = JOptionPane.showInputDialog(this, "Digite o Número do processo (Ex: 12345):", "Buscar Processo Específico", JOptionPane.QUESTION_MESSAGE);
        if (numero == null || numero.trim().isEmpty()) {
            return; 
        }

        Processo processoEncontrado = ProcessoRepository.buscarProcessoPorBaseNumero(base.trim().toUpperCase(), numero.trim());

        if (processoEncontrado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum processo encontrado para a Base '" + base.toUpperCase() + "' e Número '" + numero + "'.", "Busca Sem Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            long idEncontrado = processoEncontrado.getId();
            carregarProcessosNaTabela(ProcessoRepository.listarTodosProcessos());
            txtFiltroBase.setText(""); 
            cmbFiltroTipo.setSelectedItem("Todos");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if ((long) tableModel.getValueAt(i, 0) == idEncontrado) {
                    tabelaProcessos.setRowSelectionInterval(i, i);
                    tabelaProcessos.scrollRectToVisible(tabelaProcessos.getCellRect(i, 0, true));
                    visualizarProcessoSelecionado();
                    break;
                }
            }
        }
    }
}