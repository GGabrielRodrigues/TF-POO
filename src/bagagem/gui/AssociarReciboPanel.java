package bagagem.gui;

import bagagem.model.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AssociarReciboPanel extends JPanel {

    private JTextField txtBuscaBase;
    private JTextField txtBuscaNumeroProcesso;
    private JButton btnBuscarProcesso;
    private JTextArea areaDetalhesProcesso;

    private JComboBox<String> cmbTipoRecibo;
    private JFormattedTextField txtDataAssinatura;
    private JLabel lblCampoEspecificoRecibo;
    private JTextField txtCampoEspecificoRecibo;
    private JButton btnSalvarRecibo;
    private JPanel panelCriacaoRecibo;

    private JTextField txtCaminhoDocumentoRecibo;
    private JButton btnAnexarDocumentoRecibo;
    private String tempCaminhoDocRecibo;
    private String tempTipoDocRecibo;
    private long tempTamanhoDocRecibo;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Processo processoAlvo;

    public AssociarReciboPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);

        JPanel panelBusca = new JPanel(new GridBagLayout());
        panelBusca.setBorder(BorderFactory.createTitledBorder("1. Buscar Processo para Associar"));
        GridBagConstraints gbcBusca = new GridBagConstraints();
        gbcBusca.insets = new Insets(5, 5, 5, 5);
        gbcBusca.fill = GridBagConstraints.HORIZONTAL;

        gbcBusca.gridx = 0; gbcBusca.gridy = 0;
        panelBusca.add(new JLabel("Base:"), gbcBusca);
        txtBuscaBase = new JTextField(5);
        gbcBusca.gridx = 1;
        panelBusca.add(txtBuscaBase, gbcBusca);

        gbcBusca.gridx = 2;
        panelBusca.add(new JLabel("Número Processo:"), gbcBusca);
        txtBuscaNumeroProcesso = new JTextField(8);
        gbcBusca.gridx = 3;
        panelBusca.add(txtBuscaNumeroProcesso, gbcBusca);

        btnBuscarProcesso = new JButton("Buscar Processo");
        gbcBusca.gridx = 4;
        panelBusca.add(btnBuscarProcesso, gbcBusca);

        add(panelBusca, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        areaDetalhesProcesso = new JTextArea(5, 40);
        areaDetalhesProcesso.setEditable(false);
        areaDetalhesProcesso.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaDetalhesProcesso.setBorder(BorderFactory.createTitledBorder("Detalhes do Processo Encontrado"));
        panelCentral.add(new JScrollPane(areaDetalhesProcesso), BorderLayout.NORTH);

        panelCriacaoRecibo = new JPanel(new GridBagLayout());
        panelCriacaoRecibo.setBorder(BorderFactory.createTitledBorder("2. Criar e Salvar Novo Recibo"));
        GridBagConstraints gbcRecibo = new GridBagConstraints();
        gbcRecibo.insets = new Insets(5, 5, 5, 5);
        gbcRecibo.fill = GridBagConstraints.HORIZONTAL;
        gbcRecibo.anchor = GridBagConstraints.WEST;

        gbcRecibo.gridx = 0; gbcRecibo.gridy = 0;
        panelCriacaoRecibo.add(new JLabel("Tipo de Recibo:"), gbcRecibo);
        cmbTipoRecibo = new JComboBox<>();
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(cmbTipoRecibo, gbcRecibo);

        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        panelCriacaoRecibo.add(new JLabel("Data Assinatura (dd/MM/yyyy):"), gbcRecibo);
        try {
            txtDataAssinatura = new JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (ParseException e) {
            txtDataAssinatura = new JFormattedTextField();
        }
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtDataAssinatura, gbcRecibo);

        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        lblCampoEspecificoRecibo = new JLabel("Dado Específico:");
        panelCriacaoRecibo.add(lblCampoEspecificoRecibo, gbcRecibo);
        txtCampoEspecificoRecibo = new JTextField(20);
        gbcRecibo.gridx = 1; gbcRecibo.gridwidth = 2;
        panelCriacaoRecibo.add(txtCampoEspecificoRecibo, gbcRecibo);

        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 1;
        panelCriacaoRecibo.add(new JLabel("Anexo do Recibo:"), gbcRecibo);
        txtCaminhoDocumentoRecibo = new JTextField(20);
        txtCaminhoDocumentoRecibo.setEditable(false);
        gbcRecibo.gridx = 1;
        panelCriacaoRecibo.add(txtCaminhoDocumentoRecibo, gbcRecibo);
        btnAnexarDocumentoRecibo = new JButton("Anexar...");
        gbcRecibo.gridx = 2;
        panelCriacaoRecibo.add(btnAnexarDocumentoRecibo, gbcRecibo);
        
        gbcRecibo.gridx = 0; gbcRecibo.gridy++; gbcRecibo.gridwidth = 3; gbcRecibo.anchor = GridBagConstraints.CENTER;
        btnSalvarRecibo = new JButton("Salvar Recibo");
        panelCriacaoRecibo.add(btnSalvarRecibo, gbcRecibo);

        panelCentral.add(panelCriacaoRecibo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        btnBuscarProcesso.addActionListener(e -> buscarProcesso());
        btnSalvarRecibo.addActionListener(e -> salvarRecibo());
        cmbTipoRecibo.addActionListener(e -> atualizarCampoEspecificoRecibo());
        btnAnexarDocumentoRecibo.addActionListener(e -> selecionarDocumentoRecibo());

        habilitarCamposRecibo(false);
    }

    private void habilitarCamposRecibo(boolean habilitar) {
        panelCriacaoRecibo.setVisible(habilitar);
        if(!habilitar) {
            areaDetalhesProcesso.setText("");
            processoAlvo = null;
        }
    }

    private void buscarProcesso() {
        String base = txtBuscaBase.getText().trim().toUpperCase();
        String numero = txtBuscaNumeroProcesso.getText().trim();
        if (base.isEmpty() || numero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Base e Número do Processo são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        processoAlvo = ProcessoRepository.buscarProcessoPorBaseNumero(base, numero);
        if (processoAlvo == null) {
            JOptionPane.showMessageDialog(this, "Processo não encontrado.", "Busca", JOptionPane.INFORMATION_MESSAGE);
            habilitarCamposRecibo(false);
            return;
        }
        List<Recibo> recibosExistentes = ProcessoRepository.listarRecibosPorProcesso(processoAlvo);
        if (!recibosExistentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este processo já possui um recibo associado.", "Validação", JOptionPane.WARNING_MESSAGE);
            habilitarCamposRecibo(false);
            return;
        }
        areaDetalhesProcesso.setText("Processo Encontrado:\n" + "  - Tipo: " + processoAlvo.getClass().getSimpleName() + "\n" + "  - Base: " + processoAlvo.getBase() + "\n" + "  - Número: " + processoAlvo.getNumeroProcesso());
        popularTiposRecibo();
        habilitarCamposRecibo(true);
        txtDataAssinatura.setText(dateFormat.format(new Date()));
    }

    private void popularTiposRecibo() {
        cmbTipoRecibo.removeAllItems();
        if (processoAlvo instanceof DanificacaoBagagem) {
            cmbTipoRecibo.addItem("Recibo de Conserto de Bagagem");
            cmbTipoRecibo.addItem("Recibo de Indenização em Milhas");
        } else if (processoAlvo instanceof ExtravioBagagem) {
            cmbTipoRecibo.addItem("Recibo de Entrega de Bagagem Extraviada");
        } else if (processoAlvo instanceof ItemEsquecidoAviao) {
            cmbTipoRecibo.addItem("Recibo de Item Esquecido em Avião");
        }
    }
    
    private void atualizarCampoEspecificoRecibo() {
        String tipoRecibo = (String) cmbTipoRecibo.getSelectedItem();
        if (tipoRecibo == null) return;
        switch(tipoRecibo) {
            case "Recibo de Indenização em Milhas": lblCampoEspecificoRecibo.setText("Quantidade de Milhas:"); break;
            case "Recibo de Item Esquecido em Avião": lblCampoEspecificoRecibo.setText("Doc. Identificação Cliente:"); break;
            default: lblCampoEspecificoRecibo.setText("Local de Entrega/Retirada:"); break;
        }
        txtCampoEspecificoRecibo.setText("");
    }

    private void selecionarDocumentoRecibo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imagens e PDFs", "jpg", "jpeg", "png", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtCaminhoDocumentoRecibo.setText(selectedFile.getAbsolutePath());
            this.tempCaminhoDocRecibo = selectedFile.getAbsolutePath();
            String fileName = selectedFile.getName();
            int i = fileName.lastIndexOf('.');
            this.tempTipoDocRecibo = (i > 0) ? fileName.substring(i + 1).toUpperCase() : "";
            this.tempTamanhoDocRecibo = selectedFile.length();
        }
    }

    private void salvarRecibo() {
        if(processoAlvo == null) return;
        Date dataAssinatura;
        try {
            dataAssinatura = dateFormat.parse(txtDataAssinatura.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tipoRecibo = (String) cmbTipoRecibo.getSelectedItem();
        String dadoEspecifico = txtCampoEspecificoRecibo.getText().trim();
        if (dadoEspecifico.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo específico é obrigatório.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Recibo novoRecibo = null;
        try {
            switch(tipoRecibo) {
                case "Recibo de Conserto de Bagagem": novoRecibo = new ReciboConsertoBagagem(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico); break;
                case "Recibo de Indenização em Milhas": novoRecibo = new ReciboIndenizacaoMilhas(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, Integer.parseInt(dadoEspecifico)); break;
                case "Recibo de Entrega de Bagagem Extraviada": novoRecibo = new ReciboEntregaBagagemExtraviada(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico); break;
                case "Recibo de Item Esquecido em Avião": novoRecibo = new ReciboItemEsquecidoAviao(processoAlvo.getBase(), processoAlvo.getNumeroProcesso(), dataAssinatura, processoAlvo, dadoEspecifico); break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de formato: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (novoRecibo != null) {
            novoRecibo.setCaminhoDocumento(this.tempCaminhoDocRecibo);
            novoRecibo.setTipoArquivoDocumento(this.tempTipoDocRecibo);
            novoRecibo.setTamanhoArquivoDocumento(this.tempTamanhoDocRecibo);
            ProcessoRepository.adicionarRecibo(novoRecibo);
            JOptionPane.showMessageDialog(this, "Recibo associado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        }
    }

    private void limparFormulario() {
        txtBuscaBase.setText("");
        txtBuscaNumeroProcesso.setText("");
        txtCaminhoDocumentoRecibo.setText("");
        this.tempCaminhoDocRecibo = null;
        habilitarCamposRecibo(false);
    }
}