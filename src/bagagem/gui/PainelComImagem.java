package bagagem.gui;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

/**
 * Um JPanel personalizado que desenha uma imagem de fundo.
 * A imagem é carregada a partir dos recursos do projeto e é redimensionada
 * dinamicamente para preencher todo o tamanho do painel.
 */
public class PainelComImagem extends JPanel {

    private Image imagemDeFundo;

    /**
     * Construtor que carrega a imagem a partir de um caminho de recurso no projeto.
     * @param caminhoImagem O caminho para a imagem, a partir da raiz dos fontes (src).
     * Exemplo: "/imagens/fundo.jpg"
     */
    public PainelComImagem(String caminhoImagem) {
        // Carrega a imagem de forma segura, usando a variável recebida.
        URL url = getClass().getResource(caminhoImagem);
        
        if (url != null) {
            this.imagemDeFundo = new ImageIcon(url).getImage();
        } else {
            // Usa a variável também na mensagem de erro.
            System.err.println("Imagem de fundo não encontrada em: " + caminhoImagem);
        }
    }

    /**
     * Sobrescreve o método paintComponent para desenhar a imagem de fundo.
     * Este método é chamado automaticamente pelo Swing sempre que o painel precisa ser redesenhado.
     * @param g O contexto gráfico no qual o componente será desenhado.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Mantém o comportamento padrão de pintura do JPanel
        
        // Desenha a imagem de fundo, escalonando-a para cobrir toda a área do painel.
        if (imagemDeFundo != null) {
            g.drawImage(imagemDeFundo, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}