package bagagem.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A classe MainFrame representa a janela principal da aplicação.
 * Ela estende JFrame e serve como o contêiner para todos os outros painéis da GUI,
 * além de abrigar a barra de menus principal para navegação.
 */
public class MainFrame extends JFrame {

    /**
     * Construtor da MainFrame.
     * Configura o título, tamanho, operação de fechamento e a aparência da janela principal.
     * Também inicializa a barra de menus da aplicação.
     */
    public MainFrame() {
        setTitle("Sistema de Gestão de Bagagens da Companhia Aérea");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new PainelComImagem("/imagens/bag.jpg"));
        setLayout(new BorderLayout());

        createMenuBar();
    }

    /**
     * Cria e configura a barra de menus (JMenuBar) da aplicação.
     * Este método constrói os menus "Processo", "Recibo" e "Ajuda",
     * adiciona os itens de menu a cada um e define as ações (ActionListeners)
     * para navegação entre os painéis.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu "Processo"
        JMenu processoMenu = new JMenu("Processo");
        JMenuItem cadastrarProcessoItem = new JMenuItem("Cadastrar Novo Processo");
        JMenuItem listarProcessosItem = new JMenuItem("Listar Processos");
        processoMenu.add(cadastrarProcessoItem);
        processoMenu.add(listarProcessosItem);
        menuBar.add(processoMenu);

        cadastrarProcessoItem.addActionListener(e -> showPanel(new CadastroProcessoPanel(this)));
        listarProcessosItem.addActionListener(e -> showPanel(new ListagemProcessosPanel(this)));

        // Menu "Recibo"
        JMenu reciboMenu = new JMenu("Recibo");
        JMenuItem associarReciboItem = new JMenuItem("Associar Recibo");
        JMenuItem listarRecibosItem = new JMenuItem("Listar Recibos");
        reciboMenu.add(associarReciboItem);
        reciboMenu.add(listarRecibosItem);
        menuBar.add(reciboMenu);
        
        associarReciboItem.addActionListener(e -> showPanel(new AssociarReciboPanel(this, null)));
        listarRecibosItem.addActionListener(e -> showPanel(new ListagemRecibosPanel(this)));
        
        // Menu "Ajuda"
        JMenu ajudaMenu = new JMenu("Ajuda");
        JMenuItem sobreItem = new JMenuItem("Sobre");
        ajudaMenu.add(sobreItem);
        menuBar.add(ajudaMenu);

        sobreItem.addActionListener(e -> mostrarJanelaSobre());

        setJMenuBar(menuBar);
    }
    
    /**
     * Exibe uma janela de diálogo (JOptionPane) com informações "Sobre" o sistema,
     * incluindo detalhes do projeto e dos desenvolvedores.
     */
    private void mostrarJanelaSobre() {
        String mensagem = "<html>" +
            "<body>" +
            "<h2>Sistema de Gestão de Bagagens</h2>" +
            "<p>Este sistema automatiza o registro e organização digital<br>" +
            "dos processos de bagagens de uma companhia aérea.</p>" +
            "<br>" + 
            "<b>Desenvolvido por:</b>" +
            "<ul>" +
            "<li>JÚLIA DE SOUZA NASCCIMENTO/li>" +
            "<li>ANA LUÍSA PEREIRA DOS SANTOS</li>" +
            "<li>GABRIEL RODRIGUES DA SILVA</li>" +
            "</ul>" +
            "<p><b>Disciplina:</b> Programação Orientada a Objetos</p>" +
            "<p><b>Docente:</b> Prof. Elias Batista Ferreira</p>" +
            "<br>" +
            "<p>Universidade Federal de Goiás - UFG</p>" +
            "<p>Instituto de Informática - 2025/1</p>" +
            "</body>" +
            "</html>";

        ImageIcon icone = null;
        URL urlIcone = getClass().getResource("/imagens/logo_sobre.png");
        if (urlIcone != null) {
            icone = new ImageIcon(urlIcone);
        }

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Sobre o Sistema",
            JOptionPane.INFORMATION_MESSAGE,
            icone
        );
    }

    /**
     * Método central para navegação. Remove o painel atual do contentor principal
     * e adiciona o novo painel fornecido.
     * @param panel O painel (JPanel) a ser exibido na janela principal.
     */
    void showPanel(JPanel panel) {
        panel.setOpaque(false);
        getContentPane().removeAll();
        getContentPane().add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}