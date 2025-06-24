package bagagem.gui;

import bagagem.model.Processo;
import bagagem.model.ProcessoRepository;
import bagagem.model.Recibo;
import bagagem.model.ReciboConsertoBagagem;
import bagagem.model.ReciboIndenizacaoMilhas;
import bagagem.model.ReciboEntregaBagagemExtraviada;
import bagagem.model.ReciboItemEsquecidoAviao;

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

    private JTextField txtBuscaBase;
    private JTextField txtBuscaNumeroProcesso;
    private JComboBox<String> cmbTipoReciboFiltro;

    public ListagemRecibosPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4; // Ajustado para o número de colunas
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Lista e Consulta de Recibos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST; // Reset do anchor

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Base Processo:"), gbc);
        
        gbc.gridx = 1;
        txtBuscaBase = new JTextField(8);
        topPanel.add(txtBuscaBase, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Número Processo:"), gbc);
        
        gbc.gridx = 3;
        txtBuscaNumeroProcesso = new JTextField(10);
        topPanel.add(txtBuscaNumeroProcesso, gbc);
        
        gbc.gridy++;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Tipo Recibo:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbTipoReciboFiltro = new JComboBox<>();
        cmbTipoReciboFiltro.addItem("Todos");
        popularTiposReciboFiltro();
        topPanel.add(cmbTipoReciboFiltro, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBuscarFiltrar = new JButton("Buscar/Filtrar Recibos");
        btnBuscarFiltrar.addActionListener(e -> buscarFiltrarRecibos());
        filterButtonPanel.add(btnBuscarFiltrar);

        JButton btnListarTodos = new JButton("Listar Todos");
        btnListarTodos.addActionListener(e -> carregarRecibosNaTabela());
        filterButtonPanel.add(btnListarTodos);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(filterButtonPanel, gbc);


        JPanel acoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.addActionListener(e -> visualizarDetalhesRecibo());
        acoesPanel.add(btnDetalhes);

        JButton btnEditar = new JButton("Editar Recibo");
        btnEditar.addActionListener(e -> editarReciboSelecionado());
        acoesPanel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir Recibo");
        btnExcluir.addActionListener(e -> excluirReciboSelecionado());
        acoesPanel.add(btnExcluir);


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
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabelaRecibos), BorderLayout.CENTER);
        add(acoesPanel, BorderLayout.SOUTH);

        carregarRecibosNaTabela();
    }


    private void popularTiposReciboFiltro() {
        cmbTipoReciboFiltro.addItem("ReciboConsertoBagagem");
        cmbTipoReciboFiltro.addItem("ReciboIndenizacaoMilhas");
        cmbTipoReciboFiltro.addItem("ReciboEntregaBagagemExtraviada");
        cmbTipoReciboFiltro.addItem("ReciboItemEsquecidoAviao");
    }

    private void carregarRecibosNaTabela() {
        tableModel.setRowCount(0);
        List<Recibo> recibos = ProcessoRepository.listarTodosRecibos();
        preencherTabelaComRecibos(recibos);
    }

    private void buscarFiltrarRecibos() {
        String base = txtBuscaBase.getText().trim();
        String numeroProcesso = txtBuscaNumeroProcesso.getText().trim();
        String tipoRecibo = cmbTipoReciboFiltro.getSelectedItem() != null && !cmbTipoReciboFiltro.getSelectedItem().equals("Todos") ? 
                            (String) cmbTipoReciboFiltro.getSelectedItem() : null;
        
        List<Recibo> recibosFiltrados = ProcessoRepository.filtrarRecibos(
            base.isEmpty() ? null : base,
            tipoRecibo,
            numeroProcesso.isEmpty() ? null : numeroProcesso
        );
        
        if (recibosFiltrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum recibo encontrado com os filtros aplicados.", "Busca/Filtro", JOptionPane.INFORMATION_MESSAGE);
        }
        tableModel.setRowCount(0);
        preencherTabelaComRecibos(recibosFiltrados);
    }

    private void preencherTabelaComRecibos(List<Recibo> recibos) {
        for (Recibo r : recibos) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getClass().getSimpleName(),
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

        long idRecibo = (long) tableModel.getValueAt(selectedRow, 0);
        Recibo reciboParaRemover = ProcessoRepository.buscarReciboPorId(idRecibo);

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

        long idRecibo = (long) tableModel.getValueAt(selectedRow, 0);
        Recibo reciboParaEditar = ProcessoRepository.buscarReciboPorId(idRecibo);

        if (parentFrame != null && reciboParaEditar != null) {
            AssociarReciboPanel painelEdicao = new AssociarReciboPanel(parentFrame, reciboParaEditar);
            parentFrame.showPanel(painelEdicao);
        } else {
            JOptionPane.showMessageDialog(this, "Recibo não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

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

        StringBuilder detalhes = new StringBuilder();
        detalhes.append("<html><body><h2>Detalhes do Recibo</h2>");
        detalhes.append("<p><b>ID Recibo:</b> ").append(recibo.getId()).append("</p>");
        detalhes.append("<p><b>Tipo Recibo:</b> ").append(recibo.getClass().getSimpleName()).append("</p>");
        detalhes.append("<p><b>Data Assinatura:</b> ").append(dateFormat.format(recibo.getDataAssinatura())).append("</p>");
        
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

            JButton btnVerProcesso = new JButton("Ver Detalhes do Processo");
            btnVerProcesso.addActionListener(e -> {
                Window window = SwingUtilities.windowForComponent(btnVerProcesso);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
                parentFrame.showPanel(new CadastroProcessoPanel(parentFrame, processoAssociado));
            });
            
            JPanel panelDetalhesReciboVisual = new JPanel(new BorderLayout(5,5));
            panelDetalhesReciboVisual.setOpaque(false);
            
            JLabel lblMiniaturaRecibo = new JLabel();
            lblMiniaturaRecibo.setPreferredSize(new Dimension(100, 100));
            lblMiniaturaRecibo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            lblMiniaturaRecibo.setHorizontalAlignment(SwingConstants.CENTER);
            lblMiniaturaRecibo.setVerticalAlignment(SwingConstants.CENTER);

            exibirMiniatura(recibo.getCaminhoDocumento(), lblMiniaturaRecibo);

            JPanel panelTexto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            panelTexto.setOpaque(false);
            panelTexto.add(new JLabel(detalhes.toString()));

            panelDetalhesReciboVisual.add(panelTexto, BorderLayout.CENTER);
            panelDetalhesReciboVisual.add(lblMiniaturaRecibo, BorderLayout.EAST);
            
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
                    int desiredSize = 90;
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