package bagagem.gui;

import bagagem.model.Processo;
import bagagem.model.ProcessoRepository;
import bagagem.model.Recibo;
import bagagem.model.ReciboConsertoBagagem; // Necessário para instanceof
import bagagem.model.ReciboIndenizacaoMilhas; // Necessário para instanceof
import bagagem.model.ReciboEntregaBagagemExtraviada; // Necessário para instanceof
import bagagem.model.ReciboItemEsquecidoAviao; // Necessário para instanceof

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListagemRecibosPanel extends JPanel {

    private JTable tabelaRecibos;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private MainFrame parentFrame;

    // Campos de busca/filtro
    private JTextField txtBuscaBase;
    private JTextField txtBuscaNumeroProcesso; // Para buscar pelo número do processo associado
    private JComboBox<String> cmbTipoReciboFiltro; // Para filtrar por tipo de recibo

    public ListagemRecibosPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));

        // --- Painel Superior para Título e Filtros/Ações ---
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5;
        JLabel titleLabel = new JLabel("Lista e Consulta de Recibos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, gbc);

        // Campos de Filtro
        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0;
        topPanel.add(new JLabel("Base Processo:"), gbc);
        gbc.gridx = 1;
        txtBuscaBase = new JTextField(8);
        topPanel.add(txtBuscaBase, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Número Processo:"), gbc);
        gbc.gridx = 3;
        txtBuscaNumeroProcesso = new JTextField(10);
        topPanel.add(txtBuscaNumeroProcesso, gbc);

        gbc.gridx = 4;
        topPanel.add(new JLabel("Tipo Recibo:"), gbc);
        gbc.gridx = 5; // Ajustado para não sobrepor o botão de busca/filtro
        cmbTipoReciboFiltro = new JComboBox<>();
        cmbTipoReciboFiltro.addItem("Todos"); // Opção para não filtrar por tipo
        popularTiposReciboFiltro(); // Popula o ComboBox com os tipos de recibo conhecidos
        topPanel.add(cmbTipoReciboFiltro, gbc);

        // Botões de Ação na Busca/Filtro
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 1;
        JButton btnBuscarFiltrar = new JButton("Buscar/Filtrar Recibos");
        btnBuscarFiltrar.addActionListener(e -> buscarFiltrarRecibos());
        topPanel.add(btnBuscarFiltrar, gbc);

        gbc.gridx = 1;
        JButton btnListarTodos = new JButton("Listar Todos");
        btnListarTodos.addActionListener(e -> carregarRecibosNaTabela());
        topPanel.add(btnListarTodos, gbc);

        gbc.gridx = 2;
        JButton btnDetalhes = new JButton("Ver Detalhes"); // NOVO BOTÃO
        btnDetalhes.addActionListener(e -> visualizarDetalhesRecibo());
        topPanel.add(btnDetalhes, gbc);

        gbc.gridx = 3;
        JButton btnEditar = new JButton("Editar Recibo");
        btnEditar.addActionListener(e -> editarReciboSelecionado());
        topPanel.add(btnEditar, gbc);

        gbc.gridx = 4; // Ajustado para a próxima coluna disponível
        JButton btnExcluir = new JButton("Excluir Recibo");
        btnExcluir.addActionListener(e -> excluirReciboSelecionado());
        topPanel.add(btnExcluir, gbc);

        add(topPanel, BorderLayout.NORTH);

        // --- Tabela de Recibos ---
        // Adicionar coluna de ID
        String[] colunas = {"ID Recibo", "Tipo Recibo", "Base Processo", "Nº Processo", "Data Assinatura", "Documento Anexado"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRecibos = new JTable(tableModel);
        tabelaRecibos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaRecibos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    visualizarDetalhesRecibo();
                }
            }
        });
        
        add(new JScrollPane(tabelaRecibos), BorderLayout.CENTER);

        carregarRecibosNaTabela();
    }

    // Método para popular o ComboBox de tipos de recibo
    private void popularTiposReciboFiltro() {
        cmbTipoReciboFiltro.addItem("ReciboConsertoBagagem");
        cmbTipoReciboFiltro.addItem("ReciboIndenizacaoMilhas");
        cmbTipoReciboFiltro.addItem("ReciboEntregaBagagemExtraviada");
        cmbTipoReciboFiltro.addItem("ReciboItemEsquecidoAviao");
        // Isso é para coincidir com Class.getSimpleName(). Pode ser exibido de forma mais amigável
        // no ComboBox se você quiser, e convertido para o nome simples na lógica de filtro.
    }

    // Carrega todos os recibos (sem filtro)
    private void carregarRecibosNaTabela() {
        tableModel.setRowCount(0);
        List<Recibo> recibos = ProcessoRepository.listarTodosRecibos();
        preencherTabelaComRecibos(recibos);
    }

    // Novo método para buscar/filtrar recibos
    private void buscarFiltrarRecibos() {
        String base = txtBuscaBase.getText().trim();
        String numeroProcesso = txtBuscaNumeroProcesso.getText().trim();
        String tipoRecibo = cmbTipoReciboFiltro.getSelectedItem() != null && !cmbTipoReciboFiltro.getSelectedItem().equals("Todos") ? 
                            (String) cmbTipoReciboFiltro.getSelectedItem() : null;
        
        List<Recibo> recibosFiltrados = ProcessoRepository.filtrarRecibos(
            base.isEmpty() ? null : base,
            tipoRecibo, // O tipo já está no formato simples aqui
            numeroProcesso.isEmpty() ? null : numeroProcesso
        );
        
        if (recibosFiltrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum recibo encontrado com os filtros aplicados.", "Busca/Filtro", JOptionPane.INFORMATION_MESSAGE);
        }
        tableModel.setRowCount(0); // Limpa a tabela antes de preencher
        preencherTabelaComRecibos(recibosFiltrados);
    }

    // Método auxiliar para preencher a tabela, usado por carregarRecibosNaTabela e buscarFiltrarRecibos
    private void preencherTabelaComRecibos(List<Recibo> recibos) {
        for (Recibo r : recibos) {
            tableModel.addRow(new Object[]{
                r.getId(), // ID do Recibo
                r.getClass().getSimpleName(), // Nome simples da classe
                r.getBase(),
                r.getNumeroProcesso(),
                dateFormat.format(r.getDataAssinatura()),
                r.getCaminhoDocumento() != null ? r.getCaminhoDocumento() : "N/A"
            });
        }
    }

    private void excluirReciboSelecionado() {
        int selectedRow = tabelaRecibos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um recibo para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idRecibo = (long) tableModel.getValueAt(selectedRow, 0); // Pega o ID da primeira coluna
        Recibo reciboParaRemover = ProcessoRepository.buscarReciboPorId(idRecibo); // Busca pelo ID

        if (reciboParaRemover == null) {
             JOptionPane.showMessageDialog(this, "Recibo não encontrado no repositório.", "Erro", JOptionPane.ERROR_MESSAGE);
             return;
        }

        if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o recibo (ID: " + idRecibo + ")?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (ProcessoRepository.removerRecibo(reciboParaRemover)) {
                JOptionPane.showMessageDialog(this, "Recibo excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarRecibosNaTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarReciboSelecionado() {
        int selectedRow = tabelaRecibos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um recibo para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idRecibo = (long) tableModel.getValueAt(selectedRow, 0); // Pega o ID da primeira coluna
        Recibo reciboParaEditar = ProcessoRepository.buscarReciboPorId(idRecibo); // Busca pelo ID

        if (parentFrame != null && reciboParaEditar != null) {
            AssociarReciboPanel painelEdicao = new AssociarReciboPanel(reciboParaEditar);
            parentFrame.showPanel(painelEdicao);
        } else {
            JOptionPane.showMessageDialog(this, "Recibo não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // NOVO MÉTODO: Visualizar detalhes do recibo com miniatura e link para o processo
    private void visualizarDetalhesRecibo() {
        int selectedRow = tabelaRecibos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um recibo para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long idRecibo = (long) tableModel.getValueAt(selectedRow, 0);
        Recibo recibo = ProcessoRepository.buscarReciboPorId(idRecibo);

        if (recibo == null) {
            JOptionPane.showMessageDialog(this, "Recibo não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Constrói a mensagem de detalhes
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("<html><body><h2>Detalhes do Recibo</h2>");
        detalhes.append("<p><b>ID Recibo:</b> ").append(recibo.getId()).append("</p>");
        detalhes.append("<p><b>Tipo Recibo:</b> ").append(recibo.getClass().getSimpleName()).append("</p>");
        detalhes.append("<p><b>Data Assinatura:</b> ").append(dateFormat.format(recibo.getDataAssinatura())).append("</p>");
        
        // Detalhes específicos do recibo
        if (recibo instanceof ReciboConsertoBagagem) {
            detalhes.append("<p><b>Local de Entrega/Retirada:</b> ").append(((ReciboConsertoBagagem) recibo).getEntregaOuRetiradaEmAeroporto()).append("</p>");
        } else if (recibo instanceof ReciboIndenizacaoMilhas) {
            detalhes.append("<p><b>Quantidade de Milhas:</b> ").append(((ReciboIndenizacaoMilhas) recibo).getQuantidadeMilhas()).append("</p>");
        } else if (recibo instanceof ReciboEntregaBagagemExtraviada) {
            detalhes.append("<p><b>Local de Entrega/Retirada:</b> ").append(((ReciboEntregaBagagemExtraviada) recibo).getEntregaOuRetiradaEmAeroporto()).append("</p>");
        } else if (recibo instanceof ReciboItemEsquecidoAviao) {
            detalhes.append("<p><b>Documento de Identificação:</b> ").append(((ReciboItemEsquecidoAviao) recibo).getDocumentoIdentificacaoClienteRetirada()).append("</p>");
        }

        detalhes.append("<br><h3>Processo Associado:</h3>");
        Processo processoAssociado = recibo.getProcessoAssociado();
        if (processoAssociado != null) {
            detalhes.append("<p><b>ID Processo:</b> ").append(processoAssociado.getId()).append("</p>");
            detalhes.append("<p><b>Base:</b> ").append(processoAssociado.getBase()).append("</p>");
            detalhes.append("<p><b>Número:</b> ").append(processoAssociado.getNumeroProcesso()).append("</p>");
            detalhes.append("<p><b>Tipo:</b> ").append(processoAssociado.getClass().getSimpleName()).append("</p>");
            detalhes.append("<p><b>Data Abertura:</b> ").append(dateFormat.format(processoAssociado.getDataAbertura())).append("</p>");

            // Adiciona um botão para ver detalhes completos do processo
            JButton btnVerProcesso = new JButton("Ver Detalhes do Processo");
            btnVerProcesso.addActionListener(e -> {
                // Fechar o JOptionPane atual antes de abrir outro painel
                Window window = SwingUtilities.windowForComponent(btnVerProcesso);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
                // Abre o CadastroProcessoPanel em modo de edição/visualização para o processo associado
                parentFrame.showPanel(new CadastroProcessoPanel(processoAssociado));
            });
            
            // Adicionar componente da miniatura do recibo
            JPanel panelDetalhesReciboVisual = new JPanel(new BorderLayout(5,5));
            panelDetalhesReciboVisual.setOpaque(false);
            
            JLabel lblMiniaturaRecibo = new JLabel();
            lblMiniaturaRecibo.setPreferredSize(new Dimension(100, 100)); // Tamanho da miniatura
            lblMiniaturaRecibo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            lblMiniaturaRecibo.setHorizontalAlignment(SwingConstants.CENTER);
            lblMiniaturaRecibo.setVerticalAlignment(SwingConstants.CENTER);

            exibirMiniatura(recibo.getCaminhoDocumento(), lblMiniaturaRecibo); // Usar o método existente

            JPanel panelTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panelTexto.setOpaque(false);
            panelTexto.add(new JLabel(detalhes.toString()));

            panelDetalhesReciboVisual.add(panelTexto, BorderLayout.CENTER);
            panelDetalhesReciboVisual.add(lblMiniaturaRecibo, BorderLayout.EAST); // Miniatura à direita
            
            // Adiciona o botão de ver processo
            JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelBotoes.setOpaque(false);
            panelBotoes.add(btnVerProcesso);
            panelDetalhesReciboVisual.add(panelBotoes, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panelDetalhesReciboVisual, "Detalhes do Recibo", JOptionPane.PLAIN_MESSAGE);

        } else {
            detalhes.append("<p>Nenhum processo associado encontrado.</p>");
            JOptionPane.showMessageDialog(this, detalhes.toString(), "Detalhes do Recibo", JOptionPane.INFORMATION_MESSAGE);
        }
        detalhes.append("</body></html>");
    }

    // Método auxiliar para exibir a miniatura (REUTILIZADO do CadastroProcessoPanel ou similar)
    private void exibirMiniatura(String caminhoArquivo, JLabel targetLabel) {
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            targetLabel.setIcon(null);
            targetLabel.setText("Sem Anexo");
            return;
        }

        File file = new File(caminhoArquivo);
        if (!file.exists()) {
            targetLabel.setIcon(null);
            targetLabel.setText("Arquivo não encontrado");
            return;
        }

        String tipoDoc = "";
        int i = caminhoArquivo.lastIndexOf('.');
        if (i > 0) {
            tipoDoc = caminhoArquivo.substring(i + 1).toLowerCase();
        }
        
        if (tipoDoc.equals("jpg") || tipoDoc.equals("jpeg") || tipoDoc.equals("png")) {
            try {
                BufferedImage originalImage = ImageIO.read(file);
                if (originalImage != null) {
                    int desiredSize = 90; // Tamanho para a miniatura
                    Image scaledImage = originalImage.getScaledInstance(desiredSize, desiredSize, Image.SCALE_SMOOTH);
                    targetLabel.setIcon(new ImageIcon(scaledImage));
                    targetLabel.setText("");
                } else {
                    targetLabel.setIcon(null);
                    targetLabel.setText("Erro ao carregar");
                }
            } catch (IOException e) {
                targetLabel.setIcon(null);
                targetLabel.setText("Erro de leitura");
                System.err.println("Erro ao carregar miniatura do recibo: " + e.getMessage());
            }
        } else if (tipoDoc.equals("pdf")) {
            targetLabel.setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
            targetLabel.setText("PDF");
        } else {
            targetLabel.setIcon(null);
            targetLabel.setText("Não suportado");
        }
    }
}