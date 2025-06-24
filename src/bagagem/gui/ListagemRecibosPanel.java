package bagagem.gui;

import bagagem.model.ProcessoRepository;
import bagagem.model.Recibo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ListagemRecibosPanel extends JPanel {

    private JTable tabelaRecibos;
    private DefaultTableModel tableModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private MainFrame parentFrame;

    public ListagemRecibosPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Lista de Todos os Recibos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        String[] colunas = {"Tipo Recibo", "Base Processo", "Nº Processo", "Data Assinatura", "Documento Anexado"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRecibos = new JTable(tableModel);
        tabelaRecibos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tabelaRecibos), BorderLayout.CENTER);

        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnEditar = new JButton("Editar Recibo");
        JButton btnExcluir = new JButton("Excluir Recibo Selecionado");

        panelAcoes.add(btnAtualizar);
        panelAcoes.add(btnEditar);
        panelAcoes.add(btnExcluir);
        add(panelAcoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarRecibosNaTabela());
        btnEditar.addActionListener(e -> editarReciboSelecionado());
        btnExcluir.addActionListener(e -> excluirReciboSelecionado());

        carregarRecibosNaTabela();
    }

    private void carregarRecibosNaTabela() {
        tableModel.setRowCount(0);
        List<Recibo> recibos = ProcessoRepository.listarTodosRecibos();

        for (Recibo r : recibos) {
            tableModel.addRow(new Object[]{
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

        if (JOptionPane.showConfirmDialog(this, "Tem certeza?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Recibo reciboParaRemover = ProcessoRepository.listarTodosRecibos().get(selectedRow);
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

        // Pega o recibo selecionado da lista do repositório
        Recibo reciboParaEditar = ProcessoRepository.listarTodosRecibos().get(selectedRow);

        if (parentFrame != null && reciboParaEditar != null) {
            AssociarReciboPanel painelEdicao = new AssociarReciboPanel(reciboParaEditar);
            parentFrame.showPanel(painelEdicao);
        }
    }
}